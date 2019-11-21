var app = {
    wheelShown: false,
    defaultData: {},
    ctabStores: [
        {objectStoreName: 'trip_settings', optionalParams: {keyPath: ['speciesKey','yearKey']}, updateMinuteInterval: 5},
        {objectStoreName: 'vessels', optionalParams: {keyPath: 'id'}, updateMinuteInterval: 5}
    ],
    initDB: function(db) {
        //Object store creation (if not exists)
        $.db_createObjectStore(db, 'length_frequencies', {keyPath: "lfId"});

        $.db_createObjectStore(db, 'ctab_checksums', {keyPath: "objectStoreName"});
        for (var x = 0; x < app.ctabStores.length; x++) {
            $.db_createObjectStore(db, app.ctabStores[x].objectStoreName, app.ctabStores[x].optionalParams);
        }
    },
    requestCtabStores: function(objectRequests) {
        return new Promise(function(doReturn) {

            var path = window.location.pathname;
            var html;

            if (objectRequests === null || objectRequests === undefined) {
                objectRequests = {requests: []};

                if(!app.wheelShown) {
                    if (path.includes("/eng/")) {
                        html = app.getDbLoadingHtml("eng", $.getMessage("js.load_server.lang"), $.getMessage("js.please_wait"));
                    } else if (path.includes("/fra/")) {
                        html = app.getDbLoadingHtml("fra", $.getMessage("js.load_server.lang"), $.getMessage("js.please_wait"));
                    } else {
                        html = app.getDbLoadingHtml("", $.getMessage("js.load_server.all"), $.getMessage("js.please_wait.all"));
                    }
                    $("#main").append(html);
                    app.wheelShown = true;
                }
                for (var q = 0; q < app.ctabStores.length; q++) {
                    objectRequests.requests[q] = {};
                    objectRequests.requests[q].objectStoreName = app.ctabStores[q].objectStoreName;
                }
            }
            else {
                for(var x = 0; x < objectRequests.requests.length; x++){
                    if(!objectRequests.requests[x].successfulInput){
                        if(!app.wheelShown) {
                            if (path.includes("/eng/")) {
                                html = app.getDbLoadingHtml("eng", $.getMessage("js.load_server.lang"), $.getMessage("js.please_wait"));
                            } else if (path.includes("/fra/")) {
                                html = app.getDbLoadingHtml("fra", $.getMessage("js.load_server.lang"), $.getMessage("js.please_wait"));
                            } else {
                                html = app.getDbLoadingHtml("", $.getMessage("js.load_server.all"), $.getMessage("js.please_wait.all"));
                            }
                            $("#main").append(html);
                            app.wheelShown = true;
                        }

                        objectRequests.requests[x].checksumUUID = objectRequests.requests[x].checksumUUID.substr(0, objectRequests.requests[x].checksumUUID.length - 2)
                    }
                }
            }

            $.ajax({
                url: contextedPath + 'checksumObjects',
                method: "POST",
                data: JSON.stringify(objectRequests),
                mimeType: 'application/json',
                contentType: 'application/json',
                success: function (data) {
                    data.requestSuccess = true;
                    doReturn(data);
                },
                error: function() {
                    var data = {requestSuccess: false};
                    doReturn(data);
                }
            });
        });
    },
    updateCtabStores: function(objectResponses) {
        var checksumData = {};
        var hasError = false;
        var numCompleted = 0;
        var shownHere = false;

        $.each(objectResponses.responses, function(index, response) {
             $.db_clearObjectStore(response.objectStoreName).then(function(isCleared) {
                 if (isCleared) {
                     var path = window.location.pathname;
                     var html;

                     if(app.wheelShown && !shownHere) {
                         $(".dbLoading").hide();
                         if (path.includes("/eng/")) {
                             html = app.getDbLoadingHtml("eng", $.getMessage("js.load_local.lang"), $.getMessage("js.please_wait"));
                         } else if (path.includes("/fra/")) {
                             html = app.getDbLoadingHtml("fra", $.getMessage("js.load_local.lang"), $.getMessage("js.please_wait"));
                         } else {
                             html = app.getDbLoadingHtml("", $.getMessage("js.load_local.all"), $.getMessage("js.please_wait.all"));
                         }
                         $("#main").append(html);
                         shownHere = true;
                     }

                     checksumData = {checksumUUID: response.checksumUUID, objectStoreName: response.objectStoreName, lastUpdate: new Date(), successfulInput : false};
                     $.db_insertOrUpdate('ctab_checksums', checksumData);

                     $.db_bulkInsertOrUpdate(response.objectStoreName, response.objects).then(function(complete){
                         if(!complete) hasError = complete;

                         if(!hasError){
                             numCompleted++;
                             checksumData = {checksumUUID: response.checksumUUID, objectStoreName: response.objectStoreName, lastUpdate: new Date(), successfulInput : true};
                             $.db_insertOrUpdate('ctab_checksums', checksumData);
                             console.log(response.objectStoreName + " have been successfully stored in IndexedDB");

                            if(numCompleted >= objectResponses.responses.length)
                                $(".dbLoading").hide();
                         }
                     });
                 }
             });

        });
    },
    getDbLoadingHtml : function(locale, message, secondMessage) {
        var html = "<div class=\"dbLoading\">";
        html += "<div>";
        html += "<p class=\"text-info\">";
        if(locale === "eng"){
            html += "<span>" + message + "</span>";
            html += "<br />";
            html += "<span>" + secondMessage + "</span>";
        }
        else if(locale === "fra"){
            html += "<span>" + message + "</span>";
            html += "<br />";
            html += "<span>" + secondMessage + "</span>";
        }
        else {
            html += "<span>" + message + "</span>";
            html += "<br />";
            html += "<span>" + secondMessage + "</span>";
        }
        html += "</p>";
        html += "</div>";
        html += "</div>";

        return html;
    },
    enforceDefaultValue: function(elem, value) {
        if ($(elem).prop('tagName') === 'SELECT') {
            if ($(elem).children('option:selected').val() === '') {
                $(elem).prop('selectedIndex', parseInt(value));
            }
        } else {
            if ($(elem).val().trim() === '') {
                $(elem).val(value);
            }
        }
    },
    initFormEnhancements: function() {
        $('input[data-conv-num-other]').on('change', function() {
            var settingsStr = $(this).attr('data-conv-num-other').replace(new RegExp('&quot;', 'g'), '"');
            var settings = JSON.parse(settingsStr);
            app.convertNumberValue(this, settings);
        });
    },
    convertNumberValue: function(sourceInput, settings) {
        var sourceVal = $(sourceInput).val();
        if (settings.roundUp) {
            sourceVal = Math.round(sourceVal);
        }
        if (sourceVal !== '' && !isNaN(sourceVal)) {
            var convVal = eval((settings.roundUp ? 'Math.round(' : '')+sourceVal+settings.op+settings.factor+(settings.roundUp ? ')' : ''));
            sourceVal = parseFloat(sourceVal);
            $('#'+settings.fieldId).val(settings.roundUp ? convVal : convVal.toFixed(2));
            $(sourceInput).val(settings.roundUp ? Math.round(sourceVal) : sourceVal.toFixed(2));
        } else {
            $('#'+settings.fieldId).val('');
            $(sourceInput).val('');
        }
    },
    currentYear: function() {
        var thisDate = new Date();
        return thisDate.getFullYear();
    },
    removeUnwantedData: function(object, unwantedDataStartWithPatterns) {
        var toReturn = {};
        Object.keys(object).forEach(function(prop,index) {
            for (var x = 0; x < unwantedDataStartWithPatterns.length; x++) {
                if (prop.startsWith(unwantedDataStartWithPatterns[x])) {
                    toReturn[prop] = object[prop];
                    delete object[prop];
                    break;
                }
            }
        });
        return toReturn;
    },
    hideAllSelectOptions: function(selectId, forceReset) {
        if (typeof selectId !== 'undefined' && selectId) {
            if (selectId.indexOf('#') !== 0) selectId = '#'+selectId;
            $(selectId).children('option').each(function () {
                if (forceReset) $(this).prop('selected', false);
                if ($(this).val() !== '') {
                    if (!$(this).is(':selected')) {
                        $(this).hide().prop('disabled', true).prop('selected', false);
                    }
                }
            });
        }
    },
    validateIsFloat: function(elem) {
        if(!$(elem).val().match("^([-])?[0-9]+([.])?([0-9]+)*$")){
            $(elem).val('')
        }
    },
    validateIsNumber: function(elem) {
        if(!$(elem).val().match("^([-])?[0-9]+$")){
            $(elem).val('')
        }
    },
    validateLengthOfNumber: function(elem, length, e) {
        if($(elem).val().length > length - 1){
            $(elem).val($(elem).val().slice(0, length));
        }
    }
};

