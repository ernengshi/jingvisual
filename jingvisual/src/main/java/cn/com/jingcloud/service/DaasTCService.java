/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

import cn.com.jingcloud.dto.daas.DesktopDTO;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.daas.TCGroupUsedVO;
import cn.com.jingcloud.vo.daas.TCGroupVO;
import cn.com.jingcloud.vo.daas.TCVO;

import java.util.List;

/**
 * @author liyong
 */
public interface DaasTCService {

    /**
     * 桌面管理
     *
     * @param pageNo
     * @param pageSize
     * @param keyword
     * @return
     */
    Pager<DesktopDTO> listDesktops(int pageNo, int pageSize, String keyword);

    Pager<TCVO> pageDaasTC(int pageNo, int pageSize, String keyword);

    List<TCGroupVO> listTCTree(String groupid, int pageNo, int pageSize);
    
    List<TCVO> listTCVO(String groupid, int pageNo, int pageSize);

//    Pager<TCVO> pageTC(String groupid, int pageNo, int pageSize);
    void saveTCGroupUsed(String groupId);

    List<TCGroupUsedVO> getWeekInformation(String groupId);

    List<TCGroupUsedVO> getMonthInformation(String groupId);

    List<TCGroupUsedVO> getSeasonInformation(String groupId);

    List<TCGroupUsedVO> getYearInformation(String groupId);
}
