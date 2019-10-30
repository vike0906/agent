function ajaxGet(url, success) {
    $.ajax({
        url: url,
        type: 'GET',
        timeout: 10000,
        success: success,
        complete: function (xhr, status) {
            if(status=='timeout'){
                console.log('请求超时');
            }
        },
        error: function (xhr, status) {
            /*$.hideLoading();
            $.toast('服务器出错', "cancel");*/
            console.log('服务器出错');
        }
    });
}

function ajaxPost(url, params, success) {
    $.ajax({
        url: url,
        type: 'POST',
        data: params,
        dataType: 'JSON',
        timeout: 10000,
        success: success,
        complete: function (xhr, status) {
            if(status=='timeout'){
                alter('请求超时');
            }
        },
        error: function (xhr, status) {
            alter('请求出错');
        }
    });
}

function alter(message) {
    $('#alterModalMessage').html(message);
    $('#alterModal').modal('show');
}

function messageReload() {
    location.reload();
}

function agentConfirm(message, confirm) {
    $('#confirmModalMessage').html(message);
    $('#confirmButton').click(confirm);
    $('#confirmModal').modal('show');
}

function indexView(url) {
    ajaxGet(url,function (response) {
        $(".col-xl-12").html(response);
    })
}
function alterToast(info) {
    $.toast({
        text : info,
        showHideTransition : 'fade',
        bgColor : '#ee5130',
        textColor : '#e5eed0',
        class: 'jq-toast-primary',
        allowToastClose : false,
        hideAfter : 2000,
        stack : 5,
        textAlign : 'center',
        position : 'top-center'
    });
}
function changePassword() {
    var oldPassword = $("#oldPassword").val();
    var newPassword = $("#newPassword").val();
    var newPassword1 = $("#newPassword1").val();
    if(oldPassword.length==0){
        alterToast('原密码为空');
        return;
    }
    if(newPassword.length==0){
        alterToast('新密码为空');
        return;
    }
    if(newPassword1.length==0){
        alterToast('请再次输入新密码');
        return;
    }
    if(newPassword1!=newPassword){
        alterToast('两次输入新密码不同');
        return;
    }
    var parpams = {oldPassword:oldPassword,newPassword:newPassword};
    ajaxPost('/change/password',parpams,function (response) {
        if(response.code==0){
            $('#changePasswordModal').modal('hide');
            alterToast('密码修改成功，请重新登录');
            setTimeout(function () {
                window.location.href="/logout";
            },2000);
        }else{
            alterToast(response.message);
        }
    });
}




