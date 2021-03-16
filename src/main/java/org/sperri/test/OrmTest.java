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
