var monitorChart = function (chartOptions) {
    var allInterval;
    var chart;
    var args = {"context": "abc"};
    var chartMonitor = function (args, options) {
        if (!options) {
            options = {};
        }
        var resource = options.resource;
        var charttype = chartOptions.charttype(resource);
        var chartDiv = chartOptions.chartDiv(resource);
        var available;
        var chartData;
        var date;

        if (allInterval && allInterval != null) {
            clearInterval(allInterval);
            allInterval == null;
        }
        if (chart && chart != null && $("div.#" + chartDiv).html() != "") {
            chart.destroy();
        }
        chartOptions.chartData({
            context: args.context,
            timeType: "hour",
            resource: resource,
            response: {
                success: function (data) {
                    chartData = data.chartTypeData;
                    available = data.available;
                    date = data.date;
                }
            }
        });
        if (!chartData) {
            return false;
        }
        var chartDataArray = [];
        $.each(chartData, function (index) {
            var typeData = {
                name: chartData[index].typetext,
                data: (function () {
                    // 初始化数据
                    var datas = [];
                    var chartDataPoint = chartData[index].data;

                    for (var i = 0; i < 100; i++) {
                        var time = new Date(Date.parse(date[i].replace(/-/g, "/"))).getTime();
                        datas.push({
                            x: time,
                            y: (available[i] == false) ? 0 : chartDataPoint[i]
                        });
                    }
                    return datas;
                })()
            };
            if (chartData[index].color) {
                typeData.color = chartData[index].color;
            }
            chartDataArray.push(typeData);
        });
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });
        chart = new Highcharts.Chart({
            chart: {
                renderTo: chartDiv,
                defaultSeriesType: charttype,
                marginRight: 10,
                events: {
                    load: function () {
                        var chartUpdate = this;
                        var chartInterval = setInterval(function () {
                            allInterval = chartInterval;
                            var chartCurrentData;
                            var currentAvailable;
                            var currentDate;
                            chartOptions.chartCurrentApi({
                                context: args.context,
                                resource: resource,
                                timeType: "hour",
                                response: {
                                    success: function (data) {
                                        chartCurrentData = data.chartCurrentData;
                                        currentAvailable = data.currentAvailable;
                                        currentDate = data.currentDate;
                                    }
                                }
                            });
                            var x = new Date(Date.parse(currentDate.replace(/-/g, "/"))).getTime(); // 当前时间 
                            $.each(chartCurrentData, function (index) {
                                var yVal = (currentAvailable == false) ? 0 : chartCurrentData[index];
                                chartUpdate.series[index].addPoint([x, yVal], true, true);
                            });

                        }, 36000);
                    }
                }
            },
            title: {
                text: chartOptions.title(resource)
            },
            xAxis: {
                title: {
                    text: chartOptions.xtext(resource)
                },
                type: 'datetime'
            },
            yAxis: {
                title: {
                    text: chartOptions.ytext(resource)
                },
                labels: {
                    formatter: function () {//设置纵坐标值的样式  
                        var yVal = chartOptions.yformatter(resource, this.value);
                        return yVal;
                    }
                },
                plotLines: [
                    {
                        value: 0,
                        width: 0,
                        color: '#808080'
                    }
                ]
            },
            tooltip: {
                shared: true,
                formatter: function () {
                    var tooltipVal = chartOptions.tooltip(resource, this);
                    return tooltipVal;
                }
            },
            legend: {
                enabled: true
            },
            credits: {
                enabled: false
            },
            exporting: {
                enabled: true
            },
            plotOptions: {
                area: {
                    stacking: 'normal',
                    lineColor: '#666666',
                    lineWidth: 1,
                    marker: {
                        enabled: false
                    }
                }
            },
            series: chartDataArray
        });
        return true;
    };

    var exe = function (args) {
        var resource = chartOptions.resource;
        $.each(resource, function (index) {
            var result = chartMonitor(args, {resource: resource[index]});
            if (result == false) {
                return false;
            }
        });
    };

    return exe(args);
};