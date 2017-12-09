/**
 * myProgress.js
 * Version: 1.0
 * Author: Mahuaide
 * Download:
 * You may use this script for free
 */
;
(function ($) {
    if (typeof($.fn.myProgress) != 'undefined') {
        return false;
    }
    $.fn.myProgress = function (options) {
        initOptions(options);
        $(this).each(function () {
            var this_ = $(this);
            var $percent = $(this).find("div.percent-show>span");
            var progress_in = $(this).find("div.progress-in");
            initCss(options, $(this));
            var t = setInterval(function () {
                $percent.html(options.percent)
            }, options.speed / 100);
            progress_in.animate({
                width: options.percent + "%"
            }, options.speed, function () {
                clearInterval(t);
                t = null;
                $percent.html(options.percent);
                options.percent == 100 && progress_in.css("border-radius", 0);
            });
        });
        return $(this);
    }

    function initOptions(options) {
        (!options.hasOwnProperty("speed") || isNaN(options.speed)) && (options.speed = 1000);
        (!options.hasOwnProperty("percent") || isNaN(options.percent)) && (options.percent = 100); 
        !options.hasOwnProperty("direction") && (options.direction = 'left'); 
    }

    function getStatuasClass(status) {
        if(status == "0"){
            return "normal-progress";
        }
        if(status == "1"){
            return "waring-progress";
        }
        if(status == "2"){
            return "danger-progress";
        }
        return "danger-progress";
    }
    
    function initCss(options, obj) { 
        var statusClass = getStatuasClass(options.status);
        obj.find("div.progress-in").addClass(statusClass);
        // obj.css({
        //     "width": options.width,
        //     "height": options.height
        // }).find("div.percent-show").css({
        //     "lineHeight": options.lineHeight,
        //     "fontSize": options.fontSize
        // });
        // if(options.direction =="right"){
        //     obj.find("div.progress-in").addClass("direction-right");
        // }else{
        //     obj.find("div.progress-in").addClass("direction-left");
        // }
    }
})(jQuery);
