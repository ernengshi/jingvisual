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
public class HostMemCpuVO {

    private String ramfree;
    private String rambyvm;
    private String rambyplat;

    private String cpuusage;
    private String available;
    private String time;

}
