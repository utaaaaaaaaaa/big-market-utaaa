package com.uta.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.uta.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.uta.domain.award.model.vo.AccountStatusVO;
import com.uta.domain.credit.model.aggregate.TradeAggregate;
import com.uta.domain.credit.model.entity.CreditAccountEntity;
import com.uta.domain.credit.model.entity.CreditOrderEntity;
import com.uta.domain.credit.model.entity.TaskEntity;
import com.uta.domain.credit.repository.ICreditRepository;
import com.uta.infrastructure.event.EventPublisher;
import com.uta.infrastructure.persistent.dao.TaskMapper;
import com.uta.infrastructure.persistent.dao.UserCreditAccountMapper;
import com.uta.infrastructure.persistent.dao.UserCreditOrderMapper;
import com.uta.infrastructure.persistent.po.Task;
import com.uta.infrastructure.persistent.po.UserCreditAccount;
import com.uta.infrastructure.persistent.po.UserCreditOrder;
import com.uta.infrastructure.persistent.redis.IRedisService;
import com.uta.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CreditRepository implements ICreditRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private UserCreditAccountMapper userCreditAccountMapper;
    @Resource
    private UserCreditOrderMapper userCreditOrderMapper;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;


    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {

        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        TaskEntity taskEntity = tradeAggregate.getTaskEntity();

        // 积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        // 知识；仓储往上有业务语义，仓储往下到 dao 操作是没有业务语义的。所以不用在乎这块使用的字段名称，直接用持久化对象即可。
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.OPEN.getCode());

        // 积分订单
        UserCreditOrder userCreditOrderReq = new UserCreditOrder();
        userCreditOrderReq.setUserId(creditOrderEntity.getUserId());
        userCreditOrderReq.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrderReq.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrderReq.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrderReq.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrderReq.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        Task task = new Task();
        task.setTopic(taskEntity.getTopic());
        task.setUserId(taskEntity.getUserId());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo());
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(userId);
            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    // 1. 保存账户积分
                    UserCreditAccount userCreditAccount = userCreditAccountMapper.queryUserCreditAccount(userCreditAccountReq);
                    if (null == userCreditAccount) {
                        userCreditAccountMapper.insert(userCreditAccountReq);
                    } else {
                        userCreditAccountMapper.updateAddAmount(userCreditAccountReq);
                    }
                    // 2. 保存账户订单
                    userCreditOrderMapper.insert(userCreditOrderReq);
                    // 3. 写入任务
                    taskMapper.insert(task);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度失败 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                }
                return 1;
            });
        } finally {
            dbRouter.clear();
            lock.unlock();
        }

        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getTopic(), task.getMessage());
            // 更新数据库记录，task 任务表
            taskMapper.updateTaskMessageCompleted(task);
            log.info("调整账户积分记录，发送MQ消息完成 userId: {} orderId:{} topic: {}", userId, creditOrderEntity.getOrderId(), task.getTopic());
        } catch (Exception e) {
            log.error("调整账户积分记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            taskMapper.updateTaskMessageFail(task);
        }

    }
}
