package org.apache.demo.mapper;

import org.apache.demo.entity.Article;

/**
 * @author Jie Zhao
 * @date 2024/11/3 13:58
 */
public interface ArticleMapper {

  Article selectArticleByOrderId(int id);
}
