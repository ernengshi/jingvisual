/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.entity.p;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author liyong
 */
@ApiModel(value = "USER用户", description = "用户的基本信息")
@Entity
@Table(name = "user")
public class User implements Serializable {

    
    private static final long serialVersionUID = 6719850716603009660L;

    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(value = "用户名")
    @Column(nullable = false, length = 5)
    private String name;

    @Column(nullable = false)
    private Integer age;

//    @OneToOne
//    @JoinColumn(name = "userid")
//    private RedisUser ruser;
//
//    public RedisUser getUser() {
//        return ruser;
//    }
//
//    public void setUser(RedisUser user) {
//        this.ruser = user;
//    }

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    // 省略setter和getter 
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}
