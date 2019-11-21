var lookups = {
    initList: function() {

        var settings = {
            ajaxSource: listDataPath,
            order: [[1, 'asc']],
            columns: []
        };

        if(typeof setSort !== "undefined" && setSort !== null) {
            settings.order[0] = setSort;
        }

        settings.columns.push({data: 'id'});
        if(typeof removeOrder === "undefined" || removeOrder === false) {
            settings.columns.push({data: 'order'});
        }
        if(typeof removeDescription === "undefined" || removeDescription === false) {
            settings.columns.push({data: 'description'});
        }
        if (typeof additionalColumnDefs !== 'undefined' && additionalColumnDefs !== null &&
            additionalColumnDefs.constructor === Array) {
            additionalColumnDefs.forEach(function(colDef) {
                settings.columns.push(colDef);
            });
        }
        if (typeof renderLegacyCode !== 'undefined' && renderLegacyCode) settings.columns.push({data: 'legacyCode'});
        if(typeof removeActions === "undefined" || removeActions === false) {
            settings.columns.push({data: 'buttons', orderable: false});
        }
        $('#lookupTable').attr('data-wb-tables', JSON.stringify(settings)).addClass('wb-tables').trigger('wb-init.wb-tables');
    },
    initForm: function() {
        $('#vesselLengthMeters').trigger('change');
        $(".removeNumbler").removeClass("js-numbler");
        $.hideDisabledLookups();
        $.convertDataTextOnly();

        $('form').on('reset', function() {
            setTimeout(function() {
                window.location.reload();
            }, 0);
        });
    },
    activateLookup: function(id, description, active) {
        $('#successMessage').hide();
        $.confirmAction(function() {
            $.processPromise(new Promise(function(doReturn) {
                $.ajax({
                   url: activatePath+"?id="+id+"&active="+active,
                   type: 'PATCH',
                   success: function(result){doReturn(result === 'success')},
                   error: function(){doReturn(false)}
                });
            }), function(result) {
                if (result) {
                    var activateButton = $('#activate_'+id);
                    if ($(activateButton).length) {
                        if(description.length > 60){
                            description = description.substr(0, 60) + "..."
                        }
                        $(activateButton).attr('href', 'javascript: lookups.activateLookup('+id+',`'+description+'`,'+!active+')');
                        $(activateButton).attr('title', $.getMessage('js.icon.'+(!active ? 'activate' : 'deactivate')+'.title'));
                        $(activateButton).find('span').first().attr('class', 'glyphicon glyphicon-'+(active ? 'ok-circle text-green' : 'ban-circle text-red'));
                    }
                } else {
                    $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'lookupTable-container');
                }
            });
        }, 'js.confirm.'+(active ? 'activate' : 'deactivate'), [description])
    },
    editLookup: function(lId) {
        window.location.href = editPath + "?lId="+lId;
    },
    validateMinGreaterThanMax: function(max, min, errorId, messageId){
        $('#'+ errorId).html("");
        if($(max).val() !== "" && $(min).val() !== "") {
            var maxVal = parseFloat($(max).val());
            var minVal = parseFloat($(min).val());

            if (maxVal < minVal) {
                messageId = messageId || 'js.error.max_less_than_min_meters';
                $.showAlert('danger', $.getMessage(messageId), 0, errorId);
                $('#'+ errorId).find("section").fadeIn(400, function () {
                    $(this).get(0).scrollIntoView({behavior: 'smooth'})
                });

                var fadeOut = function () {
                    $('#'+ errorId).fadeOut(400, function () {
                        $(this).remove()
                    });
                };
                $('#'+ errorId).on('click', fadeOut);
            }
        }
    },
    validateIsNumber: function(elem) {
        if(isNaN($(elem).val())){
            $(elem).val('')
        }
    },
    convertNumberFields: function(elem, factor, op, otherFieldId, fixedDecimals) {
        if (fixedDecimals === undefined || fixedDecimals === null) fixedDecimals = 2;
        if ((op === undefined || op === null) && op !== '/' && op !== '*') op = '*';
        var thisElemVal = $(elem).val();
        if (thisElemVal !== '' && !isNaN(thisElemVal)) {
            var settings = {factor: factor, op: op, fieldId: otherFieldId, roundUp: false};
            app.convertNumberValue(elem, settings);
            $(elem).val(parseFloat($(elem).val()).toFixed(fixedDecimals));
        } else {
            $(elem).val('');
            if (otherFieldId.indexOf('#') !== 0) otherFieldId = '#'+otherFieldId;
            $(otherFieldId).val('');
        }
    },
    vessel: {
        autoSelectLengthCategory: function(elem, factor, op, otherFieldId, fixedDecimals) {
            lookups.convertNumberFields(elem, factor, op, otherFieldId, fixedDecimals);
            var lengthMeters = $('#vesselLengthMeters').val();
            if (lengthMeters !== '') {
                lengthMeters = parseFloat(lengthMeters);
                var lengthCategoryFound = false;
                var lengthCategoryElem = $('#vesselLengthCategoryData');
                $(lengthCategoryElem).children('option').each(function(index, opt) {
                    if ($(opt).val() !== '') {
                        var catData = $(opt).val().split(';');
                        var min = parseFloat(catData[1]).toFixed(1);
                        var max = parseFloat(catData[2]).toFixed(1);
                        if (lengthMeters >= min && lengthMeters <= max) {
                            $('#vesselLengthCategoryData').prop('selectedIndex', index);
                            lengthCategoryFound = true;
                            return false;
                        }
                    }
                });
                if (!lengthCategoryFound) {
                    $(lengthCategoryElem).prop('selectedIndex', 0);
                }
            }
            $.convertDataTextOnly();
        }
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/list', lookups.initList);
    $.initForPathContains('/create', lookups.initForm);
    $.initForPathContains('/edit', lookups.initForm);
});