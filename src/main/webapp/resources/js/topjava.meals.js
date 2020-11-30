var ctx;
var filterOn = false;

// $(document).ready(function () {
$(function () {
    // https://stackoverflow.com/a/5064235/548473
    ctx = {
        ajaxUrl: "ui/meals",
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    };
    makeEditable();
});

function filter() {
    $.ajax({
        url: ctx.ajaxUrl + "/filter",
        data: $('#filterForm').serialize(),
        type: "Get"
    }).done(updateTableByData);
    filterOn = true;
}

function clearFilter() {
    filterOn = false;
    form = $('#filterForm').trigger('reset');
    updateTable()
}

function updateTable() {
    if (filterOn) {
        filter()
    } else {
        $.get(ctx.ajaxUrl, function (data) {
            updateTableByData(data);
        });
    }
}

