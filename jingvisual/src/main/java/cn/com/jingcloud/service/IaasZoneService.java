/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.DashboardVO;
import cn.com.jingcloud.vo.iaas.OnDuty;
import cn.com.jingcloud.vo.iaas.ZoneVO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liyong
 */
public interface IaasZoneService {

    void saveZoneCapacity(String zoneId);

    
    Pager<ZoneVO> pageZone(int pageNo, int pageSize);

    Pager<OnDuty> getOnDutys(int pageNo, int pageSize);
    
    List<DashboardVO> getWeekInformation(String zoneId, int type);

    
    List<DashboardVO> getMonthInformation(String zoneId, int type);

    List<DashboardVO> getSeasonInformation(String zoneId, int type);

    List<DashboardVO> getYearInformation(String zoneId, int type);

    Map<String, List<DashboardVO>> getWeekInformation(String zoneId);

    Map<String, List<DashboardVO>> getMonthInformation(String zoneId);

    Map<String, List<DashboardVO>> getSeasonInformation(String zoneId);

    Map<String, List<DashboardVO>> getYearInformation(String zoneId);
}
