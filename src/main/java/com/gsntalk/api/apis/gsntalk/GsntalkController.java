package com.gsntalk.api.apis.gsntalk;

import java.util.List;
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
import com.gsntalk.api.config.GsntalkJWTUtil;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

/**
 * API 2.2 Common
 */
@Controller
@RequestMapping( value = "/common" )
public class GsntalkController extends CommonController {

	@Autowired
	private GsntalkService gsntalkService;
	
	@Autowired
	private MemberService memberService;
	
	public GsntalkController() {
		super( GsntalkController.class );
	}
	
	/**
	 * 2.2.1 공통코드 목록조회 ( 여러코드 한번에 조회 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getComnCdItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getComnCdItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		param = super.resetJSONObject( param );
		
		JSONArray codeItems = GsntalkUtil.getJSONArray( param, "codeItems" );
		if( GsntalkUtil.isEmptyArray( codeItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		JSONObject item = null;
		
		try {
			item = gsntalkService.getComnCdItems( codeItems );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( item );
	}
	
	/**
	 * 2.2.2 중개사 사무소 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/srchEstBlkOfcItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject srchEstBlkOfcItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String srchVal = GsntalkUtil.getString( param.get( "srchVal" ) );
		int lastRnum = GsntalkUtil.getInteger( param.get( "lastRnum" ) );
		
		Map<String, Object> resMap = null;
		
		try {
			resMap = gsntalkService.srchEstBlkOfcItems( srchVal, lastRnum );
					
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getMapResponse( resMap );
	}
	
	/**
	 * 2.2.3 단일 공통코드 목록조회 ( 상/하위코드 목록조회 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/getSingleComnCdItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getSingleComnCdItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String upItemCd = GsntalkUtil.getString( param.get( "upItemCd" ) );
		String itemCd = GsntalkUtil.getString( param.get( "itemCd" ) );
		
		if( GsntalkUtil.isEmpty( itemCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		JSONArray items = null;
		
		try {
			items = gsntalkService.getSingleComnCdItems( upItemCd, itemCd );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( items );
	}
	
	/**
	 * 2.2.4 기본 지역 및 지역코드 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/searchStandardRegionItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject searchStandardRegionItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String sidoCd = GsntalkUtil.getString( param.get( "sidoCd" ) );
		String sggCd = GsntalkUtil.getString( param.get( "sggCd" ) );
		String umdCd = GsntalkUtil.getString( param.get( "umdCd" ) );
		
		if( GsntalkUtil.isEmpty( sidoCd ) ) {
			sidoCd = "00";
			sggCd = "000";
			umdCd = "000";
		}
		if( GsntalkUtil.isEmpty( sggCd ) ) {
			sggCd = "000";
			umdCd = "000";
		}
		if( GsntalkUtil.isEmpty( umdCd ) ) {
			umdCd = "000";
		}
		
		JSONArray items;
		
		try {
			items = gsntalkService.searchStandardRegionItems( sidoCd, sggCd, umdCd );
					
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( items );
	}
	
	/**
	 * 2.2.5 기본지역 주소 명칭 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/searchStandardRegionAddrNmItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject searchStandardRegionAddrNmItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String srchVal = GsntalkUtil.getString( param.get( "srchVal" ) );
		String matchTag = GsntalkUtil.getString( param.get( "matchTag" ) ).replaceAll( "[^a-zA-z]", "" );
		
		if( GsntalkUtil.isEmpty( srchVal ) || srchVal.length() < 2 ) {
			return super.getItemsResponse( new JSONArray() );
		}
		
		JSONArray items = null;
		
		try {
			items = gsntalkService.searchStandardRegionAddrNmItems( srchVal, matchTag );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( items ); 
	}
	
	/**
	 * 2.2.6 임시 이미지파일 업로드 ( 로그인 필수 )
	 * 
	 * @param request
	 * @param response
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/uploadTmpImgFiles", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject uploadTmpImgFiles( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = true, value="files" ) List<MultipartFile> files )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		if( GsntalkUtil.isEmptyList( files ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "files" );
		}
		
		JSONArray items = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			items = gsntalkService.uploadTmpImgFiles( files );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( response, GsntalkJWTUtil.updateJWTToken( request ), items );
	}
	
	/**
	 * 2.2.7 서비스이용약관 동의여부 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/checkForServiceTermsAgreeItem", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject checkForServiceTermsAgreeItem( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		JSONObject item = null;
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			item = gsntalkService.checkForServiceTermsAgreeItem( memberVO.getMemSeqno() );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( response, GsntalkJWTUtil.updateJWTToken( request ), item );
	}
	
	/**
	 * 2.2.8 서울/인천/경기지역 주소 명칭 검색 ( 시도->시군구 단위명칭 까지 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/searchMetroRegionAddrNmItems", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject searchMetroRegionAddrNmItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String srchVal = GsntalkUtil.getString( param.get( "srchVal" ) );
		
		if( GsntalkUtil.isEmpty( srchVal ) || srchVal.length() < 2 ) {
			return super.getItemsResponse( new JSONArray() );
		}
		
		JSONArray items = null;
		
		try {
			items = gsntalkService.searchMetroRegionAddrNmItems( srchVal );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemsResponse( items ); 
	}
}