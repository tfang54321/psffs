var samplingSetting = {
    initForm : function () {
        $("#tsYear").on('change', samplingSetting.checkToOpenForm);
        $("#tsSpecies").on('change', samplingSetting.checkToOpenForm);
        $("#tsBtnReset").on('click', samplingSetting.resetForm);

        $(".removeNumbler").removeClass("js-numbler");
        samplingSetting.prepareDefaults();
        $("#tsUnits").on('change', samplingSetting.filterLengthGroupsByUnit).trigger('change');
        $("#tsLengthRangeStart").on('change', samplingSetting.validateMinLessThanMax);
        $("#tsLengthRangeEnd").on('change', samplingSetting.validateMinLessThanMax);

        if(jQuery.validator && jQuery.validator !== 'undefined'){
            jQuery.validator.addMethod("rangeMinLessThanMax", function() {
                return samplingSetting.validateMinLessThanMax();
            }, jQuery.validator.format($.getMessage("js.error.length_range")));
        }
    },
    validateMinLessThanMax : function() {
        var min = $("#tsLengthRangeStart").val();
        var max = $("#tsLengthRangeEnd").val();

        if(min != "" && max != ""){
            if(min > max){
                return false;
            }
        }
        return true;
    },
    checkToOpenForm : function(){
        var year = $("#tsYear").val();
        if (year !== null && !isNaN(year)) {
            year = parseInt(year);
        }
        var speciesId = $("#tsSpecies").val();
        samplingSetting.prepareDefaults();
        if(speciesId === '' && year !== 0){
            year = 0;
        }

        $.ajax({
            type: "GET",
            url: dataPath,
            contentType: "application/json",
            data: "year=" + year + "&speciesId=" + speciesId,
            success: function (data) {
                if(data.id == null) data.id = "";
                $("#tripForm").bindFromJSON(data);
                $('#tsUnits').trigger('change');
                if(year < new Date().getFullYear() && year !== 0){
                    $('#subSection :input').attr('disabled', true);
                }
                else {
                    $('#subSection :input').attr('disabled', false);
                }
                if(data.year == null && year !== 0) {
                    $.showAlert('info', $.getMessage('js.info.trip_setting_default'), 10000, '#predendAlert');
                }
                samplingSetting.showDefaultCheckbox(speciesId !== '' && year !== 0);
            }
        });
    },
    resetForm : function () {
        window.location.reload(true);
    },
    prepareDefaults : function () {
        var year = $("#tsYear").val();
        var speciesId = $("#tsSpecies").val();
        //Remove non default values from drop down list when no species is selected
        if(speciesId === ''){
            $("#tsYear option").each(function (){
                if($(this).val().toString() !== '0'){
                    $(this).hide();
                }
            });
            $("#tsYear").val(0);
        }
        else {
            $("#tsYear option").show();
        }

        samplingSetting.showDefaultCheckbox(year !== '0');
    },
    filterLengthGroupsByUnit : function() {
        var selectedLengthUnitId = $('#tsUnits').children('option:selected').val();
        var lengthGroupElem = $('#tsLengthGroup');
        if (selectedLengthUnitId !== '') {
            var lengthGroupData;
            $(lengthGroupElem).children('option').each(function(index, opt) {
                 lengthGroupData = $(opt).val();
                 if (lengthGroupData !== '') {
                     if (lengthGroupData.split(';')[1] === selectedLengthUnitId) {
                         $(opt).show();
                     } else {
                         if ($(opt).is(':selected')) $(lengthGroupElem).prop('selectedIndex', 0);
                         $(opt).hide();
                     }
                 }
            });
        } else {
            $(lengthGroupElem).prop('selectedIndex', 0).children('option[value!=""]').hide();
        }
    },
    showDefaultCheckbox: function(showCheckbox) {
        if (showCheckbox) {
            $('#setDefault,#setDefaultLabel').show();
        } else {
            $('#setDefault,#setDefaultLabel').hide();
        }
    }
};

$(document).on('wb-ready.wb', function() {
    var success = $("#successMessage").text();

    if(success != null && success !== '') {
        $.showAlert('success', success, 10000, '#predendAlert');
    }

    $.initForPathContains('/form', samplingSetting.initForm);
});