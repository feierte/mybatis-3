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
