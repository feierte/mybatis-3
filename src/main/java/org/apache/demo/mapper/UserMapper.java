package org.apache.demo.mapper;

import org.apache.demo.entity.User;

/**
 * @author jie zhao
 * @date 2020/4/9 19:53
 */
public interface UserMapper {

    User selectUser(Integer id);
    User selectUserByConstructor(Integer id);
}
