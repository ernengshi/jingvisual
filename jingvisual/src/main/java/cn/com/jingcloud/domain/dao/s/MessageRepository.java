/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.dao.s;

import cn.com.jingcloud.domain.entity.s.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring-data-jpa的一大特性:通过解析方法名创建查询 由于Spring-data-jpa依赖于Hibernate 所以JPQL语句 有点类似
 * hql
 *
 * @author liyong
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
}
