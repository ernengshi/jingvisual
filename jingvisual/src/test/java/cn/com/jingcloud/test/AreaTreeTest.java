/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.domain.entity.generic.AreaTree;
import cn.com.jingcloud.service.IaasVMService;
import cn.com.jingcloud.utils.Utils;
import cn.com.jingcloud.utils.repository.GenericRepository;
import cn.com.jingcloud.vo.daas.TreeNode;
import cn.com.jingcloud.vo.iaas.VmGroupVO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaTreeTest {

    @Autowired
    @Qualifier("primaryGenericRepository")
    GenericRepository genericRepository;

    @Test
    public void createOne() {
        AreaTree u1 = new AreaTree();
        u1.setName("n1");
        u1.setPid(0);
        u1.setEnabled(1);

        AreaTree u2 = new AreaTree();
        u2.setName("n2");
        u2.setPid(0);
        u2.setEnabled(1);

        AreaTree u3 = new AreaTree();
        u3.setName("n3");
        u3.setPid(0);
        u3.setEnabled(1);

        AreaTree u4 = new AreaTree();
        u4.setName("n11");
        u4.setPid(1);
        u4.setEnabled(1);

        long id1 = genericRepository.create(u1);

        long id2 = genericRepository.create(u2);
        long id3 = genericRepository.create(u3);
        long id4 = genericRepository.create(u4);
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

    @Autowired
    IaasVMService iaasVMService;

    @Test
    public void leafTree() {
        List<VmGroupVO> list = new ArrayList<>();
        list.add(new VmGroupVO("1", "01", null, true));
        list.add(new VmGroupVO("2", "02", "1", true));
        list.add(new VmGroupVO("3", "03", "2", true));
        list.add(new VmGroupVO("4", "04", "3", true));//leaf
        list.add(new VmGroupVO("5", "05", "2", true));//leaf
        list.add(new VmGroupVO("6", "06", "3", true));//leaf
        list.add(new VmGroupVO("7", "07", "4", true));
        list.add(new VmGroupVO("8", "08", "7", true));
        list.add(new VmGroupVO("9", "09", "8", true));
        list.add(new VmGroupVO("10", "10", "9", true));

//        List<String> ids = new ArrayList<>();
        List<String> pids = new ArrayList<>();
        for (VmGroupVO tree : list) {
//            ids.add(tree.getId());
            pids.add(tree.getpId());
        }
        List<VmGroupVO> leafNode = new ArrayList<>();
        List<VmGroupVO> parentNode = new CopyOnWriteArrayList<>();
        for (VmGroupVO tree : list) {//有父节点 但没有子节点
            if (tree.isIsParent() && !pids.contains(tree.getId())) {
//                System.out.println("------------" + tree.getName());
                leafNode.add(tree);
            } else {
                parentNode.add(tree);
            }
        }
        long t1 = System.currentTimeMillis();
//        String heh = getTreeGlobalPath(parentNode, leafNode.get(0), leafNode.get(0).getName());
        for (VmGroupVO vo : leafNode) {
            String heh = getTreeGlobalPath(parentNode, vo, vo.getName());
            System.out.println(heh);
        }

        long t2 = System.currentTimeMillis();
        System.out.println("total Time is " + (t1 - t1));
    }

    @Test
    public void leafMapTree() {
        List<VmGroupVO> list = iaasVMService.listVmGroup(100);

        List<String> ids = new ArrayList<>();
        List<String> pids = new ArrayList<>();
        for (VmGroupVO tree : list) {
            ids.add(tree.getId());
            pids.add(tree.getpId());
        }
        List<VmGroupVO> leafNode = new ArrayList<>();
        List<VmGroupVO> parentNode = new CopyOnWriteArrayList<>();
        for (VmGroupVO tree : list) {//有父节点 但没有子节点
            if (tree.isIsParent() && !pids.contains(tree.getId())) {
//                System.out.println("------------" + tree.getName());
                leafNode.add(tree);
            } else {
                parentNode.add(tree);
            }
        }

//        String heh = getTreeGlobalPath(parentNode, leafNode.get(0), leafNode.get(0).getName());
        for (VmGroupVO vo : leafNode) {
            String heh = getTreeGlobalPath(parentNode, vo, vo.getName());
            System.out.println(heh);
        }

    }
}
