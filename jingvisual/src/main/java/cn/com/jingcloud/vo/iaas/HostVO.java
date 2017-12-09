/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.vo.iaas;

import lombok.Data;

/**
 *
 *
 * @author liyong
 */
@Data
public class HostVO {

    private String id;
    private String name;

    private String ip;
    private String cpunumber;
    private String cpuspeed;
    private String cpuused;
    private String cpu;
    private String memorytotal;
    private String memoryused;
    private String memoryallocated;
    private String memory;

    private String networkread;
    private String networkwrite;
    private String network;
//    资源状态
    private String resourcestate;
//    状态
    private String state;

    //    资源状态国际化翻译
    private String resourcestateTitle;
//    状态国际化翻译
    private String stateTitle;

   

}
