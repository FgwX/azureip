// 请求获取通知权限
Notification.requestPermission().then(function (result) {
    // 暂无功能
});

var annNumEle, localLatestAnnInfo;
var submitQueryLoading, importAnnLoading, delLocalLoading;

$(function () {
    annNumEle = $("input[name='annNum']");
    localLatestAnnInfo = $("#localLatestAnnInfo");
    queryLocalLatestAnn();
    // 创建按钮Loading效果
    submitQueryLoading = Ladda.create(document.getElementById("submitQueryBtn"));
    importAnnLoading = Ladda.create(document.getElementById("importRemoteAnnBtn"));
    delLocalLoading = Ladda.create(document.getElementById("delLocalAnnBtn"));
    optExcelLoading = Ladda.create(document.getElementById("optExcelBtn"));
    addLinkLoading = Ladda.create(document.getElementById("addLinkBtn"));
    queryRejectionLoading = Ladda.create(document.getElementById("queryRejectionBtn"));

    $("#sqrqStart").daterangepicker({

            singleDatePicker: true,
            showDropdowns: true,
            autoUpdateInput: true,
            // timePicker24Hour: true,
            timePicker: false,
            "locale": {
                format: 'YYYYMMDD',
                applyLabel: "应用",
                cancelLabel: "取消",
                resetLabel: "重置",
            }
        },
        function (start, end, label) {
            // beginTimeTake = start;
            // if (!this.startDate) {
            //     this.element.val('');
            // } else {
            //     this.element.val(this.startDate.format(this.locale.format));
            // }
        }
    );
});

//查询本地公告中最新一期的公告期号
function queryLocalLatestAnn() {
    $.ajax({
        type: "GET",
        url: "/ann/queryLocalLatestAnnNum",
        async: false,
        success: function (resp) {
            if (resp.status === "S") {
                //查询期号为本地最新期号加1
                annNumEle.val(parseInt(resp.result) + 1);
                localLatestAnnInfo.html("数据库中公告截止第" + resp.result + "期")
            }
        }
    });
}

// 公告表单检查
function annFormCheck() {
    if (annNumEle.val()) {
        $("#annNumCheck").hide();
        return true;
    } else {
        $("#annNumCheck").show();
        return false;
    }
}

// 提交公告表单
function submitAnnForm() {
    if (annFormCheck()) {
        submitQueryLoading.start();
        $.ajax({
            type: "POST",
            url: "/ann/queryAnnCount",
            data: $("#annImportForm").serializeArray(),
            success: function (resp) {
                if (resp.status === "S") {
                    var localCount = resp.result.localCount;
                    var remoteCount = resp.result.remoteCount;
                    $("#localAnnCount").val(localCount);
                    $("#remoteAnnCount").val(remoteCount);
                    if (localCount === remoteCount) {
                        $("#importRemoteAnnBtn").attr("disabled", "disabled");
                        $("#delLocalAnnBtn").attr("disabled", "disabled");
                        $("#localAnnCount").removeClass("bg-danger");
                        $("#remoteAnnCount").removeClass("bg-danger");
                    } else {
                        $("#delLocalAnnBtn").attr("disabled", "disabled");
                        if (localCount === 0) {
                            $("#importRemoteAnnBtn").removeAttr("disabled");
                        } else {
                            $("#importRemoteAnnBtn").attr("disabled", "disabled");
                            if (remoteCount !== 0) {
                                $("#delLocalAnnBtn").removeAttr("disabled");
                                $("#localAnnCount").addClass("bg-danger");
                                $("#remoteAnnCount").addClass("bg-danger");
                            }
                        }
                    }
                } else {
                    // $("#maskLayer").modal('show');
                    // $('#maskLayer').modal({backdrop: 'static', keyboard: false});
                    alert("查询失败，原因如下：\n" + resp.message);
                }
            },
            complete: function () {
                submitQueryLoading.stop();
            }
        });
    }
}

