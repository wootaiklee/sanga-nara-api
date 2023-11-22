package com.gsntalk.api.apis.knwldgIndCmplx;

import java.util.ArrayList;
import java.util.List;
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
 * API 2.5 KnwldgIndCmplx
 */
@Controller
@RequestMapping( value = "/knwldgIndCmplx" )
public class KnwldgIndCmplxController extends CommonController {

	@Autowired
	private KnwldgIndCmplxService knwldgIndCmplxService;
	
	@Autowired
	private MemberService memberService;
	
	public KnwldgIndCmplxController() {
		super( KnwldgIndCmplxController.class );
	}
	
	/**
	 * 2.6.1 지식산업센터 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getKnwldgIndCmplxDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getKnwldgIndCmplxDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		long knwldgIndCmplxSeqno		= GsntalkUtil.getLong( param.get( "knwldgIndCmplxSeqno" ) );
		
		JSONObject item = null;
		
		try {
			item = knwldgIndCmplxService.getKnwldgIndCmplxDtlItem( memSeqno, knwldgIndCmplxSeqno );
			
			// 로그인토큰 갱신
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
	 * 2.6.2 Admin - 지식산업센터 등록 
	 * 
	 * @param request
	 * @param response
	 * @param vwmapFile
	 * @param featrFiles
	 * @param frmapFiles
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerKnwldgIndCmplxItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerKnwldgIndCmplxItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = true, value="vwmapFile" ) MultipartFile vwmapFile, @RequestParam( required = false, value="featrFiles" ) List<MultipartFile> featrFiles, @RequestParam( required = false, value="frmapFiles" ) List<MultipartFile> frmapFiles )throws Exception {
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
		long memSeqno = memberVO.getMemSeqno();
		
		String addr						= GsntalkUtil.getString( request.getParameter( "addr" ) );
		String addrShortNm				= GsntalkUtil.getString( request.getParameter( "addrShortNm" ) );
		double lat						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( request.getParameter( "lat" ) ) );
		double lng						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( request.getParameter( "lng" ) ) );
		String bldNm					= GsntalkUtil.getString( request.getParameter( "bldNm" ) );
		String cmpltnDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( request.getParameter( "cmpltnDate" ) ) );
		long askSalesMinPrc				= GsntalkUtil.getLong( request.getParameter( "askSalesMinPrc" ) ) * 10000;
		long askSalesAvgPrc				= GsntalkUtil.getLong( request.getParameter( "askSalesAvgPrc" ) ) * 10000;
		long askSalesMaxPrc				= GsntalkUtil.getLong( request.getParameter( "askSalesMaxPrc" ) ) * 10000;
		long askLeaseMinPrc				= GsntalkUtil.getLong( request.getParameter( "askLeaseMinPrc" ) ) * 10000;
		long askLeaseAvgPrc				= GsntalkUtil.getLong( request.getParameter( "askLeaseAvgPrc" ) ) * 10000;
		long askLeaseMaxPrc				= GsntalkUtil.getLong( request.getParameter( "askLeaseMaxPrc" ) ) * 10000;
		double lndArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( request.getParameter( "lndArea" ) ) );
		double bldArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( request.getParameter( "bldArea" ) ) );
		double totFlrArea				= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( request.getParameter( "totFlrArea" ) ) );
		int minFlr						= GsntalkUtil.getInteger( request.getParameter( "minFlr" ) );
		int maxFlr						= GsntalkUtil.getInteger( request.getParameter( "maxFlr" ) );
		int parkingCarCnt				= GsntalkUtil.getInteger( request.getParameter( "parkingCarCnt" ) );
		int husHoldCnt					= GsntalkUtil.getInteger( request.getParameter( "husHoldCnt" ) );
		String devCompNm				= GsntalkUtil.getString( request.getParameter( "devCompNm" ) );
		String constCompNm				= GsntalkUtil.getString( request.getParameter( "constCompNm" ) );
		String trfcInfo					= GsntalkUtil.getString( request.getParameter( "trfcInfo" ) );
		String siteExplntn				= GsntalkUtil.getString( request.getParameter( "siteExplntn" ) );
		String smplSmrDscr				= GsntalkUtil.getString( request.getParameter( "smplSmrDscr" ) );
		
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr" );
		}
		if( GsntalkUtil.isEmpty( addrShortNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addrShortNm" );
		}
		if( lat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lat" );
		}
		if( lng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lng" );
		}
		if( GsntalkUtil.isEmpty( bldNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bldNm" );
		}
		if( GsntalkUtil.isEmpty( cmpltnDate ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "cmpltnDate" );
		}
		if( !GsntalkUtil.is8DateFormat( cmpltnDate, true ) ) {
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "cmpltnDate" );
		}
		if( askSalesMinPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askSalesMinPrc" );
		}
		if( askSalesAvgPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askSalesAvgPrc" );
		}
		if( askSalesMaxPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askSalesMaxPrc" );
		}
		if( askLeaseMinPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askLeaseMinPrc" );
		}
		if( askLeaseAvgPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askLeaseAvgPrc" );
		}
		if( askLeaseMaxPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askLeaseMaxPrc" );
		}
		if( lndArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lndArea" );
		}
		if( bldArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bldArea" );
		}
		if( totFlrArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "totFlrArea" );
		}
		if( minFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "minFlr" );
		}
		if( maxFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "maxFlr" );
		}
		if( parkingCarCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "parkingCarCnt" );
		}
		if( husHoldCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "husHoldCnt" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			knwldgIndCmplxService.registerKnwldgIndCmplxItem( memSeqno, addr, addrShortNm, lat, lng, bldNm, cmpltnDate, askSalesMinPrc, askSalesAvgPrc, askSalesMaxPrc, askLeaseMinPrc, askLeaseAvgPrc, askLeaseMaxPrc,
						lndArea, bldArea, totFlrArea, minFlr, maxFlr, parkingCarCnt, husHoldCnt, devCompNm, constCompNm, trfcInfo, siteExplntn, smplSmrDscr, vwmapFile, featrFiles, frmapFiles );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.6.3 Admin - 지식산업센터 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getKnwldgIndCmplxItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getKnwldgIndCmplxItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String regDtSrchTyp				= GsntalkUtil.getString( param.get( "regDtSrchTyp" ) );
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) );
		
		if( !GsntalkUtil.isEmpty( regDtSrchTyp ) && !GsntalkUtil.isIn( regDtSrchTyp, "W", "M", "Y" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "regDtSrchTyp 값이 잘못됨  -> is not in [ W, M, Y ]" );
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
			
			resMap = knwldgIndCmplxService.getKnwldgIndCmplxItems( regDtSrchTyp, srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.6.4 Admin - 지식산업센터 삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteKnwldgIndCmplxItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteKnwldgIndCmplxItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		JSONArray knwldgIndCmplxSeqnoItems = GsntalkUtil.getJSONArray( param, "knwldgIndCmplxSeqnoItems" );
		
		if( GsntalkUtil.isEmptyArray( knwldgIndCmplxSeqnoItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "knwldgIndCmplxSeqnoItems" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			knwldgIndCmplxService.deleteKnwldgIndCmplxItems( knwldgIndCmplxSeqnoItems );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.6.5 Admin - 지식산업센터 수정
	 * 
	 * @param request
	 * @param response
	 * @param vwmapFile
	 * @param featrFiles
	 * @param frmapFiles
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateKnwldgIndCmplxItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateKnwldgIndCmplxItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="vwmapFile" ) MultipartFile vwmapFile, @RequestParam( required = false, value="featrFiles" ) List<MultipartFile> featrFiles, @RequestParam( required = false, value="frmapFiles" ) List<MultipartFile> frmapFiles )throws Exception {
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
		
		long knwldgIndCmplxSeqno		= GsntalkUtil.getLong( request.getParameter( "knwldgIndCmplxSeqno" ) );
		String addr						= GsntalkUtil.getString( request.getParameter( "addr" ) );
		String addrShortNm				= GsntalkUtil.getString( request.getParameter( "addrShortNm" ) );
		double lat						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( request.getParameter( "lat" ) ) );
		double lng						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( request.getParameter( "lng" ) ) );
		String bldNm					= GsntalkUtil.getString( request.getParameter( "bldNm" ) );
		String cmpltnDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( request.getParameter( "cmpltnDate" ) ) );
		long askSalesMinPrc				= GsntalkUtil.getLong( request.getParameter( "askSalesMinPrc" ) ) * 10000;
		long askSalesAvgPrc				= GsntalkUtil.getLong( request.getParameter( "askSalesAvgPrc" ) ) * 10000;
		long askSalesMaxPrc				= GsntalkUtil.getLong( request.getParameter( "askSalesMaxPrc" ) ) * 10000;
		long askLeaseMinPrc				= GsntalkUtil.getLong( request.getParameter( "askLeaseMinPrc" ) ) * 10000;
		long askLeaseAvgPrc				= GsntalkUtil.getLong( request.getParameter( "askLeaseAvgPrc" ) ) * 10000;
		long askLeaseMaxPrc				= GsntalkUtil.getLong( request.getParameter( "askLeaseMaxPrc" ) ) * 10000;
		double lndArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( request.getParameter( "lndArea" ) ) );
		double bldArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( request.getParameter( "bldArea" ) ) );
		double totFlrArea				= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( request.getParameter( "totFlrArea" ) ) );
		int minFlr						= GsntalkUtil.getInteger( request.getParameter( "minFlr" ) );
		int maxFlr						= GsntalkUtil.getInteger( request.getParameter( "maxFlr" ) );
		int parkingCarCnt				= GsntalkUtil.getInteger( request.getParameter( "parkingCarCnt" ) );
		int husHoldCnt					= GsntalkUtil.getInteger( request.getParameter( "husHoldCnt" ) );
		String devCompNm				= GsntalkUtil.getString( request.getParameter( "devCompNm" ) );
		String constCompNm				= GsntalkUtil.getString( request.getParameter( "constCompNm" ) );
		String trfcInfo					= GsntalkUtil.getString( request.getParameter( "trfcInfo" ) );
		String siteExplntn				= GsntalkUtil.getString( request.getParameter( "siteExplntn" ) );
		String smplSmrDscr				= GsntalkUtil.getString( request.getParameter( "smplSmrDscr" ) );
		String delFeatrUrls				= GsntalkUtil.getString( request.getParameter( "delFeatrUrls" ) );
		String delFrmapUrls				= GsntalkUtil.getString( request.getParameter( "delFrmapUrls" ) );
		
		if( knwldgIndCmplxSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "knwldgIndCmplxSeqno" );
		}
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr" );
		}
		if( GsntalkUtil.isEmpty( addrShortNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addrShortNm" );
		}
		if( lat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lat" );
		}
		if( lng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lng" );
		}
		if( GsntalkUtil.isEmpty( bldNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bldNm" );
		}
		if( GsntalkUtil.isEmpty( cmpltnDate ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "cmpltnDate" );
		}
		if( !GsntalkUtil.is8DateFormat( cmpltnDate, true ) ) {
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "cmpltnDate" );
		}
		if( askSalesMinPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askSalesMinPrc" );
		}
		if( askSalesAvgPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askSalesAvgPrc" );
		}
		if( askSalesMaxPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askSalesMaxPrc" );
		}
		if( askLeaseMinPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askLeaseMinPrc" );
		}
		if( askLeaseAvgPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askLeaseAvgPrc" );
		}
		if( askLeaseMaxPrc == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "askLeaseMaxPrc" );
		}
		if( lndArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lndArea" );
		}
		if( bldArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bldArea" );
		}
		if( totFlrArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "totFlrArea" );
		}
		if( minFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "minFlr" );
		}
		if( maxFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "maxFlr" );
		}
		if( parkingCarCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "parkingCarCnt" );
		}
		if( husHoldCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "husHoldCnt" );
		}
		
		if( GsntalkUtil.isEmpty( delFeatrUrls ) ) {
			delFeatrUrls = "[]";
		}
		if( GsntalkUtil.isEmpty( delFrmapUrls ) ) {
			delFrmapUrls = "[]";
		}
		if( featrFiles == null ) {
			featrFiles = new ArrayList<MultipartFile>();
		}
		if( frmapFiles == null ) {
			frmapFiles = new ArrayList<MultipartFile>();
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			JSONParser jsonParser = new JSONParser();
			
			knwldgIndCmplxService.updateKnwldgIndCmplxItem( knwldgIndCmplxSeqno, addr, addrShortNm, lat, lng, bldNm, cmpltnDate, askSalesMinPrc, askSalesAvgPrc, askSalesMaxPrc, askLeaseMinPrc, askLeaseAvgPrc, askLeaseMaxPrc,
						lndArea, bldArea, totFlrArea, minFlr, maxFlr, parkingCarCnt, husHoldCnt, devCompNm, constCompNm, trfcInfo, siteExplntn, smplSmrDscr, vwmapFile, (JSONArray)jsonParser.parse( delFeatrUrls ), featrFiles, (JSONArray)jsonParser.parse( delFrmapUrls ), frmapFiles );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
}