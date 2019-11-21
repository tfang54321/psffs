var observer = {
    //INIT FUNCTIONS
    initCreate: function() {
        $('#tripSetSpeciesForm').on('submit', function(e) {
            var valid = $('#tripSetSpeciesForm').validate();
            if(!valid.numberOfInvalids()) {
                observer.saveTripSetSpecies(e, false, function (tssId) {
                    if (tssId.indexOf('ERROR:') >= 0) {
                        $("#errDiv").html("");
                        $.showAlert("danger", $.getMessage(tssId.replace('ERROR:', '')), 0, "errDiv");
                    } else {
                        window.location.href = listPath + '#' + tssId;
                    }
                });
            }
        });

        $('#tripSetSpeciesForm').on('reset', function() {
            setTimeout(function() {
                $('select[data-searchable=true]').trigger("change.select2");
            }, 0);
        });

        observer.applyCommonHeaderEvents();
        $('#nafoDivisionId,#lengthUnitId').trigger('change');
        $.hideDisabledLookups();
    },
    initList: function() {
        var hash = window.location.hash;
        if (typeof hash !== 'undefined' && hash != null && hash.length > 1) {
            var tssID = hash.substring(hash.indexOf('#') + 1);
            $.showAlert('success', $.getMessage('js.success.tss_saved', [tssID]), 10000,
                'main');
            window.location.hash = '';
        }

        var fetchServerRecords = function(isRefresh) {
            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    $.ajax({
                        type: 'GET',
                        url: listPath,
                        data: 'year=' + $('#filterYear').children('option:selected').val(),
                        success: function (data) {
                            if (data.tripSetSpeciesList) {
                                var settings = {
                                    data: data.tripSetSpeciesList,
                                    order: [[0, 'desc']],
                                    columns: [
                                        {data: 'id'},
                                        {data: 'tssId'},
                                        {data: 'catchDate'},
                                        {data: 'tripNumber'},
                                        {data: 'setNumber'},
                                        {data: 'speciesText'},
                                        {data: 'observerCompanyText'},
                                        {data: 'statusText'},
                                        {data: 'buttons', orderable: false},
                                        {data: 'checkbox', orderable: false}
                                    ]
                                };

                                if (!isRefresh) {
                                    $('#online-table').attr('data-wb-tables', JSON.stringify(settings)).addClass('wb-tables').trigger('wb-init.wb-tables').on('change', 'input.tss-list-checkbox', function () {
                                        var isChecked = $(this).is(':checked');
                                        if ($(this).val() === '') {
                                            $('input.tss-list-checkbox').prop('checked', isChecked);
                                        } else {
                                            $('input.tss-list-checkbox').each(function (index, elem) {
                                                if ($(elem).val() !== '' && $(elem).is(':checked')) {
                                                    isChecked = true;
                                                }
                                            });
                                            if (!isChecked) {
                                                $('input[value=""].tss-list-checkbox').prop('checked', false);
                                            }
                                        }

                                        $('#btnDeleteON').prop('disabled', !isChecked);
                                        // $('#btnMarkArchiveON').prop('disabled', !isChecked);
                                        $('#btnMarkDistributedON').prop('disabled', !isChecked);
                                    });
                                } else {
                                    var dt = $('#online-table').DataTable();
                                    dt.clear().rows.add(data.tripSetSpeciesList).draw();
                                }
                            }
                        },
                        error: function () {
                            $.showAlert('danger', $.getMessage('js.error.failed_to_fetch_data'), 10000, 'main');
                        }
                    })
                }
            });
        };
        fetchServerRecords(false);
        $('#btnRefresh').on('click', function() {fetchServerRecords(true)});

        $('#btnDeleteON').on('click', function() {
            var checkedTSSs = $('input[value!=""].tss-list-checkbox:checkbox:checked').map(function(){return this.value;}).get();
            observer.doDeleteTSSs(checkedTSSs);
        });
        // $('#btnMarkArchiveON').on('click', function() {
        //     var checkedTSSs = $('input[value!=""].tss-list-checkbox:checkbox:checked').map(function(){return this.value;}).get();
        //     observer.doMarkTSSs(checkedTSSs, 35);
        // });
        $('#btnMarkDistributedON').on('click', function() {
            var checkedTSSs = $('input[value!=""].tss-list-checkbox:checkbox:checked').map(function(){return this.value;}).get();
            observer.doMarkTSSs(checkedTSSs, 34);
        });
    },
    initEdit: function() {
        var formContainer = $('#edit-tss-form-container').hide();
        var tssId = window.location.hash.substring(1);
        observer.applyCommonHeaderEvents();
        $('#nafoDivisionId,#lengthUnitId').trigger('change');
        $('table#tbl-lf-summary').on('change', 'input.lf-summ-num', function() {
            observer.calcTotals();
            $('#entriesModified').val('true');
        });
        $('#dt-otolith-summ').on('details.opened', function() {
            var minLengthGroup = parseInt($('#lengthGroupMin').val());
            var maxLengthGroup = parseInt($('#lengthGroupMax').val());
            var range = maxLengthGroup - minLengthGroup;
            var focusIndex = Math.round(range * 0.28);
            $('input[name="u_o['+focusIndex+']"],input[name="m_o['+focusIndex+']"],input[name="f_o['+focusIndex+']"]').each(
                function () {
                    if ($(this).is(':visible')) {
                        $(this).focus();
                        return false;
                    }
                }
            );
        });
        $('#tripSetSpeciesForm').on('submit', function(e) {
            e.preventDefault();
            var proceed = function() {
                observer.saveTripSetSpecies(e, true, function (tssId) {
                    if (tssId.indexOf('ERROR:') >= 0) {
                        $.showError(tssId.replace('ERROR:', ''), null, [$('#tssId').val()]);
                    } else {
                        window.location.href = listPath + '#' + tssId;
                    }
                });
            };
            var checkedBy = $('#checkedBy');
            if ($(checkedBy).length && $(checkedBy).children('option:selected').val() !== '') {
                $.confirmAction(proceed, 'js.confirm.checked');
            } else {
                proceed();
            }
        });
        //LOAD RECORD
        $.processPromise(observer.getOnlineTSS(tssId), function(jsonData) {
            if (jsonData) {
                observer.renderSummaryDataTable(jsonData.lengthGroupMin, jsonData.lengthGroupMax);
                $('#tripSetSpeciesForm').bindFromJSON(jsonData);
                $('#nafoDivisionId,#lengthUnitId,#sampledSpeciesId,#depthMeters,input.lf-catch-loc:checked').trigger('change');
                observer.calcTotals();
                $('#sampledSpeciesId').attr('data-text-only', 'true');
                $.hideDisabledLookups();
                $.convertDataTextOnly();

                var statusId = parseInt(jsonData.statusId);
                if (statusId === 32) {
                    $('input.lf-summ-num').Numbler();
                    $('#btnSave').prop('disabled', false);
                } else {
                    $('#tripSetSpeciesForm').setReadonly(true);
                    $('#btnSave').prop('disabled', true);
                    $.showAlert('warning', $.getMessage('js.warning.record_readonly'), 0, 'main');
                }
            } else {
                $.showError('js.error.record_not_found');
                $('#btnSave').prop('disabled', true);
                $('#tripSetSpeciesForm').find('input,textarea,select').prop('disabled', true);
            }
            $(formContainer).show();
        });
    },
    //PAGE FUNCTIONS
    applyCommonHeaderEvents: function() {
        $('input.lf-catch-loc').on('change', function() {
            $('select.lf-catch-loc').prop('disabled', true).hide();
            $('label.lf-catch-loc').hide().attr('class', 'lf-catch-loc');
            if ($(this).is(':checked')) {
                $('select[data-catch-loc="'+$(this).val()+'"]').each(function() {
                    $(this).prop('disabled', false).show().attr('class', 'lf-catch-loc field-name');
                    $('label[for="'+$(this).attr('id')+'"]').show();
                    observer.autoSelectQuarter(this);
                });
            }
        });
        $('input#catchDate').on('blur', function() {
            $('select.lf-catch-loc').each(function() {
                if (!$(this).is(':disabled')) {
                    observer.autoSelectQuarter(this);
                    return false;
                }
            });
        });
        $('#nafoDivisionId').on('change', function(event) {
            var selection = $(this).children('option:selected').val();
            var unitAreaSelector = '#unitAreaData';
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
                $(unitAreaSelector).prop("disabled", false);
            }
        });
        $('#depthMeters').on('change', function() {
            if (isNaN($(this).val())) $(this).val('');
        });
    },
    autoSelectQuarter: function(selectElem) {
        var catchDate = $('#catchDate').val();
        if (catchDate) {
            var catchMonth = ''+parseInt(catchDate.split('-')[1]);
            var months = [];
            $(selectElem).children('option').each(function () {
                months = $(this).val().substring($(this).val().indexOf(';') + 1).split(',');
                if ($.inArray(catchMonth, months) > -1) {
                    $(this).prop('selected', true).prop('disabled', false);
                } else {
                    $(this).prop('selected', false).prop('disabled', true);
                }
            });
        }
    },
    deleteTSS: function(tssId) {
        observer.doDeleteTSSs([tssId]);
    },
    editTSS: function(tssId) {
        window.location.href = editPath + '#' + tssId;
    },
    doDeleteTSSs: function(checkedTSSs) {
        if (checkedTSSs && checkedTSSs.constructor === Array && checkedTSSs.length > 0) {
            $.confirmAction(function() {
                $.processPromise(new Promise(function(doReturn) {
                    var dt = $('#online-table').DataTable();
                    checkedTSSs.forEach(function(tssId, index) {
                        $.ajax({
                            url: deletePath,
                            type: 'DELETE',
                            contentType: 'text/plain',
                            data: tssId,
                            success: function(data) {
                                if (data === 'success') {
                                    dt.row($('#delete_' + tssId).parents('tr')).remove();
                                    if ((index + 1) === checkedTSSs.length) {
                                        $.showAlert('success', $.getMessage('js.success.tss_deleted', [checkedTSSs.join(', ')]), 10000, 'main');
                                        $('#btnDeleteON').prop('disabled', true);
                                        dt.draw();
                                        doReturn();
                                    }
                                } else {
                                    $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'main');
                                    dt.draw();
                                    doReturn();
                                }
                            },
                            error: function() {
                                $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'main');
                                dt.draw();
                                doReturn();
                            }
                        });
                    });
                }));
            }, 'js.confirm.delete_records', [checkedTSSs.join(', ')]);
        }
    },
    doMarkTSSs: function(checkedTSSs, newStatusId) {
        if (checkedTSSs && checkedTSSs.constructor === Array && checkedTSSs.length > 0) {
            $.confirmAction(function() {
                $.processPromise(new Promise(function(doReturn) {
                    var dt = $('#online-table').DataTable();
                    var row;
                    var markedTSSs = [];
                    checkedTSSs.forEach(function(tssId, index) {
                        $.ajax({
                            url: markPath,
                            type: 'PATCH',
                            contentType: 'text/plain',
                            data: tssId+','+newStatusId,
                            success: function(data) {
                                if (data !== 'error') {
                                    row = dt.row($('#delete_' + tssId).parents('tr')).node();
                                    dt.cell(row, 7).data(data);
                                    markedTSSs.push(tssId);
                                    if ((index + 1) === checkedTSSs.length) {
                                        if (markedTSSs.length > 0) {
                                            $.showAlert('success', $.getMessage('js.success.records_marked', [markedTSSs.join(', ')]), 10000, 'main');
                                        }
                                        dt.draw();
                                        doReturn();
                                    }
                                } else {
                                    if ((index + 1) === checkedTSSs.length) {
                                        dt.draw();
                                        doReturn();
                                    }
                                }
                            },
                            error: function() {
                                if ((index + 1) === checkedTSSs.length) {
                                    dt.draw();
                                    doReturn();
                                }
                            }
                        });
                    });
                }));
            }, newStatusId === 34 ? 'js.confirm.mark_records_distributed' : 'js.confirm.mark_records', [checkedTSSs.join(', ')]);
        }
    },
    renderSummaryDataTable: function(min, max) {
        min = isNaN(min) ? parseInt(min) : min;
        max = isNaN(max) ? parseInt(max) : max;

        var RC = 'lf-summ'; var RC_ = ' '+RC+'-';
        var COLS = ['o'];
        var TYPES = ['u','m','f'];

        var html = '';
        var field;
        for (var y = 0; y <= max; y++) {
            html += '<tr';
            if (y < min) {
                html += ' class="hidden"';
            }
            html += '>';
            html += '<td class="'+RC+'-cm">'+y+'</td>';
            for (var z = 0; z < TYPES.length; z++) {
                for (var x = 0; x < COLS.length; x++) {
                    field = TYPES[z]+'_'+COLS[x];
                    html += '<td class="'+RC_.substring(1)+COLS[x]+RC_+TYPES[z]+'">';
                    html += '<input type="number" id="';
                    html += field+'['+y+']" name="';
                    html += field+'['+y+']" value="0" min="0" ';
                    html += 'pattern="\\d*" class="lf-summ-num" oninput="app.validateIsNumber(this); app.validateLengthOfNumber(this, 5)"';
                    html += ' onblur="app.enforceDefaultValue(this, \'0\')" />';
                    html += '</td>';
                }
            }
            html += '</tr>';
        }
        $('#tss-summ-data-tbody').html(html);
        $('.lf-summ-m,.lf-summ-f,.lf-summ-u').show();
    },
    calcTotals: function() {
        var genders = ['u','m','f'];
        var samplings = ['o'];
        var totals = [];
        var num, identifier;
        var tblTotals = $('.lf-summ-totals');

        for (var g = 0; g < genders.length; g++) {
            for (var s = 0; s < samplings.length; s++) {
                identifier = '.lf-summ-'+samplings[s]+'.lf-summ-'+genders[g];
                totals[identifier] = 0;
                $(identifier+' input[type="number"]').each(function() {
                    num = $(this).val();
                    if (num !== '0') {
                        totals[identifier] += parseInt(num);
                    }
                });
                $(tblTotals).find(identifier).html(totals[identifier]);
            }
        }
    },
    getOnlineTSS: function(tssId) {
        return new Promise(function(doReturn) {
            $.ajax({
                url: getTSSPath,
                type: 'GET',
                data: 'tssId='+tssId,
                success: function(jsonData) {
                    $.jsonRevertFormArrays(jsonData, ['entries']);
                    doReturn(jsonData);
                },
                error: function() {
                    doReturn(false);
                }
            });
        });
    },
    saveTripSetSpecies: function(event, isUpdate, thenFunction) {
        event.preventDefault();

        if (typeof isUpdate === 'undefined' || isUpdate === null) {
            isUpdate = false;
        }

        $.processPromise(new Promise(function(doReturn) {
            var tssForm = $('#tripSetSpeciesForm');
            var dataToSend = $(tssForm).serializeJSON();
            var csrfToken = dataToSend._csrf;
            delete dataToSend._csrf;
            var destination = $(tssForm).attr('action') + '?save=true&_csrf='+csrfToken;
            if (isUpdate) {
                $.jsonConvertFormArrays(dataToSend);
            }
            $.ajax({
                url: destination,
                data: JSON.stringify(dataToSend),
                type: 'POST',
                contentType: 'application/json',
                success: function(status) {
                    if (status.status === 'SUCCESS') {
                        doReturn(status.id);
                    } else {
                        doReturn('ERROR:' + status.errorKey);
                    }
                },
                error: function() {
                    doReturn('ERROR:js.error.serverDB_operation_failed');
                }
            });
        }), function(tssId) {
            thenFunction(tssId);
        });
    },
    viewSamplingData: function() {
        var sId = $('#tssId').val();
        if (sdPath) window.location.href = sdPath + '?sId=' + sId;
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/list', observer.initList);
    $.initForPathContains('/create', observer.initCreate);
    $.initForPathContains('/edit', observer.initEdit);
});