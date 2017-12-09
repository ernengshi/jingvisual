$(document).ready(function () {
    $('.iaas-setting').click(function () {
        if (!$(this).hasClass("active")) {
            window.location.href = "/setting/iaas";
        }
    });
    $('.daas-setting').click(function () {
        if (!$(this).hasClass("active")) {
            window.location.href = "/setting/daas";
        }
    });
});