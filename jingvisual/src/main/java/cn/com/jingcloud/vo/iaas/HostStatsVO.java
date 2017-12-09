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
public class HostStatsVO {

    private String ramallocated;
    private String ramavailable;
    private String rambysystem;
    private String rambyplat;
    private String ramtotal;
    private String rambyvm;
    private String available;
    private String cpuusage;
    private String networkkbsread;
    private String networkkbswrite;
    private String cpunumber;
    private String cpuspeed;
    private String date;

}
