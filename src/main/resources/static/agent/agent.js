function ajaxGet(url, success) {
    showLoading();
    $.ajax({
        url: url,
        type: 'GET',
        timeout: 10000,
        success: success,
        complete: function (xhr, status) {
            closeLoading();
            if(status=='timeout'){
                alterToast('请求超时');
            }
        },
        error: function (xhr, status) {
            closeLoading();
            alterToast('服务器出错');
        }
    });
}

function ajaxPost(url, params, success) {
    showLoading();
    $.ajax({
        url: url,
        type: 'POST',
        data: params,
        dataType: 'JSON',
        timeout: 10000,
        success: success,
        complete: function (xhr, status) {
            closeLoading();
            if(status=='timeout'){
                alterToast('请求超时');
            }
        },
        error: function (xhr, status) {
            closeLoading();
            alterToast('服务器出错');
        }
    });
}

function alter(message) {
    $('#alterModalMessage').html(message);
    $('#alterModal').modal('show');
}

function messageReload() {
    var pageNo = $(".page-item.active.disabled").children(".page-link").text();
    if(pageNo.length==0){
        var url = $('#viewSelect').val();
        indexView(url);
    }else {
        turnPage(pageNo)
    }
}

function agentConfirm(message, confirm) {
    $('#confirmModalMessage').html(message);
    $('#confirmButton').click(confirm);
    $('#confirmModal').modal('show');
}

function indexView(url) {
    ajaxGet(url,function (response) {
        $("#homeView").html(response);
    })
}
function alterToast(info) {
    var color = '#e54533';
    var textColor = '#e5eed0';
    alterToastBasic(info,color,textColor)
}

function alterGreen(info) {
    var color = '#36aeff';
    var textColor = '#260706';
    alterToastBasic(info,color,textColor)
}

function alterToastBasic(info, color, textColor) {
    $.toast({
        text : info,
        showHideTransition : 'fade',
        bgColor : color,
        textColor : textColor,
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
function withdrawPost() {
    var balanceAmount = $('#balanceAmount').text();
    var withdrawAmount = $('#withdrawAmount').val();
    var withdrawAccount = $('#withdrawAccount').val();
    var withdrawName = $('#withdrawName').val();
    var withdrawType = $('#withdrawType').val();
    var withdrawRemark = $('#withdrawRemark').val();
    withdrawAmount = Number(withdrawAmount);
    balanceAmount = Number(balanceAmount);
    if(typeof withdrawAmount != 'number'||withdrawAmount<=0){
        alterToast("请输入正确的提现金额");
        return;
    }
    if(withdrawAccount.length==0){
        alterToast("请输入正确的账号");
        return;
    }
    if(withdrawName.length==0){
        alterToast("请输入真实姓名");
        return;
    }
    if(withdrawAmount>balanceAmount){
        alterToast("余额不足");
        return;
    }
    var params = {account:withdrawAccount,name:withdrawName,type:withdrawType,amount:withdrawAmount,remark:withdrawRemark};
    showLoading();
    ajaxPost('/summary/add-withdraw',params,function (response) {
        closeLoading();
        if(response.code==0){
            $('#withdrawModal').modal('hide');
            alter("申请成功，订单号："+response.message);
        }else {
            alterToast(response.message);
        }
    })

}
function turnPage(pageNo) {
    var url = $('#viewSelect').val();
    url = url+'?pageNo='+pageNo;
    var queryStr = $('#queryStr').val();
    if(typeof queryStr == 'string' &&queryStr.length>0){
        url = url+'&queryStr='+queryStr;
    }
    var queryDate = $('#queryDate').val();
    if(typeof queryDate == 'string' && queryDate.length>0){
        url = url+'&queryDate='+queryDate;
    }
    indexView(url);
}

function showLoading() {
    $(".loading").css('display','block');
}

function closeLoading() {
    $(".loading").css('display','none');
}

function alertInfo(info) {
    var alertInfo = $("#alertInfo");
    alertInfo.text(info);
    alertInfo.attr('hidden',false);
    setTimeout(function () {
        alertInfo.attr('hidden',true);
    },2000)
}




