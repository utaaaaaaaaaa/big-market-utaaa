package com.uta.trigger.job;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.uta.domain.task.model.entity.TaskEntity;
import com.uta.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 扫描task表中发送失败的消息
 */
@Slf4j
@Component
public class SendMessageTaskJob {

    @Resource
    private ITaskService taskService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
        try {
            // 获取分库数量
            int count = dbRouter.dbCount();
            for (int i = 1; i <= count; i++) {
                int finalI = i;
                threadPoolExecutor.execute(() -> {
                    try {
                        dbRouter.setDBKey(finalI);
//                          dbRouter.setTBKey(0);
                        List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                        if (taskEntities == null || taskEntities.isEmpty())return;
                        //发送mq消息
                        for (TaskEntity task : taskEntities) {
                            threadPoolExecutor.execute(() ->{
                                try {
                                    taskService.sendMessage(task);
                                    taskService.updateTaskSendMessageCompleted(task.getUserId(),task.getMessageId());
                                }catch (Exception e){
                                    log.error("写入中奖记录，发送MQ失败 userId:{}, topic:{}", task.getUserId(), task.getTopic());
                                    taskService.updateTaskSendMessageFail(task.getUserId(),task.getMessageId());
                                }
                            });
                        }
                    }finally {
                        dbRouter.clear();
                    }
                });
            }

        }catch (Exception e){
            log.error("【定时任务】,扫描MQ任务表发送消息失败",e);
        }

    }

}
