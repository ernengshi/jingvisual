/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.dao.generic;

import cn.com.jingcloud.domain.entity.generic.AreaTree;
import java.util.List;

/**
 *
 * @author liyong
 */
public interface AreaTreeDao {

    AreaTree findAreaTree(long id);

    List<AreaTree> findAreaTrees(long pid);

    List<AreaTree> findProvinces();

    List<AreaTree> findAll();

    AreaTree addAreaTree(AreaTree areaTree);

    AreaTree updateAreaTree(AreaTree areaTree);

    boolean deleteAreaTree(AreaTree areaTree);
}
