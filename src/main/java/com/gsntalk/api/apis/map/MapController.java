package com.gsntalk.api.apis.map;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.gsntalk.api.apis.member.MemberService;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonController;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.config.GsntalkCORS;
import com.gsntalk.api.config.GsntalkJWTUtil;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

/**
 * API 2.4 Map
 */
@Controller
@RequestMapping( value = "/map" )
public class MapController extends CommonController {

	@Autowired
	private MapService mapService;
	
	@Autowired
	private MemberService memberService;
	
	public MapController() {
		super( MapController.class );
	}
	
	/**
	 * 2.4.1 매물지도 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getPropertyMapItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getPropertyMapItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		param = super.resetJSONObject( param );
		
		/* required fields */
		double swLat					= GsntalkUtil.getDouble( param.get( "swLat" ) );
		double swLng					= GsntalkUtil.getDouble( param.get( "swLng" ) );
		double neLat					= GsntalkUtil.getDouble( param.get( "neLat" ) );
		double neLng					= GsntalkUtil.getDouble( param.get( "neLng" ) );
		String estateTypGbCd			= GsntalkUtil.getString( param.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( param.get( "estateTypCd" ) );
		
		JSONArray tranTypGbCdItems		= GsntalkUtil.getJSONArray( param, "tranTypGbCdItems" );
		String dealMinAmt				= GsntalkUtil.getString( param.get( "dealMinAmt" ) );
		String dealMaxAmt				= GsntalkUtil.getString( param.get( "dealMaxAmt" ) );
		String dpstMinAmt				= GsntalkUtil.getString( param.get( "dpstMinAmt" ) );
		String dpstMaxAmt				= GsntalkUtil.getString( param.get( "dpstMaxAmt" ) );
		String monRentMinAmt			= GsntalkUtil.getString( param.get( "monRentMinAmt" ) );
		String monRentMaxAmt			= GsntalkUtil.getString( param.get( "monRentMaxAmt" ) );
		String minSplyArea				= GsntalkUtil.getString( param.get( "minSplyArea" ) );
		String maxSplyArea				= GsntalkUtil.getString( param.get( "maxSplyArea" ) );
		String minPrvArea				= GsntalkUtil.getString( param.get( "minPrvArea" ) );
		String maxPrvArea				= GsntalkUtil.getString( param.get( "maxPrvArea" ) );
		String minLndArea				= GsntalkUtil.getString( param.get( "minLndArea" ) );
		String maxLndArea				= GsntalkUtil.getString( param.get( "maxLndArea" ) );
		String minTotFlrArea			= GsntalkUtil.getString( param.get( "minTotFlrArea" ) );
		String maxTotFlrArea			= GsntalkUtil.getString( param.get( "maxTotFlrArea" ) );
		String monMntnceMinCost			= GsntalkUtil.getString( param.get( "monMntnceMinCost" ) );
		String monMntnceMaxCost			= GsntalkUtil.getString( param.get( "monMntnceMaxCost" ) );
		String useCnfrmYearSrchTypCd	= GsntalkUtil.getString( param.get( "useCnfrmYearSrchTypCd" ) );
		String loanGbCd					= GsntalkUtil.getString( param.get( "loanGbCd" ) );
		String minPrmmAmt				= GsntalkUtil.getString( param.get( "minPrmmAmt" ) );
		String maxPrmmAmt				= GsntalkUtil.getString( param.get( "maxPrmmAmt" ) );
		JSONArray sectrGbCdItems		= GsntalkUtil.getJSONArray( param, "sectrGbCdItems" );
		String flrHghtTypGbCd			= GsntalkUtil.getString( param.get( "flrHghtTypGbCd" ) );
		String elctrPwrTypGbCd			= GsntalkUtil.getString( param.get( "elctrPwrTypGbCd" ) );
		String sortItem					= GsntalkUtil.getString( param.get( "sortItem" ) );
		String sortTyp					= GsntalkUtil.getString( param.get( "sortTyp" ) );
		
		if(
				swLat == 0.0d
				||
				swLng == 0.0d
				||
				neLat == 0.0d
				||
				neLng == 0.0d
				||
				GsntalkUtil.isEmpty( estateTypGbCd )
				||
				GsntalkUtil.isEmpty( estateTypCd )
		) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		
		JSONObject item = null;
		
		try {
			item = mapService.getPropertyMapItem( memSeqno, swLat, swLng, neLat, neLng, estateTypGbCd, estateTypCd, tranTypGbCdItems, dealMinAmt, dealMaxAmt, dpstMinAmt, dpstMaxAmt, monRentMinAmt, monRentMaxAmt,
					minSplyArea, maxSplyArea, minPrvArea, maxPrvArea, minLndArea, maxLndArea, minTotFlrArea, maxTotFlrArea, monMntnceMinCost, monMntnceMaxCost, useCnfrmYearSrchTypCd, loanGbCd, minPrmmAmt,
					maxPrmmAmt, sectrGbCdItems, flrHghtTypGbCd, elctrPwrTypGbCd, sortItem, sortTyp );
			
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
	 * 2.4.2 실거래가 지도 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getRealDealMapItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getRealDealMapItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		long memSeqno = memberVO == null ? 0L : memberVO.getMemSeqno();
		
		/* required fields */
		double swLat					= GsntalkUtil.getDouble( param.get( "swLat" ) );
		double swLng					= GsntalkUtil.getDouble( param.get( "swLng" ) );
		double neLat					= GsntalkUtil.getDouble( param.get( "neLat" ) );
		double neLng					= GsntalkUtil.getDouble( param.get( "neLng" ) );
		
		if( swLat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "swLat 값 없음" );
		}
		if( swLng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "swLng 값 없음" );
		}
		if( neLat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "neLat 값 없음" );
		}
		if( neLng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "neLng 값 없음" );
		}
		
		JSONObject item = null;
		
		try {
			item = mapService.getRealDealMapItem( memSeqno, swLat, swLng, neLat, neLng );
			
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
}