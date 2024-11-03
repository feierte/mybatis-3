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

import java.io.InputStream;

/**
 * 加载配置文件的工具类
 *
 * @author Jie Zhao
 * @date 2021/3/15 20:24
 */
public class Resources {

  public static InputStream getResourceAsStream(String path) {

    return Resources.class.getClassLoader().getResourceAsStream(path);
  }
}
