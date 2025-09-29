package persistent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import persistent.pojo.Task;
import persistent.service.TaskService;
import persistent.mapper.TaskMapper;
import org.springframework.stereotype.Service;

/**
* @author 24333
* @description 针对表【task(任务表，发送MQ)】的数据库操作Service实现
* @createDate 2025-09-28 06:56:19
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

}




