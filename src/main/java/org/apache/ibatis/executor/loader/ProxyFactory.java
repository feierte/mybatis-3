/**
 *    Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.executor.loader;

import java.util.List;
import java.util.Properties;

import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;

/**
 * @author Eduardo Macarron
 *
 * @apiNote
 *
 * <p>延迟加载实现原理：使用javassist（默认）或cglib创建目标对象的代理对象。当调用代理对象的延迟加载属性的getting方法时，进入拦截器方法。
 * 比如调用user.getOrder().getName()方法，进入拦截器的invoke(...)方法，发现user.getOrder()需要延迟加载时，那么就会单独发送事先保存好的查询关联Order对象的sql,
 * 把Order查询上来，然后调用user.setOrder(order)方法，于是user对象的order属性就有值了，接着完成user.getOrder().getName()方法的调用。这就是延迟加载的基本原理。
 *
 * 总结：延迟加载主要是通过动态代理的形式实现，通过代理拦截到指定方法，执行数据加载。
 */
public interface ProxyFactory {

  default void setProperties(Properties properties) {
    // NOP
  }

  Object createProxy(Object target, ResultLoaderMap lazyLoader, Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

}
