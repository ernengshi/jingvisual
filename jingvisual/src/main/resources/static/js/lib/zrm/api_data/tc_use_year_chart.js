window.chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)',
    violet: 'rgb(238, 130, 238)'
};

function drawTcUseChart(timeArr, data) {

    var config = {
        type: 'line',
        data: {
            labels: timeArr,
            datasets: [
                {
                    label: "正常使用率",
                    backgroundColor: window.chartColors.green,
                    borderColor: window.chartColors.green,
                    data: data,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            title: {
                display: true,
//                text: '社区周使用率'
            },
            tooltips: {
                mode: 'index',
                intersect: false
            },
            hover: {
                mode: 'nearest',
                intersect: true
            },
            scales: {
                xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: '日期'
                        }
                    }],
                yAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: '百分比 %'
                        }
                    }]
            }
        }
    };

    var ctx = document.getElementById("canvas_tcUseYearChart").getContext("2d");
    window.myLine = new Chart(ctx, config);

}

function getTCUsedChartInfo(node) {
    var groupId = node.id;
    var address = getFulAddress(node.name);

    var usedArr = new Array();//8
    var timeArr = new Array();
    $.ajax({
        url: "/tcs_used/" + groupId + "/year",
        dataType: "json",
        async: true,
        success: function (json) {
            $.each(json, function (i, n) {
                usedArr.push(n.used);
                timeArr.push(n.time);
            });

            $("#used_address_title").html(address + " 云一体机周使用率")
            drawTcUseChart(timeArr, usedArr);
        }
    });

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

function initTCChartTree() {
    var zNodes = getTCTree();
    $.fn.zTree.init($("#devicetree"), setting, zNodes);
    filterLeaf(zNodes);
}


var auto_page_index = 0;
function letsGO() {
    var node = leafNode[auto_page_index];
//    alert(node.id + " " + node.name + "     " + index);
    getTCUsedChartInfo(node);
    auto_page_index++;
    if (auto_page_index >= (pagecount)) {
        auto_page_index = 0;
    }

}

$(function () {
    initTCChartTree();
    letsGO();
    window.setInterval(function () {
        return letsGO()
    }, 3000);

}); 