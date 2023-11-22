package com.gsntalk.api.apis.property;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * API 2.5 Property
 */
@Controller
@RequestMapping( value = "/property" )
public class PropertyController extends CommonController {

	@Autowired
	private PropertyService propertyService;
	
	@Autowired
	private MemberService memberService;
	
	public PropertyController() {
		super( PropertyController.class );
	}
	
	/**
	 * 2.5.1 관심매물 등록
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerFavProperty", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerFavProperty( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.registerFavProperty( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.2 관심매물 해제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/releaseFavProperty", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject releaseFavProperty( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.releaseFavProperty( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.3 중개사 매물목록 조회 ( 중개사무소 상세페이지 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkOfcPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkOfcPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		long estBrkMemOfcSeqno		= GsntalkUtil.getLong( param.get( "estBrkMemOfcSeqno" ) );
		String tranTypGbCd			= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		String sortItem				= GsntalkUtil.getString( param.get( "sortItem" ) );
		String sortTyp				= GsntalkUtil.getString( param.get( "sortTyp" ) );
		
		if( estBrkMemOfcSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		JSONObject item = null;
		
		try {
			item = propertyService.getEstBrkOfcPropertyItems( memSeqno, estBrkMemOfcSeqno, tranTypGbCd, sortItem, sortTyp );
			
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
	 * 2.5.4 매물 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getPropertyDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getPropertyDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		String prptNo = GsntalkUtil.getString( param.get( "prptNo" ) );
		
		if( prptSeqno == 0L && GsntalkUtil.isEmpty( prptNo ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( !GsntalkUtil.isEmpty( prptNo ) ) {
			prptSeqno = GsntalkUtil.getLong( param.get( "prptNo" ) );
		}
		
		JSONObject item = null;
		
		try {
			item = propertyService.getPropertyDtlItem( memSeqno, prptSeqno );
			
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
	 * 2.5.5 중개회원 매물등록 1단계 임시저장
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerEstBrkPrptStep1RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerEstBrkPrptStep1RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno = memberVO.getMemSeqno();
		
		long prptSeqno					= GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		String estateTypGbCd			= GsntalkUtil.getString( param.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		long dealAmt					= GsntalkUtil.getLong( param.get( "dealAmt" ) );
		String dealAmtDiscsnPsblYn		= GsntalkUtil.getString( param.get( "dealAmtDiscsnPsblYn" ) );
		long dpstAmt					= GsntalkUtil.getLong( param.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( param.get( "montRentAmt" ) );
		String existngLeaseExstsYn		= GsntalkUtil.getString( param.get( "existngLeaseExstsYn" ) );
		long crntDpstAmt				= GsntalkUtil.getLong( param.get( "crntDpstAmt" ) );
		int crntMontRentAmt				= GsntalkUtil.getInteger( param.get( "crntMontRentAmt" ) );
		String keyMonExstsYn			= GsntalkUtil.getString( param.get( "keyMonExstsYn" ) );
		long keyMonAmt					= GsntalkUtil.getLong( param.get( "keyMonAmt" ) );
		long prmmAmt					= GsntalkUtil.getLong( param.get( "prmmAmt" ) );
		String cmpltExpctDate			= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "cmpltExpctDate" ) ) );
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 중개회원 매물등록 1단계 임시저장
			item = propertyService.registerEstBrkPrptStep1RegItem( memSeqno, prptSeqno, estateTypGbCd, estateTypCd, tranTypGbCd, dealAmt, dealAmtDiscsnPsblYn, dpstAmt, montRentAmt, existngLeaseExstsYn, crntDpstAmt, crntMontRentAmt, keyMonExstsYn, keyMonAmt, prmmAmt, cmpltExpctDate );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.6 중개회원 매물등록 2단계 임시저장
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
	@PostMapping( value = "/registerEstBrkPrptStep2RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerEstBrkPrptStep2RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		long memSeqno = memberVO.getMemSeqno();
		
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		
		String addr						= GsntalkUtil.getString( param.get( "addr" ) );
		String dtlAddr					= GsntalkUtil.getString( param.get( "dtlAddr" ) );
		String addrShortNm				= GsntalkUtil.getString( param.get( "addrShortNm" ) );
		double lat						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( param.get( "lat" ) ) );
		double lng						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( param.get( "lng" ) ) );
		String mapDispYn				= GsntalkUtil.getString( param.get( "mapDispYn" ) );
		String tmpAddrYn				= GsntalkUtil.getString( param.get( "tmpAddrYn" ) );
		String unregistYn				= GsntalkUtil.getString( param.get( "unregistYn" ) );
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 중개회원 매물등록 2단계 임시저장
			propertyService.registerEstBrkPrptStep2RegItem( memSeqno, regTmpKey, addr, dtlAddr, addrShortNm, lat, lng, mapDispYn, tmpAddrYn, unregistYn );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		JSONObject item = new JSONObject();
		item.put( "regTmpKey", regTmpKey );
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.7 중개회원 매물등록 3단계 임시저장
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
	@PostMapping( value = "/registerEstBrkPrptStep3RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerEstBrkPrptStep3RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		long memSeqno = memberVO.getMemSeqno();
		
		param = super.resetJSONObject( param );
		
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		
		int flr							= GsntalkUtil.getInteger( param.get( "flr" ) );
		int allFlr						= GsntalkUtil.getInteger( param.get( "allFlr" ) );
		int minFlr						= GsntalkUtil.getInteger( param.get( "minFlr" ) );
		int maxFlr						= GsntalkUtil.getInteger( param.get( "maxFlr" ) );
		double splyArea					= GsntalkUtil.getDouble( param.get( "splyArea" ) );
		double prvArea					= GsntalkUtil.getDouble( param.get( "prvArea" ) );
		double lndArea					= GsntalkUtil.getDouble( param.get( "lndArea" ) );
		double totFlrArea				= GsntalkUtil.getDouble( param.get( "totFlrArea" ) );
		String useCnfrmDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "useCnfrmDate" ) ) );
		String bldUsageGbCd				= GsntalkUtil.getString( param.get( "bldUsageGbCd" ) );
		String suggstnBldUsageGbCd		= GsntalkUtil.getString( param.get( "suggstnBldUsageGbCd" ) );
		String lndCrntUsageGbCd			= GsntalkUtil.getString( param.get( "lndCrntUsageGbCd" ) );
		String psblMovDayTypCd			= GsntalkUtil.getString( param.get( "psblMovDayTypCd" ) );
		String psblMovDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "psblMovDate" ) ) );
		int monMntnceCost				= GsntalkUtil.getInteger( param.get( "monMntnceCost" ) );
		JSONArray mntnceCostTypItems	= GsntalkUtil.getJSONArray( param, "mntnceCostTypItems" );
		String loanGbCd					= GsntalkUtil.getString( param.get( "loanGbCd" ) );
		long loanAmt					= GsntalkUtil.getLong( param.get( "loanAmt" ) );
		String parkingPsblYn			= GsntalkUtil.getString( param.get( "parkingPsblYn" ) );
		int parkingCost					= GsntalkUtil.getInteger( param.get( "parkingCost" ) );
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 중개회원 매물등록 3단계 임시저장
			propertyService.registerEstBrkPrptStep3RegItem( memSeqno, regTmpKey, flr, allFlr, minFlr, maxFlr, splyArea, prvArea, lndArea, totFlrArea, useCnfrmDate, bldUsageGbCd,
					suggstnBldUsageGbCd, lndCrntUsageGbCd, psblMovDayTypCd, psblMovDate, monMntnceCost, mntnceCostTypItems, loanGbCd, loanAmt, parkingPsblYn, parkingCost );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		JSONObject item = new JSONObject();
		item.put( "regTmpKey", regTmpKey );
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.8 중개회원 매물등록 4단계 임시저장
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
	@PostMapping( value = "/registerEstBrkPrptStep4RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerEstBrkPrptStep4RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		long memSeqno = memberVO.getMemSeqno();
		
		param = super.resetJSONObject( param );
		
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		
		int roomCnt						= GsntalkUtil.getInteger( param.get( "roomCnt" ) );
		int bathRoomCnt					= GsntalkUtil.getInteger( param.get( "bathRoomCnt" ) );
		String crntSectrGbCd			= GsntalkUtil.getString( param.get( "crntSectrGbCd" ) );
		String suggstnSectrGbCd			= GsntalkUtil.getString( param.get( "suggstnSectrGbCd" ) );
		String bldDirctnGbCd			= GsntalkUtil.getString( param.get( "bldDirctnGbCd" ) );
		String heatKindGbCd				= GsntalkUtil.getString( param.get( "heatKindGbCd" ) );
		double wghtPerPy				= GsntalkUtil.getDouble( param.get( "wghtPerPy" ) );
		String elvFcltExstsYn			= GsntalkUtil.getString( param.get( "elvFcltExstsYn" ) );
		String frghtElvExstsYn			= GsntalkUtil.getString( param.get( "frghtElvExstsYn" ) );
		String intrrYn					= GsntalkUtil.getString( param.get( "intrrYn" ) );
		String dockExstsYn				= GsntalkUtil.getString( param.get( "dockExstsYn" ) );
		String hoistExstsYn				= GsntalkUtil.getString( param.get( "hoistExstsYn" ) );
		String flrHghtTypGbCd			= GsntalkUtil.getString( param.get( "flrHghtTypGbCd" ) );
		String elctrPwrTypGbCd			= GsntalkUtil.getString( param.get( "elctrPwrTypGbCd" ) );
		String intnlStrctrTypCd			= GsntalkUtil.getString( param.get( "intnlStrctrTypCd" ) );
		String bultInYn					= GsntalkUtil.getString( param.get( "bultInYn" ) );
		String movInReprtPsblYn			= GsntalkUtil.getString( param.get( "movInReprtPsblYn" ) );
		String cityPlanYn				= GsntalkUtil.getString( param.get( "cityPlanYn" ) );
		String bldCnfrmIssueYn			= GsntalkUtil.getString( param.get( "bldCnfrmIssueYn" ) );
		String lndDealCnfrmApplYn		= GsntalkUtil.getString( param.get( "lndDealCnfrmApplYn" ) );
		String entrnceRoadExstsYn		= GsntalkUtil.getString( param.get( "entrnceRoadExstsYn" ) );
		String optionExstsYn			= GsntalkUtil.getString( param.get( "optionExstsYn" ) );
		JSONArray optionTypItems		= GsntalkUtil.getJSONArray( param, "optionTypItems" );
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 중개회원 매물등록 4단계 임시저장
			propertyService.registerEstBrkPrptStep4RegItem( memSeqno, regTmpKey, roomCnt, bathRoomCnt, crntSectrGbCd, suggstnSectrGbCd, bldDirctnGbCd, heatKindGbCd, wghtPerPy, elvFcltExstsYn, frghtElvExstsYn,
					intrrYn, dockExstsYn, hoistExstsYn, flrHghtTypGbCd, elctrPwrTypGbCd, intnlStrctrTypCd, bultInYn, movInReprtPsblYn, cityPlanYn, bldCnfrmIssueYn, lndDealCnfrmApplYn, entrnceRoadExstsYn, optionExstsYn, optionTypItems );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		JSONObject item = new JSONObject();
		item.put( "regTmpKey", regTmpKey );
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.9 중개회원 매물등록 5단계 최종 등록
	 * 
	 * @param request
	 * @param response
	 * @param repFile
	 * @param addFiles
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerEstBrkPrptFinalStepRegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerEstBrkPrptFinalStepRegItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = true, value="repFile" ) MultipartFile repFile, @RequestParam( required = false, value="addFiles" ) List<MultipartFile> addFiles )throws Exception {
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
		long memSeqno = memberVO.getMemSeqno();
		
		String regTmpKey				= GsntalkUtil.getString( request.getParameter( "regTmpKey" ) );
		
		String smplSmrDscr				= GsntalkUtil.getString( request.getParameter( "smplSmrDscr" ) );
		String dtlExplntnDscr			= GsntalkUtil.getString( request.getParameter( "dtlExplntnDscr" ) );
		String prvtMemoDscr				= GsntalkUtil.getString( request.getParameter( "prvtMemoDscr" ) );
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 중개회원 매물등록 5단계 최종 등록
			propertyService.registerEstBrkPrptFinalStepRegItem( memSeqno, regTmpKey, repFile, addFiles, smplSmrDscr, dtlExplntnDscr, prvtMemoDscr );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.10 중개회원 - 내 매물 목록 조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String srchClasGbCd				= GsntalkUtil.getString( param.get( "srchClasGbCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) );
		
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
			
			resMap = propertyService.getEstBrkPropertyItems( memberVO.getMemSeqno(), srchClasGbCd, tranTypGbCd, estateTypCd, srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.5.11 중개회원 - 내 매물 삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteEstBrkPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteEstBrkPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		JSONArray prptSeqnoItems = GsntalkUtil.getJSONArray( param, "prptSeqnoItems" );
		if( GsntalkUtil.isEmptyArray( prptSeqnoItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqnoItems 값 없음" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.deleteEstBrkPropertyItems( memberVO.getMemSeqno(), prptSeqnoItems );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.12 중개회원 - 내 매물 상태변경
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateEstBrkPropertyDealStatItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateEstBrkPropertyDealStatItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno				= GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		String dealStatGbCd			= GsntalkUtil.getString( param.get( "dealStatGbCd" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno 값 없음" );
		}
		if( GsntalkUtil.isEmpty( dealStatGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealStatGbCd 값 없음" );
		}
		if( !GsntalkUtil.isIn( dealStatGbCd, "ING", "FIN", "PRV" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dealStatGbCd 값이 잘못됨 -> see CommonCode [DEAL_STAT_GB_CD]" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.updateEstBrkPropertyDealStatItem( memberVO.getMemSeqno(), prptSeqno, dealStatGbCd );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.13 중개회원 - 등록만료 매물 재등록
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/regenEstBrkPropertyItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject regenEstBrkPropertyItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno 값 없음" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.regenEstBrkPropertyItem( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.14 중개회원 - 매물등록 1단계 등록 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkPrptStep1RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkPrptStep1RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno 값 없음" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getEstBrkPrptStep1RegItem( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.15 중개회원 - 매물등록 2단계 등록 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkPrptStep2RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkPrptStep2RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno 값 없음" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getEstBrkPrptStep2RegItem( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.16 중개회원 - 매물등록 3단계 등록 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkPrptStep3RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkPrptStep3RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno 값 없음" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getEstBrkPrptStep3RegItem( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.17 중개회원 - 매물등록 4단계 등록 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkPrptStep4RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkPrptStep4RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno 값 없음" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getEstBrkPrptStep4RegItem( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.18 중개회원 - 매물등록 5단계 등록 정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkPrptStep5RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkPrptStep5RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno 값 없음" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getEstBrkPrptStep5RegItem( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.19 중개회원 매물수정 5단계 최종 수정 ( 매물수정 전용 )
	 * 
	 * @param request
	 * @param response
	 * @param repFile
	 * @param addFiles
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateEstBrkPrptFinalStepRegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateEstBrkPrptFinalStepRegItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="repFile" ) MultipartFile repFile, @RequestParam( required = false, value="addFiles" ) List<MultipartFile> addFiles )throws Exception {
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
		
		long memSeqno = memberVO.getMemSeqno();
		
		long prptSeqno					= GsntalkUtil.getLong( request.getParameter( "prptSeqno" ) );
		String delFileUrls				= GsntalkUtil.getString( request.getParameter( "delFileUrls" ) );
		String smplSmrDscr				= GsntalkUtil.getString( request.getParameter( "smplSmrDscr" ) );
		String dtlExplntnDscr			= GsntalkUtil.getString( request.getParameter( "dtlExplntnDscr" ) );
		String prvtMemoDscr				= GsntalkUtil.getString( request.getParameter( "prvtMemoDscr" ) );
		
		if( GsntalkUtil.isEmpty( delFileUrls ) ) {
			delFileUrls = "[]";
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 중개회원 매물수정 5단계 최종 수정 ( 매물수정 전용 )
			propertyService.updateEstBrkPrptFinalStepRegItem( memSeqno, prptSeqno, repFile, (JSONArray)new JSONParser().parse( delFileUrls ), addFiles, smplSmrDscr, dtlExplntnDscr, prvtMemoDscr );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.20 일반회원 매물등록 1단계 임시저장
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerMemberPrptStep1RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerMemberPrptStep1RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno = memberVO.getMemSeqno();
		
		String estateTypGbCd			= GsntalkUtil.getString( param.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		long dealAmt					= GsntalkUtil.getLong( param.get( "dealAmt" ) );
		String dealAmtDiscsnPsblYn		= GsntalkUtil.getString( param.get( "dealAmtDiscsnPsblYn" ) );
		long dpstAmt					= GsntalkUtil.getLong( param.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( param.get( "montRentAmt" ) );
		String existngLeaseExstsYn		= GsntalkUtil.getString( param.get( "existngLeaseExstsYn" ) );
		long crntDpstAmt				= GsntalkUtil.getLong( param.get( "crntDpstAmt" ) );
		int crntMontRentAmt				= GsntalkUtil.getInteger( param.get( "crntMontRentAmt" ) );
		String keyMonExstsYn			= GsntalkUtil.getString( param.get( "keyMonExstsYn" ) );
		long keyMonAmt					= GsntalkUtil.getLong( param.get( "keyMonAmt" ) );
		long prmmAmt					= GsntalkUtil.getLong( param.get( "prmmAmt" ) );
		String cmpltExpctDate			= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "cmpltExpctDate" ) ) );
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 일반회원 매물등록 1단계 임시저장
			item = propertyService.registerMemberPrptStep1RegItem( memSeqno, estateTypGbCd, estateTypCd, tranTypGbCd, dealAmt, dealAmtDiscsnPsblYn, dpstAmt, montRentAmt, existngLeaseExstsYn, crntDpstAmt, crntMontRentAmt, keyMonExstsYn, keyMonAmt, prmmAmt, cmpltExpctDate );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.21 일반회원 매물등록 2단계 임시저장
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
	@PostMapping( value = "/registerMemberPrptStep2RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerMemberPrptStep2RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno = memberVO.getMemSeqno();
		
		String regTmpKey				= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		
		String addr						= GsntalkUtil.getString( param.get( "addr" ) );
		String dtlAddr					= GsntalkUtil.getString( param.get( "dtlAddr" ) );
		String addrShortNm				= GsntalkUtil.getString( param.get( "addrShortNm" ) );
		double lat						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( param.get( "lat" ) ) );
		double lng						= GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( param.get( "lng" ) ) );
		String mapDispYn				= GsntalkUtil.getString( param.get( "mapDispYn" ) );
		String tmpAddrYn				= GsntalkUtil.getString( param.get( "tmpAddrYn" ) );
		String unregistYn				= GsntalkUtil.getString( param.get( "unregistYn" ) );
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 일반회원 매물등록 2단계 임시저장
			propertyService.registerMemberPrptStep2RegItem( memSeqno, regTmpKey, addr, dtlAddr, addrShortNm, lat, lng, mapDispYn, tmpAddrYn, unregistYn );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		JSONObject item = new JSONObject();
		item.put( "regTmpKey", regTmpKey );
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.22 일반회원 매물등록 3단계 최종등록
	 * 
	 * @param request
	 * @param response
	 * @param repFile
	 * @param addFiles
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerMemberPrptFinalStepRegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerMemberPrptFinalStepRegItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = true, value="repFile" ) MultipartFile repFile, @RequestParam( required = false, value="addFiles" ) List<MultipartFile> addFiles )throws Exception {
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
		
		long memSeqno = memberVO.getMemSeqno();
		
		String regTmpKey				= GsntalkUtil.getString( request.getParameter( "regTmpKey" ) );
		
		double prvArea					= GsntalkUtil.getDouble( request.getParameter( "prvArea" ) );
		double lndArea					= GsntalkUtil.getDouble( request.getParameter( "lndArea" ) );
		double totFlrArea				= GsntalkUtil.getDouble( request.getParameter( "totFlrArea" ) );
		String dtlExplntnDscr			= GsntalkUtil.getString( request.getParameter( "dtlExplntnDscr" ) );
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 일반회원 매물등록 3단계 최종등록
			propertyService.registerMemberPrptFinalStepRegItem( memSeqno, regTmpKey, prvArea, lndArea, totFlrArea, repFile, addFiles, dtlExplntnDscr );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.23 등록임시키로 매물유형 및 거래유형 정보 조회 ( 중개회원/일반회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getProperyTypInfo", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getProperyTypInfo( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		if( !GsntalkUtil.isIn( memberVO.getMemTypCd(), GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT, GsntalkConstants.MEM_TYP_CD_NORMAL_USER ) ){
			// 조회할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_SEARCH );
		}
		
		String regTmpKey = GsntalkUtil.getString( param.get( "regTmpKey" ) );
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getProperyTypInfo( memberVO.getMemTypCd(), memberVO.getMemSeqno(), regTmpKey );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.24 일반회원 - 내매물 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMyPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMyPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		param = super.resetJSONObject( param );
		
		String regStatGbCd				= GsntalkUtil.getString( param.get( "regStatGbCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) );
		
		if( GsntalkUtil.isEmpty( regStatGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regStatGbCd" );
		}
		if( !GsntalkUtil.isIn( regStatGbCd, "R", "F" ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "regStatGbCd is not in R/F" );
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
			
			resMap = propertyService.getMyPropertyItems( memberVO.getMemSeqno(), regStatGbCd, tranTypGbCd, estateTypCd, srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.5.25 일반회원 - 내 매물 삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteMyPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteMyPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		param = super.resetJSONObject( param );
		
		JSONArray prptSeqnoItems = GsntalkUtil.getJSONArray( param, "prptSeqnoItems" );
		
		if( GsntalkUtil.isEmptyArray( prptSeqnoItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqnoItems" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.deleteMyPropertyItems( memberVO.getMemSeqno(), prptSeqnoItems );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.26 Admin - 일반회원 매물 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMembersPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMembersPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		String dealStatGbCdItems		= GsntalkUtil.getString( param.get( "dealStatGbCdItems" ) );
		String tranTypGbCdItems			= GsntalkUtil.getString( param.get( "tranTypGbCdItems" ) );
		String treatStatGbCd			= GsntalkUtil.getString( param.get( "treatStatGbCd" ) );
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) );
		
		if( !GsntalkUtil.isEmpty( regDtSrchTyp ) && !GsntalkUtil.isIn( regDtSrchTyp, "W", "M", "Y" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "regDtSrchTyp 값이 잘못됨  -> is not in [ W, M, Y ]" );
		}
		if( GsntalkUtil.isEmpty( dealStatGbCdItems ) ) {
			dealStatGbCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( tranTypGbCdItems ) ) {
			tranTypGbCdItems = "[]";
		}
		if( !GsntalkUtil.isEmpty( treatStatGbCd ) && !GsntalkUtil.isIn( treatStatGbCd, "A", "B" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "treatStatGbCd 값이 잘못됨  -> is not in [ A, B ]" );
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
			
			JSONParser parser = new JSONParser();
			
			resMap = propertyService.getMembersPropertyItems( regDtSrchTyp, (JSONArray)parser.parse( dealStatGbCdItems ), (JSONArray)parser.parse( tranTypGbCdItems ), treatStatGbCd, srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.5.27 Admin - 일반회원 매물 목록조회 - Excel Download
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/downloadMembersPropertyItems", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void downloadMembersPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String regDtSrchTyp				= GsntalkUtil.getString( param.get( "regDtSrchTyp" ) );
		String dealStatGbCd				= GsntalkUtil.getString( param.get( "dealStatGbCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		String treatStatGbCd			= GsntalkUtil.getString( param.get( "treatStatGbCd" ) );
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) );
		
		if( !GsntalkUtil.isEmpty( regDtSrchTyp ) && !GsntalkUtil.isIn( regDtSrchTyp, "W", "M", "Y" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "regDtSrchTyp 값이 잘못됨  -> is not in [ W, M, Y ]" );
		}
		if( !GsntalkUtil.isEmpty( dealStatGbCd ) && !GsntalkUtil.isIn( dealStatGbCd, "ING", "FIN", "PRV" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dealStatGbCd 값이 잘못됨  -> is not in [ ING, FIN, PRV ]" );
		}
		if( !GsntalkUtil.isEmpty( tranTypGbCd ) && !GsntalkUtil.isIn( tranTypGbCd, "TRADE", "LEASE", "LEASE_ST", "CHARTER", "MONTLY", "RESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranTypGbCd 값이 잘못됨  -> is not in [ TRADE, LEASE, LEASE_ST, CHARTER, MONTLY, RESALE ]" );
		}
		if( !GsntalkUtil.isEmpty( treatStatGbCd ) && !GsntalkUtil.isIn( treatStatGbCd, "A", "B" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "treatStatGbCd 값이 잘못됨  -> is not in [ A, B ]" );
		}
		
		XSSFWorkbook wb = null;
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			wb = propertyService.downloadMembersPropertyItems( regDtSrchTyp, dealStatGbCd, tranTypGbCd, treatStatGbCd, srchVal );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		OutputStream out = null;
		try {
			response.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
			response.setHeader( "Content-Disposition", "attachment; filename=" + URLEncoder.encode( "일반회원_매물목록", "UTF-8" ) + "_" + GsntalkUtil.getServerTime( "%Y%m%d%H%i" ) + ".xlsx;" );
			
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
	 * 2.5.28 Admin - 일반회원 매물 삭제
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteMembersPropertyItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteMembersPropertyItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		JSONArray prptSeqnoItems = GsntalkUtil.getJSONArray( param, "prptSeqnoItems" );
		
		if( GsntalkUtil.isEmptyArray( prptSeqnoItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqnoItems" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.deleteMembersPropertyItems( prptSeqnoItems );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.29 Admin - 일반회원/중개회원 매물 상세정보 조회( 수정용 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMembersPropertyDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMembersPropertyDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getMembersPropertyDtlItem( prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.30 Admin - 일반회원/중개회원 매물 상세정보 수정 ( 일반회원의 기존 공개되지 않은매물의 경우 공개시작 )
	 * 
	 * @param request
	 * @param response
	 * @param repFile
	 * @param addFiles
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/openMembersPropertyItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject openMembersPropertyItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="repFile" ) MultipartFile repFile, @RequestParam( required = false, value="addFiles" ) List<MultipartFile> addFiles )throws Exception {
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
		
		long prptSeqno					= GsntalkUtil.getLong( request.getParameter( "prptSeqno" ) );
		
		String baseItem					= GsntalkUtil.getString( request.getParameter( "baseItem" ) );
		String tranItem					= GsntalkUtil.getString( request.getParameter( "tranItem" ) );
		String additionItem				= GsntalkUtil.getString( request.getParameter( "additionItem" ) );
		String photoItem				= GsntalkUtil.getString( request.getParameter( "photoItem" ) );
		String delFileUrls				= GsntalkUtil.getString( request.getParameter( "delFileUrls" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno" );
		}
		if( GsntalkUtil.isEmpty( baseItem ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem" );
		}
		if( GsntalkUtil.isEmpty( tranItem ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem" );
		}
		if( GsntalkUtil.isEmpty( additionItem ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "additionItem" );
		}
		if( GsntalkUtil.isEmpty( photoItem ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "photoItem" );
		}
		
		if( GsntalkUtil.isEmpty( delFileUrls ) ) {
			delFileUrls = "[]";
		}
		
		JSONParser parser = new JSONParser();
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// Admin - 일반회원/중개회원 매물 상세정보 수정 ( 일반회원의 기존 공개되지 않은매물의 경우 공개시작 )
			propertyService.openMembersPropertyItem( prptSeqno, (JSONObject)parser.parse( baseItem ), (JSONObject)parser.parse( tranItem ), (JSONObject)parser.parse( additionItem ), (JSONObject)parser.parse( photoItem ), (JSONArray)parser.parse( delFileUrls ), repFile, addFiles );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.31 Admin - 일반매물 거래상태 변경
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/updateMembersPropertyDealStateItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject updateMembersPropertyDealStateItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno = GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		String dealStatGbCd = GsntalkUtil.getString( param.get( "dealStatGbCd" ) );
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno" );
		}
		if( GsntalkUtil.isEmpty( dealStatGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealStatGbCd" );
		}
		if( !GsntalkUtil.isIn( dealStatGbCd, "ING", "FIN", "PRV" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dealStatGbCd 값이 잘못됨 -> see CommonCode [DEAL_STAT_GB_CD]" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.updateMembersPropertyDealStateItem( prptSeqno, dealStatGbCd );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.32 Admin - 중개사 매물 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getEstBrkPrptItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getEstBrkPrptItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) );
		String regiEstTypCdItems		= GsntalkUtil.getString( param.get( "regiEstTypCdItems" ) );
		String commEstTypCdItems		= GsntalkUtil.getString( param.get( "commEstTypCdItems" ) );
		String preEstTypCdItems			= GsntalkUtil.getString( param.get( "preEstTypCdItems" ) );
		String dealStatGbCdItems		= GsntalkUtil.getString( param.get( "dealStatGbCdItems" ) );
		String tranTypGbCdItems			= GsntalkUtil.getString( param.get( "tranTypGbCdItems" ) );
		String prptStatGbCd				= GsntalkUtil.getString( param.get( "prptStatGbCd" ) );
		
		if( GsntalkUtil.isEmpty( regiEstTypCdItems ) ) {
			regiEstTypCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( commEstTypCdItems ) ) {
			commEstTypCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( preEstTypCdItems ) ) {
			preEstTypCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( dealStatGbCdItems ) ) {
			dealStatGbCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( tranTypGbCdItems ) ) {
			tranTypGbCdItems = "[]";
		}
		if( !GsntalkUtil.isEmpty( prptStatGbCd ) && !GsntalkUtil.isIn( prptStatGbCd, "S", "E" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "prptStatGbCd 값이 잘못됨  -> is not in [ S, E ]" );
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
			
			JSONParser parser = new JSONParser();
			
			resMap = propertyService.getEstBrkPrptItems( srchVal, (JSONArray)parser.parse( dealStatGbCdItems ), (JSONArray)parser.parse( tranTypGbCdItems ), (JSONArray)parser.parse( regiEstTypCdItems ), (JSONArray)parser.parse( commEstTypCdItems ), (JSONArray)parser.parse( preEstTypCdItems ), prptStatGbCd, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.5.33 Admin - 중개사 매물 목록조회 - Excel Download
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/downloadEstBrkPrptItems", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void downloadEstBrkPrptItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String srchVal					= GsntalkUtil.getString( param.get( "srchVal" ) );
		String regiEstTypCdItems		= GsntalkUtil.getString( param.get( "regiEstTypCdItems" ) );
		String commEstTypCdItems		= GsntalkUtil.getString( param.get( "commEstTypCdItems" ) );
		String preEstTypCdItems			= GsntalkUtil.getString( param.get( "preEstTypCdItems" ) );
		String dealStatGbCdItems		= GsntalkUtil.getString( param.get( "dealStatGbCdItems" ) );
		String tranTypGbCdItems			= GsntalkUtil.getString( param.get( "tranTypGbCdItems" ) );
		String prptStatGbCd				= GsntalkUtil.getString( param.get( "prptStatGbCd" ) );
		
		if( GsntalkUtil.isEmpty( regiEstTypCdItems ) ) {
			regiEstTypCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( commEstTypCdItems ) ) {
			commEstTypCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( preEstTypCdItems ) ) {
			preEstTypCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( dealStatGbCdItems ) ) {
			dealStatGbCdItems = "[]";
		}
		if( GsntalkUtil.isEmpty( tranTypGbCdItems ) ) {
			tranTypGbCdItems = "[]";
		}
		if( !GsntalkUtil.isEmpty( prptStatGbCd ) && !GsntalkUtil.isIn( prptStatGbCd, "S", "E" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "prptStatGbCd 값이 잘못됨  -> is not in [ S, E ]" );
		}
		
		XSSFWorkbook wb = null;
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			JSONParser parser = new JSONParser();
			
			wb = propertyService.downloadEstBrkPrptItems( srchVal, (JSONArray)parser.parse( dealStatGbCdItems ), (JSONArray)parser.parse( tranTypGbCdItems ), (JSONArray)parser.parse( regiEstTypCdItems ), (JSONArray)parser.parse( commEstTypCdItems ), (JSONArray)parser.parse( preEstTypCdItems ), prptStatGbCd );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		OutputStream out = null;
		try {
			response.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
			response.setHeader( "Content-Disposition", "attachment; filename=" + URLEncoder.encode( "중개회원_매물목록", "UTF-8" ) + "_" + GsntalkUtil.getServerTime( "%Y%m%d%H%i" ) + ".xlsx;" );
			
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
	 * 2.5.34 FRT - 매물제안서 요청 1단계 임시저장 ( 일반/중개회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerPrptSuggstReqStep1RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerPrptSuggstReqStep1RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		param = super.resetJSONObject( param );
		
		String estateTypCd			= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		double wishArea				= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( param.get( "wishArea" ) ) );
		String sectrGbCd			= GsntalkUtil.getString( param.get( "sectrGbCd" ) );
		int usePplCnt				= GsntalkUtil.getInteger( param.get( "usePplCnt" ) );
		String psblMovDayTypCd		= GsntalkUtil.getString( param.get( "psblMovDayTypCd" ) );
		String psblMovStDate		= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "psblMovStDate" ) ) );
		String psblMovEdDate		= GsntalkUtil.parseNumberString( GsntalkUtil.getString( param.get( "psblMovEdDate" ) ) );
		String suggstRegionItems	= GsntalkUtil.getString( param.get( "suggstRegionItems" ) );
		
		if( GsntalkUtil.isEmpty( estateTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypCd" );
		}
		if( !GsntalkUtil.isIn( estateTypCd, "STR", "KOG", "FTR" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd is not in STR / KOG / FTR" );
		}
		if( wishArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "wishArea" );
		}
		if( GsntalkUtil.isEmpty( psblMovDayTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "psblMovDayTypCd" );
		}
		if( !GsntalkUtil.isIn( psblMovDayTypCd, "IMMDTLY", "DISCSN", "INPUT" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "psblMovDayTypCd is not in IMMDTLY / DISCSN / INPUT" );
		}
		if( "INPUT".equals( psblMovDayTypCd ) ) {
			if( GsntalkUtil.isEmpty( psblMovStDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "psblMovStDate" );
			}
			if( !GsntalkUtil.is8DateFormat( psblMovStDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "psblMovStDate" );
			}
			if( GsntalkUtil.isEmpty( psblMovEdDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "psblMovEdDate" );
			}
			if( !GsntalkUtil.is8DateFormat( psblMovEdDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "psblMovEdDate" );
			}
		}
		if( GsntalkUtil.isEmpty( suggstRegionItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstRegionItems" );
		}
		JSONArray regionItems = (JSONArray)new JSONParser().parse( suggstRegionItems );
		if( regionItems.size() == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstRegionItems" );
		}
		if( regionItems.size() > 3 ) {
			// 등록가능한 수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ITEMS_SIZE, "suggstRegionItems over 3 items" );
		}
		
		// 상가
		if( "STR".equals( estateTypCd ) ) {
			usePplCnt = 0;
			
		}else {
			sectrGbCd = "";
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.registerPrptSuggstReqStep1RegItem( memberVO.getMemTypCd(), memberVO.getMemSeqno(), estateTypCd, wishArea, sectrGbCd, usePplCnt, psblMovDayTypCd,
					psblMovStDate, psblMovEdDate, regionItems );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	
	/**
	 * 2.5.35 FRT - 매물제안서 요청 2단계 임시저장 ( 일반/중개회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerPrptSuggstReqStep2RegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerPrptSuggstReqStep2RegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String regTmpKey			= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		String tranTypGbCd			= GsntalkUtil.getString( param.get( "tranTypGbCd" ) );
		long dealAmt				= GsntalkUtil.getLong( param.get( "dealAmt" ) );
		long dpstAmt				= GsntalkUtil.getLong( param.get( "dpstAmt" ) );
		int montRentAmt				= GsntalkUtil.getInteger( param.get( "montRentAmt" ) );
		String clientNm				= GsntalkUtil.getString( param.get( "clientNm" ) );
		String compNm				= GsntalkUtil.getString( param.get( "compNm" ) );
		String suggstSectrCd		= GsntalkUtil.getString( param.get( "suggstSectrCd" ) );
		
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		if( GsntalkUtil.isEmpty( tranTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranTypGbCd" );
		}
		if( !GsntalkUtil.isIn( tranTypGbCd, "TRADE", "LEASE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranTypGbCd is not in TRADE / LEASE" );
		}
		if( "TRADE".equals( tranTypGbCd ) ) {
			if( dealAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealAmt" );
			}
			dpstAmt = 0L;
			montRentAmt = 0;
			
		}else {
			if( dpstAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dpstAmt" );
			}
			if( montRentAmt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "montRentAmt" );
			}
			dealAmt = 0L;
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.registerPrptSuggstReqStep2RegItem( memberVO.getMemTypCd(), memberVO.getMemSeqno(), regTmpKey, tranTypGbCd, dealAmt, dpstAmt, montRentAmt,
					clientNm, compNm, suggstSectrCd );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.36  FRT - 매물제안서 요청 3단계 최종 등록 ( 일반/중개회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/registerPrptSuggstReqFinalStepRegItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject registerPrptSuggstReqFinalStepRegItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		String regTmpKey			= GsntalkUtil.getString( param.get( "regTmpKey" ) );
		String wishFlrTypCd			= GsntalkUtil.getString( param.get( "wishFlrTypCd" ) );
		String intrrYn				= GsntalkUtil.getString( param.get( "intrrYn" ) );
		String reqDscr				= GsntalkUtil.getString( param.get( "reqDscr" ) );
		
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		if( !GsntalkUtil.isEmpty( intrrYn ) && !GsntalkUtil.isIn( intrrYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "intrrYn is not empty, but intrrYn is not in Y / N" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.registerPrptSuggstReqFinalStepRegItem( memberVO.getMemTypCd(), memberVO.getMemSeqno(), regTmpKey, wishFlrTypCd, intrrYn, reqDscr );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.37 FRT - 내 매물 제안서 목록조회 ( 일반/중개회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMyPrptSuggstItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMyPrptSuggstItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
			// 조회할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_SEARCH );
		}
		
		JSONArray items = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			items = propertyService.getMyPrptSuggstItems( memberVO.getMemSeqno() );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( response, GsntalkJWTUtil.updateJWTToken( request ), items );
	}
	
	/**
	 * 2.5.38 FRT - 제안받은 매물 목록조회 ( 일반/중개회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMySuggstRcvPrptItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMySuggstRcvPrptItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
			// 조회할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_SEARCH );
		}
		
		long prptSuggstReqSeqno		= GsntalkUtil.getLong( param.get( "prptSuggstReqSeqno" ) );
		String suggstMemTypCd		= GsntalkUtil.getString( param.get( "suggstMemTypCd" ) );
		long suggstMemSeqno			= GsntalkUtil.getLong( param.get( "suggstMemSeqno" ) );
		
		if( prptSuggstReqSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSuggstReqSeqno" );
		}
		if( GsntalkUtil.isEmpty( suggstMemTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstMemTypCd" );
		}
		if( !GsntalkUtil.isIn( suggstMemTypCd, GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT, GsntalkConstants.MEM_TYP_CD_NORMAL_USER ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "suggstMemTypCd is not in E / N" );
		}
		if( suggstMemSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstMemSeqno" );
		}
		
		Map<String, Object> resMap = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			resMap = propertyService.getMySuggstRcvPrptItems( memberVO.getMemSeqno(), prptSuggstReqSeqno, suggstMemTypCd, suggstMemSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.5.39 Admin - 매물 제안요청 목록조회 ( 페이징 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getPrptSuggstReqItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getPrptSuggstReqItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
			
			resMap = propertyService.getPrptSuggstReqItems( srchVal, pageCnt, nowPage, listPerPage );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
	}
	
	/**
	 * 2.5.40 투어 요청 ( 일반/중개회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/requestTour", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject requestTour( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSeqno		= GsntalkUtil.getLong( param.get( "prptSeqno" ) );
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.requestTour( memberVO.getMemSeqno(), prptSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.41 FRT - 내 매물 제안서 삭제 ( 일반/중개회원 공통 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/deleteMyPrptSuggstItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject deleteMyPrptSuggstItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long prptSuggstReqSeqno		= GsntalkUtil.getLong( param.get( "prptSuggstReqSeqno" ) );
		String suggstMemTypCd		= GsntalkUtil.getString( param.get( "suggstMemTypCd" ) );
		long suggstMemSeqno			= GsntalkUtil.getLong( param.get( "suggstMemSeqno" ) );
		
		if( prptSuggstReqSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSuggstReqSeqno" );
		}
		if( GsntalkUtil.isEmpty( suggstMemTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstMemTypCd" );
		}
		if( !GsntalkUtil.isIn( suggstMemTypCd, GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT, GsntalkConstants.MEM_TYP_CD_NORMAL_USER ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "suggstMemTypCd is not in E / N" );
		}
		if( suggstMemSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstMemSeqno" );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			propertyService.deleteMyPrptSuggstItem( memberVO.getMemSeqno(), prptSuggstReqSeqno, suggstMemTypCd, suggstMemSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
	}
	
	/**
	 * 2.5.42 Admin - 매물제안 회원 상세정보 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getSuggstnPrptMemDtlItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getSuggstnPrptMemDtlItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno		= GsntalkUtil.getLong( param.get( "memSeqno" ) );
		if( memSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = propertyService.getSuggstnPrptMemDtlItem( memSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.5.43 Admin - 회원 제안서 목록조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getMemPrptSuggstItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getMemPrptSuggstItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
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
		
		long memSeqno		= GsntalkUtil.getLong( param.get( "memSeqno" ) );
		if( memSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "memSeqno" );
		}
		
		JSONArray items = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			items = propertyService.getMyPrptSuggstItems( memSeqno );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( response, GsntalkJWTUtil.updateJWTToken( request ), items );
	}
}