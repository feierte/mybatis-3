-- 该脚本用于创建多对多关系的数据
-- 在实际项目开发中，多对多关系也是非常常见的关系，
-- 比如，一个购物系统中，一个用户可以有多个订单，这是一对多的关系；一个订单中可以购买多种商品，一种商品也可以属于多个不同的订单，订单和商品就是多对多的关系。
-- 对于数据库中多对多关系建议使用一个中间表来维护关系，中间表中的订单d作为外键参照订单表的id，商品id作为外键参照商品表的id。


drop table if exists tb_user;
-- 创建用户表
CREATE TABLE tb_user (
     id INT PRIMARY KEY AUTO_INCREMENT,
     user_name VARCHAR(18),
     login_name VARCHAR(18),
     password VARCHAR(18),
     phone VARCHAR(18),
     address VARCHAR(18)
);

-- 插入用户表测试数据
INSERT INTO tb_user (user_name,login_name,password,phone,address)
VALUES('马云','jack','123456','13600000000','杭州');


drop table if exists tb_article;
-- 创建商品表
CREATE TABLE tb_article(
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(18),
                           price DOUBLE,
                           remark VARCHAR(18)
);
-- 插入商品表测试数据
INSERT INTO tb_article(name,price,remark) VALUES('精通Python自然语言处理',108.9,'经典著作');
INSERT INTO tb_article(name,price,remark) VALUES('自然语言处理原理与实践',99.9,'经典著作');
INSERT INTO tb_article(name,price,remark) VALUES('大数据架构详解',89.9,'经典著作');
INSERT INTO tb_article(name,price,remark) VALUES('推荐系统实践',69.9,'经典著作');


drop table if exists tb_order;
-- 创建订单表
CREATE TABLE tb_order(
     id INT PRIMARY KEY AUTO_INCREMENT,
     code VARCHAR(32),
     total DOUBLE,
     user_id INT,
     FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

-- 插入订单表测试数据
INSERT INTO tb_order(code,total,user_id)VALUES('20180315ORDER1212',388.6,1);
INSERT INTO tb_order(code,total,user_id)VALUES('20180315ORDER1213',217.8,1);


drop table if exists tb_item;
-- 创建中间表
CREATE TABLE tb_item(
    order_id INT,
    article_id INT,
    amount INT,
    PRIMARY KEY (order_id,article_id),
    FOREIGN KEY (order_id) REFERENCES  tb_order(id),
    FOREIGN KEY (article_id) REFERENCES tb_article(id)
);

-- 创建插入中间表数据
INSERT INTO tb_item(order_id,article_id,amount)VALUES(1,1,1);
INSERT INTO tb_item(order_id,article_id,amount)VALUES(1,2,1);
INSERT INTO tb_item(order_id, article_id,amount)VALUES(1,3,2);
INSERT INTO tb_item(order_id, article_id,amount)VALUES(2,4,2);
INSERT INTO tb_item(order_id, article_id,amount)VALUES(2,1,1);