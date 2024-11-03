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
