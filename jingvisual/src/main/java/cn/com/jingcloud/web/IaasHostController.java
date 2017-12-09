/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.IaasHostService;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.HostVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author liyong
 */
@Api(value = "iaas平台主机接口")
@Controller
public class IaasHostController {

    @Autowired
    ResourceService iaasResourceService;

    @Autowired
    IaasHostService iaasHostService;

    /**
     * 轮播数据+页面
     *
     * @param map
     * @return
     */
//    @ApiIgnore
//    @ApiOperation(value = "单个主机页面——供测试使用")
//    @RequestMapping(value = "/hosts_test/{pageNo}", method = RequestMethod.GET)
//    public String hosts_test(@PathVariable int pageNo, ModelMap map) {
//        Pager<HostVO> page = iaasHostService.pageHost(pageNo, JingVisualConstant.PAGE_SIZE);
//        Platform iaas = iaasResourceService.findFirstIaas();
//        map.put("page", page);
//        map.put("ip", iaas.getIp());
//        map.put("port", iaas.getPort());
////        return "host";
//        return "host_for_data";
//    }
    /**
     * 主机页面
     *
     * @param pageNo
     * @param map
     * @return
     */
    @ApiOperation(value = "单个主机页面")
    @RequestMapping(value = "/hosts/{pageNo}", method = RequestMethod.GET)
    public String hosts(@PathVariable int pageNo, ModelMap map) {
        Pager<HostVO> page = iaasHostService.pageHost(pageNo, JingVisualConstant.PAGE_SIZE);
//        Platform iaas = iaasResourceService.findFirstIaas();
//        map.put("page", page);
//        map.put("ip", iaas.getIp());
//        map.put("port", iaas.getPort());

        int count = page.getResultList().size();
        if (count == 0 && pageNo > 1) {
            return "redirect:/hosts/1";
        }
        map.put("page", page);
        map.put("count", count);
        return "host";
    }

    /**
     *
     * @param pageNo
     * @param map
     * @return
     */
    @ApiOperation(value = "单个主机页面 js套数据")
    @RequestMapping(value = "/hosts_js", method = RequestMethod.GET)
    public String hosts_js() {
//        Pager<HostVO> page = iaasHostService.pageHost(pageNo, JingVisualConstant.PAGE_SIZE);
//
//        int count = page.getResultList().size();
//        if (count == 0 && pageNo > 1) {
//            return "redirect:/hosts_js/1";
//        }
//        map.put("page", page);
//        map.put("count", count);
        return "host_js";
    }

    /**
     * 轮播数据，只有数据
     *
     * @param pageNo
     * @param map
     * @return
     */
//    @ApiIgnore
    @ApiOperation(value = "分页查询主机数据")
    @RequestMapping(value = "/hosts_data/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<HostVO> hostPageData(@PathVariable int pageNo, ModelMap map) {
        Pager<HostVO> page = iaasHostService.pageHost(pageNo, JingVisualConstant.PAGE_SIZE);

        return page;
    }

    /**
     * 自动轮播 只有js
     *
     * @param map
     * @return
     */
//    @ApiIgnore
//    @RequestMapping(value = "/hosts", method = RequestMethod.GET)
//    public String hosts(ModelMap map) {
//        Pager<HostVO> page = iaasHostService.pageHost(1, JingVisualConstant.PAGE_SIZE);
//        Platform iaas = iaasResourceService.findFirstIaas();
//        map.put("page", page);
//        map.put("ip", iaas.getIp());
//        map.put("port", iaas.getPort());
//        return "host_for";
//    }
    /**
     * 具体某一个主机
     *
     * @param hostId
     * @param map
     * @return
     */
//    @ApiIgnore
    @RequestMapping(value = "/host/{hostId}", method = RequestMethod.GET)
    public String host(@PathVariable String hostId, ModelMap map) {
        Platform iaas = iaasResourceService.findFirstIaas();
        map.put("hostId", hostId);
        map.put("ip", iaas.getIp());
        map.put("port", iaas.getPort());
        return "hostinfo";
    }

    /**
     * 主机一周所有类型数据
     *
     * @param hostId
     * @param type
     * @return
     */
//    @ApiIgnore
    @RequestMapping(value = "/host/{hostId}/week", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> week(@PathVariable String hostId) {
        Map<String, List<String>> map = iaasHostService.getWeekInformation(hostId);
        return map;
    }

    /**
     * 一个月所有类型数据
     *
     * @param hostId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/host/{hostId}/month", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> month(@PathVariable String hostId) {
        Map<String, List<String>> map = iaasHostService.getMonthInformation(hostId);
        return map;
    }

    /**
     * 一个季度所有类型数据
     *
     * @param hostId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/host/{hostId}/season", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> season(@PathVariable String hostId) {
        Map<String, List<String>> map = iaasHostService.getSeasonInformation(hostId);
        return map;
    }

    /**
     * 一年所有类型数据
     *
     * @param hostId
     * @param type
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/host/{hostId}/year", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> year(@PathVariable String hostId) {
        Map<String, List<String>> map = iaasHostService.getYearInformation(hostId);
        return map;
    }
}
