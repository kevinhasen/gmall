package com.yee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.gmall.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserInfoMapper
 * Description:
 * date: 2022/2/25 21:33
 * 用户基本信息表
 * @author Yee
 * @since JDK 1.8
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
