package com.gsntalk.api.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkEncryptor;
import com.gsntalk.api.util.GsntalkUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class GsntalkJWTUtil {

	// token 만료 : 3시간
	private static final long TOKEN_EXPIRES = 1000 * 60 * 60 * 3;
	
	private static final String JWT_SECRET_KEY = "n9IDPqwFuJV7NW2S2hc8o407G6vKzG8Q466L";
	private static final SignatureAlgorithm JWT_ALGORITHM = SignatureAlgorithm.HS256;
	
	private static String tokenCompress( String uncompressedToken ) throws IOException {
		byte[] dataByte = uncompressedToken.getBytes();
		
		Deflater deflater = new Deflater();
		
		deflater.setLevel( Deflater.BEST_COMPRESSION );
		deflater.setInput( dataByte );
		deflater.finish();
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream( dataByte.length );
		byte[] buf = new byte[1024];
		while(!deflater.finished()) {
			int compByte = deflater.deflate(buf);
			bao.write(buf, 0, compByte);
		}
		
		bao.close();
		deflater.end();
		
		byte[] b = bao.toByteArray();
		
		// byte to hex-string
		StringBuffer sb = new StringBuffer( b.length * 2);
		String hexNumber;
		for (int x = 0; x < b.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & b[x]);

			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}
	
	private static String tokenDecompress( String compressedToken ) throws IOException, DataFormatException {
		// hex-string to byte
		byte[] ba = new byte[ compressedToken.length() / 2 ];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt( compressedToken.substring( 2 * i, 2 * i + 2 ), 16 );
		}
		Inflater inflater = new Inflater();
		inflater.setInput( ba );
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		
		try {
			while( !(inflater.finished() || inflater.needsDictionary() || inflater.needsInput() ) ) {
				int compByte = inflater.inflate( buf );			
				bao.write( buf, 0, compByte );
			}
		}catch( Exception e ) {
			throw e;
		}finally {
			bao.close();
		}
		
		inflater.end();
		
		return new String( bao.toByteArray() );
	}
	
	private static String createNewLoginToken( long memSeqno ) throws Exception {
		String token = GsntalkUtil.getServerTime( "%H%i%s" ) + GsntalkEncryptor.encrypt( String.valueOf( memSeqno )) + GsntalkUtil.getRandomString( -1, 12 );
		return GsntalkEncryptor.sha512( token );
	}
	
	@SuppressWarnings( "unchecked" )
	public static String createNewJWTToken( long memSeqno, JSONObject userItem ) throws Exception {
		userItem.put( GsntalkConstants.GSN_TALK_LOGIN_TOKEN, GsntalkJWTUtil.createNewLoginToken( memSeqno ) );
		
		Key signingKey = Keys.hmacShaKeyFor( GsntalkJWTUtil.JWT_SECRET_KEY.getBytes( StandardCharsets.UTF_8 ) );
		
		String token = Jwts.builder()
				.setHeaderParam( Header.TYPE, Header.JWT_TYPE )
				.setIssuer( "api.gsntalk" )
				.setIssuedAt( new Date() )
				.claim( "userItem", userItem.toJSONString() )
				.setExpiration( new Date( System.currentTimeMillis() + GsntalkJWTUtil.TOKEN_EXPIRES ) )
				.signWith( signingKey, GsntalkJWTUtil.JWT_ALGORITHM )
				.compact();
		
		try {
			token = tokenCompress( token );
		}catch( Exception e ) {
			e.printStackTrace();
		}
		
		return token;
	}
	
	public static String updateJWTToken( HttpServletRequest request ) throws Exception {
		String compressedToken = request.getHeader( GsntalkConstants.GSN_TALK_TOKEN_KEY );
		if( GsntalkUtil.isEmpty( compressedToken ) ) {
			return null;
		}
		
		Key signingKey = Keys.hmacShaKeyFor( GsntalkJWTUtil.JWT_SECRET_KEY.getBytes( StandardCharsets.UTF_8 ) );
		
		String decompressedToken = null;
		try {
			decompressedToken = tokenDecompress( compressedToken );
		}catch( Exception e ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		JSONObject userItem = null;
		try {
			Claims claims = Jwts.parserBuilder()
								.setSigningKey( signingKey )
								.build()
								.parseClaimsJws( decompressedToken )
								.getBody();
			
			userItem = (JSONObject)new JSONParser().parse( GsntalkUtil.getString( claims.get( "userItem" ) ) );
		}catch( ExpiredJwtException e ) {
			// JWT 토큰 만료
			throw new GsntalkAPIException( GsntalkAPIResponse.EXPIRED_JWT_TOKEN );
		}catch( Exception e ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}		
		
		String token = Jwts.builder()
				.setHeaderParam( Header.TYPE, Header.JWT_TYPE )
				.setIssuer( "api.gsntalk" )
				.setIssuedAt( new Date() )
				.claim( "userItem", userItem.toJSONString() )
				.setExpiration( new Date( System.currentTimeMillis() + GsntalkJWTUtil.TOKEN_EXPIRES ) )
				.signWith( signingKey, GsntalkJWTUtil.JWT_ALGORITHM )
				.compact();
		
		try {
			token = tokenCompress( token );
		}catch( Exception e ) {
			e.printStackTrace();
		}
		
		return token;
	}
	
	public static MemberVO getMemberVO( HttpServletRequest request ) throws Exception {
		String compressedToken = request.getHeader( GsntalkConstants.GSN_TALK_TOKEN_KEY );
		if( GsntalkUtil.isEmpty( compressedToken ) ) {
			return null;
		}
		
		Key signingKey = Keys.hmacShaKeyFor( GsntalkJWTUtil.JWT_SECRET_KEY.getBytes( StandardCharsets.UTF_8 ) );
		
		String decompressedToken = null;
		try {
			decompressedToken = tokenDecompress( compressedToken );
		}catch( Exception e ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		JSONObject userItem = null;
		
		try {
			Claims claims = Jwts.parserBuilder()
								.setSigningKey( signingKey )
								.build()
								.parseClaimsJws( decompressedToken )
								.getBody();
			
			userItem = (JSONObject)new JSONParser().parse( GsntalkUtil.getString( claims.get( "userItem" ) ) );
		}catch( ExpiredJwtException e ) {
			// JWT 토큰 만료
			throw new GsntalkAPIException( GsntalkAPIResponse.EXPIRED_JWT_TOKEN );
		}catch( Exception e ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		MemberVO memberVO = new MemberVO();
		memberVO.setMemTypCd( GsntalkUtil.getString( userItem.get( "memTypCd" ) ) );
		memberVO.setEstBrkMemOfcSeqno( GsntalkUtil.getLong( userItem.get( "estBrkMemOfcSeqno" ) ) );
		memberVO.setMemSeqno( GsntalkUtil.getLong( userItem.get( "memSeqno" ) ) );
		memberVO.setMemName( GsntalkUtil.getString( userItem.get( "memName" ) ) );
		memberVO.setEmail( GsntalkUtil.getString( userItem.get( "email" ) ) );
		memberVO.setPrflImgUrl( GsntalkUtil.getString( userItem.get( "prflImgUrl" ) ) );
		memberVO.setLoginToken( GsntalkUtil.getString( userItem.get( GsntalkConstants.GSN_TALK_LOGIN_TOKEN ) ) );
		
		return memberVO;
	}
	
	public static MemberVO getMemberVO( String compressedToken ) throws Exception {
		if( GsntalkUtil.isEmpty( compressedToken ) ) {
			return null;
		}
		
		Key signingKey = Keys.hmacShaKeyFor( GsntalkJWTUtil.JWT_SECRET_KEY.getBytes( StandardCharsets.UTF_8 ) );
		
		String decompressedToken = null;
		try {
			decompressedToken = tokenDecompress( compressedToken );
		}catch( Exception e ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		JSONObject userItem = null;
		
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey( signingKey )
					.build()
					.parseClaimsJws( decompressedToken )
					.getBody();
			
			userItem = (JSONObject)new JSONParser().parse( GsntalkUtil.getString( claims.get( "userItem" ) ) );
		}catch( ExpiredJwtException e ) {
			// JWT 토큰 만료
			throw new GsntalkAPIException( GsntalkAPIResponse.EXPIRED_JWT_TOKEN );
		}catch( Exception e ) {
			// JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
		}
		
		MemberVO memberVO = new MemberVO();
		memberVO.setMemTypCd( GsntalkUtil.getString( userItem.get( "memTypCd" ) ) );
		memberVO.setMemSeqno( GsntalkUtil.getLong( userItem.get( "memSeqno" ) ) );
		memberVO.setMemName( GsntalkUtil.getString( userItem.get( "memName" ) ) );
		memberVO.setEmail( GsntalkUtil.getString( userItem.get( "email" ) ) );
		memberVO.setPrflImgUrl( GsntalkUtil.getString( userItem.get( "prflImgUrl" ) ) );
		memberVO.setLoginToken( GsntalkUtil.getString( userItem.get( GsntalkConstants.GSN_TALK_LOGIN_TOKEN ) ) );
		
		return memberVO;
	}
	
	public static String getLoginToken( HttpServletRequest request ) throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
		return memberVO.getLoginToken();
	}
	
	public static String getLoginToken( String compressedToken ) throws Exception {
		MemberVO memberVO = GsntalkJWTUtil.getMemberVO( compressedToken );
		return memberVO.getLoginToken();
	}
}