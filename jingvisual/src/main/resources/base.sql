
/**
 * Desc 工程数据库使用表
 * Author: liyong 
 * Created: 2017-8-21
 */

-- iaas  or daas
DROP TABLE IF EXISTS `platform`;
CREATE TABLE `platform` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `ip` VARCHAR(15) NOT NULL,
  `port` SMALLINT(5)  NOT NULL,
  `type` TINYINT(1)  NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8

-- city tree
DROP TABLE IF EXISTS `area_tree`;
CREATE TABLE `area_tree` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(15) NOT NULL,
  `enabled` TINYINT(1)  NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8






