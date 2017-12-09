/* global getZone */

$(function () {
    drawZoneChart("week");
    getShow();
    letsGOOnduty();
    setInterval(refresh, 80000);
});

function refresh() {
    drawZoneChart("week");
    getShow();
    letsGOOnduty();
}

function parseData(value) {
    if (value.indexOf(".") !== -1) {
        var numindex = parseInt(value.indexOf("."));
        var head = value.substring(0, numindex);
        return head;
    } else {
        return parseInt(value);
    }
}

function convertHz(hz) {
    if (hz == null)
        return "";
    if (hz < 1000) {
        return hz + " MHz";
    } else {
        return (hz / 1000).toFixed(2) + " GHz";
    }
}

function convertBytes(bytes) {
    if (bytes < 1024 * 1024) {
        return (bytes / 1024).toFixed(2) + " KB";
    } else if (bytes < 1024 * 1024 * 1024) {
        return (bytes / 1024 / 1024).toFixed(2) + " MB";
    } else if (bytes < 1024 * 1024 * 1024 * 1024) {
        return (bytes / 1024 / 1024 / 1024).toFixed(2) + " GB";
    } else {
        return (bytes / 1024 / 1024 / 1024 / 1024).toFixed(2) + " TB";
    }
}

function getShow() {
    var zoneid = getZone().id;
    var ip = getIp();
    var port = '8096';
    var urlParam = "http://" + ip + ":" + port + "/client/api?command=listCapacity%26response=json&zoneid=" + zoneid;
    var capacities = getShowZoneData(urlParam);
    var ordercapacities = new Array(capacities.length);
    var orderothers = [];
    $(capacities).each(function (index) {
        if (this.type == 3) {
            capacities.splice(index, 1);
            ordercapacities = new Array(capacities.length - 1);
        }
    });

    $(capacities).each(function (index) {
        if (this.type == 1) {
            //CPU
            ordercapacities[0] = this;
        } else if (this.type == 0) {
            //内存
            ordercapacities[1] = this;
        } else if (this.type == 2) {
            //主存储
            ordercapacities[2] = this;
        } else if (this.type == 6) {
            //辅助存储
            ordercapacities[3] = this;
        } else if (this.type == 4) {
            //公用IP地址
            ordercapacities[4] = this;
        } else if (this.type == 5) {
            //管理类IP地址
            ordercapacities[5] = this;
        } else if (this.type == 7) {
            //VLAN
            ordercapacities[6] = this;
        } else if (this.type == 8) {
            //直接IP
            ordercapacities[7] = this;
        }
    });
    $.each(ordercapacities, function (index, ordercapacitie) {
        if (index >= 8) {
            return false;
        }
        var capacityused = ordercapacitie.capacityused + "条";
        var capacitytotal = ordercapacitie.capacitytotal + "条";
        if (ordercapacitie.type == 1) {
            capacityused = convertHz(ordercapacitie.capacityused);
            capacitytotal = convertHz(ordercapacitie.capacitytotal);
        }
        if (ordercapacitie.type == 0 || ordercapacitie.type == 2 || ordercapacitie.type == 6) {
            capacityused = convertBytes(ordercapacitie.capacityused);
            capacitytotal = convertBytes(ordercapacitie.capacitytotal);
        }
        if (ordercapacitie.type == 7) {
            capacityused = ordercapacitie.capacityused + "个";
            capacitytotal = ordercapacitie.capacitytotal + "个";
        }
        $('.resource-progross .all-resouce-span' + index).html(capacityused + " / " + capacitytotal);
        var percentused = parseData(ordercapacitie.percentused);
        var capacities_status = getResouceStatus(percentused);
        $("#div" + (index + 1)).myProgress({speed: 500, percent: percentused, status: capacities_status});
    });
}

function getResouceStatus(percentused) {
    if (percentused <= 60) {
        return 0;
    } else if (percentused > 60 && percentused < 80) {
        return 1;
    } else if (percentused >= 80) {
        return 2;
    }
}