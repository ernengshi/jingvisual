/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.service.PlatformService;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.PlatformVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import cn.com.jingcloud.service.ResourceService;
import io.swagger.annotations.Api;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liyong
 */
@Api(value = "管理平台接口")
@Controller
public class PlatformController {

    @Autowired
    ResourceService iaasResourceService;

    @Autowired
    PlatformService platformService;

    /**
     * iaas
     *
     * @param model
     *
     * @return
     */
    @RequestMapping(value = "/platform", method = RequestMethod.GET)
    public String iaasPlatform(ModelMap model) {
        int pageNo = 1;
        Pager<PlatformVO> ipage = platformService.pageIaasPlatform(pageNo, JingVisualConstant.PAGE_SIZE);
        Pager<PlatformVO> dpage = platformService.pageDaasPlatform(pageNo, JingVisualConstant.PAGE_SIZE);
        Map<String, List<PlatformVO>> map = new LinkedHashMap<>();
        map.put("IAAS管理平台", ipage.getResultList());
        map.put("DAAS管理平台", dpage.getResultList());
        model.put("map", map);
        return "platform";
    }

    @RequestMapping(value = "/platforms_data/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<PlatformVO>> iaasPlatformss(@PathVariable int pageNo) {
        Pager<PlatformVO> ipage = platformService.pageIaasPlatform(pageNo, JingVisualConstant.PAGE_SIZE);
        Pager<PlatformVO> dpage = platformService.pageDaasPlatform(pageNo, JingVisualConstant.PAGE_SIZE);
        Map<String, List<PlatformVO>> map = new HashMap<>();
        map.put("IAAS", ipage.getResultList());
        map.put("DAAS", dpage.getResultList());

        return map;
    }

    /**
     * iaas_data
     *
     * @param pageNo
     * @param map
     * @return
     */
    @RequestMapping(value = "/iplatforms_data/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<PlatformVO> iaasPlatforms_data(@PathVariable int pageNo, ModelMap map) {
        Pager<PlatformVO> page = platformService.pageIaasPlatform(pageNo, JingVisualConstant.PAGE_SIZE);
        return page;
    }

    /**
     * daas
     *
     * @param pageNo
     * @param map
     * @return
     */
//    @ApiIgnore
//    @RequestMapping(value = "/dplatforms/{pageNo}", method = RequestMethod.GET)
//    public String daasPlatforms(@PathVariable int pageNo, ModelMap map) {
//        Pager<PlatformVO> page = platformService.pageDaasPlatform(pageNo, JingVisualConstant.PAGE_SIZE);
////        String zoneid = zones.get(0).getId();
//        Platform iaas = iaasResourceService.findFirstDaas();
//        map.put("page", page);
//        map.put("ip", iaas.getIp());
//        map.put("port", iaas.getPort());
//        return "zone";
//    }
    /**
     * daas_data
     *
     * @param pageNo
     * @param map
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/dplatforms_data/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<PlatformVO> daasPlatforms_data(@PathVariable int pageNo, ModelMap map) {
        Pager<PlatformVO> page = platformService.pageDaasPlatform(pageNo, JingVisualConstant.PAGE_SIZE);
        return page;
    }
}
