$(function () {
    var args = {
        resource: ["cpu", "memory"],
        title: function (resource) {
            if (resource == "cpu") {
                return "cpu信息";
            } else if (resource == "memory") {
                return  "内存信息";
            }
        },
        chartDiv: function (resource) {
            if (resource == "cpu") {
                return "host_CH211-241_cpuchart";
            } else if (resource == "memory") {
                return  "host_CH211-241_memorychart";
            }
        },
        charttype: function (resource) {
            if (resource == "cpu") {
                return "area";
            } else if (resource == "memory") {
                return  "area";
            }
        },
        xtext: function (resource) {
            return  "";
        },
        ytext: function (resource) {
            if (resource == "cpu") {
                return "";
            } else if (resource == "memory") {
                return  "";
            }
        },
        yformatter: function (resource, yvalue) {
            if (resource == "memory") {
                return (yvalue / 1024 / 1024 / 1024).toFixed(2) + " GB ";
            } else if (resource == "cpu") {
                return yvalue + " % ";
            }
        },
        tooltip: function (resource, chart) {
            if (resource == "cpu") {
                /*
                 var content = '<span style="font-size: 12px;">' + Highcharts.dateFormat("%Y-%m-%d %H:%M:%S",chart.x )+ '</span><br/>';
                 for (var i = 0; i < chart.points.length; i++) {
                 var yVal = (chart.points[i].y) ? (chart.points[i].y)+"%" : 0+"%";
                 content += '<span style="color: ' + chart.points[i].series.color + '">' + chart.points[i].series.name + '</span>: ' + yVal + '<br/>';
                 };
                 */
                return "";
            } else if (resource == "memory") {
                /*
                 var content = '<span style="font-size: 12px;">' + Highcharts.dateFormat("%Y-%m-%d %H:%M:%S",chart.x )+ '</span><br/>';
                 var memoryTotalVal = ((chart.points[0].y===0 ||chart.points[0].y) && (chart.points[1].y===0 ||chart.points[1].y) && (chart.points[2].y===0 ||chart.points[2].y)) ? cloudStack.converters.convertBytes(chart.points[0].y+chart.points[1].y+chart.points[2].y):0+"KB" ; 
                 content += '<span style="color: #CD5C5C">'+_l('内存总量')+'</span>: ' + memoryTotalVal + '<br/>';
                 for (var i = 0; i < chart.points.length; i++) {
                 var yVal = (chart.points[i].y) ? cloudStack.converters.convertBytes(chart.points[i].y):0+"KB" ;
                 content += '<span style="color: ' + chart.points[i].series.color + '">' + chart.points[i].series.name + '</span>: ' + yVal + '<br/>';
                 };
                 */
                return "";
            }
        },
        chartData: function (args) {
            //var urlParam = "http://192.168.211.252:8096/client/api?command=listHostResourceGraphStats%26hostid=32666bd1-a6c3-4e28-bcca-4a40917c20e8%26timespan=HOUR%26size=100%26response=json";
            var urlParam = "http://192.168.37.250:8096/client/api?command=listHostResourceGraphStats%26hostid=5e221f04-3a7d-4a51-9962-601f33a90a49%26timespan=HOUR%26size=100%26response=json";
            $.ajax({
                url: "/showZone?urlParam=" + urlParam,
                dataType: "json",
                async: false,
                success: function (json) {
                    var hostChart = json.listhostresourcestatsresponse.hostresourcestats;
                    var available = [];
                    var resource = args.resource;
                    var date = [];
                    if (resource == "cpu") {
                        var cpuusage = [];
                        $.each(hostChart, function () {
                            available.push(this.available);
                            cpuusage.push(this.cpuusage);
                            date.push(this.date);
                        });
                        args.response.success({
                            chartTypeData: [{
                                    data: cpuusage,
                                    typetext: "CPU占用率",
                                }],
                            available: available,
                            date: date,
                        });
                    } else if (resource == "memory") {
                        var ramfree = [];
                        var rambyvm = [];
                        var rambyplat = [];
                        $.each(hostChart, function () {
                            ramfree.push(this.ramfree);
                            rambyvm.push(this.rambyvm);
                            rambyplat.push(this.rambyplat);
                            available.push(this.available);
                            date.push(this.date);
                        });
                        args.response.success({
                            chartTypeData: [{
                                    data: ramfree,
                                    typetext: "空闲内存",
                                },
                                {
                                    data: rambyvm,
                                    typetext: "虚拟机使用内存",
                                },
                                {
                                    data: rambyplat,
                                    typetext: "白金加速占用内存",
                                    color: "orange",
                                }],
                            available: available,
                            date: date,
                        });
                    }
                }
            });
        },
        chartCurrentApi: function (args) {
            //var urlParam = "http://192.168.211.252:8096/client/api?command=listHostResourceGraphStats%26hostid=32666bd1-a6c3-4e28-bcca-4a40917c20e8%26timespan=HOUR%26size=100%26response=json%26fetchlatest=true";
            var urlParam = "http://192.168.37.250:8096/client/api?command=listHostResourceGraphStats%26hostid=5e221f04-3a7d-4a51-9962-601f33a90a49%26timespan=HOUR%26size=100%26response=json%26fetchlatest=true";
            $.ajax({
                url: "/showZone?urlParam=" + urlParam,
                dataType: "json",
                async: false,
                success: function (json) {
                    var resource = args.resource;
                    var hostChart = json.listhostresourcestatsresponse.hostresourcestats;
                    if (resource == "cpu") {
                        args.response.success({
                            chartCurrentData: [hostChart[0].cpuusage],
                            currentAvailable: hostChart[0].available,
                            currentDate: hostChart[0].date,
                        });
                    } else if (resource == "memory") {
                        args.response.success({
                            chartCurrentData: [hostChart[0].ramfree, hostChart[0].rambyvm, hostChart[0].rambyplat],
                            currentAvailable: hostChart[0].available,
                            currentDate: hostChart[0].date,
                        });
                    }
                }
            });
        }
    };

    monitorChart(args);
});