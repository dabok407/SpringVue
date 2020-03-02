
var common = {};

/*
 * context path
 * {param} url
 * {return} context path + url
 */
common.getContextPath = function getContextPath() {
    var hostIndex = location.href.indexOf( location.host ) + location.host.length;
    return location.href.substring( hostIndex, location.href.indexOf('/', hostIndex + 1) );
};

/*
 * href link
 * {param} url
 * {return} context path + url => link
 */
common.href = function href(url){
    location.href = common.getContextPath()+"/"+url;
}

/*
 * form data json parse
 * {param} form Data (serialize)
 * {return} json
 */
common.serializeObject = function(formId) {
    var returnObj = null;
    var data = null;
    var $selector = $('#'+formId);
    try {
        if($selector[0].tagName && $selector[0].tagName.toUpperCase() == "FORM" ) {
            var arr = $selector.serializeArray();
            if(arr){
                returnObj = {};
                data = {};
                jQuery.each(arr, function() {
                    data[this.name] = this.value;
                });
            }
        }
    }catch(e) {
        alert(e.message);
    }finally {}
    returnObj["data"] = data;
    return returnObj;
}

/*
 * form data json parse
 * {param} form Data (serialize)
 * {return} json
 */
common.serializeObject = function(formId, objlistName, objList) {
    var returnObj = null;
    var data = null;
    var $selector = $('#'+formId);
    try {
        if($selector[0].tagName && $selector[0].tagName.toUpperCase() == "FORM" ) {
            var arr = $selector.serializeArray();
            if(arr){
                returnObj = {};
                data = {};
                jQuery.each(arr, function() {
                    data[this.name] = this.value;
                });
                data[objlistName] = objList;
            }
        }
    }catch(e) {
        alert(e.message);
    }finally {}
    returnObj["data"] = data;
    return returnObj;
}

/*
 * object empty 여부 확인
 * {param} obj
 * {return} boolean true or false
 */
common.isEmpty = function(obj){
    if (obj == null) return true;
    if (obj == "null") return true;
    if (obj.length > 0)    return false;
    if (obj.length === 0)  return true;
    if (typeof obj !== "object") return true;
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }
    return true;
};

/*
 * 1000단위 콤마
 * {param} str
 * {return} 콤마 문자열
 */
common.setComma = function (str) {
    str = str.toString();
    var pattern = /(-?\d+)(\d{3})/;
    while (pattern.test(str))
        str = str.replace(pattern, "$1,$2");
    return str;
};

/*
 * 현재날짜 반환 함수
 * 서버 쪽 시간을 가져오기 위해서 xmlHttp 객체 안에 있는 서버의 시간
 * 정보를 header에서 추출해서 사용
 * {return} 오늘 날짜
 */
common.getToday = function()
{
    var xmlHttp;
    var serverTime;
    if (window.XMLHttpRequest) {//분기하지 않으면 IE에서만 작동된다.
        xmlHttp = new XMLHttpRequest(); // IE 7.0 이상, 크롬, 파이어폭스 등
        xmlHttp.open('HEAD',window.location.href.toString(),false);
        xmlHttp.setRequestHeader("Content-Type", "text/html");
        xmlHttp.send('');
        serverTime = xmlHttp.getResponseHeader("Date");
    } else if (window.ActiveXObject) {
        xmlHttp = new ActiveXObject('Msxml2.XMLHTTP');
        xmlHttp.open('HEAD',window.location.href.toString(),false);
        xmlHttp.setRequestHeader("Content-Type", "text/html");
        xmlHttp.send('');
        serverTime = xmlHttp.getResponseHeader("Date");
    }

    var dateObj = new Date(serverTime);
    var year = dateObj.getFullYear();
    var month = dateObj.getMonth()+1;
    var day = dateObj.getDate();

    //1~9월 까지는 앞에 0 붙히기
    //이 부분 로직 formatter로 변경가능한지 찾아봐야함.
    if(month<10)
    {
        month="0"+month;
    }
    if(day<10)
    {
        day="0"+day;
    }

    var today = year+"-"+month+"-"+day;
    return today;
};
/*
 * 문자열size가 maxNum 이상일때 ...표시
 * {return} ...표시 문자열
 */
