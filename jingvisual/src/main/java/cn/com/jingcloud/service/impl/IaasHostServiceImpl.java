/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.IaasHostService;
import cn.com.jingcloud.service.LocaleMessageSourceService;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.HostMemCpuVO;
import cn.com.jingcloud.vo.iaas.HostStatsVO;
import cn.com.jingcloud.vo.iaas.HostVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import cn.com.jingcloud.service.ResourceService;
import cn.com.jingcloud.utils.Charsets;
import cn.com.jingcloud.utils.constant.DateConstants;
import java.io.File;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 *
 * @author liyong
 */
@Service
public class IaasHostServiceImpl implements IaasHostService {

    private final static Logger LOG = LoggerFactory.getLogger(IaasHostServiceImpl.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ResourceService resourceService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    /**
     * 主机信息分页
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Pager<HostVO> pageHost(int pageNo, int pageSize) {

        Platform iaas = resourceService.findFirstIaas();
        List<HostVO> list = new ArrayList<>();
        Pager<HostVO> pager = new Pager<>();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());
//            http://192.168.23.250:8096/client/api?command=listHosts&response=json&page=1&pageSize=15&type=routing&listAll=true
            String url = "http://{ip}:{port}/client/api?command=listHosts&response=json&page=" + pageNo + "&pageSize=" + pageSize + "&type=routing&listAll=true";
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listhostsresponse");
                int totalsize = NumberUtils.toInt(response.path("totalsize").textValue());
                int currentpage = NumberUtils.toInt(response.path("currentpage").textValue());
                pager.setPageNo(currentpage);
                pager.setPageSize(pageSize);
                pager.setTotal(totalsize);
                JsonNode hosts = response.path("host");
                if (hosts.isArray()) {
                    Iterator<JsonNode> iterator = hosts.iterator();
                    while (iterator.hasNext()) {
                        HostVO vo = new HostVO();
                        JsonNode node = iterator.next();
                        String id = node.path("id").textValue();
                        String name = node.path("name").textValue();

                        String ipaddress = node.path("ipaddress").textValue();
                        vo.setIp(ipaddress);
                        String cpunumber = node.path("cpunumber").asText();
                        vo.setCpunumber(cpunumber);
                        long cpuspeed = node.path("cpuspeed").longValue();
                        vo.setCpuspeed(cpuspeed + "");
//                        listHosts命令查出来的cpuused和getHostStats命令查出来的cpuusage有出入，这里选择getHostStats
//                        因为后者查询出来的结果和监控信息里边的查询数据一致
//                        String cpuused = node.path("cpuused").textValue();
//                        vo.setCpuused(cpuused);
                        String cpuused = "";
                        HostStatsVO hostStatsVO = getHostStats(id);
                        cpuused = hostStatsVO.getCpuusage();
                        vo.setCpuused(cpuused);

                        vo.setCpu(cpunumber + " x " + this.convertHz(cpuspeed) + "(" + cpuused + "%)");

                        long memorytotal = node.path("memorytotal").asLong();
                        long memoryused = node.path("memoryused").asLong();
                        long memoryallocated = node.path("memoryallocated").asLong();

                        vo.setMemorytotal(memorytotal + "");
                        vo.setMemoryused(memoryused + "");
                        vo.setMemoryallocated(memoryallocated + "");
                        String m_used = cn.com.jingcloud.utils.NumberUtil.divideRateStr(memoryused, memorytotal);
                        vo.setMemory(Utils.toReadableSize(memorytotal) + StringUtils.SPACE + "(" + m_used + "%)");

                        long networkkbsread = node.path("networkkbsread").longValue();
                        vo.setNetworkread(networkkbsread + "");
                        long networkkbswrite = node.path("networkkbswrite").longValue();
                        vo.setNetworkwrite(networkkbswrite + "");

                        vo.setNetwork(Utils.toReadableSize(networkkbsread) + " (r) " + Utils.toReadableSize(networkkbswrite) + " (w) ");

                        String resourcestate = node.path("resourcestate").textValue();
                        vo.setResourcestateTitle(convertResourceState(resourcestate));

                        String state = node.path("state").textValue();
                        vo.setStateTitle(convertHostState(state));

                        vo.setId(id);
                        vo.setName(name);
                        vo.setResourcestate(resourcestate);
                        vo.setState(state);
                        list.add(vo);
                    }
                }
                pager.setResultList(list);
            } catch (IOException ex) {
                LOG.error("listHost " + ex.getMessage());
            }

        }
        return pager;
    }

    private String convertHostState(String state) {
        String returnState = "";
        if (!Utils.isNullOrEmpty(state)) {
            switch (state) {
                case "Up":
                    returnState = "label.launch";
                    break;
                case "Down":
                    returnState = "label.status.down";
                    break;
                case "Disconnected":
                    returnState = "state.Disconnected";
                    break;
                case "Alert":
                    returnState = "label.warn";
                    break;
                case "Error":
                    returnState = "state.Error";
                    break;
                case "Creating":
                    returnState = "state.Creating";
                    break;
                case "Connecting":
                    returnState = "state.Connecting";
                    break;
                case "Removed":
                    returnState = "label.delete";
                    break;
                case "Rebalancing":
                    returnState = "label.status.Rebalancing";
                    break;
                default:
                    break;
            }
        }

        return localeMessageSourceService.getMessage(returnState);
    }

    private String convertResourceState(String resourcestate) {
        String returnState = "";
        if (!Utils.isNullOrEmpty(resourcestate)) {
            switch (resourcestate) {
                case "Enabled":
                    returnState = "label.available";
                    break;
                case "Disabled":
                    returnState = "label.disable";
                    break;
                case "Destroyed":
                    returnState = "state.Destroyed";
                    break;
                case "PrepareForMaintenance":
                    returnState = "state.PrepareForMaintenance";
                    break;
                case "Maintenance":
                    returnState = "state.Maintenance";
                    break;
                case "ErrorInMaintenance":
                    returnState = "state.ErrorInMaintenance";
                    break;
                default:
                    break;
            }
        }

        return localeMessageSourceService.getMessage(returnState);
    }

    /**
     * cloudstack sharedFunction.js 1218行中改写过来的
     *
     * @param hz cpuspeed
     * @return
     */
    private String convertHz(long hz) {
        if (hz <= 0) {
            return "";
        }

        if (hz < 1000) {
            return hz + " MHz";
        } else {
            return cn.com.jingcloud.utils.NumberUtil.divide(hz, 1000) + " GHz";
        }
    }

    private static final String[] HOST_RESOURCE_GRAPH_STATS_NAMES = {"ramfree", "rambyvm", "rambyplat", "cpuusage", "available", "time"};

    @Async
    @Override
    public void saveHostResourceGraphStats(String hostId) {
//        long time = iaasResourceService.getDateIgnoreMinuteAndSECOND();
//        Platform iaas = iaasResourceService.findFirstIaas();
//        if (null != iaas) {
//
//            Map<String, Object> params = new HashMap();
//            params.put("ip", iaas.getIp());
//            params.put("port", iaas.getPort());
//
//            String url = "http://{ip}:{port}/client/api?command=listHostResourceGraphStats&timespan=HOUR&size=100&response=json&fetchlatest=true&hostid=" + hostId;
//            String result = restTemplate.getForObject(url, String.class, params);
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root;
//            try {
//                root = mapper.readTree(result);
//                JsonNode response = root.path("listhostresourcestatsresponse");
//                JsonNode hostState = response.path("hostresourcestats");
//                if (hostState.isArray()) {
//
//                    Iterator<JsonNode> iterator = hostState.iterator();
//                    while (iterator.hasNext()) {
//                        StringBuilder sb = new StringBuilder();
//                        JsonNode node = iterator.next();
//
//                        long ramfree = node.path(NAMES[0]).longValue();//空闲内存
//                        long rambyvm = node.path(NAMES[1]).longValue();//虚拟机使用内存
////                        long rambysystem = node.path("rambysystem").longValue();
//                        long rambyplat = node.path(NAMES[2]).longValue();//白金加速占用内存
//                        String cpuusage = node.path(NAMES[3]).asText();
//                        boolean available = node.path(NAMES[4]).booleanValue();
//                        sb.append(ramfree).append(StringUtils.SPACE).
//                                append(rambyvm).append(StringUtils.SPACE).
//                                append(rambyplat).append(StringUtils.SPACE).
//                                append(cpuusage).append(StringUtils.SPACE).
//                                append(available).append(StringUtils.SPACE).
//                                append(time);
//                        String fileName = initHostIndexFileName(hostId);
//                        int count = iaasResourceService.getFileLineNumber(fileName);
//                        if (count >= DashboardConstant.YEAR_SIZE) {
//                            iaasResourceService.deleteFirstLineAddEnd(fileName, sb.toString());
//                        } else {
//                            iaasResourceService.writeFile(fileName, sb.toString());
//                        }
//                    }
//
//                }
//            } catch (IOException ex) {
//                LOG.error("saveHostResourceGraphStats " + ex.getMessage());
//            }
//
//        }

        List<HostMemCpuVO> list = this.getHostResourceGraphStats(hostId);
        for (HostMemCpuVO vo : list) {
            StringBuilder sb = new StringBuilder();

            String ramfree = vo.getRamfree();//空闲内存
            String rambyvm = vo.getRambyvm();//虚拟机使用内存
//                        long rambysystem = node.path("rambysystem").longValue();
            String rambyplat = vo.getRambyplat();//白金加速占用内存
            String cpuusage = vo.getCpuusage();
            String available = vo.getAvailable();
            String time = vo.getTime();

            sb.append(ramfree).append(StringUtils.SPACE).
                    append(rambyvm).append(StringUtils.SPACE).
                    append(rambyplat).append(StringUtils.SPACE).
                    append(cpuusage).append(StringUtils.SPACE).
                    append(available).append(StringUtils.SPACE).
                    append(time);
            String fileName = initHostIndexFileName(hostId);
            //resourceService
            resourceService.synchronizedWriteFile(fileName, sb.toString());
        }

    }

    private List<HostMemCpuVO> getHostResourceGraphStats(String hostId) {
        long time = resourceService.getDateIgnoreMinuteAndSECOND();
        Platform iaas = resourceService.findFirstIaas();
        List<HostMemCpuVO> list = new ArrayList<>();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());

            String url = "http://{ip}:{port}/client/api?command=listHostResourceGraphStats&timespan=HOUR&size=100&response=json&fetchlatest=true&hostid=" + hostId;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listhostresourcestatsresponse");
                JsonNode hostState = response.path("hostresourcestats");
                if (hostState.isArray()) {

                    Iterator<JsonNode> iterator = hostState.iterator();
                    while (iterator.hasNext()) {
                        HostMemCpuVO vo = new HostMemCpuVO();
                        JsonNode node = iterator.next();

                        long ramfree = node.path(HOST_RESOURCE_GRAPH_STATS_NAMES[0]).longValue();//空闲内存
                        long rambyvm = node.path(HOST_RESOURCE_GRAPH_STATS_NAMES[1]).longValue();//虚拟机使用内存
//                        long rambysystem = node.path("rambysystem").longValue();
                        long rambyplat = node.path(HOST_RESOURCE_GRAPH_STATS_NAMES[2]).longValue();//白金加速占用内存
                        String cpuusage = node.path(HOST_RESOURCE_GRAPH_STATS_NAMES[3]).asText();
                        boolean available = node.path(HOST_RESOURCE_GRAPH_STATS_NAMES[4]).booleanValue();

                        vo.setRamfree(ramfree + "");
                        vo.setRambyvm(rambyvm + "");
                        vo.setRambyplat(rambyplat + "");
                        vo.setCpuusage(cpuusage);
                        vo.setAvailable(available + "");
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

//    private static final String[] HOST_STATS_NAMES = {
//        "ramallocated", "ramavailable", "rambysystem", "rambyplat", "ramtotal", "rambyvm", "available",
//        "cpuusage", "networkkbsread", "networkkbswrite", "cpunumber", "cpuspeed", "date"};
    private HostStatsVO getHostStats(String hostId) {
//        long time = iaasResourceService.getDateIgnoreMinuteAndSECOND();
        Platform iaas = resourceService.findFirstIaas();
        HostStatsVO vo = new HostStatsVO();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());

            String url = "http://{ip}:{port}/client/api?command=getHostStats&response=json&hostid=" + hostId;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("gethoststatsresponse");
                JsonNode node = response.path("hoststats");
                if (node.isObject()) {

                    String ramallocated = node.path("ramallocated").asText();
                    String ramavailable = node.path("ramavailable").asText();
                    String rambysystem = node.path("rambysystem").asText();
                    String rambyplat = node.path("rambyplat").asText();
                    String ramtotal = node.path("ramtotal").asText();

                    String rambyvm = node.path("rambyvm").asText();
                    String available = node.path("available").asText();
                    String cpuusage = node.path("cpuusage").asText();
                    String networkkbsread = node.path("networkkbsread").asText();
                    String networkkbswrite = node.path("networkkbswrite").asText();

                    String cpunumber = node.path("cpunumber").asText();
                    String cpuspeed = node.path("cpuspeed").asText();

                    vo.setRamallocated(ramallocated);
                    vo.setRamavailable(ramavailable);
                    vo.setRambysystem(rambysystem);
                    vo.setRambyplat(rambyplat);
                    vo.setRamtotal(ramtotal);

                    vo.setRambyvm(rambyvm);
                    vo.setAvailable(available);
                    vo.setCpuusage(cpuusage);
                    vo.setNetworkkbsread(networkkbsread);
                    vo.setNetworkkbswrite(networkkbswrite);

                    vo.setCpunumber(cpunumber);
                    vo.setCpuspeed(cpuspeed);
//                    vo.setDate(time + "");

                }
            } catch (IOException ex) {
                LOG.error("getHostStats " + ex.getMessage());
            }

        }
        return vo;
    }

