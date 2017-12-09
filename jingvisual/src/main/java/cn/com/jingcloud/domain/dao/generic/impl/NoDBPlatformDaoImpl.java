/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.dao.generic.impl;

import cn.com.jingcloud.domain.dao.generic.PlatformDao;
import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.utils.PlatformConfUtil;
import cn.com.jingcloud.utils.Utils;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * 不使用数据库来实现
 *
 * @author liyong
 */
@Repository("noDBPlatformDaoImpl")
public class NoDBPlatformDaoImpl implements PlatformDao {

    private final static Logger LOG = LoggerFactory.getLogger(NoDBPlatformDaoImpl.class);

    /**
     * 组合结果
     *
     * @param value ip,port
     * @return
     */
    private Platform getPlatform(String value) {
        Platform platform = null;
        if (!Utils.isNullOrEmpty(value)) {
            platform = new Platform();
            String[] arr = null;
            arr = StringUtils.split(value, ",");
            if (arr != null && arr.length == 2) {
                platform.setIp(arr[0]);
                platform.setPort(NumberUtils.toInt(arr[1]));
            }
        }
        return platform;
    }

    @Override
    public Platform findFirstIaas() {
        PlatformConfUtil conf = PlatformConfUtil.instance();
        String value = conf.getConfig(PlatformConfUtil.JINGVISUAL_PLATFORM_IAAS);

        return getPlatform(value);
    }

    @Override
    public Platform findFirstDaas() {
        PlatformConfUtil conf = PlatformConfUtil.instance();
        String value = conf.getConfig(PlatformConfUtil.JINGVISUAL_PLATFORM_DAAS);

        return getPlatform(value);
    }

    @Override
    public Platform findIaas(long id) {
        return findFirstIaas();
    }

    @Override
    public Platform findDaas(long id) {
        return findFirstDaas();
    }

    @Override
    public Platform addIaas(Platform iaas) {
        String value = "=" + iaas.getIp() + "," + iaas.getPort();
        PlatformConfUtil conf = PlatformConfUtil.instance();
        List<String> list = new ArrayList<>();
        list.add(PlatformConfUtil.JINGVISUAL_PLATFORM_IAAS + value);
        try {
            conf.addConfig(list);
        } catch (Exception ex) {
            LOG.error("addIaas: " + ex.getMessage());
        }
        return iaas;
    }

    @Override
    public Platform addDaas(Platform daas) {
        String value = "=" + daas.getIp() + "," + daas.getPort();
        PlatformConfUtil conf = PlatformConfUtil.instance();
        List<String> list = new ArrayList<>();
        list.add(PlatformConfUtil.JINGVISUAL_PLATFORM_DAAS + value);
        try {
            conf.addConfig(list);
        } catch (Exception ex) {
            LOG.error("addDaas: " + ex.getMessage());
        }
        return daas;
    }

    @Override
    public Platform updateIaas(Platform iaas) {
        String value = "=" + iaas.getIp() + "," + iaas.getPort();
        PlatformConfUtil conf = PlatformConfUtil.instance();
        List<String> list = new ArrayList<>();
        list.add(PlatformConfUtil.JINGVISUAL_PLATFORM_IAAS + value);
        try {
            conf.updateConfig(list);
        } catch (Exception ex) {
            LOG.error("addIaas: " + ex.getMessage());
        }
        return iaas;
    }

    @Override
    public Platform updateDaas(Platform daas) {
        String value = "=" + daas.getIp() + "," + daas.getPort();
        PlatformConfUtil conf = PlatformConfUtil.instance();
        List<String> list = new ArrayList<>();
        list.add(PlatformConfUtil.JINGVISUAL_PLATFORM_DAAS + value);
        try {
            conf.updateConfig(list);
        } catch (Exception ex) {
            LOG.error("addDaas: " + ex.getMessage());
        }
        return daas;
    }

    @Override
    public boolean deleteIaas(Platform iaas) {
        boolean b = true;
        String value = "=" + iaas.getIp() + "," + iaas.getPort();
        PlatformConfUtil conf = PlatformConfUtil.instance();
        List<String> list = new ArrayList<>();
        list.add(PlatformConfUtil.JINGVISUAL_PLATFORM_IAAS + value);
        try {
            conf.deleteConfig(list);
        } catch (Exception ex) {
            LOG.error("deleteIaas: " + ex.getMessage());
            b = false;
        }
        return b;
    }

    @Override
    public boolean deleteDaas(Platform daas) {
        boolean b = true;
        String value = "=" + daas.getIp() + "," + daas.getPort();
        PlatformConfUtil conf = PlatformConfUtil.instance();
        List<String> list = new ArrayList<>();
        list.add(PlatformConfUtil.JINGVISUAL_PLATFORM_DAAS + value);
        try {
            conf.deleteConfig(list);
        } catch (Exception ex) {
            LOG.error("deleteDaas: " + ex.getMessage());
            b = false;
        }
        return b;
    }

}
