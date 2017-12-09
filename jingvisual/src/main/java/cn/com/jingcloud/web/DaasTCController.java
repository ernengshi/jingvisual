/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.domain.dao.generic.AreaTreeDao;
import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.DaasTCService;
import cn.com.jingcloud.service.IaasVMService;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.repository.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import cn.com.jingcloud.service.ResourceService;
import cn.com.jingcloud.vo.daas.TCGroupUsedVO;
import cn.com.jingcloud.vo.daas.TCGroupVO;
import cn.com.jingcloud.vo.daas.TreeNode;
import cn.com.jingcloud.vo.daas.TCVO;
import cn.com.jingcloud.vo.iaas.VmGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author liyong
 */
//@ApiIgnore
@Api(value = "TC接口")
@Controller
public class DaasTCController {

    @Autowired
    ResourceService resourceService;

    @Autowired
    DaasTCService daasTCService;

    @Autowired
    AreaTreeDao areaTreeDao;

    @Autowired
    IaasVMService iaasVMService;

    //    @ApiIgnore
    @ApiOperation(value = "分组查询TC")
    @RequestMapping(value = "/tc_group", method = RequestMethod.GET)
    @ResponseBody
    public List<VmGroupVO> listVmGroup(@RequestParam int pageSize) {
        List<VmGroupVO> list = iaasVMService.listVmGroup(pageSize);
        return list;
    }

    /**
     * 思路 1> iaas分组查询虚拟机 2> iaas使用分组id查询虚拟机 3> daas 中使用虚拟机name查询虚拟机对应的用户名 4>
     * daas中使用username_OS 作为tcName 查询tc信息
     *
     * @param groupid
     * @param pageNo
     * @param pageSize
     * @return
     */
//    @ApiIgnore
    @ApiOperation(value = "分页查询TC数据_展示树形")
    @RequestMapping(value = "/tcs_data/{groupid}/{pageNo}/{pageSize}", method = RequestMethod.GET)
    @ResponseBody
    public List<TCGroupVO> tcTreePageData(@PathVariable String groupid, @PathVariable int pageNo, @PathVariable int pageSize) {

        return daasTCService.listTCTree(groupid, pageNo, pageSize);

    }

    /**
     * TC数据展示
     *
     * @param groupid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页查询TC数据")
    @RequestMapping(value = "/tcs_vo/{groupid}/{pageNo}/{pageSize}", method = RequestMethod.GET)
    @ResponseBody
    public List<TCVO> tcVOPageData(@PathVariable String groupid, @PathVariable int pageNo, @PathVariable int pageSize) {

        return daasTCService.listTCVO(groupid, pageNo, pageSize);

    }

    @ApiOperation(value = "分组查询各个社区的TC周使用率")
    @RequestMapping(value = "/tcs_used/{groupid}/week", method = RequestMethod.GET)
    @ResponseBody
    public List<TCGroupUsedVO> week(@PathVariable String groupid) {
        return daasTCService.getWeekInformation(groupid);
    }

    @ApiOperation(value = "分组查询各个社区的TC月使用率")
    @RequestMapping(value = "/tcs_used/{groupid}/month", method = RequestMethod.GET)
    @ResponseBody
    public List<TCGroupUsedVO> month(@PathVariable String groupid) {
        return daasTCService.getMonthInformation(groupid);
    }

    @ApiOperation(value = "分组查询各个社区的TC季度使用率")
    @RequestMapping(value = "/tcs_used/{groupid}/season", method = RequestMethod.GET)
    @ResponseBody
    public List<TCGroupUsedVO> season(@PathVariable String groupid) {
        return daasTCService.getSeasonInformation(groupid);
    }

    @ApiOperation(value = "分组查询各个社区的TC年使用率")
    @RequestMapping(value = "/tcs_used/{groupid}/year", method = RequestMethod.GET)
    @ResponseBody
    public List<TCGroupUsedVO> vmPageData(@PathVariable String groupid) {
        return daasTCService.getYearInformation(groupid);
    }

    /**
     * daas
     *
     * @param pageNo
     * @param map
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/tcs/{pageNo}", method = RequestMethod.GET)
    public String gateWays(@PathVariable int pageNo, ModelMap map) {
        Pager<TCVO> page = daasTCService.pageDaasTC(pageNo, JingVisualConstant.PAGE_SIZE, "");
        int count = page.getResultList().size();
        if (count == 0) {
            return "redirect:/tcs/1";
        }
        Platform daas = resourceService.findFirstDaas();
        map.put("page", page);
        map.put("count", count);
        map.put("ip", daas.getIp());
        map.put("port", daas.getPort());
        return "tc";
    }

    /**
     * daas_data
     *
     * @param pageNo
     * @param map
     * @return
     */
    @RequestMapping(value = "/tcs_data/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public Pager<TCVO> daasPlatforms_data(@PathVariable int pageNo) {
        Pager<TCVO> page = daasTCService.pageDaasTC(pageNo, JingVisualConstant.PAGE_SIZE, "");
        return page;
    }

