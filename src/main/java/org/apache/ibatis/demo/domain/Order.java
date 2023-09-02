package org.apache.ibatis.demo.domain;

import lombok.Data;

import java.util.Date;

/**
 * 订单实体类
 */
@Data
public class Order {
  private int id;
  private Date orderTime;
  private double total;
  private User user;
}
