package com.uta.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.uta.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.uta.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.uta.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.uta.domain.activity.model.entity.*;
import com.uta.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.uta.domain.activity.model.vo.ActivityStateVO;
import com.uta.domain.activity.model.vo.OrderTradeTypeVO;
import com.uta.domain.activity.model.vo.UserRaffleOrderStateVO;
import com.uta.domain.activity.repository.IActivityRepository;
import com.uta.infrastructure.event.EventPublisher;
import com.uta.infrastructure.persistent.dao.*;
import com.uta.infrastructure.persistent.po.*;
import com.uta.infrastructure.persistent.redis.IRedisService;
import com.uta.types.common.Constants;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private RaffleActivityMapper raffleActivityMapper;
    @Resource
    private RaffleActivitySkuMapper raffleActivitySkuMapper;
    @Resource
    private RaffleActivityCountMapper raffleActivityCountMapper;
    @Resource
    private RaffleActivityOrderMapper raffleActivityOrderMapper;
    @Resource
    private RaffleActivityAccountMapper raffleActivityAccountMapper;
    @Resource
    private RaffleActivityAccountMonthMapper raffleActivityAccountMonthMapper;
    @Resource
    private RaffleActivityAccountDayMapper raffleActivityAccountDayMapper;
    @Resource
    private UserRaffleOrderMapper userRaffleOrderMapper;

    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuMapper.queryActivitySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .productAmount(raffleActivitySku.getProductAmount())
                .build();
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if (null != activityEntity) return activityEntity;
        // 从库中获取数据
        RaffleActivity raffleActivity = raffleActivityMapper.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();
        redisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if (null != activityCountEntity) return activityCountEntity;
        // 从库中获取数据
        RaffleActivityCount raffleActivityCount = raffleActivityCountMapper.queryRaffleActivityCountByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public void doSaveNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        RLock lock = redisService.getLock(Constants.RedisKey.ACTIVITY_ACCOUNT_LOCK + createQuotaOrderAggregate.getUserId() + Constants.UNDERLINE + createQuotaOrderAggregate.getActivityId());
        try {
            lock.lock(3, TimeUnit.SECONDS);
            // 订单对象
            ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
            raffleActivityOrder.setSku(activityOrderEntity.getSku());
            raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
            raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
            raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
            raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
            raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
            raffleActivityOrder.setTotalCount(activityOrderEntity.getTotalCount());
            raffleActivityOrder.setDayCount(activityOrderEntity.getDayCount());
            raffleActivityOrder.setMonthCount(activityOrderEntity.getMonthCount());
            raffleActivityOrder.setTotalCount(createQuotaOrderAggregate.getTotalCount());
            raffleActivityOrder.setDayCount(createQuotaOrderAggregate.getDayCount());
            raffleActivityOrder.setMonthCount(createQuotaOrderAggregate.getMonthCount());
            raffleActivityOrder.setPayAmount(activityOrderEntity.getPayAmount());
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

            // 账户对象 - 总
            RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
            raffleActivityAccount.setUserId(createQuotaOrderAggregate.getUserId());
            raffleActivityAccount.setActivityId(createQuotaOrderAggregate.getActivityId());
            raffleActivityAccount.setTotalCount(createQuotaOrderAggregate.getTotalCount());
            raffleActivityAccount.setTotalCountSurplus(createQuotaOrderAggregate.getTotalCount());
            raffleActivityAccount.setDayCount(createQuotaOrderAggregate.getDayCount());
            raffleActivityAccount.setDayCountSurplus(createQuotaOrderAggregate.getDayCount());
            raffleActivityAccount.setMonthCount(createQuotaOrderAggregate.getMonthCount());
            raffleActivityAccount.setMonthCountSurplus(createQuotaOrderAggregate.getMonthCount());

            // 账户对象 - 月
            RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
            raffleActivityAccountMonth.setUserId(createQuotaOrderAggregate.getUserId());
            raffleActivityAccountMonth.setActivityId(createQuotaOrderAggregate.getActivityId());
            raffleActivityAccountMonth.setMonth(raffleActivityAccountMonth.currentMonth());
            raffleActivityAccountMonth.setMonthCount(createQuotaOrderAggregate.getMonthCount());
            raffleActivityAccountMonth.setMonthCountSurplus(createQuotaOrderAggregate.getMonthCount());

            // 账户对象 - 日
            RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
            raffleActivityAccountDay.setUserId(createQuotaOrderAggregate.getUserId());
            raffleActivityAccountDay.setActivityId(createQuotaOrderAggregate.getActivityId());
            raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
            raffleActivityAccountDay.setDayCount(createQuotaOrderAggregate.getDayCount());
            raffleActivityAccountDay.setDayCountSurplus(createQuotaOrderAggregate.getDayCount());

            // 以用户id作为切分键，通过doRouter设定路由【这样保证了后续操作都在同一连接下，也保证了事务的特性】
            dbRouter.doRouter(createQuotaOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    // 1.写入订单
                    raffleActivityOrderMapper.insert(raffleActivityOrder);
                    // 2. 更新账户 - 总
                    RaffleActivityAccount raffleActivityAccountRes = raffleActivityAccountMapper.queryActivityAccountByUserId(raffleActivityAccount);
                    if (null == raffleActivityAccountRes) {
                        raffleActivityAccountMapper.insert(raffleActivityAccount);
                    } else {
                        raffleActivityAccountMapper.updateAccountQuota(raffleActivityAccount);
                    }
                    // 4. 更新账户 - 月
                    raffleActivityAccountMonthMapper.addAccountQuota(raffleActivityAccountMonth);
                    // 5. 更新账户 - 日
                    raffleActivityAccountDayMapper.addAccountQuota(raffleActivityAccountDay);
                    return 1;
                }catch (DuplicateKeyException e){
                    // 主键冲突异常回滚
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引异常，userId:{}, activityId:{}, sku:{}",activityOrderEntity.getUserId(),activityOrderEntity.getActivityId(),activityOrderEntity.getSku());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
            });
        } finally {
            dbRouter.clear();
            lock.unlock();
        }

    }

    @Override
    public void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        try {
            // 创建交易订单
            ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
            raffleActivityOrder.setSku(activityOrderEntity.getSku());
            raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
            raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
            raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
            raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
            raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
            raffleActivityOrder.setTotalCount(activityOrderEntity.getTotalCount());
            raffleActivityOrder.setDayCount(activityOrderEntity.getDayCount());
            raffleActivityOrder.setMonthCount(activityOrderEntity.getMonthCount());
            raffleActivityOrder.setTotalCount(createQuotaOrderAggregate.getTotalCount());
            raffleActivityOrder.setDayCount(createQuotaOrderAggregate.getDayCount());
            raffleActivityOrder.setMonthCount(createQuotaOrderAggregate.getMonthCount());
            raffleActivityOrder.setPayAmount(activityOrderEntity.getPayAmount());
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

            // 以用户ID作为切分键，通过 doRouter 设定路由【这样就保证了下面的操作，都是同一个链接下，也就保证了事务的特性】
            dbRouter.doRouter(createQuotaOrderAggregate.getUserId());

            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    raffleActivityOrderMapper.insert(raffleActivityOrder);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }

    }

    @Override
    public void cacheActivitySkuStockCount(String cacheKey, Integer stockCount) {
        if (redisService.isExists(cacheKey)) {return;}
        redisService.setValue(cacheKey, stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime) {
        long surplus = redisService.decr(cacheKey);
        if (surplus == 0){
            // 库存消耗完了以后，发送MQ消息，更新数据库缓存
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(), activitySkuStockZeroMessageEvent.buildEventMessage(sku));
            return true;
        }else if(surplus < 0){
            redisService.setAtomicLong(cacheKey, 0);
            return false;
        }

        // 加锁兜底，同时设置加锁时间为活动到期 + 1天
        String lockKey = cacheKey + Constants.UNDERLINE + surplus;
        long expireMills = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean lock = redisService.setNx(lockKey, expireMills, TimeUnit.MILLISECONDS);
        if (!lock){
            log.info("活动sku加锁失败：{}",lockKey);
        }
        return lock;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        blockingQueue.offer(activitySkuStockKeyVO);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        String key = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        return blockingQueue.poll();
    }

    @Override
    public void clearQueueValue() {
        String key = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        blockingQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuMapper.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuMapper.clearActivitySkuStock(sku);
    }

    @Override
    public void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        try {
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
            ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    // 1.更新总账户额度
                    int count = raffleActivityAccountMapper.updateActivityAccountSubtractionQuota(
                            RaffleActivityAccount.builder()
                                    .activityId(activityId)
                                    .userId(userId)
                                    .build());
                    if (count != 1) {
                        status.setRollbackOnly();
                        log.warn("用户总账户额度不够，userId:{}, activityId:{}",userId,activityId);
                        throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                    }

                    // 2.创建或者更新月额度
                    if (createPartakeOrderAggregate.isExistAccountMonth()){
                        int countMonth = raffleActivityAccountMonthMapper.updateActivityAccountSubtractionQuota(
                                RaffleActivityAccountMonth.builder()
                                        .activityId(activityId)
                                        .userId(userId)
                                        .month(activityAccountMonthEntity.getMonth())
                                        .build());
                        if (countMonth != 1) {
                            status.setRollbackOnly();
                            log.warn("用户月账户额度不够，userId:{}, activityId:{}, month:{}",userId,activityId,activityAccountMonthEntity.getMonth());
                            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                        }
                    }else{
                        raffleActivityAccountMonthMapper.insert(
                                RaffleActivityAccountMonth.builder()
                                        .userId(userId)
                                        .month(activityAccountMonthEntity.getMonth())
                                        .activityId(activityId)
                                        .monthCount(activityAccountMonthEntity.getMonthCount())
                                        .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus() - 1)
                                        .build());
                        // 创建新月账户，则更新总表中月镜像额度
                        raffleActivityAccountMapper.updateActivityAccountMonthSurplusImageQuota(
                                RaffleActivityAccount.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                                        .build());
                    }

                    // 3.创建或者更新日额度
                    if (createPartakeOrderAggregate.isExistAccountDay()){
                        int countDay = raffleActivityAccountDayMapper.updateActivityAccountSubtractionQuota(
                                RaffleActivityAccountDay.builder()
                                        .activityId(activityId)
                                        .userId(userId)
                                        .day(activityAccountDayEntity.getDay())
                                        .build());
                        if (countDay != 1) {
                            status.setRollbackOnly();
                            log.warn("用户日账户额度不够，userId:{}, activityId:{}, Day:{}",userId,activityId,activityAccountDayEntity.getDay());
                            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                        }
                    }else{
                        raffleActivityAccountDayMapper.insert(
                                RaffleActivityAccountDay.builder()
                                        .userId(userId)
                                        .day(activityAccountDayEntity.getDay())
                                        .activityId(activityId)
                                        .dayCount(activityAccountDayEntity.getDayCount())
                                        .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus() - 1)
                                        .build());
                        // 创建新月账户，则更新总表中月镜像额度
                        raffleActivityAccountMapper.updateActivityAccountDaySurplusImageQuota(
                                RaffleActivityAccount.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                                        .build());
                    }

                    // 4.写入订单记录
                    userRaffleOrderMapper.insert(
                            UserRaffleOrder.builder()
                                    .userId(userRaffleOrderEntity.getUserId())
                                    .activityId(userRaffleOrderEntity.getActivityId())
                                    .activityName(userRaffleOrderEntity.getActivityName())
                                    .strategyId(userRaffleOrderEntity.getStrategyId())
                                    .orderId(userRaffleOrderEntity.getOrderId())
                                    .orderTime(userRaffleOrderEntity.getOrderTime())
                                    .orderState(userRaffleOrderEntity.getOrderState().getCode())
                                    .build());

                    return 1;
                }catch (DuplicateKeyException e){
                    // 主键冲突异常回滚
                    status.setRollbackOnly();
                    log.error("写入日额度、月额度记录，唯一索引异常，userId:{}, activityId:{}",userId,activityId);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
            });

        }finally {
            dbRouter.clear();
        }
    }

    @Override
    public UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        // 查询数据
        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(partakeRaffleActivityEntity.getUserId());
        userRaffleOrderReq.setActivityId(partakeRaffleActivityEntity.getActivityId());
        UserRaffleOrder userRaffleOrderRes = userRaffleOrderMapper.queryNoUsedRaffleOrder(userRaffleOrderReq);
        if (null == userRaffleOrderRes) return null;
        // 封装结果
        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        userRaffleOrderEntity.setUserId(userRaffleOrderRes.getUserId());
        userRaffleOrderEntity.setActivityId(userRaffleOrderRes.getActivityId());
        userRaffleOrderEntity.setActivityName(userRaffleOrderRes.getActivityName());
        userRaffleOrderEntity.setStrategyId(userRaffleOrderRes.getStrategyId());
        userRaffleOrderEntity.setOrderId(userRaffleOrderRes.getOrderId());
        userRaffleOrderEntity.setOrderTime(userRaffleOrderRes.getOrderTime());
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVO.valueOf(userRaffleOrderRes.getOrderState()));
        return userRaffleOrderEntity;
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuListByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkus = raffleActivitySkuMapper.queryActivitySkuListByActivityId(activityId);
        List<ActivitySkuEntity> activitySkuEntities = new ArrayList<>();
        for (RaffleActivitySku activitySku : raffleActivitySkus) {
            ActivitySkuEntity activitySkuEntity = new ActivitySkuEntity();
            activitySkuEntity.setSku(activitySku.getSku());
            activitySkuEntity.setActivityId(activitySku.getActivityId());
            activitySkuEntity.setActivityCountId(activitySku.getActivityCountId());
            activitySkuEntity.setStockCount(activitySku.getStockCount());
            activitySkuEntity.setStockCountSurplus(activitySku.getStockCountSurplus());
            activitySkuEntities.add(activitySkuEntity);
        }
        return activitySkuEntities;
    }

    @Override
    public Integer queryUserDayPartakeCount(Long activityId, String userId) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(now);
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setUserId(userId);
        raffleActivityAccountDay.setActivityId(activityId);
        raffleActivityAccountDay.setDay(day);
        Integer i = raffleActivityAccountDayMapper.queryUserDayPartakeCount(raffleActivityAccountDay);
        return i == null ? 0 : i;
    }

    @Override
    public ActivityAccountEntity getRaffleActivityAccount(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountMapper.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        if (null == raffleActivityAccount) {
            return ActivityAccountEntity.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .totalCount(0)
                    .totalCountSurplus(0)
                    .dayCount(0)
                    .dayCountSurplus(0)
                    .monthCount(0)
                    .monthCountSurplus(0)
                    .build();
        }

        // 2. 查询月账户额度
        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthMapper.queryActivityAccountMonthByUserId(RaffleActivityAccountMonth.builder()
                .activityId(activityId)
                .userId(userId)
                .month(RaffleActivityAccountMonth.currentMonth())
                .build());

        // 3. 查询日账户额度
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayMapper.queryActivityAccountDayByUserId(RaffleActivityAccountDay.builder()
                .activityId(activityId)
                .userId(userId)
                .day(RaffleActivityAccountDay.currentDay())
                .build());

        // 组装对象
        ActivityAccountEntity activityAccountEntity = new ActivityAccountEntity();
        activityAccountEntity.setUserId(userId);
        activityAccountEntity.setActivityId(activityId);
        activityAccountEntity.setTotalCount(raffleActivityAccount.getTotalCount());
        activityAccountEntity.setTotalCountSurplus(raffleActivityAccount.getTotalCountSurplus());

        // 如果没有创建日账户，则从总账户中获取日总额度填充。「当新创建日账户时，会获得总账户额度」
        if (null == raffleActivityAccountDay) {
            activityAccountEntity.setDayCount(raffleActivityAccount.getDayCount());
            activityAccountEntity.setDayCountSurplus(raffleActivityAccount.getDayCount());
        } else {
            activityAccountEntity.setDayCount(raffleActivityAccountDay.getDayCount());
            activityAccountEntity.setDayCountSurplus(raffleActivityAccountDay.getDayCountSurplus());
        }

        // 如果没有创建月账户，则从总账户中获取月总额度填充。「当新创建日账户时，会获得总账户额度」
        if (null == raffleActivityAccountMonth) {
            activityAccountEntity.setMonthCount(raffleActivityAccount.getMonthCount());
            activityAccountEntity.setMonthCountSurplus(raffleActivityAccount.getMonthCount());
        } else {
            activityAccountEntity.setMonthCount(raffleActivityAccountMonth.getMonthCount());
            activityAccountEntity.setMonthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus());
        }

        return activityAccountEntity;
    }

    @Override
    public Integer queryRaffleActivityAccountPartakeCount(Long activityId, String userId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountMapper.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }

    @Override
    public void updateOrder(DeliveryOrderEntity deliveryOrderEntity) {
        RLock lock = redisService.getLock(Constants.RedisKey.ACTIVITY_ACCOUNT_UPDATE_LOCK + deliveryOrderEntity.getUserId());
        try {
            lock.lock(3, TimeUnit.SECONDS);

            // 查询订单
            RaffleActivityOrder raffleActivityOrderReq = new RaffleActivityOrder();
            raffleActivityOrderReq.setUserId(deliveryOrderEntity.getUserId());
            raffleActivityOrderReq.setOutBusinessNo(deliveryOrderEntity.getOutBusinessNo());
            RaffleActivityOrder raffleActivityOrderRes = raffleActivityOrderMapper.queryRaffleActivityOrder(raffleActivityOrderReq);
            if (raffleActivityOrderRes == null) {return;}

            // 账户对象 - 总
            RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
            raffleActivityAccount.setUserId(raffleActivityOrderRes.getUserId());
            raffleActivityAccount.setActivityId(raffleActivityOrderRes.getActivityId());
            raffleActivityAccount.setTotalCount(raffleActivityOrderRes.getTotalCount());
            raffleActivityAccount.setTotalCountSurplus(raffleActivityOrderRes.getTotalCount());
            raffleActivityAccount.setDayCount(raffleActivityOrderRes.getDayCount());
            raffleActivityAccount.setDayCountSurplus(raffleActivityOrderRes.getDayCount());
            raffleActivityAccount.setMonthCount(raffleActivityOrderRes.getMonthCount());
            raffleActivityAccount.setMonthCountSurplus(raffleActivityOrderRes.getMonthCount());

            // 账户对象 - 月
            RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
            raffleActivityAccountMonth.setUserId(raffleActivityOrderRes.getUserId());
            raffleActivityAccountMonth.setActivityId(raffleActivityOrderRes.getActivityId());
            raffleActivityAccountMonth.setMonth(RaffleActivityAccountMonth.currentMonth());
            raffleActivityAccountMonth.setMonthCount(raffleActivityOrderRes.getMonthCount());
            raffleActivityAccountMonth.setMonthCountSurplus(raffleActivityOrderRes.getMonthCount());

            // 账户对象 - 日
            RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
            raffleActivityAccountDay.setUserId(raffleActivityOrderRes.getUserId());
            raffleActivityAccountDay.setActivityId(raffleActivityOrderRes.getActivityId());
            raffleActivityAccountDay.setDay(RaffleActivityAccountDay.currentDay());
            raffleActivityAccountDay.setDayCount(raffleActivityOrderRes.getDayCount());
            raffleActivityAccountDay.setDayCountSurplus(raffleActivityOrderRes.getDayCount());


            dbRouter.doRouter(deliveryOrderEntity.getUserId());
            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    // 1. 更新订单
                    int updateCount = raffleActivityOrderMapper.updateOrderCompleted(raffleActivityOrderReq);
                    if (1 != updateCount) {
                        status.setRollbackOnly();
                        return 1;
                    }
                    // 2. 更新账户 - 总
                    RaffleActivityAccount raffleActivityAccountRes = raffleActivityAccountMapper.queryActivityAccountByUserId(raffleActivityAccount);
                    if (null == raffleActivityAccountRes) {
                        raffleActivityAccountMapper.insert(raffleActivityAccount);
                    } else {
                        raffleActivityAccountMapper.updateAccountQuota(raffleActivityAccount);
                    }
                    // 4. 更新账户 - 月
                    raffleActivityAccountMonthMapper.addAccountQuota(raffleActivityAccountMonth);
                    // 5. 更新账户 - 日
                    raffleActivityAccountDayMapper.addAccountQuota(raffleActivityAccountDay);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("更新订单记录，完成态，唯一索引冲突 userId: {} outBusinessNo: {}", deliveryOrderEntity.getUserId(), deliveryOrderEntity.getOutBusinessNo(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
            lock.unlock();
        }
    }

    @Override
    public UnpaidActivityOrderEntity queryUnpaidActivityOrder(SkuRechargeEntity skuRechargeEntity) {
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(userId);
        raffleActivityOrder.setSku(sku);
        RaffleActivityOrder unpaidOrder = raffleActivityOrderMapper.queryUnpaidActivityOrder(raffleActivityOrder);
        if (unpaidOrder == null)return null;

        return UnpaidActivityOrderEntity.builder()
                .userId(unpaidOrder.getUserId())
                .orderId(unpaidOrder.getOrderId())
                .outBusinessNo(unpaidOrder.getOutBusinessNo())
                .payAmount(unpaidOrder.getPayAmount())
                .build();
    }

    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkus = raffleActivitySkuMapper.queryActivitySkuListByActivityId(activityId);
        List<SkuProductEntity> skuProductEntityList = new ArrayList<>();
        for (RaffleActivitySku raffleActivitySku : raffleActivitySkus) {
            RaffleActivityCount raffleActivityCount = raffleActivityCountMapper.queryRaffleActivityCountByActivityCountId(raffleActivitySku.getActivityCountId());
            SkuProductEntity skuProductEntity = new SkuProductEntity();
            skuProductEntity.setSku(raffleActivitySku.getSku());
            skuProductEntity.setActivityId(raffleActivitySku.getActivityId());
            skuProductEntity.setActivityCountId(raffleActivitySku.getActivityCountId());
            skuProductEntity.setStockCount(raffleActivitySku.getStockCount());
            skuProductEntity.setStockCountSurplus(raffleActivitySku.getStockCountSurplus());
            skuProductEntity.setProductAmount(raffleActivitySku.getProductAmount());
            skuProductEntity.setActivityCount(SkuProductEntity.ActivityCount.builder()
                            .dayCount(raffleActivityCount.getDayCount())
                            .monthCount(raffleActivityCount.getMonthCount())
                            .totalCount(raffleActivityCount.getTotalCount())
                    .build());

            skuProductEntityList.add(skuProductEntity);
        }
        return skuProductEntityList;
    }


    @Override
    public ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId) {
        // 1. 查询账户
        RaffleActivityAccount raffleActivityAccountReq = new RaffleActivityAccount();
        raffleActivityAccountReq.setUserId(userId);
        raffleActivityAccountReq.setActivityId(activityId);
        RaffleActivityAccount raffleActivityAccountRes = raffleActivityAccountMapper.queryActivityAccountByUserId(raffleActivityAccountReq);
        if (null == raffleActivityAccountRes) return null;
        // 2. 转换对象
        return ActivityAccountEntity.builder()
                .userId(raffleActivityAccountRes.getUserId())
                .activityId(raffleActivityAccountRes.getActivityId())
                .totalCount(raffleActivityAccountRes.getTotalCount())
                .totalCountSurplus(raffleActivityAccountRes.getTotalCountSurplus())
                .dayCount(raffleActivityAccountRes.getDayCount())
                .dayCountSurplus(raffleActivityAccountRes.getDayCountSurplus())
                .monthCount(raffleActivityAccountRes.getMonthCount())
                .monthCountSurplus(raffleActivityAccountRes.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month) {
        // 1. 查询账户
        RaffleActivityAccountMonth raffleActivityAccountMonthReq = new RaffleActivityAccountMonth();
        raffleActivityAccountMonthReq.setUserId(userId);
        raffleActivityAccountMonthReq.setActivityId(activityId);
        raffleActivityAccountMonthReq.setMonth(month);
        RaffleActivityAccountMonth raffleActivityAccountMonthRes = raffleActivityAccountMonthMapper.queryActivityAccountMonthByUserId(raffleActivityAccountMonthReq);
        if (null == raffleActivityAccountMonthRes) return null;
        // 2. 转换对象
        return ActivityAccountMonthEntity.builder()
                .userId(raffleActivityAccountMonthRes.getUserId())
                .activityId(raffleActivityAccountMonthRes.getActivityId())
                .month(raffleActivityAccountMonthRes.getMonth())
                .monthCount(raffleActivityAccountMonthRes.getMonthCount())
                .monthCountSurplus(raffleActivityAccountMonthRes.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day) {
        // 1. 查询账户
        RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
        raffleActivityAccountDayReq.setUserId(userId);
        raffleActivityAccountDayReq.setActivityId(activityId);
        raffleActivityAccountDayReq.setDay(day);
        RaffleActivityAccountDay raffleActivityAccountDayRes = raffleActivityAccountDayMapper.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if (null == raffleActivityAccountDayRes) return null;
        // 2. 转换对象
        return ActivityAccountDayEntity.builder()
                .userId(raffleActivityAccountDayRes.getUserId())
                .activityId(raffleActivityAccountDayRes.getActivityId())
                .day(raffleActivityAccountDayRes.getDay())
                .dayCount(raffleActivityAccountDayRes.getDayCount())
                .dayCountSurplus(raffleActivityAccountDayRes.getDayCountSurplus())
                .build();
    }



}