    /**
     * tree
     *
     * @return
     */
//    @RequestMapping(value = "/ztree", method = RequestMethod.GET)
//    public String ztree() {
//
//        return "/tree/ztree";
//    }
    /**
     * ztree_async_edit
     *
     * @return
     */
//    @RequestMapping(value = "/ztree2", method = RequestMethod.GET)
//    public String ztree2() {
//
//        return "/tree/ztree_async_edit";
//    }
    /**
     * ztree_async_edit
     *
     * @return
     */
//    @RequestMapping(value = "/ztree3", method = RequestMethod.GET)
//    public String ztree3() {
//
//        return "/tree/diy_async";
//    }
    @RequestMapping(value = "/tc_map", method = RequestMethod.GET)
    public String tc_map() {

        return "/tc_map";
    }

    @RequestMapping(value = "/tc_chart", method = RequestMethod.GET)
    public String tc_chart() {

        return "/tc_chart";
    }

    /**
     * page 虚拟机树形结构
     *
     * @return
     */
    @RequestMapping(value = "/ztree_vm", method = RequestMethod.GET)
    public String ztree_vm() {

        return "/tree/page_vm";
    }

    /**
     * page 虚拟机树形结构
     *
     * @return
     */
    @RequestMapping(value = "/ztree_tc", method = RequestMethod.GET)
    public String ztree_tc() {

        return "/tree/page_tc";
    }

    /**
     * btree
     *
     * @return
     */
    @RequestMapping(value = "/btree", method = RequestMethod.GET)
    public String btree() {

        return "/tree/bootstrap_tree";
    }

    /**
     * json data
     *
     * @param request
     * @param pageNo
     * @param map
     * @return
     */
    @RequestMapping(value = "/ztree_data", method = RequestMethod.GET)
    @ResponseBody
    public List<TreeNode> ztree_data(HttpServletRequest request) {

//        List<AreaTree> AreaTree = new ArrayList<>();
//        String paramPid = request.getParameter("pid");
//        if (!Utils.isNullOrEmpty(paramPid)) {
//            AreaTree = areaTreeDao.findAreaTrees(NumberUtils.toLong(paramPid));
//        } else {
//            AreaTree = areaTreeDao.findProvinces();
//        }
//        String name = request.getParameter("name");
//        String level = request.getParameter("level");
//        String otherParam = request.getParameter("otherParam");
//
//        System.out.println("---------" + id + " " + name + " " + level + " " + otherParam);
        List<TreeNode> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            list.add(new TreeNode(1, "01", 0, true));
            list.add(new TreeNode(2, "02", 0, true));
            list.add(new TreeNode(3, "03", 0, true));
            list.add(new TreeNode(4, "04", 1, false));

//            
//            list.add(new TreeNode(1, 0, "n1"));
//            list.add(new TreeNode(2, 0, "n2"));
//            list.add(new TreeNode(3, 0, "n3"));
//            list.add(new TreeNode(4, 0, "n4"));
//            list.add(new TreeNode(5, 1, "n4"));
        }
        return list;

//        for (AreaTree areaTree : AreaTree) {
//            long id = areaTree.getId();
//            String name = areaTree.getName();
//            long pid = areaTree.getPid();
//            list.add(new TreeNode(id, name, "/webjars/zTree/css/zTreeStyle/img/diy/1_open.png", pid, true));
//        }
//        return list;
    }

    /**
     * 地图
     *
     * @return
     */
    @RequestMapping(value = "map", method = RequestMethod.GET)
    public String map() {
        return "map/index";
    }

    /**
     * 百度地图
     *
     * @return
     */
    @RequestMapping(value = "baidu/map", method = RequestMethod.GET)
    public String baiduMap() {
        return "map/baidu/index";
    }

    /**
     * 百度地图
     *
     * @return
     */
    @RequestMapping(value = "baidu/map1", method = RequestMethod.GET)
    public String baiduMap1() {
        return "map/baidu/index1";
    }

    /**
     * 百度地图
     *
     * @return
     */
    @RequestMapping(value = "baidu/map2", method = RequestMethod.GET)
    public String baiduMap2() {
        return "map/baidu/index2";
    }
}
