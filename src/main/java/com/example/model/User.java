/**
 *    Copyright 2009-2025 the original author or authors.
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
package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jie Zhao
 * @date 2025/11/3 21:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
  private Long id;
  private String username;
  private String email;
  private String fullName;
  private String department;
  private String position;
  private Byte status;
  private LocalDateTime createdAt;
  private List<Role> roles; // 用户拥有的角色
}
