
function fullScreen(){
	var isScroll = haveScroll();
	if(isScroll.scrollY == false){
		$("body").css({"overflow-y":"hidden"});
	}
	else{
		$("body").css({"overflow-y":"auto"});
	}
	if(!$(".fullScreen").hasClass("full")){
		$(".fix-sidebar-nav").addClass("sidebar-nav-hidden");
		$(".navbar-inner").addClass("navbar-inner-hidden");
		$(".auto-content").addClass("auto-content-fullscreen");
		$("footer").addClass("hidden");
		$(".fullScreen").addClass("full");
		$(".auto-content .rightcontent").css({
    		"min-height": $(window).height()-25
    	})
	    if($(".platform-setting .platform-setting-content").size()>0){
			$(".platform-setting .platform-setting-content").css({
		    	"min-height": $(window).height()-25
		    })
	    }
	    if($(".custom-height").size()>0){
			$(".custom-height").css({
		    	"min-height": $(window).height()-25
		    })
	    }
	    if($(".device-tree .devicetree").size()>0){
			$(".device-tree .devicetree").css({
		    	"height": $(window).height()-25
		    })
	    }
	}
	else{
		$(".auto-content .rightcontent").css({
    		"min-height": $(window).height()-105
    	})
	    if($(".platform-setting .platform-setting-content").size()>0){
			$(".platform-setting .platform-setting-content").css({
		    	"min-height": $(window).height()-105
		    })
	    }
	    if($(".custom-height").size()>0){
			$(".custom-height").css({
		    	"min-height": $(window).height()-105
		    })
	    }
	    if($(".device-tree .devicetree").size()>0){
			$(".device-tree .devicetree").css({
		    	"height": $(window).height()-105
		    })
	    }
		$(".fix-sidebar-nav").removeClass("sidebar-nav-hidden");
		$(".navbar-inner").removeClass("navbar-inner-hidden");
		$(".auto-content").removeClass("auto-content-fullscreen");
		$("footer").removeClass("hidden");
		$(".fullScreen").removeClass("full");
	}  
}

function haveScroll(el) {
 // test targets
 var elems = el ? [el] : [document.documentElement, document.body];
 var scrollX = false, scrollY = false;
 for (var i = 0; i < elems.length; i++) {
     var o = elems[i];
     // test horizontal
     var sl = o.scrollLeft;
     o.scrollLeft += (sl > 0) ? -1 : 1;
     o.scrollLeft !== sl && (scrollX = scrollX || true);
     o.scrollLeft = sl;
     // test vertical
     var st = o.scrollTop;
     o.scrollTop += (st > 0) ? -1 : 1;
     o.scrollTop !== st && (scrollY = scrollY || true);
     o.scrollTop = st;
 }
 // ret
 return {
     scrollX: scrollX,
     scrollY: scrollY
 };
}
function getContainerHeight(){
	$(".auto-content .rightcontent").css({
    	"min-height": $(window).height()-105
    })
    if($(".platform-setting .platform-setting-content").size()>0){
		$(".platform-setting .platform-setting-content").css({
	    	"min-height": $(window).height()-105
	    })
    }
    if($(".custom-height").size()>0){
		$(".custom-height").css({
	    	"min-height": $(window).height()-105
	    })
    }
    if($(".device-tree .devicetree").size()>0){
		$(".device-tree .devicetree").css({
	    	"height": $(window).height()-105
	    })
    }
}
getContainerHeight();