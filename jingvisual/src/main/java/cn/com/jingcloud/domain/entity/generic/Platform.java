/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.entity.generic;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author liyong
 */
public class Platform {

    private long id;
    private String ip;
    @JsonIgnore
    private int port;
    //1 iaas 2 daas
    @JsonIgnore
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
