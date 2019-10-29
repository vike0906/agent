function alertInfo(info) {
    var alertInfo = $("#alertInfo");
    alertInfo.text(info);
    alertInfo.attr('hidden',false);
    setTimeout(function () {
        alertInfo.attr('hidden',true);
    },2000)
}
function addUser() {
    var name = $("#userName").val();
    var loginName = $("#loginName").val();
    var password = $("#password").val();
    var password1 = $("#password1").val();
    var userRole = $("#userRole").val();
    if(name.length==0){
        alertInfo('用户姓名为空');
        return;
    }
    if(loginName.length==0){
        alertInfo('登陆名为空');
        return;
    }
    if(password.length==0){
        alertInfo('请输入密码');
        return;
    }
    if(password1.length==0){
        alertInfo('请再次输入密码');
        return;
    }
    if(password != password1){
        alertInfo('两次输入密码不一致');
        return;
    }
    var params = {name:name,loginName:loginName,password:password,roleId:userRole};
    $('#addUserModal').modal('hide');
    ajaxPost('/system/add-user',params,function (data) {
        if(data.code==0){
            alter(data.message);
        }else{
            alter(data.message);
        }
    });
}

function changeUser(userId, type) {
    var msg = type==1?'禁用':'启用';
    agentConfirm('确定'+msg+'该用户？',function () {
        $('#confirmModal').modal('hide');
        $('#confirmButton').unbind();
        params = {userId:userId,type:type};
        ajaxPost('/system/change-user',params,function (data) {
            if(data.code==0){
                alter('用户已'+msg);
            }else {
                alter(data.message);
            }
        });
    });
}






