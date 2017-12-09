/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.dao.generic;

import cn.com.jingcloud.domain.entity.generic.Platform;

/**
 *
 * @author liyong
 */
public interface PlatformDao {

    Platform findFirstIaas();

    Platform findFirstDaas();
    
    Platform findIaas(long id);

    Platform findDaas(long id);

    Platform addIaas(Platform iaas);

    Platform addDaas(Platform daas);

    Platform updateIaas(Platform iaas);

    Platform updateDaas(Platform daas);

    boolean deleteIaas(Platform iaas);

    boolean deleteDaas(Platform daas);
}
