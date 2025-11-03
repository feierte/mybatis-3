package org.apache.demo.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Jie Zhao
 * @date 2024/11/3 13:14
 */
@Data
@ToString
public class Article {
  private Integer id; // 商品id，主键
  private String name; // 商品名称
  private Double price; // 商品价格
  private String remark; // 商品描述

  // 商品和订单是多对多的关系，即一种商品可以包含在多个订单中
  private List<Order> orders;
}
