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
public class TCGroupUsedVO {

    private String used;
    private long online;
    private long total;
    private String time;
}
