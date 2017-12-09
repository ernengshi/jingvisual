/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.vo.iaas;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * VM的树形结构
 *
 * @author liyong
 */
public class VmGroupVO {

    public VmGroupVO() {
    }

    public VmGroupVO(String id, String name, String pId, boolean isParent) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.isParent = isParent;
    }

    private String id;
    private String pId;
    private String name;
    @JsonIgnore
    long accountid;
//    @JsonIgnore
    private String icon;
    private boolean isParent;
    private boolean open;
    String t;//title
    int page;
    int pageSize;
    int count;
    int maxPage;
//    @JsonIgnore
    private boolean isLeaf;
    private String leafNodeGlobalPath;
    private boolean click = false;

    public String getLeafNodeGlobalPath() {
        return leafNodeGlobalPath;
    }

    public void setLeafNodeGlobalPath(String leafNodeGlobalPath) {
        this.leafNodeGlobalPath = leafNodeGlobalPath;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAccountid() {
        return accountid;
    }

    public void setAccountid(long accountid) {
        this.accountid = accountid;
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public boolean isClick() {
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

}
