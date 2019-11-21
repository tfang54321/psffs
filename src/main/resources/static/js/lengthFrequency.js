var lengthFrequency = {
    //VARIABLES
    editLFEntries: [],
    serverRecordsFetched: false,
    counts: {},
    sexTypeVal: '8',
    //INIT FUNCTIONS
    initOfflineCreate: function() {
        $.hideDisabledLookups();
        lengthFrequency.initLFHeaderFormValidations();

        $('#create-lf-form').on('submit', function (e) {
            e.preventDefault();
            var validator = $(this).validate();
            validator.form();
            if (!validator.numberOfInvalids()) {
                var destination = $(this).attr('action');
                var dataJSON = $(this).serializeJSON();
                var initials = lengthFrequency.resolveInitials();
                $.processPromise(lengthFrequency.createOfflineLF('LOCAL-' + app.currentYear() + '-' + initials + '-', dataJSON, destination));
            }
        });

        $('#create-lf-form').on('reset', function() {
            setTimeout(function() {
                $('select[data-searchable=true]').trigger("change.select2");
            }, 0);
        });
    },
    initList: function() {
        var hash = window.location.hash;
        if (typeof hash !== 'undefined' && hash != null && hash.length > 1) {
            var workedLFId = hash.substring(hash.indexOf('#') + 1);
            if (workedLFId.indexOf('LOCAL') === 0) {
                $.showAlert('success',$.getMessage('js.success.lf_created', [workedLFId]), 10000,
                    'local-list');
            } else {
                $.showAlert('success', $.getMessage('js.success.length_frequency_updated', [workedLFId]), 10000,
                    'server-list');
            }
            window.location.hash = '';
        }

        var fetchServerRecords = function(isRefresh) {
            lengthFrequency.serverRecordsFetched = true;
            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    $.ajax({
                        type: 'GET',
                        url: listPath,
                        data: 'year=' + $('#filterYear').children('option:selected').val(),
                        success: function (data) {
                            if (data.lengthFrequencies) {
                                var settings = {
                                    data: data.lengthFrequencies,
                                    order: [[0, 'desc']],
                                    lengthMenu: [10,25,50,100,250,500],
                                    columns: [
                                        {data: 'id'},
                                        {data: 'lfId'},
                                        {data: 'sampleSpeciesIdText'},
                                        {data: 'nafoDivisionIdText'},
                                        {data: 'gearIdText'},
                                        {data: 'statusIdText'},
                                        {data: 'buttons', orderable: false, className: 'hidden-print'},
                                        {data: 'checkbox', orderable: false, className: 'hidden-print'}
                                    ]
                                };

                                if (!isRefresh) {
                                    $('#online-table').attr('data-wb-tables', JSON.stringify(settings)).addClass('wb-tables').trigger('wb-init.wb-tables').on('change', 'input.lf-on-list-checkbox', function () {
                                        var isChecked = $(this).is(':checked');
                                        if ($(this).val() === '') {
                                            $('input.lf-on-list-checkbox').prop('checked', isChecked);
                                        } else {
                                            $('input.lf-on-list-checkbox').each(function (index, elem) {
                                                if ($(elem).val() !== '' && $(elem).is(':checked')) {
                                                    isChecked = true;
                                                }
                                            });
                                            if (!isChecked) {
                                                $('input[value=""].lf-on-list-checkbox').prop('checked', false);
                                            }
                                        }

                                        $('#btnDeleteON,#btnMarkON').prop('disabled', !isChecked);
                                    });
                                } else {
                                    var dt = $('#online-table').DataTable();
                                    dt.clear().rows.add(data.lengthFrequencies).draw();
                                }
                            }
                        },
                        error: function () {
                            $.showAlert('danger', $.getMessage('js.error.failed_to_fetch_data'), 10000, 'server-list');
                        }
                    })
                }
            });
        };

        $(document).on('app.offline app.online', function(e) {
            if (e.namespace === 'offline') {
                $('#server-offline-message').show();
                $('#server-list').hide();
            } else {
                $('#server-offline-message').hide();
                $('#server-list').show();
                if (!lengthFrequency.serverRecordsFetched) {
                    fetchServerRecords(false);
                }
            }
        });
        $('#btnDeleteOL,#btnDeleteON').on('click', function() {
            var listType = $(this).attr('id') === 'btnDeleteON' ? 'on' : 'off';
            var checkedLFs = $('input[value!=""].lf-'+listType+'-list-checkbox:checkbox:checked').map(function(){return this.value;}).get();
            if (checkedLFs && checkedLFs.constructor === Array && checkedLFs.length > 0) {
                $.confirmAction(function() {
                    $.processPromise(new Promise(function(doReturn) {
                        var dt = $('#'+listType+'line-table').DataTable();
                        checkedLFs.forEach(function(lfId, index) {
                            if (listType === 'on') {
                                $.ajax({
                                    url: deletePath,
                                    type: 'DELETE',
                                    contentType: 'text/plain',
                                    data: lfId,
                                    success: function(data) {
                                        if (data === 'success') {
                                            dt.row($('#delete_' + lfId).parents('tr')).remove();
                                            if ((index + 1) === checkedLFs.length) {
                                                $.showAlert('success', $.getMessage('js.success.lf_deleted', [checkedLFs.join(', ')]), 10000, 'server-list');
                                                $('#btnDeleteON').prop('disabled', true);
                                                dt.draw();
                                                doReturn();
                                            }
                                        } else {
                                            $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'server-list');
                                            dt.draw();
                                            doReturn();
                                        }
                                    },
                                    error: function() {
                                        $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'server-list');
                                        dt.draw();
                                        doReturn();
                                    }
                                });
                            } else {
                                $.db_deleteByKeyPath('length_frequencies', lfId).then(function (result) {
                                    if (result) {
                                        dt.row($('#delete_' + lfId).parents('tr')).remove();
                                        if ((index + 1) === checkedLFs.length) {
                                            $.showSuccess('js.success.lf_deleted', [checkedLFs.join(', ')]);
                                            $('#btnSync').prop('disabled', true);
                                            $('#btnDeleteOL').prop('disabled', true);
                                            dt.draw();
                                            doReturn();
                                        }
                                    } else {
                                        $.showError('js.error.indexedDB_operation_failed');
                                        dt.draw();
                                        doReturn();
                                    }
                                });
                            }
                        });
                    }));
                }, 'js.confirm.delete_records', [checkedLFs.join(', ')]);
            }
        });
        $('#btnMarkON').on('click', function() {
            var checkedLFs = $('input[value!=""].lf-on-list-checkbox:checkbox:checked').map(function(){return this.value;}).get();
            if (checkedLFs && checkedLFs.constructor === Array && checkedLFs.length > 0) {
                $.confirmAction(function() {
                    $.processPromise(new Promise(function(doReturn) {
                        var dt = $('#online-table').DataTable();
                        var row;
                        var markedLFs = [];
                        var errorLFs = [];
                        checkedLFs.forEach(function(lfId, index) {
                            $.ajax({
                                url: markPath,
                                type: 'PATCH',
                                contentType: 'text/plain',
                                data: lfId,
                                success: function(data) {
                                    if (data !== 'error') {
                                        row = dt.row($('#delete_' + lfId).parents('tr')).node();
                                        dt.cell(row, 4).data(data);
                                        markedLFs.push(lfId);
                                        if ((index + 1) === checkedLFs.length) {
                                            if (markedLFs.length > 0) {
                                                $.showAlert('success', $.getMessage('js.success.records_marked', [markedLFs.join(', ')]), 10000, 'server-list');
                                            }
                                            dt.draw();
                                            doReturn();
                                        }
                                    } else {
                                        errorLFs.push(lfId);
                                        if ((index + 1) === checkedLFs.length) {
                                            if (errorLFs.length > 0) {
                                                $.showAlert('danger', $.getMessage('js.error.records_marked', [errorLFs.join(', ')]), 10000, 'server-list');
                                            }
                                            dt.draw();
                                            doReturn();
                                        }
                                    }
                                },
                                error: function() {
                                    errorLFs.push(lfId);
                                    if ((index + 1) === checkedLFs.length) {
                                        $.showAlert('danger', $.getMessage('js.error.records_marked', [errorLFs.join(', ')]), 10000, 'server-list');
                                        dt.draw();
                                        doReturn();
                                    }
                                }
                            });
                        });
                    }));
                }, 'js.confirm.mark_records', [checkedLFs.join(', ')]);
            }
        });
        $('#btnSync').on('click', lengthFrequency.syncToServer);

        $.db_getAll('length_frequencies').then(function (results) {
            if (results !== undefined && results !== null && results.length > 0) {
                var data = [];
                for (var x=0;x<results.length;x++) {
                    if (!results[x].sampleSpeciesIdText) results[x].sampleSpeciesIdText = '';
                    if (!results[x].nafoDivisionIdText) results[x].nafoDivisionIdText = '';
                    if (!results[x].gearIdText) results[x].gearIdText = '';
                    data[x] = $.cloneAndPluck(results[x], ['id', 'lfId', 'sampleSpeciesIdText', 'nafoDivisionIdText', 'gearIdText', 'statusIdText']);
                    data[x]['buttons'] = lengthFrequency.renderOfflineListDatatablesButtons(data[x]['lfId']);
                    data[x]['checkbox'] = '<input type="checkbox" class="lf-off-list-checkbox list-checkbox" value="'+data[x]['lfId']+'" />';
                }
                var settings = {
                    data: data,
                    order: [[0, 'desc']],
                    columns: [
                        {data: 'id'},
                        {data: 'lfId'},
                        {data: 'sampleSpeciesIdText'},
                        {data: 'nafoDivisionIdText'},
                        {data: 'gearIdText'},
                        {data: 'statusIdText'},
                        {data: 'buttons', orderable: false},
                        {data: 'checkbox', orderable: false}
                    ]
                };
                $('#offline-table').attr('data-wb-tables', JSON.stringify(settings)).addClass('wb-tables').trigger('wb-init.wb-tables').on('change', 'input.lf-off-list-checkbox', function() {
                    var isChecked = $(this).is(':checked');
                    if ($(this).val() === '') {
                        $('input.lf-off-list-checkbox').prop('checked', isChecked);
                    } else {
                        $('input.lf-off-list-checkbox').each(function (index, elem) {
                            if ($(elem).val() !== '' && $(elem).is(':checked')) {
                                isChecked = true;
                            }
                        });
                        if (!isChecked) {
                            $('input[value=""].lf-off-list-checkbox').prop('checked', false);
                        }
                    }

                    $('#btnDeleteOL').prop('disabled', !isChecked);
                    $.isOnline().then(function (isOnline) {
                        if (isOnline) {
                            $('#btnSync').prop('disabled', !isChecked);
                        } else {
                            $('#btnSync').prop('disabled', true);
                        }
                    });
                });

                $(document).on('app.offline', function() {
                    $('#btnSync').prop('disabled', true);
                });
                $(document).on('app.online', function() {
                    if ($('input[value!=""].lf-off-list-checkbox:checkbox:checked').length) {
                        $('#btnSync').prop('disabled', false);
                    }
                });
            } else {
                var tr = '<tr>';
                tr += '<td colspan="7" align="center">'+$.getMessage('js.info.no_data_found')+'</td>';
                tr += '</tr>';
                $('#offline-table').find('tbody').append(tr);
                $('#btnSync').prop('disabled', true);
            }
        });

        $('#btnRefresh').on('click', function() {fetchServerRecords(true)});
    },
    initEdit: function() {
        //EVENT HANDLING
        var formContainer = $('#edit-lf-form-container').hide();
        var lfId = window.location.hash.substring(1);
        $('#vesselId').on('change', function() {
            var vesselId = $(this).children('option:selected').val();
            var vDetails = $('#vessel-details');
            var noDetails = $('#no-vessel-details');
            var vDetailsSelects = $(vDetails).find('select');
            if (vesselId !== '' && vesselId !== 'other') {
                $.db_getByKeyPath('vessels', parseInt(vesselId)).then(function(vessel) {
                    if (vessel) {
                        $(noDetails).hide();
                        $(vDetails).show();
                        $(vDetailsSelects).prop('disabled', false).setReadonly(false);
                        $(vDetails).bindFromJSON(vessel, 'id', 'vessel_');
                        if (vessel.lengthCategoryId !== null) {
                            $('#vessel_lengthCategoryId').children('option').each(function(index, opt) {
                                $(opt).prop('selected', false);
                                var optVal = $(opt).val();
                                if (optVal !== '' && optVal.substring(0, optVal.indexOf(';')) === (''+vessel.lengthCategoryId)) {
                                    $('#vessel_lengthCategoryId').prop('selectedIndex', index);
                                }
                            });
                        }
                        $(vDetailsSelects).setReadonly(true);
                        $('#vessel_vesselLengthMeters').trigger('change');
                        var desc;
                        if ($('html')[0].lang === 'fr') {
                            desc = vessel.frenchDescription;
                        } else {
                            desc = vessel.englishDescription;
                        }
                        $('#vessel_description').val(desc);
                        $.convertDataTextOnly();
                    }
                });
            } else {
                $(noDetails).show();
                $(vDetails).hide();
                $(vDetailsSelects).prop('disabled', true);
            }
        });
        $('#sexTypeId').on('change', function(event) {
            $.confirmAction(function() {
                $('#lf-summ-container').hide();
                $('#lf-summ-data-tbody').html('');
                $('#lf-summ-loading').show();
                $('#countsModified').val('false');
                $('#entriesModified').val('false');
                lengthFrequency.editLFEntries = [];
                lengthFrequency.counts = {};
                lengthFrequency.sexTypeVal = $(event.target).children('option:selected').val();
                $('#lf-summ-container').hide();
                $('details#dt-frequency-summ').prop('data-loaded', false).trigger('details.opened');
                $('button.lf-summ-gnd-btn').parent().hide();
                var activeGenders = lengthFrequency.determineActiveGenders();
                for (var x = 0; x < activeGenders.length; x++) {
                    $('button.lf-summ-gnd-btn[data-selection="'+activeGenders[x]+'"]').parent().show();
                }
            }, 'js.confirm.change_sex_type', null, function() {
                $(event.target).children('option[value="'+lengthFrequency.sexTypeVal+'"]').prop('selected', true);
            });
        });
        $(document).on('keyup keydown', lengthFrequency.bindKeysToKeypad);
        $('.btn-math-op').on('click', function() {
            $('.btn-math-op').removeClass('btn-default').removeClass('btn-primary').addClass('btn-default');
            $(this).removeClass('btn-default').addClass('btn-primary');
            lengthFrequency.updateKeypadOpLbl();
        });
        $('#math-op-nbr').on('change', function(e) {
            app.enforceDefaultValue(this, '1');
            lengthFrequency.updateKeypadOpLbl();
        });
        $('input.lf-catch-loc').on('change', function() {
            $('select.lf-catch-loc').prop('disabled', true).hide();
            $('label.lf-catch-loc').hide().attr('class', 'lf-catch-loc');
            if ($(this).is(':checked')) {
                $('select[data-catch-loc="'+$(this).val()+'"]').each(function() {
                    $(this).prop('disabled', false).show().attr('class', 'lf-catch-loc field-name');
                    $('label[for="'+$(this).attr('id')+'"]').show();
                    lengthFrequency.autoSelectQuarter(this);
                });
            }
        });
        $('input#catchDate').on('blur', function() {
            $('select.lf-catch-loc').each(function() {
                if (!$(this).is(':disabled')) {
                    lengthFrequency.autoSelectQuarter(this);
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
        $('#btn-keypad-modal').on('click', function() {
            $('#modal-keypad').trigger('open.wb-overlay');
            setTimeout(function() {
                lengthFrequency.resetKeypadState();
                lengthFrequency.updateEntryHistory(lengthFrequency.editLFEntries);
            }, 500);
        });
        $('.btn-keypad').on('click', lengthFrequency.keypadPress);
        $('button.lf-summ-gnd-btn').on('click', function() {
            if (!$(this).hasClass('btn-primary')) {
                $('button.lf-summ-gnd-btn').removeClass('btn-primary').removeClass('btn-default').addClass('btn-default');
                $(this).removeClass('btn-default');
                $(this).addClass('btn-primary');
                lengthFrequency.updateKeypadOpLbl();
                lengthFrequency.showRelevantSamplingRequirements();
            }
        });
        $('button.lf-summ-smp-btn').on('click', function() {
            var glyph = $(this).find('span.glyphicon');
            if ($(this).hasClass('btn-default')) {
                $(this).removeClass('btn-default');
                $(this).addClass('btn-primary');
                $(glyph).removeClass('glyphicon-unchecked');
                $(glyph).addClass('glyphicon-check');
            } else {
                $(this).removeClass('btn-primary');
                $(this).addClass('btn-default');
                $(glyph).removeClass('glyphicon-check');
                $(glyph).addClass('glyphicon-unchecked');
            }
            lengthFrequency.updateKeypadOpLbl();
            lengthFrequency.showRelevantSamplingData();
        });
        $('#lengthUnitId').on('change', function() {
             $('.lf-summ-length-unit').html($(this).children('option:selected').text());
        });
        $('input.lf-sampling-req').on('change', function() {
           var sampling;
           var disabledFeature = $(this).val() === '0';
           switch ($(this).attr('id')) {
               case 'reqOtoliths': sampling = 'o';
                    // $('td.lf-summ-'+sampling).find('input.lf-summ-num').prop('readonly', disabledFeature);
                    break;
               case 'reqStomachs': sampling = 's'; break;
               case 'reqFrozens': sampling = 'c'; break;
               case 'reqWeights': sampling = 'w'; break;
           }
           $('button.lf-summ-smp-btn[data-selection="_'+sampling+'"]').prop('disabled', disabledFeature);
        });
        $('#sampleSpeciesId').on('change', function() {
            var conversionElem = $('#conversionFactorData');
            var factorData = $(conversionElem).children('option').map(function() {return this.value;}).get();
            var chosenSpecies = $(this).children('option:selected').val();
            $(conversionElem).prop('selectedIndex', 0).children('option').show();
            if (chosenSpecies) {
                factorData.forEach(function (factor, index) {
                    var factorSplit = factor.split(';');
                    if (factorSplit[2] !== '') {
                        var species = factorSplit[2].split(',');
                        if (!species.includes(chosenSpecies)) {
                            $(conversionElem).children('option[value="' + factor + '"]').hide();
                        }
                    }
                });
            }
            $(conversionElem).trigger('change');
        });
        $('#conversionFactorData').on('change', function() {
            $('#turnoutWeightKilograms,#sampleWeightKilograms').trigger('change');
        });
        var kgToLibsChangeFunc = function(index, elem) {
            var classToFind = $(elem).hasClass('turnout-kg-lbs') ? 'turnout-kg-lbs' : 'sample-kg-lbs';
            $(elem).on('change', function() {
                if (!isNaN($(elem).val())) {
                    var factor = parseFloat($('#conversionFactorData').children('option:selected').val().split(';')[1]);
                    var op = index === 0 ? '*' : '/';
                    var otherFieldId = $($('input.' + classToFind)[index === 0 ? 1 : 0]).attr('id');
                    var settings = {factor: factor, op: op, fieldId: otherFieldId, roundUp: false};
                    app.convertNumberValue(elem, settings);
                } else {
                    $(elem).val('');
                }
            });
        };
        $('input.turnout-kg-lbs').each(kgToLibsChangeFunc);
        $('input.sample-kg-lbs').each(kgToLibsChangeFunc);
        $('#soakTimeHours').on('change', function() {
           var strVal = $(this).val();
           if (isNaN(strVal)) {
               $(this).val('');
           } else {
               $(this).val(parseFloat(strVal).toFixed(2));
            }
        });
        $('table#tbl-lf-summary').on('change', 'input.lf-summ-num', function() {
            var entryCount = lengthFrequency.determineEntryCountByField($(this).attr('name'));
            var fieldCount = parseInt($(this).val());
            if (fieldCount < entryCount) {
                $(this).val(entryCount);
            }
            lengthFrequency.calcTotals(true);
            $('#countsModified').val('true');
        });
        $('#edit-lf-form').on('submit', function (e) {
            e.preventDefault();
            var form = this;
            var proceed = function() {
                $.processPromise(lengthFrequency.updateLF(form), function (errorKey) {
                    if (!errorKey) {
                        var workedLFId = $('#lfId').val();
                        if (workedLFId.indexOf('LOCAL') === 0) {
                            $.showSuccess('js.success.length_frequency_updated', [workedLFId]);
                            $('#countsModified,#entriesModified').val('false');
                            $.hideDisabledLookups();
                        } else {
                            window.location.href = listPath + '#' + workedLFId;
                        }
                    } else {
                        $.showError(errorKey);
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
        $("#latitude").on("input", function () {
            var lat = $("#latitude").attr("max");
            app.validateLengthOfNumber(this, lat.length)

        });
        $("#longitude").on("input", function () {
            var long = $("#longitude").attr("max");
            app.validateLengthOfNumber(this, long.length)
        });
        //LOAD RECORD
        var promiseToProcess;
        var recordIsLocal = lfId.indexOf('LOCAL-') === 0;
        if (recordIsLocal) {
            promiseToProcess = $.db_getByKeyPath('length_frequencies', lfId);
        } else {
            promiseToProcess = lengthFrequency.getOnlineLF(lfId);
            $('#entriesModified,#countsModified,#checkedBy').prop('disabled', false);
            $('#localLfId').parent().parent().show();
            $('#checkedBy').parent().show();
            $('#btn-view-sd').show();
        }
        $.processPromise(promiseToProcess, function(jsonData) {
            if (jsonData) {
                lengthFrequency.counts = app.removeUnwantedData(jsonData, ['m[', 'f[', 'u[', 'm_', 'f_', 'u_']);
                $('#edit-lf-form').bindFromJSON(jsonData);
                $('#vesselId,#nafoDivisionId,#lengthUnitId,#sampleSpeciesId,#meshSizeMillimeters,#depthMeters,.lf-sampling-req,input.lf-catch-loc:checked').trigger('change');
                $('#sampleSpeciesId').attr('data-text-only', 'true');
                $.hideDisabledLookups();
                $.convertDataTextOnly();
                lengthFrequency.editLFEntries = jsonData.entries;
                var statusId = parseInt($('#statusId').children('option:selected').val());
                if (statusId === 1 || statusId === 2) {
                    $('#lfSubmit').prop('disabled', false);
                    $('#modal-keypad-body').attr('data-keypad-disabled', false);
                    lengthFrequency.initLFHeaderFormValidations();
                    if (jsonData.sexTypeId) {
                        lengthFrequency.sexTypeVal = jsonData.sexTypeId;
                    }
                } else {
                    $('#edit-lf-form').setReadonly(true);
                    $('#modal-keypad-body').attr('data-keypad-disabled', true);
                    $('#lfSubmit').prop('disabled', true);
                    $.showAlert('warning', $.getMessage('js.warning.record_readonly'), 0, 'main');
                }
            } else {
                $.showError('js.error.record_not_found');
                $('details#dt-frequency-summ').prop('data-disabled', true);
                $('#lfSubmit').prop('disabled', true);
                $('#edit-lf-form').find('input,textarea,select').prop('disabled', true);
            }
            $(formContainer).show();
            $('#nafoDivisionId').trigger("change");
        });
        $('details#dt-frequency-summ').on('details.opened', function(event) {
            if (!$(event.target).prop('data-disabled')) {
                if (!$('#lf-summ-container').is(':visible')) {
                    $('#lf-summ-loading').show();
                    (new Promise(function (resolve) {
                        lengthFrequency.renderSummaryDataTable($('#lengthGroupMin').val(), $('#lengthGroupMax').val());
                        $(event.target).bindFromJSON(lengthFrequency.counts);
                        lengthFrequency.calcTotals(false, true);
                        lengthFrequency.updateEntryHistory(lengthFrequency.editLFEntries);
                        var statusId = parseInt($('#statusId').children('option:selected').val());
                        if (statusId === 1 || statusId === 2) {
                            $('input.lf-summ-num-l').Numbler();
                        } else {
                            $(event.target).setReadonly(true);
                        }
                        $(event.target).prop('data-loaded', true);
                        resolve();
                    })).then(function () {
                        $('#lf-summ-loading').hide();
                        $('#lf-summ-container').show();
                        $('thead.lf-summ').find('th:not(.lf-summ-cm)').hide();
                        switch ($('#sexTypeId').children('option:selected').val()+'') {
                            case '8':
                                $('.lf-summ-u').show().find('input.lf-summ-num').prop('disabled', false);
                                $('.lf-summ-l').find('input.lf-summ-num').prop('readonly', false);
                                $('.lf-summ-o').find('input.lf-summ-num').prop('readonly', true);
                                $('.lf-maturity-level-container').hide();
                                break;
                            case '9':
                                $('.lf-summ-m,.lf-summ-f').show().find('input.lf-summ-num').prop('disabled', false);
                                $('.lf-summ-l').find('input.lf-summ-num').prop('readonly', false);
                                $('.lf-summ-o').find('input.lf-summ-num').prop('readonly', true);
                                $('.lf-maturity-level-container').hide();
                                break;
                            case '10':
                                $('.lf-summ-u,.lf-summ-m,.lf-summ-f').show().find('input.lf-summ-num').prop('disabled', false);
                                $('.lf-summ-l,.lf-summ-o').find('input.lf-summ-num').prop('readonly', true);
                                $('.lf-maturity-level-container').show();
                                break;
                        }
                        var minLengthGroup = parseInt($('#lengthGroupMin').val());
                        var maxLengthGroup = parseInt($('#lengthGroupMax').val());
                        var range = maxLengthGroup - minLengthGroup;
                        var focusIndex = Math.round(range * 0.28);
                        $('input[name="u[' + focusIndex + ']"],input[name="m[' + focusIndex + ']"],input[name="f[' + focusIndex + ']"]').each(
                            function () {
                                if ($(this).is(':visible')) {
                                    $(this).focus();
                                    return false;
                                }
                            }
                        );
                    });
                }
            }
        });
    },
    //PAGE FUNCTIONS
    createOfflineLF: function(idPrefix, dataJSON, destination) {
        return new Promise(function(doReturn) {
            $.db_getAndIncrementSeq('length_frequency_seq', 9999).then(function (idSeq) {
                if (idSeq) {
                    var speciesId = parseInt($('#sampleSpeciesId').val());
                    lengthFrequency.getSettingsBySpeciesAndYear(speciesId, app.currentYear()).then(function(settings) {
                        if (settings) {
                            var paddedId = $.padZeroes(idSeq, 4);
                            dataJSON.id = idSeq;
                            dataJSON.lfId = idPrefix + paddedId;
                            dataJSON.lengthGroupId = settings.lengthGroupId;
                            dataJSON.lengthUnitId = settings.lengthUnitId;
                            dataJSON.measuringTechniqueId = settings.measuringTechniqueId;
                            dataJSON.reqOtoliths = settings.otolithsDefaultFrequency;
                            dataJSON.reqStomachs = settings.stomachsDefaultFrequency;
                            dataJSON.reqFrozens = settings.frozensDefaultFrequency;
                            dataJSON.reqWeights = settings.weightsDefaultFrequency;
                            dataJSON.perNthOtoliths = settings.perNthOtoliths;
                            dataJSON.perNthStomachs = settings.perNthStomachs;
                            dataJSON.perNthFrozens = settings.perNthFrozens;
                            dataJSON.perNthWeights = settings.perNthWeights;
                            dataJSON.sexedOtoliths = settings.sexedOtoliths;
                            dataJSON.sexedStomachs = settings.sexedStomachs;
                            dataJSON.sexedFrozens = settings.sexedFrozens;
                            dataJSON.sexedWeights = settings.sexedWeights;
                            dataJSON.lengthGroupMin = settings.lengthGroupMin;
                            dataJSON.lengthGroupMax = settings.lengthGroupMax;
                            dataJSON.entries = [];
                            $.db_insertOrUpdate('length_frequencies', dataJSON).then(function (result) {
                                if (result) {
                                    window.location.href = destination + '#' + dataJSON.lfId;
                                } else {
                                    $.showError("js.error.create_length_frequency_failed");
                                    console.error('An error occurred during an attempt to insert the record into indexedDB');
                                    doReturn();
                                }
                            });
                        } else {
                            $.showError("js.error.create_length_frequency_failed");
                            console.error('An error occurred during an attempt to get trip settings from indexedDB');
                            doReturn();
                        }
                    });
                } else {
                    $.showError('js.error.seq_gen_failed');
                    doReturn();
                }
            });
        });
    },
    renderOfflineListDatatablesButtons: function(lfId) {
        var wip = '<a href="javascript: lengthFrequency.editLF(\'' + lfId + '\')" class="btn btn-link cell-btn large-buttons" title="' + $.getMessage('js.icon.edit.title') + '">';
        wip += '<span class="glyphicon glyphicon-edit"></span></a>';
        wip += '<a id="delete_'+lfId+'" href="javascript: lengthFrequency.deleteLF(\'' + lfId + '\')" class="btn btn-link cell-btn large-buttons" title="' + $.getMessage('js.icon.delete.title') + '">';
        wip += '<span class="glyphicon glyphicon-trash"></span></a>';
        return wip;
    },
    deleteLF: function(lfId) {
        $.confirmAction( function() {
            $.processPromise(new Promise(function(doReturn) {
                if (lfId.indexOf('LOCAL-') === 0) {
                    $.db_deleteByKeyPath('length_frequencies', lfId).then(function (result) {
                        if (result) {
                            var dt = $('#offline-table').DataTable();
                            dt.row($('#delete_' + lfId).parents('tr')).remove().draw();
                            $.showSuccess('js.success.lf_deleted', [lfId]);
                            if (!dt.data().any()) {
                                $('#btnSync').prop('disabled', true);
                            }
                            doReturn();
                        } else {
                            $.showError('js.error.indexedDB_operation_failed');
                            doReturn();
                        }
                    });
                } else {
                    $.ajax({
                        url: deletePath,
                        type: 'DELETE',
                        contentType: 'text/plain',
                        data: lfId,
                        success: function(data) {
                            if (data === 'success') {
                                var dt = $('#online-table').DataTable();
                                dt.row($('#delete_' + lfId).parents('tr')).remove().draw();
                                $.showAlert('success', $.getMessage('js.success.lf_deleted', [lfId]), 10000, 'server-list');
                            } else {
                                $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'server-list');
                            }
                            doReturn();
                        },
                        error: function() {
                            $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'server-list');
                            doReturn();
                        }
                    });
                }
            }));
        }, 'js.confirm.delete', [lfId]);
    },
    editLF: function(lfId) {
        window.location.href = editPath + '#'+lfId;
    },
    updateLF: function(form) {
        return new Promise(function(doReturn) {
            var validator = $(form).validate();
            validator.form();
            if (!validator.numberOfInvalids()) {
                var dataJSON = $(form).serializeJSON();
                if ($('details#dt-frequency-summ').prop('data-loaded')) {
                    dataJSON.entries = lengthFrequency.editLFEntries;
                } else {
                    dataJSON.entriesModified = false;
                    dataJSON.countsModified = false;
                }
                var lfId = $('#lfId').val();
                if (lfId.indexOf('LOCAL-') === 0) {
                    $.db_getByKeyPath('length_frequencies', lfId).then(function(originalJSON) {
                        if (originalJSON) {
                            Object.keys(dataJSON).forEach(function(key, index) {
                                originalJSON[key] = dataJSON[key];
                            });
                        } else {
                            originalJSON = dataJSON;
                        }

                        $.db_insertOrUpdate('length_frequencies', originalJSON).then(function (result) {
                            if (!result) {
                                doReturn("js.error.update_length_frequency_failed");
                            } else {
                                doReturn(false);
                            }
                        });
                    });
                } else {
                    $.jsonConvertFormArrays(dataJSON);
                    $.ajax({
                       url: syncPath,
                       type: 'POST',
                       data: JSON.stringify(dataJSON),
                       contentType: 'application/json',
                       success: function(result) {
                           if (result.status === 'SUCCESS') {
                               doReturn(false);
                           } else {
                               doReturn(result.errorKey);
                           }
                       },
                       error: function() {
                           doReturn("js.error.update_length_frequency_failed");
                       }
                    });
                }
            }
            else {
                doReturn("js.error.update_length_frequency_failed");
            }
        });
    },
    getOnlineLF: function(lfId) {
        return new Promise(function(doReturn) {
            $.ajax({
                url: getLFPath,
                type: 'GET',
                data: 'lfId='+lfId,
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
    preselectKeypadGender: function() {
        var buttons = $('button.lf-summ-gnd-btn');
        $(buttons).removeClass('btn-primary');
        $(buttons).removeClass('btn-default');
        $(buttons).addClass('btn-default');
        $(buttons).each(function () {
            if ($(this).is(':visible')) {
                $(this).removeClass('btn-default');
                $(this).addClass('btn-primary');
                return false;
            }
        });
    },
    resetKeypadSamplings: function() {
        var selection;
        $('button.lf-summ-smp-btn').each(function() {
           selection = $(this).attr('data-selection');
           if (selection !== '_g') {
               $(this).removeClass('btn-primary');
               $(this).removeClass('btn-default');
               $(this).addClass('btn-default');
               $(this).find('span.glyphicon').attr('class', 'glyphicon glyphicon-unchecked');
           } else {
               $(this).removeClass('btn-primary');
               $(this).removeClass('btn-default');
               $(this).addClass('btn-primary');
               $(this).find('span.glyphicon').attr('class', 'glyphicon glyphicon-check');
           }
        });
    },
    determineSelectedSamplings: function() {
        var samplings = [];
        var selection;
        $('button.lf-summ-smp-btn').each(function () {
           if ($(this).hasClass('btn-primary')) {
               selection = $(this).attr('data-selection');
               if (selection === '_g') selection = '';
               samplings.push(selection);
           }
        });
        return samplings;
    },
    determineSelectedGender: function() {
        var gender = 'u';
        $('button.lf-summ-gnd-btn').each(function() {
            if ($(this).is(':visible') && $(this).hasClass('btn-primary')) {
                gender = $(this).attr('data-selection');
                return false;
            }
        });
        return gender;
    },
    determineMathOp: function() {
        return $('.btn-primary.btn-math-op').attr('data-math-op');
    },
    keypadPress: function() {
        var cmInput = $('#length-cm');
        var cmValue, modeValue, modeInput, modeMaxLength, type, subtypes, subtype, field, fieldVal, mathOp, mathOpInput;
        var mathNbr, isValidEntry, modeOp;
        var btnPushed = $(this).attr('data-keypad-op');
        var updateKeypadWIP = true;
        var resetKeypad = false;
        switch (btnPushed) {
            case 'c':
                lengthFrequency.resetKeypadState();
                break;
            case 'r':
                modeInput = $('input.border-primary-thick');
                modeValue = $(modeInput).val();
                $(modeInput).val(modeValue.substring(0, modeValue.length - 1));
                if ($(modeInput).attr('data-input-op') === 'd') {
                    $(modeInput).trigger('focus keyup');
                }
                lengthFrequency.showRelevantSamplingRequirements();
                break;
            case 'e':
                isValidEntry = $('#lf-summ-keypad-wip').hasClass('label-success');
                if (isValidEntry) {
                    type = lengthFrequency.determineSelectedGender();
                    subtypes = lengthFrequency.determineSelectedSamplings();
                    cmValue = $(cmInput).val();
                    mathOp = lengthFrequency.determineMathOp();
                    mathNbr = parseInt($('#math-op-nbr').val());
                    if (cmValue !== undefined && cmValue !== null && cmValue.length > 0) {
                        cmValue = parseInt(cmValue);
                        while (mathNbr > 0) {
                            for (var f = 0; f < subtypes.length; f++) {
                                subtype = subtypes[f] === '_g' ? '' : subtypes[f];
                                field = $('input[name="' + type + subtype + '[' + cmValue + ']"]');
                                if ($(field).length && !$(field).closest('tr').hasClass('hidden')) {
                                    if (mathOp === '-') {
                                        fieldVal = parseInt($(field).val()) - 1;
                                    } else {
                                        fieldVal = parseInt($(field).val()) + 1;
                                    }
                                    $(field).val(fieldVal >= 0 ? fieldVal : 0);
                                    resetKeypad = true;
                                    if ((f + 1) === subtypes.length) mathNbr--;
                                } else {
                                    var minLength = $('#lengthGroupMin').val();
                                    var maxLength = $('#lengthGroupMax').val();
                                    var lengthUnit = $('#lengthUnitId').children('option:selected').text();
                                    $.showAlert('danger', $.getMessage('js.error.lf_length_not_found', [minLength, maxLength, lengthUnit]), null, 'modal-keypad-body');
                                    updateKeypadWIP = false;
                                    resetKeypad = false;
                                    mathNbr = 0;
                                    break;
                                }
                            }
                        }
                        if (resetKeypad) {
                            var modalKeypad = $('#modal-keypad');
                            var selectedEditButton = $(modalKeypad).find('a.edit-btn.icon-selected');
                            var selectedDeleteButton = $(modalKeypad).find('a.delete-btn.icon-selected');
                            if ($(selectedDeleteButton).length) {
                                lengthFrequency.editLFEntries.splice(parseInt($(selectedDeleteButton).attr('data-index')), 1);
                                lengthFrequency.updateEntryHistory(lengthFrequency.editLFEntries);
                            } else if ($(selectedEditButton).length) {
                                var index = parseInt($(selectedEditButton).attr('data-index'));
                                var newEntry = lengthFrequency.determineEntry();
                                var oldEntry = lengthFrequency.editLFEntries[index];
                                var subtype = '';
                                var field, fieldVal;
                                for (var s = 0; s < oldEntry.samplings.length; s++) {
                                    if (oldEntry.samplings[s] !== 'l') {
                                        subtype = '_' + oldEntry.samplings[s];
                                    }
                                    field = $('input[name="' + oldEntry.sex + subtype + '[' + oldEntry.length + ']"]');
                                    fieldVal = parseInt($(field).val()) - parseInt(oldEntry.increment);
                                    $(field).val(fieldVal >= 0 ? fieldVal : 0);
                                }
                                lengthFrequency.editLFEntries[index] = newEntry;
                                lengthFrequency.updateEntryHistory(lengthFrequency.editLFEntries);
                            } else {
                                mathNbr = parseInt($('#math-op-nbr').val());
                                var origNbr = mathNbr;
                                while (mathNbr > 0) {
                                    lengthFrequency.updateEntryHistory();
                                    if (mathNbr === origNbr) lengthFrequency.resetFieldNbr();
                                    mathNbr--;
                                }
                            }
                            lengthFrequency.resetKeypadState();
                            lengthFrequency.calcTotals();
                            $(field).trigger('change');
                            $('#entriesModified').val('true');
                            $('td.keypad-sampling-summ').hide();
                        }
                    }
                }
                break;
            case 'a':
                modeInput = $('#length-cm');
                mathOpInput = $('#math-op-nbr');
                if ($(mathOpInput).val() === '') $(mathOpInput).val(1);
                $('button[data-keypad-op="x"],button[data-keypad-op="d"]').attr('class', 'btn btn-default btn-keypad');
                $('#math-op-nbr,#lf-maturity-level').removeClass('border-primary-thick');
                $('button[data-keypad-op="a"]').attr('class', 'btn btn-primary btn-keypad');
                $(modeInput).addClass('border-primary-thick');
                break;
            case 'x':
                modeInput = $('#math-op-nbr');
                $('button[data-keypad-op="a"],button[data-keypad-op="d"]').attr('class', 'btn btn-default btn-keypad');
                $('#length-cm,#lf-maturity-level').removeClass('border-primary-thick');
                $('button[data-keypad-op="x"]').attr('class', 'btn btn-primary btn-keypad');
                $(modeInput).addClass('border-primary-thick');
                $(modeInput).val("");
                break;
            case 'd':
                modeInput = $('#lf-maturity-level');
                if ($(modeInput).is(':visible')) {
                    mathOpInput = $('#math-op-nbr');
                    if ($(mathOpInput).val() === '') $(mathOpInput).val(1);
                    $('button[data-keypad-op="a"],button[data-keypad-op="x"]').attr('class', 'btn btn-default btn-keypad');
                    $('#length-cm,#math-op-nbr').removeClass('border-primary-thick');
                    $('button[data-keypad-op="d"]').attr('class', 'btn btn-primary btn-keypad');
                    $(modeInput).addClass('border-primary-thick');
                }
                break;
            default:
                modeInput = $('input.border-primary-thick');
                modeMaxLength = parseInt($(modeInput).attr('maxlength'));
                modeValue = $(modeInput).val() + btnPushed;
                if (modeValue.length > modeMaxLength) {
                    modeValue = modeValue.substring(0, modeMaxLength);
                }
                $(modeInput).val(modeValue);
                modeOp = $(modeInput).attr('data-input-op');
                if (modeOp === 'a') {
                    lengthFrequency.showRelevantSamplingRequirements();
                }
                break;
        }
        if (updateKeypadWIP) {
            lengthFrequency.updateKeypadOpLbl();
        }
    },
    bindKeysToKeypad: function(e) {
        if ($('#modal-keypad').is(':visible') && !$('#lf-maturity-datalist-container').is(':visible')) {
            var dataFocus = false;
            $('#sampling-data').find('input,select,textarea').each(function(index, elem) {
                if ($(elem).is(':focus')) {
                    dataFocus = true;
                    return false;
                }
            });
            var key = e.key | e.keyIdentifier | e.keyCode;
            if (key === 13) {
                e.preventDefault();
            }

            if (!dataFocus) {
                var opUsed, selUsed;
                switch (key) {
                    case 48://0
                    case 96://0(Num)
                        opUsed = '0';
                        break;
                    case 49://1
                    case 97://1(Num)
                        opUsed = '1';
                        break;
                    case 50://2
                    case 98://2(Num)
                        opUsed = '2';
                        break;
                    case 51://3
                    case 99://3(Num)
                        opUsed = '3';
                        break;
                    case 52://4
                    case 100://4(Num)
                        opUsed = '4';
                        break;
                    case 53://5
                    case 101://5(Num)
                        opUsed = '5';
                        break;
                    case 54://6
                    case 102://6(Num)
                        opUsed = '6';
                        break;
                    case 55://7
                    case 103://7(Num)
                        opUsed = '7';
                        break;
                    case 56://8
                    case 104://8(Num)
                        opUsed = '8';
                        break;
                    case 57://9
                    case 105://9(Num)
                        opUsed = '9';
                        break;
                    case 8://Backspace
                        opUsed = 'r';
                        break;
                    case 13://Enter
                        opUsed = 'e';
                        break;
                    case 65://a
                        opUsed = 'a';
                        break;
                    case 88://x
                        opUsed = 'x';
                        break;
                    case 68://d
                        opUsed = 'd';
                        break;
                    case 85://u
                        selUsed = 'u';
                        break;
                    case 77://m
                        selUsed = 'm';
                        break;
                    case 70://f
                        selUsed = 'f';
                        break;
                    case 76://L
                        selUsed = '_g';
                        break;
                    case 79://o
                        selUsed = '_o';
                        break;
                    case 83://s
                        selUsed = '_s';
                        break;
                    case 67://c
                        selUsed = '_c';
                        break;
                    case 87://w
                        selUsed = '_w';
                        break;
                }

                if (opUsed) {
                    var btnUsed = $('button[data-keypad-op="' + opUsed + '"]');
                    if (e.type === 'keyup') {
                        $(btnUsed).removeClass('btn-keypad-pressed');
                        $(btnUsed)[0].click();
                        if (opUsed === 'e' && $(btnUsed).is(':disabled')) {
                            var mathOpInput = $('input[data-input-op="x"]');
                            var lengthOpInput = $('input[data-input-op="a"]');
                            var maturityOpInput = $('input[data-input-op="d"]');
                            if ($(mathOpInput).val() === '') $(mathOpInput).val(1);
                            if ($(maturityOpInput).is(':visible')) {
                                if ($(lengthOpInput).val() === '') {
                                    $('button[data-keypad-op="'+$(lengthOpInput).attr('data-input-op')+'"]')[0].click();
                                } else if ($(maturityOpInput).val() === '') {
                                    $('button[data-keypad-op="'+$(maturityOpInput).attr('data-input-op')+'"]')[0].click();
                                } else if (!lengthFrequency.validateMaturityCode()) {
                                    lengthFrequency.showMaturityLevelDatalist();
                                }
                            }
                        }
                    } else {
                        $(btnUsed).addClass('btn-keypad-pressed');
                    }
                } else if (selUsed && e.type === 'keyup') {
                    $('button[data-selection="' + selUsed + '"]').each(function () {
                        if ($(this).is(':visible')) {
                            this.click();
                            return false;
                        }
                    });
                }
            }
        }
    },
    calcTotals: function(onlyActiveSamplings, rerender) {
        if (onlyActiveSamplings === null || typeof onlyActiveSamplings === 'undefined') {
            onlyActiveSamplings = false;
        }
        if (rerender === null || typeof rerender === 'undefined') {
            rerender = false;
        }

        var genders = lengthFrequency.determineActiveGenders();
        var samplings = onlyActiveSamplings ? lengthFrequency.determineActiveSamplings() : ['l','o','s','c','w'];
        var totals = [];
        var num, identifier;
        var tblTotals = $('.lf-summ-totals');
        if (rerender) $('td.lf-summ-total').hide();
        var identifiedTotal;

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
                identifiedTotal = $(tblTotals).find(identifier);
                $(identifiedTotal).html(totals[identifier]);
                if (rerender) $(identifiedTotal).show();
            }
        }
    },
    determineActiveGenders: function() {
        var genders = [];
        switch ($('#sexTypeId').children('option:selected').val()) {
            case '8':
                genders = ['u']; break;
            case '9':
            case '10':
                genders = ['m','f']; break;
        }
        return genders;
    },
    determineActiveSamplings: function() {
        var activeSamplings = ['l'];
        if ($('#reqOtoliths').val() !== '0') activeSamplings.push('o');
        if ($('#reqStomachs').val() !== '0') activeSamplings.push('s');
        if ($('#reqFrozens').val() !== '0') activeSamplings.push('c');
        if ($('#reqWeights').val() !== '0') activeSamplings.push('w');
        return activeSamplings;
    },
    updateKeypadOpLbl: function(opElem) {
        var keypadOpLblTxt = '';
        if (!opElem || typeof opElem === 'undefined' || opElem === null) {
            opElem = 'lf-summ-keypad-wip';
        }
        opElem = opElem.indexOf('#') !== 0 ? '#'+opElem : opElem;
        var opLblElem = $(opElem);
        var mathOpNbr = $('#math-op-nbr').val();
        var mathOp = mathOpNbr !== '' ? lengthFrequency.determineMathOp()+parseInt(mathOpNbr) : '';
        var gender;
        var length = $('#length-cm').val();
        var samplings = lengthFrequency.determineSelectedSamplings();
        var opCount = 0;

        if (mathOp && mathOp !== '') {
            keypadOpLblTxt += ' '+mathOp; opCount++;
        }
        if ($('.lf-maturity-level-container').first().is(':visible')) {
            var maturityCode = lengthFrequency.validateMaturityCode();
            gender = $('button[data-selection="'+lengthFrequency.determineSelectedGender()+'"]').find('span').first().html();
            if (gender && gender !== '') {
                keypadOpLblTxt += ' '+gender; opCount++;
            }
            if (maturityCode) {
                keypadOpLblTxt += '('+maturityCode+')'; opCount++;
            }
        } else {
            gender = $('button[data-selection="'+lengthFrequency.determineSelectedGender()+'"]').find('span').first().html();
            if (gender && gender !== '') {
                keypadOpLblTxt += ' '+gender; opCount++;
            }
            opCount++;
        }
        if (length && length !== '') {
            keypadOpLblTxt += ' '+parseInt(length)+$('#lengthUnitId').children('option:selected').text(); opCount++;
        }
        if (samplings.length > 0) {
            keypadOpLblTxt += ' [';
            for (var s = 0; s < samplings.length; s++) {
                keypadOpLblTxt += samplings[s] !== '' ? samplings[s].substring(1).toUpperCase() : 'L';
                if ((s+1) !== samplings.length) {
                    keypadOpLblTxt += ',';
                }
            }
            keypadOpLblTxt += ']';
            opCount++;
        }

        $($(opLblElem).find('span')[0]).html(keypadOpLblTxt !== '' ? keypadOpLblTxt : '&nbsp;');

        if (opCount === 5) {
            $(opLblElem).attr('class', 'label label-success').attr('title', $.getMessage('js.state.valid_operation'));
            $($(opLblElem).find('span')[1]).attr('class','glyphicon glyphicon-ok');
            var keypadDisabled = $('#modal-keypad-body').attr('data-keypad-disabled') === 'true';
            $('button[data-keypad-op="e"]').prop('disabled', keypadDisabled);
        } else {
            $(opLblElem).attr('class', 'label label-danger').attr('title', $.getMessage('js.state.invalid_operation'));
            $($(opLblElem).find('span')[1]).attr('class','glyphicon glyphicon-remove');
            $('button[data-keypad-op="e"]').prop('disabled', true);
        }
        $(opLblElem).show();
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
    updateEntryHistory: function(entryData) {
        if (entryData === null || entryData === undefined) {
            //entryData is null/undefined, so adding an entry, rendering alone and resetting button indices
            var entry = lengthFrequency.determineEntry();
            lengthFrequency.editLFEntries.unshift(entry);
            lengthFrequency.renderHistoryRow(entry, 0, true);
            lengthFrequency.resetHistoryButtonIndices();
        } else {
            //entryData was passed, so emptying table and re-populating with existing history
            $('#entry-history').empty();
            $.each(entryData, function (index, value) {
                lengthFrequency.renderHistoryRow(value, index, false);
            });
        }
    },
    renderHistoryRow: function(entry, index, prepend) {
        var tr = '<tr>';
            tr += '<td style="width: 60%;">';
                tr += (entry.sex+(entry.maturityLevel !== null ? '('+entry.maturityLevel+')':'')+'<br />'+entry.length+': ').toUpperCase();
                $.each(entry.samplings, function(index, value) {
                   tr += value.toUpperCase();
                   if ((index+1) !== entry.samplings.length) {
                       tr += ',';
                   }
                });
            tr += '</td>';
            tr += '<td style="padding-right: 0px!important; margin-right: 0px!important; vertical-align: middle;">';
                tr += '<a id="edit-lf-entry-'+index+'" data-index="'+index+'" style="min-height: auto; padding: 2px; border-width: 2px; border-style: outset; font-size: 1.5em;" href="javascript: lengthFrequency.modifyLFEntry(' + index + ', \'edit\')" class="edit-btn" title="' + $.getMessage('js.icon.edit.title') + '">';
                tr += '<i class="glyphicon glyphicon-edit"></i></a>';
            tr += '</td>';
            tr += '<td style="padding-left: 0px!important; margin-left: 0px!important; vertical-align: middle;">';
                tr += '<a id="delete-lf-entry-'+index+'" data-index="'+index+'" style="min-height: auto; padding: 2px; border-width: 2px; border-style: outset; font-size: 1.5em;" href="javascript: lengthFrequency.modifyLFEntry(' + index + ', \'delete\')" class="delete-btn" title="' + $.getMessage('js.icon.delete.title') + '">';
                tr += '<i class="glyphicon glyphicon-trash"></i></a>';
            tr += '</td>';
        tr += '</tr>';
        if (prepend) {
            $('#entry-history').prepend(tr);
        } else {
            $('#entry-history').append(tr);
        }
    },
    resetHistoryButtonIndices: function() {
        var entryHistory = $('#entry-history');
        $(entryHistory).find('a.edit-btn').each(function(index, elem) {
            $(elem)
                .attr('id', 'edit-lf-entry-'+index)
                .attr('data-index', index)
                .attr('href', 'javascript: lengthFrequency.modifyLFEntry('+index+', \'edit\')');
        });
        $(entryHistory).find('a.delete-btn').each(function(index, elem) {
            $(elem)
                .attr('id', 'delete-lf-entry-'+index)
                .attr('data-index', index)
                .attr('href', 'javascript: lengthFrequency.modifyLFEntry('+index+', \'delete\')');
        });
    },
    modifyLFEntry: function(index, action) {
        var modBtn = $('#'+action+'-lf-entry-'+index);
        $('td.keypad-sampling-summ').hide();
        $('button[data-math-op="+"]')[0].click();
        lengthFrequency.resetKeypadState();
        if ($(modBtn).hasClass('icon-selected')) {
            $('a.delete-btn,a.edit-btn').removeClass('icon-selected');
            $('#wip-label').html($.getMessage('js.label.new'));
        } else {
            var entry = lengthFrequency.editLFEntries[index];
            $('a.delete-btn,a.edit-btn').removeClass('icon-selected');
            $(modBtn).addClass('icon-selected');
            $('#wip-label').html($.getMessage('js.label.'+action));
            $('#math-op-nbr').val(entry.increment);
            $('#length-cm').val(entry.length);
            $('#lf-maturity-level').val(entry.maturityLevel);
            var sex = entry.sex ? entry.sex.toLowerCase() : 'u';
            $('button[data-selection="'+sex+'"]')[0].click();
            var sampling;
            var uncheckLength = true;
            $.each(entry.samplings, function(index, value) {
                 sampling = value !== 'l' ? '_'+value : '_g';
                 if (sampling !== '_g') {
                     $('button[data-selection="' + sampling + '"]').each(function (index, elem) {
                         if ($(elem).prop('disabled')) {
                             $(elem).prop('disabled', false);
                             $(elem)[0].click();
                             $(elem).prop('disabled', true);
                         } else {
                             $(elem)[0].click();
                         }
                     });
                 } else if (sampling === '_g') {
                     uncheckLength = false;
                 }
            });
            if (uncheckLength) {
                $('button[data-selection="_g"]')[0].click();
            }
            $('#sampling-data').bindFromJSON(entry.samplingData, 'id', 'sampleData_');
            if (action === 'delete') {
                $('button[data-math-op="-"]')[0].click();
                $('input#math-op-nbr').val(entry.increment);
                $('#modal-keypad').find('button[data-keypad-op],button[data-selection]').prop('disabled', true);
                var keypadDisabled = $('#modal-keypad-body').attr('data-keypad-disabled') === 'true';
                $('button[data-keypad-op="e"]').prop('disabled', keypadDisabled);
                $('#sampling-data').find('select,input,textarea').setReadonly(true);
            } else if (action === 'edit') {
                lengthFrequency.showRelevantSamplingRequirements();
            }
            lengthFrequency.updateKeypadOpLbl();
        }
    },
    resetKeypadState: function() {
        lengthFrequency.preselectKeypadGender();
        lengthFrequency.resetKeypadSamplings();
        $('#math-op-nbr').val(1);
        $('#lf-maturity-level').val('');
        $('#length-cm').val('');
        $('button[data-math-op="+"]')[0].click();
        $('button[data-keypad-op="a"]')[0].click();
        $('button[data-keypad-op="e"]').prop('disabled', true);
        var keypadDisabled = $('#modal-keypad-body').attr('data-keypad-disabled') === 'true';
        $('#modal-keypad').find('button[data-keypad-op]').prop('disabled', keypadDisabled);
        $('#modal-keypad').find('button[data-selection]').prop('disabled', false);
        $('input.lf-sampling-req').trigger('change');
        $('#sampling-data').find('select,input,textarea').each(function(index, elem) {
           if ($(elem).attr('data-force-readonly') === 'true') {
               $(elem).setReadonly(true);
           } else {
               $(elem).setReadonly(false);
           }
        });
        lengthFrequency.resetSamplingData();
        lengthFrequency.showRelevantSamplingData();
        $('td.keypad-sampling-summ').hide();
        $('#wip-label').html($.getMessage('js.label.new'));
        lengthFrequency.resetMaturityLevelList();
        $('#lf-maturity-datalist-container').hide();
        lengthFrequency.updateKeypadOpLbl();
    },
    determineEntry: function() {
        var length = $('#length-cm').val();
        var sex = lengthFrequency.determineSelectedGender();
        var increment = 1;
        var maturityLevel = null;
        if ($('.lf-maturity-level-container').first().is(':visible')) {
            maturityLevel = parseInt($('#lf-maturity-level').val());
        }
        var samplings = lengthFrequency.determineSelectedSamplings();
        for (var x = 0; x < samplings.length; x++) {
            samplings[x] = samplings[x] === '' ? samplings[x] = 'l' : samplings[x].substring(1);
        }
        var samplingData = null;
        var samplingDataForm = $('#sampling-data');
        if ($(samplingDataForm).is(':visible')) {
            samplingData = $(samplingDataForm).serializeJSON('id', 'sampleData_');
            var dataExists = false;
            Object.keys(samplingData).forEach(function(key) {
                if (samplingData[key] !== undefined && samplingData[key] !== null && samplingData[key] !== '') {
                    dataExists = true;
                }
            });
            if (!dataExists) samplingData = null;
        }
        return lengthFrequency.injectEntryFields({length: length, sex: sex, increment: increment, samplings: samplings, maturityLevel: maturityLevel, samplingData: samplingData});
    },
    injectEntryFields: function(entry) {
        entry.fields = [];
        var fieldName;
        for (var x =0; x < entry.samplings.length; x++) {
            fieldName = '';
            fieldName += entry.sex;
            if (entry.samplings[x] !== 'l') {
                fieldName += '_'+entry.samplings[x];
            }
            fieldName += '['+entry.length+']';
            entry.fields.push(fieldName);
        }
        return entry;
    },
    determineEntryCountByField: function(fieldName) {
        var count = 0;
        for (var x = 0; x < lengthFrequency.editLFEntries.length; x++) {
            if (lengthFrequency.editLFEntries[x].fields.includes(fieldName)) {
                count++;
            }
        }
        return count;
    },
    showRelevantSamplingRequirements: function() {
        var inputLength = $('#length-cm').val();
        if (inputLength !== '') {
            var length = parseInt(inputLength);
            var sex = lengthFrequency.determineSelectedGender();
            var samplings = lengthFrequency.determineActiveSamplings();
            if (samplings.length > 1) {
                for (var s = 1; s < samplings.length; s++) {
                    lengthFrequency.showSpecificSamplingRequirement(length, sex, samplings[s]);
                }
            }
        } else {
            $('td.keypad-sampling-summ').hide();
        }
    },
    showSpecificSamplingRequirement: function(length, sex, sampling) {
        var reqSamplingNum = parseInt($('input.lf-sampling-req[data-for-sampling="'+sampling+'"]').val());
        var sampleTable = $('table#keypad-sampling-'+sampling);
        $(sampleTable).parent().show();
        $(sampleTable).find('span.keypad-sampling-summ-req-num').html(reqSamplingNum);
        $(sampleTable).find('.keypad-sampling-summ-col-grp,.keypad-sampling-summ-col-req').html('-');
        var perNth = parseInt($('input.lf-sampling-nth[data-for-sampling="'+sampling+'"]').val());
        var startLength = length - (3 * perNth);//2
        //Round down to nearest multiple of perNth
        startLength = perNth*(Math.floor(startLength/perNth));
        var samplingUnsexed = parseInt($('select.lf-sampling-sexed[data-for-sampling="'+sampling+'"]').children('option:selected').val()) === 0;
        var oppositeSex = sex !== 'u' ? (sex === 'm' ? 'f' : 'm') : 'u';
        var sampleInputs;
        var count = 0, rowNum = 1;
        var maxRange = 7 * perNth;//5
        var colGrp, rowSelector, sampleCount;
        var rows = {};
        for (var l = startLength; l < (startLength + maxRange); l++) {
            if (samplingUnsexed && oppositeSex !== 'u') {
                sampleInputs = $('input[name="' + sex + '_' + sampling + '[' + l + ']"],input[name="' + oppositeSex + '_' + sampling + '[' + l + ']"]');
            } else {
                sampleInputs = $('input[name="' + sex + '_' + sampling + '[' + l + ']"]');
            }
            rowSelector = '#keypad-sampling-'+sampling+' tr.keypad-sampling-summ-row-'+rowNum;
            if ($(sampleInputs).length) {
                sampleCount = 0;
                $(sampleInputs).each(function(index, elem) {
                    sampleCount += parseInt($(elem).val());
                });
                if (!rows.hasOwnProperty(rowSelector)) {
                    rows[rowSelector] = {samples: sampleCount, lengths: [l]};
                } else {
                    rows[rowSelector].samples += sampleCount;
                    rows[rowSelector].lengths.push(l);
                }
            }
            count++;
            if ((count % perNth) === 0) {
                rowNum++;
                count = 0;
            }
        }
        var keypadSamplingRow, reqToShow, minLen, maxLen;
        Object.keys(rows).forEach(function(rowSel, index) {
            keypadSamplingRow = $(rowSel);
            if ($(keypadSamplingRow).length) {
                minLen = Math.min.apply(null, rows[rowSel].lengths);
                maxLen = Math.max.apply(null, rows[rowSel].lengths);
                colGrp = minLen !== maxLen ? minLen + '-' + maxLen : minLen + '';
                reqToShow = rows[rowSel].samples >= reqSamplingNum ?
                    '<span class="glyphicon glyphicon-ok"></span>' : rows[rowSel].samples;
                $(keypadSamplingRow).find('.keypad-sampling-summ-col-grp').html(colGrp);
                $(keypadSamplingRow).find('.keypad-sampling-summ-col-req').html(reqToShow);
            }
        });
    },
    showRelevantSamplingData: function() {
        var aS = lengthFrequency.determineSelectedSamplings();
        var samplingDataElem = $('#sampling-data').hide();
        var noSamplingDataElem = $('#no-sampling-data-alert').hide();
        if (aS.includes('_o') || aS.includes('_s') || aS.includes('_c') || aS.includes('_w')) {
            $(samplingDataElem).show();
        } else {
            $(noSamplingDataElem).show();
        }
    },
    resetSamplingData: function() {
        $('#sampling-data').find('input,select,textarea').each(function(index, elem) {
            var defaultVal = $(elem).attr('data-default');
            switch ($(elem).prop('tagName')) {
                case 'INPUT':
                    if ($(elem).attr('type') === 'radio' || $(elem).attr('type') === 'checkbox') {
                        if (defaultVal) {
                            $(elem).prop('checked', true);
                        }
                    } else {
                        $(elem).val(defaultVal ? defaultVal : '');
                    }
                    break;
                case 'SELECT':
                    $(elem).prop('selectedIndex', defaultVal ? defaultVal : 0);
                    break;
                case 'TEXTAREA':
                    $(elem).html(defaultVal ? defaultVal : '');
            }
            lengthFrequency.resetFieldNbr();
            $(elem).trigger('change');
        });
    },
    resolveInitials: function() {
        var enteredBy = $('#enteredBy').children('option:selected').val();
        var initials = '';
        if (enteredBy !== null && enteredBy !== undefined && enteredBy !== '') {
            initials = enteredBy === 'other' ? $('#enteredByOther').val() : enteredBy.substring(enteredBy.indexOf(',') + 1);
        }
        return initials;
    },
    renderSummaryDataTable: function(min, max) {
        min = isNaN(min) ? parseInt(min) : min;
        max = isNaN(max) ? parseInt(max) : max;

        var RC = 'lf-summ'; var RC_ = ' '+RC+'-';
        var LEN = 'l'; var OTO = 'o'; var STM = 's'; var FRZ = 'c'; var WGT = 'w';
        var COLS = [LEN, OTO, STM, FRZ, WGT];
        var TYPES = ['u','m','f'];
        var READONLY_COLS = [OTO, STM, FRZ, WGT];

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
                    field = COLS[x] !== LEN ? TYPES[z]+'_'+COLS[x] : TYPES[z];
                    html += '<td class="'+RC_.substring(1)+COLS[x]+RC_+TYPES[z]+'">';
                    html += '<input type="number" id="';
                    html += field+'['+y+']" name="';
                    html += field+'['+y+']" value="0" min="0" ';
                    html += 'pattern="\\d*" class="lf-summ-num' + (COLS[x] === LEN ? ' lf-summ-num-l' : '') + '"';
                    if (READONLY_COLS.includes(COLS[x])) {
                        html += ' readonly="readonly"';
                    }
                    html += ' onblur="app.enforceDefaultValue(this, \'0\')" />';
                    html += '</td>';
                }
            }
            html += '</tr>';
        }
        $('#lf-summ-data-tbody').html(html);
    },
    getSettingsBySpeciesAndYear: function(speciesId, year) {
        return new Promise(function(doReturn) {
            $.db_getByKeyPath('trip_settings', [speciesId, year]).then(function(specificSettings) {
                if (specificSettings) {
                    doReturn(specificSettings);
                } else {
                    $.db_getByKeyPath('trip_settings', [speciesId, -1]).then(function(speciesSettings) {
                        if (speciesSettings) {
                            doReturn(speciesSettings);
                        } else {
                            $.db_getByKeyPath('trip_settings', [-1, -1]).then(function(defaultSettings) {
                                if (defaultSettings) {
                                    doReturn(defaultSettings);
                                } else {
                                    doReturn(null);
                                }
                            });
                        }
                    });
                }
            });
        });
    },
    resetMaturityLevelList: function() {
        $('select#maturityLevels').children('option').prop('selected', false);
    },
    showMaturityLevelDatalist: function() {
        var height = 0;
        $('#modal-keypad').find('.modal-header,.modal-body,.modal-footer').each(function(index,part) {
            height += $(part).outerHeight(true);
        });
        $('#lf-maturity-datalist-container').height(height).show();
        lengthFrequency.resetMaturityLevelList();
        var maturityCode = $('#lf-maturity-level').val();
        var maturityLevels = $('#maturityLevels');
        $(maturityLevels).children('option').each(function(index, opt) {
            if ($(opt).val().indexOf(maturityCode) === 0) {
                var optionTop = $(opt).offset().top;
                var selectTop = $(maturityLevels).offset().top;
                $(maturityLevels).scrollTop($(maturityLevels).scrollTop() + (optionTop - selectTop));
                $(opt).prop('selected', true);
                return false;
            }
        });
        $(maturityLevels).focus();
    },
    selectMaturityLevel: function() {
        var opt = $('select#maturityLevels').children('option:selected');
        if ($(opt).length) {
            var optVals = $(opt).val().split(';');
            $('#lf-maturity-level').val(optVals[0]);
            $('button[data-selection="' + optVals[1].toLowerCase() + '"]').trigger('click');
            lengthFrequency.updateKeypadOpLbl();
            $('#lf-maturity-datalist-container').hide();
        }
    },
    validateMaturityCode: function() {
        var toReturn = null;
        var maturityElem = $('#lf-maturity-level');
        var maturityCode = $(maturityElem).val();
        var maturityCodeList = $('#maturityLevels');
        var selectedSex, optVal;
        if (maturityCode && maturityCode !== '') {
            $(maturityCodeList).children('option').each(function(index, opt) {
                if ($(opt).val().indexOf(maturityCode+';') === 0) {
                    toReturn = maturityCode;
                    optVal = $(opt).val();
                    selectedSex = optVal.substring(optVal.indexOf(';')+1);
                    $('button[data-selection="'+selectedSex.toLowerCase()+'"]').trigger('click');
                    return false;
                }
            });
        }
        return toReturn;
    },
    resetFieldNbr: function() {
        var catchDtStr = $('#catchDate').val();
        if (catchDtStr) {
            var catchDt = new Date(catchDtStr);
            $('#sampleData_fieldNbr').val(''+catchDt.getFullYear()+'-'+lengthFrequency.resolveInitials()+'-'+$.generateUUID().substring(0, 18).toUpperCase());
        }
    },
    syncToServer: function() {
        var checkedLFs = $('input[value!=""].lf-off-list-checkbox:checkbox:checked').map(function(){return this.value;}).get();
        if ($.isArray(checkedLFs) && checkedLFs.length > 0) {
            $.confirmAction(function () {
                $('#sync-modal').remove();

                var modal = '<section id="sync-modal" class="mfp-hide modal-dialog modal-content overlay-def">';
                modal += '<header class="modal-header">';
                modal += '<h2 class="modal-title">' + $.getMessage('js.modal.sync.title') + '</h2>';
                modal += '</header>';
                modal += '<div class="modal-body">';
                modal += '<div id="sync-modal-body" style="max-height: 300px; overflow-y: auto;">';
                modal += '<ol style="font-size: 1.2em;">';
                checkedLFs.forEach(function (lfId, index) {
                    modal += '<li class="pending-sync">';
                    modal += '<span>' + lfId + '</span>&nbsp;<span class="glyphicon glyphicon-question-sign" tabindex="-1"></span>';
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

                var syncFunction = function () {
                    var syncModal = $('#sync-modal');
                    if ($(syncModal).length && $(syncModal).is(':visible')) {
                        var syncElem = $('li.pending-sync');
                        if ($(syncElem).length) {
                            syncElem = $(syncElem).first();
                            $(syncElem).attr('class', 'sync').find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-circle-arrow-down');
                            var lfId = $(syncElem).find('span').first().html();
                            $.db_getByKeyPath('length_frequencies', lfId).then(function (lf) {
                                if (lf) {
                                    if (lf.verifiedBy) {
                                        $.jsonConvertFormArrays(lf);
                                        var dataToSend = JSON.stringify(lf);
                                        $.ajax({
                                            type: 'POST',
                                            url: syncPath,
                                            data: dataToSend,
                                            contentType: 'application/json',
                                            success: function (data) {
                                                if (data.status === 'SUCCESS') {
                                                    $(syncElem).attr('class', 'sync-complete').append('&nbsp;<span>' + data.id + '</span>');
                                                    $.db_deleteByKeyPath('length_frequencies', lfId).then(function (result) {
                                                        var dt = $('#offline-table').DataTable();
                                                        if (result) {
                                                            $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-circle-arrow-right text-green').focus();
                                                            dt.row($('#delete_' + lfId).parents('tr')).remove();
                                                        } else {
                                                            $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage('js.error.sync_fail.local_record_delete_fail', [lfId]));
                                                            $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-circle-arrow-right text-red').focus();
                                                        }
                                                        setTimeout(syncFunction, 500);
                                                    });
                                                } else {
                                                    var errorKey = 'js.error.sync_fail';
                                                    if (typeof data.errorKey !== 'undefined' && data.errorKey != null &&
                                                        data.errorKey.length > 0) errorKey = data.errorKey;
                                                    $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage(errorKey, [lfId]));
                                                    $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                                                    setTimeout(syncFunction, 500);
                                                }
                                            },
                                            error: function () {
                                                $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage('js.error.sync_fail', [lfId]));
                                                $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                                                setTimeout(syncFunction, 500);
                                            }
                                        });
                                    } else {
                                        $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage('js.error.sync_fail.record_not_verified', [lfId]));
                                        $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                                        setTimeout(syncFunction, 500);
                                    }
                                } else {
                                    $(syncElem).attr('class', 'sync-failed').attr('data-error', $.getMessage('js.error.sync_fail.local_record_not_found', [lfId]));
                                    $(syncElem).find('span:nth-child(2)').attr('class', 'glyphicon glyphicon-remove-sign text-red').focus();
                                    setTimeout(syncFunction, 500);
                                }
                            });
                        } else {
                            $(syncModal).find('button').html($.getMessage('ok'));
                            $(syncModal).find('.modal-title').html($.getMessage('js.modal.sync.title_complete'));
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
                            $('#offline-table').DataTable().draw();
                            $('#filterYear').prop('selectedIndex', 0);
                            $('#btnRefresh')[0].click();
                        }
                    }
                };
                setTimeout(syncFunction, 500);
            }, 'js.confirm.sync_records', [checkedLFs.join(', ')]);
        } else {
            $('#btnSync').prop('disabled', true);
        }
    },
    initLFHeaderFormValidations: function() {
        $.validator.addMethod("otherEnteredBy", function (value, element) {
            var answer = true;
            if ($('#enteredBy').val() === 'other') {
                if (value === '' || value.length !== 2) {
                    answer = false;
                }
            }
            $(element).val($(element).val().toUpperCase());
            return answer;
        }, $.getMessage('js.error.other_initials_not_entered'));

        $.validator.addMethod("otherEnteredByUnique", function (value, element) {
            var answer = true;
            if ($('#enteredBy').val() === 'other') {
                if (value !== '' && value.length === 2) {
                    $('#enteredBy').children('option').each(function () {
                        var val = $(this).val();
                        if (val !== 'other' && val.substring(val.indexOf(',') + 1) === value) {
                            answer = false;
                            return false;
                        }
                    });
                }
            }
            return answer;
        }, $.getMessage('js.error.other_initials_not_unique'));
        $('#enteredByOther').rules('add', {otherEnteredBy: true, otherEnteredByUnique: true});

        $.validator.addMethod("otherVesselDetails", function (value, element) {
            var answer = true;
            if ($('#vesselId').val() === 'other') {
                if (value === '') {
                    answer = false;
                }
            }
            return answer;
        }, $.getMessage('js.error.other_details_not_entered'));
        $('#otherVesselDetails').rules('add', {otherVesselDetails: true});
    },
    viewSamplingData: function() {
        var sId = $('#lfId').val();
        if (sdPath) window.location.href = sdPath + '?sId=' + sId;
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/create', lengthFrequency.initOfflineCreate);
    $.initForPathContains('/list', lengthFrequency.initList);
    $.initForPathContains('/edit', lengthFrequency.initEdit);
});