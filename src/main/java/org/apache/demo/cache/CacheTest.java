package org.apache.demo.cache;

import org.apache.demo.entity.User;
import org.apache.demo.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Jie Zhao
 * @date 2021/3/27 19:42
 */
public class CacheTest {

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
    SqlSession sqlSession1 = sqlSessionFactory.openSession();
    SqlSession sqlSession2 = sqlSessionFactory.openSession();
    UserMapper mapper = sqlSession1.getMapper(UserMapper.class);

    User parameter = new User(1, "张三", "");
    User user1 = mapper.selectUserByCondition(parameter);
    System.out.println(user1);

    sqlSession1.commit(); // 执行sqlSession的commit或者close方法才会将查询结果保存到二级缓存中去
    // sqlSession1.close();

    User user2 = sqlSession2.selectOne("org.apache.demo.mapper.UserMapper.selectUserByCondition", parameter);
    System.out.println(user1 == user2);
  }
}
