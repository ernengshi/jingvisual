/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.service.IaasVMService;
import cn.com.jingcloud.utils.repository.Pager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import cn.com.jingcloud.service.ResourceService;
import cn.com.jingcloud.vo.iaas.VmGroupVO;
import cn.com.jingcloud.vo.iaas.VmVO;
import java.util.List;
import java.util.Map;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author liyong
 */
@Controller
@Api(value = "虚拟桌面接口")
public class IaasVMController {

    @Autowired
    ResourceService iaasResourceService;

    @Autowired
    IaasVMService iaasVMService;

    /**
     * vm列表
     *
     * @return
     */
    @RequestMapping(value = "/vm_info", method = RequestMethod.GET)
    public String vmInfo() {

        return "/vm_info";
    }

    /**
     * 虚拟机曲线图
     *
     * @return
     */
    @RequestMapping(value = "/vm_chart/{vmid}", method = RequestMethod.GET)
    public String vmChart(@PathVariable String vmid, ModelMap map) {
        map.put("vmId", vmid);
        return "/vm_chart";
    }

    //    @ApiIgnore
    @ApiOperation(value = "分页查询虚拟机分组只显示虚拟机分组")
    @RequestMapping(value = "/vm_group", method = RequestMethod.GET)
    @ResponseBody
    public List<VmGroupVO> listVmGroup(@RequestParam int pageSize) {
        List<VmGroupVO> list = iaasVMService.listVmGroup(pageSize);
        return list;
    }

//    @ApiIgnore
    @ApiOperation(value = "分组分页查询虚拟机数据")
    @RequestMapping(value = "/vms_data/{groupid}/{pageNo}/{pageSize}", method = RequestMethod.GET)
    @ResponseBody
    public List<VmGroupVO> vmPageData(@PathVariable String groupid, @PathVariable int pageNo, @PathVariable int pageSize) {
        Pager<VmGroupVO> page = iaasVMService.pageVM(groupid, pageNo, pageSize);
        return page.getResultList();
    }

    /**
     * 分页查询虚拟机,供虚拟机自动刷新页面使用
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页查询虚拟机数据")
    @RequestMapping(value = "/vms_data/{pageNo}/{pageSize}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<VmVO> vmPageData(@PathVariable int pageNo, @PathVariable int pageSize) {
        Pager<VmVO> page = iaasVMService.pageVirtualMachine("", pageNo, pageSize);
        return page;
    }

    /**
     * 虚拟机一周所有类型数据
     *
     * @param vmId
     * @return
     */
//    @ApiIgnore
    @RequestMapping(value = "/vm/{vmId}/week", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> week(@PathVariable String vmId) {
        Map<String, List<String>> map = iaasVMService.getWeekInformation(vmId);
        return map;
    }

    /**
     * 一个月所有类型数据
     *
     * @param vmId
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/vm/{vmId}/month", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> month(@PathVariable String vmId) {
        Map<String, List<String>> map = iaasVMService.getMonthInformation(vmId);
        return map;
    }

    /**
     * 一个季度所有类型数据
     *
     * @param vmId
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/vm/{vmId}/season", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> season(@PathVariable String vmId) {
        Map<String, List<String>> map = iaasVMService.getSeasonInformation(vmId);
        return map;
    }

    /**
     * 一年所有类型数据
     *
     * @param vmId
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/vm/{vmId}/year", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> year(@PathVariable String vmId) {
        Map<String, List<String>> map = iaasVMService.getYearInformation(vmId);
        return map;
    }

}
