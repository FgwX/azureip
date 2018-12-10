String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "")
};
$(function () {
    $('.navHover').mouseenter(function () {
        $(this).parent().find('.hoverBox').stop(false, true).slideDown();
        $(this).addClass('open');
    });
    $('.navHover').mouseleave(function () {
        $(this).children('.hoverBox').stop(false, true).slideUp();
        $(this).children().removeClass('open');
    });
});
$(document).ready(
    function () {
        $(".topselect ").hover(function () {
                $(".topselect .select").show();
            }, function () {
                $(".topselect .select").hide();
            }
        );
        $(".topselect .select li").click(function () {
                $(".showselect").html($(this).html());
                var tip = $(this).html();
                if (tip == "全部") {
                    tip = "商标名/申请人/注册号";
                }
                $("#keyword").attr('placeholder', '请输入' + tip + "关键词！")
                $("#stT").val($(this).attr("data-st"));
                $(".topselect .select li").removeClass("active");
                $(this).addClass("active");
                $(".topselect .select").hide();
            }
        );

        headReflashScore();
        $(".wordsNum4").hover(function () {
            $(this).addClass("hover")
        }, function () {
            $(this).removeClass("hover")
        });
        $("#myRenlingEntry").hover(function () {
            $(this).addClass("open");
            $("#myRenling").animate({height: "show", opacity: "show"}, "fast", function () {
                $("#myRenling").show()
            });
            $("#myRenling").css("display", "block")
        }, function () {
            $(this).removeClass("open");
            $("#myRenling").animate({height: "hide", opacity: "hide"}, "fast", function () {
                $("#myRenling").hide()
            })
        });
        $(".messageList_con dt").each(function (a) {
            if (a % 2 == 1) {
                $(this).addClass("dt1")
            }
        });
        $("input[focucmsg]").each(function () {
            if ($(this).val() == "") {
                $(this).val($(this).attr("focucmsg"));
                $(this).val($(this).attr("focucmsg")).css("color", "#979393")
            } else {
                $(this).css("font-style", "normal")
            }
            $(this).focus(function () {
                $(this).css("font-style", "normal");
                if ($(this).val() == $(this).attr("focucmsg")) {
                    $(this).val("");
                    $(this).val("").css("color", "#6b6969")
                }
            });
            $(this).blur(function () {
                if (!$(this).val()) {
                    $(this).val($(this).attr("focucmsg"));
                    $(this).val($(this).attr("focucmsg")).css("color", "#979393");
                    $(this).css("font-style", "italic")
                }
            })
        })
    });

function loadMyRenlingZch() {
    TMRAsynGet("/hasrenlingzch!ajaxHeader.php", "text", loadMyRenlingZchBack)
}

function loadMyRenlingZchBack(d) {
    if (d.indexOf("ERROR") >= 0) {
        $("#my_renling_zch").html("您" + d.split(":")[1]);
        return
    }
    var b = "";
    var c = d.split(";");
    for (var a = 0; a < c.length; a++) {
        if (c[a] != null) {
            b += "<a href='/home/detail.php?zch=" + c[a].split("#")[0] + "&gjfl=" + c[a].split("#")[1] + "' target='_blank'>" + c[a].split("#")[0] + "</a>"
        }
    }
    b += "<div class='clear'></div>";
    b += "<a href='/home/myrenlingzch.php' style='font-size: 12px;font-weight: normal;margin-right:10px'>更多...</a>";
    $("#my_renling_zch").html(b)
}

function loadMyRenlingSqr() {
    TMRAsynGet("/hasrenlingsqr!ajaxHeader.php", "text", loadMyRenlingSqrBack)
}

function loadMyRenlingSqrBack(c) {
    if (c.indexOf("ERROR") >= 0) {
        $("#my_renling_sqr").html("您" + c.split(":")[1]);
        return
    }
    var d = "";
    var b = c.split(";");
    for (var a = 0; a < b.length; a++) {
        if (b[a] != null) {
            d += "<a href='/searchmore/sqr!view.php?sqrmcZw=" + encodeURI(b[a].split("#")[0]) + "&idCardNo=" + b[a].split("#")[1] + "&dlrmc=' title='" + b[a].split("#")[0] + "' target='_blank'>" + b[a].split("#")[0] + "</a><div class='clear'></div>"
        }
    }
    d += "<a href='/home/myrenlingsqr.php' style='font-size: 12px;font-weight: normal;margin-right:10px'>更多...</a>";
    $("#my_renling_sqr").html(d)
}

