/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.dao.generic.impl;

import cn.com.jingcloud.domain.dao.generic.*;
import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.repository.GenericRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 *
 * @author liyong
 */
@Repository("platformDaoImpl")
public class PlatformDaoImpl implements PlatformDao {

    @Autowired
    @Qualifier("primaryGenericRepository")
    GenericRepository genericRepository;

    @Override
    public Platform findFirstIaas() {
        String sql = "SELECT * FROM platform p where p.type=" + JingVisualConstant.IAAS_TYPE;
        return genericRepository.findFirst(Platform.class, sql, Collections.emptyMap());
    }

    @Override
    public Platform findFirstDaas() {
        String sql = "SELECT * FROM platform p where p.type=" + JingVisualConstant.DAAS_TYPE;
        return genericRepository.findFirst(Platform.class, sql, Collections.emptyMap());
    }

    @Override
    public Platform addIaas(Platform iaas) {
        long id = genericRepository.create(iaas);
        iaas.setId(id);
        return iaas;
    }

    @Override
    public Platform addDaas(Platform daas) {
        long id = genericRepository.create(daas);
        daas.setId(id);
        return daas;
    }

    @Override
    public Platform updateIaas(Platform iaas) {
        genericRepository.update(iaas);
        return iaas;
    }

    @Override
    public Platform updateDaas(Platform daas) {
        genericRepository.update(daas);
        return daas;
    }

    @Override
    public boolean deleteIaas(Platform iaas) {
        int rowsAffected = genericRepository.delete(iaas);
        return rowsAffected >= 1;
    }

    @Override
    public boolean deleteDaas(Platform daas) {
        int rowsAffected = genericRepository.delete(daas);
        return rowsAffected >= 1;
    }

    @Override
    public Platform findIaas(long id) {
        return genericRepository.find(id, Platform.class);
    }

    @Override
    public Platform findDaas(long id) {
        return genericRepository.find(id, Platform.class);
    }

}
