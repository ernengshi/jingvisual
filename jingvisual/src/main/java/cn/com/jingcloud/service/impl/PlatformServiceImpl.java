/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.LocaleMessageSourceService;
import cn.com.jingcloud.service.PlatformService;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.PlatformVO;
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
public class PlatformServiceImpl implements PlatformService {

    private final static Logger LOG = LoggerFactory.getLogger(PlatformServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ResourceService iaasResourceService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;
    @Value("${daas.client.token}")
    private String token;

    /**
     * IAAS是否开启HA
     *
     * @return
     */
    private boolean isIaasHA(int pageNo, int pageSize) {
        boolean b = false;
        Platform iaas = iaasResourceService.findFirstIaas();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());
            String url = "http://{ip}:{port}/server/api?command=listConfigurations&category=kingwah&response=json&page=" + 1 + "&pagesize="
                    + 20;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listconfigurationsresponse");
                JsonNode platforms = response.path("configuration");
                if (platforms.isArray()) {
                    Iterator<JsonNode> iterator = platforms.iterator();
                    while (iterator.hasNext()) {
                        JsonNode node = iterator.next();
                        String name = node.path("name").textValue();
                        if ("manager.high.available".equals(name)) {
                            boolean value = node.path("value").asBoolean();
                            b = value;
                            break;
                        }
                    }
                }
            } catch (IOException ex) {
                LOG.error("IAAS listConfigurations " + ex.getMessage());
            }

        }

        return b;
    }

    /**
     * IAAS
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Pager<PlatformVO> pageIaasPlatform(int pageNo, int pageSize) {
//
        Platform iaas = iaasResourceService.findFirstIaas();
        List<PlatformVO> list = new ArrayList<>();
        Pager<PlatformVO> pager = new Pager<>();
        boolean b = isIaasHA(pageNo, pageSize);
        if (null != iaas && b) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());
            String url = "http://{ip}:{port}/client/api?command=getManagerHAInfo&response=json&page=" + pageNo + "&pageSize=" + pageSize + "&listAll=true";

            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("getmanagerhainforesponse");
                pager.setPageNo(pageNo);
                pager.setPageSize(pageSize);

                JsonNode platforms = response.path("managerhainfo").path("managerpeers").path("responses");
                if (platforms.isArray()) {
                    pager.setTotal(platforms.size());
                    Iterator<JsonNode> iterator = platforms.iterator();
                    while (iterator.hasNext()) {
                        PlatformVO vo = new PlatformVO();
                        JsonNode node = iterator.next();
                        String id = node.path("msid").asText();
                        String name = node.path("name").textValue();
                        String status = node.path("status").textValue();
                        String status_title = convertPlatformStatus(status);
                        String ipaddress = node.path("ipaddress").textValue();
                        String role = node.path("role").textValue();
                        String role_title = convertPlatformRole(role);
                        String isactive = node.path("isactive").asText();
                        String isactive_title = convertPlatformIsactive(isactive);
                        String vipip = node.path("vipip").textValue();
                        String routerid = node.path("routerid").asText();
                        vo.setId(id);
                        vo.setName(name);
                        vo.setStatus(status);
                        vo.setStatus_title(status_title);
                        vo.setIpaddress(ipaddress);
                        vo.setRole(role);
                        vo.setRole_title(role_title);
                        vo.setIsactive(isactive);
                        vo.setIsactive_title(isactive_title);
                        vo.setVipip(vipip);
                        vo.setRouterid(routerid);
                        list.add(vo);
                    }
                }
                pager.setResultList(list);
            } catch (IOException ex) {
                LOG.error("pageIaasPlatform " + ex.getMessage());
            }

        }
        return pager;
    }

    /**
     * DAAS是否开启HA
     *
     * @return
     */
    private boolean isDaasHA() {
        boolean b = false;
        Platform iaas = iaasResourceService.findFirstDaas();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());
            //String url = "http://{ip}:{port}/server/api?command=listManagerHAInfo&response=json&page=" + pageNo + "&pagesize=" + pageSize + "&token=" + token;
            String url = "http://{ip}:{port}/server/api?command=listPlatform&response=json&token=" + token;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listPlatformResponse");
