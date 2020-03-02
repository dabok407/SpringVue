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

    // 등록 모달 팝업 open
    $('#registPopupBtn').click(function () {
        // template 태그 삽입
        //$('#modalContentDiv').html($('#regist-template').html());
        // 등록 모달 팝업 show
        $('#itemRegistModal').modal('show');
    });

    // 등록 event
    $('#registBtn').click(function () {
        registItem();
    });

    // 등록 모달 팝업 close
    $('#registCloseModalTopBtn').click(function () {
        // regist form reset
        registFormReset();
        // regist modal hide
        $('#itemRegistModal').modal('hide');
    });

    // 수정 event
    $('#modifyBtn').click(function () {
        modifyItem();
    });

    // 삭제 event
    $('#deleteBtn').click(function () {
        if(confirm("정말 삭제 하시겠습니까?")){
            deleteItem();
        }
    });

    // 상세 모달 팝업 hide
    function closeModifyPopup() {
        $('#itemModifyModal').modal('hide');
    }

    // 등록 모달 팝업 hide
    function closeRegistPopup() {
        $('#itemRegistModal').modal('hide');
    }

    // 등록 모달 팝업 close
    $('#modifyCloseModalBtn').click(function () {
        // modify form reset
        modifyFormReset();
        // modal popup hide
        $('#itemModifyModal').modal('hide');
    });

    // 등록 from 값 초기화
    function registFormReset() {
        $('#registForm').find('input, select, checkbox, radio').val(null);
        $('#reg_pwdCompareText').text("");
    }

    // 수정 form 값 초기화
    function modifyFormReset() {
        $('#modifyForm').find('input, select, checkbox, radio').val(null);
        $("#mod_pwdCompareText").text("");
    }

    function searchStart(index, initialYn) {

        var pageSize = 10;
        var paramUrl = "";
        var account = $("#account").val();
        var status = $("#status").val();
        var brandName = $("#brandName").val();
        var partnerName = $("#partnerName").val();
        if(account != "" && account != null){
            paramUrl += "&account="+account;
        }
        if(status != "" && status != null){
            paramUrl += "&status="+status;
        }
        if(brandName != "" && brandName != null){
            paramUrl += "&brandName="+brandName;
        }
        if(partnerName != "" && partnerName != null){
            paramUrl += "&partner.name="+partnerName;
        }
        if(initialYn != "" && initialYn != null){
            paramUrl += "&initialYn=Y";
        }else{
            paramUrl += "&initialYn=''";
        }

        $.get("/api/item?page="+index+'&size='+pageSize+paramUrl, function (response) {

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

            if(response.init_data != null){
                // 파트너 코드 set
                if(typeof response.init_data.partnerAllList != "undefined"){
                    var partnerList = response.init_data.partnerAllList;
                    var $regSelector = $("#registForm").find("#reg_partner_id");
                    var $modSelector = $("#modifyForm").find("#mod_partner_id");

                    $.each(partnerList, function(key, value) {
                        var id = value.id;
                        var name = value.name;
                        var html = "<option value="+id+">"+name+"</option>";
                        $regSelector.append(html);
                        $modSelector.append(html);
                    });
                }
            }

        });
    }

    // 상품 등록
    function registItem(){

        $.ajax({
            url: "/api/item",
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            /*data: JSON.stringify($('#registForm').serialize()),*/
            data: JSON.stringify(common.serializeObject("registForm")),
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
                    // 상품 조회
                    searchStart(0);
                }else{
                    alert(response.description);
                }
            }
        });
    }

    // 고객 수정
    function modifyItem(){

        $.ajax({
            url: "/api/item",
            type: 'PUT',
            contentType: 'application/json; charset=utf-8',
            /*data: JSON.stringify($('#registForm').serialize()),*/
            data: JSON.stringify(common.serializeObject("modifyForm")),
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
                    // 팝업 close
                    closeModifyPopup();
                    // 사용자 조회
                    searchStart(0);
                }
            }
        });
    }

    // 상품 삭제
    function deleteItem(){
        var id = $("#mod_id").val();
        $.ajax({
            url: "/api/item/"+id,
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
                    // 상품 조회
                    searchStart(0);
                }else{
                    alert(response.description);
                }
            }
        });
    }

    // 상품 상세 정보 조회
    function detailSearch(id){
        // template 태그 삽입
        $('#modalContentDiv').html($('#modify-template').html());
        // 상세 모달 팝업 show
        $('#itemModifyModal').modal('show');
        // 상세 조회 서비스 호출
        $.ajax({
            url: "/api/item/"+id,
            success: function (response, textStatus, jqXHR) {
                var itemData = response.data;
                var $selector = $('#modifyForm');
                // 수정폼 데이터 초기화
                modifyFormReset();

                $selector.find("#mod_id").val(itemData.id);
                $selector.find("#mod_name").val(itemData.name);
                $selector.find("#mod_status").val(itemData.status);
                $selector.find("#mod_title").val(itemData.title);
                $selector.find("#mod_content").val(itemData.content);
                $selector.find("#mod_price").val(common.replaceAll(itemData.price, "," ,""));
                $selector.find("#mod_brand_name").val(itemData.brand_name);
                $selector.find("#mod_partner_name").val(itemData.partner.name);
                $selector.find("#mod_partner_id").val(itemData.partner_id);
                $selector.find("#mod_registered_at").text(itemData.registered_at);
                $selector.find("#mod_unregistered_at").text(itemData.unregistered_at);

                /*new Vue({
                    el : '#adminModifyTableDiv',
                    data :adminUserData
                });*/
            }
        });
    }
    

})(jQuery);