/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.domain.entity.generic.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;
import cn.com.jingcloud.service.ResourceService;

/**
 *
 * @author liyong
 */
@Controller
@Api(value = "设置模块")
public class SettingController {

    private final static Logger LOG = LoggerFactory.getLogger(SettingController.class);

    /**
     * iaas setting
     *
     * @return
     */
    @RequestMapping(value = "/setting/iaas", method = RequestMethod.GET)
    public String settingIaas() {
        return "setting_iaas";
    }

    /**
     * daas setting
     *
     * @return
     */
    @RequestMapping(value = "/setting/daas", method = RequestMethod.GET)
    public String settingDaas() {
        return "setting_daas";
    }

    @Autowired
    ResourceService resourceService;

    @ApiOperation(value = "查询iaas平台信息")
    @RequestMapping(value = "/iaas", method = RequestMethod.GET)
    @ResponseBody
    public Platform getIAAS() {
        Platform platform = resourceService.findFirstIaas();
        return platform;
    }

    @ApiOperation(value = "查询daas平台信息")
    @RequestMapping(value = "/daas", method = RequestMethod.GET)
    @ResponseBody
    public Platform getDAAS() {
        Platform platform = resourceService.findFirstDaas();
        return platform;
    }

    /**
     * @PathVariable出现点号"."时导致路径参数截断获取不全的解决办法
     *
     * 在@RequestMapping的value中使用SpEL来表示，value中的{version}换成{version:.+}。
     */
    @ApiOperation(value = "添加iaas平台")
    @RequestMapping(value = "/iaas/{ip:.+}", method = RequestMethod.POST)
    @ResponseBody
    public Platform addIAAS(@PathVariable String ip) {

        Platform platform = resourceService.addIaas(ip, JingVisualConstant.IAAS_PORT);
        return platform;
    }

    @ApiOperation(value = "添加daas平台")
    @RequestMapping(value = "/daas/{ip:.+}", method = RequestMethod.POST)
    @ResponseBody
    public Platform addDAAS(@PathVariable String ip) {

        Platform platform = resourceService.addDaas(ip, JingVisualConstant.DAAS_PORT);
        return platform;
    }

    @ApiOperation(value = "更新iaas平台")
    @RequestMapping(value = "/iaas/{ip:.+}", method = RequestMethod.PUT)
    @ResponseBody
    public Platform updateIAAS(@PathVariable String ip, @RequestParam(name = "id", required = false) Long id) {
        if (null == id) {
            id = 0L;
        }
        Platform platform = resourceService.updateIaas(id, ip);
        return platform;
    }

    @ApiOperation(value = "更新daas平台")
    @RequestMapping(value = "/daas/{ip:.+}", method = RequestMethod.PUT)
    @ResponseBody
    public Platform updateDAAS(@PathVariable String ip, @RequestParam(value = "id", required = false) Long id) {
        if (null == id) {
            id = 0L;
        }
        Platform platform = resourceService.updateDaas(id, ip);
        return platform;
    }

    @ApiOperation(value = "删除iaas平台")
    @RequestMapping(value = "/iaas/{ip:.+}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean deleteIAAS(@PathVariable String ip, @RequestParam(name = "id", required = false) Long id) {
        if (null == id) {
            id = 0L;
        }
        boolean b = resourceService.deleteIaas(id, ip);
        return b;
    }

    @ApiOperation(value = "删除daas平台")
    @RequestMapping(value = "/daas/{ip:.+}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean deleteDAAS(@PathVariable String ip, @RequestParam(value = "id", required = false) Long id) {
        if (null == id) {
            id = 0L;
        }
        boolean b = resourceService.deleteDaas(id, ip);
        return b;
    }

}
