window.open("http://wsjs.saic.gov.cn")



var rootPath = "";
var sidebarSelectedList = new (Map)();
$(document).ready(function () {
    window.alert = function ($_473) {
        $.fz_common.alert(l_C_HINT, $_473);
    };
});

function isIECompat() {
    var $_473 = navigator.userAgent.toLowerCase();
    var $_484 = window.opera;
    var $_482 = {
        ie: /(msie\s|trident.*rv:)([\w.]+)/.test($_473),
        opera: (!!$_484 && $_484.version),
        webkit: ($_473.indexOf(' applewebkit/') > -1),
        mac: ($_473.indexOf('macintosh') > -1),
        quirks: (document.compatMode == 'BackCompat')
    };
    var $_483 = 0;
    if ($_482.ie) {
        var $_476 = $_473.match(/(?:msie\s([\w.]+))/);
        var $_472 = $_473.match(/(?:trident.*rv:([\w.]+))/);
        if ($_476 && $_472 && $_476[1] && $_472[1]) {
            $_483 = Math.max($_476[1] * 1, $_472[1] * 1);
        } else if ($_476 && $_476[1]) {
            $_483 = $_476[1] * 1;
        } else if ($_472 && $_472[1]) {
            $_483 = $_472[1] * 1;
        } else {
            $_483 = 0;
        }
        $_482.ie7Compat = (($_483 == 7 && !document.documentMode) || document.documentMode == 7);
        $_482.ie9below = $_483 < 8;
        if ($_482.ie7Compat || $_482.ie9below) {
            return true;
        }
    }
    return false;
}

function goUrl($_473, $_482) {
    if ($_473.indexOf('?') > -1) {
        $_473 = $_473 + "&";
    } else {
        $_473 = $_473 + "?";
    }
    if ($_482 == null || $_482 == "") {
        _$zr(window, '=', 'location', $_473 + 'locale=' + $("#locale").val());
    } else {
        _$zq(window, 'open', $_473 + 'locale=' + $("#locale").val(), $_482);
    }
}

function checkBoxClick() {
    $("#list_box").find(":input").bind("click", function () {
        var $_473 = 0;
        var $_482 = true;
        $("#list_box").find(":input").each(function () {
            var $_484 = $(this).attr("tid");
            if (_$zq($(this), 'prop', "checked") && $(this).attr("tid") != "checkAll") {
                $_473++;
                sidebarSelectedList.put($_484, {"value": $(this).attr("tid") + "@" + $(this).attr("rn") + "@" + $(this).attr("fd") + "@" + $(this).attr("sn") + "@" + $(this).attr("mno") + "@" + $(this).attr("hnc") + "@" + $(this).attr("nc") + "@" + $(this).attr("img")});
            } else {
                sidebarSelectedList.remove($_484);
                if ($_484 != "checkAll") {
                    $_482 = false;
                }
            }
        });
        $("#selectedNum").text($_473);
        _$zq($("#checkAll"), 'prop', "checked", $_482);
    });
}

function _doSubmit($_484, $_481, $_472, $_475) {
    $_481 = $_481 + "&locale=" + $("#locale").val();
    var $_473 = "";
    if ($_472 != null) {
        $_473 = "target=\"" + $_472 + "\"";
    }
    var $_482 = "post";
    if ($_475 != null) {
        $_482 = $_475;
    }
    $("#myPatentFormTmp").remove();
    $("body").append('<form action="' + $_484 + '" style="display:none;" ' + $_473 + ' method="' + $_482 + '" name="myPatentFormTmp" id="myPatentFormTmp"></form>');
    var $_477 = $_481.split("&");
    for (var $_483 = 0; $_483 < $_477.length; $_483++) {
        var $_478 = _$sG($_477, $_483).indexOf('=');
        if ($_478 == -1) continue;
        var $_476 = _$sG($_477, $_483).substring(0, $_478);
        var $_479 = decodeURIComponent(_$sG($_477, $_483).substring($_478 + 1));
        $("#myPatentFormTmp").prepend('<input type="text" name="' + $_476 + '" />');
        $("#myPatentFormTmp input:first-child").val($_479);
    }
    _$zq($("#myPatentFormTmp"), 'submit');
}

function getCookie($_482) {
    var $_473, $_484 = new (RegExp)("(^| )" + $_482 + "=([^;]*)(;|$)");
    if ($_473 = _$dM(document).match($_484)) return unescape($_473[2]); else return null;
}

function Map() {
    this.container = new (Object)();
}

