/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.dao.generic.impl;

import cn.com.jingcloud.domain.dao.generic.AreaTreeDao;
import cn.com.jingcloud.domain.entity.generic.AreaTree;
import cn.com.jingcloud.utils.repository.GenericRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 *
 * @author liyong
 */
@Repository
public class AreaTreeDaoImpl implements AreaTreeDao {

    @Autowired
    @Qualifier("primaryGenericRepository")
    GenericRepository genericRepository;

    @Override
    public AreaTree findAreaTree(long id) {
        String sql = "SELECT * FROM area_tree at where at.enabled=1 and at.id=:id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return genericRepository.findFirst(AreaTree.class, sql, paramMap);
    }

    /**
     * 获取第一层树
     *
     * @return
     */
    @Override
    public List<AreaTree> findProvinces() {
        //树形结构表中 数据表跟自己做连接 找出所有二级节点#--rootNodeId  根节点id
//        select a.id from ap_photo_node b,ap_photo_node a where a.parent_id = b.id and b.parent_id= rootNodeId  and b.enabled=1 and a.enabled=1
//        String sql = "SELECT * FROM area_tree at where at.enabled=1 and at.pid=0";
        String sql = "SELECT * FROM area_tree at where at.enabled=1";
        return genericRepository.list(AreaTree.class, sql, Collections.emptyMap());
    }

    @Override
    public List<AreaTree> findAreaTrees(long pid) {
        String sql = "SELECT * FROM area_tree at where at.enabled=1 and at.pid=:pid";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pid", pid);
        return genericRepository.list(AreaTree.class, sql, paramMap);
    }

    @Override
    public List<AreaTree> findAll() {
        String sql = "SELECT * FROM area_tree at where at.enabled=1";

        return genericRepository.list(AreaTree.class, sql, Collections.emptyMap());
    }

    @Override
    public AreaTree addAreaTree(AreaTree areaTree) {
        long id = genericRepository.create(areaTree);
        areaTree.setId(id);
        return areaTree;
    }

    @Override
    public AreaTree updateAreaTree(AreaTree areaTree) {
        genericRepository.update(areaTree);
        return areaTree;
    }

    @Override
    public boolean deleteAreaTree(AreaTree areaTree) {
        int rowsAffected = genericRepository.delete(areaTree);
        return rowsAffected >= 1;
    }

}
