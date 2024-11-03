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
package org.apache.demo.batch;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class BatchTest {


  // org/sperri/mybatis/java/config/mybatis-config.xml
  private static final String RESOURCE = "org/apache/demo/config/mybatis-config.xml";

  /**
   * SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例。
   * 使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory
   * 被视为一种代码“坏习惯”。因此 SqlSessionFactory 的最佳作用域是应用作用域。有很多方法可以做到，
   * 最简单的就是使用单例模式或者静态单例模式。
   */
  private static SqlSessionFactory sqlSessionFactory;

  static {
    try {
      InputStream resourceAsStream = Resources.getResourceAsStream(RESOURCE);
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {



      sqlSession.commit();
    }
  }
}
