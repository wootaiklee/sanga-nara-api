package com.gsntalk.api.apis.asset;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gsntalk.api.apis.member.MemberService;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonController;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.config.GsntalkCORS;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.config.GsntalkJWTUtil;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

/**
 * API 2.10 Asset
 */
@Controller
@RequestMapping( value = "/asset" )
public class AssetController extends CommonController {

	@Autowired
	private AssetService assetService;
	
	@Autowired
	private MemberService memberService;
	
	public AssetController() {
		super( AssetController.class );
	}
	
	/**
	 * 2.10.1 FRT - 일반회원 자산 등록 1단계 ( 등록/수정 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerAssetStep1Item", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerAssetStep1Item( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno					= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		String estateTypGbCd			= GsntalkUtil.getString( param.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		String tmpAddrYn				= GsntalkUtil.getString( param.get( "tmpAddrYn" ) );
		String unregistYn				= GsntalkUtil.getString( param.get( "unregistYn" ) );
		String addr						= GsntalkUtil.getString( param.get( "addr" ) );
		String roadAddr					= GsntalkUtil.getString( param.get( "roadAddr" ) );
		String dtlAddr					= GsntalkUtil.getString( param.get( "dtlAddr" ) );
		double lat						= GsntalkUtil.getDouble( param.get( "lat" ) );
		double lng						= GsntalkUtil.getDouble( param.get( "lng" ) );
		double splyArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( param.get( "splyArea" ) ) );
		double prvArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( param.get( "prvArea" ) ) );
		double lndArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( param.get( "lndArea" ) ) );
		double totFlrArea				= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( param.get( "totFlrArea" ) ) );
		String inspGbCd					= GsntalkUtil.getString( param.get( "inspGbCd" ) );
		String bizmanGbCd				= GsntalkUtil.getString( param.get( "bizmanGbCd" ) );
		
		if( GsntalkUtil.isEmpty( estateTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypGbCd" );
		}
		if( !GsntalkUtil.isIn( estateTypGbCd, "COMMERCIAL", "REGIDENTAL", "PRESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypGbCd 값이 잘못됨  -> see [4. Common Codes - ESTATE_TYP_GB_CD]" );
		}
		if( GsntalkUtil.isEmpty( estateTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypCd" );
		}
		if( "COMMERCIAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "KOG", "STR", "BLD", "LND", "FTR" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨  -> see [4. Common Codes - ESTATE_TYP_CD - COMMERCIAL]" );
			}
		}else if( "REGIDENTAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "APT", "OFT", "SMH", "TWN", "SHC", "HUS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨  -> see [4. Common Codes - ESTATE_TYP_CD - REGIDENTAL]" );
			}
		}else {
			if( !GsntalkUtil.isIn( estateTypCd, "CPS", "SPS", "APS", "OPS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨  -> see [4. Common Codes - ESTATE_TYP_CD - PRESALE]" );
			}
		}
		if( GsntalkUtil.isEmpty( tmpAddrYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tmpAddrYn" );
		}
		if( !GsntalkUtil.isIn( tmpAddrYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tmpAddrYn 값이 잘못됨  -> is not in Y / N" );
		}
		
		if( "PRESALE".equals( estateTypGbCd ) ) {
			if( GsntalkUtil.isEmpty( unregistYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "분양권일때 unregistYn 필수" );
			}
			if( !GsntalkUtil.isIn( unregistYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "unregistYn 값이 잘못됨  -> is not in Y / N" );
			}
		}else {
			unregistYn = "";
		}
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr" );
		}
		if( GsntalkUtil.isEmpty( roadAddr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "roadAddr" );
		}
		if( lat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lat" );
		}
		if( lng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lng" );
		}
		
		/** 건물, 토지/임야, 단독공장이면 */
		if( GsntalkUtil.isIn( estateTypCd, "BLD", "LND", "FTR" ) ) {
			// 대지면적 검증
			if( lndArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lndArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			// 토지/임야가 아니면 연면적 추가 검증
			if( !"LND".equals( estateTypCd ) ) {
				if( totFlrArea == 0.0d ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "totFlrArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}else {
				// 토지/임야면 연멱적 없음
				totFlrArea = 0.0d;
			}
			
			// 단독공장이면 전용면적 추가 검증
			if( "FTR".equals( estateTypCd ) ) {
				if( prvArea == 0.0d ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prvArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}else {
				prvArea = 0.0d;
			}
			
			splyArea = 0.0d;
			
		/** 건물, 토지/임야, 단독공장이 아니면 */
		}else {
			// 공급면적, 전용면적 검증
			if( splyArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "splyArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( prvArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prvArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			lndArea = 0.0d;
			totFlrArea = 0.0d;
		}
		
		if( GsntalkUtil.isEmpty( inspGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "inspGbCd" );
		}
		if( !GsntalkUtil.isIn( inspGbCd, "S", "M" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "inspGbCd 값이 잘못됨  -> is not in S / M" );
		}
		if( GsntalkUtil.isEmpty( bizmanGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bizmanGbCd" );
		}
		if( !GsntalkUtil.isIn( bizmanGbCd, "PB", "B", "P" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "bizmanGbCd 값이 잘못됨  -> is not in PB / B / P" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.registerAssetStep1Item( memberVO.getMemSeqno(), assetSeqno, estateTypGbCd, estateTypCd, tmpAddrYn, unregistYn,
					addr, roadAddr, dtlAddr, lat, lng, splyArea, prvArea, lndArea, totFlrArea, inspGbCd, bizmanGbCd );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.2 FRT - 일반회원 자산 1단계 수정용 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getAssetStep1DtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getAssetStep1DtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno		= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		if( assetSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "assetSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.getAssetStep1DtlItem( memberVO.getMemSeqno(), assetSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.3 FRT - 일반회원 기존 자산 주소 중복 확인
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/checkForExistsAddress", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject checkForExistsAddress( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		String addr = GsntalkUtil.getString( param.get( "addr" ) );
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.checkForExistsAddress( memberVO.getMemSeqno(), addr );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.4 FRT - 일반회원 자산 등록 2단계 ( 등록/수정 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerAssetStep2Item", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerAssetStep2Item( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno					= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		String tranTypGbCd				= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		long dealAmt					= GsntalkUtil.getLong( param.get( "dealAmt" ) );
		String contDate					= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "contDate" ) ) );
		String registDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "registDate" ) ) );
		double acqstnTaxRatio			= GsntalkUtil.getDouble( param.get( "acqstnTaxRatio" ) );
		long etcCost					= GsntalkUtil.getLong( param.get( "etcCost" ) );
		int taxofcCost					= GsntalkUtil.getInteger( param.get( "taxofcCost" ) );
		int estFeeAmt					= GsntalkUtil.getInteger( param.get( "estFeeAmt" ) );
		long loanAmt					= GsntalkUtil.getLong( param.get( "loanAmt" ) );
		double loanIntrRatio			= GsntalkUtil.getDouble( param.get( "loanIntrRatio" ) );
		int loanMonTerm					= GsntalkUtil.getInteger( param.get( "loanMonTerm" ) );
		String loanDate					= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "loanDate" ) ) );
		String loanMthdGbCd				= GsntalkUtil.getString( param.get( "loanMthdGbCd" ) );
		
