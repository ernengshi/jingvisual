/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.vo.daas;

/**
 *
 * @author liyong
 */
//@Data
//@AllArgsConstructor
public class TreeNode {

    private long id;
    private long pId;
    private String name;
    private String icon;
    private boolean isParent;
    private boolean open;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isIsParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public TreeNode(long id, String name, long pId, boolean isParent) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.isParent = isParent;
    }

    public TreeNode(long id, String name, String icon, long pId, boolean isParent) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.icon = icon;
        this.isParent = isParent;
    }

    public TreeNode(long id, String name, long pId) {
        this.id = id;
        this.pId = pId;
        this.name = name;

    }

    public TreeNode() {
    }

}
