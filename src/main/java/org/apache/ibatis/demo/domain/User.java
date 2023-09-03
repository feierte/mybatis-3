package org.apache.ibatis.demo.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户实体类
 */
@Data
public class User {
  private int id;
  private String username;
  private String password;
  private Date birthday;

  // 用户拥有多个Order，测试多对多联合查询
  private List<Order> orderList;
  private List<Role> roleList;
}
