/**
 *    Copyright 2009-2025 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.cache.decorators.TransactionalCache;

/**
 * @author Clinton Begin
 *
 * @apiNote 事务缓存管理器
 */
public class TransactionalCacheManager {

  // 缓存Cache与TransactionalCache 的映射关系
  // 从这里可以看出Cache是键，是为了取出TransactionalCache，所以二级缓存真正起作用的地方是TransactionalCache这个类。
  // 为什么要这样做呢？
  // 从源码可知，二级缓存是从MappedStatement对象中获取的，由于MappedStatement存在于全局配置中，可以被多个CachingExecutor获取到，
  // 这样就会出现线程安全问题。除此之外，若不加以控制，多个事务共用一个缓存实例，会导致脏读，
  // 至于脏读问题，需要借助TransactionalCacheManager类来处理，这就是为什么二级缓存会将查询结果先缓存到临时地方，等到commit或者close时，才会将结果存储到二级缓存中。
  private final Map<Cache, TransactionalCache> transactionalCaches = new HashMap<>();

  public void clear(Cache cache) {
    getTransactionalCache(cache).clear();
  }

  public Object getObject(Cache cache, CacheKey key) {
    // 从TransactionalCache中获取缓存
    return getTransactionalCache(cache).getObject(key);
  }

  public void putObject(Cache cache, CacheKey key, Object value) {
    getTransactionalCache(cache).putObject(key, value);
  }

  public void commit() {
    for (TransactionalCache txCache : transactionalCaches.values()) {
      txCache.commit(); // 这里会将存储了临时缓存的值真正存储到二级缓存中
    }
  }

  public void rollback() {
    for (TransactionalCache txCache : transactionalCaches.values()) {
      txCache.rollback();
    }
  }

  private TransactionalCache getTransactionalCache(Cache cache) {
    return transactionalCaches.computeIfAbsent(cache, TransactionalCache::new);
  }

}
