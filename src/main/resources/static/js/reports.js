var reports = {
    initReports: function() {
        $('#reportType,#cellCriteria_year,#cellCriteria_sampleSpecies').on('change', function(event) {
            var type = $('#reportType').children('option:selected').val();
            var year = $('#cellCriteria_year').children('option:selected').val();
            var species = $('#cellCriteria_sampleSpecies').children('option:selected').val();

            $('#btnGenerate').prop('disabled', type === '' || year === '' || species === '');

            if ($(event.target).attr('id') === 'reportType') {
                if (type !== '') {
                    var text = $(event.target).children('option:selected').text();
                    $('#report-title-span').html(text);
                } else {
                    $('#report-title-span').html('');
                }
            }
        });

        $('form#execute-report-form').on('submit', function(event) {
            event.preventDefault();
            if (!$('#btnGenerate').prop('disabled')) {
                $.processPromise(new Promise(function(doReturn) {
                    var data = $(event.target).serialize();
                    $.ajax({
                       url: $(event.target).attr('action'),
                       type: $(event.target).attr('method').toUpperCase(),
                       data: data,
                       success: function(reportHTML) {
                           $('#selection-criteria-details').removeAttr('open');
                           $('#report-section').show().find('div').html(reportHTML);
                           $.convertDataTextOnly(['','0'], true);
                           doReturn();
                       },
                       error: function() {
                           reports.clearReport();
                           $.showError('js.error.serverDB_operation_failed');
                           doReturn();
                       }
                    });
                }));
            }
        });
    },
    clearReport: function() {
        $('#report-section').hide().find('div').html('');
        $('#selection-criteria-details').prop('open', true);
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/reports', reports.initReports);
});