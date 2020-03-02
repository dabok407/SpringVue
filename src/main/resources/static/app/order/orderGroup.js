(function ($) {

    var maxBtnSize = 7;              // 검색 하단 최대 범위
    var indexBtn = [];               // 인덱스 버튼

    // 페이징 처리 데이터
    var pagination = {
        total_pages : 0,            // 전체 페이지수
        total_elements : 0,         // 전체 데이터수
        current_page :  0,          // 현재 페이지수
        current_elements : 0        // 현재 데이터수
    };


    // 페이지 정보
    var showPage = new Vue({
        el : '#showPage',
        data : {
            totalElements : {},
            currentPage:{}
        }
    });

    // 데이터 리스트
    var itemList = new Vue({
        el : '#itemList',
        data : {
            itemList : {}
        },
        methods: {
            click: function (id) {
                // 상세 정보 조회
                detailSearch(id);
            }
        }
    });


    // 페이지 버튼 리스트
    var pageBtnList = new Vue({
        el : '#pageBtn',
        data : {
            btnList : {}
        },
        methods: {
            indexClick: function (id) {
                searchStart(id-1)
            },
            previousClick:function () {
                if(pagination.current_page !== 0){
                    searchStart(pagination.current_page-1);
                }
            },
            nextClick:function () {
                if(pagination.current_page !== pagination.total_pages-1){
                    searchStart(pagination.current_page+1);
                }
            }
        },
        mounted:function () {
            // 제일 처음 랜더링 후 색상 처리
            setTimeout(function () {
                $('li[btn_id]').removeClass( "active" );
                $('li[btn_id='+(pagination.current_page+1)+']').addClass( "active" );
            },50)
        }
    });


    $('#search').click(function () {
        searchStart(0)
    });

    $(document).ready(function () {
        searchStart(0, "Y")
    });

    $('#itemRegistPopupBtn, #itemModifyPopupBtn').click(function () {
        window.open("/pages/itemRegistPopup", "itemPopup","width=1300,height=690");
    });

    // 등록 모달 팝업 open
    $('#registPopupBtn').click(function () {
        // template 태그 삽입
        //$('#modalContentDiv').html($('#regist-template').html());
        // 등록 모달 팝업 show
        $('#orderRegistModal').modal('show');
    });

    // 등록 event
    $('#registBtn').click(function () {
        registOrder();
    });

    // 등록 모달 팝업 close
    $('#registCloseModalTopBtn').click(function () {
        // regist form reset
        registFormReset();
        // regist modal hide
        $('#orderRegistModal').modal('hide');
    });

    // 수정 event
    $('#modifyBtn').click(function () {
        modifyOrder();
    });

    // 삭제 event
    $('#deleteBtn').click(function () {
        if(confirm("정말 삭제 하시겠습니까?")){
            deleteOrder();
        }
    });

    // 등록 모달팝업 상품 삭제
    $('#itemRegistDeleteBtn').click(function(){
        var $table = $("#registFormItemTable");

        $table.find("input[name=checkItem]:checked").each(function(index, item) {
            $(item).parent().parent().remove();
        });
    });

    // 수정 모달팝업 상품 삭제
    $('#itemModifyDeleteBtn').click(function(){
        var $table = $("#modifyFormItemTable");

        $table.find("input[name=checkItem]:checked").each(function(index, item) {
            $(item).parent().parent().remove();
        });
    });

    // 상세 모달 팝업 hide
    function closeModifyPopup() {
        $('#orderModifyModal').modal('hide');
        /*$('#adminModifyModal').remove();*/
    }

    // 등록 모달 팝업 hide
    function closeRegistPopup() {
        $('#orderRegistModal').modal('hide');
        /*$('#adminRegistModal').remove();*/
    }

    // 수정 모달 팝업 close
    $('#modifyCloseModalBtn, #modifyCloseModalTopBtn').click(function () {
        // modify form reset
        modifyFormReset();
        // modal popup hide
        $('#orderModifyModal').modal('hide');

    });

    // 등록 from 값 초기화
    function registFormReset() {
        $('#registForm').find('input, select, checkbox, radio').val(null);
        $("#registForm").find("#registFormItemTable > tbody > tr").remove()
    }

    // 수정 form 값 초기화
    function modifyFormReset() {
        $('#modifyForm').find('input, select, checkbox, radio').val(null);
        $("#modifyForm").find("#modifyFormItemTable > tbody > tr").remove()
    }

    function searchStart(index, initialYn) {

        var pageSize = 10;
        var paramUrl = "";
        var account = $("#user_account").val();
        var status = $("#status").val();
        var revName = $("#revName").val();
        var paymentType = $("#paymentType").val();
        if(account != "" && account != null){
            paramUrl += "&user.account="+account;
        }
        if(status != "" && status != null){
            paramUrl += "&status="+status;
        }
        if(revName != "" && revName != null){
            paramUrl += "&revName="+revName;
        }
        if(paymentType != "" && paymentType != null){
            paramUrl += "&paymentType="+paymentType;
        }
        if(initialYn != "" && initialYn != null){
            paramUrl += "&initialYn=Y";
        }else{
            paramUrl += "&initialYn=''";
        }

        $.get("/api/orderGroup?page="+index+'&size='+pageSize+paramUrl, function (response) {

            /* 데이터 셋팅 */
            // 페이징 처리 데이터
            indexBtn = [];
            pagination = response.pagination;


            //전체 페이지
            showPage.totalElements = pagination.current_elements;
            showPage.currentPage = pagination.current_page+1;


            // 검색 데이터
            itemList.itemList = response.data;


            // 이전버튼
            if(pagination.current_page === 0){
                $('#previousBtn').addClass("disabled")
            }else{
                $('#previousBtn').removeClass("disabled")
            }


            // 다음버튼
            if(pagination.current_page === pagination.total_pages-1){
                $('#nextBtn').addClass("disabled")
            }else{
                $('#nextBtn').removeClass("disabled")
            }

            // 페이징 버튼 처리
            var temp = Math.floor(pagination.current_page / maxBtnSize);
            for(var i = 1; i <= maxBtnSize; i++){
                var value = i+(temp*maxBtnSize);

                if(value <= pagination.total_pages){
                    indexBtn.push(value)
                }
            }

            // 페이지 버튼 셋팅
            pageBtnList.btnList = indexBtn;


            // 색상처리
            setTimeout(function () {
                $('li[btn_id]').removeClass( "active" );
                $('li[btn_id='+(pagination.current_page+1)+']').addClass( "active" );
            },50)
        });
    }

    // 주문 등록
    function registOrder(){

        var itemData = null;
        var $itemTableList = $("#registFormItemTable").find("tbody tr");
        var itemDataArryay = new Array();
        $itemTableList.each(function(index, item) {
            var itemObj = new Object();
            var itemId = $(item).find("input[name=itemId]").val();
            var itemCnt = $(item).find("span[name=itemCnt]").text();
            var itemTotalPrice = $(item).find("span[name=itemTotalPrice]").text();
            itemObj["item_id"] = itemId;
            itemObj["status"] = "ORDERING";
            itemObj["quantity"] = itemCnt;
            itemObj["total_price"] = common.replaceAll(itemTotalPrice, ",", "");
            itemDataArryay.push(itemObj);
        });

        /*itemData = JSON.stringify(itemDataArryay);*/
        itemData = itemDataArryay;
        var groupData = JSON.stringify(common.serializeObject("registForm", 'order_detail_api_request_list', itemData));
            /*.replace(/\\/g,'');*/

        $.ajax({
            url: "/api/orderGroup",
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            data: groupData,
            dataType: 'json',
            async: true,
            beforeSend : function(xhr) {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                xhr.setRequestHeader(header, token);
            },
            success: function (response, textStatus, jqXHR) {
                var resultCode = response.result_code;
                if(resultCode == "OK"){
                    alert("등록 되었습니다.");
                    // form 초기화
                    registFormReset();
                    // 팝업 close
                    closeRegistPopup();
                    // 주문 조회
                    searchStart(0);
                }else{
                    alert(response.description);
                }
            }
        });
    }

    // 주문 수정
    function modifyOrder(){

        var itemData = null;
        var $itemTableList = $("#modifyFormItemTable").find("tbody tr");
        var itemDataArryay = new Array();
        $itemTableList.each(function(index, item) {
            var itemObj = new Object();
            var id = $(item).find("input[name=id]").val();
            var itemId = $(item).find("input[name=itemId]").val();
            var itemCnt = $(item).find("span[name=itemCnt]").text();
            var itemTotalPrice = $(item).find("span[name=itemTotalPrice]").text();
            itemObj["group_detail_id"] = id;
            itemObj["item_id"] = itemId;
            itemObj["status"] = "ORDERING";
            itemObj["quantity"] = itemCnt;
            itemObj["total_price"] = common.replaceAll(itemTotalPrice, ",", "");
            itemDataArryay.push(itemObj);
        });

        /*itemData = JSON.stringify(itemDataArryay);*/
        itemData = itemDataArryay;
        var groupData = JSON.stringify(common.serializeObject("modifyForm", 'order_detail_api_request_list', itemData));

        $.ajax({
            url: "/api/orderGroup",
            type: 'PUT',
            contentType: 'application/json; charset=utf-8',
            /*data: JSON.stringify($('#registForm').serialize()),*/
            data: groupData,
            dataType: 'json',
            async: true,
            beforeSend : function(xhr) {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                xhr.setRequestHeader(header, token);
            },
            success: function (response, textStatus, jqXHR) {
                var resultCode = response.result_code;
                if(resultCode == "OK"){
                    alert("수정 되었습니다.");
                    // 수정 폼 초기화
                    modifyFormReset();
                    // 팝업 close
                    closeModifyPopup();
                    // 주문 조회
                    searchStart(0);
                }
            }
        });
    }

    // 주문 삭제
    function deleteOrder(){
        var id = $("#mod_group_id").val();
        $.ajax({
            url: "/api/orderGroup/"+id,
            type: 'DELETE',
            beforeSend : function(xhr) {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                xhr.setRequestHeader(header, token);
            },
            success: function (response, textStatus, jqXHR) {
                var resultCode = response.result_code;
                if(resultCode == "OK"){
                    alert("삭제 되었습니다.");
                    // 팝업 close
                    closeModifyPopup();
                    // 주문 조회
                    searchStart(0);
                }else{
                    alert(response.description);
                }
            }
        });
    }

    // 주문 상세 정보 조회
    function detailSearch(id){
        // template 태그 삽입
        $('#modalContentDiv').html($('#modify-template').html());
        // 상세 모달 팝업 show
        $('#orderModifyModal').modal('show');
        // 상세 조회 서비스 호출
        $.ajax({
            url: "/api/orderGroup/"+id,
            success: function (response, textStatus, jqXHR) {
                var orderData = response.data;
                var $selector = $('#modifyForm');

                $selector.find("#mod_group_id").val(orderData.id);
                $selector.find("#mod_user_account").val(orderData.user_account);
                $selector.find("#mod_status").val(orderData.status);
                $selector.find("#mod_order_type").val(orderData.order_type);
                $selector.find("#mod_payment_type").val(orderData.payment_type);
                $selector.find("#mod_total_price").val(orderData.total_price);
                $selector.find("#mod_total_price_text").text(orderData.total_price);
                $selector.find("#mod_total_quantity").val(orderData.total_quantity);
                $selector.find("#mod_rev_name").val(orderData.rev_name);
                $selector.find("#mod_rev_address").val(orderData.rev_address);

                fnOrderDetailAdd(orderData.order_detail_list, $('#modifyForm').find("#modifyFormItemTable > tbody"));

                /*new Vue({
                    el : '#adminModifyTableDiv',
                    data :adminUserData
                });*/
            }
        });
    }


    $("#mod_user_account").keyup(function(){
        var el =
        $("#mod_user_account").autocomplete({
            source : function(request, response){
                $.ajax({
                    type:"GET",
                    url: "/api/user/like/"+$("#mod_user_account").val(),
                    dataType:"json",
                    success: function(data) {
                        response(
                            $.map(data.data, function(item) {
                                return {
                                    label: (item.account +"("+item.email+")"),
                                    id : item.id,
                                    account : item.account,
                                    email : item.email
                                }
                            })
                        );
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert("오류가 발생 되었습니다.");
                    }
                });
            },
            focus: function( event, ui ) {
                return false;
            },
            classes: {
                "ui-autocomplete": "highlight"
            },
            minLength: 1,
            select: function(event, ui) {
                var userId = ui.item.id;
                var account = ui.item.account;
                $("#mod_user_account").val(account);
                $("#mod_user_id").val(userId);
            }
        });
        el.data( "ui-autocomplete" )._renderItem = function( ul, item ) {
            $(ul).css("z-index","99999");
            $(ul).css("max-height","500px");
            $(ul).css("overflow-y","auto");
            $(ul).css("overflow-x","hidden");
            return $( "<li>" )
                .data( "ui-autocomplete-item", item )
                .append( "<a>" + item.label + "</a>" )
                .appendTo( ul );
        };
    });

})(jQuery);

