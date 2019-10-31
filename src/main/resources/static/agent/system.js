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
        var params = {userId: userId, type: type};
        ajaxPost('/system/change-user',params,function (data) {
            if(data.code==0){
                alter('用户已'+msg);
            }else {
                alter(data.message);
            }
        });
    });
}

function addRole() {
    var name = $("#roleName").val();
    if(name.length==0){
        alertInfo('角色名称为空');
        return;
    }
    var params = {name:name};
    $('#addRoleModal').modal('hide');
    ajaxPost('/system/add-role',params,function (data) {
        if(data.code==0){
            alter(data.message);
        }else{
            alter(data.message);
        }
    });
}

function editRole(id) {

    var name = $("#"+id).val();
    $("#editRoleModalCenterTitle").html('编辑<strong>'+name+'</strong>的权限');

    ajaxGet('/system/role-permissions?roleId='+id, function (response) {
        if(response.code==0){
            var trBody = '';
            response.data.forEach(function (a) {
                trBody=trBody+'<tr><td>'+a.name+'</td><td>'+a.url+'</td><td>'+(a.status==1?'<span style="color: green">有</span>':'<span style="color: red">无</span>')+'</td><td><button class="btn btn-sm btn-outline-'+(a.status==1?'danger':'success')+'" onclick="changRolePermission('+a.rpId+','+(a.status==1?2:1)+')">'+(a.status==1?'关闭':'添加')+'</button></td></tr>';
            });
            $("#editRoleTableBody").html(trBody);
            $("#editRoleModal").modal('show');
        }else{
            alter(data.message);
        }
    });
}

function changRolePermission(id, type) {
    var params = {id:id, type:type};
    ajaxPost('/system/edit-role',params,function (response) {
        if(response.code==0){
            $("#editRoleModal").modal('hide');
            alter('添加成功');
        }else {
            alterToast(response.message);
        }
    });
}

function addPermission() {
    var name = $("#permissionName").val();
    var url = $("#url").val();
    var parentId = $("#parentId").val();
    if(name.length==0){
        alertInfo('菜单名为空');
        return;
    }
    if(url.length==0){
        alertInfo('路由为空');
        return;
    }
    if(parentId.length==0){
        alertInfo('请选择主菜单');
        return;
    }
    var params = {name:name,url:url,parentId:parentId};
    $('#addPermissionModal').modal('hide');
    ajaxPost('/system/add-permissions',params,function (data) {
        if(data.code==0){
            alter(data.message);
        }else{
            alter(data.message);
        }
    });
}

function deletePermisson(id) {
    agentConfirm('确定删除此菜单？',function () {
        $('#confirmModal').modal('hide');
        $('#confirmButton').unbind();
        var params = {id: id};
        ajaxPost('/system/delete-permissions',params,function (data) {
            if(data.code==0){
                alter('删除成功');
            }else {
                alter('删除失败');
            }
        });
    });
}






