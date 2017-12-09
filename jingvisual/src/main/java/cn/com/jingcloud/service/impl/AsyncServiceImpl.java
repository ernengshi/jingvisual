/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.service.AsyncService;
import cn.com.jingcloud.service.DaasTCService;
import cn.com.jingcloud.service.IaasHostService;
import cn.com.jingcloud.service.IaasVMService;
import cn.com.jingcloud.service.IaasZoneService;
//import cn.com.jingcloud.service.ThreadPoolService;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.constant.DateConstants;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.HostVO;
import cn.com.jingcloud.vo.iaas.VmGroupVO;
import cn.com.jingcloud.vo.iaas.VmVO;
import cn.com.jingcloud.vo.iaas.ZoneVO;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author liyong
 */
@Component
public class AsyncServiceImpl implements AsyncService {

    private final static Logger LOG = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Autowired
    IaasZoneService iaasZoneService;

    @Autowired
    IaasHostService iaasHostService;

    @Autowired
    IaasVMService iaasVMService;

    @Autowired
    DaasTCService daasTCService;

//    @Autowired
//    ThreadPoolService threadPoolService;
    @Async
    @Override
    public void saveZoneCapacity() {
        String time = DateFormatUtils.format(Calendar.getInstance().getTime(), DateConstants.DATE_TIME_FORMAT);
        LOG.info("saveZoneCapacity now time is " + time);
        /**
         * 原接口分页有问题，不管取第几页都有数据，数据不会为空
         */
        Pager<ZoneVO> pager = iaasZoneService.pageZone(1, 100);
        List<ZoneVO> list = pager.getResultList();

        for (ZoneVO zone : list) {
            if (null != zone) {
                String zoneId = zone.getId();
                iaasZoneService.saveZoneCapacity(zoneId);
            }
        }

    }

    @Async
    @Override
    public void saveHostResourceGraphStats() {
        String time = DateFormatUtils.format(Calendar.getInstance().getTime(), DateConstants.DATE_TIME_FORMAT);
        LOG.info("saveHostResourceGraphStats now time is " + time);
        for (int i = 1; i < Integer.MAX_VALUE; i++) {

            Pager<HostVO> pager = iaasHostService.pageHost(i, 100);
            List<HostVO> list = pager.getResultList();
            if (Utils.isNullOrEmpty(list)) {
                break;
            } else {
                for (HostVO host : list) {
                    if (null != host) {
                        String hostId = host.getId();
                        iaasHostService.saveHostResourceGraphStats(hostId);
                    }
                }
            }
        }

    }

    @Async
    @Override
    public void saveVMResourceMonitor() {
        String time = DateFormatUtils.format(Calendar.getInstance().getTime(), DateConstants.DATE_TIME_FORMAT);
        LOG.info("saveVMResourceMonitor now time is " + time);
        for (int i = 1; i < Integer.MAX_VALUE; i++) {

            //groupId
            Pager<VmVO> pager = iaasVMService.pageVirtualMachine("", i, 100);
            List<VmVO> list = pager.getResultList();
            if (Utils.isNullOrEmpty(list)) {
                break;
            } else {
                for (VmVO vm : list) {
                    if (null != vm) {
                        String vmId = vm.getId();
                        String state = vm.getState();
                        //只监控运行中的虚拟机
                        if ("Running".equals(state)) {
                            iaasVMService.saveVMResourceMonitor(vmId);
                        }
                    }
                }
            }
        }

    }

    /**
     * TC分组展示使用率，按照最小社区分组
     */
    @Async
    @Override
    public void saveTCGroupUsed() {
        String time = DateFormatUtils.format(Calendar.getInstance().getTime(), DateConstants.DATE_TIME_FORMAT);
        LOG.info("saveTCGroupUsed now time is " + time);

        //groupId
        List<VmGroupVO> list = iaasVMService.listVmGroup(100);
        if (!Utils.isNullOrEmpty(list)) {
            for (VmGroupVO vm : list) {
                if (null != vm && vm.isIsLeaf()) {
                    String groupId = vm.getId();
                    //分组保存信息
                    daasTCService.saveTCGroupUsed(groupId);
                }
            }
        }
    }
}