		if( assetSeqno == 0L && GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "수정시-assetSeqno or 등록시-regTmpKey 둘 중 하나는 필수" );
		}
		if( GsntalkUtil.isEmpty( tranTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranTypGbCd" );
		}
		if( !GsntalkUtil.isIn( tranTypGbCd, "TRADE", "PRESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranTypGbCd 값이 잘못됨  -> is not in TRADE / PRESALE" );
		}
		if( dealAmt == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealAmt" );
		}
		if( GsntalkUtil.isEmpty( contDate ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "contDate" );
		}
		if( !GsntalkUtil.is8DateFormat( contDate, true ) ) {
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "contDate" );
		}
		if( assetSeqno == 0L ) {
			registDate = "";
		}
		if( !GsntalkUtil.isEmpty( registDate ) && !GsntalkUtil.is8DateFormat( registDate, true ) ) {
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "registDate" );
		}
		if( acqstnTaxRatio == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "acqstnTaxRatio" );
		}
		if( loanMonTerm > 999 ) {
			// 허용 가능한 값 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_MAX_VALUE, "loanMonTerm value over than 999" );
		}
		if( !GsntalkUtil.isEmpty( loanDate ) && !GsntalkUtil.is8DateFormat( loanDate, true ) ) {
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "loanDate" );
		}
		if( !GsntalkUtil.isEmpty( loanMthdGbCd ) ) {
			if( !GsntalkUtil.isIn( loanMthdGbCd, "10", "20", "90" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "loanMthdGbCd 값이 잘못됨  -> is not in 10 / 20 / 90" );
			}
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.registerAssetStep2Item( memberVO.getMemSeqno(), assetSeqno, regTmpKey, tranTypGbCd, dealAmt, contDate, registDate,
					acqstnTaxRatio, etcCost, taxofcCost, estFeeAmt, loanAmt, loanIntrRatio, loanMonTerm, loanDate, loanMthdGbCd );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.5 FRT - 일반회원 자산 2단계 수정용 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getAssetStep2DtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getAssetStep2DtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno		= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		if( assetSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "assetSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.getAssetStep2DtlItem( memberVO.getMemSeqno(), assetSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.6 FRT - 일반회원 자산 등록 3단계 ( 등록/수정 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerAssetStep3Item", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerAssetStep3Item( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno					= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		String emptyTypGbCd				= GsntalkUtil.getString( param.get( "emptyTypGbCd" ) );
		String leseeNm					= GsntalkUtil.getString( param.get( "leseeNm" ) );
		String leseeTelNo				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "leseeTelNo" ) ) );
		String rentAmtPayMthdGbCd		= GsntalkUtil.getString( param.get( "rentAmtPayMthdGbCd" ) );
		long dpstAmt					= GsntalkUtil.getLong( param.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( param.get( "montRentAmt" ) );
		String monthlyPayDayGbCd		= GsntalkUtil.getString( param.get( "monthlyPayDayGbCd" ) );
		String rentContStDate			= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "rentContStDate" ) ) );
		String rentContEdDate			= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "rentContEdDate" ) ) );
		
		if( assetSeqno == 0L && GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "수정시-assetSeqno or 등록시-regTmpKey 둘 중 하나는 필수" );
		}
		if( GsntalkUtil.isEmpty( emptyTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "emptyTypGbCd" );
		}
		if( !GsntalkUtil.isIn( emptyTypGbCd, "E", "C", "N" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "emptyTypGbCd 값이 잘못됨  -> is not in E / C / N" );
		}
		
		// 공실유형이 계약중이면
		if( "C".equals( emptyTypGbCd ) ) {
			
			if( !GsntalkUtil.isEmpty( leseeTelNo ) && !GsntalkUtil.isTelFormat( leseeTelNo ) ) {
				// 잘못된 전화번호 형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TEL_NO_FORMAT, "leseeTelNo" );
			}
			if( !GsntalkUtil.isEmpty( rentAmtPayMthdGbCd ) && !GsntalkUtil.isIn( rentAmtPayMthdGbCd, "A", "F" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "rentAmtPayMthdGbCd 값이 잘못됨  -> is not in A / F" );
			}
			if( !GsntalkUtil.isEmpty( rentContStDate ) && !GsntalkUtil.is8DateFormat( rentContStDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "rentContStDate" );
			}
			if( !GsntalkUtil.isEmpty( rentContEdDate ) && !GsntalkUtil.is8DateFormat( rentContEdDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "rentContEdDate" );
			}
		}else {
			leseeNm = "";
			leseeTelNo = "";
			rentAmtPayMthdGbCd = "";
			dpstAmt = 0L;
			montRentAmt = 0;
			monthlyPayDayGbCd = "";
			rentContStDate = "";
			rentContEdDate = "";
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.registerAssetStep3Item( memberVO.getMemSeqno(), assetSeqno, regTmpKey, emptyTypGbCd, leseeNm, leseeTelNo,
					rentAmtPayMthdGbCd, dpstAmt, montRentAmt, monthlyPayDayGbCd, rentContStDate, rentContEdDate );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.7 FRT - 일반회원 자산 3단계 수정용 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getAssetStep3DtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getAssetStep3DtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno		= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		if( assetSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "assetSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.getAssetStep3DtlItem( memberVO.getMemSeqno(), assetSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.8 FRT - 일반회원 자산 등록 최종단계 ( 등록/수정 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param scFile
	 * @param brFile
	 * @param rcFile
	 * @param etFile
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerAssetFinalStepItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerAssetFinalStepItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="scFile" ) MultipartFile scFile, @RequestParam( required = false, value="brFile" ) MultipartFile brFile,
			@RequestParam( required = false, value="rcFile" ) MultipartFile rcFile, @RequestParam( required = false, value="etFile" ) MultipartFile etFile )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno					= GsntalkUtil.getLong( request.getParameter( "assetSeqno" ) );
		String regTmpKey				= GsntalkUtil.getString( request.getParameter( "regTmpKey" ) );
		String scFileDelYn				= GsntalkUtil.getString( request.getParameter( "scFileDelYn" ) );
		String brFileDelYn				= GsntalkUtil.getString( request.getParameter( "brFileDelYn" ) );
		String rcFileDelYn				= GsntalkUtil.getString( request.getParameter( "rcFileDelYn" ) );
		String etFileDelYn				= GsntalkUtil.getString( request.getParameter( "etFileDelYn" ) );
		
		if( assetSeqno == 0L && GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "수정시-assetSeqno or 등록시-regTmpKey 둘 중 하나는 필수" );
		}
		if( assetSeqno == 0L ) {
			scFileDelYn = GsntalkConstants.NO;
			brFileDelYn = GsntalkConstants.NO;
			rcFileDelYn = GsntalkConstants.NO;
			etFileDelYn = GsntalkConstants.NO;
		}else {
			if( !GsntalkConstants.YES.equals( scFileDelYn ) ) {
				scFileDelYn = GsntalkConstants.NO;
			}
			if( !GsntalkConstants.YES.equals( brFileDelYn ) ) {
				brFileDelYn = GsntalkConstants.NO;
			}
			if( !GsntalkConstants.YES.equals( rcFileDelYn ) ) {
				rcFileDelYn = GsntalkConstants.NO;
			}
			if( !GsntalkConstants.YES.equals( etFileDelYn ) ) {
				etFileDelYn = GsntalkConstants.NO;
			}
		}
		
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			assetService.registerAssetFinalStepItem( memberVO.getMemSeqno(), assetSeqno, regTmpKey, scFile, brFile, rcFile, etFile, scFileDelYn, brFileDelYn, rcFileDelYn, etFileDelYn );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.10.9 FRT - 일반회원 자산 최종단계 수정용 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getAssetFinalStepDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getAssetFinalStepDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno		= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		if( assetSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "assetSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.getAssetFinalStepDtlItem( memberVO.getMemSeqno(), assetSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.10 FRT - 일반회원 자산 요약정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getAssetSummaryItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getAssetSummaryItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.getAssetSummaryItem( memberVO.getMemSeqno() );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.11 FRT - 일반회원 내 자산 목록조회 
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/myAssetItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject myAssetItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		JSONArray items = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			items = assetService.myAssetItems( memberVO.getMemSeqno() );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( response, GsntalkJWTUtil.updateJWTToken( request ), items );
	}
	
	/**
	 * 2.10.12 FRT - 일반회원 내 자산 개별삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteAsset", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteAsset( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno		= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		if( assetSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "assetSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			assetService.deleteAsset( memberVO.getMemSeqno(), assetSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.10.13 FRT - 일반회원 내 자산 일괄삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteAssetByAddr", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteAssetByAddr( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		String addr		= GsntalkUtil.getString( param.get( "addr" ) );
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			assetService.deleteAssetByAddr( memberVO.getMemSeqno(), addr );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.10.14 FRT - 일반회원 내 자산 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMyAssetDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMyAssetDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_NORMAL_USER.equals( memberVO.getMemTypCd() ) ){
			// 일반회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_NORMAL_USER );
		}
		
		long assetSeqno		= GsntalkUtil.getLong( param.get( "assetSeqno" ) );
		if( assetSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "assetSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.getMyAssetDtlItem( memberVO.getMemSeqno(), assetSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.15 Admin - 일반회원 자산 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMemAssetItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMemAssetItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			// 관리자회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}
		
		String srchVal		= GsntalkUtil.getString( param.get( "srchVal" ) );
		
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
			
			resMap = assetService.getMemAssetItems( srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.10.16 Admin - 자산 일반회원 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getAssetMemDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getAssetMemDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			// 관리자회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}
		
		long memSeqno = GsntalkUtil.getLong( param.get( "memSeqno" ) );
		if( memSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = assetService.getAssetMemDtlItem( memSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.10.17 Admin - 일반회원 자산 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMemAssetDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMemAssetDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
			// 관리자회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
		}
		
		long memSeqno = GsntalkUtil.getLong( param.get( "memSeqno" ) );
		if( memSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			resMap = assetService.getMemAssetDtlItem( memSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
}