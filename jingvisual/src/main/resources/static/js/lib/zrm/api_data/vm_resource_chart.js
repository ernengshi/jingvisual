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

function drawVmInfoChart(time) {
    var cpu = ["28.28", "28.28", "28.28", "28.28", "28.28", "28.28", "28.28", "28.28", "28.28", "28.28", "28.28", "28.28"];

    var tcUseInfo = {};
    if (tcUseInfo) {
        // $.each(tcUseInfo, function (i, n) {
        //     i = parseInt(i);
        //     if (i === 0) {
        //         $.each(n, function (index, m) {
        //             cpu.push(m.percentused);
        //             timeArr.push(m.time);
        //         });

        //     } else if (i === 1) {

        //         $.each(n, function (index, m) {
        //             memory.push(m.percentused);
        //         });
        //     } else if (i === 2) {

        //         $.each(n, function (index, m) {
        //             mainStore.push(m.percentused);
        //         });

        //     }  
        // });
    }
    var config = {
        type: 'line',
        data: {
            labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
            datasets: [
                {
                    label: "河山街道",
                    backgroundColor: window.chartColors.red,
                    borderColor: window.chartColors.red,
                    data: cpu,
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

    var ctx = document.getElementById("canvas_vm_cpu").getContext("2d");
    window.myLine = new Chart(ctx, config);

}
//$(function () {
//    drawVmInfoChart("year");
//}); 