var scoreNull = "<div class='scoreNullHelp'>您可以通过如下方式来获得积分：</div>";
scoreNull += "<div class='scoreNullStep'>1. 每天登录标库网，系统每天赠送<span class='jifen'></span><span class='score'>" + globalLoginSysSend + "</span>积分。<br/>";
scoreNull += "2. 邀请会员注册获得奖励<span class='jifen'></span><span class='score'>" + globalInviteUserSysSend + "</span>积分/人。<a href='/home/myinvite.php' target='_blank'>现在就去邀请&gt;&gt;</a><br/>";
scoreNull += "3. 取消关注回收积分。<a href='http://help.tmkoo.com/category/score/' target='_blank'>查看积分消费、回收标准&gt;&gt;</a><br/>";
scoreNull += "4. 反馈问题、提意见奖励<span class='jifen'></span><span class='score'>10~300</span>积分。<br/>";
scoreNull += "5. <font color='#ff0000' style='font-size: 22px;'>在线支付</font>，立马获得充足积分<font style='color:#ff0000;font-family:Verdana'>￥1人民币</font> = <span class='jifen'></span><span class='score'>100</span>。<br/><a href='/sail.php' target='_blank'>现在去支付&gt;&gt;</a><br/>";
scoreNull += "</div>";

function TMRGet(a, c, b) {
    if (a.indexOf("?") > 0) {
        a += "&t=" + Math.random()
    } else {
        a += "?t=" + Math.random()
    }
    $.ajax({
        async: false, type: "get", url: a, data: null, timeout: 90000, dataType: c, success: b, error: function () {
            art.dialog.tips("失败，请重试", 1)
        }
    })
}

function TMRAsynGet(a, c, b) {
    if (a.indexOf("?") > 0) {
        a += "&t=" + Math.random()
    } else {
        a += "?t=" + Math.random()
    }
    $.ajax({
        type: "get", url: a, timeout: 90000, data: null, dataType: c, success: b, error: function () {
            art.dialog.tips("失败，请重试", 1)
        }
    })
}

function TMRPost(a, c, e, d) {
    var b = $("#" + c).serialize();
    $.ajax({
        async: false, type: "POST", url: a, timeout: 90000, data: b, dataType: e, success: d, error: function () {
            art.dialog.tips("失败，请重试", 1)
        }
    })
}

function TMRAsynPost(a, c, e, d) {
    var b = $("#" + c).serialize();
    $.ajax({
        type: "POST",
        url: a,
        timeout: 90000,
        data: b,
        dataType: e,
        success: d,
        error: function () {
            art.dialog.tips("失败，请重试", 1)
        }
    })
}

var afterLoginUrl;

function TMRVerifyLogin(a) {
    $.ajax({
        type: "POST",
        url: "/user!hasLogin.php",
        timeout: 90000,
        data: null,
        dataType: "text",
        async: false,
        success: function (c) {
            if (c == "SUCCESS") {
                location.href = a
            } else {
                afterLoginUrl = a;
                var b = {title: "请登录", lock: true};
                art.dialog.open("/login.php?act=url", b, false)
            }
        },
        error: function () {
            art.dialog.tips("失败，请重试", 1)
        }
    })
}

function finishLogin() {
    if (afterLoginUrl != null && afterLoginUrl.length > 0) {
        location.href = afterLoginUrl
    }
}

function goReg() {
    location.href = "/regp.php"
}

function goFindPwd() {
    location.href = "/find-pwdp.php"
}

function headReflashScore() {
    TMRAsynGet("/user-score.php", "text", function (a) {
        $("#myscore").html(a);
        $("#myscoreHome").html(a)
    })
}

function goSearchTM() {
    if ($("#topSearchKey").val() == null || $("#topSearchKey").val() == "" || $.trim($("#topSearchKey").val()) == "录入商标名/注册号/申请人，超1500万海量商标任你搜！") {
        art.dialog.tips("必须输入待查询的关键字！", 2);
        return
    }
    $("#topSearch").submit()
};

function topS() {
    if ($("#keywordT").val() == "") {
        layer.tips('请输入商标名称关键字', '#keywordT', {
            tips: [1, '#3595CC'],
            time: 4000
        });
        return;
    }
    $("#topF").submit();
}
