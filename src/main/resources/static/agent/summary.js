function searchAgent() {
    var url = $('#viewSelect').val();
    var mobile = $('#queryByMobile').val();
    url = url+'?mobile='+mobile;
    indexView(url);
}

function changeAgent(agentId, type) {
    var msg = type==1?'禁用':'启用';
    agentConfirm('确定'+msg+'该代理？',function () {
        $('#confirmModal').modal('hide');
        $('#confirmButton').unbind();
        var params = {agentId: agentId, type: type};
        ajaxPost('/summary/change-agent',params,function (data) {
            if(data.code==0){
                alter('用户已'+msg);
            }else {
                alter(data.message);
            }
        });
    });
}

function addAgent() {
    var nickName = $("#nickName").val();
    var loginName = $("#loginName").val();
    var mobile = $("#mobile").val();
    var ratio = $("#ratio").val();
    var password = $("#password").val();
    var password1 = $("#password1").val();
    if(loginName.length==0){
        alterToast('登陆名为空');
        return;
    }
    var check = baseCheck(nickName,mobile,ratio);
    if(check!=0){
        return;
    }
    if(password.length==0){
        alterToast('请输入密码');
        return;
    }
    if(password1.length==0){
        alterToast('请再次输入密码');
        return;
    }
    if(password != password1){
        alterToast('两次输入密码不一致');
        return;
    }

    var params = {nickName:nickName,loginName:loginName,mobile:mobile,ratio:ratio,password:password};
    ajaxPost('/summary/add-agent',params,function (data) {
        if(data.code==0){
            $('#addAgentModal').modal('hide');
            alter(data.message);
        }else{
            alterToast(data.message);
        }
    });
}

function baseCheck(nickName,mobile,ratio) {
    if(nickName.length==0){
        alterToast('昵称为空');
        return 1;
    }
    if(mobile.length==0){
        alterToast('联系电话为空');
        return 1;
    }
    if(ratio.length==0){
        alterToast('分佣比例为空');
        return 1;
    }
    var reg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
    if(reg.test(mobile)==false){
        alterToast("请输入正确的联系电话");
        return 1;
    }
    if(ratio<=0||ratio>=100){
        alterToast("分佣比例应在1~100之间");
        return 1;
    }
    return 0;
}
