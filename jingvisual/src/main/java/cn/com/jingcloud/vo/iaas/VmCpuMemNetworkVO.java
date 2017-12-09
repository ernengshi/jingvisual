/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.vo.iaas;

import lombok.Data;

/**
 *
 * @author liyong
 */
@Data
public class VmCpuMemNetworkVO {

    private String cpuuse;
    private String memuse;
    private String networkkbsread;
    private String networkkbswrite;
    private String time;

}