$(document).on('wb-ready.wb', function() {
    $(document).on('app.db-init-complete', function() {
        app.requestCtabStores().then(function(objectResponses) {
            if (objectResponses.requestSuccess) {
                app.updateCtabStores(objectResponses);
            }
        });
    });
    $(document).on('app.db-opened', function() {
        $.isOnline().then(function (isOnline) {
            if (isOnline) {
                $.db_getAll('ctab_checksums').then(function (data) {
                    if (data !== null && data.length > 0) {
                        data = data.filter(function (value, index) {
                            var ctabStore = app.ctabStores.find(function (obj) {
                                return obj.objectStoreName === value.objectStoreName;
                            });
                            var updateTime = value.lastUpdate.getTime() + (ctabStore.updateMinuteInterval * 60 * 1000);
                            return (new Date()).getTime() >= updateTime || !value.successfulInput;
                        });
                        var objectRequests = {requests: data};
                        app.requestCtabStores(objectRequests).then(function (objectResponses) {
                            if (objectResponses.requestSuccess) {
                                objectResponses.responses = objectResponses.responses.filter(function (value, index) {
                                    return value.status === 'OUT_OF_DATE';
                                });
                                app.updateCtabStores(objectResponses);
                            }
                        });
                    }
                });
            }
        });
    });
    $.db_initDB(app.initDB);
    app.initFormEnhancements();
});
