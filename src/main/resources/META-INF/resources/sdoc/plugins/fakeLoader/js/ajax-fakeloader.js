var A = {
    ajax_get : function(url, callback,slient,async) {
        if(A.checkUndefined(slient)){
            slient=false;
        }
        if(A.checkUndefined(async)){
            async=true;
        }
        if(!slient){
            A.loading.start();
        }
        $.ajax({
            type : "GET",
            async : async,
            cache : false,
            url : url,
            success : function(result, textStatus, jqXHR) {
                if(!slient){
                    A.loading.cancel();
                }
                callback(result);
            },
            error : function(jqXHR, textStatus, errorThrown) {
                if(!slient){
                    A.loading.cancel();
                }
                try {
                    var data = jQuery.parseJSON(jqXHR.responseText);
                    if(!slient){
                        layer.msg("请求失败~~", {icon:5,shift:6});
                    }
                } catch (err) {
                    if(!slient){
                        layer.msg("请求失败~~", {icon:5,shift:6});
                    }
                }
            }
        });
    },

    ajax_post : function(url, data, callback,slient,async) {
        if(A.checkUndefined(slient)){
            slient=false;
        }
        if(A.checkUndefined(async)){
            async=true;
        }
        if(!slient){
            A.loading.start();
        }
        $.ajax({
            type : "POST",
            async : async,
            cache : false,
            url : url,
            contentType : "application/json",
            dataType : "JSON",
            data : JSON.stringify(data),
            timeout : this.ajax_timeout,
            success : function(result) {
                if(!slient){
                    A.loading.cancel();
                }
                callback(result);
            },
            error : function(jqXHR, textStatus, errorThrown) {
                if(!slient){
                    A.loading.cancel();
                }
                try {
                    var data = jQuery.parseJSON(jqXHR.responseText);
                    if(!slient){
                        layer.msg("请求失败~~", {icon:5,shift:6});
                    }
                } catch (err) {
                    if(!slient){
                        layer.msg("请求失败~~", {icon:5,shift:6});
                    }
                }
            },
        });
    },
    checkUndefined:function(reValue){
        if (typeof(reValue) == "undefined") {
            return true;
        }else{
            return false;
        }
    },
    loading : {
        start : function() {
            fakeLoader.loading($("#fakeloader"));
        },
        cancel : function() {
            fakeLoader.cancel($("#fakeloader"));
        }
    }
}


var fakeLoader = {
    cancel : function(obj) {
        var el = $(obj);
        $(el).fadeOut();
    },
    start : function(obj) {
        var el = $(obj);
        $(el).fadeIn();
    },
    loading : function(obj,options) {
        var el = $(obj);
        if(el.html()!=''){
            $(el).fadeIn();
            return;
        }
        var settings = $.extend({
            pos:'fixed',// Default Position
            top:'0px',  // Default Top value
            left:'0px', // Default Left value
            width:'100%', // Default width 
            height:'100%', // Default Height
            zIndex: '999999999',  // Default zIndex
            bgColor: '#000', // Default background color
            imagePath:'' // Default Path custom image
        }, options);
        //The target
        var el = $(obj);
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
        el.css(initStyles);
        var result = obj.css({
            'backgroundColor':settings.bgColor,
            'filter':'alpha(Opacity=30)',
            '-moz-opacity':'0.3',
            'opacity': '0.3',
            'zIndex':settings.zIndex
        });
        return result

    }
}