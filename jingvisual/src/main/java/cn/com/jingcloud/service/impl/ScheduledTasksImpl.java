/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.service.AsyncService;
import cn.com.jingcloud.service.IaasHostService;
import cn.com.jingcloud.service.ScheduledTasks;
//import cn.com.jingcloud.service.ThreadPoolService;
//import cn.com.jingcloud.utils.thread.ThreadUtil;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import cn.com.jingcloud.service.IaasZoneService;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.constant.DateConstants;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.HostVO;
import cn.com.jingcloud.vo.iaas.ZoneVO;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * http://cron.qqe2.com/ 创建定时任务实现类
 *
 * @author liyong
 */
@Component
//@Service
public class ScheduledTasksImpl implements ScheduledTasks {

    private final static Logger LOG = LoggerFactory.getLogger(ScheduledTasksImpl.class);

    @Autowired
    IaasZoneService iaasZoneService;

    @Autowired
    IaasHostService iaasHostService;

    //    @Autowired
//    ThreadPoolService threadPoolService;
    @Autowired
    @Qualifier("javaExecutorService")
    ExecutorService javaExecutorService;

    @Autowired
    AsyncService asyncService;

    /**
     *
     */
    //    @Scheduled(cron = "0 0 0/1 * * ?")
    //    @Async
    @Scheduled(cron = "0 0/1 * * * ?")
    @Override
    public void everyHour() {
        asyncService.saveZoneCapacity();
        asyncService.saveHostResourceGraphStats();
        asyncService.saveVMResourceMonitor();
//        saveZoneCapacity();

//        saveHostResourceGraphStats();
    }

    /**
     * 2017/11/16 18:20:00 ,2017/11/16 19:20:00 ,2017/11/16 20:20:00
     */
    @Scheduled(cron = "0 0/5 * * * ?")
//    @Scheduled(cron = "0 20 0/1 * * ?")
    @Override
    public void everyHourAnd15Min() {
        asyncService.saveTCGroupUsed();
    }

    /**
     * 原接口分页有问题，不管取第几页都有数据，数据不会为空
     */
    private void saveZoneCapacity() {
        String time = DateFormatUtils.format(Calendar.getInstance().getTime(), DateConstants.DATE_TIME_FORMAT);
        LOG.info("saveZoneCapacity now time is " + time);

        Pager<ZoneVO> pager = iaasZoneService.pageZone(1, 100);
        List<ZoneVO> list = pager.getResultList();

//        ExecutorService service = threadPoolService.getExecutors();
        for (ZoneVO zone : list) {
            javaExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    String zoneId = zone.getId();
                    iaasZoneService.saveZoneCapacity(zoneId);
                }
            });
        }

    }

    /**
     * 此方法有内存耗尽风险，由于依赖分页接口取回来的数据，万一分页接口永远都有取不完的数据，则会创建无数多线程，导致内存耗尽
     */
    private void saveHostResourceGraphStats() {
        String time = DateFormatUtils.format(Calendar.getInstance().getTime(), DateConstants.DATE_TIME_FORMAT);
        LOG.info("saveHostResourceGraphStats now time is " + time);
        for (int i = 1; i < Integer.MAX_VALUE; i++) {

            Pager<HostVO> pager = iaasHostService.pageHost(i, 100);
            List<HostVO> list = pager.getResultList();
            if (Utils.isNullOrEmpty(list)) {
                break;
            } else {
//                ExecutorService service = threadPoolService.getExecutors();
                for (HostVO host : list) {
                    javaExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            String hostId = host.getId();
                            iaasHostService.saveHostResourceGraphStats(hostId);
                        }
                    });
                }
            }
        }

    }

}
