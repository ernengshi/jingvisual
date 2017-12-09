/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.IaasVMService;
import cn.com.jingcloud.service.LocaleMessageSourceService;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.VmVO;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import cn.com.jingcloud.service.ResourceService;
import cn.com.jingcloud.utils.Charsets;
import cn.com.jingcloud.utils.constant.DateConstants;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.constant.PicConstant;
import cn.com.jingcloud.vo.iaas.VmCpuMemNetworkVO;
import cn.com.jingcloud.vo.iaas.VmGroupVO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author liyong
 */
@Service
public class IaasVMServiceImpl implements IaasVMService {

    private final static Logger LOG = LoggerFactory.getLogger(IaasVMServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ResourceService resourceService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Override
    public List<VmGroupVO> listVmGroup(int pageSize) {

        Platform iaas = resourceService.findFirstIaas();
        List<VmGroupVO> list = new ArrayList<>();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());
            String url = "http://{ip}:{port}/client/api?command=listVmGroup&response=json";
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listvmgroupresponse");
                JsonNode vmGroup = response.path("uservmgroup");
                if (vmGroup.isArray()) {
                    Iterator<JsonNode> iterator = vmGroup.iterator();
                    while (iterator.hasNext()) {
                        VmGroupVO tree = new VmGroupVO();
                        JsonNode node = iterator.next();
                        String id = node.path("id").textValue();
                        String name = node.path("name").textValue();
                        String parentid = node.path("parentid").textValue();
//                        long accountid = node.path("accountid").longValue();
                        tree.setId(id);
                        tree.setName(name);
                        if (!Utils.isNullOrEmpty(parentid)) {
                            tree.setpId(parentid);
                        } else {
                            tree.setpId(null);
                        }
                        tree.setIsParent(true);
                        list.add(tree);
                    }
                }
            } catch (IOException ex) {
                LOG.error("listVmGroup " + ex.getMessage());
            }

        }
        list = initListVmGroup(list, pageSize);
        return list;
    }

    /**
     * 找出树的完整路径
     *
     * @param parentNode
     * @return
     */
    private String getTreeGlobalPath(List<VmGroupVO> parentNodes, VmGroupVO leafNode, String str) {
        String result = str;
        if (!Utils.isNullOrEmpty(parentNodes)) {
            List<VmGroupVO> parentNodes2 = new CopyOnWriteArrayList<>();
            parentNodes2.addAll(parentNodes);

            for (VmGroupVO vo : parentNodes2) {
                String id = vo.getId();
                String pid = vo.getpId();

                String leafPid = leafNode.getpId();
                if (null != leafPid && leafPid.equals(id)) {
                    String leafName = vo.getName() + "_" + str;
                    parentNodes2.remove(vo);
                    return getTreeGlobalPath(parentNodes2, vo, leafName);
                }
            }
        }
        return result;

    }

    /**
     * 1>设置叶子节点 2>给叶子结点设置完整书的路径
     *
     * @param list
     * @return
     */
    private List<VmGroupVO> initLeafNode(List<VmGroupVO> list) {
        List<String> pids = new ArrayList<>();
        for (VmGroupVO tree : list) {
            pids.add(tree.getpId());
        }
        //分理出叶子结点和非叶子结点
        List<VmGroupVO> leafNode = new ArrayList<>();
        List<VmGroupVO> parentNode = new CopyOnWriteArrayList<>();

        for (VmGroupVO tree : list) {//有父节点 但没有子节点
            if (tree.isIsParent() && !pids.contains(tree.getId())) {
                tree.setIsLeaf(true);
                leafNode.add(tree);
            } else {
                parentNode.add(tree);
            }
        }

        for (VmGroupVO vo : leafNode) {
            String treeGlobalPathName = getTreeGlobalPath(parentNode, vo, vo.getName());
            vo.setLeafNodeGlobalPath(treeGlobalPathName);
//            System.out.println(heh);
        }
        parentNode.addAll(leafNode);
        return parentNode;
//        return list;
    }

    /**
     * pageSize从前台传入方便在前台修改代码
     *
     * @param list
     * @param pageSize
     * @return
     */
    private List<VmGroupVO> initListVmGroup(List<VmGroupVO> list, int pageSize) {
        long begin = System.currentTimeMillis();
        List<VmGroupVO> result = initLeafNode(list);
        long end = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("initListVmGroup spend total time is " + (end - begin));
        }

        for (VmGroupVO tree : result) {
            if (tree.isIsLeaf()) {
                String gid = tree.getId();
                Pager<VmVO> pager = pageVirtualMachine(gid, 1, pageSize);
                int count = pager.getTotal();
                tree.setMaxPage(pager.getPageCount());
                tree.setCount(count);
            } else {
                tree.setOpen(true);
            }
            tree.setT("请点击分页按钮");
            tree.setPage(0);
            tree.setPageSize(pageSize);
        }
        return result;
    }

    /**
     * 虚拟机分组管理
     *
     * @param groupid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Pager<VmGroupVO> pageVM(String groupid, int pageNo, int pageSize) {
        Pager<VmVO> vmPager = this.pageVirtualMachine(groupid, pageNo, pageSize);
        List<VmVO> list = vmPager.getResultList();

        List<VmGroupVO> groups = new ArrayList<>();
        for (VmVO vm : list) {
            VmGroupVO group = new VmGroupVO();
            group.setId(vm.getId());
            group.setName(vm.getName());
            group.setIsParent(false);
            group.setpId(groupid);
            String state = vm.getState();
            if (!Utils.isNullOrEmpty(state) && "Running".equals(state)) {
//                group.setIcon("/webjars/zTree/css/zTreeStyle/img/diy/1_open.png");
                group.setIcon(PicConstant.VM_STATE_PIC_PATH_RUNNING);
            } else if (!Utils.isNullOrEmpty(state) && "Stopped".equals(state)) {
//                group.setIcon("/webjars/zTree/css/zTreeStyle/img/diy/1_close.png");
                group.setIcon(PicConstant.VM_STATE_PIC_PATH_STOPPED);
            } else if (!Utils.isNullOrEmpty(state) && "Error".equals(state)) {
//                group.setIcon("/webjars/zTree/css/zTreeStyle/img/diy/1_close.png");
                group.setIcon(PicConstant.VM_STATE_PIC_PATH_ERROR);
            } else {
                group.setIcon(PicConstant.VM_STATE_PIC_PATH_STOPPED);
            }
            group.setClick(true);
            groups.add(group);
        }

        Pager<VmGroupVO> result = new Pager<>();

        result.setPageNo(vmPager.getPageNo());
        result.setPageSize(vmPager.getPageSize());
        result.setTotal(vmPager.getTotal());
        result.setResultList(groups);
        return result;
    }

    /**
     * iaas 查询分页虚拟机
     *
     * @param groupid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Pager<VmVO> pageVirtualMachine(String groupid, int pageNo, int pageSize) {

        Platform iaas = resourceService.findFirstIaas();
        List<VmVO> list = new ArrayList<>();
        Pager<VmVO> pager = new Pager<>();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());

//            params.put("ip", "192.168.211.252");
//            params.put("port", iaas.getPort());
            String url = "http://{ip}:{port}/client/api?command=listVirtualMachines&response=json&page=" + pageNo + "&pageSize=" + pageSize + "&listAll=true";
            if (!Utils.isNullOrEmpty(url)) {
                url = url + "&groupid=" + groupid;
            }
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listvirtualmachinesresponse");
                int totalsize = NumberUtils.toInt(response.path("totalsize").textValue());
                int currentpage = NumberUtils.toInt(response.path("currentpage").textValue());
                pager.setPageNo(currentpage);
                pager.setPageSize(pageSize);
                pager.setTotal(totalsize);
                JsonNode zone = response.path("virtualmachine");
                if (zone.isArray()) {
                    Iterator<JsonNode> iterator = zone.iterator();
                    while (iterator.hasNext()) {
                        VmVO vo = new VmVO();
                        JsonNode node = iterator.next();
                        String id = node.path("id").textValue();
                        String name = node.path("name").textValue();

//                        String ipaddress = node.path("ipaddress").textValue();
//                        vo.setIp(ipaddress);
                        String cpuuse = node.path("cpuuse").asText();
                        vo.setCpuuse(cpuuse);
                        String memuse = node.path("memuse").asText();
                        vo.setMemuse(memuse);

                        long networkkbsread = node.path("networkkbsread").longValue();
                        vo.setNetworkkbsread(networkkbsread + "");
                        long networkkbswrite = node.path("networkkbswrite").longValue();
                        vo.setNetworkkbswrite(networkkbswrite + "");
                        vo.setNetwork(Utils.toReadableSize(networkkbsread) + " (r) " + Utils.toReadableSize(networkkbswrite) + " (w) ");

                        String state = node.path("state").textValue();

                        JsonNode nic = node.path("nic");
                        if (nic.isArray()) {
                            Iterator<JsonNode> nicIterator = nic.iterator();
                            while (nicIterator.hasNext()) {
                                JsonNode nicNode = nicIterator.next();

                                String ipaddress = nicNode.path("ipaddress").textValue();
                                if (!Utils.isNullOrEmpty(ipaddress)) {
                                    vo.setIp(ipaddress);
                                    break;
                                }
                            }
                        }
                        vo.setId(id);
                        vo.setName(name);
                        vo.setState(state);
                        vo.setStateTitle(this.convertVmState(state));
                        list.add(vo);
                    }
                }
                pager.setResultList(list);
            } catch (IOException ex) {
                LOG.error("pageVirtualMachine " + ex.getMessage());
            }

        }
        return pager;
    }

    private String convertVmState(String state) {
        String vmState = "";
        if (!Utils.isNullOrEmpty(state)) {
            switch (state) {
                case "Running":
                    vmState = "state.Running";
                    break;
                case "Stopped":
                    vmState = "state.Stopped";
                    break;
                case "Starting":
                    vmState = "state.Starting";
                    break;
                case "Sleeping":
                    vmState = "state.Sleeping";
                    break;
                case "Sleeped":
                    vmState = "state.Sleeped";
                    break;
                case "Wakeup":
                    vmState = "state.Wakeup";
                    break;
                case "Stopping":
                    vmState = "state.Stopping";
                    break;
                case "Expunging":
                    vmState = "state.Expunging";
                    break;
                case "Error":
                    vmState = "state.Error";
                    break;
                case "Migrating":
                    vmState = "state.Migrating";
                    break;
                case "Destroyed":
                    vmState = "state.Destroyed";
                    break;
                default:
                    break;
            }
        }

        return localeMessageSourceService.getMessage(vmState);
    }
    private static final String[] VM_RESOURCE_MONITOR_NAMES = {"cpuused", "memuse", "networkkbsread", "networkkbswrite", "time"};

    private List<VmCpuMemNetworkVO> getVMResourceMonitor(String vmId) {
        long time = resourceService.getDateIgnoreMinuteAndSECOND();
        Platform iaas = resourceService.findFirstIaas();
        List<VmCpuMemNetworkVO> list = new ArrayList<>();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());

            String url = "http://{ip}:{port}/client/api?command=listVirtualMachines&response=json&id=" + vmId;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listvirtualmachinesresponse");
                JsonNode vmResponse = response.path("virtualmachine");
                if (vmResponse.isArray()) {

                    Iterator<JsonNode> iterator = vmResponse.iterator();
                    while (iterator.hasNext()) {
                        VmCpuMemNetworkVO vo = new VmCpuMemNetworkVO();
                        JsonNode node = iterator.next();

                        String cpuused = node.path(VM_RESOURCE_MONITOR_NAMES[0]).asText();//cpu使用率
                        if (!Utils.isNullOrEmpty(cpuused)) {
                            cpuused = StringUtils.substringBeforeLast(cpuused, "%");
                        } else {
                            cpuused = "0";
                        }
                        String memuse = node.path(VM_RESOURCE_MONITOR_NAMES[1]).asText();//内存
                        if (Utils.isNullOrEmpty(memuse)) {

                            memuse = "0";
                        }
//                        long rambysystem = node.path("rambysystem").longValue();
                        long networkkbsread = node.path(VM_RESOURCE_MONITOR_NAMES[2]).asLong();//网络读
                        if (networkkbsread < 0) {

                            networkkbsread = 0;
                        }
                        long networkkbswrite = node.path(VM_RESOURCE_MONITOR_NAMES[3]).asLong();//网络写
                        if (networkkbswrite < 0) {

                            networkkbswrite = 0;
                        }
                        vo.setCpuuse(cpuused);
                        vo.setMemuse(memuse);
                        vo.setNetworkkbsread(networkkbsread + "");
                        vo.setNetworkkbswrite(networkkbswrite + "");
                        vo.setTime(time + "");
                        list.add(vo);
                    }

                }
            } catch (IOException ex) {
                LOG.error("getHostResourceGraphStats " + ex.getMessage());
            }

        }
        return list;

    }

    /**
     * 保存虚拟机资源信息
     *
     * @param vmId
     */
    @Async
    @Override
    public void saveVMResourceMonitor(String vmId) {

        List<VmCpuMemNetworkVO> list = this.getVMResourceMonitor(vmId);
        for (VmCpuMemNetworkVO vo : list) {
            StringBuilder sb = new StringBuilder();

            String cpuuse = vo.getCpuuse();
            String memuse = vo.getMemuse();
            String networkkbsread = vo.getNetworkkbsread();
            String networkkbswrite = vo.getNetworkkbswrite();
            String time = vo.getTime();

            sb.append(cpuuse).append(StringUtils.SPACE).
                    append(memuse).append(StringUtils.SPACE).
                    append(networkkbsread).append(StringUtils.SPACE).
                    append(networkkbswrite).append(StringUtils.SPACE).
                    append(time);
            String fileName = initVMIndexFileName(vmId);
            //resourceService
            resourceService.synchronizedWriteFile(fileName, sb.toString());
        }

    }

    private String initVMIndexFileName(String vmId) {

        String path = JingVisualConstant.VM_FILE_PATH + vmId;
        return path + JingVisualConstant.FILE_PATH_SUFFIX;
    }

    @Override
    public Map<String, List<String>> getWeekInformation(String vmId) {

        return getVMLimit(vmId, JingVisualConstant.WEEK_SIZE);
    }

    @Override
    public Map<String, List<String>> getMonthInformation(String vmId) {

        return getVMLimit(vmId, JingVisualConstant.MONTH_SIZE);
    }

    @Override
    public Map<String, List<String>> getSeasonInformation(String vmId) {

        return getVMLimit(vmId, JingVisualConstant.SEASON_SIZE);
    }

    @Override
    public Map<String, List<String>> getYearInformation(String vmId) {
        return getVMLimit(vmId, JingVisualConstant.YEAR_SIZE);
    }

    private List<VmCpuMemNetworkVO> getHosts(String vmId, int limit) {

        String fileName = initVMIndexFileName(vmId);
        List<VmCpuMemNetworkVO> list = readFile(fileName, limit);

        return list;
    }

    private Map<String, List<String>> getVMLimit(String vmId, int limit) {

        Map<String, List<String>> map = new HashMap<>();
        List<String> cpuuses = new ArrayList<>();
        List<String> memuses = new ArrayList<>();
        List<String> networkkbsreads = new ArrayList<>();
        List<String> networkkbswrites = new ArrayList<>();
        List<String> times = new ArrayList<>();

        List<VmCpuMemNetworkVO> list = getHosts(vmId, limit);
        for (VmCpuMemNetworkVO vm : list) {
            cpuuses.add(vm.getCpuuse());
            memuses.add(vm.getMemuse());
            networkkbsreads.add(vm.getNetworkkbsread());
            networkkbswrites.add(vm.getNetworkkbswrite());
            times.add(vm.getTime());
        }

        map.put(VM_RESOURCE_MONITOR_NAMES[0], cpuuses);
        map.put(VM_RESOURCE_MONITOR_NAMES[1], memuses);
        map.put(VM_RESOURCE_MONITOR_NAMES[2], networkkbsreads);
        map.put(VM_RESOURCE_MONITOR_NAMES[3], networkkbswrites);
        map.put(VM_RESOURCE_MONITOR_NAMES[4], times);
        return map;
    }

    /**
     * 读取索引文件
     *
     * @param path
     *
     */
    /*
    private List<VmCpuMemNetworkVO> readFileFromHead(String fileName, int limit) {
        long start = System.currentTimeMillis();
        LineNumberReader lnr = null;
        String str = null;
        List<VmCpuMemNetworkVO> list = new ArrayList<>();
        try {
            lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(fileName), Charsets.UTF_8));
            while ((str = lnr.readLine()) != null) {
                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 5) {
                    VmCpuMemNetworkVO vm = new VmCpuMemNetworkVO();
                    vm.setCpuuse(arr[0]);
                    vm.setMemuse(arr[1]);
                    vm.setNetworkkbsread(arr[2]);
                    vm.setNetworkkbswrite(arr[3]);
                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[4]), DateConstants.DATE_TIME_FORMAT);
                    vm.setTime(date);
                    list.add(vm);
                }

                if (lnr.getLineNumber() >= limit) {
                    break;
                }

//                System.out.println(str + " : " + lnr.getLineNumber());
            }

        } catch (FileNotFoundException e) {
            LOG.error(fileName + "not found" + e.getMessage());
        } catch (IOException e) {
            LOG.error(fileName + "read index " + e.getMessage());
        } finally {
            try {
                if (null != lnr) {
                    lnr.close();
                }
            } catch (IOException e) {
                LOG.error("closed io failed " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();

        if (LOG.isDebugEnabled()) {
            LOG.debug("read vm index Use Time: " + (end - start));
        }

//        Collections.reverse(list);
        return list;
    }

    private List<VmCpuMemNetworkVO> readFileFromTail(String fileName, int limit) {
        long start = System.currentTimeMillis();
        ReversedLinesFileReader rdf = null;
        AtomicInteger flag = new AtomicInteger(0);
        String str = null;
        List<VmCpuMemNetworkVO> list = new ArrayList<>();
        try {
            rdf = new ReversedLinesFileReader(new File(fileName), 64, Charsets.UTF_8);
            while ((str = rdf.readLine()) != null) {
                flag.incrementAndGet();
                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 5) {
                    VmCpuMemNetworkVO vm = new VmCpuMemNetworkVO();
                    vm.setCpuuse(arr[0]);
                    vm.setMemuse(arr[1]);
                    vm.setNetworkkbsread(arr[2]);
                    vm.setNetworkkbswrite(arr[3]);
                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[4]), DateConstants.DATE_TIME_FORMAT);
                    vm.setTime(date);
                    list.add(vm);
                }

                if (flag.get() >= limit) {
                    break;
                }

//                System.out.println(str + " : " + lnr.getLineNumber());
            }

        } catch (FileNotFoundException e) {
            LOG.error(fileName + "not found" + e.getMessage());
        } catch (IOException e) {
            LOG.error(fileName + "read index " + e.getMessage());
        } finally {
            try {
                if (null != rdf) {
                    rdf.close();
                }
            } catch (IOException e) {
                LOG.error("closed io failed " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();

        if (LOG.isDebugEnabled()) {
            LOG.debug("read vm index Use Time: " + (end - start) + " Line Num: "
                    + flag.get());
        }

        Collections.reverse(list);
        return list;
    }*/
    private List<VmCpuMemNetworkVO> readFile(String fileName, int limit) {

        List<VmCpuMemNetworkVO> list = new ArrayList<>();

//        List<String> lines = resourceService.readFileFromTail(fileName, 64, limit);
        List<String> lines = resourceService.synchronizedReadFileFromTail(fileName, 64, limit);
        for (String str : lines) {
            String[] arr = StringUtils.split(str, StringUtils.SPACE);
            if (null != arr && arr.length == 5) {
                VmCpuMemNetworkVO vm = new VmCpuMemNetworkVO();
                vm.setCpuuse(arr[0]);
                vm.setMemuse(arr[1]);
                vm.setNetworkkbsread(arr[2]);
                vm.setNetworkkbswrite(arr[3]);
                String date = DateFormatUtils.format(NumberUtils.toLong(arr[4]), DateConstants.DATE_TIME_FORMAT);
                vm.setTime(date);
                list.add(vm);
            }

        }

        return list;
    }
}
