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
package org.sperri.test;

import org.junit.jupiter.api.Test;
import org.sperri.orm.Resources;
import org.sperri.orm.SqlSession;
import org.sperri.orm.SqlSessionFactory;
import org.sperri.orm.SqlSessionFactoryBuilder;
import org.sperri.orm.pojo.User;

import java.io.InputStream;

/**
 * @author Jie Zhao
 * @date 2021/3/15 21:07
 */
public class OrmTest {

  @Test
  public void test() throws Exception {
    String path = "sqlMapConfig.xml";
    InputStream inputStream = Resources.getResourceAsStream(path);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    SqlSession sqlSession = sqlSessionFactory.openSqlSession();

    User user = new User();
    User user1 = sqlSession.selectOne("user.selectUser", user);

    System.out.println(inputStream);
  }
}
