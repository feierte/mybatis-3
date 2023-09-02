package org.apache.ibatis.demo.domain;

import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
public class User {
  private int id;
  private String username;
  private String password;
  private Date birthday;
}
