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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @author Jie Zhao
 * @date 2021/3/16 21:10
 */
public class XMLMapperBuilder {
  private Configuration configuration;

  public XMLMapperBuilder(Configuration configuration) {
    this.configuration = configuration;
  }


  public void parse(InputStream inputStream) throws Exception {
    Document document = new SAXReader().read(inputStream);

    Element rootElement = document.getRootElement();
    String namespace = rootElement.attributeValue("namespace");

    List<Element> list = rootElement.selectNodes("//select");
    for (Element element : list) {
      String id = element.attributeValue("id");
      String resultType = element.attributeValue("resultType");
      String parameterType = element.attributeValue("parameterType");
      String sql = element.getTextTrim();

      MappedStatement mappedStatement = new MappedStatement();
      mappedStatement.setId(id);
      mappedStatement.setResultType(resultType);
      mappedStatement.setParameterType(parameterType);
      mappedStatement.setSql(sql);
      configuration.getMappedStatementMap().put(namespace + "." + id, mappedStatement);
    }
  }
}
