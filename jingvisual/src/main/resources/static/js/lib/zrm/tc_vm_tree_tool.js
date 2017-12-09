//树形结构的展开和关闭
function hamburger_cross() {
    if ($(".rightcontent.device-tree").hasClass('is-open')) {
        $(".rightcontent.device-tree").removeClass('is-open').addClass('is-closed');
        $(".icon-posation").removeClass("glyphicon-arrow-left").addClass('glyphicon-arrow-right');
    } else {
        $(".rightcontent.device-tree").removeClass('is-closed').addClass('is-open');
        $(".icon-posation").removeClass("glyphicon-arrow-right").addClass('glyphicon-arrow-left');
    }
}

function searchdataContainer() {
    if ($(".rightcontent.device-tree .searchtree").hasClass('hidden')) {
        $(".rightcontent.device-tree .searchtree").removeClass('hidden');
    } else {
        $(".rightcontent.device-tree .searchtree").addClass('hidden');
    }
    $(".rightcontent.device-tree .showmethodContent").addClass('hidden');
}
function showmethodContainer() {
    if ($(".rightcontent.device-tree .showmethodContent").hasClass('hidden')) {
        $(".rightcontent.device-tree .showmethodContent").removeClass('hidden');
    } else {
        $(".rightcontent.device-tree .showmethodContent").addClass('hidden');
    }
    $(".rightcontent.device-tree .searchtree").addClass('hidden');
}


function focusKey(e) {
    if ($("#searchtree").hasClass("empty")) {
        $("#searchtree").removeClass("empty");
    }
}
function blurKey(e) {
    if ($("#searchtree").get(0).value === "") {
        $("#searchtree").addClass("empty");
    }
}
function searchNode() {
    var zTree = $.fn.zTree.getZTreeObj("devicetree");
    updateNodes(false, zTree.transformToArray(zTree.getNodes()));
    var value = $.trim($("#searchtree").get(0).value);
    var keyType = "name";
    if (value == "") {
        return false;
    }
    nodeList = zTree.getNodesByParamFuzzy(keyType, value);
    updateNodes(true, nodeList);
}
function getFontCss(treeId, treeNode) {
    return (!!treeNode.highlight) ? {color: "#A60000", "font-weight": "bold"} : {color: "#333", "font-weight": "normal"};
}

function updateNodes(highlight, nodeList) {
    var zTree = $.fn.zTree.getZTreeObj("devicetree");
    for (var i = 0, l = nodeList.length; i < l; i++) {
        nodeList[i].highlight = highlight;
        zTree.updateNode(nodeList[i]);
    }
}

$(document).ready(function () {
    $('.icon-posation').click(function () {
        hamburger_cross();
    });
    $('.devicetree .searchdata').click(function () {
        searchdataContainer();
    });
    $('.devicetree .showmethod').click(function () {
        showmethodContainer();
    });
    $("#searchtree").bind("focus", focusKey)
            .bind("blur", blurKey)
            .bind("propertychange", searchNode)
            .bind("input", searchNode);
    $('.devicetree .showmethodContent select').change(function () {
        var selectedValue = $(this).children('option:selected').val();
        if (selectedValue == "1") {
            window.location.href = "tc_chart.html";
        } else {
            window.location.href = "tc_map.html";
        }
    });
});