/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.DaasGateWayService;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.daas.GateWayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import cn.com.jingcloud.service.ResourceService;

/**
 *
 * @author liyong
 */
//@ApiIgnore
@Controller
@Api(value = "接入服务管理")
public class DaasGateWayController {

    @Autowired
    ResourceService iaasResourceService;

    @Autowired
    DaasGateWayService daasGateWayService;

    /**
     * 网关
     *
     * @param pageNo
     * @param map
     * @return
     */
//    @ApiIgnore
    @RequestMapping(value = "/gateway", method = RequestMethod.GET)
    public String gateWays() {
        return "gateway";
    }

//    @ApiIgnore
    @ApiOperation(value = "分页查询网关数据")
    @RequestMapping(value = "/gateways_data/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<GateWayVO> gateWayPageData(@PathVariable int pageNo, ModelMap map) {
        Pager<GateWayVO> page = daasGateWayService.pageGateWay(pageNo, JingVisualConstant.PAGE_SIZE);
        return page;
    }

}
