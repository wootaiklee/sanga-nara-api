package com.gsntalk.api.util;

public enum GsntalkAPIResponse {

	SUCCESS										( "0000" )		// SUCCESS
	
	,MISSING_REQUIRED_PARAMETER					( "1000" )		// 필수 요청파라메터 누락
	,INVALID_EMAIL_FORMAT						( "1001" )		// 잘못된 이메일 형식
	,EMAIL_ALREADY_USED							( "1002" )		// 이미 사용중인 이메일
	,INVALID_MOB_NO_FORMAT						( "1003" )		// 잘못된 휴대폰번호 형식
	,INVALID_MOB_VRF_NO_FORMAT					( "1004" )		// 잘못된 인증번호 형식
	,FAIL_TO_MOB_NO_VERIFICATION				( "1005" )		// 휴대폰 본인인증 실패
	,EXPIRED_MOB_VRF_NO							( "1006" )		// 휴대폰 본인인증 인증번호 만료
	,INVALID_MOB_VRF_CNFRM_TOKEN				( "1007" )		// 휴대폰 본인인증 검증확인토큰 불일치
	,INVALID_VALUE_OF_PARAMETER					( "1008" )		// 잘못된 파라메터 값
	,MISSING_REQUIRED_ATTACHMENT				( "1009" )		// 필수 첨부파일 누락
	,CANNOT_ACCEPT_FILE_FORMAT					( "1010" )		// 허용되지 않은 파일 포맷
	,OVER_CNT_ATTACHMENT_SIZE					( "1011" )		// 첨부가능한 최대 파일수량 초과
	,INVALID_PWD_FORMAT							( "1012" )		// 잘못된 비밀번호 형식
	,NOT_MATCHED_USER_EMAIL_AND_MOBN_NO			( "1013" )		// 이메일과 휴대폰번호가 일치하는 계정이 없음
	,LOGIN_FAIL									( "1014" )		// 로그인 실패 ( 이메일 또는 비밀번호 불일치 )
	,AUTO_LOGIN_FAIL							( "1015" )		// 로그인 실패 ( 자동로그인 토큰 검증 실패 )
	,NOT_APPROVAL_ACCOUNT						( "1016" )		// 관리자에 의해 아직 가입 승인이 되지 않은 계정
	,INVALID_DATE_FORMAT						( "1017" )		// 잘못된 날짜형식
	,INVALID_BIZ_REG_NO_FORMAT					( "1018" )		// 잘못된 사업자번호 ( 10차리가 아님 )
	,CANNOT_CONVERT_GEOCODE_ADDRESS				( "1019" )		// 입력한 주소로 위치정보를 확인할 수 없음
	,INVALID_TARGET								( "1020" )		// 유효하지 않은 대상
	,PASSWORD_MISMATCH 							( "1021" )		// 비밀번호 불일치 ( 패스워드 변경 시 )
	,INVALID_TEL_NO_FORMAT						( "1022" )		// 잘못된 전화번호 형식
	,INVALID_MONTH_FORMAT						( "1023" )		// 잘못된 월 형식
	,DUPLICATED_DATA							( "1024" )		// 중복된 데이터
	,INVALID_ST_ED_DATE_VALUES					( "1025" )		// 시작일자가 종료일자보다 큼
	,DATA_TO0_LONG_FOR_PARAMETER				( "1026" )		// 파라미터 길이가 너무 길때
	,OVER_CNT_ITEMS_SIZE						( "1027" )		// 등록가능한 수량 초과
	,BOTH_SAME_VALUES							( "1028" )		// 두 값이 서로 동일함
	,ALREADY_REQUESTED_TOUR						( "1029" )		// 이미 신청한 투어
	,MODIFICATION_DATE_HAS_PASSED 				( "1030" ) 		// 수정 가능 한 일자가 지남
	,NOT_VALID_DATE 							( "1031" ) 		// 유효한 날짜가 아님
	,OVER_MAX_VALUE 							( "1032" ) 		// 허용 가능한 값 초과

	,NEED_TO_LOGIN								( "2000" )		// 미 로그인 ( 또는 JWT 토큰 누락 )
	,FAIL_JWT_VALIDATION						( "2001" )		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
	,EXPIRED_JWT_TOKEN							( "2002" )		// JWT 토큰 만료
	,NOT_YET_SEND_MOB_VRF_NO					( "2003" )		// 휴대폰 본인인증 연속발송 제한
	,MISSING_USERAGENT							( "2004" )		// User-Agent를 확인할 수 없음
	
