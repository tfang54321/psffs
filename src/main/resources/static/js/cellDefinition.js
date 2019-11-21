var cellDefinition = {
    initCreate: function() {
        $.hideDisabledLookups();
        $.convertDataTextOnly();

        $('#cdCreateForm').on('reset', function() {
            setTimeout(function() {
                $('#sampleSpeciesId').trigger("change.select2");
            }, 0);
        });
    },
    initEdit: function() {
        if (readOnlyForm) {
            $('#cdEditForm :input:not([type=hidden]):not(#cdEditCancel)').attr('disabled', true);
        } else {
            $.hideDisabledLookups();
        }
        $.convertDataTextOnly();

        $('#cdEditForm').on('reset', function() {
            setTimeout(function() {
                $('select[data-searchable=true]').trigger("change.select2");
            }, 0);
        });
    },
    initList: function(){
        $("#cellDefTable").on('change', 'input.cd-list-checkbox', function () {
            var isChecked = $(this).is(':checked');
            if ($(this).val() === '') {
                $('input.cd-list-checkbox').prop('checked', isChecked);
            } else {
                $('input.cd-list-checkbox').each(function (index, elem) {
                    if ($(elem).val() !== '' && $(elem).is(':checked')) {
                        isChecked = true;
                    }
                });
                if (!isChecked) {
                    $('input[value=""].cd-list-checkbox').prop('checked', false);
                }
            }

            $('#btnMigrate').prop('disabled', !isChecked);
        });
        $('#btnMigrate').on('click', cellDefinition.migrateCellDefs);
    },
    migrateCellDefs : function() {
        var checkedCDs = $('input[value!=""].cd-list-checkbox:checkbox:checked').map(function(){return this.value;}).get();

        var selectHTML = "<select>";
        var yearMin = new Date().getFullYear();
        var yearMax = yearMin + 5;
        for (var x = yearMin; x < yearMax; x++){
            if(x === yearMin + 1){
                selectHTML += "<option value='" + x + "' selected='selected'>" + x + "</option>"
            }
            else {
                selectHTML += "<option value='" + x + "'>" + x + "</option>"
            }
        }
        selectHTML += "</select>";

        $.prompt("js.cd.migrate.year", null, selectHTML).then(function (year) {
            if(year !== false) {
                $.confirmAction(function () {
                    $('#sync-modal').remove();

                    var modal = '<section id="sync-modal" class="mfp-hide modal-dialog modal-content overlay-def">';
                    modal += '<header class="modal-header">';
                    modal += '<h2 class="modal-title">' + $.getMessage('js.modal.migrate.title') + '</h2>';
                    modal += '</header>';
                    modal += '<div class="modal-body">';
                    modal += '<div id="sync-modal-body" style="max-height: 300px; overflow-y: auto;">';
                    modal += '<ul style="font-size: 1.2em;">';
                    checkedCDs.forEach(function (cdId, index) {
                        modal += '<li class="pending-sync">';
                        modal += '<span>' + cdId + '</span>&nbsp;<span class="glyphicon glyphicon-question-sign" tabindex="-1"></span>';
                        modal += '</li>';
                    });
                    modal += '</ul>';
                    modal += '</div>';
                    modal += '</div>';
                    modal += '<div class="modal-footer">';
                    modal += '<button type="button" class="btn btn-default popup-modal-dismiss" id="btn-prompt-cancel">' + $.getMessage('cancel') + '</button>';
                    modal += '</div>';
                    modal += '</section>';
                    $('#main').append(modal);

                    if (!$('h1').length) {
                        $('#main').append('<h1 style="display: none;">NO H1 TITLE</h1>');
                    }

                    wb.doc.trigger("open.wb-lbx", [
                        [
                            {
                                src: "#sync-modal",
                                type: "inline"
                            }
                        ],
                        true
                    ]);

                    var syncFunction = function () {
                        var syncModal = $('#sync-modal');
                        // var year = new Date().getFullYear() + 1;
                        if ($(syncModal).length && $(syncModal).is(':visible')) {
                            var syncElem = $('li.pending-sync');
                            if ($(syncElem).length) {
                                syncElem = $(syncElem).first();
                                $(syncElem).attr('class', 'sync').find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-circle-arrow-down');
                                var cdId = $(syncElem).find('span').first().html();

                                $.ajax({
                                    type: 'GET',
                                    url: migratePath,
                                    data: "cdId=" + cdId + "&year=" + year,
                                    contentType: 'application/json',
                                    success: function (data) {
                                        if (data.status === 'SUCCESS') {
                                            $(syncElem).attr('class', 'sync-complete').append('&nbsp;<span>' + data.id + '</span>');
                                            $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-circle-arrow-right text-green').focus();

                                            setTimeout(syncFunction, 500);
                                        } else {
                                            var errorKey = 'js.error.migrate_fail';
                                            if (typeof data.errorKey !== 'undefined' && data.errorKey != null &&
                                                data.errorKey.length > 0) errorKey = data.errorKey;
                                            $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage(errorKey, data.errorArgs));
                                            $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                                            setTimeout(syncFunction, 500);
                                        }
                                    },
                                    error: function () {
                                        $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage('js.error.migrate_fail', [cdId]));
                                        $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                                        setTimeout(syncFunction, 500);
                                    }
                                });

                            } else {
                                $('#cellDefTable').DataTable().ajax.reload(function (json) {
                                    $('#myInput').val(json.lastInput);
                                });
                                $(syncModal).find('button').html($.getMessage('ok'));
                                $(syncModal).find('.modal-title').html($.getMessage('js.modal.migrate.title_complete'));
                                var syncErrorElems = $('li.sync-failed');
                                if ($(syncErrorElems).length) {
                                    var errorHTML = '<p>';
                                    $(syncErrorElems).each(function (index, elem) {
                                        errorHTML += $(elem).attr('data-error');
                                        if ((index + 1) !== $(syncErrorElems).length) {
                                            errorHTML += '<br />';
                                        }
                                    });
                                    errorHTML += '</p>';
                                    $.showAlert('danger', errorHTML, 0, 'sync-modal-body');
                                }
                            }
                        }
                    };

                    setTimeout(syncFunction, 500);
                }, 'js.confirm.migrate_records', [year, checkedCDs.join(', ')]);
            }
        });
    },
    goToEdit : function(id){
        window.location.href = editPath + '?id='+ id;
    },
    removeCellDef : function(id){
        $.confirmAction( function() {
            window.location.href = removePath + '?id='+ id;
        }, "js.confirm.cd_remove", [id]);
    },
    makeActive: function(){
        $.confirmAction( function() {
            $.ajax({
                type: 'PATCH',
                url: activePath+'?id='+editId,
                contentType: 'application/json',
                success: function (data) {
                    if (data === 'SUCCESS') {
                        $.showAlert("success", $.getMessage("js.cd_activate_success", null), 0, "displayActivate");
                        $("#btnActivate").hide();
                    }
                    else {
                        $.showAlert("error", $.getMessage("js.cd_activate_error", null), 0, "displayActivate");
                    }
                },
                error: function () {
                    $.showAlert("error", $.getMessage("js.cd_activate_error", null), 0, "displayActivate");
                }
            });
        }, "js.confirm.cd.status_active", null);
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/list', cellDefinition.initList);
    $.initForPathContains('/edit', cellDefinition.initEdit);
    $.initForPathContains('/create', cellDefinition.initCreate);
});