package com.uta.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.uta.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.uta.domain.award.model.entity.TaskEntity;
import com.uta.domain.award.model.entity.UserAwardRecordEntity;
import com.uta.domain.award.model.vo.AwardStateVO;
import com.uta.domain.award.repository.IAwardRepository;
import com.uta.infrastructure.event.EventPublisher;
import com.uta.infrastructure.persistent.dao.TaskMapper;
import com.uta.infrastructure.persistent.dao.UserAwardRecordMapper;
import com.uta.infrastructure.persistent.po.Task;
import com.uta.infrastructure.persistent.po.UserAwardRecord;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;

@Repository
@Slf4j
public class AwardRepository implements IAwardRepository {

    @Resource
    private UserAwardRecordMapper userAwardRecordMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Long strategyId = userAwardRecordEntity.getStrategyId();

        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
               try {
                   // 写入记录
                   userAwardRecordMapper.insert(userAwardRecord);
                   // 写入任务
                   taskMapper.insert(task);
                   return 1;
               }catch (DuplicateKeyException e){
                   status.setRollbackOnly();
                   log.error("写入用户中奖记录和任务，唯一索引冲突 userId:{},activityId:{},strategyId:{}", userId, activityId, strategyId);
                   throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
               }
            });
        }finally {
            dbRouter.clear();
        }

        try {
            // 发送MQ消息
            eventPublisher.publish(taskEntity.getTopic(), task.getMessage());
            // 更新任务状态
            taskMapper.updateTaskMessageCompleted(task);
        }catch (Exception e){
            log.error("写入中奖记录，发送MQ失败 userId:{}, topic:{}", userId, taskEntity.getTopic());
            taskMapper.updateTaskMessageFail(task);
        }

    }
}
