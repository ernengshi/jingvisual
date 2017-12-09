//查询树形结构
function getVmTree() {
    var zNodes = [];
    $.ajax({
        url: "/vm_group?pageSize=100",
        dataType: "json",
        async: false,
        success: function (json) {
            zNodes = json;
        }
    });
    return zNodes;
}

var setting = {
    async: {
        enable: true,
        url: getUrl,
        type: "get",
    },
    view: {
//        selectedMulti: false,
        fontCss: getFontCss,
    },
    check: {
        enable: false
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    edit: {
        enable: false
    },
    callback: {
        beforeClick: beforeClick,
        onClick: onClick,
        beforeExpand: beforeExpand,
        onAsyncSuccess: onAsyncSuccess,
        onAsyncError: onAsyncError
    }
};
//             

var curPage = 0;
function getUrl(treeId, treeNode) {
    aObj = $("#" + treeNode.tId + "_a");
    aObj.attr("title", "当前第 " + treeNode.page + " 页 / 共 " + treeNode.maxPage + " 页")
    return "/vms_data/" + treeNode.id + "/" + treeNode.page + "/" + treeNode.pageSize;
}
function goPage(treeNode, page) {
    treeNode.page = page;
    if (treeNode.page < 1)
        treeNode.page = 1;
    if (treeNode.page > treeNode.maxPage)
        treeNode.page = treeNode.maxPage;
    if (curPage == treeNode.page)
        return;
    curPage = treeNode.page;
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    zTree.reAsyncChildNodes(treeNode, "refresh");
}


function beforeExpand(treeId, treeNode) {
    if (treeNode.page == 0)
        treeNode.page = 1;
    return !treeNode.isAjaxing;
}
function onAsyncSuccess(event, treeId, treeNode, msg) {

}
function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    alert("异步获取数据出现异常。");
    treeNode.icon = "";
    zTree.updateNode(treeNode);
}

function beforeClick(treeId, treeNode, clickFlag) {
//    return (true);
    //后台输出
    console.log(treeNode.name + '_' + treeNode.click);
    return (treeNode.click);
}

function onClick(event, treeId, treeNode, clickFlag) {
//    console.log("[ " + getTime() + " onClick ]&nbsp;&nbsp;clickFlag = " + clickFlag + " (" + (clickFlag === 1 ? "普通选中" : (clickFlag === 0 ? "<b>取消选中</b>" : "<b>追加选中</b>")) + ")");
    console.log(treeNode.id);
    var vmId = treeNode.id;
//    getVmChartInfo(vmId);
    window.location.href = '/vm_chart/' + vmId;
}

function initVMTree() {
    var zNodes = getVmTree();
    $.fn.zTree.init($("#devicetree"), setting, zNodes);
}
$(document).ready(function () {
    initVMTree();
});
