package com.gsntalk.api.apis;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * API 2.0 Test
 */
@Controller
@RequestMapping( value = "/test" )
public class TestController extends CommonController {

	@Autowired
	private TestService testService;
	
	@Autowired
	private MemberService memberService;
	
	public TestController() {
		super( TestController.class );
	}
	
	/**
	 * 2.0.1 연결 테스트
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
	@PostMapping( value = "/apiTest", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject apiTest( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String serverTime = null;
		
		try {
			serverTime = testService.getServerTime();
		}catch( Exception e ) {
			throw e;
		}
		
		JSONObject item = new JSONObject();
		item.put( "serverTime", serverTime );
		
		return super.getItemResponse( item );
	}
	
	/**
	 * 2.0.2 로그인 토큰 생성 테스트
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
	@PostMapping( value = "/getJWTTokenTest", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject getJWTTokenTest( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		long memSeqno = GsntalkUtil.getLong( param.get( "memSeqno" ) );
		if( memSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		JSONObject o = new JSONObject();
		o.put( "memTypCd", "TESTER" );
		o.put( "memSeqno", memSeqno );
		o.put( "createTime", GsntalkUtil.getServerTime( "%Y%m%d%H%i%s" ) );
		
		String newJWTToken = GsntalkJWTUtil.createNewJWTToken( memSeqno, o );
		
		try {
			// 로그인토큰 갱신
			memberService.renewalLoginToken( "TESTER", memSeqno, GsntalkJWTUtil.getLoginToken( newJWTToken ) );
			
		}catch( Exception e ) {
			throw e;
		}
		
		
		return super.getSuccessResponse( response, newJWTToken );
	}
	
	/**
	 * 2.0.3 JWT 토큰 검증 테스트
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/validationJWTTokenTest", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject validationJWTTokenTest( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		if( memberVO == null ) {
			// 미 로그인 ( 또는 JWT 토큰 누락 )
			throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
		}
		if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		try {
			// 로그인 토큰 검증
			memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
			
			// 로그인토큰 갱신
			memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * N/A 전국 공인중개사 사무소 엑셀 데이터 파일 업로드 ( 테스트용 )
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping( value = "/uploadEstateAgentOfficeExlFile" )
	public JSONObject uploadEstateAgentOfficeExlFile( @RequestParam( required=true, value="file" ) MultipartFile file )throws Exception {
		try {
			// testService.uploadEstateAgentOfficeExlFile( file );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * N/A 지식산업센터 데이터 마이그레이션
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping( value = "/uploadKnwldgMigration" )
	public JSONObject uploadKnwldgMigration( @RequestParam( required=true, value="file" ) MultipartFile file )throws Exception {
		try {
			// testService.uploadKnwldgMigration( file );
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * 지식산업센터 단축주소 업데이트
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping( value = "/updateAddrShortNmToKnowledgeIndustryComplex" )
	public JSONObject updateAddrShortNmToKnowledgeIndustryComplex()throws Exception {
		try {
			// testService.updateAddrShortNmToKnowledgeIndustryComplex();
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * 매물 단축주소 업데이트
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping( value = "/updateAddrShortNmToProperty" )
	public JSONObject updateAddrShortNmToProperty()throws Exception {
		try {
			testService.updateAddrShortNmToProperty();
			
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getSuccessResponse();
	}
	
	/**
	 * 2.0.4 첨부파일 및 파라메터 업로드 테스트
	 * 
	 * @param request
	 * @param response
	 * @param file1
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/multipartUploadTest", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject multipartUploadTest( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = true, value="file1" ) MultipartFile file1, @RequestParam( required = true, value="files" ) List<MultipartFile> files )throws Exception {
		String param1 = GsntalkUtil.getString( request.getParameter( "param1" ) );
		String param2 = GsntalkUtil.getString( request.getParameter( "param2" ) );
		
		if(
				GsntalkUtil.isEmpty( param1 )
				||
				GsntalkUtil.isEmpty( param2 )
		) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		if( file1 == null || file1.getSize() == 0L ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT );
		}
		if( files == null || files.size() == 0 ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT );
		}
		if( files.size() > 5 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE );
		}
		
		JSONObject item = null;
		
		try {
			item = testService.multipartUploadTest( param1, param2, file1, files );
					
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( item );
	}
	
	/**
	 * 2.0.5 주소 -> 위경도 변환 테스트
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GsntalkCORS
	@ResponseBody
	@PostMapping( value = "/geocodeTest", produces = MediaType.APPLICATION_JSON_VALUE )
	public JSONObject geocodeTest( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception {
		String address = GsntalkUtil.getString( param.get( "address" ) );
		
		if( GsntalkUtil.isEmpty( address ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER );
		}
		
		JSONObject item = null;
		
		try {
			item = testService.geocodeTest( address );
		}catch( Exception e ) {
			throw e;
		}
		
		return super.getItemResponse( item );
	}
	
	/**
	 * 파일 이동 테스트
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@SuppressWarnings( "unchecked" )
	@PostMapping( value = "/moveS3Test" )
	public JSONObject moveS3Test( @RequestBody JSONObject param )throws Exception {
		
		String tmpFileNm = GsntalkUtil.getString( param.get( "tmpFileNm" ) );
		String url = "";
		
		try {
			url = testService.moveS3Test( tmpFileNm );
			
		}catch( Exception e ) {
			throw e;
		}
		
		JSONObject item = new JSONObject();
		item.put( "url", url );
		
		return super.getItemResponse( item ) ;
	}
}