    @Override
    public Map<String, List<String>> getWeekInformation(String hostId) {

        return getHostLimit(hostId, JingVisualConstant.WEEK_SIZE);
    }

    @Override
    public Map<String, List<String>> getMonthInformation(String hostId) {

        return getHostLimit(hostId, JingVisualConstant.MONTH_SIZE);
    }

    @Override
    public Map<String, List<String>> getSeasonInformation(String hostId) {

        return getHostLimit(hostId, JingVisualConstant.SEASON_SIZE);
    }

    @Override
    public Map<String, List<String>> getYearInformation(String hostId) {
        return getHostLimit(hostId, JingVisualConstant.YEAR_SIZE);
    }

    private List<HostMemCpuVO> getHosts(String hostId, int limit) {

        String fileName = initHostIndexFileName(hostId);
        List<HostMemCpuVO> list = readFile(fileName, limit);

        return list;
    }

    private Map<String, List<String>> getHostLimit(String hostId, int limit) {

        Map<String, List<String>> map = new HashMap<>();
        List<String> ramfrees = new ArrayList<>();
        List<String> rambyvms = new ArrayList<>();
        List<String> rambyplats = new ArrayList<>();
        List<String> cpuusages = new ArrayList<>();
        List<String> availables = new ArrayList<>();
        List<String> times = new ArrayList<>();

        List<HostMemCpuVO> list = getHosts(hostId, limit);
        for (HostMemCpuVO host : list) {
            ramfrees.add(host.getRamfree());
            rambyvms.add(host.getRambyvm());
            rambyplats.add(host.getRambyplat());
            cpuusages.add(host.getCpuusage());
            availables.add(host.getAvailable());
            times.add(host.getTime());
        }

        map.put(HOST_RESOURCE_GRAPH_STATS_NAMES[0], ramfrees);
        map.put(HOST_RESOURCE_GRAPH_STATS_NAMES[1], rambyvms);
        map.put(HOST_RESOURCE_GRAPH_STATS_NAMES[2], rambyplats);
        map.put(HOST_RESOURCE_GRAPH_STATS_NAMES[3], cpuusages);
        map.put(HOST_RESOURCE_GRAPH_STATS_NAMES[4], availables);
        map.put(HOST_RESOURCE_GRAPH_STATS_NAMES[5], times);
        return map;
    }

