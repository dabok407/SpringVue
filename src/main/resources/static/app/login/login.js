/*
document.write('<script src="/common/encrypt/sha256.js"></script>');
*/

(function ($) {
    bindLoginSubmit();
})(jQuery);

function bindLoginSubmit() {
    $("#loginForm").submit(function (e) {
        e.preventDefault();
        var form = this;
        var options = {
            method: form.method
            /*, data: $(form).serialize()*/
            , data: {
                "username" :  $("input[name='username']").val()
                ,"password" : CryptoJS.SHA256($("input[name='password']").val())+""
            }
            , beforeSend : function(xhr) {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                xhr.setRequestHeader(header, token);
            }
            , success : function(data, textStatus, jqXHR){
                common.href("");
            }
            , error : function(jqXHR, textStatus, errorThrown){
                var returnData = JSON.parse(jqXHR.responseText);
                var errorCode = returnData["errorCode"];
                var errorMsg = returnData["errorMsg"];
                alert(errorMsg);
            }
        };
        $.ajax(form.action, options)
    })


}



