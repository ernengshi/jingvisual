/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.dto.daas.DesktopDTO;
import cn.com.jingcloud.service.DaasTCService;
import cn.com.jingcloud.service.IaasVMService;
import cn.com.jingcloud.service.LocaleMessageSourceService;
import cn.com.jingcloud.service.ResourceService;
import cn.com.jingcloud.utils.Charsets;
import cn.com.jingcloud.utils.index.IndexFileItem;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.constant.DateConstants;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.constant.PicConstant;
import cn.com.jingcloud.utils.index.IndexConstants;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.daas.TCGroupUsedVO;
import cn.com.jingcloud.vo.daas.TCGroupVO;
import cn.com.jingcloud.vo.daas.TCVO;
import cn.com.jingcloud.vo.iaas.VmGroupVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 云一体机
 *
 * @author liyong
 */
@Service
public class DaasTCServiceImpl implements DaasTCService {

    private final static Logger LOG = LoggerFactory.getLogger(DaasTCServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ResourceService resourceService;

    @Autowired
    DaasTCService daasTCService;

    @Autowired
    IaasVMService iaasVMService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Value("${daas.client.token}")
    private String token;

    @Override
    public Pager<TCVO> pageDaasTC(int pageNo, int pageSize, String keyword) {

        Platform daas = resourceService.findFirstDaas();
        List<TCVO> list = new ArrayList<>();
        Pager<TCVO> pager = new Pager<>();
        if (null != daas) {
            Map<String, Object> params = new HashMap();
            params.put("ip", daas.getIp());
            params.put("port", daas.getPort());

            String url = "http://{ip}:{port}/server/api?command=listDesktopTC&response=json&page=" + pageNo + "&pagesize=" + pageSize + "&searchBy=hostname&token=" + token;
            if (!Utils.isNullOrEmpty(keyword)) {
                url = url + "&keyword=" + keyword;
            }
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
                JsonNode zone = response.path("listdesktoptcresponse");
                if (zone.isArray()) {
                    Iterator<JsonNode> iterator = zone.iterator();
                    while (iterator.hasNext()) {
                        TCVO vo = new TCVO();
                        JsonNode node = iterator.next();
                        String uuid = node.path("uuid").textValue();
                        String mac = node.path("mac").textValue();
                        String ip = node.path("ip").textValue();
                        String first_time = node.path("first_time").textValue();
                        String last_time = node.path("last_time").textValue();
                        String os = node.path("os").textValue();
                        String hostname = node.path("hostname").textValue();
                        String cpu_vendor = node.path("cpu_vendor").textValue();
                        int memory_size = node.path("memory_size").intValue();
                        int disk_size = node.path("disk_size").intValue();
                        String graphic_card_vendor = node.path("graphic_card_vendor").textValue();
                        String audio_card_vendor = node.path("audio_card_vendor").textValue();
                        String state = node.path("state").textValue();
                        vo.setId(uuid);
                        vo.setMac(mac);
                        vo.setIp(ip);
                        vo.setFirst_time(first_time);
                        vo.setLast_time(last_time);
                        vo.setOs(os);
                        vo.setHostname(hostname);
                        vo.setCpu_vendor(cpu_vendor);
                        vo.setMemory_size(memory_size + "");
                        vo.setDisk_size(disk_size + "");
                        vo.setGraphic_card_vendor(graphic_card_vendor);
                        vo.setAudio_card_vendor(audio_card_vendor);
                        vo.setState(state);
                        vo.setState_title(convertTCStatus(state));
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

    /**
     *
     * @param status
     * @return
     */
    private String convertTCStatus(String status) {
        String returnState = "";
        if (!Utils.isNullOrEmpty(status)) {
            switch (status) {
                case "online":
                    returnState = "label.desktop.tc.state.online";
                    break;
                case "offline":
                    returnState = "label.desktop.tc.state.offline";
                    break;
                case "starting":
                    returnState = "label.desktop.tc.state.starting";
                    break;
                case "shutdowning":
                    returnState = "label.desktop.tc.state.shutdowning";
                    break;
                case "rebooting":
                    returnState = "label.desktop.tc.state.rebooting";
                    break;
                case "setuping":
                    returnState = "label.desktop.tc.state.setuping";
                    break;
                default:
                    break;
            }
        }

        return localeMessageSourceService.getMessage(returnState);
    }

    /**
     * 桌面管理 使用桌面名称查询(虚拟机名称)
     *
     * @param pageNo
     * @param pageSize
     * @param keyword vmName
     * @return
     */
    @Override
    public Pager<DesktopDTO> listDesktops(int pageNo, int pageSize, String keyword) {
        Platform daas = resourceService.findFirstDaas();
        List<DesktopDTO> list = new ArrayList<>();
        Pager<DesktopDTO> pager = new Pager<>();
        if (null != daas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", daas.getIp());
            params.put("port", daas.getPort());

            String url = "http://{ip}:{port}/server/api?command=listDesktops&isUpdate=true&response=json&page=" + pageNo + "&pagesize=" + pageSize + "&searchBy=displayName&type=vm_instance&token=" + token;
            if (!Utils.isNullOrEmpty(keyword)) {
                url = url + "&keyword=" + keyword;
            }
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listDesktopsResponse");
                int totalsize = response.path("totalsize").intValue();
                int count = response.path("count").intValue();
                if (totalsize <= 0) {
                    totalsize = count;
                }
                pager.setPageNo(pageNo);
                pager.setPageSize(pageSize);
                pager.setTotal(totalsize);
                JsonNode zone = response.path("desktop");
                if (zone.isArray()) {
                    Iterator<JsonNode> iterator = zone.iterator();
                    while (iterator.hasNext()) {
                        DesktopDTO vo = new DesktopDTO();
                        JsonNode node = iterator.next();
                        String uuid = node.path("uuid").textValue();
                        String vmName = node.path("vmName").textValue();
                        String userName = node.path("userName").textValue();
                        vo.setUuid(uuid);
                        vo.setVmName(vmName);
                        vo.setUserName(userName);
                        list.add(vo);
                    }
                }
                pager.setResultList(list);
            } catch (IOException ex) {
                LOG.error("listDesktops " + ex.getMessage());
            }

        }
        return pager;
    }

    @Override
    public List<TCGroupVO> listTCTree(String groupid, int pageNo, int pageSize) {

        List<TCGroupVO> result = new ArrayList<>();

        Pager<VmGroupVO> vmPage = iaasVMService.pageVM(groupid, pageNo, pageSize);
        List<VmGroupVO> vms = vmPage.getResultList();

        for (VmGroupVO vm : vms) {
//            String globalPath = vm.getLeafNodeGlobalPath();
            String name = vm.getName();
            Pager<DesktopDTO> desktopPage = this.listDesktops(pageNo, pageSize, name);
            List<DesktopDTO> desktops = desktopPage.getResultList();
            if (!Utils.isNullOrEmpty(desktops)) {
                DesktopDTO desktop = desktops.get(0);
                String userName = desktop.getUserName();
                if (!Utils.isNullOrEmpty(userName)) {
                    String tcName = getTcNameByUserName(userName);
                    Pager<TCVO> tcPage = this.pageDaasTC(pageNo, pageSize, tcName);
                    List<TCVO> tcs = tcPage.getResultList();
                    if (!Utils.isNullOrEmpty(tcs)) {
                        TCVO tc = tcs.get(0);
                        if (null != tc) {
                            TCGroupVO group = new TCGroupVO();
//                            group.setLeafNodeGlobalPath(globalPath);
                            group.setId(tc.getId());
                            group.setName(tc.getHostname());
                            group.setIsParent(false);
                            group.setpId(groupid);
                            String state = tc.getState();
                            if (!Utils.isNullOrEmpty(state) && "online".equals(state)) {
                                //http://localhost:8080/image/block.jpg
                                group.setIcon(PicConstant.VM_STATE_PIC_PATH_RUNNING);
                            } else {
                                group.setIcon(PicConstant.VM_STATE_PIC_PATH_STOPPED);
                            }
//                            group.setClick(true);
                            result.add(group);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 分组分页查询tc详情
     *
     * @param groupid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<TCVO> listTCVO(String groupid, int pageNo, int pageSize) {

        List<TCVO> result = new ArrayList<>();

        Pager<VmGroupVO> vmPage = iaasVMService.pageVM(groupid, pageNo, pageSize);
        List<VmGroupVO> vms = vmPage.getResultList();

        for (VmGroupVO vm : vms) {
            String name = vm.getName();
            Pager<DesktopDTO> desktopPage = this.listDesktops(pageNo, pageSize, name);
            List<DesktopDTO> desktops = desktopPage.getResultList();
            if (!Utils.isNullOrEmpty(desktops)) {
                DesktopDTO desktop = desktops.get(0);
                String userName = desktop.getUserName();
                if (!Utils.isNullOrEmpty(userName)) {
                    String tcName = getTcNameByUserName(userName);
                    Pager<TCVO> tcPage = this.pageDaasTC(pageNo, pageSize, tcName);
                    List<TCVO> tcs = tcPage.getResultList();
                    if (!Utils.isNullOrEmpty(tcs)) {
                        TCVO tc = tcs.get(0);
                        result.add(tc);
                    }
                }
            }
        }
        return result;
    }

    /**
     * TC名默认的组合方式是 userName_
     *
     * @param userName
     * @return
     */
    private String getTcNameByUserName(String userName) {
        String result = "";
        if (!Utils.isNullOrEmpty(userName)) {
            result = userName + "_OS";
        }
        return result;
    }

    /**
     * 分组查询VM对应的TC
     *
     * @param groupid
     * @param pageNo
     * @param pageSize
     * @return
     */
    private List<TCVO> listGroupTC(String groupid, int pageNo, int pageSize) {

        List<TCVO> result = new ArrayList<>();
        if (!Utils.isNullOrEmpty(groupid)) {
            Pager<VmGroupVO> vmPage = iaasVMService.pageVM(groupid, pageNo, pageSize);
            List<VmGroupVO> vms = vmPage.getResultList();

            for (VmGroupVO vm : vms) {
                String name = vm.getName();
                Pager<DesktopDTO> desktopPage = this.listDesktops(pageNo, pageSize, name);
                List<DesktopDTO> desktops = desktopPage.getResultList();
                if (!Utils.isNullOrEmpty(desktops)) {
                    DesktopDTO desktop = desktops.get(0);
                    String userName = desktop.getUserName();
                    if (!Utils.isNullOrEmpty(userName)) {
                        String tcName = getTcNameByUserName(userName);
                        Pager<TCVO> tcPage = this.pageDaasTC(pageNo, pageSize, tcName);
                        result = tcPage.getResultList();
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     *
     * @param groupId
     */
    @Override
    public void saveTCGroupUsed(String groupId) {
        List<TCVO> tcs = listGroupTC(groupId, 1, 500);
        int total = tcs.size();
        int onlineSize = 0;
        for (TCVO tc : tcs) {
            if ("online".equals(tc.getState())) {
                onlineSize++;
            }
        }

        String fileName = initTCGroupIndexFileName(groupId);
        //写数据的时候进行同步或者异步操作
//        this.writeTCIndex(fileName, onlineSize, total);
        this.synchronizedWriteTCIndex(fileName, onlineSize, total);
    }

    /**
     * 不同步，可能会出现正在写的时候读文件<br/>
     * 确保
     * <1>同一天只保存一条记录<br/>
     * <2>同一天内只保存最高在线数量<br/>
     *
     * <3>每条数据保存一个arr<br/>
     * arr[0] online<br/>
     * arr[1] total <br/>
     * arr[2] time<br/>
     *
     * @param fileName
     * @param content
     */
    private void writeTCIndex(String fileName, int onlineSize, int total) {
        Date dateNow = new Date();
        FileObject fileObject = this.getFileLineNumberAndLastLine(fileName);

        int count = fileObject.getFileLineNum();

        String lastLineStr = fileObject.getLastLine();

        String newVal = new StringBuilder().
                append(onlineSize).append(StringUtils.SPACE).
                append(total).append(StringUtils.SPACE).
                append(dateNow.getTime()).toString();

        /**
         *
         * 当 >= 第2次进来，<br/>
         * 判断<br/>
         * 1>是否比上次开机比率高 2>是否同一天内
         */
        if (count > 0) {
            String[] arr = StringUtils.split(lastLineStr, StringUtils.SPACE);

            if (arr.length == 3) {
                Date lastDate = new Date(NumberUtils.toLong(arr[2]));
                int lastOnlineSize = NumberUtils.toInt(arr[0]);
                boolean sameDay = DateUtils.isSameDay(dateNow, lastDate);
                //同一天只保存一条
                if (sameDay) {
                    if (onlineSize <= lastOnlineSize) {
                        return;
                    } else if (onlineSize > lastOnlineSize) {
                        replaceFileEndLine(fileName, newVal);
                        return;
//replace end line
                    }
                }

            }
        }
//resourceService
        if (count >= JingVisualConstant.YEAR_SIZE) {
            resourceService.deleteFirstLineAddEnd(fileName, newVal);
        } else {
            resourceService.writeFileAppend(fileName, newVal);
        }
    }

    /**
     * 多线程同步，正在写的时候给文件加锁此时刻不能读
     *
     * @param fileName
     * @param onlineSize
     * @param total
     */
    private void synchronizedWriteTCIndex(String fileName, int onlineSize, int total) {
        IndexFileItem lfi = IndexConstants.IndexFileMap.get(fileName);
        if (lfi == null) {
            IndexFileItem newlfi = new IndexFileItem();
            lfi = IndexConstants.IndexFileMap.putIfAbsent(fileName, newlfi);
            if (lfi == null) {
                lfi = newlfi;
            }
        }

        synchronized (lfi) {
            lfi.setCurrentAction(IndexConstants.WRITE);

            this.writeTCIndex(fileName, onlineSize, total);

            lfi.setCurrentAction(IndexConstants.READ);
        }

    }

    private String initTCGroupIndexFileName(String groupId) {

        String path = JingVisualConstant.TC_FILE_PATH + groupId;
        return path + JingVisualConstant.FILE_PATH_SUFFIX;
    }

    /**
     * 替换文件最后一行
     *
     * @param path
     * @param add
     */
    private void replaceFileEndLine(String path, String newVal) {

        try {
            File file = new File(path);
            List<String> list = FileUtils.readLines(file, Charsets.UTF_8);
            if (!Utils.isNullOrEmpty(list)) {
                String last = list.get(list.size() - 1);
                Collections.replaceAll(list, last, newVal);
                FileUtils.writeLines(file, Charsets.UTF_8_NAME, list, false);
            }
        } catch (IOException ex) {
            LOG.error("replaceFileEndLine: " + ex.getMessage());
        }
    }

    /**
     *
     * 1>查询文件最后一行<br/>
     * 2>查询文件总行数
     *
     * @param fileName
     * @return
     */
    private FileObject getFileLineNumberAndLastLine(String fileName) {
        FileObject fileObject = new FileObject();
        long start = System.currentTimeMillis();
        int lineNum = 0;
        LineNumberReader lnr = null;
        String str = null;
        String lastLine = "";
        try {
            lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(fileName), Charsets.UTF_8));
            while ((str = lnr.readLine()) != null) {
                lastLine = str;
            }

            lineNum = lnr.getLineNumber();
            fileObject.setFileLineNum(lineNum);
            fileObject.setLastLine(lastLine);
        } catch (FileNotFoundException e) {
            LOG.error("getFileLineNumberAndLastLine fileName is " + fileName, e.getMessage());
        } catch (IOException e) {
            LOG.error("getFileLineNumberAndLastLine fileName is " + fileName, e.getMessage());
        } finally {
            try {
                if (null != lnr) {
                    lnr.close();
                }
            } catch (IOException ex) {
                LOG.error("getFileLineNumber close  error: " + fileName, ex.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("getFileLineNumber " + fileName + " Use Time: " + (end - start) + " Line Num: " + lineNum);
        }

        return fileObject;
    }

    /**
     * 封装读取文件的一些信息
     */
    private static final class FileObject {

        int fileLineNum;
        String lastLine;

        public int getFileLineNum() {
            return fileLineNum;
        }

        public void setFileLineNum(int fileLineNum) {
            this.fileLineNum = fileLineNum;
        }

        public String getLastLine() {
            return lastLine;
        }

        public void setLastLine(String lastLine) {
            this.lastLine = lastLine;
        }

    }

    @Override
    public List<TCGroupUsedVO> getWeekInformation(String groupId) {
        return getTCGroupLimit(groupId, JingVisualConstant.WEEK_SIZE);
    }

    @Override
    public List<TCGroupUsedVO> getMonthInformation(String groupId) {
        return getTCGroupLimit(groupId, JingVisualConstant.MONTH_SIZE);
    }

    @Override
    public List<TCGroupUsedVO> getSeasonInformation(String groupId) {
        return getTCGroupLimit(groupId, JingVisualConstant.SEASON_SIZE);
    }

    @Override
    public List<TCGroupUsedVO> getYearInformation(String groupId) {
        return getTCGroupLimit(groupId, JingVisualConstant.YEAR_SIZE);
    }

    private List<TCGroupUsedVO> getTCGroupLimit(String groupId, int limit) {

        String fileName = initTCGroupIndexFileName(groupId);
        List<TCGroupUsedVO> list = readFile(fileName, limit);

        return list;
    }

    /**
     * 读取索引文件
     *
     * @param path
     *
     */
    /*
    private List<TCGroupUsedVO> readFileFromHead(String fileName, int limit) {
        long start = System.currentTimeMillis();
        LineNumberReader lnr = null;
        String str = null;
        List<TCGroupUsedVO> list = new ArrayList<>();
        try {
            lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(fileName), Charsets.UTF_8));
            while ((str = lnr.readLine()) != null) {
                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 3) {
                    TCGroupUsedVO tc = new TCGroupUsedVO();
                    long online = NumberUtils.toLong(arr[0]);
                    long total = NumberUtils.toLong(arr[1]);
                    tc.setOnline(online);
                    tc.setTotal(total);
                    String used = cn.com.jingcloud.utils.NumberUtil.divideRateStr(online, total);
                    tc.setUsed(used);

                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[2]), DateConstants.DATE_TIME_FORMAT);
                    tc.setTime(date);
                    list.add(tc);
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
            LOG.debug("read tc group index Use Time: " + (end - start));
        }

//        Collections.reverse(list);
        return list;
    }


    private List<TCGroupUsedVO> readFileFromTail(String fileName, int limit) {
        long start = System.currentTimeMillis();
        ReversedLinesFileReader rdf = null;
        AtomicInteger flag = new AtomicInteger(0);
        String str = null;
        List<TCGroupUsedVO> list = new ArrayList<>();

        try {

            rdf = new ReversedLinesFileReader(new File(fileName), 32, Charsets.UTF_8);

            while ((str = rdf.readLine()) != null) {
                flag.incrementAndGet();

                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 3) {
                    TCGroupUsedVO tc = new TCGroupUsedVO();
                    long online = NumberUtils.toLong(arr[0]);
                    long total = NumberUtils.toLong(arr[1]);
                    tc.setOnline(online);
                    tc.setTotal(total);
                    String used = cn.com.jingcloud.utils.NumberUtil.divideRateStr(online, total);
                    tc.setUsed(used);

                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[2]), DateConstants.DATE_TIME_FORMAT);
                    tc.setTime(date);
                    list.add(tc);
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
            LOG.debug("read tc group index Use Time: " + (end - start) + " Line Num: "
                    + flag.get());
        }

        Collections.reverse(list);
        return list;
    }*/
    private List<TCGroupUsedVO> readFile(String fileName, int limit) {

        List<TCGroupUsedVO> list = new ArrayList<>();
//        List<String> lines = resourceService.readFileFromTail(fileName, 32, limit);
        List<String> lines = resourceService.synchronizedReadFileFromTail(fileName, 32, limit);
        for (String str : lines) {
            String[] arr = StringUtils.split(str, StringUtils.SPACE);
            if (null != arr && arr.length == 3) {
                TCGroupUsedVO tc = new TCGroupUsedVO();
                long online = NumberUtils.toLong(arr[0]);
                long total = NumberUtils.toLong(arr[1]);
                tc.setOnline(online);
                tc.setTotal(total);
                String used = cn.com.jingcloud.utils.NumberUtil.divideRateStr(online, total);
                tc.setUsed(used);

                String date = DateFormatUtils.format(NumberUtils.toLong(arr[2]), DateConstants.DATE_TIME_FORMAT);
                tc.setTime(date);
                list.add(tc);
            }
        }
        return list;
    }
}
