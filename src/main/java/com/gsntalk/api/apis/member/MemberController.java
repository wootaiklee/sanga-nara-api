package com.gsntalk.api.apis.member;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gsntalk.api.apis.interfaces.InterfacesService;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonController;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.config.GsntalkCORS;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.config.GsntalkJWTUtil;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

/**
 * API 2.3 Member
 */
@Controller
@RequestMapping( value = "/member" )
public class MemberController extends CommonController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private InterfacesService interfacesService;

	public MemberController() {
		super( MemberController.class );
	}
	
	/**
	 * 2.3.1 이메일 중복검증
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/emailDupCheck", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject emailDupCheck( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String email = GsntalkUtil.getString( param.get( "email" ) );
		if( GsntalkUtil.isEmpty( email ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		if( !GsntalkUtil.isEmailFormat( email ) ) {
			// 잘못된 이메일 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_EMAIL_FORMAT );
		}
		
		try {
			memberService.emailDupCheck( email );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * 2.3.2 FRT - 일반회원 등록
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/signupFRTMember", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject signupFRTMember( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String accsIp = GsntalkUtil.getClientIpAddress( request );
		String userAgent = GsntalkUtil.getClientUserAgent( request );
		String email = GsntalkUtil.getString( param.get( "email" ) );
		String memName = GsntalkUtil.getString( param.get( "memName" ) );
		String mobNo = GsntalkUtil.getString( param.get( "mobNo" ) );
		mobNo = GsntalkUtil.parseNumberString( mobNo );
		String vrfCnfrmToken = GsntalkUtil.getString( param.get( "vrfCnfrmToken" ) );
		
		String pwd = GsntalkUtil.getString( param.get( "pwd" ) );
		String age14OvrAgreYn = GsntalkUtil.getString( param.get( "age14OvrAgreYn" ) );
		String svcUseAgreYn = GsntalkUtil.getString( param.get( "svcUseAgreYn" ) );
		String prsnlInfAgreYn = GsntalkUtil.getString( param.get( "prsnlInfAgreYn" ) );
		String mktRcvAgreYn = GsntalkUtil.ifEmptyString( param.get( "mktRcvAgreYn" ), GsntalkConstants.NO );
		
		if(
			GsntalkUtil.isEmpty( email )
			||
			GsntalkUtil.isEmpty( memName )
			||
			GsntalkUtil.isEmpty( mobNo )
			||
			GsntalkUtil.isEmpty( pwd )
			||
			GsntalkUtil.isEmpty( age14OvrAgreYn )
			||
			GsntalkUtil.isEmpty( svcUseAgreYn )
			||
			GsntalkUtil.isEmpty( prsnlInfAgreYn )
			||
			GsntalkUtil.isEmpty( vrfCnfrmToken )
		) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( !GsntalkUtil.isEmailFormat( email ) ) {
			// 잘못된 이메일 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_EMAIL_FORMAT );
		}
		if( !GsntalkUtil.isMobnoFormat( mobNo ) ) {
			// 잘못된 휴대폰번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_NO_FORMAT );
		}
		if( !GsntalkUtil.isPasswordFormat( pwd ) ) {
			// 잘못된 비밀번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_PWD_FORMAT );
		}
		if( !GsntalkConstants.YES.equals( age14OvrAgreYn ) ) {
			// 만14세이상 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_AGE_14_OVER );
		}
		if( !GsntalkConstants.YES.equals( svcUseAgreYn ) ) {
			// 서비스이용약관 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_SERVICE_USE );
		}
		if( !GsntalkConstants.YES.equals( prsnlInfAgreYn ) ) {
			// 개인정보수집및이용 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_PERSONAL_INFO );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			resMap = memberService.signupFRTMember( email, memName, mobNo, vrfCnfrmToken, pwd, age14OvrAgreYn, svcUseAgreYn, prsnlInfAgreYn, mktRcvAgreYn, accsIp, userAgent );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkUtil.getString( resMap.get( "jwtToken" ) ), (JSONObject)resMap.get( "userItem" ) );
	}
	
	/**
	 * 2.3.3 회원 등록용 SNS 로그인 인증
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/snsLoginVerifyForSignup", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject snsLoginVerifyForSignup( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String snsGbCd = GsntalkUtil.getString( param.get( "snsGbCd" ) );
		String code = GsntalkUtil.getString( param.get( "code" ) );
		String state = GsntalkUtil.getString( param.get( "state" ) );
		
		if(
				GsntalkUtil.isEmpty( snsGbCd )
				||
				GsntalkUtil.isEmpty( code )
				||
				GsntalkUtil.isEmpty( state )
		) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( !GsntalkUtil.isIn( snsGbCd, GsntalkConstants.SNS_GB_KAKAO, GsntalkConstants.SNS_GB_NAVER ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
		}
		
		JSONObject item = null;
		
		try {
			item = memberService.snsLoginVerifyForSignup( snsGbCd, code, state );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( item );
	}
	
	/**
	 * 2.3.4 SNS 계정으로 일반회원 등록
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/snsSignupFRTMember", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject snsSignupFRTMember( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String accsIp = GsntalkUtil.getClientIpAddress( request );
		String userAgent = GsntalkUtil.getClientUserAgent( request );
		String snsGbCd = GsntalkUtil.getString( param.get( "snsGbCd" ) );
		String code = GsntalkUtil.getString( param.get( "code" ) );
		String email = GsntalkUtil.getString( param.get( "email" ) );
		String memName = GsntalkUtil.getString( param.get( "memName" ) );
		String mobNo = GsntalkUtil.getString( param.get( "mobNo" ) );
		mobNo = GsntalkUtil.parseNumberString( mobNo );
		String vrfCnfrmToken = GsntalkUtil.getString( param.get( "vrfCnfrmToken" ) );
		
		String age14OvrAgreYn = GsntalkUtil.getString( param.get( "age14OvrAgreYn" ) );
		String svcUseAgreYn = GsntalkUtil.getString( param.get( "svcUseAgreYn" ) );
		String prsnlInfAgreYn = GsntalkUtil.getString( param.get( "prsnlInfAgreYn" ) );
		String mktRcvAgreYn = GsntalkUtil.ifEmptyString( param.get( "mktRcvAgreYn" ), GsntalkConstants.NO );
		
		if(
				GsntalkUtil.isEmpty( snsGbCd )
				||
				GsntalkUtil.isEmpty( code )
				||
				GsntalkUtil.isEmpty( email )
				||
				GsntalkUtil.isEmpty( memName )
				||
				GsntalkUtil.isEmpty( mobNo )
				||
				GsntalkUtil.isEmpty( age14OvrAgreYn )
				||
				GsntalkUtil.isEmpty( svcUseAgreYn )
				||
				GsntalkUtil.isEmpty( prsnlInfAgreYn )
				||
				GsntalkUtil.isEmpty( vrfCnfrmToken )
		) {
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER snsGbCd : " + snsGbCd );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER code : " + code );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER email : " + email );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER memName : " + memName );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER mobNo : " + mobNo );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER age14OvrAgreYn : " + age14OvrAgreYn );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER svcUseAgreYn : " + svcUseAgreYn );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER prsnlInfAgreYn : " + prsnlInfAgreYn );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER vrfCnfrmToken : " + vrfCnfrmToken );
			
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( !GsntalkUtil.isIn( snsGbCd, GsntalkConstants.SNS_GB_KAKAO, GsntalkConstants.SNS_GB_NAVER ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
		}
		if( !GsntalkUtil.isEmailFormat( email ) ) {
			// 잘못된 이메일 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_EMAIL_FORMAT );
		}
		if( !GsntalkUtil.isMobnoFormat( mobNo ) ) {
			// 잘못된 휴대폰번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_NO_FORMAT );
		}
		if( !GsntalkConstants.YES.equals( age14OvrAgreYn ) ) {
			// 만14세이상 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_AGE_14_OVER );
		}
		if( !GsntalkConstants.YES.equals( svcUseAgreYn ) ) {
			// 서비스이용약관 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_SERVICE_USE );
		}
		if( !GsntalkConstants.YES.equals( prsnlInfAgreYn ) ) {
			// 개인정보수집및이용 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_PERSONAL_INFO );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			resMap = memberService.snsSignupFRTMember( snsGbCd, code, email, memName, mobNo, vrfCnfrmToken, age14OvrAgreYn, svcUseAgreYn, prsnlInfAgreYn, mktRcvAgreYn, accsIp, userAgent );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkUtil.getString( resMap.get( "jwtToken" ) ), (JSONObject)resMap.get( "userItem" ) );
	}

	/**
	 * 2.3.5 FRT - 중개회원 등록
	 * @param request
	 * @param response
	 * @param bizRegImgFile
	 * @param estateRegImgFile
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/signupFRTEstatBrokerMember", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject signupFRTEstatBrokerMember( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = true, value="bizRegImgFile" ) MultipartFile bizRegImgFile, @RequestParam( required = true, value="estateRegImgFile" ) MultipartFile estateRegImgFile )throws Exception {
		String accsIp = GsntalkUtil.getClientIpAddress( request );
		String userAgent = GsntalkUtil.getClientUserAgent( request );
		long estBrkOfcSeqno = GsntalkUtil.getLong( request.getParameter( "estBrkOfcSeqno" ) );
		String openRegNo = GsntalkUtil.getString( request.getParameter( "openRegNo" ) );
		String openRegDate = GsntalkUtil.getString( request.getParameter( "openRegDate" ) );
		String bizNo = GsntalkUtil.getString( request.getParameter( "bizNo" ) );
		String email = GsntalkUtil.getString( request.getParameter( "email" ) );
		String memName = GsntalkUtil.getString( request.getParameter( "memName" ) );
		String mobNo = GsntalkUtil.getString( request.getParameter( "mobNo" ) );
		mobNo = GsntalkUtil.parseNumberString( mobNo );
		String vrfCnfrmToken = GsntalkUtil.getString( request.getParameter( "vrfCnfrmToken" ) );
		
		String pwd = GsntalkUtil.getString( request.getParameter( "pwd" ) );
		String age14OvrAgreYn = GsntalkUtil.getString( request.getParameter( "age14OvrAgreYn" ) );
		String svcUseAgreYn = GsntalkUtil.getString( request.getParameter( "svcUseAgreYn" ) );
		String prsnlInfAgreYn = GsntalkUtil.getString( request.getParameter( "prsnlInfAgreYn" ) );
		String mktRcvAgreYn = GsntalkUtil.ifEmptyString( request.getParameter( "mktRcvAgreYn" ), GsntalkConstants.NO );
		
		if(
			estBrkOfcSeqno == 0L
			||
			GsntalkUtil.isEmpty( openRegNo )
			||
			GsntalkUtil.isEmpty( openRegDate )
			||
			GsntalkUtil.isEmpty( bizNo )
			||
			GsntalkUtil.isEmpty( email )
			||
			GsntalkUtil.isEmpty( memName )
			||
			GsntalkUtil.isEmpty( mobNo )
			||
			GsntalkUtil.isEmpty( pwd )
			||
			GsntalkUtil.isEmpty( age14OvrAgreYn )
			||
			GsntalkUtil.isEmpty( svcUseAgreYn )
			||
			GsntalkUtil.isEmpty( prsnlInfAgreYn )
			||
			GsntalkUtil.isEmpty( vrfCnfrmToken )
		) {
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER estBrkOfcSeqno : " + estBrkOfcSeqno );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER openRegNo : " + openRegNo );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER openRegDate : " + openRegDate );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER code : " + bizNo );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER email : " + email );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER memName : " + memName );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER mobNo : " + mobNo );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER pwd : " + pwd );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER age14OvrAgreYn : " + age14OvrAgreYn );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER svcUseAgreYn : " + svcUseAgreYn );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER prsnlInfAgreYn : " + prsnlInfAgreYn );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER vrfCnfrmToken : " + vrfCnfrmToken );
			
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		bizNo = GsntalkUtil.parseNumberString( bizNo );
		if( bizNo.length() != 10 ) {
			// 잘못된 사업자번호 ( 10차리가 아님 )
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_BIZ_REG_NO_FORMAT );
		}
		if( !GsntalkUtil.is8DateFormat( openRegDate, true ) ){
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT );
		}
		this.LOGGER.info( "@@@@ email : " + email );
		
		if( !GsntalkUtil.isEmailFormat( email ) ) {
			this.LOGGER.info( "@@@@ email is : " + email );
			
			// 잘못된 이메일 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_EMAIL_FORMAT );
		}
		if( !GsntalkUtil.isMobnoFormat( mobNo ) ) {
			// 잘못된 휴대폰번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_NO_FORMAT );
		}
		if( !GsntalkUtil.isPasswordFormat( pwd ) ) {
			// 잘못된 비밀번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_PWD_FORMAT );
		}
		if( !GsntalkConstants.YES.equals( age14OvrAgreYn ) ) {
			// 만14세이상 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_AGE_14_OVER );
		}
		if( !GsntalkConstants.YES.equals( svcUseAgreYn ) ) {
			// 서비스이용약관 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_SERVICE_USE );
		}
		if( !GsntalkConstants.YES.equals( prsnlInfAgreYn ) ) {
			// 개인정보수집및이용 미동의
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AGREE_TO_PERSONAL_INFO );
		}
		
		if( bizRegImgFile == null || bizRegImgFile.getSize() == 0L ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT );
		}
		if( estateRegImgFile == null || estateRegImgFile.getSize() == 0L ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT );
		}
		
		try {
			memberService.signupFRTEstatBrokerMember( estBrkOfcSeqno, openRegNo, openRegDate, bizNo, email, memName, mobNo, vrfCnfrmToken, pwd, age14OvrAgreYn, svcUseAgreYn, prsnlInfAgreYn, mktRcvAgreYn, bizRegImgFile, estateRegImgFile, accsIp, userAgent );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * 2.3.6 회원 비밀번호 찾기 ( 변경, 이메일을 통한 가입자만 가능 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/resetUserPwd", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject resetUserPwd( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String email = GsntalkUtil.getString( param.get( "email" ) );
		String mobNo = GsntalkUtil.getString( param.get( "mobNo" ) );
		String vrfCnfrmToken = GsntalkUtil.getString( param.get( "vrfCnfrmToken" ) );
		String pwd = GsntalkUtil.getString( param.get( "pwd" ) );
		
		if(
			GsntalkUtil.isEmpty( email )
			||
			GsntalkUtil.isEmpty( mobNo )
			||
			GsntalkUtil.isEmpty( vrfCnfrmToken )
			||
			GsntalkUtil.isEmpty( pwd )
		) {
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER email : " + email );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER mobNo : " + mobNo );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER vrfCnfrmToken : " + vrfCnfrmToken );
			this.LOGGER.info( "MISSING_REQUIRED_PARAMETER pwd : " + pwd );
			
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( !GsntalkUtil.isEmailFormat( email ) ) {
			// 잘못된 이메일 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_EMAIL_FORMAT );
		}
		if( !GsntalkUtil.isMobnoFormat( mobNo ) ) {
			// 잘못된 휴대폰번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_NO_FORMAT );
		}
		if( !GsntalkUtil.isPasswordFormat( pwd ) ) {
			// 잘못된 비밀번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_PWD_FORMAT );
		}
		
		try {
			memberService.resetUserPwd( email, mobNo, vrfCnfrmToken, pwd );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * 2.3.7 FRT - 일반회원 / 중개회원 이메일로 로그인
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/frtLogin", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject frtLogin( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String accsIp = GsntalkUtil.getClientIpAddress( request );
		String userAgent = GsntalkUtil.getClientUserAgent( request );
		String email = GsntalkUtil.getString( param.get( "email" ) );
		String pwd = GsntalkUtil.getString( param.get( "pwd" ) );
		String keepLoginYn = GsntalkUtil.getString( param.get( "keepLoginYn" ) );
		
		if(
			GsntalkUtil.isEmpty( email )
			||
			GsntalkUtil.isEmpty( pwd )
		) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( GsntalkUtil.isEmpty( userAgent ) ) {
			// User-Agent를 확인할 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_USERAGENT );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			// FRT - 일반회원 / 중개회원 이메일로 로그인
			resMap = memberService.frtLogin( accsIp, userAgent, email, pwd, keepLoginYn );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkUtil.getString( resMap.get( "jwtToken" ) ), (JSONObject)resMap.get( "userItem" ) );
	}
	
	/**
	 * 2.3.8 FRT - 일반회원 / 중개회원 자동로그인
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/frtAutoLogin", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject frtAutoLogin( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String accsIp = GsntalkUtil.getClientIpAddress( request );
		String userAgent = GsntalkUtil.getClientUserAgent( request );
		String autoLoginToken = GsntalkUtil.getString( param.get( "autoLoginToken" ) );
		
		if( GsntalkUtil.isEmpty( autoLoginToken ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			// FRT - 일반회원 / 중개회원 이메일로 로그인
			resMap = memberService.frtAutoLogin( accsIp, userAgent, autoLoginToken );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkUtil.getString( resMap.get( "jwtToken" ) ), (JSONObject)resMap.get( "userItem" ) );
	}
	
	/**
	 * 2.3.9 FRT - 일반회원 SNS 로그인
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/frtSnsLogin", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject frtSnsLogin( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String accsIp = GsntalkUtil.getClientIpAddress( request );
		String userAgent = GsntalkUtil.getClientUserAgent( request );
		String snsGbCd = GsntalkUtil.getString( param.get( "snsGbCd" ) );
		String code = GsntalkUtil.getString( param.get( "code" ) );
		String state = GsntalkUtil.getString( param.get( "state" ) );
		
		if(
				GsntalkUtil.isEmpty( snsGbCd )
				||
				GsntalkUtil.isEmpty( code )
				||
				GsntalkUtil.isEmpty( state )
		) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( !GsntalkUtil.isIn( snsGbCd, GsntalkConstants.SNS_GB_KAKAO, GsntalkConstants.SNS_GB_NAVER ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			resMap = memberService.frtSnsLogin( snsGbCd, code, state, accsIp, userAgent );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkUtil.getString( resMap.get( "jwtToken" ) ), (JSONObject)resMap.get( "userItem" ) );
	}
	
	/**
	 * 2.3.10 중개사무소 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkOfcItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkOfcItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		
		long estBrkMemOfcSeqno = GsntalkUtil.getLong( param.get( "estBrkMemOfcSeqno" ) );
		if( estBrkMemOfcSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		JSONObject item = null;
		
		try {
			item = memberService.getEstBrkOfcItem( estBrkMemOfcSeqno );
			
			// 로그인 토큰 갱신
			if( memberVO != null ) {
				memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			}
		}catch( Exception e ) {
			throw e;
		}
		
		if( memberVO == null ) {
			return super.getItemResponse( item );
		}else {
			return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
		}
	}


	/**
	 * 2.3.11 Admin - 관리자 로그인
	 *
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/adminLogin", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject adminLogin( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String accsIp 		= GsntalkUtil.getClientIpAddress( request );
		String userAgent 	= GsntalkUtil.getClientUserAgent( request );
		String email 		= GsntalkUtil.getString( param.get( "email" ) );
		String pwd 			= GsntalkUtil.getString( param.get( "pwd" ) );
		String keepLoginYn 	= GsntalkUtil.getString( param.get( "keepLoginYn" ) );

		// Validation Check
		if( GsntalkUtil.isEmpty( email ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "email" );
		}
		if( GsntalkUtil.isEmpty( pwd ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "pwd" );
		}

		Map<String, Object> resMap = null;

		try {
			resMap = memberService.adminLogin( accsIp, userAgent, email, pwd, keepLoginYn );
		}catch( Exception e ) {
			throw e;
		}

		return super.getItemResponse( response, GsntalkUtil.getString( resMap.get( "jwtToken" ) ), (JSONObject)resMap.get( "userItem" ) );
	}

	/**
	 * 2.3.12 Admin - 관리자 자동 로그인
	 *
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/adminAutoLogin", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject adminAutoLogin( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String accsIp 			= GsntalkUtil.getClientIpAddress( request );
		String userAgent 		= GsntalkUtil.getClientUserAgent( request );
		String autoLoginToken 	= GsntalkUtil.getString( param.get( "autoLoginToken" ) );

		// Validation Check
		if( GsntalkUtil.isEmpty( autoLoginToken ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "autoLoginToken" );
		}

		Map<String, Object> resMap = null;

		try {
			resMap = memberService.adminAutoLogin( accsIp, userAgent, autoLoginToken );
		}catch( Exception e ) {
			throw e;
		}

		return super.getItemResponse( response, GsntalkUtil.getString( resMap.get( "jwtToken" ) ), (JSONObject)resMap.get( "userItem" ) );
	}

	/**
	 * 2.3.13 Admin - 중개사 회원 목록 조회  ( 페이징 )
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstateBrokerMemberItems", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getEstateBrokerMemberItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}


		// 파라메터 추출
		param = super.resetJSONObject( param );

		String viewTaget 				= GsntalkUtil.getString( param.get( "viewTaget" ) );		// 조회 대상 ( N : 승인대기 / Y : 승인완료 / D : 탈퇴 )
		String acntAprvStatCd 			= GsntalkUtil.getString( param.get( "acntAprvStatCd" ) );	// 계정승인상태코드( 승인대기 화면 전용 ) ( Y : 승인 / N : 대기 / D : 거부 )
		String actvStatGbCd 			= GsntalkUtil.getString( param.get( "actvStatGbCd" ) );		// 활동상태구분코드( 승인완료 화면 전용 ) (  NOR : 활동가능 / BLK : 활동중지 / WDR : 탈퇴처리 )
		String srchDateType 			= GsntalkUtil.getString( param.get( "srchDateType" ) );		// 기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )
		String srchVal 					= GsntalkUtil.getString( param.get( "srchVal" ) );

		JSONObject pageItem				= GsntalkUtil.getJSONObject( param, "pageItem" );
		int pageCnt						= GsntalkUtil.getInteger( pageItem.get( "pageCnt" ) );
		int nowPage						= GsntalkUtil.getInteger( pageItem.get( "nowPage" ) );
		int listPerPage					= GsntalkUtil.getInteger( pageItem.get( "listPerPage" ) );

		// default pageItem
		pageCnt 	= ( pageCnt < 5 ? 5 : pageCnt );
		nowPage 	= ( nowPage < 1 ? 1 : nowPage );
		listPerPage = ( listPerPage < 16 ? 16 : listPerPage );

		// Validation Check
		if( GsntalkUtil.isEmpty( viewTaget ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "viewTaget" );
		}

		// 잘못된 파라메터 값 ( viewTaget 가 NULL 이 아닌데 N Y D 가 아니면 )
		if( !GsntalkUtil.isIn( viewTaget, "N", "Y", "D") ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "viewTaget" );
		}

		// 잘못된 파라메터 값 ( srchDateType 가 NULL 이 아닌데 W M Y 가 아니면 )
		if( !GsntalkUtil.isEmpty( srchDateType ) && !GsntalkUtil.isIn( srchDateType, GsntalkConstants.SRCH_DATE_TYPE_WEEK, GsntalkConstants.SRCH_DATE_TYPE_MONTH, GsntalkConstants.SRCH_DATE_TYPE_YEAR ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
		}

		Map<String, Object>  resMap = null;

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 조회
			resMap = memberService.getEstateBrokerMemberItems( viewTaget, acntAprvStatCd, actvStatGbCd, srchDateType, srchVal, pageCnt, nowPage, listPerPage );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}

	/**
	 * 2.3.14 Admin - 중개사 회원관리 - 가입 승인
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateAprvEstateBrokerMember", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateAprvEstateBrokerMember( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}


		// 파라메터 추출
		long memSeqno 			= GsntalkUtil.getLong( param.get( "memSeqno" ) ); 	// 승인대상
		long aprvTreatMemSeqno 	= memberVO.getMemSeqno(); 							// 승인 처리자
		String acntAprvStatCd	= GsntalkUtil.getString( param.get( "acntAprvStatCd" ) ); // 승인 / 거부


		// Validation Check
		if( GsntalkUtil.isEmpty( memSeqno ) || GsntalkUtil.isEmpty( acntAprvStatCd ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}
		if( !GsntalkUtil.isIn( acntAprvStatCd , "Y", "D" ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "acntAprvStatCd" );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 승인처리
			memberService.updateAprvEstateBrokerMember( aprvTreatMemSeqno, memSeqno, acntAprvStatCd );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.15 Admin - 중개사/일반 회원관리 - 회원 상세 정보 조회
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMemberInfoItem", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getMemberInfoItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		// 파라메터 추출
		long memSeqno 			= GsntalkUtil.getLong( param.get( "memSeqno" ) ); 	// 조회 대상

		// Validation Check
		if( GsntalkUtil.isEmpty( memSeqno ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}

		Map<String, Object> resMap = null;

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 조회
			resMap = memberService.getMemberInfoItem( memSeqno );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), (JSONObject)resMap.get( "item" ) );
	}

	/**
	 * 2.3.16 Admin - 중개사 / 일반 회원관리 - 활동 상태 변경
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateActvStatMember", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateActvStatMember(  HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param  ) throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		long memSeqno 			= GsntalkUtil.getLong( param.get( "memSeqno" ) ); 			// 조회 대상
		String actvStatGbCd 	= GsntalkUtil.getString( param.get( "actvStatGbCd" ) );		// 활동상태구분코드 (  NOR : 활동가능 / BLK : 활동중지 / WDR : 탈퇴처리 )

		// Validation Check
		if( GsntalkUtil.isEmpty( memSeqno ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}
		if( GsntalkUtil.isEmpty( actvStatGbCd ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "actvStatGbCd" );
		}
		if( !GsntalkUtil.isIn( actvStatGbCd , "NOR", "BLK", "WDR" ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "actvStatGbCd" );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 상태 변경
			memberService.updateActvStatMember ( memSeqno, actvStatGbCd );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.17 Admin - 일반 회원 목록 조회  ( 페이징 )
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getNomalMemberItems", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getNormalMemberItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		// 파라메터 추출
		param = super.resetJSONObject( param );

		String delYn 					= GsntalkUtil.getString( param.get( "delYn" ) );
		String actvStatGbCd 			= GsntalkUtil.getString( param.get( "actvStatGbCd" ) );		// 활동상태구분코드 (  NOR : 활동가능 / BLK : 활동중지 / WDR : 탈퇴처리 )
		String srchDateType 			= GsntalkUtil.getString( param.get( "srchDateType" ) );		// 기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )
		String srchVal 					= GsntalkUtil.getString( param.get( "srchVal" ) );

		JSONObject pageItem				= GsntalkUtil.getJSONObject( param, "pageItem" );
		int pageCnt						= GsntalkUtil.getInteger( pageItem.get( "pageCnt" ) );
		int nowPage						= GsntalkUtil.getInteger( pageItem.get( "nowPage" ) );
		int listPerPage					= GsntalkUtil.getInteger( pageItem.get( "listPerPage" ) );

		// default pageItem
		pageCnt 	= ( pageCnt < 5 ? 5 : pageCnt );
		nowPage 	= ( nowPage < 1 ? 1 : nowPage );
		listPerPage = ( listPerPage < 16 ? 16 : listPerPage );

		// Validation Check
		if( GsntalkUtil.isEmpty( delYn ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "delYn" );
		}
		if( !GsntalkUtil.isIn( delYn, "N", "Y") ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "delYn" );
		}
		if( !GsntalkUtil.isEmpty( actvStatGbCd )  && !GsntalkUtil.isIn( actvStatGbCd, "NOR", "BLK", "WDR" ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "actvStatGbCd" );
		}
		// 잘못된 파라메터 값 ( srchDateType 가 NULL 이 아닌데 W M Y 가 아니면 )
		if( !GsntalkUtil.isEmpty( srchDateType ) && !GsntalkUtil.isIn( srchDateType, GsntalkConstants.SRCH_DATE_TYPE_WEEK, GsntalkConstants.SRCH_DATE_TYPE_MONTH, GsntalkConstants.SRCH_DATE_TYPE_YEAR ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
		}

		Map<String, Object>  resMap = null;
		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 조회
			resMap = memberService.getNormalMemberItems( delYn, actvStatGbCd, srchDateType, srchVal, pageCnt, nowPage, listPerPage );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}

	/**
	 * 2.3.18 내 정보 조회
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMyInfoItem", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getMyInfoItem(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}

		long memSeqno = memberVO.getMemSeqno();

		Map<String, Object> resMap = null;

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 조회
			resMap = memberService.getMemberInfoItem( memSeqno );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), (JSONObject)resMap.get( "item" ) );
	}

	/**
	 * 2.3.19 내 정보 수정
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateMyInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateMyInfo( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}

		// 파라메터 확인
		long memSeqno 	= memberVO.getMemSeqno();
		String memTypCd = memberVO.getMemTypCd();
		String mobNo 	= GsntalkUtil.getString( param.get( "mobNo" ) );
		String mobVrfNo = GsntalkUtil.getString( param.get( "mobVrfNo" ) );
		String memName 	= GsntalkUtil.getString( param.get( "memName" ) );

		// Validation Check
		if( GsntalkUtil.isEmpty( mobNo ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "mobNo" );
		}
		if( GsntalkUtil.isEmpty( mobVrfNo ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "mobVrfNo" );
		}

		mobNo = GsntalkUtil.parseNumberString( mobNo );

		// 잘못된 휴대폰번호 형식
		if( !GsntalkUtil.isMobnoFormat( mobNo ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_NO_FORMAT, "mobNo" );
		}

		// 잘못된 인증번호 형식
		if( !mobVrfNo.equals( GsntalkUtil.parseNumberString( mobVrfNo ) ) ||  mobVrfNo.length() != 6 ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_VRF_NO_FORMAT, "mobVrfNo" );
		}

		// 일반회원, 중개사 회원만 가능하도록
		if( !GsntalkUtil.isIn(memTypCd , "E", "N" )){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 인증 확인
			interfacesService.checkMobNoVerification( mobNo, mobVrfNo );

			// 수정 진행
			memberService.updateMyInfo( memSeqno, mobNo, mobVrfNo, memName );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.20 중개회원 중개사무소 정보 수정
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateOfcInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateOfcInfo( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}

		// 파라메터 확인
		long memSeqno 	= memberVO.getMemSeqno();
		String memTypCd = memberVO.getMemTypCd();
		String telNo 	= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "telNo" ) ) );

		// 중개사 회원이 아닐 경우 예외 처리
		if( !"E".equals( memTypCd ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
		}

		// 잘못된 전화번호 형식
		if( !GsntalkUtil.isTelFormat( telNo ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TEL_NO_FORMAT, "telNo" );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 수정
			memberService.updateOfcInfo( memSeqno, telNo );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.21 내 비밀번호 변경
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateMyPassowrd", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateMyPassowrd( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}

		// 파라메터 확인
		long memSeqno 	= memberVO.getMemSeqno();
		String email 	= memberVO.getEmail();

		String nowPwd 	= GsntalkUtil.getString( param.get( "nowPwd" ) );
		String newPwd 	= GsntalkUtil.getString( param.get( "newPwd" ) );

		// 잘못된 비밀번호 형식
		if( !GsntalkUtil.isPasswordFormat( newPwd ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_PWD_FORMAT, "newPwd" );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 비밀번호 수정
			memberService.updatePassowrd( memSeqno, email, nowPwd, newPwd );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.22 내 프로필 이미지 수정
	 * @param request
	 * @param response
	 * @param prflImg
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateMyPrflImg", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateMyPrflImg( HttpServletRequest request, HttpServletResponse response,@RequestParam( required = true, value="prflImg" ) MultipartFile prflImg  )throws Exception {
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}

		// 파라메터 확인
		long memSeqno 	= memberVO.getMemSeqno();

		// 필수 첨부파일 누락
		if( prflImg == null || prflImg.getSize() == 0L ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT, "prflImg" );
		}

		JSONObject uploadItem = null;

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 프로필 이미지 수정
			uploadItem = memberService.updateMemberPrflImg( memSeqno, prflImg );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ) , uploadItem );
	}
	
	/**
	 * 2.3.23 FRT - 관심매물 목록 조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getFavPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getFavPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		param = super.resetJSONObject( param );
		
		String prptTyp						= GsntalkUtil.getString( param.get( "prptTyp" ) );
		String tranTypGbCd					= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		String estateTypCd					= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		
		if( GsntalkUtil.isEmpty( prptTyp ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptTyp" );
		}
		if( !GsntalkUtil.isIn( prptTyp, "P", "S" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "prptTyp is not in P / S" );
		}
		
		JSONObject pageItem				= GsntalkUtil.getJSONObject( param, "pageItem" );
		int pageCnt						= GsntalkUtil.getInteger( pageItem.get( "pageCnt" ) );
		int nowPage						= GsntalkUtil.getInteger( pageItem.get( "nowPage" ) );
		int listPerPage					= GsntalkUtil.getInteger( pageItem.get( "listPerPage" ) );
		
		if( pageCnt < 5 ) {
			pageCnt = 5;				// default
		}
		if( nowPage < 1 ) {
			nowPage = 1;				// default
		}
		if( listPerPage < 16 ) {
			listPerPage = 16;			// default
		}
		
		Map<String, Object> resMap = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			resMap = memberService.getFavPropertyItems( memberVO.getMemSeqno(), prptTyp, tranTypGbCd, estateTypCd, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.3.24 FRT - 최근 본 매물 목록조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getRecentPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getRecentPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		param = super.resetJSONObject( param );
		
		String recentItems		= GsntalkUtil.getString( param.get( "recentItems" ) );
		if( GsntalkUtil.isEmpty( recentItems ) ) {
			recentItems = "[]";
		}
		
		JSONArray items = null;
		
		try {
			items = memberService.getRecentPropertyItems( memSeqno, (JSONArray)new JSONParser().parse( recentItems ) );
			
			// 로그인토큰 갱신
			if( memberVO != null ) {
				memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			}
		}catch( Exception e ) {
			throw e;
		}
		
		if( memberVO == null ) {
			return super.getItemsResponse( items );
		}else {
			return super.getItemsResponse( response, GsntalkJWTUtil.updateJWTToken( request ), items );
		}
	}

	/**
	 * 2.3.25 알림 목록 조회
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getNotificationItems", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getNotificationItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}

		long memSeqno = memberVO.getMemSeqno();
		String notiGbCd = GsntalkUtil.getString( param.get( "notiGbCd" ) );

		// Validation Check
		if( GsntalkUtil.isEmpty( notiGbCd ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "notiGbCd" );
		}
		if( !GsntalkUtil.isIn( notiGbCd, "NOTI", "PRPT", "SALES", "SCHDL" ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "notiGbCd" );
		}

		JSONObject item = null;

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 조회
			item = memberService.getNotificationItems( memSeqno, notiGbCd );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}

	/**
	 * 2.3.26 Admin - 일반 회원 목록 조회 - Excel Download
	 * @param request
	 * @param response
	 * @param param
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/downloadNormalMemberItems", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void downloadNormalMemberItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		String delYn 					= GsntalkUtil.getString( param.get( "delYn" ) );
		String actvStatGbCd 			= GsntalkUtil.getString( param.get( "actvStatGbCd" ) );		// 활동상태구분코드 (  NOR : 활동가능 / BLK : 활동중지 / WDR : 탈퇴처리 )
		String srchDateType 			= GsntalkUtil.getString( param.get( "srchDateType" ) );		// 기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )
		String srchVal 					= GsntalkUtil.getString( param.get( "srchVal" ) );

		// Validation Check
		if( GsntalkUtil.isEmpty( delYn ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "delYn" );
		}
		if( !GsntalkUtil.isIn( delYn, "N", "Y") ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "delYn" );
		}
		if( !GsntalkUtil.isEmpty( actvStatGbCd )  && !GsntalkUtil.isIn( actvStatGbCd, "NOR", "BLK", "WDR" ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "actvStatGbCd" );
		}


		// 잘못된 파라메터 값 ( srchDateType 가 NULL 이 아닌데 W M Y 가 아니면 )
		if( !GsntalkUtil.isEmpty( srchDateType ) && !GsntalkUtil.isIn( srchDateType, GsntalkConstants.SRCH_DATE_TYPE_WEEK, GsntalkConstants.SRCH_DATE_TYPE_MONTH, GsntalkConstants.SRCH_DATE_TYPE_YEAR ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
		}

		XSSFWorkbook wb = null;
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			wb = memberService.downloadNormalMemberItems( delYn, actvStatGbCd, srchDateType, srchVal );

			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}

		OutputStream out = null;
		try {
			response.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
			response.setHeader( "Content-Disposition", "attachment; filename=" + URLEncoder.encode( "일반회원_목록", "UTF-8" ) + "_" + GsntalkUtil.getServerTime( "%Y%m%d%H%i" ) + ".xlsx;" );

			out = response.getOutputStream();
			wb.write( out );
			out.flush();

		}catch( Exception e ) {
			throw e;
		}finally {
			if(out != null ) {
				try{ out.close(); }catch(Exception e){}
			}
		}
	}

	/**
	 * 2.3.27 Admin - 중개사 회원 목록 조회 - Excel Download
	 * @param request
	 * @param response
	 * @param param
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/downloadEstateBrokerMemberItems", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void downloadEstateBrokerMemberItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		// 파라메터 추출
		String viewTaget 				= GsntalkUtil.getString( param.get( "viewTaget" ) );		// 조회 대상 ( N : 승인대기 / Y : 승인완료 / D : 탈퇴 )
		String acntAprvStatCd 			= GsntalkUtil.getString( param.get( "acntAprvStatCd" ) );	// 계정승인상태코드( 승인대기 화면 전용 ) ( Y : 승인 / N : 대기 / D : 거부 )
		String actvStatGbCd 			= GsntalkUtil.getString( param.get( "actvStatGbCd" ) );		// 활동상태구분코드( 승인완료 화면 전용 ) (  NOR : 활동가능 / BLK : 활동중지 / WDR : 탈퇴처리 )
		String srchDateType 			= GsntalkUtil.getString( param.get( "srchDateType" ) );		// 기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )
		String srchVal 					= GsntalkUtil.getString( param.get( "srchVal" ) );

		// Validation Check
		if( GsntalkUtil.isEmpty( viewTaget ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "viewTaget" );
		}
		// 잘못된 파라메터 값 ( viewTaget 가 NULL 이 아닌데 N Y D 가 아니면 )
		if( !GsntalkUtil.isIn( viewTaget, "N", "Y", "D") ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "viewTaget" );
		}
		if( !GsntalkUtil.isEmpty( actvStatGbCd ) && !GsntalkUtil.isIn( actvStatGbCd, "NOR", "BLK", "WDR" ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "actvStatGbCd" );
		}
		// 잘못된 파라메터 값 ( srchDateType 가 NULL 이 아닌데 W M Y 가 아니면 )
		if( !GsntalkUtil.isEmpty( srchDateType ) && !GsntalkUtil.isIn( srchDateType, GsntalkConstants.SRCH_DATE_TYPE_WEEK, GsntalkConstants.SRCH_DATE_TYPE_MONTH, GsntalkConstants.SRCH_DATE_TYPE_YEAR ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "srchDateType" );
		}

		XSSFWorkbook wb = null;
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			wb = memberService.downloadEstateBrokerMemberItems( viewTaget, acntAprvStatCd, actvStatGbCd, srchDateType, srchVal );

			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}

		OutputStream out = null;
		try {
			response.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
			response.setHeader( "Content-Disposition", "attachment; filename=" + URLEncoder.encode( "중개사회원_목록", "UTF-8" ) + "_" + GsntalkUtil.getServerTime( "%Y%m%d%H%i" ) + ".xlsx;" );

			out = response.getOutputStream();
			wb.write( out );
			out.flush();

		}catch( Exception e ) {
			throw e;
		}finally {
			if(out != null ) {
				try{ out.close(); }catch(Exception e){}
			}
		}
	}

	/**
	 * 2.3.28 Admin - 공지 알림 등록
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerPublicNotification", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject registerPublicNotification( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		String notiTypGbCd 	= GsntalkUtil.getString( param.get( "notiTypGbCd" ) );
		String sendDt 		= GsntalkUtil.getString( param.get( "sendDt" ) );
		String notiDscr 	= GsntalkUtil.getString( param.get( "notiDscr" ) );

		if( GsntalkUtil.isEmpty( notiTypGbCd ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "notiTypGbCd" );
		}
		if( GsntalkUtil.isEmpty( sendDt ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "sendDt" );
		}
		if( GsntalkUtil.isEmpty( notiDscr ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "notiDscr" );
		}
		if( !GsntalkUtil.is8DateFormat( sendDt, true) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT );
		}
		if( !GsntalkUtil.isIn( notiTypGbCd, "E", "U", "S" ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "actvStatGbCd" );
		}
		if( GsntalkUtil.getInteger( GsntalkUtil.getServerTime( "%Y%m%d" ) ) >= GsntalkUtil.getInteger( GsntalkUtil.parseNumberString(sendDt) ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_VALID_DATE );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 등록
			memberService.registerPublicNotification( notiTypGbCd, sendDt, notiDscr );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.29 Admin - 공지 알림 삭제
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deletePublicNotification", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject deletePublicNotification( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		param                       		= super.resetJSONObject( param );
		JSONArray publicNotiSeqnoItems    	= GsntalkUtil.getJSONArray( param, "publicNotiSeqnoItems" );

		// 필수 요청파라메터 누락
		if( GsntalkUtil.isEmptyArray( publicNotiSeqnoItems ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "publicNotiSeqnoItems" );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 삭제
			memberService.deletePublicNotification( publicNotiSeqnoItems );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}


		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.30 Admin - 공지 알림 수정
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updatePublicNotification", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updatePublicNotification( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		long publicNotiSeqno = GsntalkUtil.getLong( param.get( "publicNotiSeqno") );
		String notiTypGbCd 	= GsntalkUtil.getString( param.get( "notiTypGbCd" ) );
		String sendDt 		= GsntalkUtil.getString( param.get( "sendDt" ) );
		String notiDscr 	= GsntalkUtil.getString( param.get( "notiDscr" ) );

		if( publicNotiSeqno == 0L ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "publicNotiSeqno" );
		}

		if( GsntalkUtil.isEmpty( notiTypGbCd ) && GsntalkUtil.isEmpty( sendDt ) && GsntalkUtil.isEmpty( notiDscr ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}

		if( !GsntalkUtil.isEmpty( notiTypGbCd )  && !GsntalkUtil.isIn( notiTypGbCd, "E", "U", "S" ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "actvStatGbCd" );
		}

		if( !GsntalkUtil.isEmpty( sendDt ) && GsntalkUtil.getInteger( GsntalkUtil.getServerTime( "%Y%m%d" ) ) >= GsntalkUtil.getInteger( GsntalkUtil.parseNumberString(sendDt) ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_VALID_DATE );
		}

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 수정
			memberService.updatePublicNotification( publicNotiSeqno, notiTypGbCd, sendDt, notiDscr );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}

	/**
	 * 2.3.31 Admin - 공지 알림 상세 조회
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getPublicNotificationItem", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getPublicNotificationItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception{
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}

		// 파라메터 추출
		long publicNotiSeqno 			= GsntalkUtil.getLong( param.get( "publicNotiSeqno" ) ); 	// 조회 대상

		// Validation Check
		if( GsntalkUtil.isEmpty( publicNotiSeqno ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "publicNotiSeqno" );
		}

		Map<String, Object> resMap = null;

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 조회
			resMap = memberService.getPublicNotificationItem( publicNotiSeqno );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), (JSONObject)resMap.get( "item" ) );
	}

	/**
	 * 2.3.32 Admin - 공지 알림 목록 조회 ( 페이징 )
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getPublicNotificationList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getPublicNotificationList( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		// JWT 검증
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		// 미 로그인 ( 또는 JWT 토큰 누락 )
		if( memberVO == null ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		// 관리자회원이 아님
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}


		// 파라메터 추출
		param = super.resetJSONObject( param );

		String srchDateType 			= GsntalkUtil.getString( param.get( "srchDateType" ) );		// 기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )
		String srchVal 					= GsntalkUtil.getString( param.get( "srchVal" ) );

		JSONObject pageItem				= GsntalkUtil.getJSONObject( param, "pageItem" );
		int pageCnt						= GsntalkUtil.getInteger( pageItem.get( "pageCnt" ) );
		int nowPage						= GsntalkUtil.getInteger( pageItem.get( "nowPage" ) );
		int listPerPage					= GsntalkUtil.getInteger( pageItem.get( "listPerPage" ) );

		// default pageItem
		pageCnt 	= ( pageCnt < 5 ? 5 : pageCnt );
		nowPage 	= ( nowPage < 1 ? 1 : nowPage );
		listPerPage = ( listPerPage < 16 ? 16 : listPerPage );

		// 잘못된 파라메터 값 ( srchDateType 가 NULL 이 아닌데 W M Y 가 아니면 )
		if( !GsntalkUtil.isEmpty( srchDateType ) && !GsntalkUtil.isIn( srchDateType, GsntalkConstants.SRCH_DATE_TYPE_WEEK, GsntalkConstants.SRCH_DATE_TYPE_MONTH, GsntalkConstants.SRCH_DATE_TYPE_YEAR ) ){
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
		}

		Map<String, Object>  resMap = null;

		try{
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

			// 조회
			resMap = memberService.getPublicNotificationList( srchDateType, srchVal, pageCnt, nowPage, listPerPage );

			// 로그인 토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch ( Exception e ){
			throw e;
		}

		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}

}