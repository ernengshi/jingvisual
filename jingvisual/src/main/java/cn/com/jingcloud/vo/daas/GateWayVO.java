/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.vo.daas;

import lombok.Data;

/**
 *
 * @author liyong
 */
@Data
public class GateWayVO{

    private String id;
    private String uuid;
//    	ViewServer-212-253
    private String hostname;
//     IP地址
    private String ip;
//    子网掩码
    private String mask;
//     用户数量
    private String users;
//     用户数量
    private String connections;
//     启用时间
    private String start;
//      最后活动时间
    private String end;
    private String load_balance;
    private String load_balance_title;
    private String state;
    private String state_title;
    private String online;
    private String online_title;

}