// 등록 목록 전체 선택, 해제
$("#registFormItemTable").find("#allCheck").click(function(){
    if($("#allCheck").prop("checked")) {
        $("#registFormItemTable").find("input[type=checkbox]").prop("checked",true);
    } else {
        $("#registFormItemTable").find("input[type=checkbox]").prop("checked",false);
    }
})

// 상세 목록 전체 선택, 해제
$("#modifyFormItemTable").find("#modAllCheck").click(function(){
    if($("#modAllCheck").prop("checked")) {
        $("#modifyFormItemTable").find("input[type=checkbox]").prop("checked",true);
    } else {
        $("#modifyFormItemTable").find("input[type=checkbox]").prop("checked",false);
    }
})

// 상품 선택 callback
function fnPopupCallback(obj){
    var $selector = $("#registFormItemTable").find("tbody");
    fnOrderDetailAdd(obj, $selector);
    var $selector = $("#modifyFormItemTable").find("tbody");
    fnOrderDetailAdd(obj, $selector);
}

// 주문 상세 상품 목록 add
function  fnOrderDetailAdd(obj, $selector) {
    for(var i=0; i<obj.length; i++){
        var html = '<tr role="row" class="odd">'
            +'<td class="text-center">'
            +'<input type="checkbox" name="checkItem" id="" style="width:18px;height:18px;">'
            +'</td>'
            +'<td class="text-center">'
            +obj[i].name
            +'<div style="display: none;">'
            +'<input name="group_detail_id" value="'+obj[i].id+'">'
            +'<input name="itemId" value="'+obj[i].item_id+'">'
            +'<input name="itemOriginPrice" value="'+common.setComma(obj[i].total_price)+'">'
            +'</div>'
            +'</td>'
            +'<td class="text-center">ORDERING</td>'
            +'<td class="text-right">'
            +'<span name="itemCnt" style="padding-right:5px;">'+obj[i].quantity+'</span>'
            +'&nbsp;&nbsp;'
            +'<button type="button" class="btn btn-default inner-btn" onclick="fnPlusItem(this)">+</button>'
            +'&nbsp;'
            +'<button type="button" class="btn btn-default inner-btn" onclick="fnMinusItem(this)">-</button>'
            +'</td>'
            +'<td class="text-right">'
            +'<span name="itemTotalPrice">'+common.setComma(obj[i].total_price)+'</span>'
            +'</td>'
            +'<td class="text-center">'
            +'<button type="button" class="btn btn-warning inner-btn" onclick="fnDeleteItem(this)">삭제</button>'
            +'</td>'
            +'</tr>'
        $selector.append(html);
    }
}

