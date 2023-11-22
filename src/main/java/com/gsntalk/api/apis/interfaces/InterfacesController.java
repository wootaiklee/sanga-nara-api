package com.gsntalk.api.apis.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonController;
import com.gsntalk.api.config.GsntalkCORS;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

/**
 * API 2.1 Interfaces
 */
@Controller
@RequestMapping( value = "/interfaces" )
public class InterfacesController extends CommonController {

	@Autowired
	private InterfacesService interfacesService;
	
	public InterfacesController() {
		super( InterfacesController.class );
	}
	
	/**
	 * 2.1.1 휴대폰번호 본인확인용 인증번호 알림톡 or SMS 발송 ( 재전송 공용 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/sendMobNoVerification", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject sendMobNoVerification( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String mobNo = GsntalkUtil.getString( param.get( "mobNo" ) );
		String email = GsntalkUtil.getString( param.get( "email" ) );
		
		if( GsntalkUtil.isEmpty( mobNo ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		mobNo = GsntalkUtil.parseNumberString( mobNo );
		
		if( !GsntalkUtil.isMobnoFormat( mobNo ) ) {
			// 잘못된 휴대폰번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_NO_FORMAT );
		}
		
		if( !GsntalkUtil.isEmpty( email ) && !GsntalkUtil.isEmailFormat( email ) ) {
			// 잘못된 이메일 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_EMAIL_FORMAT );
		}
		
		try {
			interfacesService.sendMobNoVerification( mobNo, email );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * 2.1.2 휴대폰 본인인증 번호 검증
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@SuppressWarnings( "unchecked" )
	@PostMapping( value = "/checkMobNoVerification", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject checkMobNoVerification( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String mobNo = GsntalkUtil.getString( param.get( "mobNo" ) );
		String mobVrfNo = GsntalkUtil.getString( param.get( "mobVrfNo" ) );
		
		if( GsntalkUtil.isEmpty( mobNo ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		mobNo = GsntalkUtil.parseNumberString( mobNo );
		
		if( !GsntalkUtil.isMobnoFormat( mobNo ) ) {
			// 잘못된 휴대폰번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_NO_FORMAT );
		}
		
		if( GsntalkUtil.isEmpty( mobVrfNo ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		if( !mobVrfNo.equals( GsntalkUtil.parseNumberString( mobVrfNo ) ) ) {
			// 잘못된 인증번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_VRF_NO_FORMAT );
		}
		if( mobVrfNo.length() != 6 ) {
			// 잘못된 인증번호 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MOB_VRF_NO_FORMAT );
		}
		
		String vrfCnfrmToken = null;
		
		try {
			vrfCnfrmToken = interfacesService.checkMobNoVerification( mobNo, mobVrfNo );
			
		}catch( Exception e ) {
			throw e;
		}
		
		JSONObject item = new JSONObject();
		item.put( "vrfCnfrmToken", vrfCnfrmToken );
		
		return super.getItemResponse( item );
	}
	
	/**
	 * 2.1.3 사업자 진위여부 확인 및 상태조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/checkBizValidation", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject checkBizValidation( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String bizNo = GsntalkUtil.getString( param.get( "bizNo" ) );
		String openRegDate = GsntalkUtil.getString( param.get( "openRegDate" ) );
		String reprNm = GsntalkUtil.getString( param.get( "reprNm" ) );
		
		if(
			GsntalkUtil.isEmpty( bizNo )
			||
			GsntalkUtil.isEmpty( openRegDate )
			||
			GsntalkUtil.isEmpty( reprNm )
		) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		JSONObject item = null;
		
		try {
			item = interfacesService.checkBizValidation( bizNo, openRegDate, reprNm );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( item );
	}
}