	,NOT_AGREE_TO_AGE_14_OVER					( "3000" )		// 만14세이상 미동의
	,NOT_AGREE_TO_SERVICE_USE					( "3001" )		// 서비스이용약관 미동의
	,NOT_AGREE_TO_PERSONAL_INFO					( "3002" )		// 개인정보수집및이용 미동의
	,NOT_VERIFIED_SNS							( "3003" )		// SNS 로그인 미인증
	,HAS_NO_PERMISSION_TO_PROCESS				( "3004" )		// 처리할 권한이 없음
	,IS_NOT_ESTATE_BROKER_USER					( "3005" )		// 중개회원이 아님
	,IS_NOT_NORMAL_USER							( "3006" )		// 일반회원이 아님
	,IS_NOT_ADMIN_USER							( "3007" )		// 관리자회원이 아님
	,HAS_NO_PERMISSION_TO_SEARCH				( "3008" )		// 조회할 권한이 없음
	,IS_NOT_ACTIVE_MEMBER						( "3009" ) 		// 활동가능 회원이 아님
	
	,NOT_AVAIL_PROPERTY							( "4000" )		// 유효하지 않은 매물
	,NOT_FOUND_TEMP_DATA_STEP_1					( "4001" )		// 1단계 임시등록 데이터를 찾을 수 없음
	,NOT_FOUND_TEMP_DATE_PREV_STEP				( "4002" )		// 이전 단계 임시등록 데이터를 찾을 수 없음
	,NOT_FOUND_KNWLDG_IND_CMPLX					( "4003" )		// 유효하지 않은 지식산업센터
	,NOT_FOUND_SUGGSTN_SALES_PROPERTY			( "4004" )		// 유효하지 않은 추천분양
	,NOT_FOUND_ESTATE_BROKER_OFFICE				( "4005" )		// 유효하지 않은 중개사
	,NOT_FOUND_PROPERTY_SUGGEST					( "4006" )		// 유효하지 않은 매물제안
	,NOT_FOUND_PROPERTY_REQ_SUGGESTER			( "4007" )		// 매물 제안자 정보를 찾을 수 없음
	
	,CANNOT_FOUND_LOCATION_ADDRESS				( "5000" )		// 위치정보를 확인할 수 없는 주소

	,CANNOT_LIKE_MY_POST						( "6000" ) 		// 내 게시물에 '좋아요'를 표시할 수 없습니다.
	
	,FAIL_TO_SNS_LOGIN							( "8000" )		// SNS 로그인 연동 실패
	,FAIL_TO_BIZ_VALIDATION						( "8001" )		// 사업자 진위여부 불일치
	,NOT_FOUND_SELECTED_EST_BRK_OFC				( "8002" )		// 선택한 중개사무소 정보 확인불가
	,CANNOT_CHANGE_PWD_SNS_USER					( "8003" )		// SNS가입회원은 비밀번호를 변경할 수 없음
	,NOT_FOUND_SNS_MEMBER						( "8004" )		// SNS회원 계정이 존재하지 않음
	,ALREADY_DELETED_OR_NO_PERM_PROPERTY		( "8005" )		// 이미 삭제되었거나 삭제할 권한이 없는 매물
	,NOT_FOUND_PROPERTY							( "8006" )		// 매물정보를 찾을 수 없음
	,ALREADY_EXPIRED_PROPERTY					( "8007" )		// 만료된 매물
	,IS_NOT_EXPIRED_PROPERTY					( "8008" )		// 아직 만료되지 않은 매물
	,ALREADY_FIN_PROPERTY						( "8009" )		// 이미 거래가 완료된 매물
	,IS_NOT_CONFIRMED_PROPERTY					( "8010" )		// 아직 승인되지 않은 매물
	,ALREADY_USED_IN_COMP_NM					( "8011" )		// 이미 등록된 기업명
	,CANNOT_REQUEST_TOUR_MY_PRPT				( "8012" )		// 본인의 매물에는 투어요청을 할 수 없음
	
	,SERVER_ERROR								( "9999" )		// SERVER_ERROR
	;
	
	private String resCode;
	
	private GsntalkAPIResponse( String resCode ) {
		this.resCode = resCode;
	}

	public String getResCode() {
		return resCode;
	}
}