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

import java.io.Serializable;
import java.util.List;

/**
 * @author jie zhao
 * @date 2020/4/9 19:55
 */
@Data
@ToString
public class User implements Serializable {

  private Integer id;
  private String username;
  private String password;
  private String email;
  private Long regTime;
  private String face;
  private Integer proId;

  private String loginname; // 登录名
  private String phone; // 联系电话
  private String address; // 收货地址
  // 用户和订单是一对多的关系，即一个用户可以有多个订单
  private List<Order> orders;

  public User() {
  }

  public User(Integer id, String username, String password) {
    this.id = id;
    this.username = username;
    this.password = password;
  }
}
