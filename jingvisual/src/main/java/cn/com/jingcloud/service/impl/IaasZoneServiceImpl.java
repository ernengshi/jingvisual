/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.domain.entity.generic.Platform;
import cn.com.jingcloud.service.IaasZoneService;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.iaas.DashboardVO;
import cn.com.jingcloud.vo.iaas.ZoneVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import cn.com.jingcloud.vo.iaas.OnDuty;
import java.io.File;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author liyong
 */
@Service
public class IaasZoneServiceImpl implements IaasZoneService {

    private final static Logger LOG = LoggerFactory.getLogger(IaasZoneServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ResourceService resourceService;

    private List<OnDuty> getOndutyFromConf() {

        List<OnDuty> ondutys = new ArrayList<>();
        try {
            List<String> lines = FileUtils.readLines(new File(JingVisualConstant.JINGVISUAL_ON_DUTY_CONF_PATH), Charsets.UTF_8);
            for (String str : lines) {
                if (str.indexOf("姓名") < 0) {

                    String[] arr = str.split("\\p{javaWhitespace}+");
                    if (arr.length == 3) {
                        OnDuty duty = new OnDuty();
                        duty.setName(arr[0]);
                        duty.setPhone(arr[1]);
                        duty.setEmail(arr[2]);
                        ondutys.add(duty);
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error("read onduty file failed " + ex.getMessage());
        }
        return ondutys;
    }

    /**
     *
     * @param list
     * @param pageNo
     * @param pageSize
     * @return
     */
    private List limit(List list, int pageNo, int pageSize) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        int totalCount = list.size();
        int totalPages = (totalCount - 1) / pageSize + 1;
        if (pageNo > totalPages) {
            pageNo = totalPages;
        }
        int beginSize = (pageNo - 1) * pageSize;

        if (pageNo != totalPages) {
            pageSize = pageNo * pageSize;
        } else {
            pageSize = totalCount;
        }
        List result = new ArrayList();
        for (int i = (beginSize + 1); i <= pageSize; i++) {
            result.add(list.get(i - 1));
        }
        return result;
    }

    /**
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Pager<OnDuty> getOnDutys(int pageNo, int pageSize) {
        List<OnDuty> ondutys = getOndutyFromConf();
        Pager<OnDuty> pager = new Pager<>();
        pager.setPageNo(pageNo);
        pager.setPageSize(pageSize);
        pager.setTotal(ondutys.size());
        pager.setResultList(limit(ondutys, pageNo, pageSize));

        return pager;
    }

    /**
     * 获取数据中心id
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Pager<ZoneVO> pageZone(int pageNo, int pageSize) {

        Platform iaas = resourceService.findFirstIaas();
        List<ZoneVO> list = new ArrayList<>();
        Pager<ZoneVO> pager = new Pager<>();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());
            String url = "http://{ip}:{port}/client/api?command=listZones&response=json&page=" + pageNo + "&pagesize=" + pageSize;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;
            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listzonesresponse");
                int totalsize = NumberUtils.toInt(response.path("totalsize").textValue());
                int count = response.path("count").intValue();
                if (totalsize <= 0) {
                    totalsize = count;
                }
                pager.setPageNo(pageNo);
                pager.setPageSize(pageSize);
                pager.setTotal(totalsize);
                JsonNode zone = response.path("zone");
                if (zone.isArray()) {
                    Iterator<JsonNode> iterator = zone.iterator();
                    while (iterator.hasNext()) {
                        JsonNode node = iterator.next();
                        String id = node.path("id").textValue();
                        String name = node.path("name").textValue();
                        ZoneVO vo = new ZoneVO();
                        vo.setId(id);
                        vo.setName(name);
                        list.add(vo);
                    }
                    pager.setResultList(list);
                }
            } catch (IOException ex) {
                LOG.error("listZoneIds " + ex.getMessage());
            }

        }
        return pager;
    }

    @Async
    @Override
    public void saveZoneCapacity(String zoneId) {
        long time = resourceService.getDateIgnoreMinuteAndSECOND();
        Platform iaas = resourceService.findFirstIaas();
        if (null != iaas) {

            Map<String, Object> params = new HashMap();
            params.put("ip", iaas.getIp());
            params.put("port", iaas.getPort());

            String url = "http://{ip}:{port}/client/api?command=listCapacity&response=json&zoneid=" + zoneId;
            String result = restTemplate.getForObject(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;
            try {
                root = mapper.readTree(result);
                JsonNode response = root.path("listcapacityresponse");
                JsonNode capacity = response.path("capacity");
                if (capacity.isArray()) {

                    Iterator<JsonNode> iterator = capacity.iterator();
                    while (iterator.hasNext()) {
                        StringBuilder sb = new StringBuilder();
                        JsonNode node = iterator.next();
                        int type = node.path("type").intValue();
                        if (type == 3 || type == 10 || type == 9) {
                            continue;
                        }

//                        String zoneName = node.path("zonename").textValue();
                        String id = node.path("zoneid").textValue();
                        long capacityTotal = node.path("capacitytotal").longValue();
                        long capacityUsed = node.path("capacityused").longValue();
                        String percentUsed = node.path("percentused").textValue();
                        //String time = DateFormatUtils.format(Calendar.getInstance().getTime(), DateConstants.DATE_TIME_FORMAT);

                        sb.append(percentUsed).append(StringUtils.SPACE).
                                append(capacityUsed).append(StringUtils.SPACE).
                                append(capacityTotal).append(StringUtils.SPACE).
                                append(time);
                        String fileName = initZoneIndexFileName(type, id);
                        //resourceService
                        resourceService.synchronizedWriteFile(fileName, sb.toString());
                    }

                }
            } catch (IOException ex) {
                LOG.error("listZoneIds " + ex.getMessage());
            }

        }
    }

    @Override
    public List<DashboardVO> getWeekInformation(String zoneId, int type) {
        String fileName = initZoneIndexFileName(type, zoneId);
        return readFile(fileName, JingVisualConstant.WEEK_SIZE);
    }

    @Override
    public List<DashboardVO> getMonthInformation(String zoneId, int type) {
        String fileName = initZoneIndexFileName(type, zoneId);
        return readFile(fileName, JingVisualConstant.MONTH_SIZE);
    }

    @Override
    public List<DashboardVO> getSeasonInformation(String zoneId, int type) {
        String fileName = initZoneIndexFileName(type, zoneId);
        return readFile(fileName, JingVisualConstant.SEASON_SIZE);
    }

    @Override
    public List<DashboardVO> getYearInformation(String zoneId, int type) {
        String fileName = initZoneIndexFileName(type, zoneId);
        return readFile(fileName, JingVisualConstant.YEAR_SIZE);
    }

    @Override
    public Map<String, List<DashboardVO>> getWeekInformation(String zoneId) {

        return getZoneLimit(zoneId, JingVisualConstant.WEEK_SIZE);
    }

    @Override
    public Map<String, List<DashboardVO>> getMonthInformation(String zoneId) {

        return getZoneLimit(zoneId, JingVisualConstant.MONTH_SIZE);
    }

    @Override
    public Map<String, List<DashboardVO>> getSeasonInformation(String zoneId) {

        return getZoneLimit(zoneId, JingVisualConstant.SEASON_SIZE);
    }

    @Override
    public Map<String, List<DashboardVO>> getYearInformation(String zoneId) {
        return getZoneLimit(zoneId, JingVisualConstant.YEAR_SIZE);
    }

    private Map<String, List<DashboardVO>> getZoneLimit(String zoneId, int limit) {

        Map<String, List<DashboardVO>> map = new HashMap<>();
        int[] arr = {1, 0, 2, 6, 4, 5, 7, 8};
        for (int i : arr) {
            String fileName = initZoneIndexFileName(i, zoneId);
            List<DashboardVO> list = readFile(fileName, limit);
            map.put(i + "", list);
        }

        return map;
    }

    private String initZoneIndexFileName(int type, String zoneId) {

        String path = JingVisualConstant.ZONE_FILE_PATH + zoneId + JingVisualConstant.UNDER_LINE;
        String fileName = initFileName(type);
        return path + fileName + JingVisualConstant.FILE_PATH_SUFFIX;
    }

    /**
     * 初始化文件名
     *
     * @param type
     * @param zoneId
     * @return
     */
    private String initFileName(int type) {
//        String fileName = DashboardConstant.FILE_PATH + zoneId + DashboardConstant.UNDER_LINE;
        String fileName = "";
        switch (type) {
            case 1:
                //内存
                fileName += "memory";
                break;
            case 0:
                //CPU
                fileName += "cpu";
                break;
            case 2:
                //主存储
                fileName += "mainStore";
                break;
            case 6:
                //辅助存储
                fileName += "auxiliaryStore";
                break;
            case 4:
                //公用IP地址
                fileName += "publicIP";
                break;
            case 5:
                //管理类IP地址
                fileName += "managerIP";
                break;
            case 7:
                //VLAN
                fileName += "vlan";
                break;
            case 8:
                //直接IP
                fileName += "directIP";
                break;
            case 9:
                //主机曲线图
                fileName += "hostGraph";
                break;
            default:
                break;
        }
        return fileName;
    }

    /**
     * 读取索引文件
     *
     * @param path
     *
     */
    /*
    private List<DashboardVO> readFileFromHead(String fileName, int limit) {
        long start = System.currentTimeMillis();
        LineNumberReader lnr = null;
        String str = null;
        List<DashboardVO> list = new ArrayList<>();
        try {
            lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(fileName), Charsets.UTF_8));
            while ((str = lnr.readLine()) != null) {
                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 4) {
                    DashboardVO db = new DashboardVO();
                    db.setPercentused(arr[0]);
                    db.setCapacityused(arr[1]);
                    db.setCapacitytotal(arr[2]);
//                    long time = NumberUtils.toLong(arr[3]);
                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[3]), DateConstants.DATE_TIME_FORMAT);
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
            LOG.debug("read zone index Use Time: " + (end - start));
        }

//        Collections.reverse(list);
        return list;
    }

    private List<DashboardVO> readFileFromTail(String fileName, int limit) {
        long start = System.currentTimeMillis();
        ReversedLinesFileReader rdf = null;
        AtomicInteger flag = new AtomicInteger(0);
        String str = null;
        List<DashboardVO> list = new ArrayList<>();
        try {
            rdf = new ReversedLinesFileReader(new File(fileName), 64, Charsets.UTF_8);
            while ((str = rdf.readLine()) != null) {
                flag.incrementAndGet();
                String[] arr = StringUtils.split(str, StringUtils.SPACE);
                if (null != arr && arr.length == 4) {
                    DashboardVO db = new DashboardVO();
                    db.setPercentused(arr[0]);
                    db.setCapacityused(arr[1]);
                    db.setCapacitytotal(arr[2]);
//                    long time = NumberUtils.toLong(arr[3]);
                    String date = DateFormatUtils.format(NumberUtils.toLong(arr[3]), DateConstants.DATE_TIME_FORMAT);
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
            LOG.debug("read zone index Use Time: " + (end - start) + " Line Num: "
                    + flag.get());
        }

        Collections.reverse(list);
        return list;
    }
     */
    private List<DashboardVO> readFile(String fileName, int limit) {
        List<DashboardVO> list = new ArrayList<>();
//        List<String> lines = resourceService.readFileFromTail(fileName, 64, limit);
        List<String> lines = resourceService.synchronizedReadFileFromTail(fileName, 64, limit);
        for (String line : lines) {
            String[] arr = StringUtils.split(line, StringUtils.SPACE);
            if (null != arr && arr.length == 4) {
                DashboardVO db = new DashboardVO();
                db.setPercentused(arr[0]);
                db.setCapacityused(arr[1]);
                db.setCapacitytotal(arr[2]);
                String date = DateFormatUtils.format(NumberUtils.toLong(arr[3]), DateConstants.DATE_TIME_FORMAT);
                db.setTime(date);
                list.add(db);
            }
        }
        return list;
    }

}
