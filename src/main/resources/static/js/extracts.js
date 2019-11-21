var extracts = {
    //INIT METHODS
    initExtracts: function() {
        $('#btnExecute').on('click', function() {
            extracts.execute();
        });
        $('#extractClass').on('change', function() {
            var selection = $(this).children('option:selected').val();
            if (selection !== '') {
                $('#btnExecute').prop('disabled', false);
            } else {
                $('#btnExecute').prop('disabled', true);
            }
        });

        $('#btnRefresh').on('click', function() { extracts.fetchList(true); });
        extracts.fetchList(false);
    },
    //PAGE METHODS
    fetchList: function(isRefresh) {
        $.processPromise(new Promise(function(doReturn) {
            $.ajax({
                type: 'GET',
                url: listPath,
                data: 'type=' + $('#extractClass').children('option:selected').val(),
                success: function (data) {
                    if (data.extracts) {
                        var settings = {
                            data: data.extracts,
                            order: [[2, 'desc']],
                            columns: [
                                {data: 'downloadLink', orderable: false},
                                {data: 'simpleType'},
                                {data: 'createdDate'}
                            ]
                        };

                        if (!isRefresh) {
                            $('#extractTable').attr('data-wb-tables', JSON.stringify(settings)).addClass('wb-tables').trigger('wb-init.wb-tables');
                            doReturn(true);
                        } else {
                            var dt = $('#extractTable').DataTable();
                            dt.clear().rows.add(data.extracts).draw();
                            doReturn(true);
                        }
                    }
                },
                error: function () {
                    doReturn(false);
                }
            })
        }), function(success) {
            if (!success) {
                $.showAlert('danger', $.getMessage('js.error.failed_to_fetch_data'), 10000, 'extractTableContainer');
            }
        });
    },
    execute: function() {
        $.confirmAction(function() {
            $.processPromise(new Promise(function(doReturn) {
                $.ajax({
                    type: 'POST',
                    url: executePath,
                    data: 'type=' + $('#extractClass').children('option:selected').val(),
                    success: function(data) {
                        if (data === 'success') {
                            doReturn({success: true, messageKey: 'js.success.extracts_executed'});
                        } else {
                            doReturn({success: false, messageKey: data});
                        }
                    },
                    error: function() {
                        doReturn({success: false, messageKey: 'js.error.server_request_failed'});
                    }
                });
            }), function(state) {
                $.showAlert(state.success ? 'success' : 'danger', $.getMessage(state.messageKey), 20000, 'main');
            });
        }, 'js.confirm.execute_extracts');
    },
    downloadExtract: function(extractId) {
        window.open(downloadPath + '?extractId=' + extractId, '_blank');
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/extracts', extracts.initExtracts);
});