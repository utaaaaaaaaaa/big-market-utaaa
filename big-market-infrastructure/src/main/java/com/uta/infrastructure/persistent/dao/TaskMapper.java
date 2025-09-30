package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.uta.infrastructure.persistent.po.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 24333
* @description 针对表【task(任务表，发送MQ)】的数据库操作Mapper
* @createDate 2025-09-28 06:56:19
* @Entity persistent.pojo.Task
*/
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    @DBRouter
    void updateTaskMessageCompleted(Task task);

    @DBRouter
    void updateTaskMessageFail(Task task);

    List<Task> queryNoSendMessageTaskList();
}




