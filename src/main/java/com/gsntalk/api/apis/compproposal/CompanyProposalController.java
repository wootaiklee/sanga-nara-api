package com.gsntalk.api.apis.compproposal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * API 2.9 Company-Move Proposal
 */
@Controller
@RequestMapping( value = "/compproposal" )
public class CompanyProposalController extends CommonController {

	@Autowired
	private CompanyProposalService companyProposalService;
	
	@Autowired
	private MemberService memberService;
	
	public CompanyProposalController() {
		super( CompanyProposalController.class );
	}
	
	/**
	 * 2.9.1 신규기업 등록
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerNewCompanyItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerNewCompanyItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		String compNm				= GsntalkUtil.getString( param.get( "compNm" ) );
		String estBrkDispPosNm		= GsntalkUtil.getString( param.get( "estBrkDispPosNm" ) );
		
		if( GsntalkUtil.isEmpty( compNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compNm" );
		}
		if( GsntalkUtil.isEmpty( estBrkDispPosNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estBrkDispPosNm" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 신규기업 등록
			companyProposalService.registerNewCompanyItem( memberVO.getEstBrkMemOfcSeqno(), compNm, estBrkDispPosNm );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.9.2 기업명 수정
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateCompNm", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateCompNm( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno				= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		String compNm				= GsntalkUtil.getString( param.get( "compNm" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		if( GsntalkUtil.isEmpty( compNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compNm" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 기업명 수정
			companyProposalService.updateCompNm( memberVO.getEstBrkMemOfcSeqno(), compSeqno, compNm );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.9.3 기업 삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteCompanyItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteCompanyItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno = GsntalkUtil.getLong( param.get( "compSeqno" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 기업 삭제
			companyProposalService.deleteCompanyItem( memberVO.getEstBrkMemOfcSeqno(), compSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.9.4 기업 목록 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getCompItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getCompItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		JSONArray items = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 기업 목록 조회
			items = companyProposalService.getCompItems( memberVO.getEstBrkMemOfcSeqno() );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( response, GsntalkJWTUtil.updateJWTToken( request ), items );
	}
	
	/**
	 * 2.9.5 신규건물(매물) 추가 1단계 ( 등록/수정 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerBldStep1Item", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerBldStep1Item( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		long movPrpslPrptSeqno			= GsntalkUtil.getLong( param.get( "movPrpslPrptSeqno" ) );
		String estateTypGbCd			= GsntalkUtil.getString( param.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		String tmpAddrYn				= GsntalkUtil.getString( param.get( "tmpAddrYn" ) );
		String addr						= GsntalkUtil.getString( param.get( "addr" ) );
		String roadAddr					= GsntalkUtil.getString( param.get( "roadAddr" ) );
		String bldNm					= GsntalkUtil.getString( param.get( "bldNm" ) );
		double lat						= GsntalkUtil.getDouble( param.get( "lat" ) );
		double lng						= GsntalkUtil.getDouble( param.get( "lng" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		if( GsntalkUtil.isEmpty( estateTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypGbCd" );
		}
		if( !GsntalkUtil.isIn( estateTypGbCd, "COMMERCIAL", "REGIDENTAL" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypGbCd 값이 잘못됨  -> see [4. Common Codes - COMP_PRPSL_ESTATE_TYP_GB_CD]" );
		}
		if( GsntalkUtil.isEmpty( estateTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypCd" );
		}
		if( "COMMERCIAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "KWC", "STR", "OFC", "BLD", "FCG", "LND" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨  -> see [4. Common Codes - COMP_PRPSL_ESTATE_TYP_CD - COMMERCIAL]" );
			}
		}else {
			if( !GsntalkUtil.isIn( estateTypCd, "APT", "OFT", "SHC", "HUS", "SMH", "TWN" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨  -> see [4. Common Codes - COMP_PRPSL_ESTATE_TYP_CD - REGIDENTAL]" );
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
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr" );
		}
		if( GsntalkConstants.NO.equals( tmpAddrYn ) && GsntalkUtil.isEmpty( roadAddr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "roadAddr is required when tmpAddrYn is Y" );
		}
		if( GsntalkUtil.isEmpty( bldNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bldNm" );
		}
		if( lat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lat" );
		}
		if( lng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lng" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 신규건물(매물) 추가 1단계 ( 등록/수정 공통 )
			item = companyProposalService.registerBldStep1Item( memberVO.getMemSeqno(), memberVO.getEstBrkMemOfcSeqno(), compSeqno, movPrpslPrptSeqno, estateTypGbCd, estateTypCd, tmpAddrYn, addr, roadAddr, bldNm, lat, lng );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.9.6 이전제안 건물(매물) 1단계 수정용 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getBldStep1DtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getBldStep1DtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		long movPrpslPrptSeqno			= GsntalkUtil.getLong( param.get( "movPrpslPrptSeqno" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		if( movPrpslPrptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "movPrpslPrptSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 이전제안 건물(매물) 1단계 수정용 정보 조회
			item = companyProposalService.getBldStep1DtlItem( memberVO.getEstBrkMemOfcSeqno(), compSeqno, movPrpslPrptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.9.7 신규건물(매물) 추가 2단계 ( 등록/수정 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerBldStep2Item", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerBldStep2Item( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		long movPrpslPrptSeqno			= GsntalkUtil.getLong( param.get( "movPrpslPrptSeqno" ) );
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		String tranTypGbCd				= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		long salesCost					= GsntalkUtil.getLong( param.get( "salesCost" ) );
		long dpstAmt					= GsntalkUtil.getLong( param.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( param.get( "montRentAmt" ) );
		long prmmAmt					= GsntalkUtil.getLong( param.get( "prmmAmt" ) );
		double acqstnTaxRatio			= GsntalkUtil.getDouble( param.get( "acqstnTaxRatio" ) );
		long supprtAmt					= GsntalkUtil.getLong( param.get( "supprtAmt" ) );
		long etcCost					= GsntalkUtil.getLong( param.get( "etcCost" ) );
		double loanRatio1				= GsntalkUtil.getDouble( param.get( "loanRatio1" ) );
		double loanRatio2				= GsntalkUtil.getDouble( param.get( "loanRatio2" ) );
		double loanIntrRatio			= GsntalkUtil.getDouble( param.get( "loanIntrRatio" ) );
		String investYn					= GsntalkUtil.getString( param.get( "investYn" ) );
		long investDpstAmt				= GsntalkUtil.getLong( param.get( "investDpstAmt" ) );
		int investMontRentAmt			= GsntalkUtil.getInteger( param.get( "investMontRentAmt" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		if( movPrpslPrptSeqno == 0 && GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "수정시-movPrpslPrptSeqno or 등록시-regTmpKey 둘 중 하나는 필수" );
		}
		if( GsntalkUtil.isEmpty( tranTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranTypGbCd" );
		}
		if( !GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "MONTLY" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranTypGbCd 값이 잘못됨  -> is not in TRADE / CHARTER / MONTLY" );
		}
		
		// 매매유형 검증
		if( "TRADE".equals( tranTypGbCd ) ) {
			if( salesCost == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "[매매] 유형에서 salesCost 값 누락" );
			}
			if( acqstnTaxRatio == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "[매매] 유형에서 acqstnTaxRatio 값 누락" );
			}
			if( GsntalkUtil.isEmpty( investYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "[매매] 유형에서 investYn 값 누락" );
			}
			if( !GsntalkUtil.isIn( investYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "investYn 값이 잘못됨  -> is not in Y / N" );
			}
			if( loanRatio1 == loanRatio2 ) {
				// 두 값이 서로 동일함
				throw new GsntalkAPIException( GsntalkAPIResponse.BOTH_SAME_VALUES, "loanRatio1, loanRatio2 두 값은 서로 달라야 함." );
			}
			
			if( !GsntalkConstants.YES.equals( investYn ) ) {
				investDpstAmt = 0L;
				investMontRentAmt = 0;
			}
			
			dpstAmt = 0L;
			montRentAmt = 0;
			
		// 전세유형 검증
		}else if( "CHARTER".equals( tranTypGbCd ) ) {
			if( dpstAmt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "[전세] 유형에서 dpstAmt 값 누락" );
			}
			
			salesCost = 0L;
			montRentAmt = 0;
			prmmAmt = 0L;
			acqstnTaxRatio = 0.0d;
			supprtAmt = 0L;
			etcCost = 0L;
			loanRatio1 = 0.0d;
			loanRatio2 = 0.0d;
			loanIntrRatio = 0.0d;
			investYn = "";
			investDpstAmt = 0L;
			investMontRentAmt = 0;
			
		// 월세 검증
		}else {
			
			salesCost = 0L;
			prmmAmt = 0L;
			acqstnTaxRatio = 0.0d;
			supprtAmt = 0L;
			etcCost = 0L;
			loanRatio1 = 0.0d;
			loanRatio2 = 0.0d;
			loanIntrRatio = 0.0d;
			investYn = "";
			investDpstAmt = 0L;
			investMontRentAmt = 0;
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 신규건물(매물) 추가 2단계 ( 등록/수정 공통 )
			item = companyProposalService.registerBldStep2Item( memberVO.getMemSeqno(), memberVO.getEstBrkMemOfcSeqno(), compSeqno, movPrpslPrptSeqno, regTmpKey, tranTypGbCd, salesCost, dpstAmt, montRentAmt, prmmAmt,
					acqstnTaxRatio, supprtAmt, etcCost, loanRatio1, loanRatio2, loanIntrRatio, investYn, investDpstAmt, investMontRentAmt );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.9.8 이전제안 건물(매물) 2단계 수정용 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getBldStep2DtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getBldStep2DtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		long movPrpslPrptSeqno			= GsntalkUtil.getLong( param.get( "movPrpslPrptSeqno" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		if( movPrpslPrptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "movPrpslPrptSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 이전제안 건물(매물) 2단계 수정용 정보 조회
			item = companyProposalService.getBldStep2DtlItem( memberVO.getEstBrkMemOfcSeqno(), compSeqno, movPrpslPrptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.9.9 신규건물(매물) 추가 최종단계 ( 등록/수정 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerBldFinalStepItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerBldFinalStepItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		param = super.resetJSONObject( param );
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		long movPrpslPrptSeqno			= GsntalkUtil.getLong( param.get( "movPrpslPrptSeqno" ) );
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		double prvArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( param.get( "prvArea" ) ) );
		int flr							= GsntalkUtil.getInteger( param.get( "flr" ) );
		int allFlr						= GsntalkUtil.getInteger( param.get( "allFlr" ) );
		int monMntnceCost				= GsntalkUtil.getInteger( param.get( "monMntnceCost" ) );
		String psblMovDayTypCd			= GsntalkUtil.getString( param.get( "psblMovDayTypCd" ) );
		String psblMovDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "psblMovDate" ) ) );
		String heatKindGbCd				= GsntalkUtil.getString( param.get( "heatKindGbCd" ) );
		int parkingCarCnt				= GsntalkUtil.getInteger( param.get( "parkingCarCnt" ) );
		String compPrpslBldFacTypItems	= GsntalkUtil.getString( param.get( "compPrpslBldFacTypItems" ) );
		String bldSpclAdvtgDscr			= GsntalkUtil.getString( param.get( "bldSpclAdvtgDscr" ) );
		String reqDscr					= GsntalkUtil.getString( param.get( "reqDscr" ) );
		String outerTmpFileNm			= GsntalkUtil.getString( param.get( "outerTmpFileNm" ) );
		String innerTmpFileItems		= GsntalkUtil.getString( param.get( "innerTmpFileItems" ) );
		String delInnterFileURLItems	= GsntalkUtil.getString( param.get( "delInnterFileURLItems" ) );
		String modInnerFileItems		= GsntalkUtil.getString( param.get( "modInnerFileItems" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		if( movPrpslPrptSeqno == 0 && GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "수정시-movPrpslPrptSeqno or 등록시-regTmpKey 둘 중 하나는 필수" );
		}
		if( prvArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prvArea" );
		}
		if( flr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "flr" );
		}
		if( allFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "allFlr" );
		}
		if( GsntalkUtil.isEmpty( psblMovDayTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "psblMovDayTypCd" );
		}
		if( !GsntalkUtil.isIn( psblMovDayTypCd, "IMMDTLY", "DISCSN", "INPUT" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "psblMovDayTypCd 값이 잘못됨  -> see [4. Common Codes - PSBL_MOV_DAY_TYP_CD]" );
		}
		if( "INPUT".equals( psblMovDayTypCd ) ) {
			if( GsntalkUtil.isEmpty( psblMovDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "psblMovDate is empty, psblMovDayTypCd value is INPUT" );
			}
			if( GsntalkUtil.is8DateFormat( psblMovDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "psblMovDate 값 잘못됨" );
			}
		} else {
			psblMovDate = "";
		}
		if( !GsntalkUtil.isEmpty( heatKindGbCd ) && !GsntalkUtil.isIn( heatKindGbCd, "PRVT", "CNTR", "AREA" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "heatKindGbCd 값이 잘못됨  -> see [4. Common Codes - HEAT_KIND_GB_CD]" );
		}
		if( GsntalkUtil.isEmpty( compPrpslBldFacTypItems ) ) {
			compPrpslBldFacTypItems = "[]";
		}
		if( GsntalkUtil.isEmpty( innerTmpFileItems ) ) {
			innerTmpFileItems = "[]";
		}
		if( GsntalkUtil.isEmpty( delInnterFileURLItems ) ) {
			delInnterFileURLItems = "[]";
		}
		if( GsntalkUtil.isEmpty( modInnerFileItems ) ) {
			modInnerFileItems = "[]";
		}
		
		// 등록루틴
		if( movPrpslPrptSeqno == 0L ) {
			if( GsntalkUtil.isEmpty( outerTmpFileNm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "outerTmpFileNm" );
			}
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			JSONParser parser = new JSONParser();
			
			// 신규건물(매물) 추가 최종단계 ( 등록/수정 공통 )
			companyProposalService.registerBldFinalStepItem( memberVO.getMemSeqno(), memberVO.getEstBrkMemOfcSeqno(), compSeqno, movPrpslPrptSeqno, regTmpKey, prvArea, flr, allFlr, monMntnceCost, psblMovDayTypCd, psblMovDate,
					heatKindGbCd, parkingCarCnt, (JSONArray)parser.parse( compPrpslBldFacTypItems ), bldSpclAdvtgDscr, reqDscr, outerTmpFileNm, (JSONArray)parser.parse( innerTmpFileItems ), (JSONArray)parser.parse( delInnterFileURLItems ), (JSONArray)parser.parse( modInnerFileItems ) );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.9.10 이전제안 건물(매물) 최종단계 수정용 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getBldFinalStepDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getBldFinalStepDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		long movPrpslPrptSeqno			= GsntalkUtil.getLong( param.get( "movPrpslPrptSeqno" ) );
		
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		if( movPrpslPrptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "movPrpslPrptSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 이전제안 건물(매물) 최종단계 수정용 정보 조회
			item = companyProposalService.getBldFinalStepDtlItem( memberVO.getEstBrkMemOfcSeqno(), compSeqno, movPrpslPrptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.9.11 기업 이전 제안 정보조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getCompMovPrpslItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getCompMovPrpslItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = companyProposalService.getCompMovPrpslItem( memberVO.getEstBrkMemOfcSeqno(), compSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.9.12 기업이전 제안 건물 삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteCompPrpslBldItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteCompPrpslBldItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memberVO.getMemTypCd() ) ){
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER );
		}
		
		long movPrpslPrptSeqno					= GsntalkUtil.getLong( param.get( "movPrpslPrptSeqno" ) );
		if( movPrpslPrptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "movPrpslPrptSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			companyProposalService.deleteCompPrpslBldItem( memberVO.getEstBrkMemOfcSeqno(), movPrpslPrptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.9.13 Admin - 회원별 기업이전 제안서 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMemCompPrpslItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMemCompPrpslItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		param = super.resetJSONObject( param );
		
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) ).replaceAll( "-", "" );
		
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
			
			resMap = companyProposalService.getMemCompPrpslItems( srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.9.14 Admin - 회원 기업이전 제안 작성 목록조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMemCompPrpslWrtItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMemCompPrpslWrtItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno					= GsntalkUtil.getLong( param.get( "memSeqno" ) );
		if( memSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}
		
		JSONArray items = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			items = companyProposalService.getMemCompPrpslWrtItems( memSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( response, GsntalkJWTUtil.updateJWTToken( request ), items );
	}
	
	/**
	 * 2.9.15 기업이전 제안서 PDF 견적서용 정보 조회 ( 관리자, 중개회원 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getCompMovPrpslQuoteItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getCompMovPrpslQuoteItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}		
		if( !GsntalkUtil.isIn( memberVO.getMemTypCd(), GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT, GsntalkConstants.MEM_TYP_CD_ADMIN_USER ) ){
			// 조회할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_SEARCH );
		}
		
		long compSeqno					= GsntalkUtil.getLong( param.get( "compSeqno" ) );
		if( compSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compSeqno" );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			resMap = companyProposalService.getCompMovPrpslQuoteItem( memberVO.getMemTypCd(), memberVO.getEstBrkMemOfcSeqno(), compSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
}