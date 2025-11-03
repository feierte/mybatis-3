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
package org.apache.demo.mapper;

/**
 * @author Jie Zhao
 * @date 2024/11/3 10:16
 */
public interface TimeMapper {

  /**
   * 用于演示 mapper 中的 databaseId 属性。
   *
   * <p/>
   * 获取当前数据库的当前时间，如果是 mysql 数据库，获取的就是 mysql 数据库当前时间，
   * 如果是 postgresql 数据库，获取的就是 postgresql 数据库当前时间。
   * @return 当前时间的字符串表示形式
   */
  String getTime();
}
