/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.HostVO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liyong
 */
public interface IaasHostService {

    Pager<HostVO> pageHost(int pageNo, int pageSize);

    void saveHostResourceGraphStats(String hostId);

    Map<String, List<String>> getWeekInformation(String hostId);

    Map<String, List<String>> getMonthInformation(String hostId);

    Map<String, List<String>> getSeasonInformation(String hostId);

    Map<String, List<String>> getYearInformation(String hostId);
}
