var loading = {
    show: function () {
        $('#loadingModal').modal({'show': 'center', "backdrop": "static"});
    },
    hide: function () {
        $('#loadingModal').modal('hide');
    }
}