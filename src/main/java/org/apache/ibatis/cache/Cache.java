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
package org.apache.ibatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * SPI for cache providers.
 * <p>
 * One instance of cache will be created for each namespace.
 * <p>
 * The cache implementation must have a constructor that receives the cache id as an String parameter.
 * <p>
 * MyBatis will pass the namespace as id to the constructor.
 *
 * <pre>
 * public MyCache(final String id) {
 *   if (id == null) {
 *     throw new IllegalArgumentException("Cache instances require an ID");
 *   }
 *   this.id = id;
 *   initialize();
 * }
 * </pre>
 *
 * @author Clinton Begin
 *
 * @apiNote MyBatis中的缓存相关类都在cache包下面，而且定义了一个顶级接口Cache，默认只有一个实现类PerpetualCache，PerpetualCache中是内部维护了一个HashMap来实现缓存。
 * 需要注意的是decorators包下面的所有类也实现了Cache接口，那么为什么我还是要说Cache只有一个实现类呢？其实看名字就知道了，这个包里面全部是装饰器，也就是说这其实是装饰器模式的一种实现。
 * 随便打开decorator包下面的一个接口可以看到，最终都是调用了delegate来实现，只是将部分功能做了增强，其本身都需要依赖Cache的唯一实现类PerpetualCache(因为装饰器内需要传入Cache对象，故而只能传入PerpetualCache对象，因为接口是无法直接new出来传进去的)。
 */

public interface Cache {

  /**
   * @return The identifier of this cache
   */
  String getId();

  /**
   * @param key
   *          Can be any object but usually it is a {@link CacheKey}
   * @param value
   *          The result of a select.
   */
  void putObject(Object key, Object value);

  /**
   * @param key
   *          The key
   * @return The object stored in the cache.
   */
  Object getObject(Object key);

  /**
   * As of 3.3.0 this method is only called during a rollback
   * for any previous value that was missing in the cache.
   * This lets any blocking cache to release the lock that
   * may have previously put on the key.
   * A blocking cache puts a lock when a value is null
   * and releases it when the value is back again.
   * This way other threads will wait for the value to be
   * available instead of hitting the database.
   *
   *
   * @param key
   *          The key
   * @return Not used
   */
  Object removeObject(Object key);

  /**
   * Clears this cache instance.
   */
  void clear();

  /**
   * Optional. This method is not called by the core.
   *
   * @return The number of elements stored in the cache (not its capacity).
   */
  int getSize();

  /**
   * Optional. As of 3.2.6 this method is no longer called by the core.
   * <p>
   * Any locking needed by the cache must be provided internally by the cache provider.
   *
   * @return A ReadWriteLock
   */
  default ReadWriteLock getReadWriteLock() {
    return null;
  }

}
