package com.gsntalk.api.config;

public class GsntalkConstants {

	public static boolean TEST_MODE										= true;
	
	public static final String YES										= "Y";
	public static final String NO										= "N";
	
	public static final int COUNT_PER_PAGE_DEFAULT						= 50;
	
	public static final String GSN_TALK_TOKEN_KEY						= "gsntalk-token";
	public static final String GSN_TALK_LOGIN_TOKEN						= "GSN_TALK_LOGIN_TOKEN";
	
	public static final String MEM_TYP_CD_ADMIN_USER					= "A";								// 관리자
	public static final String MEM_TYP_CD_ESTATE_AGENT					= "E";								// 중개회원
	public static final String MEM_TYP_CD_NORMAL_USER					= "N";								// 일반회원
	
	public static final String SNS_GB_KAKAO								= "K";
	public static final String SNS_GB_NAVER								= "N";
	
	public static final String SNS_LOGIN_CALL_TYPE_SIGN_UP				= "SIGN_UP";
	public static final String SNS_LOGIN_CALL_TYPE_SIGN_IN				= "SIGN_IN";
	
	public static final String ATTCH_FILE_BIZ_REG_IMG					= "BIZ_REG_IMG";					// 사업자등록증 이미지파일
	public static final String ATTCH_FILE_EST_REG_IMG					= "EST_REG_IMG";					// 중개등록증 이미지파일
	
	public static final String KAKAO_MESSAGE_TEMPLATE_MOB_NO_VRF		= "TMPLT_001";						// 휴대폰본인인증 인증번호 전송
	public static final String KAKAO_MESSAGE_TEMPLATE_EST_BRK_CNFRM		= "TMPLT_002";						// 중개회원 가입승인 안내
	public static final String KAKAO_MESSAGE_TEMPLATE_EST_BRK_DENY		= "TMPLT_003";						// 중개회원 가입거부 안내
	
	public static final String LOGIN_HIST_GB_EMAIL_LOGIN				= "EMAIL_S";						// 이메일로 로그인
	public static final String LOGIN_HIST_GB_EMAIL_AUTO_LOGIN			= "EMAIL_A";						// 이메일로 자동 로그인
	public static final String LOGIN_HIST_GB_EMAIL_LOGIN_FAIL			= "EMAIL_F";						// 이메일로 로그인 실패
	public static final String LOGIN_HIST_GB_SNS_LOGIN					= "SNS";							// SNS 로그인
	
	public static final String REG_CLAS_CD_ADM_PRPT						= "ADM_PRPT";						// 관리자-매물등록
	public static final String REG_CLAS_CD_ADM_SUGGST					= "ADM_SUGGST";						// 관리자-추천분양등록
	public static final String REG_CLAS_CD_ESTBRK_PRPT					= "ESTBRK_PRPT";					// 중개회원-매물등록
	public static final String REG_CLAS_CD_ESTBRK_TRAN_PRPSL			= "ESTBRK_TRAN_PRPSL";				// 중개회원-기업이전제안서
	public static final String REG_CLAS_CD_ESTBRK_PRPSL					= "ESTBRK_PRPSL";					// 중개회원-매물제안서
	public static final String REG_CLAS_CD_MY_PRPT						= "MY_PRPT";						// 일반회원-매물등록
	public static final String REG_CLAS_CD_MY_ASST						= "MY_ASST";						// 일반회원-자산등록
	public static final String REG_CLAS_CD_MY_PRPSL						= "MY_PRPSL";						// 일반회원-매물제안서
	
	public static final String KNWLDG_CMPLX_ATCH_IMG_TYP_VWMAP			= "VWMAP";							// 조감도
	public static final String KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR			= "FEATR";							// 특장점
	public static final String KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP			= "FRMAP";							// 층별 도면

	public static final String SRCH_DATE_TYPE_WEEK						= "W";								// 이번 주
	public static final String SRCH_DATE_TYPE_MONTH						= "M";								// 이번 달
	public static final String SRCH_DATE_TYPE_YEAR						= "Y";								// 1년



}