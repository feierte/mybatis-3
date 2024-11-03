/**
 *    Copyright 2009-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.demo.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Order {

  private Double price;
  private Double gpsy; // 随便造的一个属性
  private Integer uid;


  private Integer id; // 订单id，主键
  private String code; // 订单编号
  private Double total; // 订单总金额

  // 订单和用户是多对一的关系，即一个订单只属于一个用户
  private User user;

  // 订单和商品是多对多的关系，即一个订单可以包含多种商品
  private List<Article> articles;
}
