var ctx, mealAjaxUrl = "profile/meals/";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: mealAjaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(mealAjaxUrl, updateTableByData);
}

$(function () {

    $.datetimepicker.setLocale(localeCode);
    var startDate = $('#startDate');
    var endDate = $('#endDate');
    startDate.datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        formatDate: 'Y-m-d',
        onShow: function (ct) {
            this.setOptions({
                maxDate: endDate.val() ? endDate.val() : false
            })
        }
    });
    endDate.datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        formatDate: 'Y-m-d',
        onShow: function (ct) {
            this.setOptions({
                minDate: startDate.val() ? startDate.val() : false
            })
        }
    });

    var startTime = $('#startTime');
    var endTime = $('#endTime');
    startTime.datetimepicker({
        datepicker: false,
        format: 'H:i',
        onShow: function (ct) {
            this.setOptions({
                maxTime: endTime.val() ? endTime.val() : false
            })
        }
    });
    endTime.datetimepicker({
        datepicker: false,
        format: 'H:i',
        onShow: function (ct) {
            this.setOptions({
                minTime: startTime.val() ? startTime.val() : false
            })
        }
    });

    $('#dateTime').datetimepicker({
        format: 'Y-m-d H:i'
    });

    ctx = {
        ajaxUrl: mealAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (data, type, row) {
                        return data.replace('T', " ");
                    }
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderEditBtn
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                if (data.excess) {
                    $(row).attr("data-mealExcess", true);
                } else {
                    $(row).attr("data-mealExcess", false);
                }
            }
        }),
        updateTable: function () {
            $.get(mealAjaxUrl, updateTableByData);
        }
    };
    makeEditable();
});
