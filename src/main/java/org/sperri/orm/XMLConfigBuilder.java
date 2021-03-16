package org.sperri.orm;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author Jie Zhao
 * @date 2021/3/16 20:45
 */
public class XMLConfigBuilder {

  private Configuration configuration;

  public XMLConfigBuilder() {
    this.configuration = new Configuration();
  }

  public Configuration parseConfiguration(InputStream in) throws Exception {
    Document document = new SAXReader().read(in);
    Element rootElement = document.getRootElement();
    List<Element> list = rootElement.selectNodes("//property");
    Properties properties = new Properties();
    for (Element element : list) {
      String name = element.attributeValue("name");
      String value = element.attributeValue("value");
      properties.setProperty(name, value);
    }

    ComboPooledDataSource dataSource = new ComboPooledDataSource();
    dataSource.setDriverClass(properties.getProperty("driverClass"));
    dataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
    dataSource.setUser("username");
    dataSource.setPassword("password");
    configuration.setDataSource(dataSource);

    // mapper.xml解析
    List<Element> nodes = rootElement.selectNodes("//mapper");
    for (Element node : nodes) {
      String resource = node.attributeValue("resource");
      InputStream inputStream = Resources.getResourceAsStream(resource);
      XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
      xmlMapperBuilder.parse(inputStream);
    }

    return this.configuration;
  }
}
