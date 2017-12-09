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

function drawZoneChart(time) {
    var cpu = new Array();//0
    var memory = new Array();//1
    var mainStore = new Array();//2
    var publicIP = new Array();//4
    var managerIP = new Array();//5
    var auxiliaryStore = new Array();//6
    var vlan = new Array();//7
    var directIP = new Array();//8
    var timeArr = new Array();
    var zoneid = getZone().id;
    var path = "/zone/" + zoneid + "/" + time;
    var zoneResource = getZoneResource(path);
    if(zoneResource){ 
        $.each(zoneResource, function (i, n) {
            i = parseInt(i);
            if (i === 0) {
                $.each(n, function (index, m) {
                    cpu.push(m.percentused);
                    timeArr.push(m.time);
                });

            } else if (i === 1) {

                $.each(n, function (index, m) {
                    memory.push(m.percentused);
                });
            } else if (i === 2) {

                $.each(n, function (index, m) {
                    mainStore.push(m.percentused);
                });

            } else if (i === 4) {

                $.each(n, function (index, m) {
                    publicIP.push(m.percentused);
                });

            } else if (i === 5) {

                $.each(n, function (index, m) {
                    managerIP.push(m.percentused);
                });

            } else if (i === 6) {

                $.each(n, function (index, m) {
                    auxiliaryStore.push(m.percentused);
                });

            } else if (i === 7) {

                $.each(n, function (index, m) {
                    vlan.push(m.percentused);
                });

            } else if (i === 8) {

                $.each(n, function (index, m) {
                    directIP.push(m.percentused);
                });

            }
        });
   }
    var config = {
        type: 'line',
        data: {
            labels: timeArr,
            datasets: [
                {
                    label: "CPU",
                    backgroundColor: window.chartColors.red,
                    borderColor: window.chartColors.red,
                    data: cpu,
                    fill: false
                },
                {
                    label: "内存",
                    backgroundColor: window.chartColors.violet,
                    borderColor: window.chartColors.violet,
                    data: memory,
                    fill: false
                },
                {
                    label: "主存储",
                    backgroundColor: window.chartColors.orange,
                    borderColor: window.chartColors.orange,
                    data: mainStore,
                    fill: false
                }
                ,
                {
                    label: "公用IP地址",
                    backgroundColor: window.chartColors.grey,
                    borderColor: window.chartColors.grey,
                    data: publicIP,
                    fill: false
                }
                , {
                    label: "管理类IP地址",
                    backgroundColor: window.chartColors.yellow,
                    borderColor: window.chartColors.yellow,
                    data: managerIP,
                    fill: false
                },
                {
                    label: "辅助存储",
                    backgroundColor: window.chartColors.green,
                    borderColor: window.chartColors.green,
                    data: auxiliaryStore,
                    fill: false
                },
                {
                    label: "VLAN",
                    backgroundColor: window.chartColors.blue,
                    borderColor: window.chartColors.blue,
                    data: vlan,
                    fill: false
                },
                {
                    label: "直接IP",
                    backgroundColor: window.chartColors.purple,
                    borderColor: window.chartColors.purple,
                    data: directIP,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            title: {
                display: true,
                text: '综合统计'
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

    var ctx = document.getElementById("canvas_dashboard").getContext("2d");
    window.myLine = new Chart(ctx, config);

}
$(function () {
//    drawZoneChart("week");
}); 