$(document).ready(function () {
    var daasInfo = getDaas();
    var daasIp = (daasInfo.ip ? daasInfo.ip : "");
    var daasId = (daasInfo.id ? daasInfo.id : "");
    $("#daasIp").val(daasIp);
    $("#daasId").val(daasId);
    if (daasIp == "") {
        $("#daas-submit").show();
        $(".daas-modify-delete").hide();
    } else {
        $("#daas-submit").hide();
        $(".daas-modify-delete").show();
    }
    $("#daas-submit").click(function () {
        daasSubmit();
    });
    $("#daas-modify").click(function () {
        daasModify();
    });
    $("#daas-delete").click(function () {
        daasDelete();
    });
})