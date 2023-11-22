package com.gsntalk.api.apis.suggstnsales;

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
 * API 2.8 Suggestion Sales
 */
@Controller
@RequestMapping( value = "/suggestionSales" )
public class SuggestionSalesController extends CommonController {

	@Autowired
	private SuggestionSalesService suggestionSalesService;
	
	@Autowired
	private MemberService memberService;
	
	public SuggestionSalesController() {
		super( SuggestionSalesController.class );
	}
	
	/**
	 * 2.8.1 Admin - 추천분양 신규등록 - 1단계 임시저장 ( 등록전용 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerSuggstnSalesStep1RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerSuggstnSalesStep1RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno = memberVO.getMemSeqno();
		
		String baseItem					= GsntalkUtil.getString( param.get( "baseItem" ) );
		String dongItems				= GsntalkUtil.getString( param.get( "dongItems" ) );
		String eduTmpFileNmItems		= GsntalkUtil.getString( param.get( "eduTmpFileNmItems" ) );
		
		if( GsntalkUtil.isEmpty( baseItem ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem" );
		}
		if( GsntalkUtil.isEmpty( dongItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems" );
		}
		if( GsntalkUtil.isEmpty( eduTmpFileNmItems ) ) {
			eduTmpFileNmItems = "[]";
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			JSONParser parser = new JSONParser();
			
			// Admin - 추천분양 신규등록 - 1단계 임시저장 ( 등록전용 )
			item = suggestionSalesService.registerSuggstnSalesStep1RegItem( memSeqno, (JSONObject)parser.parse( baseItem ), (JSONArray)parser.parse( dongItems ), (JSONArray)parser.parse( eduTmpFileNmItems ) );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.8.2 Admin - 추천분양 신규등록 - 2단계 최종 등록 ( 등록전용 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerSuggstnSalesFinalStepRegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerSuggstnSalesFinalStepRegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno = memberVO.getMemSeqno();
		
		String regTmpKey					= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		String prmmItems					= GsntalkUtil.getString( param.get( "prmmItems" ) );
		String salesSchdlItems				= GsntalkUtil.getString( param.get( "salesSchdlItems" ) );
		
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		if( GsntalkUtil.isEmpty( prmmItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems" );
		}
		if( GsntalkUtil.isEmpty( salesSchdlItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			JSONParser parser = new JSONParser();
			
			// Admin - 추천분양 신규등록 - 2단계 최종 등록 ( 등록전용 )
			suggestionSalesService.registerSuggstnSalesFinalStepRegItem( memSeqno, regTmpKey, (JSONArray)parser.parse( prmmItems ), (JSONArray)parser.parse( salesSchdlItems ) );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.3 Admin - 추천분양 매물 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getSuggstnSalesPrptListItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getSuggstnSalesPrptListItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String suggstnSalesRegionGbCd		= GsntalkUtil.getString( param.get( "suggstnSalesRegionGbCd" ) );
		String poStatGbCd					= GsntalkUtil.getString( param.get( "poStatGbCd" ) );
		String srchVal						= GsntalkUtil.getString( param.get( "srchVal" ) );
		
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
			
			resMap = suggestionSalesService.getSuggstnSalesPrptListItems( suggstnSalesRegionGbCd, poStatGbCd, srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	
	/**
	 * 2.8.4 Admin - 추천분양 분양상태 업데이트
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateSuggstnSalesStatItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateSuggstnSalesStatItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long suggstnSalesSeqno				= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		String poStatGbCd					= GsntalkUtil.getString( param.get( "poStatGbCd" ) );
		
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		if( GsntalkUtil.isEmpty( poStatGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "poStatGbCd" );
		}
		if( !GsntalkUtil.isIn( poStatGbCd, "E", "I" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "poStatGbCd is not in E / I" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			suggestionSalesService.updateSuggstnSalesStatItem( suggstnSalesSeqno, poStatGbCd );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.5 Admin - 추천분양 삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteSuggstnSalesItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteSuggstnSalesItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		JSONArray suggstnSalesSeqnotems = GsntalkUtil.getJSONArray( param, "suggstnSalesSeqnotems" );
		if( GsntalkUtil.isEmptyArray( suggstnSalesSeqnotems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqnotems" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			suggestionSalesService.deleteSuggstnSalesItem( suggstnSalesSeqnotems );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.6 Admin - 추천분양 1단계 등록정보 조회 ( 수정용 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getSuggstnSalesStep1RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getSuggstnSalesStep1RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long suggstnSalesSeqno				= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		
		Map<String, Object> resMap = null;
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			resMap = suggestionSalesService.getSuggstnSalesStep1RegItem( suggstnSalesSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.8.7 Admin - 추천분양 최종단계 등록정보 조회 ( 수정용 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getSuggstnSalesFinalStepRegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getSuggstnSalesFinalStepRegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long suggstnSalesSeqno				= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		
		Map<String, Object> resMap = null;
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			resMap = suggestionSalesService.getSuggstnSalesFinalStepRegItem( suggstnSalesSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.8.8 Admin - 추천분양 1단계 수정
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateSuggstnSalesStep1Item", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateSuggstnSalesStep1Item( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long suggstnSalesSeqno			= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		String baseItem					= GsntalkUtil.getString( param.get( "baseItem" ) );
		String delDongSeqnoItems		= GsntalkUtil.getString( param.get( "delDongSeqnoItems" ) );
		String dongItems				= GsntalkUtil.getString( param.get( "dongItems" ) );
		String eduTmpFileNmItems		= GsntalkUtil.getString( param.get( "eduTmpFileNmItems" ) );
		String delEduFileUrls			= GsntalkUtil.getString( param.get( "delEduFileUrls" ) );
		
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		if( GsntalkUtil.isEmpty( baseItem ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem" );
		}
		if( GsntalkUtil.isEmpty( delDongSeqnoItems ) ) {
			delDongSeqnoItems = "[]";
		}
		if( GsntalkUtil.isEmpty( dongItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems" );
		}
		if( GsntalkUtil.isEmpty( eduTmpFileNmItems ) ) {
			eduTmpFileNmItems = "[]";
		}
		if( GsntalkUtil.isEmpty( delEduFileUrls ) ) {
			delEduFileUrls = "[]";
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			JSONParser parser = new JSONParser();
			
			// Admin - 추천분양 1단계 수정
			suggestionSalesService.updateSuggstnSalesStep1Item( suggstnSalesSeqno, (JSONObject)parser.parse( baseItem ), (JSONArray)parser.parse( delDongSeqnoItems ), (JSONArray)parser.parse( dongItems ), (JSONArray)parser.parse( eduTmpFileNmItems ), (JSONArray)parser.parse( delEduFileUrls ) );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.9 Admin - 추천분양 최종단계 수정 ( ※ 모두 재등록 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateSuggstnSalesFinalStepItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateSuggstnSalesFinalStepItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long suggstnSalesSeqno				= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		String prmmItems					= GsntalkUtil.getString( param.get( "prmmItems" ) );
		String salesSchdlItems				= GsntalkUtil.getString( param.get( "salesSchdlItems" ) );
		
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		if( GsntalkUtil.isEmpty( prmmItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems" );
		}
		if( GsntalkUtil.isEmpty( salesSchdlItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			JSONParser parser = new JSONParser();
			
			// Admin - 추천분양 신규등록 - 2단계 최종 등록 ( 등록전용 )
			suggestionSalesService.updateSuggstnSalesFinalStepItem( suggstnSalesSeqno, (JSONArray)parser.parse( prmmItems ), (JSONArray)parser.parse( salesSchdlItems ) );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.10 FRT - 실시간 분양현장 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getRealtimeSalesItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getRealtimeSalesItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		param = super.resetJSONObject( param );
		
		String poStatGbCd				= GsntalkUtil.getString( param.get( "poStatGbCd" ) );
		String suggstnSalesRegionGbCd	= GsntalkUtil.getString( param.get( "suggstnSalesRegionGbCd" ) );
		
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
			resMap = suggestionSalesService.getRealtimeSalesItems( memSeqno, poStatGbCd, suggstnSalesRegionGbCd, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			if( memberVO != null ) {
				memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			}
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.8.11 FRT - 관심 추천분양 등록
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerFavSuggstnSales", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerFavSuggstnSales( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		long suggstnSalesSeqno				= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			suggestionSalesService.registerFavSuggstnSales( memberVO.getMemSeqno(), suggstnSalesSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.12 FRT - 관심 추천분양 해제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/releaseFavSuggstnSales", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject releaseFavSuggstnSales( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		long suggstnSalesSeqno				= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			suggestionSalesService.releaseFavSuggstnSales( memberVO.getMemSeqno(), suggstnSalesSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.13 FRT - 실시간 분양현장 매물 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getRealtimeSalesDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getRealtimeSalesDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		long suggstnSalesSeqno				= GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			resMap = suggestionSalesService.getRealtimeSalesDtlItem( memSeqno, suggstnSalesSeqno );
			
			// 로그인토큰 갱신
			if( memberVO != null ) {
				memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			}
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.8.14 FRT - 분양알림 설정
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/setSalesSchdlNoti", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject setSalesSchdlNoti( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
		}
		
		long suggstnSalesSeqno = GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			suggestionSalesService.setSalesSchdlNoti( memberVO.getMemSeqno(), suggstnSalesSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.8.15 FRT - 분양알림 해제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/releaseSalesSchdlNoti", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject releaseSalesSchdlNoti( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
		}
		
		long suggstnSalesSeqno = GsntalkUtil.getLong( param.get( "suggstnSalesSeqno" ) );
		if( suggstnSalesSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			suggestionSalesService.releaseSalesSchdlNoti( memberVO.getMemSeqno(), suggstnSalesSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
}