/** *************Init JS*********************
	
    TABLE OF CONTENTS
	---------------------------
	1.Ready function
	2.Load function
	3.Full height function
	4.deepor function
	5.Chat App function
	6.Resize function
 ** ***************************************/
 
 "use strict"; 
/*****Ready function start*****/
$(document).ready(function(){
	deepor();
	/*Disabled*/
	$(document).on("click", "a.disabled,a:disabled",function(e) {
		 return false;
	});
});
/*****Ready function end*****/

/*****Load function start*****/
$(window).on("load",function(){
	$(".preloader-it").delay(500).fadeOut("slow");
});
/*****Load function* end*****/

/*Variables*/
var height,width,
$wrapper = $(".hk-wrapper");

/***** deepor function start *****/
var deepor = function(){
	
	/*Feather Icon*/
	var featherIcon = $('.feather-icon');
	if( featherIcon.length > 0 ){
		feather.replace();
	}
	
	
	/*Counter Animation*/
	var counterAnim = $('.counter-anim');
	if( counterAnim.length > 0 ){
		counterAnim.counterUp({ delay: 10,
        time: 1000});
	}

	
	/*Navbar Collapse Animation*/
	var navbarNavCollapse = $('.hk-nav .navbar-nav  li');
	var navbarNavAnchor = '.hk-nav .navbar-nav  li a';
	$(document).on("click",navbarNavAnchor,function (e) {
		if ($(this).attr('aria-expanded') === "false")
				$(this).blur();
			$(this).parent().siblings().find('.collapse').collapse('hide');
			$(this).parent().find('.collapse').collapse('hide');
	});



	/*Navbar Toggle*/
	$(document).on('click', '#navbar_toggle_btn', function (e) {
		$wrapper.toggleClass('hk-nav-toggle');
		$(window).trigger( "resize" );
		return false;
	});
    $(document).on('click', '#hk_nav_backdrop,#hk_nav_close', function (e) {
        $wrapper.removeClass('hk-nav-toggle');
        return false;
    });


	/*Refresh Init Js*/
	var refreshMe = '.refresh';
	$(document).on("click",refreshMe,function (e) {
		var panelToRefresh = $(this).closest('.card').find('.refresh-container');
		var dataToRefresh = $(this).closest('.card').find('.panel-wrapper');
		var loadingAnim = panelToRefresh.find('.la-anim-1');
		panelToRefresh.show();
		setTimeout(function(){
			loadingAnim.addClass('la-animate');
		},100);
		function started(){} //function before timeout
		setTimeout(function(){
			function completed(){} //function after timeout
			panelToRefresh.fadeOut(800);
			setTimeout(function(){
				loadingAnim.removeClass('la-animate');
			},800);
		},1500);
		  return false;
	});

	
};
/***** deepor function end *****/

/***** Full height function start *****/
var setHeightWidth = function () {
	height = window.innerHeight;
	width = window.innerWidth;
	$('.full-height').css('height', (height));
	$('.hk-pg-wrapper').css('min-height', (height));
};
/***** Full height function end *****/


/***** Resize function start *****/
$(window).on("resize", function () {
	setHeightWidth();
});
$(window).trigger( "resize" );
/***** Resize function end *****/

