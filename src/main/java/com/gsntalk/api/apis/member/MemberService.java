package com.gsntalk.api.apis.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gsntalk.api.common.vo.*;
import com.gsntalk.api.util.GsntalkExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gsntalk.api.apis.interfaces.InterfacesDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.config.GsntalkJWTUtil;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkEncryptor;
import com.gsntalk.api.util.GsntalkUtil;

@Service( "MemberService" )
public class MemberService extends CommonService {

	public MemberService() {
		super( MemberService.class );
	}
	
	@Autowired
	private MemberDAO memberDAO;
	
	@Autowired
	private InterfacesDAO interfacesDAO;
	
	/**
	 * 로그인토큰 갱신
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param loginToken
	 * @throws Exception
	 */
	public void renewalLoginToken( String memTypCd, long memSeqno, String loginToken )throws Exception {
		int c = memberDAO.getExistsLoginTokenCnt( memTypCd, memSeqno, loginToken );
		if( c < 1 ) {
			// 로그인 토큰 등록
			memberDAO.registerLoginToken( memTypCd, memSeqno, loginToken );
			
		}else {
			// 로그인 토큰 갱신
			memberDAO.updateLoginToken( memTypCd, memSeqno, loginToken );
		}
	}
	
	/**
	 * 로그인 토큰 검증
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param loginToken
	 * @throws Exception
	 */
	public void validationLoginTokenExpireDttm( String memTypCd, long memSeqno, String loginToken )throws Exception {
		String validationTyp = memberDAO.validationLoginTokenExpireDttm( memTypCd, memSeqno, loginToken );
		if( GsntalkUtil.isEmpty( validationTyp ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( "E".equals( validationTyp ) ) {
			// JWT 토큰 만료
			throw new GsntalkAPIException( GsntalkAPIResponse.EXPIRED_JWT_TOKEN );
		}
	}
	
	/**
	 * 이메일 중복검증
	 * 
	 * @param email
	 * @throws Exception
	 */
	public void emailDupCheck( String email )throws Exception {
		int c = memberDAO.emailDupCheck( email );
		if( c > 0 ) {
			// 이미 사용중인 이메일
			throw new GsntalkAPIException( GsntalkAPIResponse.EMAIL_ALREADY_USED );
		}
	}
	
	/**
	 * FRT - 일반회원 등록
	 * 
	 * @param email
	 * @param memName
	 * @param mobNo
	 * @param vrfCnfrmToken
	 * @param pwd
	 * @param age14OvrAgreYn
	 * @param svcUseAgreYn
	 * @param prsnlInfAgreYn
	 * @param mktRcvAgreYn
	 * @param accsIp
	 * @param userAgent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public Map<String, Object> signupFRTMember( String email, String memName, String mobNo, String vrfCnfrmToken, String pwd, String age14OvrAgreYn, String svcUseAgreYn, String prsnlInfAgreYn, String mktRcvAgreYn, String accsIp, String userAgent )throws Exception {
		int c = memberDAO.emailDupCheck( email );
		if( c > 0 ) {
			// 이미 사용중인 이메일
			throw new GsntalkAPIException( GsntalkAPIResponse.EMAIL_ALREADY_USED );
		}
		
		// 휴대폰 본인인증 검증확인토큰 검증
		c = memberDAO.validationMobNoCnfrmToken( mobNo, vrfCnfrmToken );
		if( c == 0 ) {
			// 휴대폰 본인인증 검증확인토큰 불일치
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_VRF_CNFRM_TOKEN );
		}
		
		// 회원 기본정보 등록 - 일반회원
		String memTypCd = GsntalkConstants.MEM_TYP_CD_NORMAL_USER;			// 일반회원
		long memSeqno = memberDAO.signupMember( memTypCd, GsntalkConstants.YES, null, email, memName, mobNo, pwd, null, null, null, null, null );
		
		// 약관 동의정보 삭제 -> 등록
		memberDAO.deleteTermsAgreeIfExists( memSeqno );
		memberDAO.registerTermsAgree( memSeqno, age14OvrAgreYn, svcUseAgreYn, prsnlInfAgreYn, mktRcvAgreYn );
		
		// 휴대폰 본인인증정보 삭제
		memberDAO.deleteMobNoVerification( mobNo );
		
		JSONObject userItem = new JSONObject();
		userItem.put( "memSeqno", memSeqno );
		userItem.put( "memTypCd", memTypCd );
		userItem.put( "memName", memName );
		userItem.put( "email", email );
		userItem.put( "prflImgUrl", "" );
		
		String jwtToken = GsntalkJWTUtil.createNewJWTToken( memSeqno, userItem );
		String loginToken = GsntalkJWTUtil.getLoginToken( jwtToken );
		
		// 로그인 토큰 등록 ( 로그인 처리 )
		memberDAO.registerLoginToken( memTypCd, memSeqno, loginToken );
		
		// 회원 로그인이력 등록
		memberDAO.registerMemberLoginHist( memSeqno, accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_EMAIL_LOGIN );
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "jwtToken", jwtToken );
		resMap.put( "userItem", userItem );
		
		return resMap;
	}
	
	/**
	 * 회원 등록용 SNS 로그인 인증
	 * 
	 * @param snsGbCd
	 * @param code
	 * @param state
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject snsLoginVerifyForSignup( String snsGbCd, String code, String state )throws Exception {
		JSONObject profileObj = null;
		try {
			this.LOGGER.info( "*************** snsGbCd : " + snsGbCd );
			
			// KAKAO
			if( GsntalkConstants.SNS_GB_KAKAO.equals( snsGbCd ) ) {
				// 카카오 로그인 인증 및 회원정보 조회
				profileObj = gsntalkIFUtil.getKakaoUserItem( code, GsntalkConstants.SNS_LOGIN_CALL_TYPE_SIGN_UP );
				
			// NAVER
			}else {
				// 네이버 로그인 인증 및 회원정보 조회
				profileObj = gsntalkIFUtil.getNaverUserItem( code, state );
			}
			
		}catch( Exception e ) {
			this.LOGGER.error( "snsLoginVerifyForSignup Exception", e );
			
			// SNS 로그인 연동 실패
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_TO_SNS_LOGIN );
		}
		
		String snsId = "";
		String email = "";
		String userNm = "";
		String prflImgUrl = "";
		String mobNo = "";
		String genderCd = "";
		String birthYear = "";
		String birthDay = "";
		String ageLvl = "";
		
		// KAKAO
		if( GsntalkConstants.SNS_GB_KAKAO.equals( snsGbCd ) ) {
			JSONObject accountObj = GsntalkUtil.getJSONObject( profileObj, "kakao_account" );
			JSONObject profileItem = GsntalkUtil.getJSONObject( accountObj, "profile" );
			
			snsId = GsntalkUtil.getString( profileObj.get( "id" ) );
			email = GsntalkUtil.getString( accountObj.get( "email" ) );
			userNm = GsntalkUtil.getString( profileItem.get( "name" ) );
			prflImgUrl = GsntalkUtil.getString( profileItem.get( "profile_image_url" ) );
			mobNo = GsntalkUtil.parseTelnoFormat( GsntalkUtil.getString( accountObj.get( "phone_number" ) ) );
			genderCd = GsntalkUtil.getString( profileItem.get( "gender" ) );
			birthYear = GsntalkUtil.parseNumberString( GsntalkUtil.getString( profileItem.get( "birthyear" ) ) );
			birthDay = GsntalkUtil.parseNumberString( GsntalkUtil.getString( profileItem.get( "birthday" ) ) );
			ageLvl = GsntalkUtil.getString( profileItem.get( "age_range" ) );
			
		// NAVER
		}else {
			JSONObject responseObj = GsntalkUtil.getJSONObject( profileObj, "response" );
			
			snsId = GsntalkUtil.getString( responseObj.get( "id" ) );
			email = GsntalkUtil.getString( responseObj.get( "email" ) );
			userNm = GsntalkUtil.getString( responseObj.get( "name" ) );
			prflImgUrl = GsntalkUtil.getString( responseObj.get( "profile_image" ) );
			mobNo = GsntalkUtil.parseNumberString( GsntalkUtil.getString( responseObj.get( "mobile" ) ) );
			genderCd = GsntalkUtil.getString( responseObj.get( "gender" ) );
			birthYear = GsntalkUtil.getString( responseObj.get( "birthyear" ) );
			birthDay = GsntalkUtil.parseNumberString( GsntalkUtil.getString( responseObj.get( "birthday" ) ) );
			ageLvl = GsntalkUtil.getString( responseObj.get( "age" ) );
		}
		
		int c = memberDAO.emailDupCheck( email );
		if( c > 0 ) {
			// 이미 사용중인 이메일
			throw new GsntalkAPIException( GsntalkAPIResponse.EMAIL_ALREADY_USED );
		}
		
		// SNS회원등록정보 존재여부 확인
		c = memberDAO.isExistsSnsRegInfo( email );
		if( c == 0 ) {
			// SNS회원정보 등록
			memberDAO.registerSNSUserRegInfo( code, state, snsGbCd, snsId, email, userNm, prflImgUrl, mobNo, genderCd, birthYear, birthDay, ageLvl );
		}else {
			// SNS회원정보 업데이트
			memberDAO.updateSNSUserRegInfo( code, state, snsGbCd, snsId, email, userNm, prflImgUrl, mobNo, genderCd, birthYear, birthDay, ageLvl );
		}
		
		JSONObject responseObj = new JSONObject();
		responseObj.put( "email", email );
		responseObj.put( "memName", userNm );
		responseObj.put( "mobNo", mobNo );
		
		return responseObj;
	}
	
	/**
	 * SNS 계정으로 일반회원 등록
	 * 
	 * @param snsGbCd
	 * @param code
	 * @param email
	 * @param memName
	 * @param mobNo
	 * @param vrfCnfrmToken
	 * @param age14OvrAgreYn
	 * @param svcUseAgreYn
	 * @param prsnlInfAgreYn
	 * @param mktRcvAgreYn
	 * @param accsIp
	 * @param userAgent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public Map<String, Object> snsSignupFRTMember( String snsGbCd, String code, String email, String memName, String mobNo, String vrfCnfrmToken, String age14OvrAgreYn, String svcUseAgreYn, String prsnlInfAgreYn, String mktRcvAgreYn, String accsIp, String userAgent )throws Exception {
		int c = memberDAO.emailDupCheck( email );
		if( c > 0 ) {
			// 이미 사용중인 이메일
			throw new GsntalkAPIException( GsntalkAPIResponse.EMAIL_ALREADY_USED );
		}
		
		// 휴대폰 본인인증 검증확인토큰 검증
		c = memberDAO.validationMobNoCnfrmToken( mobNo, vrfCnfrmToken );
		if( c == 0 ) {
			// 휴대폰 본인인증 검증확인토큰 불일치
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_VRF_CNFRM_TOKEN );
		}
		
		// SNS회원 등록정보 조회
		SnsVO snsVO = memberDAO.getSnsUserRegInfo( snsGbCd, code, email );
		if( snsVO == null ) {
			// SNS 로그인 미인증
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_VERIFIED_SNS );
		}
		
		// 회원 기본정보 등록 - 일반회원
		String memTypCd = GsntalkConstants.MEM_TYP_CD_NORMAL_USER;			// 일반회원
		long memSeqno = memberDAO.signupMember( memTypCd, GsntalkConstants.YES, snsGbCd, email, memName, mobNo, null, snsVO.getPrflImgUrl(), snsVO.getSnsId(), snsVO.getBirthYear(), snsVO.getAgeLvl(), snsVO.getGenderCd() );
		
		// 약관 동의정보 삭제 -> 등록
		memberDAO.deleteTermsAgreeIfExists( memSeqno );
		memberDAO.registerTermsAgree( memSeqno, age14OvrAgreYn, svcUseAgreYn, prsnlInfAgreYn, mktRcvAgreYn );
		
		// 휴대폰 본인인증정보 삭제
		memberDAO.deleteMobNoVerification( mobNo );
		
		// SNS회원 등록정보 삭제
		memberDAO.deleteSnsUserRegInfo( snsGbCd, code, email );
		
		JSONObject userItem = new JSONObject();
		userItem.put( "memSeqno", memSeqno );
		userItem.put( "memTypCd", memTypCd );
		userItem.put( "memName", memName );
		userItem.put( "email", email );
		userItem.put( "prflImgUrl", GsntalkUtil.nullToEmptyString( snsVO.getPrflImgUrl() ) );
		
		String jwtToken = GsntalkJWTUtil.createNewJWTToken( memSeqno, userItem );
		String loginToken = GsntalkJWTUtil.getLoginToken( jwtToken );
		
		// 로그인 토큰 등록 ( 로그인 처리 )
		memberDAO.registerLoginToken( memTypCd, memSeqno, loginToken );
		
		// 회원 로그인이력 등록
		memberDAO.registerMemberLoginHist( memSeqno, accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_SNS_LOGIN );
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "jwtToken", jwtToken );
		resMap.put( "userItem", userItem );
		
		return resMap;
	}
	
	/**
	 * FRT - 중개회원 등록
	 * 
	 * @param estBrkOfcSeqno
	 * @param openRegNo
	 * @param openRegDate
	 * @param bizNo
	 * @param email
	 * @param memName
	 * @param mobNo
	 * @param vrfCnfrmToken
	 * @param pwd
	 * @param age14OvrAgreYn
	 * @param svcUseAgreYn
	 * @param prsnlInfAgreYn
	 * @param mktRcvAgreYn
	 * @param bizRegImgFile
	 * @param estateRegImgFile
	 * @param accsIp
	 * @param userAgent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void signupFRTEstatBrokerMember( long estBrkOfcSeqno, String openRegNo, String openRegDate, String bizNo, String email, String memName, String mobNo, String vrfCnfrmToken, String pwd, String age14OvrAgreYn, String svcUseAgreYn, String prsnlInfAgreYn, String mktRcvAgreYn, MultipartFile bizRegImgFile, MultipartFile estateRegImgFile, String accsIp, String userAgent )throws Exception {
		String orgBizRegImgFileName = bizRegImgFile.getOriginalFilename();
		String orgEstateRegImgFileName = estateRegImgFile.getOriginalFilename();
		
		// 업로드 파일 포맷 검증
		String uploadFileFormat = orgBizRegImgFileName.substring( orgBizRegImgFileName.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT );
		}
		uploadFileFormat = orgEstateRegImgFileName.substring( orgEstateRegImgFileName.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT );
		}
		
		// 선택한 중개사무소 정보 조회
		EstateBrokerOfficeVO selectedEstateBrokerOfficeVO = memberDAO.getSelectedEstateBrokerOfficeVO( estBrkOfcSeqno );
		if( selectedEstateBrokerOfficeVO == null ) {
			// 선택한 중개사무소 정보 확인불가
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SELECTED_EST_BRK_OFC );
		}
		
		int c = memberDAO.emailDupCheck( email );
		if( c > 0 ) {
			// 이미 사용중인 이메일
			throw new GsntalkAPIException( GsntalkAPIResponse.EMAIL_ALREADY_USED );
		}
		
		// 휴대폰 본인인증 검증확인토큰 검증
		c = memberDAO.validationMobNoCnfrmToken( mobNo, vrfCnfrmToken );
		if( c == 0 ) {
			// 휴대폰 본인인증 검증확인토큰 불일치
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_VRF_CNFRM_TOKEN );
		}
		
		// 회원 기본정보 등록 - 중개회원
		String memTypCd = GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT;			// 중개회원
		long memSeqno = memberDAO.signupMember( memTypCd, GsntalkConstants.NO, null, email, memName, mobNo, pwd, null, null, null, null, null );
		
		// 중개회원 중개사무소 정보 등록
		JSONObject geocodeItem = null;
		if( GsntalkUtil.isEmpty( selectedEstateBrokerOfficeVO.getAddr() ) ) {
			geocodeItem = new JSONObject();
			geocodeItem.put( "lat", 0.0d );
			geocodeItem.put( "lng", 0.0d );
			
		}else {
			geocodeItem = gsntalkIFUtil.getGeocode( selectedEstateBrokerOfficeVO.getAddr(), !GsntalkConstants.TEST_MODE );
			if( geocodeItem == null ) {
				geocodeItem = new JSONObject();
				geocodeItem.put( "lat", 0.0d );
				geocodeItem.put( "lng", 0.0d );
			}
		}
		
		double lat = GsntalkUtil.getDouble( geocodeItem.get( "lat" ) );
		double lng = GsntalkUtil.getDouble( geocodeItem.get( "lng" ) );
		String addrShortNm = GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) );
		
		// 중개회원 중개사무소 정보 등록
		memberDAO.registerEstateBrokerOfficeInfo( memSeqno, bizNo, openRegNo, openRegDate, selectedEstateBrokerOfficeVO.getOfcNm(), selectedEstateBrokerOfficeVO.getReprNm(), selectedEstateBrokerOfficeVO.getTelNo(), selectedEstateBrokerOfficeVO.getAddr(), addrShortNm, lat, lng );
		
		// 약관 동의정보 삭제 -> 등록
		memberDAO.deleteTermsAgreeIfExists( memSeqno );
		memberDAO.registerTermsAgree( memSeqno, age14OvrAgreYn, svcUseAgreYn, prsnlInfAgreYn, mktRcvAgreYn );
		
		// 휴대폰 본인인증정보 삭제
		memberDAO.deleteMobNoVerification( mobNo );
		
		// S3 첨부파일 업로드 ( 사업자등록증 이미지파일 )
		JSONObject uploadItem = gsntalkS3Util.uploadEstateBrokerOfficeFile( memSeqno, bizRegImgFile );
		memberDAO.registerEstateBrokerMemberAttachment( memSeqno, GsntalkConstants.ATTCH_FILE_BIZ_REG_IMG, GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ) );
		
		// S3 첨부파일 업로드 ( 중개등록증 이미지파일 )
		uploadItem = gsntalkS3Util.uploadEstateBrokerOfficeFile( memSeqno, estateRegImgFile );
		memberDAO.registerEstateBrokerMemberAttachment( memSeqno, GsntalkConstants.ATTCH_FILE_EST_REG_IMG, GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ) );
	}
	
	/**
	 * 회원 비밀번호 찾기 ( 변경, 이메일을 통한 가입자만 가능 )
	 * 
	 * @param email
	 * @param mobNo
	 * @param vrfCnfrmToken
	 * @param pwd
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void resetUserPwd( String email, String mobNo, String vrfCnfrmToken, String pwd )throws Exception {
		String snsGbCd = interfacesDAO.emailMobnoValidation( email, mobNo );
		if( GsntalkUtil.isEmpty( snsGbCd ) ) {
			// 이메일과 휴대폰번호가 일치하는 계정이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_MATCHED_USER_EMAIL_AND_MOBN_NO );
		}else if( !"E".equals( snsGbCd ) ) {
			// SNS가입회원은 비밀번호를 변경할 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_CHANGE_PWD_SNS_USER );
		}
		
		// 휴대폰 본인인증 검증확인토큰 검증
		int c = memberDAO.validationMobNoCnfrmToken( mobNo, vrfCnfrmToken );
		if( c == 0 ) {
			// 휴대폰 본인인증 검증확인토큰 불일치
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_VRF_CNFRM_TOKEN );
		}
		
		// 회원 비밀번호 찾기 ( 변경, 이메일을 통한 가입자만 가능 )
		memberDAO.resetUserPwd( email, mobNo, pwd );
		
		// 휴대폰 본인인증정보 삭제
		memberDAO.deleteMobNoVerification( mobNo );
	}
	
	/**
	 * FRT - 일반회원 / 중개회원 이메일로 로그인
	 * ※ no-transaction ※
	 * 
	 * @param accsIp
	 * @param userAgent
	 * @param email
	 * @param pwd
	 * @param keepLoginYn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> frtLogin( String accsIp, String userAgent, String email, String pwd, String keepLoginYn )throws Exception {
		long memSeqno = memberDAO.isExistsEmailSignupMember( email );
		if( memSeqno == 0L ) {
			// 로그인 실패 ( 이메일 또는 비밀번호 불일치 )
			throw new GsntalkAPIException( GsntalkAPIResponse.LOGIN_FAIL );
		}
		
		MemberVO memberVO = memberDAO.frtLogin( email, pwd );
		if( memberVO == null ) {
			// 회원 로그인 실패이력 등록
			memberDAO.registerMemberLoginHist( memSeqno, accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_EMAIL_LOGIN_FAIL );
			
			// 로그인 실패 ( 이메일 또는 비밀번호 불일치 )
			throw new GsntalkAPIException( GsntalkAPIResponse.LOGIN_FAIL );
		}
		
		// 중개회원이면 승인여부 추가 검증
		if( GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ) {
			if( !GsntalkConstants.YES.equals( memberVO.getAcntAprvStatCd() ) ) {
				// 관리자에 의해 아직 가입 승인이 되지 않은 계정
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_APPROVAL_ACCOUNT );
			}
		}
		
		JSONObject userItem = new JSONObject();
		userItem.put( "memSeqno", memberVO.getMemSeqno() );
		userItem.put( "estBrkMemOfcSeqno", memberVO.getEstBrkMemOfcSeqno() );
		userItem.put( "memTypCd", memberVO.getMemTypCd() );
		userItem.put( "memName", memberVO.getMemName() );
		userItem.put( "email", memberVO.getEmail() );
		userItem.put( "prflImgUrl", memberVO.getPrflImgUrl() );
		
		String jwtToken = GsntalkJWTUtil.createNewJWTToken( memberVO.getMemSeqno(), userItem );
		String loginToken = GsntalkJWTUtil.getLoginToken( jwtToken );
		
		// 로그인 토큰 등록 ( 로그인 처리 )
		memberDAO.registerLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), loginToken );
		
		String autoLoginToken = null;
		// 로그인 유지 적용이면
		if( GsntalkConstants.YES.equals( keepLoginYn ) ) {
			autoLoginToken = GsntalkUtil.makeAutoLoginToken( userAgent );
			
			// 자동로그인 토큰 등록
			memberDAO.registerAutoLotinToken( autoLoginToken, userAgent, memberVO.getMemSeqno() );
			
			userItem.put( "autoLoginToken", autoLoginToken );
		}
		
		// 회원 로그인이력 등록
		memberDAO.registerMemberLoginHist( memberVO.getMemSeqno(), accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_EMAIL_LOGIN );
		
		// response 내 로그인토큰 제거
		userItem.remove( GsntalkConstants.GSN_TALK_LOGIN_TOKEN );
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "jwtToken", jwtToken );
		resMap.put( "userItem", userItem );
		
		return resMap;
	}
	
	/**
	 * FRT - 일반회원 / 중개회원 자동로그인
	 * 
	 * @param accsIp
	 * @param userAgent
	 * @param autoLoginToken
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public Map<String, Object> frtAutoLogin( String accsIp, String userAgent, String autoLoginToken )throws Exception {
		MemberVO memberVO = memberDAO.frtAutoLogin( userAgent, autoLoginToken );
		if( memberVO == null ) {
			// 로그인 실패 ( 자동로그인 토큰 검증 실패 )
			throw new GsntalkAPIException( GsntalkAPIResponse.AUTO_LOGIN_FAIL );
		}
		
		JSONObject userItem = new JSONObject();
		userItem.put( "memSeqno", memberVO.getMemSeqno() );
		userItem.put( "estBrkMemOfcSeqno", memberVO.getEstBrkMemOfcSeqno() );
		userItem.put( "memTypCd", memberVO.getMemTypCd() );
		userItem.put( "memName", memberVO.getMemName() );
		userItem.put( "email", memberVO.getEmail() );
		userItem.put( "prflImgUrl", memberVO.getPrflImgUrl() );
		
		String jwtToken = GsntalkJWTUtil.createNewJWTToken( memberVO.getMemSeqno(), userItem );
		String loginToken = GsntalkJWTUtil.getLoginToken( jwtToken );
		
		// 로그인 토큰 등록 ( 로그인 처리 )
		memberDAO.registerLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), loginToken );
		
		// 자동로그인 토큰 갱신
		String newAutoLoginToken = GsntalkUtil.makeAutoLoginToken( userAgent );
		memberDAO.updateAutoLotinToken( autoLoginToken, newAutoLoginToken );
		userItem.put( "newAutoLoginToken", newAutoLoginToken );
		
		// 회원 로그인이력 등록
		memberDAO.registerMemberLoginHist( memberVO.getMemSeqno(), accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_EMAIL_AUTO_LOGIN );
		
		// response 내 로그인토큰 제거
		userItem.remove( GsntalkConstants.GSN_TALK_LOGIN_TOKEN );
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "jwtToken", jwtToken );
		resMap.put( "userItem", userItem );
		
		return resMap;
	}
	
	/**
	 * FRT - 일반회원 SNS 로그인
	 * 
	 * @param snsGbCd
	 * @param code
	 * @param state
	 * @param accsIp
	 * @param userAgent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> frtSnsLogin( String snsGbCd, String code, String state, String accsIp, String userAgent )throws Exception {
		JSONObject profileObj = null;
		try {
			// KAKAO
			if( GsntalkConstants.SNS_GB_KAKAO.equals( snsGbCd ) ) {
				// 카카오 로그인 인증 및 회원정보 조회
				profileObj = gsntalkIFUtil.getKakaoUserItem( code, GsntalkConstants.SNS_LOGIN_CALL_TYPE_SIGN_IN );
				
			// NAVER
			}else {
				// 네이버 로그인 인증 및 회원정보 조회
				profileObj = gsntalkIFUtil.getNaverUserItem( code, state );
			}
			
		}catch( Exception e ) {
			this.LOGGER.error( "snsLoginVerifyForSignup Exception", e );
			
			// SNS 로그인 연동 실패
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_TO_SNS_LOGIN );
		}
		
		String snsId = "";
		String email = "";
		
		// KAKAO
		if( GsntalkConstants.SNS_GB_KAKAO.equals( snsGbCd ) ) {
			JSONObject accountObj = GsntalkUtil.getJSONObject( profileObj, "kakao_account" );
			
			snsId = GsntalkUtil.getString( profileObj.get( "id" ) );
			email = GsntalkUtil.getString( accountObj.get( "email" ) );
			
		// NAVER
		}else {
			JSONObject responseObj = GsntalkUtil.getJSONObject( profileObj, "response" );
			
			snsId = GsntalkUtil.getString( responseObj.get( "id" ) );
			email = GsntalkUtil.getString( responseObj.get( "email" ) );
		}
		
		// FRT - 일반회원 SNS 로그인
		MemberVO memberVO = memberDAO.frtSnsLogin( snsGbCd, snsId, email );
		if( memberVO == null ) {
			// SNS회원 계정이 존재하지 않음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SNS_MEMBER );
		}
		
		JSONObject userItem = new JSONObject();
		userItem.put( "memSeqno", memberVO.getMemSeqno() );
		userItem.put( "estBrkMemOfcSeqno", 0L );
		userItem.put( "memTypCd", memberVO.getMemTypCd() );
		userItem.put( "memName", memberVO.getMemName() );
		userItem.put( "email", memberVO.getEmail() );
		userItem.put( "prflImgUrl", memberVO.getPrflImgUrl() );
		
		String jwtToken = GsntalkJWTUtil.createNewJWTToken( memberVO.getMemSeqno(), userItem );
		String loginToken = GsntalkJWTUtil.getLoginToken( jwtToken );
		
		// 로그인 토큰 등록 ( 로그인 처리 )
		memberDAO.registerLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), loginToken );
		
		// 회원 로그인이력 등록
		memberDAO.registerMemberLoginHist( memberVO.getMemSeqno(), accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_SNS_LOGIN );
		
		// response 내 로그인토큰 제거
		userItem.remove( GsntalkConstants.GSN_TALK_LOGIN_TOKEN );
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "jwtToken", jwtToken );
		resMap.put( "userItem", userItem );
		
		return resMap;
	}
	
	/**
	 * 중개사무소 상세정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getEstBrkOfcItem( long estBrkMemOfcSeqno )throws Exception {
		EstateBrokerOfficeVO estateBrokerOfficeVO = memberDAO.getEstBrkOfcItem( estBrkMemOfcSeqno );
		if( estateBrokerOfficeVO == null ) {
			// 선택한 중개사무소 정보 확인불가
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SELECTED_EST_BRK_OFC );
		}
		
		JSONObject item = new JSONObject();
		item.put( "estBrkMemOfcSeqno",	estateBrokerOfficeVO.getEstBrkMemOfcSeqno() );
		item.put( "ofcNm",				estateBrokerOfficeVO.getOfcNm() );
		item.put( "reprNm",				estateBrokerOfficeVO.getReprNm() );
		item.put( "addr",				estateBrokerOfficeVO.getAddr() );
		item.put( "reprTelNo",			GsntalkUtil.nullToEmptyString( estateBrokerOfficeVO.getTelNo() ) );
		item.put( "mobNo",				GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( estateBrokerOfficeVO.getMobNo() ) ) );
		item.put( "prflImgUrl",			GsntalkUtil.nullToEmptyString( estateBrokerOfficeVO.getPrflImgUrl() ) );
		item.put( "lat",				estateBrokerOfficeVO.getLat() );
		item.put( "lng",				estateBrokerOfficeVO.getLng() );
	
		return item;
	}

	/**
	 * Admin - 관리자 이메일로 로그인
	 * ※ no-transaction ※
	 *
	 * @param accsIp
	 * @param userAgent
	 * @param email
	 * @param pwd
	 * @param keepLoginYn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> adminLogin( String accsIp, String userAgent, String email, String pwd, String keepLoginYn )throws Exception {
		long memSeqno = memberDAO.isExistsEmailSignupAdmin( email );
		if( memSeqno == 0L ) {
			// 로그인 실패 ( 이메일 또는 비밀번호 불일치 )
			throw new GsntalkAPIException( GsntalkAPIResponse.LOGIN_FAIL );
		}

		MemberVO memberVO = memberDAO.adminLogin( email, pwd );
		if( memberVO == null ) {
			// 회원 로그인 실패이력 등록
			memberDAO.registerMemberLoginHist( memSeqno, accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_EMAIL_LOGIN_FAIL );

			// 로그인 실패 ( 이메일 또는 비밀번호 불일치 )
			throw new GsntalkAPIException( GsntalkAPIResponse.LOGIN_FAIL );
		}

		JSONObject userItem = new JSONObject();
		userItem.put( "memSeqno", memberVO.getMemSeqno() );
		userItem.put( "memTypCd", memberVO.getMemTypCd() );
		userItem.put( "memName", memberVO.getMemName() );
		userItem.put( "email", memberVO.getEmail() );
		userItem.put( "prflImgUrl", memberVO.getPrflImgUrl() );

		String jwtToken = GsntalkJWTUtil.createNewJWTToken( memberVO.getMemSeqno(), userItem );
		String loginToken = GsntalkJWTUtil.getLoginToken( jwtToken );

		// 로그인 토큰 등록 ( 로그인 처리 )
		memberDAO.registerLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), loginToken );

		String autoLoginToken = null;
		// 로그인 유지 적용이면
		if( GsntalkConstants.YES.equals( keepLoginYn ) ) {
			autoLoginToken = GsntalkUtil.makeAutoLoginToken( userAgent );

			// 자동로그인 토큰 등록
			memberDAO.registerAutoLotinToken( autoLoginToken, userAgent, memberVO.getMemSeqno() );

			userItem.put( "autoLoginToken", autoLoginToken );
		}

		// 회원 로그인이력 등록
		memberDAO.registerMemberLoginHist( memberVO.getMemSeqno(), accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_EMAIL_LOGIN );

		// response 내 로그인토큰 제거
		userItem.remove( GsntalkConstants.GSN_TALK_LOGIN_TOKEN );

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "jwtToken", jwtToken );
		resMap.put( "userItem", userItem );

		return resMap;
	}

	/**
	 * Admin - 관리자 자동로그인
	 *
	 * @param accsIp
	 * @param userAgent
	 * @param autoLoginToken
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public Map<String, Object> adminAutoLogin( String accsIp, String userAgent, String autoLoginToken )throws Exception {
		MemberVO memberVO = memberDAO.adminAutoLogin( userAgent, autoLoginToken );
		if( memberVO == null ) {
			// 로그인 실패 ( 자동로그인 토큰 검증 실패 )
			throw new GsntalkAPIException( GsntalkAPIResponse.AUTO_LOGIN_FAIL );
		}

		JSONObject userItem = new JSONObject();
		userItem.put( "memSeqno", memberVO.getMemSeqno() );
		userItem.put( "memTypCd", memberVO.getMemTypCd() );
		userItem.put( "memName", memberVO.getMemName() );
		userItem.put( "email", memberVO.getEmail() );
		userItem.put( "prflImgUrl", memberVO.getPrflImgUrl() );

		String jwtToken = GsntalkJWTUtil.createNewJWTToken( memberVO.getMemSeqno(), userItem );
		String loginToken = GsntalkJWTUtil.getLoginToken( jwtToken );

		// 로그인 토큰 등록 ( 로그인 처리 )
		memberDAO.registerLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), loginToken );

		// 자동로그인 토큰 갱신
		String newAutoLoginToken = GsntalkUtil.makeAutoLoginToken( userAgent );
		memberDAO.updateAutoLotinToken( autoLoginToken, newAutoLoginToken );
		userItem.put( "newAutoLoginToken", newAutoLoginToken );

		// 회원 로그인이력 등록
		memberDAO.registerMemberLoginHist( memberVO.getMemSeqno(), accsIp, userAgent, GsntalkConstants.LOGIN_HIST_GB_EMAIL_AUTO_LOGIN );

		// response 내 로그인토큰 제거
		userItem.remove( GsntalkConstants.GSN_TALK_LOGIN_TOKEN );

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "jwtToken", jwtToken );
		resMap.put( "userItem", userItem );

		return resMap;
	}

	/**
	 *
	 * Admin - 중개사 회원 목록
	 * @param acntAprvStatCd    승인유무
	 * @param actvStatGbCd		활동상태구분코드 (  NOR : 활동가능 / BLK : 활동중지 / WDR : 탈퇴처리 )
	 * @param srchDateType		기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )
	 * @param srchVal			검색어
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getEstateBrokerMemberItems( String viewTaget, String acntAprvStatCd,  String actvStatGbCd, String srchDateType, String srchVal, int pageCnt, int nowPage, int listPerPage  ) throws Exception{

		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );

		// 조회
		List<MemberVO> estateBrokerMemberList = memberDAO.getEstateBrokerMemberList( viewTaget, acntAprvStatCd, actvStatGbCd, srchDateType, srchVal, stRnum, edRnum );
		int totList = 0;

		if( GsntalkUtil.isEmptyList( estateBrokerMemberList ) ){
			estateBrokerMemberList = new ArrayList<MemberVO>();
		} else {
			totList = estateBrokerMemberList.get( 0 ).getTotalCount();
		}

		// 조회 데이터 가공
		JSONArray items = new JSONArray();
		JSONObject item = null;

		for( MemberVO vo : estateBrokerMemberList ){
			item = new JSONObject();
			item.put( "rnum", 				vo.getRownum() );
			item.put( "memSeqno", 			vo.getMemSeqno() );
			item.put( "estBrkMemOfcSeqno", 	vo.getEstBrkMemOfcSeqno() );
			item.put( "actvStatGbCd",		vo.getActvStatGbCd() );
			item.put( "acntAprvStatCd", 	vo.getAcntAprvStatCd() );
			item.put( "email", 				vo.getEmail() );
			item.put( "memName", 			vo.getMemName() );
			item.put( "ofcNm", 				vo.getOfcNm() );
			item.put( "openRegNo", 			vo.getOpenRegNo() );
			item.put( "mobNo", 				GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( vo.getMobNo() ) ) );
			item.put( "rmk",				vo.getRmk() );
			item.put( "regDttm", 			vo.getRegDttm() );
			item.put( "modDttm", 			vo.getModDttm() );
			item.put( "delDttm", 			vo.getDelDttm() );
			item.put( "lastLoginDttm", 		vo.getLastLoginDttm() );

			items.add( item );
		}

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );

		return resMap;
	}

	/**
	 * Admin - 중개사 회원 승인/거부 처리
	 * @param aprvTreatMemSeqno 	승인자
	 * @param memSeqno 				승인할 대상
	 * @param acntAprvStatCd		승인/거부 상태값
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public void updateAprvEstateBrokerMember( long aprvTreatMemSeqno, long memSeqno, String acntAprvStatCd ) throws Exception{

		// 승인 대상 조회
		MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );
		// 대상 없으면 예외 처리
		if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}
		String mobNo = GsntalkEncryptor.decrypt( memberVO.getMobNo() );

		// 승인 처리
		memberDAO.updateAprvEstateBrokerMember( aprvTreatMemSeqno, memSeqno, acntAprvStatCd );

		// 카카오톡 알림톡 발송
		JSONObject templateParam = new JSONObject();
		templateParam.put( "이름" , memberVO.getMemName() );
		templateParam.put( "이메일" , memberVO.getEmail() );

		String templateCode = "";
		// 승인
		if( "Y".equals( acntAprvStatCd ) ){
			templateCode = GsntalkConstants.KAKAO_MESSAGE_TEMPLATE_EST_BRK_CNFRM;
			templateParam.put( "이메일" , memberVO.getEmail() );
		// 거부
		}else if ( "D".equals( acntAprvStatCd ) ){
			templateCode = GsntalkConstants.KAKAO_MESSAGE_TEMPLATE_EST_BRK_DENY;
		}

		// 카카오 알림톡 발송
		JSONObject messageObj = gsntalkIFUtil.sendKakaoMessage( mobNo, templateCode,  templateParam);
		JSONArray sendRsltItems = GsntalkUtil.getJSONArray( messageObj, "sendResults" );
		JSONObject sendRsltItem = (JSONObject)sendRsltItems.get( 0 );

		String reqId = GsntalkUtil.getString( messageObj.get( "requestId" ) );
		String reqRsltCd = GsntalkUtil.getString( sendRsltItem.get( "resultCode" ) );
		String reqRsltMsg = GsntalkUtil.getString( sendRsltItem.get( "resultMessage" ) );

		// 알림톡 메시지 발송이력 등록
		interfacesDAO.registerKakaoMessageSendHist( mobNo, templateCode, templateParam.toJSONString(), reqId, reqRsltCd + " : " + reqRsltMsg );
	}

	/**
	 * Admin - 회원 상세 조회
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getMemberInfoItem ( long memSeqno ) throws Exception{

		// 멤버 상세 정보 조회
		MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );
		// 대상 없으면 예외 처리
		if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		JSONObject item = new JSONObject();
		item.put( "memSeqno" , memberVO.getMemSeqno() );
		item.put( "acntAprvStatCd" , memberVO.getAcntAprvStatCd() );
		item.put( "memTypCd" , memberVO.getMemTypCd() );
		item.put( "memTypNm" , memberVO.getMemTypNm() );
		item.put( "memName" , memberVO.getMemName() );
		item.put( "email" , memberVO.getEmail() );
		item.put( "mobNo" , GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( memberVO.getMobNo() ) )   );
		item.put( "prflImgUrl" , memberVO.getPrflImgUrl() );
		item.put( "rmk" , memberVO.getRmk() );
		item.put( "regDttm" , memberVO.getRegDttm() );
		item.put( "lastLoginDttm" , memberVO.getLastLoginDttm() );
		item.put( "delYn" , memberVO.getDelYn() );
		item.put( "prflImgUrl", memberVO.getPrflImgUrl() );

		// 중개 회원일 경우
		if( "E".equals( memberVO.getMemTypCd() ) ){

			item.put( "estBrkMemOfcSeqno" , memberVO.getEstBrkMemOfcSeqno() );
			item.put( "ofcNm" , memberVO.getOfcNm() );
			item.put ( "reprNm" , memberVO.getReprNm() );
			item.put( "addr" , memberVO.getAddr() );
			item.put( "addrShortNm" , memberVO.getAddrShortNm() );
			item.put( "telNo" , memberVO.getTelNo() );
			item.put( "bizNo" , memberVO.getBizNo() );
			item.put( "propertyCnt" , memberVO.getPropertyCnt() );
			item.put( "openRegNo", memberVO.getOpenRegNo() );
			item.put( "compMovPrpslPrptCnt", memberVO.getCompMovPrpslPrptCnt() );
			item.put( "recentCompMovPrpslDt", memberVO.getRecentCompMovPrpslDt() );

			List<AttachmentVO> attachmentList = memberDAO.getEstateBrokerMemberAttachmentList( memSeqno );

			if( attachmentList != null ){

				JSONArray attachment = new JSONArray();
				for ( AttachmentVO vo : attachmentList ){
					JSONObject object = new JSONObject();
					if( GsntalkUtil.isIn( vo.getAttchFileGbCd() , "BIZ_REG_IMG" , "EST_REG_IMG" ) ){
						object.put( "attchFileGbCd", vo.getAttchFileGbCd() );
						object.put( "fileUrl", vo.getFileUrl() );
					}
					attachment.add( object );
				}
				item.put( "attachments" , attachment );
			}
		}

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "item", item );

		return resMap;
	}

	/**
	 * Admin - 중개사 회원 활동 상태 변경
	 * @param memSeqno
	 * @param actvStatGbCd 활동상태구분코드 (  NOR : 활동가능 / BLK : 활동중지 / WDR : 탈퇴처리 )
	 * @throws Exception
	 */
	public void updateActvStatMember( long memSeqno , String actvStatGbCd) throws Exception {
		// 승인 대상 조회
		MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );
		// 대상 없으면 예외 처리
		if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		// 상태 변경
		memberDAO.updateActvStatMember( memSeqno, actvStatGbCd );
	}

	/**
	 * Admin - 일반 회원 목록 조회  ( 페이징 )
	 * @param delYn
	 * @param actvStatGbCd
	 * @param srchDateType
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getNormalMemberItems( String delYn, String actvStatGbCd, String srchDateType, String srchVal, int pageCnt, int nowPage, int listPerPage  ) throws Exception{
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );

		// 조회
		List<MemberVO> normalMemberList = memberDAO.getNormalMemberList( delYn, actvStatGbCd, srchDateType, srchVal, stRnum, edRnum);

		int totList = 0;

		if( GsntalkUtil.isEmptyList( normalMemberList ) ){
			normalMemberList = new ArrayList<MemberVO>();
		} else {
			totList = normalMemberList.get( 0 ).getTotalCount();
		}

		// 조회 데이터 가공
		JSONArray items = new JSONArray();
		JSONObject item = null;

		for( MemberVO vo : normalMemberList ){
			item = new JSONObject();
			item.put( "rnum", 				vo.getRownum() );
			item.put( "memSeqno", 			vo.getMemSeqno() );
			item.put( "actvStatGbCd",		vo.getActvStatGbCd() );
			item.put( "acntAprvStatCd", 	vo.getAcntAprvStatCd() );
			item.put( "snsGbCd", 			vo.getSnsGbCd() );
			item.put( "email", 				vo.getEmail() );
			item.put( "memName", 			vo.getMemName() );
			item.put( "mobNo", 				GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( vo.getMobNo() ) ) );
			item.put( "rmk",				vo.getRmk() );
			item.put( "regDttm", 			vo.getRegDttm() );
			item.put( "modDttm", 			vo.getModDttm() );
			item.put( "delDttm", 			vo.getDelDttm() );
			item.put( "lastLoginDttm",		vo.getLastLoginDttm() );

			items.add( item );
		}

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );

		return resMap;
	}

	/**
	 * 회원 정보 수정
	 * @param memSeqno
	 * @param mobNo
	 * @param mobVrfNo
	 * @param memName
	 * @throws Exception
	 */
	public void updateMyInfo( long memSeqno, String mobNo, String mobVrfNo, String memName ) throws Exception {
		// 수정 대상 조회
		MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

		// 대상 없으면 예외 처리
		if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		memberDAO.updateMemberInfo( memSeqno, mobNo, mobVrfNo, memName );

	}

	/**
	 * 중개회원 중개사무소 정보 수정
	 * @param memSeqno
	 * @param telNo
	 * @throws Exception
	 */
	public void updateOfcInfo( long memSeqno, String telNo ) throws Exception{
		// 수정 대상 조회
		MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

		// 대상 없으면 예외 처리
		if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		// 중개사 회원 아닐 경우 예외 처리
		if( !"E".equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		long estBrkMemOfcSeqno = memberVO.getEstBrkMemOfcSeqno();

		// 중개 사무소 시퀀스 없으면 예외 처리
		if( GsntalkUtil.isEmpty( estBrkMemOfcSeqno ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		memberDAO.updateOfcInfo( memSeqno, estBrkMemOfcSeqno, telNo );
	}

	/**
	 * 회원 비밀번호 수정
	 * @param memSeqno
	 * @param email
	 * @param nowPwd
	 * @param newPwd
	 * @throws Exception
	 */
	public void updatePassowrd( long memSeqno, String email, String nowPwd, String newPwd ) throws Exception{
		// 수정 대상 조회
		MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

		// 대상 없으면 예외 처리
		if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		if( !"E".equals( memberVO.getSnsGbCd() ) ) {
			// SNS가입회원은 비밀번호를 변경할 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_CHANGE_PWD_SNS_USER );
		}

		// 비밀번호 확인
		MemberVO pwdCheckMemberVO = memberDAO.frtLogin( email, nowPwd );

		// 비밀번호 불일치
		if( GsntalkUtil.isEmpty( pwdCheckMemberVO ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.PASSWORD_MISMATCH );
		}

		// 비밀번호 수정
		memberDAO.updatePassowrd( memSeqno, newPwd );

	}

	/**
	 * 회원 프로필 이미지 수정
	 * @param memSeqno
	 * @param prflImg
	 * @return
	 * @throws Exception
	 */
	public JSONObject updateMemberPrflImg( long memSeqno, MultipartFile prflImg ) throws Exception{
		String prflImgFileName = prflImg.getOriginalFilename();

		// 업로드 파일 포맷 검증
		String uploadFileFormat = prflImgFileName.substring( prflImgFileName.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT );
		}
		// S3 첨부파일 업로드 ( 이미지 업로드 )
		JSONObject uploadItem = gsntalkS3Util.uploadMemberProfileImageFile( memSeqno, prflImg );

		String orgFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
		String saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
		String fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );

		memberDAO.updateMemberPrflImg( memSeqno, fileUrl );

		return uploadItem;
	}
	
	/**
	 * FRT - 관심매물 목록 조회 ( 페이징 )
	 * 
	 * @param memSeqno
	 * @param prptTyp
	 * @param tranTypGbCd
	 * @param estateTypCd
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getFavPropertyItems( long memSeqno, String prptTyp, String tranTypGbCd, String estateTypCd, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		// FRT - 최근 본 매물 목록조회
		List<FavRecentPrptVO> favPrptList = memberDAO.getFavPropertyItems( memSeqno, prptTyp, tranTypGbCd, estateTypCd, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( favPrptList ) ) {
			favPrptList = new ArrayList<FavRecentPrptVO>();
		}else {
			totList = favPrptList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String costDscr = null;
		JSONObject areaItem = null;
		JSONArray pyAreaItems = null;
		JSONArray meterAreaItems = null;
		
		for( FavRecentPrptVO vo : favPrptList ) {
			estateTypCd = vo.getEstateTypCd();
			tranTypGbCd = vo.getTranTypGbCd();
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			
			/** 면적정보 적용 */
			areaItem = new JSONObject();
			pyAreaItems = new JSONArray();
			meterAreaItems = new JSONArray();
			// 건물이면 대지면적/연면적
			if( "BLD".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				
			// 토지/임야면 대지면적
			}else if( "LND".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			
			// 단독공장이면 전용면적/대지면적/연면적
			}else if( "FTR".equals( estateTypCd ) ) {
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			
			// 이외에는 공급면적/전용면적
			}else {
				pyAreaItems.add( "공급 " + vo.getSplyArea() );
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				meterAreaItems.add( "공급 " + GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
			}
			areaItem.put( "pyAreaItems", pyAreaItems );
			areaItem.put( "meterAreaItems", meterAreaItems );
			
			item = new JSONObject();
			
			item.put( "prptTyp", vo.getPrptTyp() );
			item.put( "seqno", vo.getSeqno() );
			item.put( "tranTypGbNm", vo.getTranTypGbNm() );
			item.put( "costDscr", costDscr );
			item.put( "addrShortNm", vo.getAddr().substring( 0, vo.getAddr().indexOf( " " ) ) + " "  + vo.getAddrShortNm() );
			item.put( "lat", vo.getLat() );
			item.put( "lng", vo.getLng() );
			item.put( "reprImgUrl", vo.getRepImgUrl() );
			item.put( "areaItem", areaItem );
			item.put( "flr", vo.getFlr() );
			item.put( "allFlr", vo.getAllFlr() );
			item.put( "minFlr", vo.getMinFlr() );
			item.put( "maxFlr", vo.getMaxFlr() );
			item.put( "smplSmrDscr", vo.getSmplSmrDscr() );
			item.put( "bldNm", vo.getBldNm() );
			item.put( "poStatGbCd", vo.getPoStatGbCd() );
			item.put( "favYn", vo.getFavYn() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * FRT - 최근 본 매물 목록조회
	 * 
	 * @param memSeqno
	 * @param resentItems
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray getRecentPropertyItems( long memSeqno, JSONArray resentItems )throws Exception {
		if( GsntalkUtil.isEmptyArray( resentItems ) ) {
			return new JSONArray();
		}
		
		String prptTyp = null;
		long seqno = 0L;
		String srchDttm = null;
		JSONObject recentItem = null;
		for( int i = 0; i < resentItems.size(); i ++ ) {
			recentItem = (JSONObject)resentItems.get( i );
			
			prptTyp = GsntalkUtil.getString( recentItem.get( "prptTyp" ) );
			seqno = GsntalkUtil.getLong( recentItem.get( "seqno" ) );
			srchDttm = GsntalkUtil.getString( recentItem.get( "srchDttm" ) );
			
			if( GsntalkUtil.isEmpty( prptTyp ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "resentItems[" + i + "] -> prptTyp is empty."  );
			}
			if( !GsntalkUtil.isIn( prptTyp, "P", "S" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "resentItems[" + i + "] -> prptTyp is not in P / S"  );
			}
			if( seqno == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "resentItems[" + i + "] -> seqno is empty."  );
			}
			if( !GsntalkUtil.is12DateTimeFormat( srchDttm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "resentItems[" + i + "] -> srchDttm is not valid, check this value."  );
			}
			
			recentItem.put( "srchDttm", GsntalkUtil.parseNumberString( srchDttm ) );
		}
		
		// FRT - 최근 본 매물 목록조회
		List<FavRecentPrptVO> recentPrptList = memberDAO.getRecentPropertyItems( memSeqno, resentItems );
		if( GsntalkUtil.isEmptyList( recentPrptList ) ) {
			recentPrptList = new ArrayList<FavRecentPrptVO>();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String costDscr = null;
		JSONObject areaItem = null;
		JSONArray pyAreaItems = null;
		JSONArray meterAreaItems = null;
		
		String estateTypCd = null;
		String tranTypGbCd = null;
		for( FavRecentPrptVO vo : recentPrptList ) {
			estateTypCd = vo.getEstateTypCd();
			tranTypGbCd = vo.getTranTypGbCd();
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			
			/** 면적정보 적용 */
			areaItem = new JSONObject();
			pyAreaItems = new JSONArray();
			meterAreaItems = new JSONArray();
			// 건물이면 대지면적/연면적
			if( "BLD".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				
			// 토지/임야면 대지면적
			}else if( "LND".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			
			// 단독공장이면 전용면적/대지면적/연면적
			}else if( "FTR".equals( estateTypCd ) ) {
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			
			// 이외에는 공급면적/전용면적
			}else {
				pyAreaItems.add( "공급 " + vo.getSplyArea() );
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				meterAreaItems.add( "공급 " + GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
			}
			areaItem.put( "pyAreaItems", pyAreaItems );
			areaItem.put( "meterAreaItems", meterAreaItems );
			
			item = new JSONObject();
			
			item.put( "prptTyp", vo.getPrptTyp() );
			item.put( "seqno", vo.getSeqno() );
			item.put( "tranTypGbNm", vo.getTranTypGbNm() );
			item.put( "costDscr", costDscr );
			item.put( "addrShortNm", vo.getAddr().substring( 0, vo.getAddr().indexOf( " " ) ) + " "  + vo.getAddrShortNm() );
			item.put( "lat", vo.getLat() );
			item.put( "lng", vo.getLng() );
			item.put( "areaItem", areaItem );
			item.put( "flr", vo.getFlr() );
			item.put( "allFlr", vo.getAllFlr() );
			item.put( "minFlr", vo.getMinFlr() );
			item.put( "maxFlr", vo.getMaxFlr() );
			item.put( "smplSmrDscr", vo.getSmplSmrDscr() );
			item.put( "bldNm", vo.getBldNm() );
			item.put( "poStatGbCd", vo.getPoStatGbCd() );
			item.put( "favYn", vo.getFavYn() );
			
			items.add( item );
		}
		
		return items;
	}

	/**
	 * 알림 목록 조회
	 * @param memSeqno
	 * @param notiGbCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public JSONObject getNotificationItems( long memSeqno, String notiGbCd ) throws Exception {
		NotificationVO notCnfrmNotiVo = memberDAO.getNotCnfrmNotificationCnt( memSeqno );

		List<NotificationVO> notificationList = memberDAO.getNotificationList( memSeqno, notiGbCd );
		JSONArray notiList = new JSONArray();
		JSONObject obj = null;
		for( NotificationVO vo : notificationList ){
			obj = new JSONObject();
			obj.put( "notiSeqno", vo.getNotiSeqno() );
			obj.put( "notiGbCd", vo.getNotiGbCd() );
			obj.put( "prptSeqno", vo.getPrptCnt() );
			obj.put( "suggstnSalesSeqno", vo.getSuggstnSalesSeqno() );
			obj.put( "notiTtl", vo.getNotiTtl() );
			obj.put( "notiDscr", vo.getNotiDscr() );
			obj.put( "cnfrmYn", vo.getCnfrmYn() );
			obj.put( "regDttm", GsntalkUtil.parserPassedTimeString( vo.getRegDttm() ) );
			notiList.add( obj );

			// 알림 확인처리
			if( "N".equals( vo.getCnfrmYn() ) ){
				memberDAO.updateNotificationCnfrm( memSeqno, vo.getNotiSeqno() );
			}
		}

		JSONObject item = new JSONObject();
		item.put( "notiCnt", notCnfrmNotiVo.getNotiCnt() );
		item.put( "prptCnt", notCnfrmNotiVo.getPrptCnt() );
		item.put( "salesCnt", notCnfrmNotiVo.getSalesCnt() );
		item.put( "schdlCnt", notCnfrmNotiVo.getSchdlCnt() );
		item.put( "notiList", notiList );

		return item;
	}

	/**
	 * Admin - 일반 회원 목록 조회 - Excel Download
	 * @param delYn
	 * @param actvStatGbCd
	 * @param srchDateType
	 * @param srchVal
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public XSSFWorkbook downloadNormalMemberItems(String delYn, String actvStatGbCd, String srchDateType, String srchVal )throws Exception {
		List<MemberVO> memberList = memberDAO.getNormalMemberExcelDownloadList( delYn, actvStatGbCd, srchDateType, srchVal );

		String[] titles = new String[]	{ "No",							"상태",								"유저아이디",						"가입경로",						"유저 이름",						"연락처",							"비고",							"가입일",							"최종접속일", 							"탈퇴일" };
		String[] fields = new String[]	{ "no",							"actvStatGbNm",						"email",						"snsGbCd",						"memName",						"mobNo",						"rmk",							"regDttm",						"lastLoginDttm", 					"delDttm" };
		int[] sizes		= new int[] 	{ GsntalkExcelUtil.WIDTH_NO, 	GsntalkExcelUtil.WIDTH_SMALL, 		GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,		GsntalkExcelUtil.WIDTH_SMALL};
		int[] aligns	= new int[] 	{ GsntalkExcelUtil.CENTER, 		GsntalkExcelUtil.CENTER, 			GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,			GsntalkExcelUtil.CENTER };

		JSONArray items = new JSONArray();
		JSONObject item = null;
		int no = 1;

		for ( MemberVO vo : memberList ){
			item = new JSONObject();
			item.put( "no", String.valueOf( no ) );
			item.put( "actvStatGbNm", 	vo.getActvStatGbNm() );
			item.put( "email", 			vo.getEmail() );
			item.put( "snsGbCd", 		vo.getSnsGbCd() );
			item.put( "memName", 		vo.getMemName() );
			item.put( "mobNo", 			GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( vo.getMobNo() ) ) );
			item.put( "rmk", 			vo.getRmk() );
			item.put( "regDttm", 		vo.getRegDttm() );
			item.put( "lastLoginDttm", 	vo.getLastLoginDttm() );
			item.put( "delDttm", 		vo.getDelDttm() );

			items.add( item );

			no ++;
		}

		return new GsntalkExcelUtil( titles, fields, sizes, aligns ).getXSSFExcelWorkbook( items );
	}

	/**
	 * Admin - 중개사 회원 목록 조회 - Excel Download
	 * @param viewTaget
	 * @param acntAprvStatCd
	 * @param actvStatGbCd
	 * @param srchDateType
	 * @param srchVal
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public XSSFWorkbook downloadEstateBrokerMemberItems( String viewTaget, String acntAprvStatCd,  String actvStatGbCd, String srchDateType, String srchVal )throws Exception {
		List<MemberVO> memberList = memberDAO.getEstateBrokerMemberExcelDownloadList( viewTaget, acntAprvStatCd, actvStatGbCd, srchDateType, srchVal );

		String[] titles = new String[]	{ "No",							"계정승인상태",						"활동상태",								"유저아이디",						"유저이름",						"중개사무소명",					"중개등록번호",						"연락처",							"비고",							"가입일",							"최근수정일", 							"최종접속일",						"탈퇴일" };
		String[] fields = new String[]	{ "no",							"acntAprvStatNm",					"actvStatGbNm",							"email",						"memName",						"ofcNm",						"openRegNo",						"mobNo",						"rmk",							"regDttm",						"modDttm", 							"lastLoginDttm", 				"delDttm" };
		int[] sizes		= new int[] 	{ GsntalkExcelUtil.WIDTH_NO, 	GsntalkExcelUtil.WIDTH_SMALL, 		GsntalkExcelUtil.WIDTH_SMALL,			GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,		GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,		GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL};
		int[] aligns	= new int[] 	{ GsntalkExcelUtil.CENTER, 		GsntalkExcelUtil.CENTER, 			GsntalkExcelUtil.CENTER, 				GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,			GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,			GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER };

		JSONArray items = new JSONArray();
		JSONObject item = null;
		int no = 1;

		for ( MemberVO vo : memberList ){
			item = new JSONObject();
			item.put( "no", String.valueOf( no ) );
			item.put( "acntAprvStatNm", vo.getAcntAprvStatNm() );
			item.put( "actvStatGbNm", 	vo.getActvStatGbNm() );
			item.put( "email", 			vo.getEmail() );
			item.put( "memName", 		vo.getMemName() );
			item.put( "mobNo", 			GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( vo.getMobNo() ) ) );
			item.put( "ofcNm", 			vo.getOfcNm() );
			item.put( "openRegNo", 		vo.getOpenRegNo() );
			item.put( "rmk", 			vo.getRmk() );
			item.put( "regDttm", 		vo.getRegDttm() );
			item.put( "modDttm", 		vo.getModDttm() );
			item.put( "delDttm", 		vo.getDelDttm() );
			item.put( "lastLoginDttm", 	vo.getLastLoginDttm() );

			items.add( item );

			no ++;
		}

		return new GsntalkExcelUtil( titles, fields, sizes, aligns ).getXSSFExcelWorkbook( items );
	}

	/**
	 * 공지 알림 등록
	 * @param notiTypGbCd
	 * @param sendDt
	 * @param notiDscr
	 * @throws Exception
	 */
	public void registerPublicNotification( String notiTypGbCd, String sendDt, String notiDscr ) throws Exception{
		memberDAO.registerPublicNotification( notiTypGbCd, sendDt, notiDscr );
	}

	/**
	 * 공지 알림 삭제
	 * @param publicNotiSeqnoItems
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void deletePublicNotification( JSONArray publicNotiSeqnoItems )throws Exception{
		PublicNotificationVO vo;
		for( int i = 0; i < publicNotiSeqnoItems.size(); i++ ){
			long publicNotiSeqno = GsntalkUtil.getLong( publicNotiSeqnoItems.get( i ) );

			vo = memberDAO.getPublicNotificationInfo( publicNotiSeqno );
			if( vo == null ){
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
			}

			memberDAO.deletePublicNotification( publicNotiSeqno );
		}
	}

	/**
	 * 공지 알림 수정
	 * @param publicNotiSeqno
	 * @param notiTypGbCd
	 * @param sendDt
	 * @param notiDscr
	 * @throws Exception
	 */
	public void updatePublicNotification( long publicNotiSeqno, String notiTypGbCd, String sendDt, String notiDscr ) throws Exception {
		PublicNotificationVO vo = memberDAO.getPublicNotificationInfo( publicNotiSeqno );
		if( vo == null ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}
		if( "N".equals( vo.getModYn() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MODIFICATION_DATE_HAS_PASSED );
		}
		memberDAO.updatePublicNotification( publicNotiSeqno, notiTypGbCd, sendDt, notiDscr );
	}

	/**
	 * 공지 알림 상세 조회
	 * @param publicNotiSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getPublicNotificationItem( long publicNotiSeqno ) throws Exception {
		PublicNotificationVO vo = memberDAO.getPublicNotificationInfo( publicNotiSeqno );
		if( vo == null ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		JSONObject item = new JSONObject();
		item.put( "publicNotiSeqno", 	vo.getPublicNotiSeqno() );
		item.put( "notiTypGbCd", 		vo.getNotiTypGbCd() );
		item.put( "sendDt", 			vo.getSendDt() );
		item.put( "notiDscr", 			vo.getNotiDscr() );
		item.put( "sendTreatDttm",		vo.getSendTreatDttm() );
		item.put( "rcpntCnt", 			vo.getRcpntCnt() );
		item.put( "modYn", 				vo.getModYn() );
		item.put( "regDttm", 			vo.getRegDttm() );

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "item", item );

		return resMap;
	}

	/**
	 * 공지 알림 목록 조회 ( 페이징 )
	 * @param srchDateType
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getPublicNotificationList( String srchDateType, String srchVal, int pageCnt, int nowPage, int listPerPage) throws Exception{
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );

		List<PublicNotificationVO> list = memberDAO.getPublicNotificationList( srchDateType, srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( list ) ) {
			list = new ArrayList<PublicNotificationVO>();
		}else {
			totList = list.get( 0 ).getTotalCount();
		}

		JSONArray items = new JSONArray();
		JSONObject item = null;

		for( PublicNotificationVO vo : list ){
			item = new JSONObject();
			item.put( "publicNotiSeqno", 	vo.getPublicNotiSeqno() );
			item.put( "notiTypGbCd", 		vo.getNotiTypGbCd() );
			item.put( "notiTypGbNm", 		vo.getNotiTypGbNm() );
			item.put( "sendDt", 			vo.getSendDt() );
			item.put( "notiDscr", 			vo.getNotiDscr() );
			item.put( "sendTreatDttm",		vo.getSendTreatDttm() );
			item.put( "rcpntCnt", 			vo.getRcpntCnt() );
			item.put( "modYn", 				vo.getModYn() );
			item.put( "regDttm", 			vo.getRegDttm() );

			items.add( item );
		}

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );

		return resMap;
	}

}