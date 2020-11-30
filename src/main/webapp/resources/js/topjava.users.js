var ctx;

// $(document).ready(function () {
$(function () {
    // https://stackoverflow.com/a/5064235/548473
    ctx = {
        ajaxUrl: "admin/users",
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
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

function updateTable() {
    $.get(ctx.ajaxUrl, function (data) {
        updateTableByData(data);
    });
}

function enable(cb, id) {
    var enabled = cb.is(":checked");
    $.ajax({
        url: ctx.ajaxUrl + "/" + id,
        type: "POST",
        data: "enabled=" + enabled
    }).done(function () {
        // updateTable();
        cb.closest("tr").attr("data-userEnabled", enabled);
        successNoty(enabled ? "enabled" : "disabled");
    }).fail(function () {
        $(cb).prop("checked", !enabled);
    });
}