/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.DaasGateWayService;
import cn.com.jingcloud.service.LocaleMessageSourceService;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.daas.GateWayVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import cn.com.jingcloud.service.ResourceService;

/**
 *
 * @author liyong
 */
@Service
public class DaasGateWayServiceImpl implements DaasGateWayService {

    private final static Logger LOG = LoggerFactory.getLogger(DaasGateWayServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ResourceService iaasResourceService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Value("${daas.client.token}")
    private String token;

    @Override
    public Pager<GateWayVO> pageGateWay(int pageNo, int pageSize) {

        Platform daas = iaasResourceService.findFirstDaas();
        List<GateWayVO> list = new ArrayList<>();
        
        Pager<GateWayVO> pager = new Pager<>();
        if (null != daas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", daas.getIp());
            params.put("port", daas.getPort());
            String url = "http://" + daas.getIp() + ":" + daas.getPort() + "/server/api?command=listGateways&response=json&page=" + pageNo + "&pagesize=" + 16 + "&searchBy=hostname&token=" + token;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("response");
                int totalsize = response.path("totalsize").intValue();
                int count = response.path("count").intValue();
                if (totalsize <= 0) {
                    totalsize = count;
                }
                pager.setPageNo(pageNo);
                pager.setPageSize(pageSize);
                pager.setTotal(totalsize);
                JsonNode zone = response.path("listgateways");
                if (zone.isArray()) {
                    Iterator<JsonNode> iterator = zone.iterator();
                    while (iterator.hasNext()) {
                        GateWayVO vo = new GateWayVO();
                        JsonNode node = iterator.next();
                        String id = node.path("id").asText();
                        String uuid = node.path("uuid").textValue();
                        String hostname = node.path("hostname").textValue();
                        String ip = node.path("ip").textValue();
                        String mask = node.path("mask").textValue();
                        String users = node.path("users").asText();
                        String connections = node.path("connections").asText();
                        String start = node.path("start").textValue();
                        String end = node.path("end").textValue();

                        int load_balance = node.path("load_balance").intValue();
                        String state = node.path("state").textValue();
                        String online = node.path("online").textValue();
                        vo.setId(id);
                        vo.setUuid(uuid);
                        vo.setHostname(hostname);
                        vo.setIp(ip);
                        vo.setMask(mask);
                        vo.setUsers(users);
                        vo.setConnections(connections);
                        vo.setStart(start);
                        vo.setEnd(end);
                        vo.setLoad_balance(load_balance + "");
                        vo.setLoad_balance_title(this.convertGateWayLoadBalance(load_balance));
                        vo.setState(state);
                        vo.setState_title(this.convertGateWayState(state));
                        vo.setOnline(online);
                        vo.setOnline_title(this.convertGateWayOnline(online));
                        list.add(vo);
                    }
                }
//                List<GateWayVO> list1 = new ArrayList<>();
//                list1.addAll(list);
//                List<GateWayVO> list2 = new ArrayList<>();
//                list2.addAll(list);
//                list2.addAll(list1);
                pager.setResultList(list);
            } catch (IOException ex) {
                LOG.error("pageVirtualMachine " + ex.getMessage());
            }

        }
        return pager;
    }

    /**
     * 负载均衡
     *
     * @param data
     * @return
     */
    private String convertGateWayLoadBalance(int data) {
        String returnState = "";

        if (data == 1) {
            returnState = "label.yes";
        } else {
            returnState = "label.no";
        }

        return localeMessageSourceService.getMessage(returnState);
    }

    /**
     * 状态
     *
     * @param state
     * @return
     */
    private String convertGateWayState(String state) {
        String returnState = "";

        if ("enabled".equals(state)) {
            returnState = "state.Enabled";
        } else if ("disabled".equals(state)) {
            returnState = "state.Disabled";
        }

        return localeMessageSourceService.getMessage(returnState);
    }

    /**
     * 是否在线
     *
     * @param online
     * @return
     */
    private String convertGateWayOnline(String online) {
        String returnState = "";

        if ("running".equals(online)) {
            returnState = "state.menu.enabled";
        } else if ("stoping".equals(online)) {
            returnState = "state.menu.disabled";
        }

        return localeMessageSourceService.getMessage(returnState);
    }
}
