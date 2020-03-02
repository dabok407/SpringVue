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
        searchStart(0)
    });

    // 등록 모달 팝업 open
    $('#registPopupBtn').click(function () {
        // template 태그 삽입
        //$('#modalContentDiv').html($('#regist-template').html());
        // 등록 모달 팝업 show
        $('#partnerRegistModal').modal('show');
    });

    // 등록 event
    $('#registBtn').click(function () {
        registPartner();
    });

    // 등록 모달 팝업 close
    $('#registCloseModalTopBtn').click(function () {
        // regist form reset
        registFormReset();
        // regist modal hide
        $('#partnerRegistModal').modal('hide');
    });

    // 등록 모달 팝업 비밀번호 일치
    $('#reg_passwordCheck').keyup(function () {
        var pwdVal = $('#reg_password').val();
        var pwdCheckVal = $('#reg_passwordCheck').val();
        var msg = "";
        if(pwdVal === pwdCheckVal){
            msg = "일치 합니다.";
            $('#reg_equal_pwd').val("success");
        }else{
            msg = "비밀번호가 일지 하지 않습니다.";
            $('#reg_equal_pwd').val("fail");
        }
        $('#reg_pwdCompareText').text(msg);
        $('#reg_pwdCompareText').show();
    });

    // 수정 event
    $('#modifyBtn').click(function () {
        modifyPartner();
    });

    // 수정 모달 팝업 비밀번호 일치
    $('#mod_passwordCheck').keyup(function () {
        var pwdVal = $('#mod_password').val();
        var pwdCheckVal = $('#mod_passwordCheck').val();
        var msg = "";
        if(pwdVal === pwdCheckVal){
            msg = "일치 합니다.";
            $('#mod_equal_pwd').val("success");
        }else{
            msg = "비밀번호가 일지 하지 않습니다.";
            $('#mod_equal_pwd').val("fail");
        }
        $('#mod_pwdCompareText').text(msg);
        $('#mod_pwdCompareText').show();
    });

    // 삭제 event
    $('#deleteBtn').click(function () {
        if(confirm("정말 삭제 하시겠습니까?")){
            deletePartner();
        }
    });

    // 상세 모달 팝업 hide
    function closeModifyPopup() {
        $('#partnerModifyModal').modal('hide');
    }

    // 등록 모달 팝업 hide
    function closeRegistPopup() {
        $('#partnerRegistModal').modal('hide');
    }

    // 등록 모달 팝업 close
    $('#modifyCloseModalBtn, #modifyCloseModalTopBtn').click(function () {
        // modify form reset
        modifyFormReset();
        // modal popup hide
        $('#partnerModifyModal').modal('hide');
    });

    // 등록 from 값 초기화
    function registFormReset() {
        $('#registForm').find('input, select, checkbox, radio').val(null);
        $('#reg_pwdCompareText').text("");
    }

    // 수정 form 값 초기화
    function modifyFormReset() {
        $('#modifyForm').find('input, select, checkbox, radio').val(null);
    }

    
    function searchStart(index) {

        var pageSize = 10;
        var paramUrl = "";
        var partner_name = $("#partner_name").val();
        var ceo_name = $("#ceo_name").val();
        var business_number = $("#business_number").val();
        if(partner_name != "" && partner_name != null){
            paramUrl += "&name="+partner_name;
        }
        if(ceo_name != "" && ceo_name != null){
            paramUrl += "&ceo_name="+ceo_name;
        }
        if(business_number != "" && business_number != null){
            paramUrl += "&business_number="+business_number;
        }

        $.get("/api/partner?page="+index+'&size='+pageSize+paramUrl, function (response) {

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

    // 파트너 등록
    function registPartner(){

        $.ajax({
            url: "/api/partner",
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
                    // 사용자 조회
                    searchStart(0);
                }else{
                    alert(response.description);
                }
            }
        });
    }

    // 파트너 수정
    function modifyPartner(){

        $.ajax({
            url: "/api/partner",
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

    // 파트너 삭제
    function deletePartner(){
        var id = $("#mod_id").val();
        $.ajax({
            url: "/api/partner/"+id,
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
                    // 사용자 조회
                    searchStart(0);
                }else{
                    alert(response.description);
                }
            }
        });
    }

    // 파트너 상세 정보 조회
    function detailSearch(id){
        // template 태그 삽입
        $('#modalContentDiv').html($('#modify-template').html());
        // 상세 모달 팝업 show
        $('#partnerModifyModal').modal('show');
        // 상세 조회 서비스 호출
        $.ajax({
            url: "/api/partner/"+id,
            success: function (response, textStatus, jqXHR) {
                var partnerData = response.data;
                var $selector = $('#modifyForm');

                $selector.find("#mod_id").val(partnerData.id);
                $selector.find("#mod_category_id").val(partnerData.category_id);
                $selector.find("#mod_status").val(partnerData.status);
                $selector.find("#mod_name").val(partnerData.name);
                $selector.find("#mod_ceo_name").val(partnerData.ceo_name);
                $selector.find("#mod_address").val(partnerData.address);
                $selector.find("#mod_call_center").val(partnerData.call_center);
                $selector.find("#mod_partner_number").val(partnerData.partner_number);
                $selector.find("#mod_business_number").val(partnerData.business_number);
                $selector.find("#mod_registered_at").text(partnerData.registered_at);
                $selector.find("#mod_unregistered_at").text(partnerData.unregistered_at);
            }
        });
    }

})(jQuery);