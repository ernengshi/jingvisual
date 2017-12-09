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
public class TCVO {

    private String id;
    private String mac;
    private String ip;
    private String first_time;
    private String last_time;
    private String os;
    private String hostname;
    private String cpu_vendor;
    private String memory_size;
    private String disk_size;
    private String graphic_card_vendor;
    private String audio_card_vendor;
    private String state;
    private String state_title;

}
