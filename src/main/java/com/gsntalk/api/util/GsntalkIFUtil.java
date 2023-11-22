package com.gsntalk.api.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.config.GsntalkConstants;

@Component
@Scope( value = "singleton" )
public class GsntalkIFUtil {

	private Logger LOGGER = LoggerFactory.getLogger( this.getClass() );
	
	private final int GET_RECORD_PER_PAGE = 1000;
	
	/** data.go */
	@Value("${api.data.go.biz.api-key}")
	private String DATA_GO_BIZ_API_KEY;
	@Value("${api.data.go.biz.validation-uri}")
	private String DATA_GO_BIZ_VALIDATION_URI;
	@Value("${api.data.go.biz.state-uri}")
	private String DATA_GO_BIZ_STATE_URI;
	@Value("${api.data.go.standard-region.call-uri}")
	private String DATA_GO_STANDARD_REGION_URI;
	
	/** Naver */
	@Value("${api.sns.naver.client.id}")
	private String NAVER_CLIENT_ID;
	@Value("${api.sns.naver.client.secret}")
	private String NAVER_CLIENT_SECRET;
	@Value("${api.sns.naver-login.request.token-uri}")
	private String NAVER_LOGIN_REQUEST_TOKEN_URI;
	@Value("${api.sns.naver-login.request.profile-uri}")
	private String NAVER_LOGIN_REQUEST_PROFILE_URI;
	
	/** Naver Maps-API */
	@Value("${api.map.naver.client.id}")
	private String MAPS_API_CLIENT_ID;
	@Value("${api.map.naver.client.secret}")
	private String MAPS_API_CLIENT_SECRET;
	@Value("${api.map.naver.geocode.host-uri}")
	private String MAPS_API_GEOCODE_URI;
	
	/** Kakao */
	@Value("${api.sns.kakao.rest-api.key}")
	private String KAKAO_REST_API_KEY;
	@Value("${api.sns.kakao.client-secret}")
	private String KAKAO_CLIENT_SECRET;
	@Value("${api.sns.kakao-login.request.token-uri}")
	private String KAKAO_LOGIN_REQUEST_TOKEN_URI;
	@Value("${api.sns.kakao-login.sign-up.return-uri}")
	private String KAKAO_LOGIN_SIGN_UP_RETURN_URI;
	@Value("${api.sns.kakao-login.sign-in.return-uri}")
	private String KAKAO_LOGIN_SIGN_IN_RETURN_URI;
	@Value("${api.sns.kakao-login.request.profile-uri}")
	private String KAKAO_LOGIN_REQUEST_PROFILE_URL;
	
	/** kakao-message */
	@Value("${api.kakao.message.host}")
	private String KAKAO_MESSAGE_HOST;
	@Value("${api.kakao.message.app-key}")
	private String KAKAO_MESSAGE_APP_KEY;
	@Value("${api.kakao.message.secret-key}")
	private String KAKAO_MESSAGE_SECRET_KEY;
	@Value("${api.kakao.message.sender-key}")
	private String KAKAO_MESSAGE_SENDER_KEY;
	
	private SSLContext sc;
	private JSONParser jsonParser;
	
