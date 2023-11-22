package com.gsntalk.api.util;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GsntalkUtil {

	public static final String SEPARATOR = "/";
	
	private static final double METERS_BY_PYUNG = 3.30579d;
	
	private static final String EOG = "100000000";
	private static final String CHEONMAN = "10000000";
	private static final String MAN = "10000";
	
	private static final String[] ACCEPT_IMAGE_FORMATS = { "jpg", "jpeg", "png" };
	
	public static boolean isNull( Object obj ) {
		return obj == null;
	}
	
	public static boolean isEmpty( Object obj ){
		if( obj == null ){
			return true;
		}
		if( obj instanceof String ){
			return "".equals( ( (String)obj ).trim() );
		}
		if( obj instanceof Integer ){
			return false;
		}
		return false;
	}
	
	public static boolean isEmptyList( List<?> list ) {
		if( list == null ) {
			return true;
		}
		return list.isEmpty();
	}
	
	public static boolean isEmptyMap( Map<?, ?> map ) {
		if( map == null ) {
			return true;
		}
		return map.isEmpty();
	}
	
	public static boolean isEmptyArray( Object[] os ) {
		if( os == null || os.length == 0 ) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmptyArray( JSONArray arr ) {
		if( arr == null || arr.size() == 0 ) {
			return true;
		}
		return false;
	}
	
	public static boolean isIn( String val, String... s ){
		if( val == null ){
			return false;
		}
		for( String str : s ){
			if( val.equals( str ) ){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isIn( int val, int... i ){
		for( int v : i ){
			if( val == v ){
				return true;
			}
		}
		return false;
	}
	
	public static String nullToEmptyString( Object o ) {
		if(  o == null ) {
			return "";
		}else {
			return ( (String)o ).trim();
		}
	}
	
	public static String ifEmptyString( Object o, String emptyValue ) {
		return isEmpty( o ) ? emptyValue : getString( o );
	}
	
	public static String ifEmptyString( String s, String emptyValue ) {
		return isEmpty( s ) ? emptyValue : s;
	}
	
	public static void makeDirs( String path ) {
		File d = new File( path );
		d.mkdirs();
	}
	
	public static boolean isPhotoFormat( String format ) {
		format = format.toUpperCase();
		return "JPG".equals( format ) || "JPEG".equals( format );
	}
	
	/**
	 * 이메일 포맷 검증
	 * @param email
	 * @return
	 */
	public static boolean isEmailFormat( String email ) {
		// @ 뒤 N차 서브도메인까지 일괄 수용
		return isCorrectRegExp( email, "[0-9a-zA-Z-_.]{3,}@[0-9a-zA-Z-_]{2,}(([.][0-9a-zA-Z]{2,}))+" );
	}
	
	/**
	 * 휴대폰번호 형식 검증
	 * 
	 * @param mobNo
	 * @return
	 */
	public static boolean isMobnoFormat( String mobNo ) {
		if( isEmpty( mobNo ) ) {
			return false;
		}
		if( mobNo.length() < 10 || mobNo.length() > 11 ) {
			return false;
		}
		if( !mobNo.startsWith( "01" ) ) {
			return false;
		}
		return true;
	}
	
	/**
	 * 전화번호 형식 검증
	 * EX) ( 1566-1234 / 02-123-4567 / 051-1234-5678  )
	 *
	 * @param telNo
	 * @return
	 */
	public static boolean isTelFormat( String telNo ) {
		if( isEmpty( telNo ) ) {
			return false;
		}
		
		telNo = parseNumberString( telNo );
		if( telNo.length() < 8 || telNo.length() > 11 ) {
			return false;
		}
		return true;
	}
	
	/**
	 * 비밀번호 포맷 검증
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean isPasswordFormat( String pwd ) {
		if( isEmpty( pwd ) ) {
			return false;
		}
		if( pwd.length() < 8 ) {
			return false;
		}
		return !pwd.equals( pwd.replaceAll( "[0-9]", "" ) ) && !pwd.equals( pwd.replaceAll( "[a-zA-Z]", "" ) );
	}
	
	public static boolean is12DateTimeFormat( String dttm ) {
		if( isEmpty( dttm ) ) {
			return false;
		}
		
		dttm = GsntalkUtil.parseNumberString( dttm );
		if( dttm.length() != 12 ) {
			return false;
		}
		
		String date = dttm.substring( 0, 8 );
		if( !GsntalkUtil.is8DateFormat( date, true ) ) {
			return false;
		}
		
		int h = Integer.valueOf( dttm.substring( 8, 10 ) );
		int m = Integer.valueOf( dttm.substring( 10, 12 ) );
		if( h < 0 || h > 24 ) {
			return false;
		}
		if( m < 0 || m > 59 ) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 8자리 날자포맷 검증 ( YYYYMMDD / YYYY{asta}MM{asta}DD )
	 * 
	 * @param date
	 * @param accpetPast
	 * @return
	 */
	public static boolean is8DateFormat( String date, boolean accpetPast ) {
		if( isEmpty( date ) ) {
			return false;
		}
		
		date = GsntalkUtil.parseNumberString( date );
		if( date.length() != 8 ) {
			return false;
		}
		
		// 과거일자를 허용하지 않는경우 검증
		String toDate = getServerTime( "%Y%m%d" );
		if( !accpetPast ) {
			if( getInteger( date ) < getInteger( toDate ) ) {
				return false;
			}
		}
		
		// 입력받은 값의 연,월,일
		int pY = getInteger( date.substring( 0, 4 ) );
		int pM = getInteger( date.substring( 4, 6 ) );
		int pD = getInteger( date.substring( 6 ) );
		
		// 현재연도
		int tY = getInteger( toDate.substring( 0, 4 ) );
		
		// 현재연도를 기준으로 50년 초과시 예외처리
		if( pY < tY - 50 || pY > tY + 50 ) {
			return false;
		}
		// 월 범위 초과시 예외처리 ( 1 ~ 12 )
		if( pM < 1 || pM > 12 ) {
			return false;
		}
		// 해당 월의 일 범위 초과시 예외처리
		Calendar cal = Calendar.getInstance();
		cal.set( pY, pM -1, 1 );
		if( pD < 1 || cal.getActualMaximum( Calendar.DATE ) < pD ) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 6자리 월포맷 검증 ( YYYYMMDD / YYYY{asta}MM{asta}DD )
	 * 
	 * @param date
	 * @param accpetPast
	 * @return
	 */
	public static boolean is6MonthFormat( String date, boolean accpetPast ) {
		if( isEmpty( date ) ) {
			return false;
		}
		
		date = GsntalkUtil.parseNumberString( date );
		if( date.length() != 6 ) {
			return false;
		}
		
		// 과거일자를 허용하지 않는경우 검증
		String toDate = getServerTime( "%Y%m" );
		if( !accpetPast ) {
			if( getInteger( date ) < getInteger( toDate ) ) {
				return false;
			}
		}
		
		// 입력받은 값의 연,월
		int pY = getInteger( date.substring( 0, 4 ) );
		int pM = getInteger( date.substring( 4, 6 ) );
		
		// 현재연도
		int tY = getInteger( toDate.substring( 0, 4 ) );
		
		// 현재연도를 기준으로 50년 초과시 예외처리
		if( pY < tY - 50 || pY > tY + 50 ) {
			return false;
		}
		// 월 범위 초과시 예외처리 ( 1 ~ 12 )
		if( pM < 1 || pM > 12 ) {
			return false;
		}
		
		return true;
	}
	
	public static String getString( Object o ) {
		if( o == null ) {
			return "";
		}
		
		return ( String.valueOf( o ) ).trim();
	}
	
	public static Long getLong( Object o ) {
		if( o == null ) {
			return 0L;
		}else if( o instanceof Double ) {
			return (long)( (Double)o * 1 );
		}else if( o instanceof Long ) {
			return (Long)o;
		}else if( o instanceof Integer ) {
			return (long)(Integer)o;
		}else if( o instanceof String ) {
			if( isEmpty( (String)o ) ) {
				return 0L;
			}
			return Long.parseLong( (String)o );
		}else {
			return 0L;
		}
	}
	
	public static Double getDouble( Object o ) {
		if( o == null ) {
			return 0.0D;
		}else if( o instanceof Double ) {
			return (Double)o;
		}else if( o instanceof Long ) {
			return Double.parseDouble( (Long)o + "" );
		}else if( o instanceof Integer ) {
			return Double.parseDouble( (Integer)o + "" );
		}else if( o instanceof String ) {
			if( isEmpty( (String)o ) ) {
				return 0.0D;
			}
			return Double.parseDouble( (String)o );
		}else {
			return 0.0D;
		}
	}
	
	public static Float getFloat( Object o ) {
		if( o == null ) {
			return 0.0F;
		}else if( o instanceof Double ) {
			return Float.parseFloat( String.valueOf( o ) );
		}else if( o instanceof Long ) {
			return Float.parseFloat( (Long)o + "" );
		}else if( o instanceof Integer ) {
			return Float.parseFloat( (Integer)o + "" );
		}else if( o instanceof String ) {
			if( isEmpty( (String)o ) ) {
				return 0.0F;
			}
			return Float.parseFloat( (String)o );
		}else {
			return 0.0F;
		}
	}
	
	public static Integer getInteger( Object o ) {
		if( o == null ) {
			return 0;
		}else if( o instanceof Double ) {
			return (int)( (Double)o * 1 );
		}else if( o instanceof Float ) {
			return (int)( (Float)o * 1 );
		}else if( o instanceof Integer ) {
			return (Integer)o;
		}else if( o instanceof Long ) {
			return Integer.parseInt( (Long)o + "" );
		}else if( o instanceof String ) {
			if( isEmpty( (String)o ) ) {
				return 0;
			}
			return Integer.parseInt( (String)o );
		}else {
			return 0;
		}
	}
	
	public static boolean getBoolean( Object o ) {
		if( o == null ) {
			return false;
		}else if( o instanceof Boolean ) {
			return Boolean.parseBoolean( String.valueOf( o ) );
		}else {
			return false;
		}
	}
	public static String set1000Comma( Object o ){
		if( o == null ) {
			return "0";
		}else{
			return set1000CommaAlgorithm( String.valueOf( o ) );
		}
	}
	
	public static JSONObject getJSONObject( JSONObject o, String key ) {
		if( o.get( key ) == null ) {
			return new JSONObject();
		}else {
			return (JSONObject)o.get( key );
		}
	}
	
	public static JSONArray getJSONArray( JSONObject o, String key ) {
		if( o.get( key ) == null ) {
			return new JSONArray();
		}else {
			return (JSONArray)o.get( key );
		}
	}
	
	/**
	 * XSS-filter encoding
	 * @param s
	 * @return
	 */
	public static String encodingXSS( Object o ) {
		if( o == null ) {
			return "";
		}
		
		String s = (String)o;
		
		s = s.replaceAll( "\t", " " );				// tab to space
		s = s.replaceAll( "script", "zcribt" );
		s = s.replaceAll( "&nbsp;", "" );
		s = s.replaceAll( "&nbsp", "" );
		s = s.replaceAll( ";", "&semi;" );
		s = s.replaceAll( "%", "&#37;" );
		s = s.replaceAll( "#", "&#35;" );
		s = s.replaceAll( "'", "&apos;" );
		s = s.replaceAll( "\"", "&quot;" );
		s = s.replaceAll( "<", "&lt;" );
		s = s.replaceAll( ">", "&gt;" );
		s = s.replaceAll( "\\*", "&#42;" );
		s = s.replaceAll( "/", "&#47;" );
		
		return s.trim();
	}
	
	public static String decodingXss( Object o ) {
		if( o == null ) {
			return "";
		}
		
		String s = (String)o;
		s = s.replaceAll( "&#47;", "/" );
		s = s.replaceAll( "&#42;", "\\*" );
		s = s.replaceAll( "&gt;", ">" );
		s = s.replaceAll( "&lt;", "<" );
		s = s.replaceAll( "&quot;", "\"" );
		s = s.replaceAll( "&apos;", "'" );
		s = s.replaceAll( "&#35;", "#" );
		s = s.replaceAll( "&#37;", "%" );
		s = s.replaceAll( "&semi;", ";" );
		
		return s.trim();
	}
	
	/**
	 * GPS 위경도 값을 GoogleMaps Long Value로 치환
	 * @param degreeVal
	 * @return
	 */
	public static double degreeLocationToLongValue ( String degreeVal ) {
		String sign = "";
		if( degreeVal.startsWith("-") ) {
			sign = "-";
			degreeVal = degreeVal.replace( "-", "" );
		}
		
		degreeVal = degreeVal.replaceAll( "[^\\d]+", " " );					// 연속적 숫자가 아닌것을 하나의 공백으로 치환.
		String[] arr = degreeVal.split( " " );								// 공백을 기준으로 쪼갬
		
		int degree = Integer.parseInt( arr[0] );							// 도 ( ° )
		
		int minutes = Integer.parseInt( arr[1] );							// 분 ( ' )
		double seconds = arr.length == 3 ? Double.parseDouble( arr[2] + ".0" ) : Double.parseDouble( arr[2] + "." + arr[3] );		// 초 ( " ), arr length 가 3이면 소숫점 자리 정보가 없으므로 .0을 대입
		
		String results = String.valueOf( ( ( minutes * 60 ) + seconds ) / ( 3600 ) );			// 소숫점 아랫부분만 계산
		results = results.substring( results.indexOf(".") + 1, results.length() );				// 소숫점 아랫부분만 추출
		results = rPad( results, 7, "0" );														// 소숫점 아래 7차리까지만 자르거나 채움
		
		return Double.parseDouble( sign + degree + "." + results );									// 정수부분과 결합하여 return
	}
	
	/**
	 * 위경도 값용 소숫점 7자리 아래 절삭
	 * 
	 * @param latLngVal
	 * @return
	 */
	public static double trimLatLng( double latLngVal ) {
		String s = String.valueOf( latLngVal );
		if( s.substring( s.indexOf( "." ) ).length() > 7 ) {
			s = s.substring( 0, s.indexOf( "." ) + 8 );
		}
		return GsntalkUtil.getDouble( s );
	}
	
	/**
	 * String[] -> JSONArray
	 * @param arr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray parseJSONArray( String[] arr ) {
		if( isEmptyArray( arr ) ) {
			return null;
		}else {
			JSONArray jsonArray = new JSONArray();
			for( String s : arr ) {
				jsonArray.add( s );
			}
			return jsonArray;
		}
	}
	
	/**
	 * \n -> <br>
	 * @param s
	 * @return
	 */
	public static String encodeLineFeed( String s ) {
		s = s.replaceAll( "\n", "<br>" );
		return s.trim();
	}
	
	/**
	 * <br> -> \n
	 * @param s
	 * @return
	 */
	public static String decodeLineFeed( String s ) {
		s = s.replaceAll( "<br>", "\n");
		return s.trim();
	}
	
	public static String parseNumberString( String str ) {
		if( GsntalkUtil.isEmpty( str ) ) {
			str = "";
		}
		return str.replaceAll( "[^\\d]", "" );					// 숫자가 아닌것을 제거.
	}
	
	public static String parseDoubleString( String str ) {
		if( GsntalkUtil.isEmpty( str ) ) {
			str = "";
		}
		return str.replaceAll( "[^\\d.]", "" );					// 숫자화 점이 아닌것을 제거.
	}
	
	public static int parseFloorNumber( String flr ) {
		boolean hasUnder = flr.indexOf( "B" ) != -1 || flr.indexOf( "b" ) != -1 || flr.indexOf( "-" ) != -1;
		return GsntalkUtil.getInteger( ( hasUnder ? "-" : "" ) + GsntalkUtil.parseNumberString( flr ) );
	}
	
	public static String parseFloorNm( int flr ) {
		return ( flr < 0 ? "B" + ( -flr ) : String.valueOf( flr ) ) + "층";
	}
	
	public static String parseTelnoFormat( String telno ) {
		if( GsntalkUtil.isEmpty( telno ) ) {
			return "";
		}
		if( telno.indexOf( "+82" ) != -1 ) {
			telno = telno.replace( "+82", "" ).trim();
			telno = "0" + telno;
		}
		
		telno = GsntalkUtil.parseNumberString( telno );
		
		if( telno.length() < 7 ) {
			return telno;
		}
		
		if( telno.length() < 9 ) {
			return telno.substring( 0, telno.length() - 4 ) + "-" + telno.substring( telno.length() - 4 );
			
		}else if( telno.length() > 11 ) {
			return telno.substring( 0, 4 ) + "-" + telno.substring( 4, telno.length() - 4 ) + "-" + telno.substring( telno.length() - 4 );
			
		}else {
			if( telno.startsWith( "02" ) ) {
				return telno.substring( 0, 2 ) + "-" + telno.substring( 2, telno.length() - 4 ) + "-" + telno.substring( telno.length() - 4 );
			
			}else if( telno.startsWith( "10" ) ) {
				return "0" + telno.substring( 0, 2 ) + "-" + telno.substring( 2, telno.length() - 4 ) + "-" + telno.substring( telno.length() - 4 );
				
			}else {
				return telno.substring( 0, 3 ) + "-" + telno.substring( 3, telno.length() - 4 ) + "-" + telno.substring( telno.length() - 4 );
			}
		}
	}
	
	public static String parseBiznoFormat( String bizno ) {
		if( GsntalkUtil.isEmpty( bizno ) ) {
			return "";
		}
		if( bizno.length() != 10 ) {
			return bizno;
		}
		
		return bizno.substring( 0, 3 ) + "-" + bizno.substring( 3, 5 ) + "-" + bizno.substring( 5, 10 );
	}
	
	/**
	 * GET RANDOM String
	 * @param t ( -1:UPPER-LOWER-NUMBER MIX, 0:UPPER-LOWER MIX, 1:ONLY UPPER, 2:ONLY LOWER, 3: ONLY NUMBER )
	 * @param l
	 * @return
	 */
	public static String getRandomString( int t, int l ){
		String _s = "";
		while( _s.length() < l ){
			_s += getRandomChar( t );
		}
		return _s;
	}
	
	public static String lPad( String base_str, int len, String asta ){
		return lrPad( base_str, len, asta, true );
	}
	
	public static String rPad( String base_str, int len, String asta ){
		return lrPad( base_str, len, asta, false );
	}
	
	public static String createKPrptRegno( int num ) {
		String ymd = GsntalkUtil.getServerTime( "%Y%m%d" );
		String y = ymd.substring( 0, 4 );
		String m = ymd.substring( 4, 6 );
		String d = ymd.substring( 6, 8 );
		
		return "K" + y + "-" + d + GsntalkUtil.lPad( String.valueOf( num ), 3, "0" ) + m;
	}
	
	public static String createPPrptRegno( int num ) {
		String ymd = GsntalkUtil.getServerTime( "%Y%m%d" );
		String y = ymd.substring( 0, 4 );
		String m = ymd.substring( 4, 6 );
		String d = ymd.substring( 6, 8 );
		
		return "P" + y + "-" + d + GsntalkUtil.lPad( String.valueOf( num ), 3, "0" ) + m;
	}
	
	public static String createRegistrationTempKey() {
		String tempKey = "";
		try {
			tempKey = GsntalkEncryptor.sha512( getRandomString( -1, 8 ) + GsntalkUtil.getServerTime( "%Y%m%d%H%i" ) + getRandomString( -1, 8 ) );
		}catch( Exception e ) {}
		return tempKey;
	}
	
	/**
	 * 서버시간 조회 [ MYSQL 포맷 ]
	 * @param f
	 * @return
	 */
	public static String getServerTime( String f ){
		Calendar cal = Calendar.getInstance();
		int Y = cal.get(Calendar.YEAR);
		int M = cal.get(Calendar.MONTH)+1;
		int D = cal.get(Calendar.DATE);
		int H = cal.get(Calendar.HOUR_OF_DAY);
		int h = cal.get(Calendar.HOUR);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		return f.replaceAll( "%Y", Y + "" ).replaceAll( "%m", ( M < 10 ? "0" + M : M + "" ) ).replaceAll( "%d", ( D < 10 ? "0" + D : D + "" ) )
			.replaceAll( "%H", ( H < 10 ? "0" + H : H + "" ) ).replaceAll( "%h", ( h < 10 ? "0" + h : h + "" ) )
			.replaceAll( "%i", ( m < 10 ? "0" + m : m + "" ) ).replaceAll( "%s", ( s < 10 ? "0" + s : s + "" ) );
	}
	
	/**
	 * 서버시간 조회 [ MYSQL 포맷 ]
	 * @param f
	 * @return
	 */
	public static String getServerTimeAddMin( String f, int distMin ){
		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.MINUTE, distMin );
		
		int Y = cal.get(Calendar.YEAR);
		int M = cal.get(Calendar.MONTH)+1;
		int D = cal.get(Calendar.DATE);
		int H = cal.get(Calendar.HOUR_OF_DAY);
		int h = cal.get(Calendar.HOUR);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		return f.replaceAll( "%Y", Y + "" ).replaceAll( "%m", ( M < 10 ? "0" + M : M + "" ) ).replaceAll( "%d", ( D < 10 ? "0" + D : D + "" ) )
				.replaceAll( "%H", ( H < 10 ? "0" + H : H + "" ) ).replaceAll( "%h", ( h < 10 ? "0" + h : h + "" ) )
				.replaceAll( "%i", ( m < 10 ? "0" + m : m + "" ) ).replaceAll( "%s", ( s < 10 ? "0" + s : s + "" ) );
	}
	
	public static double doubleTrim( double d, int underLen ) {
		String s = String.valueOf( d );
		if( s.indexOf(".") == -1 ) {
			return d;
		}
		int limit = s.length() < s.lastIndexOf(".") + underLen + 1 ? s.length() : s.lastIndexOf(".") + underLen + 1;
		s = s.substring( 0, limit );
		return Double.valueOf( s );
	}
	
	public static boolean isAcceptedUploadImageFileFormat( String format ) {
		for( String accept : ACCEPT_IMAGE_FORMATS ) {
			if( accept.equalsIgnoreCase( format ) ) {
				return true;
			}
		}
		return false;
	}
	
	public static long parseKrAmtToLongAmt( String krAmt ) {
		Pattern eogPattern = Pattern.compile( "[0-9]{1,}(억)" );
		Pattern cheonmanPattern1 = Pattern.compile( "[0-9]{1,}(천만)" );
		Pattern cheonmanPattern2 = Pattern.compile( "[0-9]{1,}(천)" );
		Pattern manPattern = Pattern.compile( "[0-9]{1,}(만)" );
		
		int eog = 0;
		int cheonman = 0;
		int man = 0;
		
		// 억 단위
		Matcher matcher = eogPattern.matcher( krAmt );
		if( matcher.find() ) {
			eog = Integer.valueOf( GsntalkUtil.parseNumberString( matcher.group() ) );
		}
		
		// 천만 단위
		matcher = cheonmanPattern1.matcher( krAmt );
		if( matcher.find() ) {
			cheonman = Integer.valueOf( GsntalkUtil.parseNumberString( matcher.group() ) );
		}
		if( cheonman == 0 ) {
			matcher = cheonmanPattern2.matcher( krAmt );
			if( matcher.find() ) {
				cheonman = Integer.valueOf( GsntalkUtil.parseNumberString( matcher.group() ) );
			}
		}
		
		// 만단위
		matcher = manPattern.matcher( krAmt );
		if( matcher.find() ) {
			man = Integer.valueOf( GsntalkUtil.parseNumberString( matcher.group() ) );
		}
		
		return ( eog * Long.valueOf( GsntalkUtil.EOG ) ) + ( cheonman * Long.valueOf( GsntalkUtil.CHEONMAN ) ) + ( man * Long.valueOf( GsntalkUtil.MAN ) );
	}
	
	public static int parseKrAmtToIntegerAmt( String krAmt ) {
		Pattern cheonmanPattern1 = Pattern.compile( "[0-9]{1,}(천만)" );
		Pattern cheonmanPattern2 = Pattern.compile( "[0-9]{1,}(천)" );
		Pattern manPattern = Pattern.compile( "[0-9]{1,}(만)" );
		
		int cheonman = 0;
		int man = 0;
		
		// 천만 단위
		Matcher matcher = cheonmanPattern1.matcher( krAmt );
		if( matcher.find() ) {
			cheonman = Integer.valueOf( GsntalkUtil.parseNumberString( matcher.group() ) );
		}
		if( cheonman == 0 ) {
			matcher = cheonmanPattern2.matcher( krAmt );
			if( matcher.find() ) {
				cheonman = Integer.valueOf( GsntalkUtil.parseNumberString( matcher.group() ) );
			}
		}
		
		// 만단위
		matcher = manPattern.matcher( krAmt );
		if( matcher.find() ) {
			man = Integer.valueOf( GsntalkUtil.parseNumberString( matcher.group() ) );
		}
		
		return ( cheonman * Integer.valueOf( GsntalkUtil.CHEONMAN ) ) + ( man * Integer.valueOf( GsntalkUtil.MAN ) );
	}
	
	public static String parseAmtToKr( long amount ) {
		amount = amount - ( amount % Integer.valueOf( GsntalkUtil.MAN ) );		// 1만 이하 단위 절삭
		
		int eog = 0;
		int cheonman = 0;
		int man = 0;
		
		eog = (int)( amount / Long.valueOf( GsntalkUtil.EOG ) );
		amount = amount - ( eog * Long.valueOf( GsntalkUtil.EOG ) );
		
		cheonman = (int)( amount / Long.valueOf( GsntalkUtil.CHEONMAN ) );
		amount = amount - ( cheonman * Long.valueOf( GsntalkUtil.CHEONMAN ) );
		
		man = (int)( amount / Long.valueOf( GsntalkUtil.MAN ) );
		amount = amount - ( man * Long.valueOf( GsntalkUtil.MAN ) );
		
		String result = "";
		if( eog > 0 ) {
			result += eog + "억 ";
		}
		if( cheonman > 0 ) {
			result += cheonman + "천 ";
		}
		if( man > 0 ) {
			result += man + "만";
		}
		
		return result;
	}
	
	public static String parseAmtToKr( int amount ) {
		amount = amount - ( amount % Integer.valueOf( GsntalkUtil.MAN ) );		// 1만 이하 단위 절삭
		
		int eog = 0;
		int cheonman = 0;
		int man = 0;
		
		eog = amount / Integer.valueOf( GsntalkUtil.EOG );
		amount = amount - ( eog * Integer.valueOf( GsntalkUtil.EOG ) );
		
		cheonman = amount / Integer.valueOf( GsntalkUtil.CHEONMAN );
		amount = amount - ( cheonman * Integer.valueOf( GsntalkUtil.CHEONMAN ) );
		
		man = amount / Integer.valueOf( GsntalkUtil.MAN );
		amount = amount - ( man * Integer.valueOf( GsntalkUtil.MAN ) );
		
		String result = "";
		if( eog > 0 ) {
			result += eog + "억 ";
		}
		if( cheonman > 0 ) {
			result += cheonman + "천 ";
		}
		if( man > 0 ) {
			result += man + "만";
		}
		
		return result;
	}
	
	public static double parsePyungToMeters( double pyung ) {
		BigDecimal em = new BigDecimal( GsntalkUtil.METERS_BY_PYUNG );
		return em.multiply( new BigDecimal( pyung ) ).setScale( 1, BigDecimal.ROUND_FLOOR ).doubleValue();
	}
	
	public static double parseMetersToPyung( double meters ) {
		BigDecimal em = new BigDecimal( GsntalkUtil.METERS_BY_PYUNG );
		return new BigDecimal( meters ).divide( em , 1, BigDecimal.ROUND_FLOOR ).doubleValue();
	}
	
	/**
	 * GoogleMaps Long Value 값을 주소매핑 적재용 소숫점 3차리 로 변경 -> 마지막 5단위 만 적용
	 * @param locVal
	 * @return
	 */
	public static double geolocationAddressMappingTrim( double locVal ) {
		long l = Long.valueOf( forceDoubleTrim( locVal * 1000.0D ) );
		return ( l - ( l % 5 ) ) / 1000.0D;
	}
	
	public static String forceDoubleTrim( double d ) {
		String s = String.valueOf( d );
		return s.substring( 0, s.lastIndexOf(".") );
	}
	
	public static String getClientIpAddress( HttpServletRequest request ) {
		String clientIp = request.getHeader( "X-Forwarded-For" );
		if( GsntalkUtil.isEmpty( clientIp ) ) {
			clientIp = request.getHeader( "Proxy-Client-IP" );
		}
		if( GsntalkUtil.isEmpty( clientIp ) ) {
			clientIp = request.getHeader( "WL-Proxy-Client-IP" );
		}
		if( GsntalkUtil.isEmpty( clientIp ) ) {
			clientIp = request.getHeader( "HTTP_CLIENT_IP" );
		}
		if( GsntalkUtil.isEmpty( clientIp ) ) {
			clientIp = request.getHeader( "HTTP_X_FORWARDED_FOR" );
		}
		if( GsntalkUtil.isEmpty( clientIp ) ) {
			clientIp = request.getRemoteAddr();
		}
		return GsntalkUtil.ifEmptyString( clientIp, "" );
	}
	
	public static String getClientUserAgent( HttpServletRequest request ) {
		return request.getHeader( "User-Agent" );
	}
	
	@SuppressWarnings( "unchecked" )
	public static String makeAutoLoginToken( String userAgent ) throws Exception {
		JSONObject item = new JSONObject();
		item.put( "userAgent", userAgent );
		item.put( "key", GsntalkEncryptor.encrypt( GsntalkUtil.getRandomString( -1, 4 ) + GsntalkUtil.getServerTime( "%Y%m%d%H%i%s" ) + GsntalkUtil.getRandomString( -1, 4 ) ) );
		return GsntalkEncryptor.encrypt( item.toJSONString() );
	}
	
	public static List<String> jsonStringArrayToList( JSONArray items ){
		if( GsntalkUtil.isEmptyArray( items ) ) {
			return new ArrayList<String>();
		}
		List<String> list = new ArrayList<String>();
		for( int i = 0; i < items.size(); i ++ ) {
			list.add( GsntalkUtil.getString( items.get( i ) ) );
		}
		return list;
	}
	
	/**
	 * 중개보수금액 정보 조회
	 * 
	 * @param estateTypCd
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public static JSONObject getEstateBrokerCommitionItem( String estateTypCd, String tranTypGbCd, long dealAmt ) {
		double maxChargeRatio = 0.0d;
		long maxAmt = 0L;
		
		// 주택 ( 아파트, 단독/다가구, 다세대/빌라/연립, 상가주택, 주택, 아파트분양권 )
		if( GsntalkUtil.isIn( estateTypCd, "APT", "SMH", "TWN", "SHC", "HUS", "APS" ) ) {
			
			// 매매 ( 매매, 전매 )
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "RESALE" ) ) {
				
				// 5천만원 미만 [ 0.6%, 25만원 ]
				if( dealAmt < 50000000 ) {
					maxChargeRatio = 0.6d;				// 상한요율
					maxAmt = 250000L;					// 상한금액
					
				// ~ 2억 미만 [ 0.5%, 80만원 ]
				}else if( dealAmt < 200000000 ) {
					maxChargeRatio = 0.5d;				// 상한요율
					maxAmt = 800000L;					// 상한금액
					
				// ~ 9억 미만 [ 0.4%, 없음 ]
				}else if( dealAmt < 900000000 ) {
					maxChargeRatio = 0.4d;				// 상한요율
					maxAmt = 0L;						// 상한금액
					
				// ~ 12억 미만 [ 0.5%, 없음 ]
				}else if( dealAmt < 1200000000 ) {
					maxChargeRatio = 0.5d;				// 상한요율
					maxAmt = 0L;						// 상한금액
					
				// ~ 15억 미만 [ 0.6%, 없음 ]
				}else if( dealAmt < 1500000000 ) {
					maxChargeRatio = 0.6d;				// 상한요율
					maxAmt = 0L;						// 상한금액
					
				// 15억 이상 [ 0.7%, 없음 ]
				}else {
					maxChargeRatio = 0.7d;				// 상한요율
					maxAmt = 0L;						// 상한금액
				}
				
			// 임대차 등 이외
			}else {
				
				// 5천만원 미만 [ 0.5%, 20만원 ]
				if( dealAmt < 50000000 ) {
					maxChargeRatio = 0.5d;				// 상한요율
					maxAmt = 200000L;					// 상한금액
					
				// ~ 1억 미만  [ 0.4%, 30만원 ]
				}else if( dealAmt < 100000000 ) {
					maxChargeRatio = 0.4d;				// 상한요율
					maxAmt = 300000L;					// 상한금액
					
				// ~ 6억 미만 [ 0.3%, 없음 ]
				}else if( dealAmt < 600000000 ) {
					maxChargeRatio = 0.3d;				// 상한요율
					maxAmt = 0L;						// 상한금액
					
				// ~ 12억 미만 [ 0.4%, 없음 ]
				}else if( dealAmt < 1200000000 ) {
					maxChargeRatio = 0.4d;				// 상한요율
					maxAmt = 0L;						// 상한금액
					
				// ~ 15억 미만 [ 0.5%, 없음 ]
				}else if( dealAmt < 1500000000 ) {
					maxChargeRatio = 0.5d;				// 상한요율
					maxAmt = 0L;						// 상한금액
					
				// 15억 이상 [ 0.6%, 없음 ]
				}else {
					maxChargeRatio = 0.6d;				// 상한요율
					maxAmt = 0L;						// 상한금액
				}
			}
			
		// 오피스텔 ( 오피스텔, 오피스텔분양권 )
		}else if( GsntalkUtil.isIn( estateTypCd, "OFT", "OPS" ) ) {
			
			// 매매 ( 매매, 전매 ) [ 0.5%, 없음 ]
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "RESALE" ) ) {
				maxChargeRatio = 0.5d;					// 상한요율
				maxAmt = 0L;							// 상한금액
				
			// 임대차 등 이외 [ 0.4%, 없음 ]
			}else {
				maxChargeRatio = 0.4d;					// 상한요율
				maxAmt = 0L;							// 상한금액
		 	}
			
		// 이외 [ 0.9%, 협의 ]
		}else {
			maxChargeRatio = 0.9d;						// 상한요율
			maxAmt = 0L;								// 상한금액
		}
		
		long cmmstnAmt = GsntalkMathUtil.multiply( dealAmt, GsntalkMathUtil.divide( maxChargeRatio, 100.0d, 3 ) );
		if( maxAmt != 0 && cmmstnAmt > maxAmt ) {
			cmmstnAmt = maxAmt;
		}
		
		JSONObject item = new JSONObject();
		item.put( "maxChargeRatio", maxChargeRatio == 0.9d ? maxChargeRatio + "% 이내 협의" : maxChargeRatio + "%" );
		item.put( "cmmstnAmt", GsntalkUtil.parseAmtToKr( cmmstnAmt ) );
		
		return item;
	}
	
	public static String getMaximumErrorMessage( Exception e, int maxByte ) {
		String errMsg = e.getLocalizedMessage();
		if( !isEmpty( errMsg ) && errMsg.length() > maxByte ) {
			errMsg = errMsg.substring( 0, maxByte );
		}
		return errMsg;
	}
	
	@SuppressWarnings( "unchecked" )
	public static JSONObject getRequestPagingItem( int listPerPage, int nowPage ) {
		int stRnum = ( nowPage * listPerPage ) - ( listPerPage - 1 );
		int edRnum = stRnum + ( listPerPage - 1 );
		
		JSONObject item = new JSONObject();
		item.put( "stRnum", stRnum );
		item.put( "edRnum", edRnum );
		
		return item;
	}
	
	@SuppressWarnings( "unchecked" )
	public static JSONObject getResponsePagingItem( int pageCnt, int listPerPage, int totalCount, int nowPage ) {
		int stPage = ( ( ( nowPage - 1 ) / pageCnt ) * pageCnt ) + 1;
		int edPage = stPage + ( pageCnt - 1 );
		int totalPages = ( ( totalCount - 1 ) / listPerPage + 1 );
		if( edPage > totalPages ) {
			edPage = totalPages;
		}
		
		JSONObject item = new JSONObject();
		item.put( "nowPage", nowPage );
		item.put( "stPage", stPage );
		item.put( "edPage", edPage );
		item.put( "totalPages", totalPages );
		item.put( "totalCount", totalCount );
		
		return item;
	}
	
	public static String addressTopLvlReplace( String addr ) {
		String firstLvl = addr.substring( 0, addr.indexOf( " " ) );
		String rtnAddr = addr;
		if( "서울시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "서울시", "서울특별시" );
		}else if( "서울".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "서울", "서울특별시" );
		}else if( "부산시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "부산시", "부산광역시" );
		}else if( "부산".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "부산", "부산광역시" );
		}else if( "대구시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "대구시", "대구광역시" );
		}else if( "대구".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "대구", "대구광역시" );
		}else if( "인천시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "인천시", "인천광역시" );
		}else if( "인천".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "인천", "인천광역시" );
		}else if( "광주시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "광주시", "광주광역시" );
		}else if( "광주".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "광주", "광주광역시" );
		}else if( "대전시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "대전시", "대전광역시" );
		}else if( "대전".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "대전", "대전광역시" );
		}else if( "울산시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "울산시", "울산광역시" );
		}else if( "울산".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "울산", "울산광역시" );
		}else if( "세종시".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "세종시", "세종특별자치시" );
		}else if( "세종".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "세종", "세종특별자치시" );
		}else if( "경기".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "경기", "경기도" );
		}else if( "충북".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "충북", "충청북도" );
		}else if( "충남".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "충남", "충청남도" );
		}else if( "전북".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "전북", "전라북도" );
		}else if( "전남".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "전남", "전라남도" );
		}else if( "경북".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "경북", "경상북도" );
		}else if( "경남".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "경남", "경상남도" );
		}else if( "제주도".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "제주도", "제주특별자치도" );
		}else if( "강원도".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "강원도", "강원특별자치도" );
		}else if( "강원".equals( firstLvl ) ) {
			rtnAddr = addr.replace( "강원", "강원특별자치도" );
		}
		
		return rtnAddr;
	}
	
	public static String getTopAddr( String addr, int trimLvl ) {
		if( GsntalkUtil.isEmpty( addr ) ) {
			return "";
		}
		
		int tried = 0;
		int idx = 0;
		for( int i = 0; i < addr.length(); i ++ ) {
			if( " ".equals( String.valueOf( addr.charAt( i ) ) ) ) {
				idx = i;
				tried ++;
			}
			if( tried >= trimLvl ) {
				break;
			}
		}
		
		return addr.substring( 0, idx );
	}
	
	public static String getSubAddr( String addr, int trimLvl ) {
		if( GsntalkUtil.isEmpty( addr ) ) {
			return "";
		}
		
		for( int i = 0; i < trimLvl; i ++ ) {
			addr = addr.substring( addr.indexOf( " " ) + 1 );
		}
		
		return addr;
	}
	
	/**
	 * 지난 시간 표시용 문구 치환
	 * -> 1시간 이내
	 * -> 1시간 전
	 * -> 2시간 전
	 * -> YY.MM.DD
	 * 
	 * @param targetDttm ( YYYYMMDDHH24MI )
	 * @return
	 */
	public static String parserPassedTimeString( String targetDttm ) {
		if( GsntalkUtil.isEmpty( targetDttm ) || !GsntalkUtil.is12DateTimeFormat( targetDttm ) ) {
			return "";
		}
		targetDttm = GsntalkUtil.parseNumberString( targetDttm );
		String nowDttm = GsntalkUtil.getServerTime( "%Y%m%d%H%i" );
		if( nowDttm.substring( 0, 8 ).equals( targetDttm.substring( 0, 8 ) ) ) {
			Calendar nowCal = Calendar.getInstance();
			Calendar targetCal = Calendar.getInstance();
			targetCal.set( Integer.valueOf( targetDttm.substring( 0, 4 ) ), Integer.valueOf( targetDttm.substring( 4, 6 ) ) -1, Integer.valueOf( targetDttm.substring( 6, 8 ) ),
					Integer.valueOf( targetDttm.substring( 8, 10 ) ), Integer.valueOf( targetDttm.substring( 10, 12 ) ) );
			
			long now = nowCal.getTimeInMillis();
			long target = targetCal.getTimeInMillis();
			
			long mGap = ( target - now ) / 60000;
			
			if( mGap < -59L || mGap > 59L ) {
				long hGap = mGap / 60L;
				return hGap < 0L ? (-hGap) + "시간 전" : hGap + "시간 후";
			}else if( mGap > -10L && mGap < 10L ) {
				return mGap < 0L ? "방금전" : "잠시후";
			}else {
				return "1시간 이내";
			}
			
			
		}else {
			return targetDttm.substring( 2, 4 ) + "." + targetDttm.substring( 4, 6 ) + "." + targetDttm.substring( 6, 8 );
		}
	}
	
	/**
	 * 랜덤알파벳 생성
	 * -1: 대소문자숫자혼합, 0:대소문자혼합, 1:대문자만, 2:소문자만, 3:숫자만
	 * @param t
	 * @return
	 */
	private static char getRandomChar( int t ){
		Random r = new Random();
		char _c = '0';
		switch( t ){
			case -1 :
				int c = r.nextInt( 3 );
				if( c == 0 ){
					_c = String.valueOf( r.nextInt( 10 ) ).charAt( 0 );
				}else if( c == 1 ){
					_c = (char)( r.nextInt( 26 ) + 65 );
				}else {
					_c = (char)( r.nextInt( 26 ) + 97 );
				}
				break;
			case 0 :
				if( r.nextInt( 2 ) == 0 ){
					_c = (char)( r.nextInt( 26 ) + 65 );
				}else{
					_c = (char)( r.nextInt( 26 ) + 97 );
				}
				break;
			case 1 :
				_c = (char)( r.nextInt( 26 ) + 65 );
				break;
			case 2 :
				_c = (char)( r.nextInt( 26 ) + 97 );
				break;
			case 3 :
				_c = String.valueOf( r.nextInt( 10 ) ).charAt( 0 );
				break;
		}
		return _c;
	}
	
	private static String lrPad( String base_str, int len, String asta, boolean isLeft ){
		if( base_str == null ){
			return null;
		}
		if( len < 1 ){
			return "";
		}
		if( base_str.length() > len ){
			return isLeft ? base_str.substring( base_str.length() - len, base_str.length() ) : base_str.substring( 0, len );
		}
		if( base_str.length() == len ){
			return base_str;
		}
		while( base_str.length() < len ){
			base_str = isLeft ? asta + base_str : base_str + asta;
		}
		return base_str;
	}
	
	/**
	 * PARSING COST-NUMBER STRING RETURN SET 1000 COMMA ( MINUS COST ALSO )
	 * @param str
	 * @return
	 */
	private static String set1000CommaAlgorithm( String str ){
		str = str.replace(",", "");
		boolean isMinus = false;
		if( Long.parseLong( str ) < 0L ){
			isMinus = true;
			str = str.replace("-", "");
		}
		String result = "";
		while( str.length() > 3 ){
			result = str.substring( str.length() -3, str.length()) + ( result.length() > 0 ? "," + result : result ) ;
			str = str.substring( 0, str.length() -3 );
		}
		if( str.length() > 0 ){
			result = str + ( result.length() > 0 ? "," + result : result ); 
		}
		return isMinus ? "-" + result : result;
	}
	
	private static boolean isCorrectRegExp( String value, String regex ) {
		return isEmpty( value.replaceAll( regex, "" ) );
	}
}