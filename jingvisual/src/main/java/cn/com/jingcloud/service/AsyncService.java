/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

/**
 *
 * @author liyong
 */
public interface AsyncService {

    void saveZoneCapacity();

    void saveHostResourceGraphStats();

    void saveVMResourceMonitor();
    
    void saveTCGroupUsed();
}
