var quarter = {
    initReplace : function() {
      quarter.initStep();
      quarter.addInitEventListeners();
    },
    initStep : function() {
        if(typeof stopJSDisable === "undefined" || stopJSDisable !== "true") {
            stopJSDisable = "false";
            $('#quarterForm :input:not([type=hidden])').attr('disabled', true);
            $('#catchLocationId').attr('disabled', false);
            $('#btnReset').attr('disabled', false);
            $('#btnCancel').attr('disabled', false);

            $(".removeNumbler").removeClass("js-numbler");
        }
    },
    addInitEventListeners: function() {
        $('#btnReset').on('click', quarter.initReplace);

        $('#catchLocationId').on("change", quarter.checkCatchLocStep);

        $('#firstQuarter select').on("change", quarter.checkFirstStep);
        $('#secondQuarter select').on("change", quarter.checkSecondStep);
        $('#thirdQuarter select').on("change", quarter.checkThirdStep);
        $('#fourthQuarter select').on("change", quarter.checkFourthStep);
    },
    checkCatchLocStep : function() {
        if($('#catchLocationId').val() !== ""){
            $('#firstQuarter :input').attr('disabled', false);
        }
        else {
            quarter.initReplace();
        }
    },
    checkFirstStep : function() {
        if($('#periodToMonth1').val() !== "" && $('#periodFromMonth1').val() !== "" && $('#periodNumber1').val() !== ""){
            quarter.changeFrom(2);
            $('#secondQuarter :input').attr('disabled', false);
        }

    },
    checkSecondStep : function() {
        var toMonth1 = parseInt($('#periodToMonth1').val());
        var fromMonth2 = parseInt($('#periodFromMonth2').val());
        if($('#periodToMonth2').val() !== "" && $('#periodFromMonth2').val() !== "" && $('#periodNumber2').val() !== "") {
            if(fromMonth2 === (toMonth1 + 1) || (fromMonth2 === 1 && toMonth1 === 12)) {
                if(quarter.checkMonthOverlap()) {
                    quarter.changeFrom(3);
                    $('#thirdQuarter :input').attr('disabled', false);
                }
            }
            else {
                $.showAlert("danger", $.getMessage("js.error.quarter.follow_month",["second","first"]), 10000, "displayErr");
            }
        }
    },
    checkThirdStep : function() {
        var toMonth2 = parseInt($('#periodToMonth2').val());
        var fromMonth3 = parseInt($('#periodFromMonth3').val());
        if($('#periodToMonth3').val() !== "" && $('#periodFromMonth3').val() !== "" && $('#periodNumber3').val() !== "") {
            if(fromMonth3 === (toMonth2 + 1) || (fromMonth3 === 1 && toMonth2 === 12)) {
                if(quarter.checkMonthOverlap()) {
                quarter.changeFrom(4);
                $('#fourthQuarter :input').attr('disabled', false);
                }
            }
            else {
                $.showAlert("danger", $.getMessage("js.error.quarter.follow_month",["third","second"]), 10000, "displayErr");
            }
        }
    },
    checkFourthStep : function() {
        var toMonth3 = parseInt($('#periodToMonth3').val());
        var fromMonth4 = parseInt($('#periodFromMonth4').val());
        if($('#periodToMonth4').val() !== "" && $('#periodFromMonth4').val() !== "" && $('#periodNumber4').val() !== "") {
            if(fromMonth4 === (toMonth3 + 1) || (fromMonth4 === 1 && toMonth3 === 12)) {
                if(quarter.checkMonthOverlap()) {
                    if(quarter.checkContainsAllMonths()) {
                        $('#cdSubmit').attr('disabled', false);
                    }
                }
            }
            else {
                $.showAlert("danger", $.getMessage("js.error.quarter.follow_month",["fourth","third"]), 10000, "displayErr");
            }
        }
    },
    checkMonthOverlap: function () {
        var toMonth1 = parseInt($('#periodToMonth1').val()) || -1;
        var fromMonth1 = parseInt($('#periodFromMonth1').val()) || -1;
        var toMonth2 = parseInt($('#periodToMonth2').val()) || -1;
        var fromMonth2 = parseInt($('#periodFromMonth2').val());
        var toMonth3 = parseInt($('#periodToMonth3').val()) || -1;
        var fromMonth3 = parseInt($('#periodFromMonth3').val()) || -1;
        var toMonth4 = parseInt($('#periodToMonth4').val()) || -1;
        var fromMonth4 = parseInt($('#periodFromMonth4').val()) || -1;
        var isValid = true;
        var monthsUsed = [false,false,false,false,false,false,false,false,false,false,false,false];

        if(fromMonth1 !== -1 && toMonth1 !== -1) {
            if(fromMonth1 <= toMonth1) {
                for (var x = fromMonth1 - 1; x < toMonth1; x++) {
                    monthsUsed[x] = true;
                }
            }
            else {
                for (var x = fromMonth1 - 1; x < 12; x++) {
                    monthsUsed[x] = true;
                }

                for (x = 0; x < toMonth1; x++) {
                    monthsUsed[x] = true;
                }
            }
        }
        if(fromMonth2 !== -1 && toMonth2 !== -1) {
            var q2Complete = false;
            var y = fromMonth2 - 1;
            while(!q2Complete){
                if(y === toMonth2 - 1) {
                    q2Complete = true;
                }
                if (monthsUsed[y]) {
                    isValid = false;
                    $.showAlert("danger", $.getMessage("js.error.quarter.overlap", ["second"]), 10000, "displayErr");
                    break;
                }
                monthsUsed[y] = true;
                if(y === 11){
                    y = 0;
                }
                else {
                    y++;
                }
            }
        }
        if(fromMonth3 !== -1 && toMonth3 !== -1) {
            var q3Complete = false;
            var z = fromMonth3 - 1;
            while(!q3Complete){
                if(z === toMonth3 - 1) {
                    q3Complete = true;
                }
                if (monthsUsed[z]) {
                    isValid = false;
                    $.showAlert("danger", $.getMessage("js.error.quarter.overlap", ["third"]), 10000, "displayErr");
                    break;
                }
                monthsUsed[z] = true;
                if(z === 11){
                    z = 0;
                }
                else {
                    z++;
                }
            }
        }
        if(fromMonth4 !== -1 && toMonth4 !== -1) {
            var q4Complete = false;
            var k = fromMonth4 - 1;
            while(!q4Complete){
                if(k === toMonth4 - 1){
                    q4Complete = true;
                }
                if (monthsUsed[k]) {
                    isValid = false;
                    $.showAlert("danger", $.getMessage("js.error.quarter.overlap", ["fourth"]), 10000, "displayErr");
                    break;
                }
                monthsUsed[k] = true;
                if(k === 11){
                    k = 0;
                }
                else {
                    k++;
                }
            }
        }

        return isValid;
    },
    checkContainsAllMonths : function() {
        var toMonth1 = parseInt($('#periodToMonth1').val()) || -1;
        var fromMonth1 = parseInt($('#periodFromMonth1').val()) || -1;
        var toMonth2 = parseInt($('#periodToMonth2').val()) || -1;
        var fromMonth2 = parseInt($('#periodFromMonth2').val());
        var toMonth3 = parseInt($('#periodToMonth3').val()) || -1;
        var fromMonth3 = parseInt($('#periodFromMonth3').val()) || -1;
        var toMonth4 = parseInt($('#periodToMonth4').val()) || -1;
        var fromMonth4 = parseInt($('#periodFromMonth4').val()) || -1;
        var isValid = true;
        var monthsUsed = [false,false,false,false,false,false,false,false,false,false,false,false];
        var unusedMonths = "";
        var date = new Date();

        var q1Complete = false;
        var x = fromMonth1 - 1;
        while(!q1Complete){
            if(x === toMonth1 - 1){
                q1Complete = true;
            }
            monthsUsed[x] = true;
            if(x === 11){
                x = 0;
            }
            else {
                x++;
            }
        }

        var q2Complete = false;
        var y = fromMonth2 - 1;
        while(!q2Complete){
            if(y === toMonth2 - 1){
                q2Complete = true;
            }
            monthsUsed[y] = true;
            if(y === 11){
                y = 0;
            }
            else {
                y++;
            }
        }

        var q3Complete = false;
        var z = fromMonth3 - 1;
        while(!q3Complete){
            if(z === toMonth3 - 1){
                q3Complete = true;
            }
            monthsUsed[z] = true;
            if(z === 11){
                z = 0;
            }
            else {
                z++;
            }
        }

        var q4Complete = false;
        var k = fromMonth4 - 1;
        while(!q4Complete){
            if(k === toMonth4 - 1){
                q4Complete = true;
            }
            monthsUsed[k] = true;
            if(k === 11){
                k = 0;
            }
            else {
                k++;
            }
        }

        for(var j = 0; j < monthsUsed.length; j++){
           if(!monthsUsed[j]){
               isValid = false;
               date.setMonth(j);
               if(pageLang === "eng") {
                   var getMonthNameEng = date.toLocaleString('en-ca', {month: 'long'});
                   unusedMonths += getMonthNameEng + ", ";
               }
               else {
                   var getMonthNameFra = date.toLocaleString('fr-ca', {month: 'long'});
                   unusedMonths += getMonthNameFra + ", ";
               }
           }
        }

        if(!isValid) {
            $.showAlert("danger", $.getMessage("js.error.quarter.all_months", [unusedMonths.substr(0,unusedMonths.length - 2)]), 10000, "displayErr");
        }
        return isValid;
    },
    changeFrom : function(number) {
        var to = -1;
        switch (number) {
            case 2: {
                to = parseInt($("#periodToMonth1").val());
                if(to !== 12) {
                    $('#periodFromMonth2').val(to + 1);
                }
                else {
                    $('#periodFromMonth2').val(1);
                }
                break;
            }
            case 3: {
                to = parseInt($("#periodToMonth2").val());
                if(to !== 12) {
                    $('#periodFromMonth3').val(to + 1);
                }
                else {
                    $('#periodFromMonth3').val(1);
                }
                break;
            }
            case 4: {
                to = parseInt($("#periodToMonth3").val());
                if(to !== 12) {
                    $('#periodFromMonth4').val(to + 1);
                }
                else {
                    $('#periodFromMonth4').val(1);
                }
                break;
            }
            default: {
                break;
            }
        }
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/replace', quarter.initReplace);
});