	public GsntalkIFUtil() {
		this.jsonParser = new JSONParser();
		try {
			this.sc = SSLContext.getInstance( "SSL" );
			this.sc.init( null, this.createTrustManagers(), new SecureRandom() );
			HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );
			HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 카카오 알림톡 발송
	 * 
	 * @param receiverMobNo
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject sendKakaoMessage( String receiverMobNo, String templateCode, JSONObject templateParam )throws Exception {
		HttpsURLConnection conn = (HttpsURLConnection) new URL( this.KAKAO_MESSAGE_HOST + "/alimtalk/v2.3/appkeys/" + GsntalkEncryptor.decrypt( this.KAKAO_MESSAGE_APP_KEY ) +"/messages" ).openConnection();
		conn.setDoInput( true );
		conn.setDoOutput( true );
		conn.setUseCaches( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "X-Secret-Key", GsntalkEncryptor.decrypt( this.KAKAO_MESSAGE_SECRET_KEY ) );
		conn.setRequestProperty( "Content-Type", "application/json;charset=UTF-8" );
		
		JSONObject recipientItem = new JSONObject();
		recipientItem.put( "recipientNo", receiverMobNo );
		recipientItem.put( "templateParameter", templateParam );
		
		JSONArray recipientItems = new JSONArray();
		recipientItems.add( recipientItem );
		
		JSONObject param = new JSONObject();
		param.put( "senderKey", GsntalkEncryptor.decrypt( this.KAKAO_MESSAGE_SENDER_KEY ) );
		param.put( "templateCode", templateCode );
		param.put( "recipientList", recipientItems );
		
		OutputStream os = conn.getOutputStream();
		os.write( param.toJSONString().getBytes( "UTF-8" ) );
		os.flush();
		os.close();
		
		BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		StringBuffer sb = new StringBuffer();
		String resultLine = null;
			
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		JSONObject resObj = (JSONObject)this.jsonParser.parse( sb.toString() );
		
		return GsntalkUtil.getJSONObject( resObj, "message" );
	}
	
	/**
	 * 카카오 알림톡 메시지 발송결과 조회
	 * 
	 * @param srchStDttm
	 * @param srchEdDttm
	 * @return
	 * @throws Exception
	 */
	public JSONArray getKakaoMessageSendResultItems( String srchStDttm, String srchEdDttm )throws Exception {
		String callUrl = this.KAKAO_MESSAGE_HOST + "/alimtalk/v2.3/appkeys/" + GsntalkEncryptor.decrypt( this.KAKAO_MESSAGE_APP_KEY ) +"/message-results?";
		String queryString = "startUpdateDate=" + URLEncoder.encode( srchStDttm, "UTF-8" ) + "&";
		queryString += "endUpdateDate=" + URLEncoder.encode( srchEdDttm, "UTF-8" ) + "&";
		queryString += "pageNum=1&";
		queryString += "pageSize=100";
		
		HttpsURLConnection conn = (HttpsURLConnection) new URL( callUrl + queryString ).openConnection();
		conn.setUseCaches( false );
		conn.setRequestMethod( "GET" );
		conn.setRequestProperty( "X-Secret-Key", GsntalkEncryptor.decrypt( this.KAKAO_MESSAGE_SECRET_KEY ) );
		conn.setRequestProperty( "Content-Type", "application/json;charset=UTF-8" );
		
		BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		StringBuffer sb = new StringBuffer();
		String resultLine = null;
			
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		JSONObject resObj = (JSONObject)this.jsonParser.parse( sb.toString() );
		return GsntalkUtil.getJSONArray( resObj, "messages" );
	}
	
	/**
	 * 네이버 로그인 인증 및 회원정보 조회
	 * 
	 * @param code
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public JSONObject getNaverUserItem( String code, String state )throws Exception {
		String callUrl = this.NAVER_LOGIN_REQUEST_TOKEN_URI + "?";
		callUrl += "grant_type=authorization_code&";
		callUrl += "client_id=" + GsntalkEncryptor.decrypt( this.NAVER_CLIENT_ID ) + "&";
		callUrl += "client_secret=" + GsntalkEncryptor.decrypt( this.NAVER_CLIENT_SECRET ) + "&";
		callUrl += "code=" + code + "&";
		callUrl += "state=" + state;
		
		this.LOGGER.info( "########### call getNaverUserItem : " + callUrl );
		
		// 접근 토큰 발급 요청
		HttpsURLConnection conn = (HttpsURLConnection) new URL( callUrl ).openConnection();
		conn.setDoInput( true );
		conn.setDoOutput( true );
		conn.setUseCaches( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/json" );
		
		BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		StringBuffer sb = new StringBuffer();
		String resultLine = null;
			
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		JSONObject tokenObj = (JSONObject)this.jsonParser.parse( sb.toString() );
		this.LOGGER.info( "########### getNaverUserItem - 1 tokenObj : " + tokenObj.toJSONString() );
		String error = GsntalkUtil.getString( tokenObj.get( "error" ) );
		if( !GsntalkUtil.isEmpty( error ) ) {
			// SNS 로그인 연동 실패
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_TO_SNS_LOGIN );
		}
		
		String accessToken = GsntalkUtil.getString( tokenObj.get( "access_token" ) );
		
		// 접근 토큰을 이용하여 프로필 API 호출
		conn = (HttpsURLConnection) new URL( this.NAVER_LOGIN_REQUEST_PROFILE_URI ).openConnection();
		conn.setDoInput( true );
		conn.setDoOutput( true );
		conn.setUseCaches( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/json" );
		conn.setRequestProperty( "Authorization", "Bearer " + accessToken );	
		
		br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		sb = new StringBuffer();
		resultLine = null;
		
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		JSONObject profileObj = (JSONObject)this.jsonParser.parse( sb.toString() );
		this.LOGGER.info( "########### getNaverUserItem - 2 profileObj : " + profileObj.toJSONString() );
		
		error = GsntalkUtil.getString( profileObj.get( "error" ) );
		if( !GsntalkUtil.isEmpty( error ) ) {
			// SNS 로그인 연동 실패
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_TO_SNS_LOGIN );
		}
		
		return profileObj;
	}
	
	/**
	 * 카카오 로그인 인증 및 회원정보 조회
	 * 
	 * @param code
	 * @param wich
	 * @return
	 * @throws Exception
	 */
	public JSONObject getKakaoUserItem( String code, String wich )throws Exception {
		String redirectUrl = null;
		if( GsntalkConstants.SNS_LOGIN_CALL_TYPE_SIGN_UP.equals( wich ) ) {
			redirectUrl = URLEncoder.encode( this.KAKAO_LOGIN_SIGN_UP_RETURN_URI, "UTF-8" );
		}else if( GsntalkConstants.SNS_LOGIN_CALL_TYPE_SIGN_IN.equals( wich ) ) {
			redirectUrl = URLEncoder.encode( this.KAKAO_LOGIN_SIGN_IN_RETURN_URI, "UTF-8" );
		}
		
		String callUrl = this.KAKAO_LOGIN_REQUEST_TOKEN_URI + "?";
		callUrl += "grant_type=authorization_code&";
		callUrl += "client_id=" + GsntalkEncryptor.decrypt( this.KAKAO_REST_API_KEY ) + "&";
		callUrl += "redirect_uri=" + redirectUrl + "&";
		callUrl += "code=" + code + "&";
		callUrl += "client_secret=" + GsntalkEncryptor.decrypt( this.KAKAO_CLIENT_SECRET );
		
		this.LOGGER.info( ">>>>>>>>>>>>>>> call getKakaoUserItem : " + callUrl );
		
		// 접근 토큰 발급 요청
		HttpsURLConnection conn = (HttpsURLConnection) new URL( callUrl ).openConnection();
		conn.setDoInput( true );
		conn.setDoOutput( true );
		conn.setUseCaches( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded;charset=utf-8" );
		
		BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		StringBuffer sb = new StringBuffer();
		String resultLine = null;
		
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		JSONObject tokenObj = (JSONObject)this.jsonParser.parse( sb.toString() );
		this.LOGGER.info( ">>>>>>>>>>>>>>>getKakaoUserItem - 1 tokenObj : " + tokenObj.toJSONString() );
		
		String accessToken = GsntalkUtil.getString( tokenObj.get( "access_token" ) );
		
		// 접근 토큰을 이용하여 프로필 API 호출
		conn = (HttpsURLConnection) new URL( this.KAKAO_LOGIN_REQUEST_PROFILE_URL ).openConnection();
		conn.setDoInput( true );
		conn.setDoOutput( true );
		conn.setUseCaches( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded;charset=utf-8" );
		conn.setRequestProperty( "Authorization", "Bearer " + accessToken );	
		
		br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		sb = new StringBuffer();
		resultLine = null;
		
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		this.LOGGER.info( "getKakaoUserItem - 2 userItem : " + sb.toString() );
		
		return (JSONObject)this.jsonParser.parse( sb.toString() );
	}
	
	/**
	 * 사업자 진위여부 확인 및 상태조회
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject validationBizinfo( String bizNo, String openRegDate, String reprNm  )throws Exception {
		// 사업자 진위여부 확인
		HttpsURLConnection conn = (HttpsURLConnection) new URL( this.DATA_GO_BIZ_VALIDATION_URI + "?serviceKey=" + this.DATA_GO_BIZ_API_KEY ).openConnection();
		conn.setDoInput( true );
		conn.setDoOutput( true );
		conn.setUseCaches( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/json" );
		
		JSONObject business = new JSONObject();
		business.put( "b_no", GsntalkUtil.parseNumberString( bizNo ) );
		business.put( "start_dt", GsntalkUtil.parseNumberString( openRegDate ) );
		business.put( "p_nm", reprNm );
		
		JSONArray businesses = new JSONArray();
		businesses.add( business );
		
		JSONObject param = new JSONObject();
		param.put( "businesses", businesses );
		
		OutputStream os = conn.getOutputStream();
		os.write( param.toJSONString().getBytes( "UTF-8" ) );
		os.flush();
		os.close();
		
		BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		StringBuffer sb = new StringBuffer();
		String resultLine = null;
		
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		JSONObject validationRsltObj = (JSONObject)this.jsonParser.parse( sb.toString() );
		JSONObject data = (JSONObject)GsntalkUtil.getJSONArray( validationRsltObj, "data" ).get( 0 );
		String valid = GsntalkUtil.getString( data.get( "valid" ) );
		if( !"01".equals( valid ) ) {
			// 사업자 진위여부 불일치
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_TO_BIZ_VALIDATION );
		}
		
		JSONObject status = GsntalkUtil.getJSONObject( data, "status" );
		String bizStateGbCd = GsntalkUtil.getString( status.get( "b_stt_cd" ) );				// 사업자 상태
		String taxationTypGbCd = GsntalkUtil.getString( status.get( "tax_type_cd" ) );			// 과세유형 구분
		String endDate = GsntalkUtil.getString( status.get( "end_dt" ) );						// 폐업일자 ( 사업자 상태가 폐업인경우 )
		
		JSONObject resObj = new JSONObject();
		resObj.put( "bizStateGbCd", bizStateGbCd );
		resObj.put( "taxationTypGbCd", taxationTypGbCd );
		resObj.put( "endDate", endDate );
		
		return resObj;
	}
	
	/**
	 * 주소 -> 좌표 변환
	 * 
	 * @param fullAddress
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getGeocode( String fullAddress, boolean isTest ) throws Exception {
		String callUrl = this.MAPS_API_GEOCODE_URI + "?";
		callUrl += "query=" + URLEncoder.encode( fullAddress, "UTF-8" );
		
		// 접근 토큰 발급 요청
		HttpsURLConnection conn = (HttpsURLConnection) new URL( callUrl ).openConnection();
		conn.setUseCaches( false );
		conn.setRequestMethod( "GET" );
		conn.setRequestProperty( "X-NCP-APIGW-API-KEY-ID", GsntalkEncryptor.decrypt( this.MAPS_API_CLIENT_ID ) );
		conn.setRequestProperty( "X-NCP-APIGW-API-KEY", GsntalkEncryptor.decrypt( this.MAPS_API_CLIENT_SECRET ) );
		conn.setRequestProperty( "Content-Type", "application/json;charset=UTF-8" );
		
		BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
		StringBuffer sb = new StringBuffer();
		String resultLine = null;
		
		while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
			sb.append( resultLine );
		}
		br.close();
		conn.disconnect();
		
		JSONObject resItem = (JSONObject)this.jsonParser.parse( sb.toString() );
		if( isTest ) {
			return resItem;
		}
		
		JSONArray addresses = GsntalkUtil.getJSONArray( resItem, "addresses" );
		JSONObject geocodeItem = null;
		
		if( !GsntalkUtil.isEmptyArray( addresses ) ) {
			JSONObject addressItem = (JSONObject)addresses.get( 0 );
			JSONArray addressItems = GsntalkUtil.getJSONArray( addressItem, "addressElements" );
			JSONArray types = null;
			JSONObject addressSubItem = null;
			String shortNm = "";
			
			// first - 시군구 추출
			for( int i = 0; i < addressItems.size(); i ++ ) {
				addressSubItem = (JSONObject)addressItems.get( i );
				types = GsntalkUtil.getJSONArray( addressSubItem, "types" );
				if( GsntalkUtil.getString( types.get( 0 ) ).equals( "SIGUGUN" ) ) {
					shortNm += GsntalkUtil.getString( addressSubItem.get( "shortName" ) );
					break;
				}
			}
			
			// second - 동면 추출
			for( int i = 0; i < addressItems.size(); i ++ ) {
				addressSubItem = (JSONObject)addressItems.get( i );
				types = GsntalkUtil.getJSONArray( addressSubItem, "types" );
				if( GsntalkUtil.getString( types.get( 0 ) ).equals( "DONGMYUN" ) ) {
					shortNm += " " + GsntalkUtil.getString( addressSubItem.get( "shortName" ) );
					break;
				}
			}
			
			geocodeItem = new JSONObject();
			geocodeItem.put( "lat", GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( addressItem.get( "y" ) ) ) );
			geocodeItem.put( "lng", GsntalkUtil.trimLatLng( GsntalkUtil.getDouble( addressItem.get( "x" ) ) ) );
			geocodeItem.put( "addrShortNm", shortNm );
		}
		
		return geocodeItem;
	}
	
	/**
	 * 행정안전부_행정표준코드_법정동코드 조회
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getStandardRegionData( String searchLocalNm, int pageNo, int retryCnt, Exception re )throws Exception {
		if( retryCnt > 10 ) {
			this.LOGGER.info( "########## drop this process over 10 tried..." );
			if( re != null ) {
				throw re;
			}
		}
		
		JSONObject item = new JSONObject();
		
		String requestUrl = this.DATA_GO_STANDARD_REGION_URI
				+ "?serviceKey=" + this.DATA_GO_BIZ_API_KEY
				+ "&pageNo=" + pageNo
				+ "&numOfRows=" + this.GET_RECORD_PER_PAGE
				+ "&type=json"
				+ "&locatadd_nm=" + URLEncoder.encode( searchLocalNm, "UTF-8" );
		
		HttpURLConnection conn = null;
		JSONObject responseItem = null;
		JSONArray regionDataItems = null;
		JSONObject regionDataItem = null;
		JSONArray headItems = null;
		JSONObject headItem = null;
		JSONArray regionItems = null;
		Object tot = null;
		int totalCount = 0;
		try {
			conn = (HttpURLConnection) new URL( requestUrl ).openConnection();
			conn.setDoInput( true );
			conn.setDoOutput( true );
			conn.setUseCaches( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/json" );
			conn.setConnectTimeout( 5000 );										// set connection timeout 5 seconds
			
			BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream(), "UTF-8" ) );
			StringBuffer sb = new StringBuffer();
			String resultLine = null;
			
			while( !GsntalkUtil.isNull( ( resultLine = br.readLine() ) ) ) {
				sb.append( resultLine );
			}
			br.close();
			conn.disconnect();
			
			responseItem = (JSONObject)this.jsonParser.parse( sb.toString() );
			regionDataItems = GsntalkUtil.getJSONArray( responseItem, "StanReginCd" );
			if( GsntalkUtil.isEmptyArray( regionDataItems ) ) {
				item.put( "totalCount", totalCount );
				return item;
			}
			
			// head 가 있는 object 추출
			for( int i = 0; i < regionDataItems.size(); i ++ ) {
				regionDataItem = (JSONObject)regionDataItems.get(i);
				headItems = GsntalkUtil.getJSONArray( regionDataItem, "head" );
				
				
				if( GsntalkUtil.isEmptyArray( headItems ) ) {
					continue;
				}else {
					for( int j = 0; j < headItems.size(); j ++ ) {
						headItem = (JSONObject)headItems.get(j);
						tot = headItem.get( "totalCount" );
						if( tot != null ) {
							totalCount = GsntalkUtil.getInteger( headItem.get( "totalCount" ) );
							break;
						}
					}
					break;
				}
			}
			if( totalCount == 0 ) {
				item.put( "totalCount", totalCount );
				return item;
			}
			
			
			// row(데이터) 가 있는 object 추출
			for( int i = 0; i < regionDataItems.size(); i ++ ) {
				regionDataItem = (JSONObject)regionDataItems.get(i);
				regionItems = GsntalkUtil.getJSONArray( regionDataItem, "row" );
				
				if( GsntalkUtil.isEmptyArray( regionItems ) ) {
					continue;
				}else {
					break;
				}
			}
			if( GsntalkUtil.isEmptyArray( regionItems ) ) {
				item.put( "totalCount", totalCount );
				return item;
			}
			
		}catch( Exception e ) {
			this.LOGGER.error( "########## GovDataPotalApisUtil.updateStandardRegionData Exception : " + e.getClass().getName(), e.getLocalizedMessage() );
			this.LOGGER.info( "########## sleep 5 seconds.... ##########" );
			Thread.sleep( 5000 );
			this.LOGGER.info( "########## TRY Again ##########" );
			
			// 50번까지 재귀
			return this.getStandardRegionData( searchLocalNm, pageNo, retryCnt + 1, e );
		}
		
		if( pageNo == 1 ) {
			this.LOGGER.info( ">>>>>>>>>> [ " + searchLocalNm + " ] get " + GsntalkUtil.set1000Comma( totalCount ) + " data done. <<<<<<<<<<" );
		}
		
		item.put( "totalCount", totalCount );
		item.put( "regionItems", regionItems );
		
		return item;
	}
	
	
	
	
	
	
	
	private TrustManager[] createTrustManagers() {
		TrustManager[] tmCerts = new TrustManager[] {
			new X509TrustManager() {
				
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
				
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			}
		};
		return tmCerts;
	}
}