//                int count = root.path("count").intValue();
                JsonNode platforms = response.path("platform");
                if (platforms.isArray()) {
//                    int arrayLength = platforms.size();

//                    if (arrayLength == 3) {
                    Iterator<JsonNode> iterator = platforms.iterator();
                    while (iterator.hasNext()) {
                        JsonNode node = iterator.next();
                        String name = node.path("name").textValue();
                        if ("viewmanager.high.available".equals(name)) {
                            boolean value = node.path("value").asBoolean();
                            b = value;
                            break;
                        }
                    }
//                    }
                }
            } catch (IOException ex) {
                LOG.error("DAAS listPlatform " + ex.getMessage());
            }

        }

        return b;
    }

    /**
     * DAAS
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Pager<PlatformVO> pageDaasPlatform(int pageNo, int pageSize) {

        Platform daas = iaasResourceService.findFirstDaas();
        List<PlatformVO> list = new ArrayList<>();
        Pager<PlatformVO> pager = new Pager<>();
        boolean b = isDaasHA();
        if (null != daas && b) {

            Map<String, Object> params = new HashMap();
            params.put("ip", daas.getIp());
            params.put("port", daas.getPort());
            String url = "http://{ip}:{port}/server/api?command=listManagerHAInfo&response=json&page=" + pageNo + "&pagesize=" + pageSize + "&token=" + token;
//            String url = "http://{ip}:{port}/server/api?command=listManagerHAInfo&response=json&token=" + token;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listManagerHAInfoResponse");
//                int totalsize = NumberUtils.toInt(response.path("totalsize").textValue());
                pager.setPageNo(pageNo);
                pager.setPageSize(pageSize);

                JsonNode platforms = response.path("managerHAInfo").path("managerpeers").path("responses");
                if (platforms.isArray()) {
                    pager.setTotal(platforms.size());
                    Iterator<JsonNode> iterator = platforms.iterator();
                    while (iterator.hasNext()) {
                        PlatformVO vo = new PlatformVO();
                        JsonNode node = iterator.next();
                        String id = node.path("msid").asText();
                        String name = node.path("name").textValue();
                        String status = node.path("status").textValue();
                        String status_title = convertPlatformStatus(status);
                        String ipaddress = node.path("ipaddress").textValue();
                        String role = node.path("role").textValue();
                        String role_title = convertPlatformRole(role);
                        String isactive = node.path("isactive").asText();
                        String isactive_title = convertPlatformIsactive(isactive);
                        String vipip = node.path("vipip").textValue();
                        String routerid = node.path("routerid").asText();
                        vo.setId(id);
                        vo.setName(name);
                        vo.setStatus(status);
                        vo.setStatus_title(status_title);
                        vo.setIpaddress(ipaddress);
                        vo.setRole(role);
                        vo.setRole_title(role_title);
                        vo.setIsactive(isactive);
                        vo.setIsactive_title(isactive_title);
                        vo.setVipip(vipip);
                        vo.setRouterid(routerid);
                        list.add(vo);
                    }
                }
                pager.setResultList(list);
            } catch (IOException ex) {
                LOG.error("pageDaasPlatform " + ex.getMessage());
            }

        }
        return pager;
    }

    /**
     * status IAAS/DAAS platform is same
     *
     * @param status
     * @return
     */
    private String convertPlatformStatus(String status) {
        String returnState = "";
        if (!Utils.isNullOrEmpty(status)) {
            switch (status) {
                case "Up":
                    returnState = "label.available";
                    break;
                case "Down":
                    returnState = "label.unavailable";
                    break;
                case "Error":
                    returnState = "label.error";
                    break;
                case "Unknown":
                    returnState = "label.getting";
                    break;
                default:
                    break;
            }
        }

        return localeMessageSourceService.getMessage(returnState);
    }

    /**
     * role IAAS/DAAS platform is same
     *
     * @param status
     * @return
     */
    private String convertPlatformRole(String role) {
        String returnState = "";
        if (!Utils.isNullOrEmpty(role)) {
            switch (role) {
                case "Primary":
                    returnState = "label.highavailability.isactive.primary";
                    break;
                case "Backup":
                    returnState = "label.highavailability.isactive.backup";
                    break;
                case "Unknown":
                    returnState = "label.getting";
                    break;
                default:
                    break;
            }
        }

        return localeMessageSourceService.getMessage(returnState);
    }

    /**
     * isactive IAAS/DAAS platform is same
     *
     * @param status
     * @return
     */
    private String convertPlatformIsactive(String status) {
        String returnState = "";
        if (!Utils.isNullOrEmpty(status)) {
            switch (status) {
                case "true":
                    returnState = "label.yes";
                    break;
                case "false":
                    returnState = "label.no";
                    break;
                default:
                    break;
            }
        }

        return localeMessageSourceService.getMessage(returnState);
    }
}
