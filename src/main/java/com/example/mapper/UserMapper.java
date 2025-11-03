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
package com.example.mapper;

import com.example.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Jie Zhao
 * @date 2025/11/3 21:27
 */
// @Mapper 注解在Spring环境中常用，纯MyBatis环境可以不用
public interface UserMapper {

  @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(User user);

  @Select("SELECT * FROM users WHERE id = #{id}")
  User findById(Integer id);

  @Select("SELECT * FROM users")
  List<User> findAll();

  @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
  int update(User user);

  @Delete("DELETE FROM users WHERE id = #{id}")
  int deleteById(Integer id);
}
