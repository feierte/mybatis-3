package org.apache.demo;

import org.apache.demo.entity.User;
import org.apache.demo.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jie zhao
 * @date 2020/4/9 11:09
 */
public class MybatisTest {

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

    public static void main(String[] args) throws IOException {

        // Configuration configuration = sqlSessionFactory.getConfiguration();

        /**
         * 每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的，因此是不能被共享的，
         * 所以它的最佳的作用域是请求或方法作用域。 绝对不能将 SqlSession 实例的引用放在一个类的静态域，
         * 甚至一个类的实例变量也不行。 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，
         * 比如 Servlet 框架中的 HttpSession。
         */
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            /*
             * 映射器是一些绑定映射语句的接口。映射器接口的实例是从 SqlSession 中获得的。
             * 虽然从技术层面上来讲，任何映射器实例的最大作用域与请求它们的 SqlSession 相同。
             * 但方法作用域才是映射器实例的最合适的作用域。 也就是说，映射器实例应该在调用它们的方法中被获取，
             * 使用完毕之后即可丢弃。
             */
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            User user = mapper.selectUser(1);
            System.out.println(user);

            /*User user1 = mapper.selectUserByConstructor(1);
            System.out.println(user1);*/
        }
    }
}
