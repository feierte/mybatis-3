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
package org.sperri.orm;

import org.dom4j.DocumentException;

import java.io.InputStream;

/**
 * 1、使用dom4j解析配置文件，将解析出来的内容封装到容器对象中
 * 2、创建SQLSessionFactory对象，用于生产SqlSession会话对象
 * @author Jie Zhao
 * @date 2021/3/15 20:21
 */
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(InputStream in) throws Exception {
    // 使用dom4j解析配置文件，将解析出来的内容封装到Configuration中
    XMLConfigBuilder builder = new XMLConfigBuilder();
    Configuration configuration = builder.parseConfiguration(in);

    // 创建SqlSessionFactory对象
    DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);

    return defaultSqlSessionFactory;
  }
}