Map.prototype.put = function ($_473, $_482) {
    _$zr(this.container, '=', $_473, $_482);
};
Map.prototype.get = function ($_473) {
    return _$sG(this.container, $_473);
};
Map.prototype.keySet = function () {
    var $_484 = new (Array)();
    var $_473 = 0;
    for (var $_482 in this.container) {
        if ($_482 == 'extend') {
            continue;
        }
        _$zr($_484, '=', $_473, $_482);
        $_473++;
    }
    return $_484;
};
Map.prototype.size = function () {
    var $_473 = 0;
    for (var $_482 in this.container) {
        if ($_482 == 'extend') {
            continue;
        }
        $_473++;
    }
    return $_473;
};
Map.prototype.remove = function ($_473) {
    delete this.container[$_473];
};
Map.prototype.toString = function () {
    var $_476 = "";
    for (var $_473 = 0, $_482 = this.keySet(), $_484 = $_482.length; $_473 < $_484; $_473++) {
        $_476 = $_476 + _$sG($_482, $_473) + "=" + this.container[_$sG($_482, $_473)] + ";\n";
    }
    return $_476;
};
Array.prototype.remove = function ($_473) {
    if (isNaN($_473) || $_473 > this.length) {
        return false;
    }
    for (var $_482 = 0, $_484 = 0; $_482 < this.length; $_482++) {
        if (_$sG(this, $_482) != _$sG(this, $_473)) {
            this[$_484++] = _$sG(this, $_482);
        }
    }
    this.length -= 1;
};
String.prototype.endWith = function ($_473) {
    if ($_473 == null || $_473 == "" || this.length == 0 || $_473.length > this.length) return false;
    if (this.substring(this.length - $_473.length) == $_473) return true; else return false;
    return true;
};
String.prototype.startWith = function ($_473) {
    if ($_473 == null || $_473 == "" || this.length == 0 || $_473.length > this.length) return false;
    if (this.substr(0, $_473.length) == $_473) return true; else return false;
    return true;
};

function AutoResizeImage($_483, $_472, $_475) {
    var $_476 = new (Image)();
    _$zr($_476, '=', 'src', _$sG($_475, 'src'));
    var $_484;
    var $_481;
    var $_473 = 1;
    var $_477 = $_476.width;
    var $_482 = $_476.height;
    $_481 = $_483 / $_477;
    $_484 = $_472 / $_482;
    if ($_483 == 0 && $_472 == 0) {
        $_473 = 1;
    } else if ($_483 == 0) {
        if ($_484 < 1) $_473 = $_484;
    } else if ($_472 == 0) {
        if ($_481 < 1) $_473 = $_481;
    } else if ($_481 < 1 || $_484 < 1) {
        $_473 = ($_481 <= $_484 ? $_481 : $_484);
    }
    if ($_473 < 1) {
        $_477 = $_477 * $_473;
        $_482 = $_482 * $_473;
    }
    $($_475).css("height", $_482);
    $($_475).css("width", $_477);
}

function changeLocale($_482) {
    var $_473 = $($_482).attr("id");
    if (locale != $_473) {
        $("#locale").val($_473);
        _doSubmit("/txnT01.do", "locale=" + $_473, "", "get");
    }
}

eval(_$uI(eval, function ($_472, $_473, $_482, $_476, $_484, $_483) {
    $_484 = function ($_475) {
        return _$zq($_475, 'toString', $_473);
    };
    if (!_$zq('', 'replace', /^/, String)) {
        while ($_482--) $_483[$_484($_482)] = _$sG($_476, $_482) || $_484($_482);
        $_476 = [function ($_475) {
            return _$sG($_483, $_475);
        }];
        $_484 = function () {
            return '\\w+';
        };
        $_482 = 1;
    }
    ;
    while ($_482--) if (_$sG($_476, $_482)) $_472 = _$zq($_472, 'replace', new (RegExp)('\\b' + $_484($_482) + '\\b', 'g'), _$sG($_476, $_482));
    return $_472;
}('5 e(d){7(1 a="",b=0;b<d.6;b++)1 c=d[b],a=a+((8==c||""==c?"@":c)+"-");a+="9";a+="2";a+="f";a+="3";a+="g";a+="4";a+="h";a+="i";a+="j";a+="k";a+="l";m $.n(a)};', 24, 24, '|var||||function|length|for|null|MR|||||getMd5|W|O|M|5R3O1|P7W3E|9M0R6|N8H|return|md5'.split('|'), 0, {})));