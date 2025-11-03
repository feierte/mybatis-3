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
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * @author Clinton Begin
 *
 * @apiNote
 * MyBatis 允许你在已映射语句执行过程中的某一点进行拦截调用。默认情况下，MyBatis 允许使用插件来拦截的方法调用包括：
 *    1、Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed) // Executor 是负责执行低层映射语句的内部对象,真正执行sql的对象
 *    2、ParameterHandler (getParameterObject, setParameters) // 参数映射器,处理参数的
 *    3、ResultSetHandler (handleResultSets, handleOutputParameters) // 结果集映射器,处理返回结果的
 *    4、StatementHandler (prepare, parameterize, batch, update, query) // StatementID映射器
 *          △ environment 环境变量
 *              △ transactionManager 事务管理器
 *              △ dataSource 数据源
 */
public interface Interceptor {

  Object intercept(Invocation invocation) throws Throwable;

  default Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  default void setProperties(Properties properties) {
    // NOP
  }

}
