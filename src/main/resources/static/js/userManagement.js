var userManagement = {
    initForm:function () {
        $('#uInitial').on("blur", function(){
            $(this).val($(this).val().toUpperCase());
            $(this).val($(this).val().replace(/\s+/g, ''));
        });

        $('#uUName').on("blur", function(){
            $(this).val($(this).val().toLowerCase());
            $(this).val($(this).val().replace(/\s+/g, ''));
        });

        $.convertDataTextOnly();
    },
    goToEdit : function(id){
        window.location.href = editPath + '?id='+ id;
    },
    activateUser: function(id, description, active) {
        $('#successMessage').hide();
        $.confirmAction(function() {
            $.processPromise(new Promise(function(doReturn) {
                $.ajax({
                    url: activatePath+"?id="+id,
                    type: 'PATCH',
                    success: function(result){doReturn(result === 'success')},
                    error: function(){doReturn(false)}
                });
            }), function(result) {
                if (result) {
                    var activateButton = $('#activate_'+id);
                    if ($(activateButton).length) {
                        $(activateButton).attr('href', 'javascript: userManagement.activateUser('+id+',"'+description+'",'+!active+')');
                        $(activateButton).attr('title', $.getMessage('js.icon.'+(!active ? 'activate' : 'deactivate')+'.title'));
                        $(activateButton).find('span').first().attr('class', 'glyphicon glyphicon-'+(!active ? 'ok-circle text-green' : 'ban-circle text-red'));
                    }
                } else {
                    $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'user-container');
                }
            });
        }, 'js.confirm.'+(!active ? 'activate' : 'deactivate'), [description])
    },
    initList: function() {
        var settings = {
            ajax: { url: listDataPath,dataSrc : "userList"},
            order: [[0, 'desc']],
            language: { search : $.getMessage('js.user_man.filter_users') },
            columns : [
                {data: 'userId'}
                ,{data: 'fullname'}
                ,{data: 'email'}
                ,{data: 'ntPrincipal'}
                ,{data: 'buttons', orderable: false}]
        };

        $("#userManagementTable").attr('data-wb-tables', JSON.stringify(settings)).addClass('wb-tables').trigger('wb-init.wb-tables');
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/create', userManagement.initForm);
    $.initForPathContains('/edit', userManagement.initForm);
    $.initForPathContains('/list', userManagement.initList);


});