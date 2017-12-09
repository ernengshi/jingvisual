/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  ly
 * Created: 2017-7-19
 */

-- 采用RBAC模式建立数据库

--     RBAC 是基于角色的访问控制（Role-Based Access Control ）在 RBAC 中，权限与角色相关联，
-- 用户通过成为适当角色的成员而得到这些角色的权限。这就极大地简化了权限的管理。
-- 这样管理都是层级相互依赖的，权限赋予给角色，而把角色又赋予用户，这样的权限设计很清楚，管理起来很方便。

-- 表结构插入
DROP TABLE IF EXISTS `u_permission`; 
CREATE TABLE `u_permission` 
( 
`id` bigint(20) NOT NULL AUTO_INCREMENT, 
`url` varchar(256) DEFAULT NULL COMMENT 'url地址',
 `name` varchar(64) DEFAULT NULL COMMENT 'url描述',
 PRIMARY KEY (`id`) 
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

--  Table structure for table `u_role`  
DROP TABLE IF EXISTS `u_role`; 
CREATE TABLE `u_role` 
( 
`id` bigint(20) NOT NULL AUTO_INCREMENT,
 `name` varchar(32) DEFAULT NULL COMMENT '角色名称',
 `type` varchar(10) DEFAULT NULL COMMENT '角色类型',
 PRIMARY KEY (`id`) 
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--  Table structure for table `u_role_permission` 
DROP TABLE IF EXISTS `u_role_permission`;
CREATE TABLE `u_role_permission` 
(
 `rid` bigint(20) DEFAULT NULL COMMENT '角色ID',
 `pid` bigint(20) DEFAULT NULL COMMENT '权限ID' 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--  Table structure for table `u_user` 
DROP TABLE IF EXISTS `u_user`; 
CREATE TABLE `u_user` 
( `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `nickname` varchar(20) DEFAULT NULL COMMENT '用户昵称', 
`email` varchar(128) DEFAULT NULL COMMENT '邮箱|登录帐号',
 `pswd` varchar(32) DEFAULT NULL COMMENT '密码', 
`create_time` datetime DEFAULT NULL COMMENT '创建时间', 
`last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
 `status` bigint(1) DEFAULT '1' COMMENT '1:有效，0:禁止登录', PRIMARY KEY (`id`) 
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8; 

-- Table structure for table `u_user_role`
 
DROP TABLE IF EXISTS `u_user_role`; 
CREATE TABLE `u_user_role` ( 
`uid` bigint(20) DEFAULT NULL COMMENT '用户ID',
 `rid` bigint(20) DEFAULT NULL COMMENT '角色ID' 
) 
ENGINE=InnoDB DEFAULT CHARSET=utf8;


