/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service.impl;

import cn.com.jingcloud.domain.entity.generic.Platform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import cn.com.jingcloud.domain.dao.generic.PlatformDao;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.AsyncResult;
import cn.com.jingcloud.service.ResourceService;
import cn.com.jingcloud.utils.Charsets;
import cn.com.jingcloud.utils.index.IndexFileItem;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.index.IndexConstants;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author liyong
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private final static Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Autowired
    @Qualifier("noDBPlatformDaoImpl")//platformDaoImpl noDBPlatformDaoImpl 二取一
    PlatformDao platformDao;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public Future<String> getRemotePostResult(String url) {
        String result = restTemplate.postForEntity(url, null, String.class).getBody();
        return new AsyncResult<>(result);
    }

    /**
     * 当前如果是2017-08-23 12:12:12 就得到 2017-08-23 12:00:00
     *
     * @return
     */
    @Override
    public long getDateIgnoreMinuteAndSECOND() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // 统计文件行数
    @Override
    public int getFileLineNumber(String fileName) {
        long start = System.currentTimeMillis();
        int lineNum = 0;
        LineNumberReader lnr = null;
        String str = null;
        try {
            lnr = new LineNumberReader(new InputStreamReader(
                    new FileInputStream(fileName), Charsets.UTF_8));
            while ((str = lnr.readLine()) != null) {
            }

            lineNum = lnr.getLineNumber();
        } catch (FileNotFoundException e) {
            LOG.error("getFileLineNumber param is " + fileName, e.getMessage());
        } catch (IOException e) {
            LOG.error("getFileLineNumber param is " + fileName, e.getMessage());
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

        return lineNum;
    }

    /**
     * can use LinkedList queue<br/>
     * 删除文件第一行，保存新数据到最后一行<br/>
     *
     * @param path
     * @param add
     */
    @Override
    public void deleteFirstLineAddEnd(String path, String add) {

        File inputFile = new File(path);
        File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            if (!inputFile.exists()) {
                FileUtils.touch(inputFile);
            }

            if (!tempFile.exists()) {
                FileUtils.touch(tempFile);
            }

//            reader = new BufferedReader(new FileReader(inputFile));
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), Charsets.UTF_8));
//            writer = new BufferedWriter(new FileWriter(tempFile));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), Charsets.UTF_8));
            String first = reader.readLine();//skip first line
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                writer.write(currentLine + System.lineSeparator());
            }

            writer.write(add + System.lineSeparator());

        } catch (IOException e) {
            LOG.error("creat index " + path + " failed : " + e.getMessage());
        } finally {
            try {
                if (null != writer) {
                    writer.close();

                }

            } catch (IOException ex) {
                LOG.error("deleteFirstLineAddEnd close writer error: " + path, ex.getMessage());
            }

            try {

                if (null != reader) {
                    reader.close();
                }
            } catch (IOException ex) {
                LOG.error("deleteFirstLineAddEnd close reader error: " + path, ex.getMessage());
            }
        }
        if (!inputFile.delete()) {
            LOG.error("Could not delete file: " + inputFile.getAbsolutePath());
            return;
        }

        if (!tempFile.renameTo(inputFile)) {
            LOG.error("Could not rename file: " + inputFile.getAbsolutePath());
        }
    }

    /**
     * 生成索引文件，追加文件到文件末尾<br/>
     *
     * @param fileName
     * @param content
     */
    @Override
    public void writeFileAppend(String fileName, String content) {
        File file = null;
        BufferedWriter fw = null;
        try {
            file = new File(fileName);
            if (!file.exists()) {
                FileUtils.touch(file);
            }
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), Charsets.UTF_8));
//            fw = new BufferedWriter(new FileWriter(file, true));
            String s = content + System.lineSeparator();
            fw.write(s);
        } catch (IOException e) {
            LOG.error("creat index " + fileName + " failed : " + e.getMessage());
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ex) {
                    LOG.error("writeFileAppend close  error: " + fileName, ex.getMessage());
                }
            }
        }
    }

    /**
     * 逆向读取文件
     *
     * @param fileName
     * @param blockSize
     * @param limit
     * @return
     */
    @Override
    public List<String> readFileFromTail(String fileName, int blockSize, int limit) {
        long start = System.currentTimeMillis();
        ReversedLinesFileReader rdf = null;
        AtomicInteger flag = new AtomicInteger(0);
        String str = null;
        List<String> list = new ArrayList<>();
        try {
            rdf = new ReversedLinesFileReader(new File(fileName), blockSize, Charsets.UTF_8);
            while ((str = rdf.readLine()) != null) {
                flag.incrementAndGet();
                list.add(str);

                if (flag.get() >= limit) {
                    break;
                }

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
            LOG.debug("read  index" + fileName + " Use Time: " + (end - start) + " Line Num: "
                    + flag.get());
        }

        Collections.reverse(list);
        return list;
    }

    /**
     * 针对每一个文件对象增加读写锁
     *
     * @param fileName
     * @param blockSize
     * @param limit
     * @return
     */
    @Override
    public List<String> synchronizedReadFileFromTail(String fileName, int blockSize, int limit) {
        IndexFileItem indexFileItem = initIndexFileItem(fileName);
        String currentActon = indexFileItem.getCurrentAction();
        //写的时候不能读
        if (IndexConstants.WRITE.equals(currentActon)) {
            int count = 0;
            while (count < 10) {//10s 最多尝试10次，文件还没有写完的话，就去读正在写的文件
                if (!IndexConstants.WRITE.equals(currentActon)) {//空(第一次是读) 或者 READ(写完成之后置了read状态)
                    break;
                } else {
                    Utils.sleepSeconds(1);
                    count++;
                }
            }
        }
        List<String> list = this.readFileFromTail(fileName, blockSize, limit);
        return list;
    }

    /**
     * 写文件的时候没有进行读加锁
     *
     * @param fileName
     * @param content
     */
    @Override
    public void writeFile(String fileName, String content) {

        int count = this.getFileLineNumber(fileName);
        if (count >= JingVisualConstant.YEAR_SIZE) {
            this.deleteFirstLineAddEnd(fileName, content);
        } else {
            this.writeFileAppend(fileName, content);
        }

    }

    /**
     * 针对每一个文件对象增加读写锁<br/>
     * TC模块自定义方法进行加锁，其余虚拟机，主机，数据中心使用统一方法进行加锁<br/>
     *
     * @param fileName
     * @param content
     */
    @Override
    public void synchronizedWriteFile(String fileName, String content) {
        IndexFileItem indexFileItem = initIndexFileItem(fileName);
        //写的时候需要进行同步
        synchronized (indexFileItem) {
            indexFileItem.setCurrentAction(IndexConstants.WRITE);

            this.writeFile(fileName, content);

            indexFileItem.setCurrentAction(IndexConstants.READ);
        }
    }

    /**
     * 初始化,此方法用来进行文件同步加锁的
     *
     * @param fileName
     * @return
     */
    private IndexFileItem initIndexFileItem(String fileName) {
        IndexFileItem lfi = IndexConstants.IndexFileMap.get(fileName);
        if (lfi == null) {
            IndexFileItem newlfi = new IndexFileItem();
            lfi = IndexConstants.IndexFileMap.putIfAbsent(fileName, newlfi);
            if (lfi == null) {
                lfi = newlfi;
            }
        }
        return lfi;
    }

    /**
     * 每一个索引文件对象
     */
//    private static class IndexFileItem {
//
//        private String currentAction;//read or write
//
//    }
    @Override
    public Platform findFirstIaas() {

        return platformDao.findFirstIaas();
    }

    @Override
    public Platform findFirstDaas() {
        return platformDao.findFirstDaas();
    }

    @Override
    public Platform addIaas(String ip, int pot) {
        Platform iaas = platformDao.findFirstIaas();
        if (null == iaas) {
            iaas = new Platform();
            iaas.setIp(ip);
            iaas.setPort(pot);
            iaas.setType(JingVisualConstant.IAAS_TYPE);
            iaas = platformDao.addIaas(iaas);
        }
//        else {
//            throw new RuntimeException("iaas have already exist!");

//        }
        return iaas;
    }

    @Override
    public Platform addDaas(String ip, int pot) {
        Platform daas = platformDao.findFirstDaas();
        if (null == daas) {
            daas = new Platform();
            daas.setIp(ip);
            daas.setPort(pot);
            daas.setType(JingVisualConstant.DAAS_TYPE);
            daas = platformDao.addDaas(daas);
        }
//        else {
//            throw new RuntimeException("iaas have already exist!");

//        }
        return daas;
    }

    @Override
    public Platform updateIaas(long id, String ip) {
        Platform iaas = platformDao.findIaas(id);
        iaas.setIp(ip);
        return platformDao.updateIaas(iaas);
    }

    @Override
    public Platform updateDaas(long id, String ip) {
        Platform daas = platformDao.findDaas(id);
        daas.setIp(ip);
        return platformDao.updateDaas(daas);
    }

    @Override
    public boolean deleteIaas(long id, String ip) {
        Platform iaas = platformDao.findIaas(id);
        iaas.setIp(ip);
        return platformDao.deleteIaas(iaas);
    }

    @Override
    public boolean deleteDaas(long id, String ip) {
        Platform daas = platformDao.findDaas(id);
        daas.setIp(ip);
        return platformDao.deleteDaas(daas);
    }
}
