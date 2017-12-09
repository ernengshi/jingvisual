//function getTCTree() {
//    var zNodes = [];
//    $.ajax({
//        url: "/tc_group?pageSize=100",
//        dataType: "json",
//        async: false,
//        success: function (json) {
//            zNodes = json;
//        }
//    });
//    return zNodes;
//}
//
//var setting = {
//    async: {
//        enable: true,
//        url: getUrl,
//        type: "get",
//    },
//    view: {
////        selectedMulti: false
//        fontCss: getFontCss,
//    },
//    check: {
//        enable: false
//    },
//    data: {
//        simpleData: {
//            enable: true
//        }
//    },
//    edit: {
//        enable: false
//    },
//    callback: {
//        beforeClick: beforeClick,
////        onClick: onClick,
//        beforeExpand: beforeExpand,
//        onAsyncSuccess: onAsyncSuccess,
//        onAsyncError: onAsyncError
//    }
//};
////
//
//function getUrl(treeId, treeNode) {
//    return "/tcs_data/" + treeNode.id + "/" + treeNode.page + "/" + treeNode.pageSize;
//}
//
//
//
//function beforeExpand(treeId, treeNode) {
//    if (treeNode.page == 0)
//        treeNode.page = 1;
//    return !treeNode.isAjaxing;
//}
//
//function onAsyncSuccess(event, treeId, treeNode, msg) {
//
//}
//function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
//    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
//    alert("异步获取数据出现异常。");
//    treeNode.icon = "";
//    zTree.updateNode(treeNode);
//}
//
//function beforeClick(treeId, treeNode, clickFlag) {
//    console.log(treeNode.name + '_' + treeNode.click);
//    return (treeNode.click);
//}

function getRdPoint(m) {
    var point = [0];
    for (var i = 0; i < m; i++)
    {
        point[i] = parseInt(Math.floor(Math.random() * 10));
        for (var j = 0; j < i; j++)
        {
            if (point[j] == point[i])
            {
                while (1)
                {
                    point[i] = parseInt(Math.floor(Math.random() * 10));
                    if (point[i] != point[j])
                    {
                        j = -1;
                        break;
                    }

                }

            }
        }
    }
    return point;
}

function getTitle(tc, fullAddress) {
    var hostname = tc.hostname;
    var ip = tc.ip;
    var mac = tc.mac;
    var state = tc.state_title;
    var lasttime = toLocalDate(tc.last_time);

    var html =
            "<h4 style='margin:0 0 5px 0;padding:0.2em 0'>" + fullAddress + "</h4>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'>" +
            "名称：" + hostname + "<br />" +
            "IP：" + ip + "<br />" +
            "Mac：" + mac + "<br />" +
            "状态：" + state + "<br />" +
            "最后接入：" + lasttime + "<br />" +
            "</p>";

    return html;

}

function getPointInfo(node) {
    var address = node.name;
    var fullAddress = getFulAddress(address);
    var detailAddress = getFullAddressExceptProvince(address);
    var provicne = getProvince(address);

    var tcArr = getTCs(node);

    //设置面包屑
//    $("#breadcrumb-custom").html(getTCBreadCrumb(address));
    $("#map_address_title").html("当前社区是 " + fullAddress)

    var myGeo = new BMap.Geocoder();
    myGeo.getPoint(detailAddress, function (point) {
        if (point) {
            var map = new BMap.Map("devicemap");
            map.centerAndZoom(point, 18);

            var pointCount = tcArr.length;
            var rdPoint = getRdPoint(pointCount);
            var marker = new Array(); //存放标注点对象的数组
            var infoWindow = new Array(); //存放提示信息窗口对象的数组
            for (var i = 0; i < pointCount; i++) {

                var tc = tcArr[i];
                var title = getTitle(tc, fullAddress);
                var state = tc.state;
                var posi = {left: -38, top: -38};
//                var myicononline = 11;
//                alert(state)
                if ("online" !== state) {
//                    myicononline = 10;
                    posi.left = -300;
                    posi.top = -37;
                }

//                var myIcon = new BMap.Icon("http://api.map.baidu.com/img/markers.png", new BMap.Size(23, 25), {
//                    offset: new BMap.Size(10, 25),
////                    imageOffset: new BMap.Size(0, 0 - ((i + 1) * 25))
//                    imageOffset: new BMap.Size(-23, 0 - (myicononline * 25))
//
//                });

                var myIcon = new BMap.Icon("/image/map_state.png", new BMap.Size(115, 114), {
                    offset: new BMap.Size(10, 25),
                    imageOffset: new BMap.Size(posi.left, posi.top)

                });



                point[i] = new window.BMap.Point(point.lng + rdPoint[i] * 0.00002, point.lat + rdPoint[i] * 0.00002); //循环生成新的地图点
                marker[i] = new window.BMap.Marker(point[i], {icon: myIcon});
//                marker[i].setIcon("11111");
                map.addOverlay(marker[i]);
//              marker[i].setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画


                marker[i].addEventListener("click", function () {
                    infoWindow[i] = new window.BMap.InfoWindow(title);
                    map.openInfoWindow(infoWindow[i], point[i]);
                    this.openInfoWindow(infoWindow[i], point[i]);
                });
            }
        } else {
            alert("您输入的地址没有解析到结果!");
        }
    }, provicne);
}
/**
 * 查询一共多少循环页面
 * @type Number
 */
var leafNode = [];
var pagecount;
function filterLeaf(json) {
//    var leafNode = [];
    $.each(json, function (i, n) {
        if (n.isLeaf) {
            leafNode.push({id: n.id, name: n.leafNodeGlobalPath});
        }

    });
    pagecount = leafNode.length;
//    return leafNode;
}



/**
 * 
 * @param {type} leafNode
 * @returns {Array|json}
 */
function getTCs(leafNode) {
    var tcArr = [];
    $.ajax({
        url: "/tcs_vo/" + leafNode.id + "/1/100",
        dataType: "json",
        async: false,
        success: function (json) {
            tcArr = json;
//            console.log(json.length);
        }
    });
    return tcArr;
}





function init() {
    var zNodes = getTCTree();
    $.fn.zTree.init($("#devicetree"), setting, zNodes);
    filterLeaf(zNodes);
//    getPointInfo("电视塔", "西安");
}


var auto_page_index = 0;
function letsGO() {
    var node = leafNode[auto_page_index];
//    alert(node.id + " " + node.name + "     " + index);
    getPointInfo(node);
    auto_page_index++;
    if (auto_page_index >= (pagecount)) {
        auto_page_index = 0;
    }

}

$(function () {
    init();
    letsGO();
    window.setInterval(function () {
        return letsGO()
    }, 3000);
});


//append baidu map.js
//$(document).ready(function () {
//    var script = document.createElement("script");
//    script.type = "text/javascript";
//    script.src = "https://api.map.baidu.com/api?v=2.0&ak=您的密钥&callback=init";
//    document.body.appendChild(script);

//});
