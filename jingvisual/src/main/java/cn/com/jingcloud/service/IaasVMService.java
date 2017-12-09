/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.VmGroupVO;
import cn.com.jingcloud.vo.iaas.VmVO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liyong
 */
public interface IaasVMService {

    void saveVMResourceMonitor(String vmId);

    List<VmGroupVO> listVmGroup(int pageSize);

    Pager<VmGroupVO> pageVM(String groupid, int pageNo, int pageSize);

    Pager<VmVO> pageVirtualMachine(String groupid, int pageNo, int pageSize);

    public Map<String, List<String>> getWeekInformation(String vmId);

    public Map<String, List<String>> getMonthInformation(String vmId);

    public Map<String, List<String>> getSeasonInformation(String vmId);

    public Map<String, List<String>> getYearInformation(String vmId);

}
