
//const baseUrl= "http://192.168.7.2:8080/";
const baseUrl = "https://" + window.location.hostname + ":" + window.location.port + "/";
function getIp() {
    var ip = ""
    $.ajax({
        url: baseUrl + "iaas",
        dataType: "json",
        async: false,
        success: function (json) {
            ip = json.ip;
        }
    });
    return ip;
}
function getZone() {
    var zones_data = {"id": "", "name": ""};
    $.ajax({
        url: baseUrl + "zones_data/1",
        dataType: "json",
        async: false,
        success: function (json) {
            var resultList = json.resultList;
            if (resultList.length > 0) {
                zones_data = resultList[0];
            }
        }
    });
    return zones_data;
}
function getZoneResource(path) {
    var zoneResource = [];
    $.ajax({
        url: baseUrl + path,
        dataType: "json",
        async: false,
        success: function (json) {
            zoneResource = json;
        }
    });
    return zoneResource;
}

function getShowZoneData(urlParam) {
    var capacities = [];
    $.ajax({
        url: baseUrl + "/showZone?urlParam=" + urlParam,
        dataType: "json",
        async: false,
        success: function (json) {
            capacities = json.listcapacityresponse.capacity;
        }
    });
    return capacities;
}