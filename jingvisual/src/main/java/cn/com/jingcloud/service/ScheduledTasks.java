/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

/**
 *
 * @author ly
 */
public interface ScheduledTasks {

    void everyHour();
    
    void everyHourAnd15Min();
}