common.summaryStr = function(obj, maxNum){
    var li_str_len = obj.length;
    var li_byte = 0;
    var li_len = 0;
    var ls_one_char = "";
    var ls_str2 = "";
    for( var j=0; j<li_str_len; j++){
        ls_one_char = obj.charAt(j);
        if(escape(ls_one_char).length > 4 ) {
            li_byte += 2;
        }else{
            li_byte++;
        }
        if(li_byte <= maxNum){
            li_len = j+1;
        }
    }
    if(li_byte > maxNum){
        ls_str2 = obj.substr(0, li_len)+"...";
    }else{
        ls_str2 = obj;
    }
    return ls_str2;
};


/*
 * 브라우저 판별
 * {return} 브라우저명
 */
common.getBrowserType = function(){

    var _ua = navigator.userAgent;
    var rv = -1;
    //IE 11,10,9,8
    var trident = _ua.match(/Trident\/(\d.\d)/i);
    if( trident != null ){
        if( trident[1] == "7.0" ) return rv = "IE" + 11;
        if( trident[1] == "6.0" ) return rv = "IE" + 10;
        if( trident[1] == "5.0" ) return rv = "IE" + 9;
        if( trident[1] == "4.0" ) return rv = "IE" + 8;
    }
    //IE 7...
    if( navigator.appName == 'Microsoft Internet Explorer' ) return rv = "IE" + 7;
    //other
    var agt = _ua.toLowerCase();
    if (agt.indexOf("chrome") != -1) return 'Chrome';
    if (agt.indexOf("opera") != -1) return 'Opera';
    if (agt.indexOf("staroffice") != -1) return 'Star Office';
    if (agt.indexOf("webtv") != -1) return 'WebTV';
    if (agt.indexOf("beonex") != -1) return 'Beonex';
    if (agt.indexOf("chimera") != -1) return 'Chimera';
    if (agt.indexOf("netpositive") != -1) return 'NetPositive';
    if (agt.indexOf("phoenix") != -1) return 'Phoenix';
    if (agt.indexOf("firefox") != -1) return 'Firefox';
    if (agt.indexOf("safari") != -1) return 'Safari';
    if (agt.indexOf("skipstone") != -1) return 'SkipStone';
    if (agt.indexOf("netscape") != -1) return 'Netscape';
    if (agt.indexOf("mozilla/5.0") != -1) return 'Mozilla';
};



/*
 * 문자열 교체
 * {return} 변경된 문자열
 */
common.replaceAll = function replaceAll(str, searchStr, replaceStr) {
    return str.split(searchStr).join(replaceStr);
}


common.autocomplete = function autocomplete(element, url, option) {
    element = '#' + element;

    var key = '';
    option = $.extend({
        minLength : 2,
        dataListId : 0,
        itemExtractor : function(row) {
            return {
                value : row.VALUE,
                label : row.LABEL
            };
        },
        filter : function(row) {
            return true;
        },
        focus: function(event, ui) {
            return false;
        },
        open: function(event, ui) {
        },
        select: function(event, ui) {
        }
    }, option || {});

    option = $.extend({
        source: function(request, response) {
            $.ajax({
                type:"GET",
                url: url,
                dataType:"json",
                success: function(data) {
                    response(
                        $.map(data.data, function(row) {
                            if(option.filter(row) !== true) {
                                return null;
                            } else {
                                var item = option.itemExtractor(row);
                                item.label = item.label
                                    .replace(/\>/g, '&gt;')
                                    .replace(/\</g, '&lt;')
                                ;
                                return item;
                            }
                        })
                    );
                },
                error: function (xhr, ajaxOptions, thrownError) {
                    alert("오류가 발생 되었습니다.");
                }
            });

            /*uxl.callFunction(url , {  data:option.data
                , loading:false
                , success:function(result) {
                    var dataList = result.getDataList(option.dataListId) || {};
                    response($.map(dataList.rows || {}
                        , function(row) {
                            if(option.filter(row) !== true) {
                                return null;
                            } else {
                                var item = option.itemExtractor(row);
                                item.label = item.label
                                    .replace(/\>/g, '&gt;')
                                    .replace(/\</g, '&lt;')
                                ;
                                return item;
                            }
                        })
                    );
                }
                , async:true
            });*/

        }
    }, option);

    var open = option.open;
    option.open = function(event, ui) {
        open(event, ui);
    };

    var el = $(element).autocomplete(option);

    el.autocomplete._renderItem = function(ul, item) {
        console.log(ul);
        console.log(item);
        return $('<li></li>').data('item.autocomplete', item ).append('<a><label style="white-space: nowrap;">' + item.label + '</label></a>').appendTo(ul);
    };
};