    private String initHostIndexFileName(String hostId) {

        String path = JingVisualConstant.HOST_FILE_PATH + hostId;
        return path + JingVisualConstant.FILE_PATH_SUFFIX;
    }

    /**
     * 读取索引文件
     *
     * @param path
     *
     */
    /*
    private List<HostMemCpuVO> readFileFromHead(String fileName, int limit) {
        long start = System.currentTimeMillis();
        LineNumberReader lnr = null;
        String str = null;
        List<HostMemCpuVO> list = new ArrayList<>();
        try {
            lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(fileName), Charsets.UTF_8));
            while ((str = lnr.readLine()) != null) {
                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 6) {
                    HostMemCpuVO db = new HostMemCpuVO();
                    db.setRamfree(arr[0]);
                    db.setRambyvm(arr[1]);
                    db.setRambyplat(arr[2]);
                    db.setCpuusage(arr[3]);
                    db.setAvailable(arr[4]);
                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[5]), DateConstants.DATE_TIME_FORMAT);
                    db.setTime(date);
                    list.add(db);
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
            LOG.debug("read host index Use Time: " + (end - start));
        }

//        Collections.reverse(list);
        return list;
    }

    private List<HostMemCpuVO> readFileFromTail(String fileName, int limit) {
        long start = System.currentTimeMillis();
        int lineNum = 0;
        ReversedLinesFileReader rdf = null;
        AtomicInteger flag = new AtomicInteger(0);
        String str = null;
        List<HostMemCpuVO> list = new ArrayList<>();
        try {
            rdf = new ReversedLinesFileReader(new File(fileName), 64, Charsets.UTF_8);
            while ((str = rdf.readLine()) != null) {
                flag.incrementAndGet();
                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 6) {
                    HostMemCpuVO db = new HostMemCpuVO();
                    db.setRamfree(arr[0]);
                    db.setRambyvm(arr[1]);
                    db.setRambyplat(arr[2]);
                    db.setCpuusage(arr[3]);
                    db.setAvailable(arr[4]);
                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[5]), DateConstants.DATE_TIME_FORMAT);
                    db.setTime(date);
                    list.add(db);
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
            LOG.debug("read host index Use Time: " + (end - start) + " Line Num: "
                    + flag.get());
        }

        Collections.reverse(list);
        return list;
    }*/
    private List<HostMemCpuVO> readFile(String fileName, int limit) {

        List<HostMemCpuVO> list = new ArrayList<>();
//        List<String> lines = resourceService.readFileFromTail(fileName, 64, limit);
        List<String> lines = resourceService.synchronizedReadFileFromTail(fileName, 64, limit);
        //synchronizedReadFileFromTail
        for (String str : lines) {
            String[] arr = StringUtils.split(str, StringUtils.SPACE);
            if (null != arr && arr.length == 6) {
                HostMemCpuVO db = new HostMemCpuVO();
                db.setRamfree(arr[0]);
                db.setRambyvm(arr[1]);
                db.setRambyplat(arr[2]);
                db.setCpuusage(arr[3]);
                db.setAvailable(arr[4]);
                String date = DateFormatUtils.format(NumberUtils.toLong(arr[5]), DateConstants.DATE_TIME_FORMAT);
                db.setTime(date);
                list.add(db);
            }
        }
        return list;
    }
}
