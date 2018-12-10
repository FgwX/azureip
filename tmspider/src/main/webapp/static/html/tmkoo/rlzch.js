function addShoping(id) {
    var shop = $("#myRenlingEntry");
    var target = $("#" + id),
        x = target.offset().left + 80,
        y = target.offset().top,
        X = shop.offset().left + shop.width() / 2 - target.width() / 2 + 10,
        Y = shop.offset().top;

    if ($('#floatOrder').length <= 0) {
        $('body').append('<div id="floatOrder" style="position: absolute;z-index:9999"><img src="/js/cart/images/crl.png"/></div');
    }
    ;
    var $obj = $('#floatOrder');
    var speed = 500;
    if (y > 3000) {
        speed = 2000;
    }
    else if (y > 2000) {
        speed = 1000;
    }
    else if (y > 1000) {
        speed = 700;
    }
    if (!$obj.is(':animated')) {
        $obj.css({'left': x, 'top': y}).animate({'left': X, 'top': Y - 60}, speed, function () {
            $obj.stop(false, false).animate({'top': Y - 10, 'opacity': 0}, 500, function () {
                $obj.fadeOut(300, function () {
                    $obj.remove();
                });
            });
        });
    }
    ;
}

//=======================================
var rZch = "";
var rGjfl = "";
//关注：
//第1步：判断是否登录
function renlingzchStep1() {
    TMRAsynGet("/user!hasLogin.php", "text", renlingzchSetp2);
}

//第2步：接收是佛已登录，判断是否已关注
function renlingzchSetp2(data) {
    if (data == 'SUCCESS') {
        //判断是否已关注
        $.blockUI({message: "<div style='font-weight: bold;height:100px;'><img src='/images/loading.gif'  alt='计算中'/></div>"});
        TMRAsynGet("/home/renlingzch!hasRenling.php?zch=" + rZch + "&gjfl=" + rGjfl, "text", renlingzchSetp3);
    }
    else {
        //弹出登录窗口
        afterLoginUrl = null;
        var options = {title: '请登录', lock: true,};
        art.dialog.open('/login.php?act=renlingzchStep1', options, false);
    }
}

//第3步：接收是否已关注及是否提示确认，进一步验证积分是否够用。
function renlingzchSetp3(data) {
    if (data.indexOf("SUCCESS") > 0) {
        //  zch:gjfl:SUCCESS:1;    超过多少分就提示
        showConfirmOverScore = parseInt(data.split(":")[3]);
        TMRAsynGet("/user-score.php", "text", renlingzchSetp4);
    }
    else if (data.indexOf("ERROR") > 0) {
        $.unblockUI();
        //已关注
        art.dialog.tips('已关注，无需重新关注', 1);
    }
    else {
        $.unblockUI();
        art.dialog.tips(data, 1);
    }
}

//第4步：接收积分余额结果，提示扣积分。。
function renlingzchSetp4(data) {
    $.unblockUI();
    if (parseInt(data) < globalRenlingZchConsume) {
        //积分不够用了。提示不能进入
        art.dialog("<div class='scoreNullTitle'>您的积分不足。当前积分<span class='jifen'></span><span class='score'>" + data + "</span></div>" + scoreNull, function () {
            ;
        });
    }
    else {
        //积分够用，看看是否需要提示
        if (globalRenlingZchConsume >= showConfirmOverScore) {
            //需要确认
            var cH = "<div class='confirmTitle'>您确定关注此商标吗？<br/><div class='confirmTxt'>如果确定，将扣除您的积分<span class='jifen'></span><span class='score'>" + globalRenlingZchConsume + "</span>。</div><br/>";
            cH += "<div class='confirmChk'><input type='radio' id='showConfirmOverScore_1' name='showConfirmOverScore' " + (showConfirmOverScore == 1 ? "checked='checked'" : "") + " value='1' /><label for='showConfirmOverScore_1'>任何扣分都提示</label>";
            cH += "<br/><input type='radio' id='showConfirmOverScore_500' name='showConfirmOverScore' " + (showConfirmOverScore == 500 ? "checked='checked'" : "") + " value='500' /><label for='showConfirmOverScore_500'>一次扣分超过<span class='score'>500</span>就提示</label>";
            cH += "<br/><input type='radio' id='showConfirmOverScore_5000' name='showConfirmOverScore' " + (showConfirmOverScore == 5000 ? "checked='checked'" : "") + " value='5000' /><label for='showConfirmOverScore_5000'>一次扣分超过<span class='score'>5000</span>就提示</label>";
            cH += "<br/><input type='radio' id='showConfirmOverScore_10000' name='showConfirmOverScore' " + (showConfirmOverScore == 10000 ? "checked='checked'" : "") + " value='10000' /><label for='showConfirmOverScore_10000'>一次扣分超过<span class='score'>10000</span>就提示</label>";
            cH += "<br/><input type='radio' id='showConfirmOverScore_30000' name='showConfirmOverScore' " + (showConfirmOverScore == 30000 ? "checked='checked'" : "") + " value='30000' /><label for='showConfirmOverScore_30000'>一次扣分超过<span class='score'>30000</span>就提示</label>";
            cH += "<br/><input type='radio' id='showConfirmOverScore_50000' name='showConfirmOverScore' " + (showConfirmOverScore == 50000 ? "checked='checked'" : "") + " value='50000' /><label for='showConfirmOverScore_50000'>一次扣分超过<span class='score'>50000</span>就提示</label>";
            cH += "<br/><input type='radio' id='showConfirmOverScore_80000' name='showConfirmOverScore' " + (showConfirmOverScore == 80000 ? "checked='checked'" : "") + " value='80000' /><label for='showConfirmOverScore_80000'>一次扣分超过<span class='score'>80000</span>就提示</label>";
            cH += "<br/><input type='radio' id='showConfirmOverScore_100000' name='showConfirmOverScore' " + (showConfirmOverScore == 100000 ? "checked='checked'" : "") + " value='100000' /><label for='showConfirmOverScore_100000'>一次扣分超过<span class='score'>100000</span>就提示</label>";
            cH += "<div style='padding-top: 10px;'><span class='score' style='font-family: Helvetica;'><strong>备注</strong>：100积分 = ￥1人民币</span></div>";
            cH += "</div>";
            art.dialog.confirm(cH, function () {
                //执行确定操作
                var configShowConfirmOverScore = $('input[name="showConfirmOverScore"]:checked').val();
                if (parseInt(configShowConfirmOverScore) != showConfirmOverScore) {
                    TMRAsynGet("/home/other-config.php?keyName=CONSUME_SCORE_NEED_CONFIRM_OVER&keyValue=" + configShowConfirmOverScore, "text", renlingzchSetp5);
                }
                else {
                    renlingzchSetp5("SUCCESS");
                }
            }, function () {
                //执行取消操作
                return;
            });
        }
        else {
            renlingzchSetp5("SUCCESS");
        }
    }
}

//第5步：进行关注动作
function renlingzchSetp5(data) {
    if (data.indexOf("SUCCESS") >= 0) {
        $.blockUI({message: "<div style='font-weight: bold;height:100px;'><img src='/images/loading.gif'  alt='计算中'/></div>"});
        TMRAsynGet("/home/renlingzch!save.php?zch=" + rZch + "&gjfl=" + rGjfl, "text", renlingzchFinish);
        $.unblockUI();
    }
    else {
        art.dialog.tips(data, 1);
    }
}
