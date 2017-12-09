/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.entity.p;

/**
 * 包含了redisuser 和user多有字段,且只是一个普通字段
 *
 * @author liyong
 */
public class RedisUserVO {

    public RedisUserVO(Long id, String name, Integer age, Integer sex, Long userid) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.userid = userid;
    }

    private Long id;

    private String name;

    private Integer age;
    private Integer sex;

    private Long userid;

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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

}
