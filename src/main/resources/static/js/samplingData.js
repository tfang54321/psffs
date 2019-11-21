var samplingData = {
    //VARIABLES
    dataTablesInitialized: false,
    dynamicFields: [
        'otolith_otolithEdgeCode', 'otolith_otolithReliabilityCode', 'otolith_age',
        'weightKg_roundWt', 'weightKg_guttedWt', 'weightKg_ggWt',
        'weightG_gonadWt', 'weightG_liverWt', 'weightG_gutsWt', 'weightG_stomachWt',
        'stomach_fullness', 'stomach_parasiteCode', 'stomach_nbrOfParasites', 'stomach_primaryStomachContentCode',
        'stomach_secondaryStomachContentCode', 'archives_status', 'archives_checkbox'
    ],
    //INIT FUNCTIONS
    initList: function() {
        $.hideDisabledLookups();
        $('button.btnFetch,button#btnFetchRemember').on('click', function() {
            samplingData.fetchSamplingListData($(this).attr('id') === 'btnFetchRemember');
        });

        var samplingDataTable = $('#sampling-data-table');
        $(samplingDataTable).on('change', 'input[list],input[data-list]', function(event) {
            var inputElem = $(event.target);
            var inputVal = $(inputElem).val();
            var listId = $(inputElem)[0].hasAttribute('list') ? $(inputElem).attr('list') :
                ($(inputElem)[0].hasAttribute('data-list') ? $(inputElem).attr('data-list') : '_dne99_');
            var dataList = $('datalist#'+listId);
            if ($(dataList).length) {
                if (!$(dataList).children('option[value="' + inputVal + '"]').length) {
                    $(inputElem).val('');
                }
            }
        });

        $(samplingDataTable).on('change', 'input', function(event) {
             $(event.target).closest('tr').find('button.btn-sd-save').attr('data-changed', 'true');
        });

        $(samplingDataTable).on('focus', 'input[list]', function(event) {
            var inputElem = $(event.target);
            var inputVal = $(inputElem).val();
            $(inputElem)[0].setSelectionRange(0, inputVal.length);
        });

        $(samplingDataTable).on('change', 'input.archives_checkbox', function(event) {
            var checkboxElem = $(event.target);
            var checkboxVal = $(checkboxElem).val();
            var checked = $(checkboxElem).is(':checked');
            if (checkboxVal === '') {
                var otherCheckboxes = $('input.archives_checkbox[value!=""]:not([disabled])');
                if ($(otherCheckboxes).length && checked) $('button.btnMark').prop('disabled', false);
                else $('button.btnMark').prop('disabled', true);
                $(otherCheckboxes).prop('checked', checked);
            } else {
                if (checked) {
                    $('button.btnMark').prop('disabled', false);
                    if (!$('input.archives_checkbox[value!=""]:not(:checked):not([disabled])').length) {
                        $('input.archives_checkbox[value=""]').prop('checked', true);
                    }
                } else {
                    if (!$('input.archives_checkbox[value!=""]:checked:not([disabled])').length) {
                        $('input.archives_checkbox[value=""]').prop('checked', false);
                        $('button.btnMark').prop('disabled', true);
                    }
                }
            }
        });

        $('#cellCriteria_nafo').on('change', function(event) {
            var selection = $(this).children('option:selected').val();
            var unitAreaSelector = '#cellCriteria_unitAreaData';
            var forceReset;
            if (typeof event.originalEvent !== 'undefined') {
                forceReset = true;
            } else {
                forceReset = selection === '';
            }
            app.hideAllSelectOptions(unitAreaSelector, forceReset);
            if (selection !== '') {
                $(unitAreaSelector).children('option').each(function() {
                    if ($(this).val() !== '') {
                        if ($(this).val().substring($(this).val().indexOf(';') + 1) === selection) {
                            $(this).show().prop('disabled', false);
                        }
                    }
                });
                $(unitAreaSelector).prop('disabled', false);
            }
        }).trigger('change');

        $('button.btnSaveAll').on('click', function() {
             var changeMarkedButtons = $('button[data-changed="true"]');
             if ($(changeMarkedButtons).length) {
                 var requests = {};
                 var request;
                 $(changeMarkedButtons).each(function (index, btn) {
                     request = {};
                     request.data = $(btn).closest('tr').serializeJSON('data-field', 'samplingData_');
                     request.fields = Object.keys(request.data);
                     requests[$(btn).attr('data-sd-id')] = request;
                 });
                 samplingData.syncSamplingDataRecords(requests);
             }
        });

        $('button.btnMark').on('click', function() {
            var checkedSDs = $('input[value!=""].archives_checkbox:checkbox:checked').map(function(){return this.value;}).get();
            if (checkedSDs && checkedSDs.constructor === Array && checkedSDs.length > 0) {
                $.confirmAction(function() {
                    $.processPromise(new Promise(function(doReturn) {
                        var dt = $('#sampling-data-table').DataTable();
                        var row;
                        var markedSDs = [];
                        checkedSDs.forEach(function(sdId, index) {
                            $.ajax({
                                url: markPath,
                                type: 'PATCH',
                                contentType: 'text/plain',
                                data: sdId,
                                success: function(data) {
                                    if (data !== 'error') {
                                        row = dt.row($('[data-sd-id="' + sdId + '"]').parents('tr')).node();
                                        dt.cell(row, 6).data(data);
                                        markedSDs.push(sdId);
                                        if ((index + 1) === checkedSDs.length) {
                                            if (markedSDs.length > 0) {
                                                $.showAlert('success', $.getMessage('js.success.records_marked', [markedSDs.join(', ')]), 10000, 'sampling-data-table_wrapper');
                                            }
                                            dt.draw();
                                            doReturn();
                                        }
                                    } else {
                                        if ((index + 1) === checkedSDs.length) {
                                            dt.draw();
                                            doReturn();
                                        }
                                    }
                                },
                                error: function() {
                                    if ((index + 1) === checkedSDs.length) {
                                        dt.draw();
                                        doReturn();
                                    }
                                }
                            });
                        });
                    }));
                }, 'js.confirm.mark_records', [checkedSDs.join(', ')]);
            }
        });

        $('input[name="source"]').on('change', function(event) {
            var isAll = $(event.target).attr('id') === 'sourceAll';
            $('#sourceSampleId').prop('disabled', isAll)[0].focus();
            $('#cellCriteria').find('input,select').prop('disabled', !isAll);
        });

        if ($('#sourceSampleId').val().trim() !== '') {
            $('#btnFetch1')[0].click();
        }
    },
    initEdit: function() {
        var sdId = $.urlParam('sdId');
        $('input[name="_csrf"]').attr('id', 'sampleData_csrf');

        $('#btnReset').on('click', function() {
            $.processPromise(samplingData.fetchSamplingDataRecord(sdId));
        });

        $('#samplingDataForm').on('submit', function(event) {
            event.preventDefault();
            $.processPromise(samplingData.updateSamplingData());
        });

        $.processPromise(samplingData.fetchSamplingDataRecord(sdId));
    },
    //PAGE FUNCTIONS
    openSamplingDataRecord: function(sdId) {
        window.location.href = editPath + "?sdId="+sdId;
    },
    fetchSamplingDataRecord: function(sdId) {
        return new Promise(function(doReturn) {
            var samplingForm = $('form#samplingDataForm');
            var action = $(samplingForm).attr('action');
            $.ajax({
                url: action,
                data: 'sdId='+sdId,
                type: 'GET',
                success: function(formData) {
                    if (formData) {
                        $('form#samplingDataForm').bindFromJSON(formData, 'id', 'sampleData_');
                        $(samplingForm).show();
                        $('span#span-samplingDataId').html(formData.samplingDataId);
                        $(samplingForm).find('details').prop('open', true);
                        $.convertDataTextOnly();
                        $.hideDisabledLookups();

                        if(formData.primaryStomachContentId == null){
                            $('#sampleData_primaryStomachContentId').val("");
                        }
                        if(formData.secondaryStomachContentId == null){
                            $('#sampleData_secondaryStomachContentId').val("");
                        }


                        $('select[data-searchable=true]').trigger("change.select2");
                    } else {
                        $.showError('js.error.no_sampling_data_found', null, [sdId]);
                    }
                    doReturn();
                },
                error: function() {
                    $.showError('js.error.serverDB_operation_failed');
                    doReturn();
                }
            });
        });
    },
    saveSamplingDataRecord: function(btn) {
        var request = {};
        request.data = $(btn).closest('tr').serializeJSON('data-field', 'samplingData_');
        request.fields = Object.keys(request.data);
        var requests = {};
        requests[$(btn).attr('data-sd-id')]=request;
        samplingData.syncSamplingDataRecords(requests);
    },
    syncSamplingDataRecords: function(requests) {
        $('#sync-modal').remove();

        var modal = '<section id="sync-modal" class="mfp-hide modal-dialog modal-content overlay-def">';
        modal += '<header class="modal-header">';
        modal += '<h2 class="modal-title">' + $.getMessage('js.modal.save.title') + '</h2>';
        modal += '</header>';
        modal += '<div class="modal-body">';
        modal += '<div id="sync-modal-body" style="max-height: 300px; overflow-y: auto;">';
        modal += '<ol style="font-size: 1.2em;">';
        Object.keys(requests).forEach(function(sdId, index) {
            modal += '<li class="pending-sync">';
            modal += 'ID: <span>'+sdId+'</span>&nbsp;<span class="glyphicon glyphicon-question-sign" tabindex="-1"></span>';
            modal += '</li>';
        });
        modal += '</ol>';
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

        var syncFunction = function() {
            var syncModal = $('#sync-modal');
            if ($(syncModal).length && $(syncModal).is(':visible')) {
                var syncElem = $('li.pending-sync');
                if ($(syncElem).length) {
                    syncElem = $(syncElem).first();
                    $(syncElem).attr('class', 'sync').find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-circle-arrow-down');
                    var sdId = $(syncElem).find('span').first().html();
                    var request = requests[sdId];
                    var dataToSend = JSON.stringify(request);
                    $.ajax({
                        type: 'PATCH',
                        url: patchPath+'?sdId='+sdId,
                        data: dataToSend,
                        contentType: 'application/json',
                        success: function (data) {
                            if (data.status === 'SUCCESS') {
                                $(syncElem).attr('class', 'sync-complete');
                                $('button[data-sd-id="'+data.id+'"]').removeAttr('data-changed');
                                var sdlistDoneCheckbox = $('input#sdlist_'+data.id+'_statusId');
                                if ($(sdlistDoneCheckbox).is(':checked')) {
                                    $(sdlistDoneCheckbox).attr('onclick', 'return false;');
                                    var row = $(sdlistDoneCheckbox).closest('tr');
                                    $(row).find('input[type!="checkbox"]').prop('readonly', true);
                                    $(row).find('button.btn-sd-save').prop('disabled', true);
                                }
                                $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-ok-sign text-green').focus();
                                setTimeout(syncFunction, 500);
                            } else {
                                var errorKey = 'js.error.save_fail';
                                if (typeof data.errorKey !== 'undefined' && data.errorKey != null &&
                                    data.errorKey.length > 0) errorKey = data.errorKey;
                                $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage(errorKey, [sdId]));
                                $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                                setTimeout(syncFunction, 500);
                            }
                        },
                        error: function () {
                            $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage('js.error.save_fail', [sdId]));
                            $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                            setTimeout(syncFunction, 500);
                        }
                    });
                } else {
                    $(syncModal).find('button').html($.getMessage('ok'));
                    $(syncModal).find('.modal-title').html($.getMessage('js.modal.save.title_complete'));
                    var syncErrorElems = $('li.sync-failed');
                    if ($(syncErrorElems).length) {
                        var errorHTML = '<p>';
                        $(syncErrorElems).each(function(index, elem) {
                            errorHTML += $(elem).attr('data-error');
                            if ((index+1) !== $(syncErrorElems).length) {
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
    },
    updateSamplingData: function() {
        return new Promise(function(doReturn) {
            var samplingDataForm = $('#samplingDataForm');
            var action = $(samplingDataForm).attr('action');
            var dataJSON = JSON.stringify($(samplingDataForm).serializeJSON('id', 'sampleData_'));
            $.ajax({
                url: action,
                type: 'POST',
                data: dataJSON,
                contentType: 'application/json',
                success: function(response) {
                    if (response) {
                        if (response.status === 'SUCCESS') {
                            $.showSuccess('js.success.sampling_data_updated');
                            $.hideDisabledLookups();
                        } else {
                            if (response.errorKey) {
                                $.showError(response.errorKey);
                            } else {
                                $.showError('js.error.serverDB_operation_failed');
                            }
                        }
                    } else {
                        $.showError('js.error.serverDB_operation_failed');
                    }
                    doReturn();
                },
                error: function() {
                    $.showError('js.error.serverDB_operation_failed');
                    doReturn();
                }
            })
        });
    },
    fetchSamplingListData: function(savePreferences) {
        if(($('#cellCriteria_year').val() !== '' && $('#cellCriteria_sampleSpecies').val() !== '') || ($('#sourceOne').is(':checked') && $('#sourceSampleId').val !== '')) {
            $('th.sd-list,th.sd-list-archives').hide();
            var saveAllBtns = $('button.btnSaveAll').prop('disabled', true);
            var samplingDataTable = $('table#sampling-data-table');

            if (samplingData.dataTablesInitialized) {
                $(samplingDataTable).DataTable().clear().destroy();
            } else {
                $(samplingDataTable).addClass('wb-tables');
                $(samplingDataTable).on('wb-init.wb-tables', function () {
                    $.processPromise(new Promise(function (doReturn) {
                        $(samplingDataTable).on('wb-ready.wb-tables', function () {
                            samplingData.dataTablesInitialized = true;
                            var fieldsSelection = $('input[name="fields"]:checked').val();
                            var dynamicFields = samplingData.determineApplicableDynamicFields(fieldsSelection);
                            var recordSaveButtons = $('button.btn-sd-save:not([disabled])');
                            if ($(samplingDataTable).DataTable().data().any() && dynamicFields.length > 0 && fieldsSelection !== 'archives') {
                                $(saveAllBtns).prop('disabled', false);
                                $(recordSaveButtons).prop('disabled', false);
                            } else {
                                $(recordSaveButtons).prop('disabled', true);
                                $('button.btnMark').prop('disabled', true);
                            }
                            $(samplingDataTable).find('th').each(function (index, th) {
                                $(th).find('span.sorting-cnt').each(function (index, sortingCnt) {
                                    if (index > 0) $(sortingCnt).remove();
                                });
                            });
                            $.initFormEnhancements(samplingDataTable);
                            doReturn();
                        });
                    }));
                });
                $(samplingDataTable).on('xhr.dt', function(event, settings, json) {
                    $('.sampling-data-identifiers').hide();
                    if (json) {
                        samplingData.updateSelectionCriteria($('form#samplingDataSettings').find('input,select'));
                        if ($('#sourceOne').is(':checked')) {
                            var cellData = json.cell;
                            if(cellData != null) {
                                var dataFields = Object.keys(cellData);
                                var container;
                                for (var x = 0; x < dataFields.length; x++) {
                                    container = $('#cell_' + dataFields[x]);
                                    if (cellData[dataFields[x]]) {
                                        $(container).html(cellData[dataFields[x]]).show().parent().show();
                                    } else {
                                        $(container).hide().parent().hide();
                                    }
                                }
                            }
                            $('#lbl_cell').show();
                        } else {
                            $('#lbl_cell_criteria').show();
                        }
                    }
                });
            }
            var samplingSettingsForm = $('form#samplingDataSettings');
            var sourceUrl = $(samplingSettingsForm).attr('action') + '?' + $(samplingSettingsForm).serialize();
            if (typeof savePreferences !== 'undefined' && savePreferences) {
                sourceUrl += '&savePreferences=true';
            }
            var settings = {
                ajax: sourceUrl,
                serverSide: true,
                order: [[1, 'asc']],
                lengthMenu: [10,25,50,100,250,500],
                searchDelay: 1000,
                columns: [
                    {data: 'samplingDataId', orderable: false},
                    {data: 'storageNbr'},
                    {data: 'tag'},
                    {data: 'sex'},
                    {data: 'maturityLevel'},
                    {data: 'entryLength'}
                ]
            };
            var selectedFields = $('input[name="fields"]:checked').val();
            var dynamicFields = samplingData.determineApplicableDynamicFields(selectedFields);
            if (selectedFields !== 'archives') {
                dynamicFields.push('sdlist_statusId');
                $('button.btnMark').hide();
            } else {
                $('button.btnMark').show();
            }
            for (var x = 0; x < dynamicFields.length; x++) {
                if (dynamicFields[x] === 'archives_status') settings.columns.push({data: 'archives_status'});
                else if (dynamicFields[x] === 'archives_checkbox' || dynamicFields[x] === 'sdlist_statusId') {
                    settings.columns.push({data: dynamicFields[x], orderable: false, className: 'hidden-print'});
                }
                else settings.columns.push({data: dynamicFields[x], orderable: false});
                $('th.' + dynamicFields[x]).show();
            }
            settings.columns.push({data: 'buttons', orderable: false, className: 'hidden-print'});
            $(samplingDataTable).removeClass('wb-tables-inited').removeClass('no-footer');
            $(samplingDataTable).attr('data-wb-tables', JSON.stringify(settings)).trigger('wb-init.wb-tables');
        }
        else if($('#sourceAll').is(':checked')) {
            $('#samplingDataSettings').validate();
        }
    },
    determineApplicableDynamicFields: function(selectedFields) {
        var dynamicFields = [];
        var dynamicFieldsPrefix = selectedFields + '_';
        var dynamicField;
        for (var x = 0; x < samplingData.dynamicFields.length; x++) {
            dynamicField = samplingData.dynamicFields[x];
            if (dynamicField.indexOf(dynamicFieldsPrefix) === 0) {
                dynamicFields.push(dynamicField);
            }
        }
        return dynamicFields;
    },
    updateSelectionCriteria: function(elements) {
        if (elements) {
            $('span[data-from]').hide().parent().hide();
            var container, op, val, text;
            for (var i = 0; i < elements.length; i++) {
                container = $('span[data-from="' + $(elements[i]).attr('id') + '"]');
                val = '';
                if (!$(elements[i]).prop('disabled')) {
                    if ($(elements[i]).prop('tagName').toLowerCase() === 'select') {
                        op = $(elements[i]).children('option:selected');
                        if (!$(op).length) {
                            op = $(elements[i]).children('optgroup').children('option:selected');
                            text = '[' + $(op).parent().prop('label') + '] ' + $(op).text();
                        } else {
                            text = $(op).text();
                        }
                        val = $(op).val();
                    } else {
                        op = $(elements[i]).prop('type').toLowerCase();
                        val = $(elements[i]).val();
                        if (op === 'radio' || op === 'checkbox') {
                            if ($(elements[i]).is(':checked')) {
                                text = $('label[for="' + $(elements[i]).attr('id') + '"]').html();
                            } else {
                                val = '';
                            }
                        } else {
                            text = val;
                        }
                    }
                }

                if (val && val !== '') {
                    $(container).html(text);
                    $(container).show().parent().show();
                } else {
                    $(container).hide();
                }
            }

        }
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains("/list", samplingData.initList);
    $.initForPathContains("/edit", samplingData.initEdit);
});