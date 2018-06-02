package infrastructure

import com.typesafe.config.ConfigFactory
import scalikejdbc._, SQLInterpolation._

object DBInitializer {
  def run() {
    val config = ConfigFactory.load()

    val jdbcDriver = config.getString("org.mariadb.jdbc.Driver")
    val url = config.getString("jdbc:mariadb://127.0.0.1:3306/spetstore?characterEncoding=utf8")
    val user = config.getString("admin")
    val password = config.getString("admin")

    //    val driverClassName = config.getString("db.default.driver")
    //    val url = config.getString("db.default.url")
    //    val user = config.getString("db.default.user")
    //    val password = config.getString("db.default.password")

    Class.forName(jdbcDriver)
    ConnectionPool.singleton(url, user, password)
    DB readOnly {
      implicit s =>
        try {
          sql"select 1 from customer limit 1".map(_.long(1)).single().apply()
        } catch {
          case e: java.sql.SQLException =>
            DB.localTx {
              implicit s =>
                sql"""
DROP TABLE IF EXISTS `customer`;
""".execute().apply()

                //                sql"""
                //CREATE TABLE `customer` (
                //  `pk`                    BIGINT        NOT NULL AUTO_INCREMENT,
                //  `id`                    BIGINT        NOT NULL,
                //  `status`                INT           NOT NULL,
                //  `name`                  VARCHAR(256)  NOT NULL,
                //  `sex_type`              INT       NOT NULL,
                //  `zip_code`              VARCHAR(20)   NOT NULL,
                //  `pref_code`             INT           NOT NULL,
                //  `city_name`             VARCHAR(256)  NOT NULL,
                //  `address_name`          VARCHAR(256)  NOT NULL,
                //  `building_name`         VARCHAR(256),
                //  `email`                 VARCHAR(64)   NOT NULL,
                //  `phone`                 VARCHAR(64)   NOT NULL,
                //  `login_name`            VARCHAR(64)   NOT NULL,
                //  `password`              VARCHAR(64)   NOT NULL,
                //  `favorite_category_id`  BIGINT,
                //  PRIMARY KEY(`pk`),
                //  UNIQUE(`id`)
                //);
                //""".execute().apply()

                sql"""
CREATE TABLE `customer1` (
  `pk`                    BIGINT        NOT NULL AUTO_INCREMENT,
  `id`                    BIGINT        NOT NULL,
  `name`                  VARCHAR(256)  NOT NULL,
  `email`                 VARCHAR(64)   NOT NULL,
  `password`              VARCHAR(64)   NOT NULL,
  `favorite_category_id`  BIGINT,
  PRIMARY KEY(`pk`),
  UNIQUE(`id`)
);
""".execute().apply()

                sql"""
CREATE TABLE `customer2` (
  `pk`                    BIGINT        NOT NULL AUTO_INCREMENT,
  `login_name`            VARCHAR(64)   NOT NULL,
  `status`                INT           NOT NULL,
  `sex_type`              INT       NOT NULL,
  `zip_code`              VARCHAR(20)   NOT NULL,
  `pref_code`             INT           NOT NULL,
  `city_name`             VARCHAR(256)  NOT NULL,
  `address_name`          VARCHAR(256)  NOT NULL,
  `building_name`         VARCHAR(256),
  `phone`                 VARCHAR(64)   NOT NULL,
  `favorite_category_id`  BIGINT,
  PRIMARY KEY(`pk`),
  UNIQUE(`id`)
);
""".execute().apply()

                sql"""
DROP TABLE IF EXISTS `category`;
""".execute().apply()
                sql"""
CREATE TABLE `category` (
  `pk`          BIGINT        NOT NULL AUTO_INCREMENT,
  `id`          BIGINT        NOT NULL,
  `status`      INT           NOT NULL,
  `name`        VARCHAR(256)  NOT NULL,
  `description` VARCHAR(1024),
  PRIMARY KEY(`pk`),
  UNIQUE(`id`)
);
""".execute().apply()
                sql"""
CREATE TABLE `cart` (
  `pk`          BIGINT        NOT NULL AUTO_INCREMENT,
  `id`          BIGINT        NOT NULL,
  `status`      INT           NOT NULL,
  `customer_id` BIGINT        NOT NULL,
  PRIMARY KEY(`pk`),
  UNIQUE(`id`)
);
""".execute().apply()
                sql"""
CREATE TABLE `cart_item` (
  `pk`          BIGINT        NOT NULL AUTO_INCREMENT,
  `id`          BIGINT        NOT NULL,
  `status`      INT           NOT NULL,
  `cart_id`     BIGINT        NOT NULL,
  `no`          INT           NOT NULL,
  `item_id`     BIGINT        NOT NULL,
  `quantity`    INT           NOT NULL,
  `in_stock`    INT           NOT NULL,
  PRIMARY KEY(`pk`),
  UNIQUE(`id`),
  UNIQUE(`cart_id`,`no`)
);

""".execute().apply()
                sql"""
CREATE TABLE `order#00d801` (
  `pk`            BIGINT        NOT NULL AUTO_INCREMENT,
  `id`            BIGINT        NOT NULL,
  `status`        INT           NOT NULL,
  `order_status`  INT           NOT NULL,
  `order_date`    TIMESTAMP     NOT NULL,
  `customer_id`   BIGINT        NOT NULL,
  `customer_name` VARCHAR(256)  NOT NULL,
  `zip_code`      VARCHAR(20)   NOT NULL,
  `pref_code`     INT           NOT NULL,
  `city_name`     VARCHAR(256)  NOT NULL,
  `address_name`  VARCHAR(256)  NOT NULL,
  `building_name` VARCHAR(256),
  `email`         VARCHAR(64)   NOT NULL,
  `phone`         VARCHAR(64)   NOT NULL,
  PRIMARY KEY(`pk`),
  UNIQUE(`id`)
);
CREATE TABLE `order_item` (
  `pk`          BIGINT        NOT NULL AUTO_INCREMENT,
  `id`          BIGINT        NOT NULL,
  `status`      INT           NOT NULL,
  `order_id`    BIGINT        NOT NULL,
  `no`          INT           NOT NULL,
  `item_id`     BIGINT        NOT NULL,
  `quantity`    INT           NOT NULL,
  PRIMARY KEY(`pk`),
  UNIQUE(`id`),
  UNIQUE(`order_id`,`no`)
);
""".execute().apply()
            }
        }
    }
  }
}

