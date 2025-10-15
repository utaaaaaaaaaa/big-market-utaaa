package persistent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import persistent.po.UserCreditAccount;
import persistent.service.UserCreditAccountService;
import persistent.mapper.UserCreditAccountMapper;
import org.springframework.stereotype.Service;

/**
* @author 24333
* @description 针对表【user_credit_account(用户积分账户)】的数据库操作Service实现
* @createDate 2025-10-16 00:36:09
*/
@Service
public class UserCreditAccountServiceImpl extends ServiceImpl<UserCreditAccountMapper, UserCreditAccount>
    implements UserCreditAccountService{

}




