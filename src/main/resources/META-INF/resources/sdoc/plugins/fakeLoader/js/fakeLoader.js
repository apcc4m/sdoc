/*--------------------------------------------------------------------
 *JAVASCRIPT "FakeLoader.js"
 *Version:    1.1.0 - 2014
 *author:     João Pereira
 *website:    http://www.joaopereira.pt
 *Licensed MIT 
-----------------------------------------------------------------------*/
(function ($) {
    $.fn.cancel = function() {
        //The target
        var el = $(this);
        $(el).fadeOut();
    }
    $.fn.loading = function(options) {
        //Defaults
        var settings = $.extend({
            pos:'fixed',// Default Position
            top:'0px',  // Default Top value
            left:'0px', // Default Left value
            width:'100%', // Default width 
            height:'100%', // Default Height
            zIndex: '9999',  // Default zIndex 
            bgColor: '#000', // Default background color
            imagePath:'' // Default Path custom image
        }, options);
        //The target
        var el = $(this);
        var winW = $(window).width()/2;
        var winH = $(window).height()/2;
        var spinner02 = '<div class="fl spinner2" style="position:absolute;left:'+winW+'px;top:'+winH+'px;"><div class="spinner-container container1"><div class="circle1"></div><div class="circle2"></div><div class="circle3"></div><div class="circle4"></div></div><div class="spinner-container container2"><div class="circle1"></div><div class="circle2"></div><div class="circle3"></div><div class="circle4"></div></div><div class="spinner-container container3"><div class="circle1"></div><div class="circle2"></div><div class="circle3"></div><div class="circle4"></div></div></div>';
		var spinner03 = '<div class="fl spinner3" style="position:absolute;left:'+winW+'px;top:'+winH+'px;"><div class="dot1"></div><div class="dot2"></div></div>';
		var spinner04 = '<div class="fl spinner4" style="position:absolute;left:'+winW+'px;top:'+winH+'px;"></div>';
        el.html(spinner03);
        //Init styles
        var initStyles = {
            'position':settings.pos,
            'width':settings.width,
            'height':settings.height,
            'top':settings.top,
            'left':settings.left
        };
        //Apply styles
        el.css(initStyles);
        //Return Styles 
        return this.css({
            'backgroundColor':settings.bgColor,
            'filter':'alpha(Opacity=30)',
            '-moz-opacity':'0.3',
            'opacity': '0.3',
            'zIndex':settings.zIndex
        });
        
    }; 


}(jQuery));




