
var A = {
 ajax_get:function(url,callback){
   A.loading.start();
    $.ajax({
        type: "GET",
        async:true,
        cache:false,
        url: url,
        success:function(result, textStatus, jqXHR){
            A.loading.cancel();
            callback(result);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            A.loading.cancel();
            try{
                var text = jQuery.parseJSON(jqXHR.responseText);
                layer.msg(text.fault.message);
            }catch(err){
                layer.msg("请求失败~~");
            }
        }
    });
},

ajax_post: function(url, data ,callback) {
    A.loading.start();
    $.ajax({
        type: "POST",
        async:true,
        cache:false,
        url: baseUrl+url,
        contentType: "application/json;charset=utf-8",
        dataType: "JSON",
        data: JSON.stringify(data),
        timeout: this.ajax_timeout,
        success: function(result) {
            A.loading.cancel();
            callback(result);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            A.loading.cancel();
            try{
                var text = jQuery.parseJSON(jqXHR.responseText);
                console.log(jqXHR.responseText);
                layer.msg(text.fault.message);
            }catch(err){
                layer.msg('请求失败~~');
            }
        },
        complete: function(xhr,status) {
            var sessionStatus = xhr.getResponseHeader('sessionstatus');
            if(sessionStatus == 'timeout') {
                var yes = confirm('由于您长时间没有操作, session已过期, 请重新登录.');
                if (yes) {
                    location.href = '/admin/login.html';
                }
            }
        }
    });
},

loading:{
    start:function(){
        $("#fakeloader").loading();
    },
     cancel:function(){
           $("#fakeloader").cancel();
           $("#fakeloader0").html("<div id='fakeloader'></div>");
    }
}
}