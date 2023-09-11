package org.apache.ibatis.demo.mapper;

import org.apache.ibatis.demo.domain.User;

import java.util.List;

public interface UserMapper {

  // 测试一对多联合查询
  List<User> findAll();

  // 测试一对多联合查询
  List<User> findUserById(Integer id);

  // 测试多对多联合查询使用
  List<User> findUserAndRoles();
}
