function getTCTree() {
    var zNodes = [];
    $.ajax({
        url: "/tc_group?pageSize=100",
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
//        selectedMulti: false
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
//        onClick: onClick,
        beforeExpand: beforeExpand,
        onAsyncSuccess: onAsyncSuccess,
        onAsyncError: onAsyncError
    }
};
//          

function getUrl(treeId, treeNode) {
    return "/tcs_data/" + treeNode.id + "/" + treeNode.page + "/" + treeNode.pageSize;
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
    console.log(treeNode.name + '_' + treeNode.click);
    return (treeNode.click);
}


function getFullAddressExceptProvince(add) {
    var str = "";
    var address = add.split("_"); //字符分割
    for (var i = 1; i < address.length; i++)
    {
        str += address[i];
    }
    return str;
}

function getFulAddress(add) {
    var str = "";
    var address = add.split("_"); //字符分割
    for (var i = 0; i < address.length; i++)
    {
        str += address[i]+" ";
    }
    return str;
}

function getProvince(add) {
    var address = add.split("_"); //字符分割
    return address[0];
}


function getTCBreadCrumb(add) {

    var str = "";
    var address = add.split("_"); //字符分割
    for (var i = 0; i < address.length; i++)
    {
        str += "<li><a href='javascript:void(0)'>" + address[i] + "</a></li>";
    }
    return str;

}

//function initTree() {
//    var zNodes = getTCTree();
//    $.fn.zTree.init($("#devicetree"), setting, zNodes);
//}
//$(document).ready(function () {
//    init();
//});
