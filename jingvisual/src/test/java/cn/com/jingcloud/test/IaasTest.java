/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.IaasHostService;
import cn.com.jingcloud.utils.repository.GenericRepository;
import cn.com.jingcloud.vo.iaas.ZoneVO;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import cn.com.jingcloud.service.IaasZoneService;
import cn.com.jingcloud.service.ResourceService;

/**
 *
 * @author ly
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IaasTest {

    @Autowired
    @Qualifier("primaryGenericRepository")
    GenericRepository genericRepository;

    @Autowired
    ResourceService jingVisualService;

    
    @Autowired
    IaasZoneService iaasZoneService;

    @Autowired
    IaasHostService iaasHostService;
    @Before
    public void setUp() {
        genericRepository.tryUpdate("delete from user", Collections.emptyMap());
    }

    @Test
    public void create() {
        Platform iaas = new Platform();
        iaas.setIp("192.168.37.250");
        iaas.setPort(8096);
        iaas.setType(1);
        long id = genericRepository.create(iaas);
        System.out.println("----------" + id);
    }
//    listZoneIds

    @Test
    public void listZoneIds() {
        List<ZoneVO> zones = iaasZoneService.pageZone(1, 1000).getResultList();
        System.out.println("ids: " + zones);
    }

    @Test
    public void test() {
        String zoneId = iaasZoneService.pageZone(1, 1000).getResultList().get(0).getId();
        iaasZoneService.saveZoneCapacity(zoneId);
    }

    @Test
    public void testFindOne() {
        Platform iaas = jingVisualService.findFirstIaas();
        System.out.println("*******" + iaas.getIp());
    }
}