// 상품 삭제
function fnDeleteItem(selector){
    var $selector = $(selector);
    $selector.parent().parent().remove();
    fnCalculateTotalData($(selector).parents("table[id$=ItemTable]"));
}

// 상품 수량 plus
function fnPlusItem(selector){
    var $selector = $(selector);
    var $dtSelector = $selector.parent().parent().find("td");
    var maximumSize = 999;
    var itemCnt = parseInt($dtSelector.eq(3).find("span").text());
    var itemOriginPrice = parseInt(common.replaceAll($dtSelector.eq(1).find("input[name=itemOriginPrice]").val(), ",", ""));
    var itemNowPrice = parseInt(common.replaceAll($dtSelector.eq(3).find("span").text(), ",", ""));
    if(itemCnt > maximumSize){
        alert("수량 최대치");
        return;
    }else{
        var cnt = itemCnt+1;
        var price = itemOriginPrice * cnt;
        $dtSelector.eq(3).find("span").text(cnt);
        $dtSelector.eq(4).find("span").text(common.setComma(price));
        fnCalculateTotalData($(selector).parents("table[id$=ItemTable]"));
    }
}

// 상품 수량 minus
function fnMinusItem(selector){
    var $selector = $(selector);
    var $dtSelector = $selector.parent().parent().find("td");
    var minimumSize = 2;
    var itemCnt = parseInt($dtSelector.eq(3).find("span").text());
    var itemOriginPrice = parseInt(common.replaceAll($dtSelector.eq(1).find("input[name=itemOriginPrice]").val(), ",", ""));
    var itemNowPrice = parseInt(common.replaceAll($dtSelector.eq(4).find("span").text(), ",", ""));
    if(itemCnt < minimumSize){
        alert("수량 최소치");
        return;
    }else{
        var cnt = itemCnt-1;
        var price = itemOriginPrice * cnt;
        $dtSelector.eq(3).find("span").text(cnt);
        $dtSelector.eq(4).find("span").text(common.setComma(price));
        fnCalculateTotalData($(selector).parents("table[id$=ItemTable]"));
    }
}

// 상품 총 수량 및 금액 계산 함수
function fnCalculateTotalData(selector){
    var $itemTableList = $(selector).find("tbody tr");
    var totalCnt = 0;
    var totalPrice = 0;
    $itemTableList.each(function(index, item) {
        var itemCnt = $(item).find("span[name=itemCnt]").text();
        var itemTotalPrice = common.replaceAll($(item).find("span[name=itemTotalPrice]").text(), ",", "") ;
        totalCnt += parseInt(itemCnt);
        totalPrice += parseInt(itemTotalPrice);
    });
    selector.parents("table").find("input[id$='total_quantity']").val(totalCnt);
    selector.parents("table").find("input[id$='total_price']").val(totalPrice);
    selector.parents("table").find("span[id$='total_price_text']").text(common.setComma(totalPrice));
}