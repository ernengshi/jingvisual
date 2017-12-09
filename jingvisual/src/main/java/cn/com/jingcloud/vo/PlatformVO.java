/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author liyong
 */
@Data
@NoArgsConstructor
public class PlatformVO {

    private String id;
    private String name;
// 节点状态   	"Up"
    private String status;
    private String status_title;

    private String ipaddress;
//    活动节点
    private String role;
    private String role_title;
    //活动节点 true false 是否
    private String isactive;
    private String isactive_title;
    private String vipip;
    private String routerid;
}