// 导入官网公告
function importRemoteAnn() {
    $("#submitQueryBtn").attr("disabled", "disabled");
    importAnnLoading.start();
    $.ajax({
        type: "POST",
        url: "/ann/importAnns",
        data: $("#annImportForm").serializeArray(),
        success: function (resp) {
            if (resp.status === "S") {
                $("#localAnnCount").val(resp.result);
                notify("提示 - TMSpider", "第" + $("input[name='annNum']").val() + "期公告导入成功！");
                importAnnLoading.disable();
            } else {
                notify("警告 - TMSpider", "第" + $("input[name='annNum']").val() + "期公告导入失败，请重试！");
            }
        },
        complete: function () {
            $("#submitQueryBtn").removeAttr("disabled");
            importAnnLoading.stop();
        }
    });
}

// 清除本地公告（与官网数量不一致，需要重新导入）
function delLocalAnnByAnnNum() {
    $("#submitQueryBtn").attr("disabled", "disabled");
    delLocalLoading.start();
    $.ajax({
        type: "POST",
        url: "/ann/deleteByAnnNum",
        data: $("#annImportForm").serializeArray(),
        success: function (resp) {
            if (resp.status === "S") {
                notify("提示 - TMSpider", "第" + $("input[name='annNum']").val() + "期公告删除成功！");
                $("#localAnnCount").val(0);
                $("#localAnnCount").removeClass("bg-danger");
                $("#remoteAnnCount").removeClass("bg-danger");
                $("#importRemoteAnnBtn").removeAttr("disabled");
                delLocalLoading.disable();
            } else {
                notify("警告 - TMSpider", "第" + $("input[name='annNum']").val() + "期公告删除失败，请重试！");
            }
        },
        complete: function () {
            $("#submitQueryBtn").removeAttr("disabled");
            delLocalLoading.stop();
        }
    });
}

// 处理本地表格
function optExcel() {
    if (confirm("即将开始处理，请误打开待处理的表格！\n是否继续？")) {
        optExcelLoading.start();
        $.ajax({
            type: "GET",
            url: "/ann/optExcel",
            success: function (resp) {
                if (resp.status === "S") {
                    var fileNames = "";
                    for (var i = 0; i < resp.resultList.length; i++) {
                        fileNames += (i + 1) + ".  " + resp.resultList[i] + "\n";
                    }
                    notify("提示 - TMSpider", "已成功处理以下文档：\n" + fileNames);
                } else {
                    notify("警告 - TMSpider", "表格处理失败！原因如下：\n" + resp.message);
                }
            },
            complete: function () {
                optExcelLoading.stop();
            }
        });
    }
}

// 通知方法
function notify(title, body) {
    if (Notification.permission === "granted") {
        var notification = new Notification(title, {
            icon: "asset/icon/azure_log.png",
            body: body,
            tag: 0,
            renotify: false
        });
        notification.onclick = function () {
            window.focus();
            notification.close();
        };
    } else {

    }
}

function addLink() {
    if (confirm("即将开始调用【添加链接】接口！\n是否继续？")) {
        addLinkLoading.start();
        $.ajax({
            type: "GET",
            url: "/reg/addLink",
            success: function (resp) {
                if (resp.status === "S") {
                    notify("提示 - TMSpider", "【添加链接】已完成");
                } else {
                    notify("警告 - TMSpider", "【添加链接】失败！原因如下：\n" + resp.message);
                }
            },
            complete: function () {
                addLinkLoading.stop();
            }
        });
    }
}

function queryRejection() {
    if (confirm("即将开始调用【查询驳回】接口！\n是否继续？")) {
        addLinkLoading.start();
        $.ajax({
            type: "GET",
            url: "/rej/queryRej",
            success: function (resp) {
                if (resp.status === "S") {
                    notify("提示 - TMSpider", "【查询驳回】已完成");
                } else {
                    notify("警告 - TMSpider", "【查询驳回】失败！原因如下：\n" + resp.message);
                }
            },
            complete: function () {
                addLinkLoading.stop();
            }
        });
    }
}