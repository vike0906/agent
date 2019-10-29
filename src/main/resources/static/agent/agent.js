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


