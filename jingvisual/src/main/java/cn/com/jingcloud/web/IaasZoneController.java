/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.IaasZoneService;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.DashboardVO;
import cn.com.jingcloud.vo.iaas.ZoneVO;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import cn.com.jingcloud.service.ResourceService;
import cn.com.jingcloud.vo.iaas.OnDuty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 *
 * @author liyong
 */
@Api(value = "数据中心接口")
@Controller
public class IaasZoneController {

    @Autowired
    ResourceService iaasResourceService;

    @Autowired
    IaasZoneService iaasZoneService;

    /**
     * 值班表分页
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/zone/onduty/{pageNo}/{pageSize}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<OnDuty> onduty(@PathVariable int pageNo, @PathVariable int pageSize) {
        Pager<OnDuty> pager = iaasZoneService.getOnDutys(pageNo, pageSize);
        return pager;
    }

    /**
     * 一周数据
     *
     * @param zoneId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/zone/{zoneId}/{type}/week", method = RequestMethod.GET)
    @ResponseBody
    public List<DashboardVO> week(@PathVariable String zoneId, @PathVariable int type) {
        List<DashboardVO> list = iaasZoneService.getWeekInformation(zoneId, type);
        return list;
    }

    /**
     * 一个月数据
     *
     * @param zoneId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/zone/{zoneId}/{type}/month", method = RequestMethod.GET)
    @ResponseBody
    public List<DashboardVO> month(@PathVariable String zoneId, @PathVariable int type) {
        List<DashboardVO> list = iaasZoneService.getMonthInformation(zoneId, type);
        return list;
    }

    /**
     * 一个季度数据
     *
     * @param zoneId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/zone/{zoneId}/{type}/season", method = RequestMethod.GET)
    @ResponseBody
    public List<DashboardVO> season(@PathVariable String zoneId, @PathVariable int type) {
        List<DashboardVO> list = iaasZoneService.getSeasonInformation(zoneId, type);
        return list;
    }

    /**
     * 一年数据
     *
     * @param zoneId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/zone/{zoneId}/{type}/year", method = RequestMethod.GET)
    @ResponseBody
    public List<DashboardVO> year(@PathVariable String zoneId, @PathVariable int type) {
        List<DashboardVO> list = iaasZoneService.getYearInformation(zoneId, type);
        return list;
    }

    /**
     * 一周所有类型数据
     *
     * @param zoneId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/zone/{zoneId}/week", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<DashboardVO>> week(@PathVariable String zoneId) {
        Map<String, List<DashboardVO>> map = iaasZoneService.getWeekInformation(zoneId);
        return map;
    }

    /**
     * 一个月所有类型数据
     *
     * @param zoneId
     * @param type
     * @return
     */
    @ApiOperation(value = "数据中心一个月数据")
    @RequestMapping(value = "/zone/{zoneId}/month", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<DashboardVO>> month(@PathVariable String zoneId) {
        Map<String, List<DashboardVO>> map = iaasZoneService.getMonthInformation(zoneId);
        return map;
    }

    /**
     * 一个季度所有类型数据
     *
     * @param zoneId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/zone/{zoneId}/season", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<DashboardVO>> season(@PathVariable String zoneId) {
        Map<String, List<DashboardVO>> map = iaasZoneService.getSeasonInformation(zoneId);
        return map;
    }

    /**
     * 一年所有类型数据
     *
     * @param zoneId
     * @param type
     * @return
     */
//    @ApiIgnore
    @RequestMapping(value = "/zone/{zoneId}/year", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<DashboardVO>> year(@PathVariable String zoneId) {
        Map<String, List<DashboardVO>> map = iaasZoneService.getYearInformation(zoneId);
        return map;
    }

    /**
     * 某个数据中心详情
     *
     * @param zoneId 数据中心ID
     * @return
     */
    @RequestMapping(value = "/zone/{zoneId}", method = RequestMethod.GET)
    public String dashboard(@PathVariable String zoneId, ModelMap map) {
//        List<ZoneVO> zones = jingVisualService.listZone();
//        String zoneid = zones.get(0).getId();
        Platform iaas = iaasResourceService.findFirstIaas();
        map.put("zoneid", zoneId);
        map.put("ip", iaas.getIp());
        map.put("port", iaas.getPort());
//        return "dashboard";
        return "data_center";
    }

    /**
     * 某个数据中心详情 进度条页面
     *
     * @param zoneId 数据中心ID
     * @return
     */
    @RequestMapping(value = "/zone_1/{zoneId}", method = RequestMethod.GET)
    public String dashboard_1(@PathVariable String zoneId, ModelMap map) {
//        List<ZoneVO> zones = jingVisualService.listZone();
//        String zoneid = zones.get(0).getId();
        Platform iaas = iaasResourceService.findFirstIaas();
        map.put("zoneid", zoneId);
        map.put("ip", iaas.getIp());
        map.put("port", iaas.getPort());
        return "dashboard_1";
    }

    /**
     * 数据中心
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/zones/{pageNo}", method = RequestMethod.GET)
    public String zones(@PathVariable int pageNo, ModelMap map) {
        Pager<ZoneVO> page = iaasZoneService.pageZone(pageNo, JingVisualConstant.PAGE_SIZE);
//        String zoneid = zones.get(0).getId();
        Platform iaas = iaasResourceService.findFirstIaas();
        map.put("page", page);
        map.put("ip", iaas.getIp());
        map.put("port", iaas.getPort());
        return "zone";
    }

    @RequestMapping(value = "/zones_data/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<ZoneVO> hostPageData(@PathVariable int pageNo, ModelMap map) {
        Pager<ZoneVO> page = iaasZoneService.pageZone(pageNo, JingVisualConstant.PAGE_SIZE);
        return page;
    }

    /**
     * index
     *
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        Pager<ZoneVO> page = iaasZoneService.pageZone(1, JingVisualConstant.PAGE_SIZE);
        List<ZoneVO> list = page.getResultList();
        if (!Utils.isNullOrEmpty(list) && list.size() == 1) {
            String zoneId = list.get(0).getId();
            return "redirect:/zone/" + zoneId;
        }
        return "redirect:/zones/1";
    }
}
