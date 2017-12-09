/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

import cn.com.jingcloud.domain.entity.generic.Platform;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author liyong
 */
public interface ResourceService {

    Future<String> getRemotePostResult(String url);

    Platform findFirstIaas();

    Platform findFirstDaas();

    Platform addIaas(String ip, int pot);

    Platform addDaas(String ip, int pot);

    Platform updateIaas(long id, String pot);

    Platform updateDaas(long id, String pot);

    boolean deleteIaas(long id, String pot);

    boolean deleteDaas(long id, String pot);

    long getDateIgnoreMinuteAndSECOND();

    int getFileLineNumber(String fileName);

    void deleteFirstLineAddEnd(String path, String add);

    void writeFileAppend(String fileName, String content);

    void writeFile(String fileName, String content);

    void synchronizedWriteFile(String fileName, String content);

    List<String> readFileFromTail(String fileName, int blockSize, int limit);

    public List<String> synchronizedReadFileFromTail(String fileName, int blockSize, int limit);
}
