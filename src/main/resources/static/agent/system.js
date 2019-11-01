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

    ajaxPost('/system/add-user',params,function (data) {
        if(data.code==0){
            $('#addUserModal').modal('hide');
            alter(data.message);
        }else{
            alterToast(data.message);
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
    ajaxPost('/system/add-role',params,function (data) {
        if(data.code==0){
            $('#addRoleModal').modal('hide');
            alter(data.message);
        }else{
            alterToast(data.message);
        }
    });
}

function editRole(id) {
    var name = $("#"+id).val();
    $("#editRoleModalCenterTitle").html('编辑<strong>'+name+'</strong>的权限');

    ajaxGet('/system/role-permissions?roleId='+id, function (response) {
        if(response.code==0){
            var trBody = '';
            var addButton = '<i class="ion-ios-add-circle-outline"></i><span> 添加</span>';
            var removeButton = '<i class="ion-ios-remove-circle-outline"></i><span> 移除</span>'
            response.data.forEach(function (a) {
                trBody=trBody+'<tr><td>'+a.name+'</td><td>'+a.url+'</td><td>'+(a.status==1?'<span style="color: green">有</span>':'<span style="color: red">无</span>')+'</td><td><a class="btn btn-sm btn-light" onclick="changRolePermission('+a.rpId+','+(a.status==1?2:1)+','+id+')">'+(a.status==1?removeButton:addButton)+'</a></td></tr>';
            });
            $("#editRoleTableBody").html(trBody);
            $("#editRoleModal").modal('show');
        }else{
            alter(data.message);
        }
    });
}

function changRolePermission(id, type, roleId) {
    var params = {id:id, type:type};
    var msg = type==1?'添加成功':'移除成功';
    ajaxPost('/system/edit-role',params,function (response) {
        if(response.code==0){
            alterToast(msg);
            // $("#editRoleModal").modal('hide');
            editRole(roleId);
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
    ajaxPost('/system/add-permissions',params,function (data) {
        if(data.code==0){
            $('#addPermissionModal').modal('hide');
            alter(data.message);
        }else{
            alterToast(data.message);
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






