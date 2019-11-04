function searchAgent() {
    var url = $('#viewSelect').val();
    var queryStr = $('#queryStr').val();
    url = url+'?queryStr='+queryStr;
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

function copyUrl(id) {
    var url  = $("#"+id).attr('title');
    var element = $("<textarea>" + url + "</textarea>");
    $("body").append(element);
    element[0].select();
    document.execCommand("Copy");
    element.remove();
    alterGreen("链接已复制到粘贴板");
}

function addAgent() {
    var nickName = $("#nickName").val();
    var loginName = $("#loginName").val();
    var mobile = $("#mobile").val();
    var ratio = $("#ratio").val();
    var password = $("#password").val();
    var password1 = $("#password1").val();
    if(nickName.length==0){
        alterToast('昵称为空');
        return;
    }
    if(loginName.length==0){
        alterToast('登陆名为空');
        return;
    }
    if(mobile.length==0){
        alterToast('联系电话为空');
        return;
    }
    if(ratio.length==0){
        alterToast('分佣比例为空');
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

    var reg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
    if(reg.test(mobile)==false){
        alterToast("请输入正确的联系电话");
        return;
    }
    if(ratio<=0||ratio>=100){
        alterToast("分佣比例应在1~100之间");
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

function editAgentModal(id) {
    $('#editAgentId').val(id);
    $('#editAgentModal').modal('show');

    var editAgent = $('#editAgent'+id);
    var nickName = editAgent.attr('nickName');
    var loginName = editAgent.attr('loginName');
    var mobile = editAgent.attr('mobile');
    var ratio = editAgent.attr('ratio');


    $("#editNickName").val(nickName);
    $("#editLoginName").val(loginName);
    $("#editLoginName").attr('readOnly',true);
    $("#editMobile").val(mobile);
    $("#editRatio").val(ratio);
}

function editAgent() {
    var id = $("#editAgentId").val();
    var nickName = $("#editNickName").val();
    var mobile = $("#editMobile").val();
    var ratio = $("#editRatio").val();
    if(nickName.length==0){
        alterToast('昵称为空');
        return;
    }
    if(mobile.length==0){
        alterToast('联系电话为空');
        return;
    }
    if(ratio.length==0){
        alterToast('分佣比例为空');
        return;
    }
    var reg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
    if(reg.test(mobile)==false){
        alterToast("请输入正确的联系电话");
        return;
    }
    if(ratio<=0||ratio>=100){
        alterToast("分佣比例应在1~100之间");
        return;
    }

    var params = {id:id,nickName:nickName,mobile:mobile,ratio:ratio};
    ajaxPost('/summary/edit-agent',params,function (data) {
        if(data.code==0){
            $('#editAgentModal').modal('hide');
            alter(data.message);
        }else{
            alterToast(data.message);
        }
    });
}

function queryByDate() {
    var url = $('#viewSelect').val();
    var queryDate = $('#queryDate').val();
    var queryStr = $('#queryStr').val();
    url = url+'?queryDate='+queryDate;
    if(queryStr.length>0){
        url = url+'&queryStr='+queryStr;
    }
    indexView(url);
}

function searchBonus() {
    var url = $('#viewSelect').val();
    var queryStr = $('#queryStr').val();
    var queryDate = $('#queryDate').val();
    url = url+'?queryStr='+queryStr;
    if(queryDate.length>0){
        url = url+'&queryDate='+queryDate;
    }
    indexView(url);
}
