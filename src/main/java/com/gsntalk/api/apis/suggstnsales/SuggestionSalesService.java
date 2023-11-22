package com.gsntalk.api.apis.suggstnsales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsntalk.api.apis.gsntalk.GsntalkDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.CommonCodeVO;
import com.gsntalk.api.common.vo.SuggestionSalesVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Service( "SuggestionSalesService" )
public class SuggestionSalesService extends CommonService {

	@Autowired
	private SuggestionSalesDAO suggestionSalesDAO;
	
	@Autowired
	private GsntalkDAO gsntalkDAO;
	
	public SuggestionSalesService() {
		super( SuggestionSalesService.class );
	}
	
	/**
	 * Admin - 추천분양 신규등록 - 1단계 임시저장 ( 등록전용 )
	 * 
	 * @param memSeqno
	 * @param baseItem
	 * @param dongItems
	 * @param eduTmpFileNmItems
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject registerSuggstnSalesStep1RegItem( long memSeqno, JSONObject baseItem, JSONArray dongItems, JSONArray eduTmpFileNmItems )throws Exception {
		/** 기본정보 ( baseItem ) 검증 */
		String suggstnSalesRegionGbCd					= GsntalkUtil.getString( baseItem.get( "suggstnSalesRegionGbCd" ) );
		String addr										= GsntalkUtil.getString( baseItem.get( "addr" ) );
		String bldNm									= GsntalkUtil.getString( baseItem.get( "bldNm" ) );
		String suggstnSalesTtl							= GsntalkUtil.getString( baseItem.get( "suggstnSalesTtl" ) );
		String salesDtlDscr								= GsntalkUtil.getString( baseItem.get( "salesDtlDscr" ) );
		int minFlr										= GsntalkUtil.getInteger( baseItem.get( "minFlr" ) );
		int maxFlr										= GsntalkUtil.getInteger( baseItem.get( "maxFlr" ) );
		int totBldCnt									= GsntalkUtil.getInteger( baseItem.get( "totBldCnt" ) );
		int parkingCarCnt								= GsntalkUtil.getInteger( baseItem.get( "parkingCarCnt" ) );
		int husHoldCnt									= GsntalkUtil.getInteger( baseItem.get( "husHoldCnt" ) );
		double lndArea									= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "lndArea" ) ) );
		double bldArea									= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "bldArea" ) ) );
		double totFlrArea								= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "totFlrArea" ) ) );
		double flrAreaRatio								= GsntalkUtil.getDouble( baseItem.get( "flrAreaRatio" ) );
		double bldToLndRatio							= GsntalkUtil.getDouble( baseItem.get( "bldToLndRatio" ) );
		String cmpltnDate								= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "cmpltnDate" ) ) );
		String expctMovMonth							= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "expctMovMonth" ) ) );
		String devCompNm								= GsntalkUtil.getString( baseItem.get( "devCompNm" ) );
		String constCompNm								= GsntalkUtil.getString( baseItem.get( "constCompNm" ) );
		String matterPortLinkUrl						= GsntalkUtil.getString( baseItem.get( "matterPortLinkUrl" ) );
		String repTmpFileNm								= GsntalkUtil.getString( baseItem.get( "repTmpFileNm" ) );
		
		if( GsntalkUtil.isEmpty( suggstnSalesRegionGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesRegionGbCd" );
		}
		// 추천분양지역구분코드 목록조회
		List<CommonCodeVO> commonCodeList = gsntalkDAO.getComnCdList( "SUGGSTN_SALES_REGION_GB_CD" );
		boolean commonCdMatched = false;
		for( CommonCodeVO vo : commonCodeList ) {
			if( vo.getItemCd().equals( suggstnSalesRegionGbCd ) ) {
				commonCdMatched = true;
				break;
			}
		}
		if( !commonCdMatched ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> suggstnSalesRegionGbCdMatched 값이 잘못됨 ->  see CommonCode [SUGGSTN_SALES_REGION_GB_CD]" );
		}
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> addr" );
		}
		if( GsntalkUtil.isEmpty( bldNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> bldNm" );
		}
		if( GsntalkUtil.isEmpty( suggstnSalesTtl ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> suggstnSalesTtl" );
		}
		if( minFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> minFlr" );
		}
		if( maxFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> maxFlr" );
		}
		if( totBldCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> totBldCnt" );
		}
		if( parkingCarCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> parkingCarCnt" );
		}
		if( husHoldCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> husHoldCnt" );
		}
		if( lndArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> lndArea" );
		}
		if( bldArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> bldArea" );
		}
		if( totFlrArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> totFlrArea" );
		}
		if( flrAreaRatio == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> flrAreaRatio" );
		}
		if( bldToLndRatio == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> bldToLndRatio" );
		}
		if( GsntalkUtil.isEmpty( cmpltnDate ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> cmpltnDate" );
		}
		if( !GsntalkUtil.is8DateFormat( cmpltnDate, true ) ) {
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "baseItem -> cmpltnDate [ " + cmpltnDate + " ] is not date value" );
		}
		if( GsntalkUtil.isEmpty( expctMovMonth ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> expctMovMonth" );
		}
		if( !GsntalkUtil.is6MonthFormat( expctMovMonth, true ) ) {
			// 잘못된 월 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MONTH_FORMAT, "baseItem -> expctMovMonth [ " + expctMovMonth + " ] is not month value" );
		}
		if( GsntalkUtil.isEmpty( repTmpFileNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> repTmpFileNm" );
		}
		
		if( eduTmpFileNmItems.size() > 50 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "eduTmpFileNmItems size over 50" );
		}
		
		JSONObject geocodeItem = gsntalkIFUtil.getGeocode( addr, false );
		if( geocodeItem == null ) {
			// 위치정보를 확인할 수 없는 주소
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_FOUND_LOCATION_ADDRESS, "baseItem -> addr : 위치정보를 확인할 수 없는 주소" );
		}
		
		// encodeXSS
		baseItem.put( "addr", GsntalkXSSUtil.encodeXss( addr ) );
		baseItem.put( "bldNm", GsntalkXSSUtil.encodeXss( bldNm ) );
		baseItem.put( "suggstnSalesTtl", GsntalkXSSUtil.encodeXss( suggstnSalesTtl ) );
		baseItem.put( "salesDtlDscr", GsntalkXSSUtil.encodeXss( salesDtlDscr ) );
		baseItem.put( "devCompNm", GsntalkXSSUtil.encodeXss( devCompNm ) );
		baseItem.put( "constCompNm", GsntalkXSSUtil.encodeXss( constCompNm ) );
		baseItem.put( "matterPortLinkUrl", GsntalkXSSUtil.encodeXss( matterPortLinkUrl ) );
		
		double lat = GsntalkUtil.getDouble( geocodeItem.get( "lat" ) );
		double lng = GsntalkUtil.getDouble( geocodeItem.get( "lng" ) );
		String addrShortNm = GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) );
		baseItem.put( "addrShortNm", addrShortNm );
		baseItem.put( "lat", lat );
		baseItem.put( "lng", lng );
		
		// 층별용도구분코드 목록조회
		commonCodeList = gsntalkDAO.getComnCdList( "FLR_USAGE_GB_CD" );

		/** 동별 정보 ( 층별 정보, dongItems ) 검증 */
		JSONObject dongItem = null;
		String dongNm = "";
		JSONArray usageItems = null;
		JSONObject usageItem = null;
		List<String> flrUsageGbCdList = null;
		String flrUsageGbCd = null;
		JSONArray flrItems = null;
		JSONObject flrItem = null;
		String stFlr = null;
		String edFlr = null;
		
		for( int i = 0; i < dongItems.size(); i ++ ) {
			dongItem = (JSONObject)dongItems.get( i );
			
			dongNm = GsntalkUtil.getString( dongItem.get( "dongNm" ) );
			if( GsntalkUtil.isEmpty( dongNm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> dongNm is empty." );
			}
			
			// encodeXSS
			dongItem.put( "dongNm", GsntalkXSSUtil.encodeXss( dongNm ) );
			
			flrUsageGbCdList = new ArrayList<String>();
			usageItems = GsntalkUtil.getJSONArray( dongItem, "usageItems" );
			
			for( int j = 0; j < usageItems.size(); j ++ ) {
				usageItem = (JSONObject)usageItems.get( j );
				
				flrUsageGbCd = GsntalkUtil.getString( usageItem.get( "flrUsageGbCd" ) );
				if( flrUsageGbCdList.contains( flrUsageGbCd ) ) {
					// 중복된 데이터
					throw new GsntalkAPIException( GsntalkAPIResponse.DUPLICATED_DATA, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrUsageGbCd(" + flrUsageGbCd + ") is duplecated." );
				}
				
				commonCdMatched = false;
				for( CommonCodeVO vo : commonCodeList ) {
					if( vo.getItemCd().equals( flrUsageGbCd ) ) {
						commonCdMatched = true;
						break;
					}
				}
				if( !commonCdMatched ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrUsageGbCd(" + flrUsageGbCd + ") 값이 잘못됨 -> see CommonCode [FLR_USAGE_GB_CD]" );
				}
				
				flrUsageGbCdList.add( flrUsageGbCd );

				flrItems = GsntalkUtil.getJSONArray( usageItem, "flrItems" );
				
				for( int k = 0; k < flrItems.size(); k ++ ) {
					flrItem = (JSONObject)flrItems.get( k );
					
					stFlr = GsntalkUtil.getString( flrItem.get( "stFlr" ) );
					edFlr = GsntalkUtil.getString( flrItem.get( "edFlr" ) );
					
					if( GsntalkUtil.isEmpty( stFlr ) ) {
						// 필수 요청파라메터 누락
						throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrItems " + ( k + 1 ) + " 번째 stFlr is empty." );
					}
					if( GsntalkUtil.isEmpty( edFlr ) ) {
						// 필수 요청파라메터 누락
						throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrItems " + ( k + 1 ) + " 번째 edFlr is empty." );
					}
				}
			}
			
		}
		
		// 1 단계에서만 임시키 생성 ( 무조건 신규생성 및 등록 )
		String regTmpKey = GsntalkUtil.createRegistrationTempKey();
		
		JSONObject item = new JSONObject();
		item.put( "baseItem", baseItem );
		item.put( "dongItems", dongItems );
		item.put( "eduTmpFileNmItems", eduTmpFileNmItems );
		
		// 기존 1단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update, 다른 관리자가 등록한것도 조회되어야 하므로 memSeqno 0 으로 조회 )
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( 0L, GsntalkConstants.REG_CLAS_CD_ADM_SUGGST, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ADM_SUGGST, regTmpKey, 1, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ADM_SUGGST, regTmpKey, 1, item.toJSONString() );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * Admin - 추천분양 신규등록 - 2단계 최종 등록 ( 등록전용 )
	 * 
	 * @param memSeqno
	 * @param regTmpKey
	 * @param prmmItems
	 * @param salesSchdlItems
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void registerSuggstnSalesFinalStepRegItem( long memSeqno, String regTmpKey, JSONArray prmmItems, JSONArray salesSchdlItems )throws Exception {
		if( GsntalkUtil.isEmptyArray( prmmItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems" );
		}
		if( GsntalkUtil.isEmptyArray( salesSchdlItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems" );
		}
		
		// 추천분양 신규등록 1단계 임시저장 JSON 조회
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( 0L, GsntalkConstants.REG_CLAS_CD_ADM_SUGGST, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 후 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		JSONObject item = null;
		
		/** 프리미엄 정보 (prmmItems) 검증 */
		String prmmTtl = null;
		String prmmDscr = null;
		for( int i = 0; i < prmmItems.size(); i ++ ) {
			item = (JSONObject)prmmItems.get( i );
			
			prmmTtl = GsntalkUtil.getString( item.get( "prmmTtl" ) );
			prmmDscr = GsntalkUtil.getString( item.get( "prmmDscr" ) );
			
			if( GsntalkUtil.isEmpty( prmmTtl ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems " + ( i + 1 ) + "번채 -> prmmTtl is empty." );
			}
			if( GsntalkUtil.isEmpty( prmmDscr ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems " + ( i + 1 ) + "번채 -> prmmDscr is empty." );
			}
			
			// encode XSS
			item.put( "prmmTtl", GsntalkXSSUtil.encodeXss( prmmTtl ) );
			item.put( "prmmDscr", GsntalkXSSUtil.encodeXss( prmmDscr ) );
		}
		
		/** 분양 일정 정보 (salesSchdlItems) 검증 */
		String schdlNm = null;
		String schdlStDate = null;
		String schdlEdDate = null;
		for( int i = 0; i < salesSchdlItems.size(); i ++ ) {
			item = (JSONObject)salesSchdlItems.get( i );
			
			schdlNm = GsntalkUtil.getString( item.get( "schdlNm" ) );
			schdlStDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlStDate" ) ) );
			schdlEdDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlEdDate" ) ) );
			
			if( GsntalkUtil.isEmpty( schdlNm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlNm is empty." );
			}
			if( GsntalkUtil.isEmpty( schdlStDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlStDate is empty." );
			}
			if( !GsntalkUtil.is8DateFormat( schdlStDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlStDate [" + schdlStDate + "] is not date format." );
			}
			if( GsntalkUtil.isEmpty( schdlEdDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlEdDate is empty." );
			}
			if( !GsntalkUtil.is8DateFormat( schdlEdDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlEdDate [" + schdlEdDate + "] is not date format." );
			}
			if( Integer.valueOf( schdlEdDate ) < Integer.valueOf( schdlStDate ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_ST_ED_DATE_VALUES, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlStDate lager than schdlStDate" );
			}
			
			// encode XSS
			item.put( "prmmTtl", GsntalkXSSUtil.encodeXss( schdlNm ) );
		}
		
		JSONObject step1Item				= (JSONObject)this.jsonParser.parse( tmpJsonData );
		/** 1단계 임시저장 데이터 - 기본정보 ( baseItem ) 추출 */
		JSONObject baseItem					= GsntalkUtil.getJSONObject( step1Item, "baseItem" );
		String suggstnSalesRegionGbCd					= GsntalkUtil.getString( baseItem.get( "suggstnSalesRegionGbCd" ) );
		String addr										= GsntalkUtil.getString( baseItem.get( "addr" ) );													// already xssEncoded
		String addrShortNm								= GsntalkUtil.getString( baseItem.get( "addrShortNm" ) );
		String bldNm									= GsntalkUtil.getString( baseItem.get( "bldNm" ) );													// already xssEncoded
		double lat										= GsntalkUtil.getDouble( baseItem.get( "lat" ) );
		double lng										= GsntalkUtil.getDouble( baseItem.get( "lng" ) );
		String suggstnSalesTtl							= GsntalkUtil.getString( baseItem.get( "suggstnSalesTtl" ) );										// already xssEncoded
		String salesDtlDscr								= GsntalkUtil.getString( baseItem.get( "salesDtlDscr" ) );											// already xssEncoded
		int minFlr										= GsntalkUtil.getInteger( baseItem.get( "minFlr" ) );
		int maxFlr										= GsntalkUtil.getInteger( baseItem.get( "maxFlr" ) );
		int totBldCnt									= GsntalkUtil.getInteger( baseItem.get( "totBldCnt" ) );
		int parkingCarCnt								= GsntalkUtil.getInteger( baseItem.get( "parkingCarCnt" ) );
		int husHoldCnt									= GsntalkUtil.getInteger( baseItem.get( "husHoldCnt" ) );
		double lndArea									= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "lndArea" ) ) );
		double bldArea									= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "bldArea" ) ) );
		double totFlrArea								= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "totFlrArea" ) ) );
		double flrAreaRatio								= GsntalkUtil.getDouble( baseItem.get( "flrAreaRatio" ) );
		double bldToLndRatio							= GsntalkUtil.getDouble( baseItem.get( "bldToLndRatio" ) );
		String cmpltnDate								= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "cmpltnDate" ) ) );
		String expctMovMonth							= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "expctMovMonth" ) ) );
		String devCompNm								= GsntalkUtil.getString( baseItem.get( "devCompNm" ) );												// already xssEncoded
		String constCompNm								= GsntalkUtil.getString( baseItem.get( "constCompNm" ) );											// already xssEncoded
		String matterPortLinkUrl						= GsntalkUtil.getString( baseItem.get( "matterPortLinkUrl" ) );										// already xssEncoded
		String repImgSaveFileNm							= GsntalkUtil.getString( baseItem.get( "repTmpFileNm" ) );
		
		/** 1단계 임시저장 데이터 - 동별 정보 ( 층별 정보, dongItems ) 추출 */
		JSONArray dongItems				= GsntalkUtil.getJSONArray( step1Item, "dongItems" );
		
		/** 1단계 임시저장 데이터 - 교육자료 임시파일명 목록 추출 */
		JSONArray eduTmpFileNmItems		= GsntalkUtil.getJSONArray( step1Item, "eduTmpFileNmItems" );
		
		
		// 관리자 추천분양 매물등록
		long suggstnSalesSeqno = suggestionSalesDAO.registerAdminSuggstnSalesProperty( memSeqno, suggstnSalesRegionGbCd, addr, addrShortNm, bldNm, lat, lng, suggstnSalesTtl, salesDtlDscr, minFlr, maxFlr, totBldCnt,
					parkingCarCnt, husHoldCnt, lndArea, bldArea, totFlrArea, flrAreaRatio, bldToLndRatio, cmpltnDate, expctMovMonth, devCompNm, constCompNm, matterPortLinkUrl );
		
		
		// 대표이미지 임시파일 이동 및 대표이미지 정보 업데이트
		String repImgUrl = gsntalkS3Util.moveTmpFileToSuggstnRepFile( suggstnSalesSeqno, repImgSaveFileNm );
		suggestionSalesDAO.updateAdminSuggstnSalesRepImageInfo( suggstnSalesSeqno, repImgSaveFileNm, repImgUrl );
		
		// 교육자료 임시파일 이동 및 교육자료 첨부파일 정보 등록
		String eduTmpFileNm = null;
		String eduFileUrl = null;
		for( int i = 0; i < eduTmpFileNmItems.size(); i ++ ) {
			eduTmpFileNm = GsntalkUtil.getString( eduTmpFileNmItems.get( i ) );
			
			eduFileUrl = gsntalkS3Util.moveTmpFileToSuggstnEduFile( suggstnSalesSeqno, eduTmpFileNm );
			
			suggestionSalesDAO.registerAdminSuggstnSalesEducationDataAttch( suggstnSalesSeqno, eduTmpFileNm, eduFileUrl );
		}
		
		// 관리자추천분양 동정보 등록
		JSONObject dongItem = null;
		long dongSeqno = 0L;
		String dongNm = null;
		JSONArray usageItems = null;
		JSONObject usageItem = null;
		String flrUsageGbCd = null;
		long usageByDongSeqno = 0L;
		JSONArray flrItems = null;
		JSONObject flrItem = null;
		String stFlr = null;
		String edFlr = null;
		String flrPlanSaveFileNm = null;
		String flrPlanFileUrl = null;
		for( int i = 0; i < dongItems.size(); i ++ ) {
			dongItem = (JSONObject)dongItems.get( i );
			
			dongNm = GsntalkUtil.getString( dongItem.get( "dongNm" ) );
			
			// 관리자추천분양 동정보 등록
			dongSeqno = suggestionSalesDAO.registerAdminSuggstnSalesDongInfo( suggstnSalesSeqno, dongNm );
			
			// 관리자추천분양 동별 용도정보
			usageItems = GsntalkUtil.getJSONArray( dongItem, "usageItems" );
			
			for( int j = 0; j < usageItems.size(); j ++ ) {
				usageItem = (JSONObject)usageItems.get( j );
				
				flrUsageGbCd = GsntalkUtil.getString( usageItem.get( "flrUsageGbCd" ) );
				
				// 관리자추천분양 동별 용도정보 등록
				usageByDongSeqno = suggestionSalesDAO.registerAdminSuggstnSalesUsageByDongInfo( dongSeqno, suggstnSalesSeqno, flrUsageGbCd );
				
				// 관리자추천분양 용도별 층정보 목록
				flrItems = GsntalkUtil.getJSONArray( usageItem, "flrItems" );
				
				for( int k = 0; k < flrItems.size(); k ++ ) {
					flrItem = (JSONObject)flrItems.get( k );
					
					stFlr = GsntalkUtil.getString( flrItem.get( "stFlr" ) );
					edFlr = GsntalkUtil.getString( flrItem.get( "edFlr" ) );
					flrPlanSaveFileNm = GsntalkUtil.getString( flrItem.get( "tmpFileNm" ) );
					flrPlanFileUrl = null;
					
					// 관리자추천분양 층별 임시이미지 정보가 있으면 임시파일 이동
					if( !GsntalkUtil.isEmpty( flrPlanSaveFileNm ) ) {
						flrPlanFileUrl = gsntalkS3Util.moveTmpFileToSuggstnFloorPlanFile( dongSeqno, suggstnSalesSeqno, flrPlanSaveFileNm );
					}
					
					// 관리자추천분양 층별정보 등록
					suggestionSalesDAO.registerAdminSuggstnSalesFloorInfo( usageByDongSeqno, dongSeqno, suggstnSalesSeqno, GsntalkUtil.parseFloorNumber( stFlr ), GsntalkUtil.parseFloorNumber( edFlr ), flrPlanSaveFileNm, flrPlanFileUrl );
				}
			}
		}
		
		
		// 관리자추천분양 프리미엄정보 등록 ( 2차정보 )
		for( int i = 0; i < prmmItems.size(); i ++ ) {
			item = (JSONObject)prmmItems.get( i );
			
			prmmTtl = GsntalkUtil.getString( item.get( "prmmTtl" ) );										// already xssEncoded
			prmmDscr = GsntalkUtil.getString( item.get( "prmmDscr" ) );										// already xssEncoded
			
			// 관리자추천분양 프리미엄정보 등록
			suggestionSalesDAO.registerAdminSuggstnSalesPremiumInfo( suggstnSalesSeqno, prmmTtl, prmmDscr );
			
			// update encode XSS
			item.put( "prmmTtl", prmmTtl );
			item.put( "prmmDscr", prmmDscr);
		}
		
		
		// 관리자추천분양 일정정보 등록 ( 2차정보 )
		for( int i = 0; i < salesSchdlItems.size(); i ++ ) {
			item = (JSONObject)salesSchdlItems.get( i );
			
			schdlNm = GsntalkUtil.getString( item.get( "schdlNm" ) );										// already xssEncoded
			
			schdlStDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlStDate" ) ) );
			schdlEdDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlEdDate" ) ) );
			
			// 관리자추천분양 일정정보 등록
			suggestionSalesDAO.registerAdminSuggstnSalesScheduleInfo( suggstnSalesSeqno, schdlNm, schdlStDate, schdlEdDate );
			
			// update encode XSS
			item.put( "schdlNm", schdlNm );
		}
		
		item = new JSONObject();
		item.put( "prmmItems", prmmItems );
		item.put( "salesSchdlItems", salesSchdlItems );
		
		// 기존 2단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update, 다른 관리자가 등록한것도 조회되어야 하므로 memSeqno 0 으로 조회 )
		String step2JsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( 0L, GsntalkConstants.REG_CLAS_CD_ADM_SUGGST, regTmpKey, 2 );
		if( GsntalkUtil.isEmpty( step2JsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ADM_SUGGST, regTmpKey, 2, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ADM_SUGGST, regTmpKey, 2, item.toJSONString() );
		}
		
		// 등록단계별임시정보 추천분양시퀀스 등록
		suggestionSalesDAO.updateTempDataSuggstnSalesSeqno( regTmpKey, suggstnSalesSeqno );
	}
	
	/**
	 * Admin - 추천분양 매물 목록조회 ( 페이징 )
	 * 
	 * @param suggstnSalesRegionGbCd
	 * @param poStatGbCd
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getSuggstnSalesPrptListItems( String suggstnSalesRegionGbCd, String poStatGbCd, String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<SuggestionSalesVO> suggestionSalesList = suggestionSalesDAO.getSuggstnSalesPrptListItems( suggstnSalesRegionGbCd, poStatGbCd, srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( suggestionSalesList ) ) {
			suggestionSalesList = new ArrayList<SuggestionSalesVO>();
		}else {
			totList = suggestionSalesList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		for( SuggestionSalesVO vo : suggestionSalesList ) {
			item = new JSONObject();
			
			item.put( "no", vo.getRownum() );
			item.put( "suggstnSalesSeqno", vo.getSuggstnSalesSeqno() );
			item.put( "poStatGbCd", vo.getPoStatGbCd() );
			item.put( "bldNm", vo.getBldNm() );
			item.put( "addr", vo.getAddr() );
			item.put( "cmpltnDate", vo.getCmpltnDate() );
			item.put( "regDate", vo.getRegDate() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * Admin - 추천분양 분양상태 업데이트
	 * 
	 * @param suggstnSalesSeqno
	 * @param poStatGbCd
	 * @throws Exception
	 */
	public void updateSuggstnSalesStatItem( long suggstnSalesSeqno, String poStatGbCd )throws Exception {
		int c = suggestionSalesDAO.isExistsSuggstnSalesSeqno( suggstnSalesSeqno );
		if( c == 0 ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		suggestionSalesDAO.updateSuggstnSalesStatItem( suggstnSalesSeqno, poStatGbCd );
	}
	
	/**
	 * Admin - 추천분양 삭제
	 * 
	 * @param suggstnSalesSeqnotems
	 * @throws Exception
	 */
	public void deleteSuggstnSalesItem( JSONArray suggstnSalesSeqnotems )throws Exception {
		for( int i = 0; i < suggstnSalesSeqnotems.size(); i ++ ) {
			suggestionSalesDAO.deleteSuggstnSalesItem( GsntalkUtil.getLong( suggstnSalesSeqnotems.get( i ) ) );
		}
	}
	
	/**
	 * Admin - 추천분양 1단계 등록정보 조회 ( 수정용 )
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getSuggstnSalesStep1RegItem( long suggstnSalesSeqno )throws Exception {
		// 추천분양 매물 기본정보 조회
		SuggestionSalesVO suggesstionSalesVO = suggestionSalesDAO.getSuggstnSalesPropertyInfo( 0L, suggstnSalesSeqno );
		if( suggesstionSalesVO == null ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		JSONObject baseItem = new JSONObject();
		baseItem.put( "suggstnSalesRegionGbCd",		suggesstionSalesVO.getSuggstnSalesRegionGbCd() );
		baseItem.put( "addr",						suggesstionSalesVO.getAddr() );
		baseItem.put( "bldNm",						suggesstionSalesVO.getBldNm() );
		baseItem.put( "suggstnSalesTtl",			suggesstionSalesVO.getSuggstnSalesTtl() );
		baseItem.put( "salesDtlDscr",				suggesstionSalesVO.getSalesDtlDscr() );
		baseItem.put( "minFlr",						suggesstionSalesVO.getMinFlr() );
		baseItem.put( "maxFlr",						suggesstionSalesVO.getMaxFlr() );
		baseItem.put( "totBldCnt",					suggesstionSalesVO.getTotBldCnt() );
		baseItem.put( "parkingCarCnt",				suggesstionSalesVO.getParkingCarCnt() );
		baseItem.put( "husHoldCnt",					suggesstionSalesVO.getHusHoldCnt() );
		baseItem.put( "lndArea",					GsntalkUtil.parsePyungToMeters( suggesstionSalesVO.getLndArea() ) );
		baseItem.put( "bldArea",					GsntalkUtil.parsePyungToMeters( suggesstionSalesVO.getBldArea() ) );
		baseItem.put( "totFlrArea",					GsntalkUtil.parsePyungToMeters( suggesstionSalesVO.getTotFlrArea() ) );
		baseItem.put( "flrAreaRatio",				suggesstionSalesVO.getFlrAreaRatio() );
		baseItem.put( "bldToLndRatio",				suggesstionSalesVO.getBldToLndRatio() );
		baseItem.put( "cmpltnDate",					suggesstionSalesVO.getCmpltnDate() );
		baseItem.put( "expctMovMonth",				suggesstionSalesVO.getExpctMovMonth() );
		baseItem.put( "devCompNm",					suggesstionSalesVO.getDevCompNm() );
		baseItem.put( "constCompNm",				suggesstionSalesVO.getConstCompNm() );
		baseItem.put( "matterPortLinkUrl",			suggesstionSalesVO.getMatterPortLinkUrl() );
		baseItem.put( "repImgUrl",					suggesstionSalesVO.getRepImgUrl() );
		resMap.put( "baseItem", baseItem );
		
		List<SuggestionSalesVO> dongList = null;
		List<SuggestionSalesVO> usageList = null;
		List<SuggestionSalesVO> flrList = null;
		JSONArray dongItems = new JSONArray();
		JSONObject dongItem = null;
		JSONArray usageItems = null;
		JSONObject usageItem = null;
		JSONArray flrItems = null;
		JSONObject flrItem = null;
		
		// 추천분양 매물 동 목록조회
		dongList = suggestionSalesDAO.getSuggstnSalesDongList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( dongList ) ) {
			dongList = new ArrayList<SuggestionSalesVO>();
		}
		for( SuggestionSalesVO dongVO : dongList ) {
			dongItem = new JSONObject();
			
			dongItem.put( "dongSeqno",					dongVO.getDongSeqno() );
			dongItem.put( "dongNm",						dongVO.getDongNm() );
			
			// 추천분양 매물 동별 용도정보 목록조회
			usageList = suggestionSalesDAO.getSuggstnSalesUsageByDongList( suggstnSalesSeqno, dongVO.getDongSeqno() );
			if( GsntalkUtil.isEmptyList( usageList ) ) {
				usageList = new ArrayList<SuggestionSalesVO>();
			}
			usageItems = new JSONArray();
			for( SuggestionSalesVO usageVO : usageList ) {
				usageItem = new JSONObject();
				
				usageItem.put( "usageByDongSeqno",			usageVO.getUsageByDongSeqno() );
				usageItem.put( "flrUsageGbCd",				usageVO.getFlrUsageGbCd() );
				
				// 추천분양 매물 층별정보 목록조회
				flrList = suggestionSalesDAO.getSuggstnSalesFloorList( suggstnSalesSeqno, dongVO.getDongSeqno(), usageVO.getUsageByDongSeqno() );
				if( GsntalkUtil.isEmptyList( flrList ) ) {
					flrList = new ArrayList<SuggestionSalesVO>();
				}
				flrItems = new JSONArray();
				for( SuggestionSalesVO flrVO : flrList ) {
					flrItem = new JSONObject();
					
					flrItem.put( "suggstnSalesFlrSeqno",		flrVO.getSuggstnSalesFlrSeqno() );
					flrItem.put( "stFlr",						flrVO.getStFlr() < 0 ? "-" + ( -flrVO.getStFlr() ) : flrVO.getStFlr() );
					flrItem.put( "edFlr",						flrVO.getEdFlr() < 0 ? "-" + ( -flrVO.getEdFlr() ) : flrVO.getEdFlr() );
					flrItem.put( "flrPlanFileUrl",				flrVO.getFlrPlanFileUrl() );
					
					flrItems.add( flrItem );
				}
				
				usageItem.put( "flrItems", flrItems );
				usageItems.add( usageItem );
			}
			
			dongItem.put( "usageItems", usageItems );
			dongItems.add( dongItem );
		}
		resMap.put( "dongItems", dongItems );
		
		// 교육자료 첨부파일 URL 목록조회
		List<SuggestionSalesVO> eduDataList = suggestionSalesDAO.getEducationDataAttachmentList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( eduDataList ) ) {
			eduDataList = new ArrayList<SuggestionSalesVO>();
		}
		JSONArray eduFileUrlItems = new JSONArray();
		for( SuggestionSalesVO vo : eduDataList ) {
			eduFileUrlItems.add( vo.getFileUrl() );
		}
		resMap.put( "eduFileUrlItems", eduFileUrlItems );
		
		return resMap;
	}
	
	
	/**
	 * Admin - 추천분양 최종단계 등록정보 조회 ( 수정용 )
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getSuggstnSalesFinalStepRegItem( long suggstnSalesSeqno )throws Exception {
		int c = suggestionSalesDAO.isExistsSuggstnSalesSeqno( suggstnSalesSeqno );
		if( c == 0 ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 추천분양 프리미엄정보 목록조회
		List<SuggestionSalesVO> prmmList = suggestionSalesDAO.getSuggstnSalesPrmmList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( prmmList ) ) {
			prmmList = new ArrayList<SuggestionSalesVO>();
		}
		JSONArray prmmItems = new JSONArray();
		JSONObject prmmItem = null;
		for( SuggestionSalesVO vo : prmmList ) {
			prmmItem = new JSONObject();
			
			prmmItem.put( "prmmTtl",		vo.getPrmmTtl() );
			prmmItem.put( "prmmDscr",		vo.getPrmmDscr() );
			
			prmmItems.add( prmmItem );
		}
		resMap.put( "prmmItems", prmmItems );
		
		// 추천분양 일정 목록조회
		List<SuggestionSalesVO> salesSchdlList = suggestionSalesDAO.getSuggstnSalesSchdlList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( salesSchdlList ) ) {
			salesSchdlList = new ArrayList<SuggestionSalesVO>();
		}
		JSONArray salesSchdlItems = new JSONArray();
		JSONObject salesSchdlItem = null;
		for( SuggestionSalesVO vo : salesSchdlList ) {
			salesSchdlItem = new JSONObject();
			
			salesSchdlItem.put( "schdlNm",			vo.getSchdlNm() );
			salesSchdlItem.put( "schdlStDate",		vo.getSchdlStDate() );
			salesSchdlItem.put( "schdlEdDate",		vo.getSchdlEdDate() );
			
			salesSchdlItems.add( salesSchdlItem );
		}
		resMap.put( "salesSchdlItems", salesSchdlItems );
		
		return resMap;
	}
	
	/**
	 * Admin - 추천분양 1단계 수정
	 * 
	 * @param suggstnSalesSeqno
	 * @param baseItem
	 * @param delDongSeqnoItems
	 * @param dongItems
	 * @param eduTmpFileNmItems
	 * @param delEduFileUrls
	 * @return
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void updateSuggstnSalesStep1Item( long suggstnSalesSeqno, JSONObject baseItem, JSONArray delDongSeqnoItems, JSONArray dongItems, JSONArray eduTmpFileNmItems, JSONArray delEduFileUrls )throws Exception {
		int c = suggestionSalesDAO.isExistsSuggstnSalesSeqno( suggstnSalesSeqno );
		if( c == 0 ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		/** 기본정보 ( baseItem ) 검증 */
		String suggstnSalesRegionGbCd					= GsntalkUtil.getString( baseItem.get( "suggstnSalesRegionGbCd" ) );
		String addr										= GsntalkUtil.getString( baseItem.get( "addr" ) );
		String bldNm									= GsntalkUtil.getString( baseItem.get( "bldNm" ) );
		String suggstnSalesTtl							= GsntalkUtil.getString( baseItem.get( "suggstnSalesTtl" ) );
		String salesDtlDscr								= GsntalkUtil.getString( baseItem.get( "salesDtlDscr" ) );
		int minFlr										= GsntalkUtil.getInteger( baseItem.get( "minFlr" ) );
		int maxFlr										= GsntalkUtil.getInteger( baseItem.get( "maxFlr" ) );
		int totBldCnt									= GsntalkUtil.getInteger( baseItem.get( "totBldCnt" ) );
		int parkingCarCnt								= GsntalkUtil.getInteger( baseItem.get( "parkingCarCnt" ) );
		int husHoldCnt									= GsntalkUtil.getInteger( baseItem.get( "husHoldCnt" ) );
		double lndArea									= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "lndArea" ) ) );
		double bldArea									= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "bldArea" ) ) );
		double totFlrArea								= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "totFlrArea" ) ) );
		double flrAreaRatio								= GsntalkUtil.getDouble( baseItem.get( "flrAreaRatio" ) );
		double bldToLndRatio							= GsntalkUtil.getDouble( baseItem.get( "bldToLndRatio" ) );
		String cmpltnDate								= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "cmpltnDate" ) ) );
		String expctMovMonth							= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "expctMovMonth" ) ) );
		String devCompNm								= GsntalkUtil.getString( baseItem.get( "devCompNm" ) );
		String constCompNm								= GsntalkUtil.getString( baseItem.get( "constCompNm" ) );
		String matterPortLinkUrl						= GsntalkUtil.getString( baseItem.get( "matterPortLinkUrl" ) );
		String repTmpFileNm								= GsntalkUtil.getString( baseItem.get( "repTmpFileNm" ) );
		
		if( GsntalkUtil.isEmpty( suggstnSalesRegionGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "suggstnSalesRegionGbCd" );
		}
		// 추천분양지역구분코드 목록조회
		List<CommonCodeVO> commonCodeList = gsntalkDAO.getComnCdList( "SUGGSTN_SALES_REGION_GB_CD" );
		boolean commonCdMatched = false;
		for( CommonCodeVO vo : commonCodeList ) {
			if( vo.getItemCd().equals( suggstnSalesRegionGbCd ) ) {
				commonCdMatched = true;
				break;
			}
		}
		if( !commonCdMatched ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> suggstnSalesRegionGbCdMatched 값이 잘못됨 ->  see CommonCode [SUGGSTN_SALES_REGION_GB_CD]" );
		}
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> addr" );
		}
		if( GsntalkUtil.isEmpty( bldNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> bldNm" );
		}
		if( GsntalkUtil.isEmpty( suggstnSalesTtl ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> suggstnSalesTtl" );
		}
		if( minFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> minFlr" );
		}
		if( maxFlr == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> maxFlr" );
		}
		if( totBldCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> totBldCnt" );
		}
		if( parkingCarCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> parkingCarCnt" );
		}
		if( husHoldCnt == 0 ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> husHoldCnt" );
		}
		if( lndArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> lndArea" );
		}
		if( bldArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> bldArea" );
		}
		if( totFlrArea == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> totFlrArea" );
		}
		if( flrAreaRatio == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> flrAreaRatio" );
		}
		if( bldToLndRatio == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> bldToLndRatio" );
		}
		if( GsntalkUtil.isEmpty( cmpltnDate ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> cmpltnDate" );
		}
		if( !GsntalkUtil.is8DateFormat( cmpltnDate, true ) ) {
			// 잘못된 날짜형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "baseItem -> cmpltnDate [ " + cmpltnDate + " ] is not date value" );
		}
		if( GsntalkUtil.isEmpty( expctMovMonth ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> expctMovMonth" );
		}
		if( !GsntalkUtil.is6MonthFormat( expctMovMonth, true ) ) {
			// 잘못된 월 형식
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_MONTH_FORMAT, "baseItem -> expctMovMonth [ " + expctMovMonth + " ] is not month value" );
		}
		
		// 기존 교육자료 첨부파일 URL 목록조회
		List<SuggestionSalesVO> eduDataList = suggestionSalesDAO.getEducationDataAttachmentList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( eduDataList ) ) {
			eduDataList = new ArrayList<SuggestionSalesVO>();
		}
		if( eduDataList.size() - delEduFileUrls.size() + eduTmpFileNmItems.size() > 50 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "eduTmpFileNmItems size over 50 ( 기존:" + eduDataList.size() + ", 삭제:" + delEduFileUrls.size() + ", 신규:" + eduTmpFileNmItems.size() );
		}
		
		
		JSONObject geocodeItem = gsntalkIFUtil.getGeocode( addr, false );
		if( geocodeItem == null ) {
			// 위치정보를 확인할 수 없는 주소
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_FOUND_LOCATION_ADDRESS, "baseItem -> addr : 위치정보를 확인할 수 없는 주소" );
		}
		
		double lat = GsntalkUtil.getDouble( geocodeItem.get( "lat" ) );
		double lng = GsntalkUtil.getDouble( geocodeItem.get( "lng" ) );
		String addrShortNm = GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) );
		
		// 층별용도구분코드 목록조회
		commonCodeList = gsntalkDAO.getComnCdList( "FLR_USAGE_GB_CD" );

		/** 동별 정보 ( 층별 정보, dongItems ) 검증 */
		JSONObject dongItem = null;
		String dongNm = "";
		JSONArray usageItems = null;
		JSONObject usageItem = null;
		List<String> flrUsageGbCdList = null;
		String flrUsageGbCd = null;
		JSONArray flrItems = null;
		JSONObject flrItem = null;
		String flrPlanSaveFileDelYn = null;
		String stFlr = null;
		String edFlr = null;
		
		for( int i = 0; i < dongItems.size(); i ++ ) {
			dongItem = (JSONObject)dongItems.get( i );
			
			dongNm = GsntalkUtil.getString( dongItem.get( "dongNm" ) );
			if( GsntalkUtil.isEmpty( dongNm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> dongNm is empty." );
			}
			
			flrUsageGbCdList = new ArrayList<String>();
			usageItems = GsntalkUtil.getJSONArray( dongItem, "usageItems" );
			
			for( int j = 0; j < usageItems.size(); j ++ ) {
				usageItem = (JSONObject)usageItems.get( j );
				
				flrUsageGbCd = GsntalkUtil.getString( usageItem.get( "flrUsageGbCd" ) );
				if( flrUsageGbCdList.contains( flrUsageGbCd ) ) {
					// 중복된 데이터
					throw new GsntalkAPIException( GsntalkAPIResponse.DUPLICATED_DATA, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrUsageGbCd(" + flrUsageGbCd + ") is duplecated." );
				}
				
				commonCdMatched = false;
				for( CommonCodeVO vo : commonCodeList ) {
					if( vo.getItemCd().equals( flrUsageGbCd ) ) {
						commonCdMatched = true;
						break;
					}
				}
				if( !commonCdMatched ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrUsageGbCd(" + flrUsageGbCd + ") 값이 잘못됨 -> see CommonCode [FLR_USAGE_GB_CD]" );
				}
				
				flrUsageGbCdList.add( flrUsageGbCd );

				flrItems = GsntalkUtil.getJSONArray( usageItem, "flrItems" );
				
				for( int k = 0; k < flrItems.size(); k ++ ) {
					flrItem = (JSONObject)flrItems.get( k );
					
					flrPlanSaveFileDelYn = GsntalkUtil.getString( flrItem.get( "tmpFileDelYn" ) );
					stFlr = GsntalkUtil.getString( flrItem.get( "stFlr" ) );
					edFlr = GsntalkUtil.getString( flrItem.get( "edFlr" ) );
					
					if( !GsntalkUtil.isEmpty( flrPlanSaveFileDelYn ) && !GsntalkUtil.isIn( flrPlanSaveFileDelYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
						// 잘못된 파라메터 값
						throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrItems " + ( k + 1 ) + " 번째 tmpFileDelYn is not in Y/N" );
					}
					if( GsntalkUtil.isEmpty( stFlr ) ) {
						// 필수 요청파라메터 누락
						throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrItems " + ( k + 1 ) + " 번째 stFlr is empty." );
					}
					if( GsntalkUtil.isEmpty( edFlr ) ) {
						// 필수 요청파라메터 누락
						throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dongItems " + ( i + 1 ) + "번째 -> usageItems " + ( j + 1 ) + " 번째 -> flrItems " + ( k + 1 ) + " 번째 edFlr is empty." );
					}
				}
			}
		}
		
		// 대표이미지 임시파일명이 있으면 파일 이동
		String repImgUrl = "";
		if( !GsntalkUtil.isEmpty( repTmpFileNm ) ) {
			repImgUrl = gsntalkS3Util.moveTmpFileToSuggstnRepFile( suggstnSalesSeqno, repTmpFileNm );
		}
		
		// 관리자 추천분양 매물정보 수정
		suggestionSalesDAO.updateAdminSuggstnSalesProperty( suggstnSalesSeqno, suggstnSalesRegionGbCd, addr, addrShortNm, bldNm, lat, lng, suggstnSalesTtl, salesDtlDscr, minFlr, maxFlr, totBldCnt, parkingCarCnt, husHoldCnt,
				lndArea, bldArea, totFlrArea, flrAreaRatio, bldToLndRatio, cmpltnDate, expctMovMonth, devCompNm, constCompNm, matterPortLinkUrl, repTmpFileNm, repImgUrl );
		
		
		// 교육자료 첨부파일 삭제
		for( int i = 0; i< delEduFileUrls.size(); i ++ ) {
			suggestionSalesDAO.deleteEducationDataAttachment( suggstnSalesSeqno, GsntalkUtil.getString( delEduFileUrls.get( i ) ) );
		}
		
		// 교육자료 임시파일 이동 및 교육자료 첨부파일 정보 등록
		String eduTmpFileNm = null;
		String eduFileUrl = null;
		for( int i = 0; i < eduTmpFileNmItems.size(); i ++ ) {
			eduTmpFileNm = GsntalkUtil.getString( eduTmpFileNmItems.get( i ) );
			
			eduFileUrl = gsntalkS3Util.moveTmpFileToSuggstnEduFile( suggstnSalesSeqno, eduTmpFileNm );
			suggestionSalesDAO.registerAdminSuggstnSalesEducationDataAttch( suggstnSalesSeqno, eduTmpFileNm, eduFileUrl );
		}
		
		JSONArray delUsageSeqnoItems = null;
		JSONArray delFlrSeqnoItems = null;
		long dongSeqno = 0L;
		long usageByDongSeqno = 0L;
		long suggstnSalesFlrSeqno = 0L;
		String flrPlanSaveFileNm = null;
		String flrPlanFileUrl = null;
		
		// 삭제할 동 시퀀스목록
		for( int i = 0; i < delDongSeqnoItems.size(); i ++ ) {
			dongSeqno = GsntalkUtil.getLong( delDongSeqnoItems.get( i ) );
			
			// 관리자추천분양 동정보 삭제
			suggestionSalesDAO.deleteAdminSuggstnSalesDongInfo( dongSeqno, suggstnSalesSeqno );
			
			// 관리자추천분양 동 하위 동별 용도 일괄 삭제
			suggestionSalesDAO.deleteAdminSuggstnSalesDongUnderUages( dongSeqno, suggstnSalesSeqno );
			
			// 관리자추천분양 동 하위 층별정보 일괄 삭제
			suggestionSalesDAO.deleteAdminSuggstnSalesDongUnderFloors( dongSeqno, suggstnSalesSeqno );
		}
		
		// 관리자추천분양 동정보 업데이트
		for( int i = 0; i < dongItems.size(); i ++ ) {
			dongItem = (JSONObject)dongItems.get( i );
			
			dongSeqno = GsntalkUtil.getLong( dongItem.get( "dongSeqno" ) );
			dongNm = GsntalkUtil.getString( dongItem.get( "dongNm" ) );
			
			// 관리자추천분양 동정보 등록 또는 업데이트
			if( dongSeqno == 0L ) {
				dongSeqno = suggestionSalesDAO.registerAdminSuggstnSalesDongInfo( suggstnSalesSeqno, dongNm );
			}else {
				suggestionSalesDAO.updateAdminSuggstnSalesDongInfo( dongSeqno, suggstnSalesSeqno, dongNm );
			}
			
			// 삭제할 동별 용도 시퀀스목록
			delUsageSeqnoItems = GsntalkUtil.getJSONArray( dongItem, "delUsageSeqnoItems" );
			for( int j = 0; j < delUsageSeqnoItems.size(); j ++ ) {
				usageByDongSeqno = GsntalkUtil.getLong( delUsageSeqnoItems.get( j ) );
				
				// 관리자추천분양 동별 용도정보 삭제
				suggestionSalesDAO.deleteAdminSuggstnSalesUsageByDongInfo( usageByDongSeqno, suggstnSalesSeqno );
				
				// 관리자추천분양 동별 용도 하위 층별정보 일괄 삭제
				suggestionSalesDAO.deleteAdminSuggstnSalesUsageByDongUnderFloors( usageByDongSeqno, suggstnSalesSeqno );
			}
			
			// 관리자추천분양 동별 용도정보
			usageItems = GsntalkUtil.getJSONArray( dongItem, "usageItems" );
			
			for( int j = 0; j < usageItems.size(); j ++ ) {
				usageItem = (JSONObject)usageItems.get( j );
				
				usageByDongSeqno = GsntalkUtil.getLong( usageItem.get( "usageByDongSeqno" ) );
				flrUsageGbCd = GsntalkUtil.getString( usageItem.get( "flrUsageGbCd" ) );
				
				// 관리자추천분양 동별 용도정보 등록 또는 업데이트
				if( usageByDongSeqno == 0L ) {
					usageByDongSeqno = suggestionSalesDAO.registerAdminSuggstnSalesUsageByDongInfo( dongSeqno, suggstnSalesSeqno, flrUsageGbCd );
				}else {
					suggestionSalesDAO.updateAdminSuggstnSalesUsageByDongInfo( usageByDongSeqno, suggstnSalesSeqno, flrUsageGbCd );
				}
				
				// 삭제할 용도별 층정보 시퀀스목록
				delFlrSeqnoItems = GsntalkUtil.getJSONArray( usageItem, "delFlrSeqnoItems" );
				for( int k = 0; k < delFlrSeqnoItems.size(); k ++ ) {
					// 관리자추천분양 층별정보 삭제
					suggestionSalesDAO.deleteAdminSuggstnSalesFloorInfo( GsntalkUtil.getLong( delFlrSeqnoItems.get( k ) ), dongSeqno, suggstnSalesSeqno );
				}
				
				// 관리자추천분양 용도별 층정보 목록
				flrItems = GsntalkUtil.getJSONArray( usageItem, "flrItems" );
				
				for( int k = 0; k < flrItems.size(); k ++ ) {
					flrItem = (JSONObject)flrItems.get( k );
					
					suggstnSalesFlrSeqno = GsntalkUtil.getLong( flrItem.get( "suggstnSalesFlrSeqno" ) );
					flrPlanSaveFileDelYn = GsntalkUtil.getString( flrItem.get( "tmpFileDelYn" ) );
					stFlr = GsntalkUtil.getString( flrItem.get( "stFlr" ) );
					edFlr = GsntalkUtil.getString( flrItem.get( "edFlr" ) );
					flrPlanSaveFileNm = GsntalkUtil.getString( flrItem.get( "tmpFileNm" ) );
					flrPlanFileUrl = null;
					
					if( GsntalkUtil.isEmpty( flrPlanSaveFileDelYn ) ) {
						flrPlanSaveFileDelYn = GsntalkConstants.NO;
					}
					
					// 관리자추천분양 층별 임시이미지 정보가 있으면 임시파일 이동
					if( !GsntalkUtil.isEmpty( flrPlanSaveFileNm ) ) {
						flrPlanFileUrl = gsntalkS3Util.moveTmpFileToSuggstnFloorPlanFile( dongSeqno, suggstnSalesSeqno, flrPlanSaveFileNm );
					}
					
					// 관리자추천분양 층별정보 등록 또는 업데이트
					if( suggstnSalesFlrSeqno == 0L ) {
						suggestionSalesDAO.registerAdminSuggstnSalesFloorInfo( usageByDongSeqno, dongSeqno, suggstnSalesSeqno, GsntalkUtil.parseFloorNumber( stFlr ), GsntalkUtil.parseFloorNumber( edFlr ), flrPlanSaveFileNm, flrPlanFileUrl );
					}else {
						suggestionSalesDAO.updateAdminSuggstnSalesFloorInfo( suggstnSalesFlrSeqno, dongSeqno, suggstnSalesSeqno, GsntalkUtil.parseFloorNumber( stFlr ), GsntalkUtil.parseFloorNumber( edFlr ), flrPlanSaveFileDelYn, flrPlanSaveFileNm, flrPlanFileUrl );
					}
				}
			}
		}
	}
	
	/**
	 * Admin - 추천분양 최종단계 수정 ( ※ 모두 재등록 )
	 * 
	 * @param suggstnSalesSeqno
	 * @param prmmItems
	 * @param salesSchdlItems
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void updateSuggstnSalesFinalStepItem( long suggstnSalesSeqno, JSONArray prmmItems, JSONArray salesSchdlItems )throws Exception {
		int c = suggestionSalesDAO.isExistsSuggstnSalesSeqno( suggstnSalesSeqno );
		if( c == 0 ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		if( GsntalkUtil.isEmptyArray( prmmItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems" );
		}
		if( GsntalkUtil.isEmptyArray( salesSchdlItems ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems" );
		}
		
		JSONObject item = null;
		
		/** 프리미엄 정보 (prmmItems) 검증 */
		String prmmTtl = null;
		String prmmDscr = null;
		for( int i = 0; i < prmmItems.size(); i ++ ) {
			item = (JSONObject)prmmItems.get( i );
			
			prmmTtl = GsntalkUtil.getString( item.get( "prmmTtl" ) );
			prmmDscr = GsntalkUtil.getString( item.get( "prmmDscr" ) );
			
			if( GsntalkUtil.isEmpty( prmmTtl ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems " + ( i + 1 ) + "번채 -> prmmTtl is empty." );
			}
			if( GsntalkUtil.isEmpty( prmmDscr ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prmmItems " + ( i + 1 ) + "번채 -> prmmDscr is empty." );
			}
		}
		
		/** 분양 일정 정보 (salesSchdlItems) 검증 */
		String schdlNm = null;
		String schdlStDate = null;
		String schdlEdDate = null;
		for( int i = 0; i < salesSchdlItems.size(); i ++ ) {
			item = (JSONObject)salesSchdlItems.get( i );
			
			schdlNm = GsntalkUtil.getString( item.get( "schdlNm" ) );
			schdlStDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlStDate" ) ) );
			schdlEdDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlEdDate" ) ) );
			
			if( GsntalkUtil.isEmpty( schdlNm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlNm is empty." );
			}
			if( GsntalkUtil.isEmpty( schdlStDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlStDate is empty." );
			}
			if( !GsntalkUtil.is8DateFormat( schdlStDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlStDate [" + schdlStDate + "] is not date format." );
			}
			if( GsntalkUtil.isEmpty( schdlEdDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlEdDate is empty." );
			}
			if( !GsntalkUtil.is8DateFormat( schdlEdDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlEdDate [" + schdlEdDate + "] is not date format." );
			}
			if( Integer.valueOf( schdlEdDate ) < Integer.valueOf( schdlStDate ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_ST_ED_DATE_VALUES, "salesSchdlItems " + ( i + 1 ) + "번채 -> schdlStDate lager than schdlStDate" );
			}
		}
		
		// 관리자추천분양 프리미엄정보 일괄 삭제 후 재등록
		suggestionSalesDAO.deleteAllAdminSuggstnSalesPremiumInfo( suggstnSalesSeqno );
		for( int i = 0; i < prmmItems.size(); i ++ ) {
			item = (JSONObject)prmmItems.get( i );
			
			prmmTtl = GsntalkXSSUtil.encodeXss( GsntalkUtil.getString( item.get( "prmmTtl" ) ) );
			prmmDscr = GsntalkXSSUtil.encodeXss( GsntalkUtil.getString( item.get( "prmmDscr" ) ) );
			
			// 관리자추천분양 프리미엄정보 등록
			suggestionSalesDAO.registerAdminSuggstnSalesPremiumInfo( suggstnSalesSeqno, prmmTtl, prmmDscr );
		}
		
		
		// 관리자추천분양 일정정보 일괄 삭제 후 재등록
		suggestionSalesDAO.deleteAllAdminSuggstnSalesScheduleInfo( suggstnSalesSeqno );
		for( int i = 0; i < salesSchdlItems.size(); i ++ ) {
			item = (JSONObject)salesSchdlItems.get( i );
			
			schdlNm = GsntalkXSSUtil.encodeXss( GsntalkUtil.getString( item.get( "schdlNm" ) ) );
			
			schdlStDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlStDate" ) ) );
			schdlEdDate = GsntalkUtil.parseNumberString( GsntalkUtil.getString( item.get( "schdlEdDate" ) ) );
			
			// 관리자추천분양 일정정보 등록
			suggestionSalesDAO.registerAdminSuggstnSalesScheduleInfo( suggstnSalesSeqno, schdlNm, schdlStDate, schdlEdDate );
		}
	}
	
	/**
	 * FRT - 실시간 분양현장 목록조회 ( 페이징 )
	 * 
	 * @param memSeqno
	 * @param poStatGbCd
	 * @param suggstnSalesRegionGbCd
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getRealtimeSalesItems( long memSeqno, String poStatGbCd, String suggstnSalesRegionGbCd, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		this.LOGGER.info( ">>> getRealtimeSalesItems <<<" );
		
		List<SuggestionSalesVO> suggestionSalesList = suggestionSalesDAO.getRealtimeSalesItems( memSeqno, poStatGbCd, suggstnSalesRegionGbCd, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( suggestionSalesList ) ) {
			suggestionSalesList = new ArrayList<SuggestionSalesVO>();
		}else {
			totList = suggestionSalesList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		for( SuggestionSalesVO vo : suggestionSalesList ) {
			item = new JSONObject();
			
			item.put( "suggstnSalesSeqno", vo.getSuggstnSalesSeqno() );
			item.put( "poStatGbCd", vo.getPoStatGbCd() );
			item.put( "repImgUrl", vo.getRepImgUrl() );
			item.put( "bldNm", vo.getBldNm() );
			item.put( "addrShortNm", vo.getAddr().substring( 0, vo.getAddr().indexOf( " " ) ) + " "  + vo.getAddrShortNm() );
			item.put( "favYn", vo.getFavYn() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * FRT - 관심 추천분양 등록
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void registerFavSuggstnSales( long memSeqno, long suggstnSalesSeqno )throws Exception {
		int c = suggestionSalesDAO.isExistsSuggstnSalesSeqno( suggstnSalesSeqno );
		if( c == 0 ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		c = suggestionSalesDAO.isExistsFavSuggstnSales( memSeqno, suggstnSalesSeqno );
		if( c == 0 ) {
			suggestionSalesDAO.registerFavSuggstnSales( memSeqno, suggstnSalesSeqno );
		}
	}
	
	/**
	 * FRT - 관심 추천분양 해제
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void releaseFavSuggstnSales( long memSeqno, long suggstnSalesSeqno )throws Exception {
		int c = suggestionSalesDAO.isExistsSuggstnSalesSeqno( suggstnSalesSeqno );
		if( c > 0 ) {
			suggestionSalesDAO.releaseFavSuggstnSales( memSeqno, suggstnSalesSeqno );
		}
	}
	
	/**
	 * FRT - 실시간 분양현장 매물 상세정보 조회
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getRealtimeSalesDtlItem( long memSeqno, long suggstnSalesSeqno )throws Exception {
		// 추천분양 매물 기본정보 조회
		SuggestionSalesVO suggesstionSalesVO = suggestionSalesDAO.getSuggstnSalesPropertyInfo( memSeqno, suggstnSalesSeqno );
		if( suggesstionSalesVO == null ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 기본정보 ( baseItem )
		JSONObject baseItem = new JSONObject();
		baseItem.put( "suggstnSalesTtl",			suggesstionSalesVO.getSuggstnSalesTtl() );
		baseItem.put( "bldNm",						suggesstionSalesVO.getBldNm() );
		baseItem.put( "addr",						suggesstionSalesVO.getAddr() );
		baseItem.put( "addrShortNm",				suggesstionSalesVO.getAddr().substring( 0, suggesstionSalesVO.getAddr().indexOf( " " ) ) + " "  + suggesstionSalesVO.getAddrShortNm() );
		baseItem.put( "lat",						suggesstionSalesVO.getLat() );
		baseItem.put( "lng",						suggesstionSalesVO.getLng() );
		baseItem.put( "salesDtlDscr",				suggesstionSalesVO.getSalesDtlDscr() );
		baseItem.put( "minFlr",						GsntalkUtil.parseFloorNm( suggesstionSalesVO.getMinFlr() ) );
		baseItem.put( "maxFlr",						GsntalkUtil.parseFloorNm( suggesstionSalesVO.getMaxFlr() ) );
		baseItem.put( "totBldCnt",					suggesstionSalesVO.getTotBldCnt() );
		baseItem.put( "parkingCarCnt",				suggesstionSalesVO.getParkingCarCnt() );
		baseItem.put( "husHoldCnt",					suggesstionSalesVO.getHusHoldCnt() );
		baseItem.put( "lndArea",					GsntalkUtil.parsePyungToMeters( suggesstionSalesVO.getLndArea() ) );
		baseItem.put( "bldArea",					GsntalkUtil.parsePyungToMeters( suggesstionSalesVO.getBldArea() ) );
		baseItem.put( "totFlrArea",					GsntalkUtil.parsePyungToMeters( suggesstionSalesVO.getTotFlrArea() ) );
		baseItem.put( "flrAreaRatio",				suggesstionSalesVO.getFlrAreaRatio() );
		baseItem.put( "bldToLndRatio",				suggesstionSalesVO.getBldToLndRatio() );
		String cmpltnDate = suggesstionSalesVO.getCmpltnDate();
		baseItem.put( "cmpltnYm",					cmpltnDate.substring( 0, 4 ) + "년 " + cmpltnDate.substring( 4, 6 ) + "월" );
		String expctMovMonth = suggesstionSalesVO.getExpctMovMonth();
		baseItem.put( "expctMovMonth",				expctMovMonth.substring( 0, 4 ) + "년 " + expctMovMonth.substring( 4, 6 ) + "월" );
		baseItem.put( "devCompNm",					suggesstionSalesVO.getDevCompNm() );
		baseItem.put( "constCompNm",				suggesstionSalesVO.getConstCompNm() );
		baseItem.put( "matterPortLinkUrl",			suggesstionSalesVO.getMatterPortLinkUrl() );
		baseItem.put( "repImgUrl",					suggesstionSalesVO.getRepImgUrl() );
		baseItem.put( "favYn",						suggesstionSalesVO.getFavYn() );
		resMap.put( "baseItem", baseItem );
		
		// 교육자료 첨부파일 URL 목록 ( eduFileUrlItems )
		List<SuggestionSalesVO> eduDataList = suggestionSalesDAO.getEducationDataAttachmentList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( eduDataList ) ) {
			eduDataList = new ArrayList<SuggestionSalesVO>();
		}
		JSONArray eduFileUrlItems = new JSONArray();
		for( SuggestionSalesVO vo : eduDataList ) {
			eduFileUrlItems.add( vo.getFileUrl() );
		}
		resMap.put( "eduFileUrlItems", eduFileUrlItems );

		
		JSONArray dongItems = new JSONArray();
		JSONObject dongItem = null;
		JSONArray flrItems = null;
		JSONObject flrItem = null;
		List<SuggestionSalesVO> flrList = null;
		// 동 및 층별정보 ( dongItems )
		List<SuggestionSalesVO> dongList = suggestionSalesDAO.getSuggstnSalesDongList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( dongList ) ) {
			dongList = new ArrayList<SuggestionSalesVO>();
		}
		for( SuggestionSalesVO dongVO : dongList ) {
			dongItem = new JSONObject();
			
			dongItem.put( "dongSeqno",					dongVO.getDongSeqno() );
			dongItem.put( "dongNm",						dongVO.getDongNm() );
			
			// 추천분양 매물 층 및 층별 용도정보 목록조회
			flrList = suggestionSalesDAO.getSuggstnSalesFloorWithUsageListOfDong( suggstnSalesSeqno, dongVO.getDongSeqno() );
			if( GsntalkUtil.isEmptyList( flrList ) ) {
				flrList = new ArrayList<SuggestionSalesVO>();
			}
			flrItems = new JSONArray();
			for( SuggestionSalesVO flrVO : flrList ) {
				flrItem = new JSONObject();
				
				flrItem.put( "suggstnSalesFlrSeqno",		flrVO.getSuggstnSalesFlrSeqno() );
				flrItem.put( "flrUsageGbNm",				flrVO.getFlrUsageGbNm() );
				flrItem.put( "stFlr",						GsntalkUtil.parseFloorNm( flrVO.getStFlr() ) );
				flrItem.put( "edFlr",						GsntalkUtil.parseFloorNm( flrVO.getEdFlr() ) );
				flrItem.put( "flrPlanFileUrl",				flrVO.getFlrPlanFileUrl() );
				
				flrItems.add( flrItem );
			}
			dongItem.put( "flrItems",					flrItems );
			
			dongItems.add( dongItem );
		}
		resMap.put( "dongItems", dongItems );
		
		// 추천분양 프리미엄정보 목록조회
		List<SuggestionSalesVO> prmmList = suggestionSalesDAO.getSuggstnSalesPrmmList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( prmmList ) ) {
			prmmList = new ArrayList<SuggestionSalesVO>();
		}
		JSONArray prmmItems = new JSONArray();
		JSONObject prmmItem = null;
		for( SuggestionSalesVO vo : prmmList ) {
			prmmItem = new JSONObject();
			
			prmmItem.put( "prmmTtl",		vo.getPrmmTtl() );
			prmmItem.put( "prmmDscr",		vo.getPrmmDscr() );
			
			prmmItems.add( prmmItem );
		}
		resMap.put( "prmmItems", prmmItems );
		
		// 추천분양 일정 목록조회
		List<SuggestionSalesVO> salesSchdlList = suggestionSalesDAO.getSuggstnSalesSchdlList( suggstnSalesSeqno );
		if( GsntalkUtil.isEmptyList( salesSchdlList ) ) {
			salesSchdlList = new ArrayList<SuggestionSalesVO>();
		}
		JSONArray salesSchdlItems = new JSONArray();
		JSONObject salesSchdlItem = null;
		String schdlStDate = null;
		String schdlEdDate = null;
		for( SuggestionSalesVO vo : salesSchdlList ) {
			salesSchdlItem = new JSONObject();
			
			salesSchdlItem.put( "schdlNm",			vo.getSchdlNm() );
			
			schdlStDate = vo.getSchdlStDate();
			schdlEdDate = vo.getSchdlEdDate();
			
			salesSchdlItem.put( "schdlStYr",		schdlStDate.substring( 0, 4 ) + "년" );
			salesSchdlItem.put( "schdlStMd",		schdlStDate.substring( 4, 6 ) + "." + schdlStDate.substring( 6, 8 ) );
			salesSchdlItem.put( "schdlEdMd",		schdlEdDate.substring( 4, 6 ) + "." + schdlEdDate.substring( 6, 8 ) );
			salesSchdlItem.put( "dday",				vo.getDday() == 0 ? "D-Day" : vo.getDday() < 0 ? "D" + vo.getDday() : "" );
			
			salesSchdlItems.add( salesSchdlItem );
		}
		resMap.put( "salesSchdlItems", salesSchdlItems );
		
		return resMap;
	}
	
	/**
	 * FRT - 분양알림 설정
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void setSalesSchdlNoti( long memSeqno, long suggstnSalesSeqno )throws Exception {
		int c = suggestionSalesDAO.isExistsSuggstnSalesSeqno( suggstnSalesSeqno );
		if( c == 0 ) {
			// 유효하지 않은 추천분양
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SUGGSTN_SALES_PROPERTY, "not found suggstnSalesSeqno -> " + suggstnSalesSeqno );
		}
		
		// 분양알림 설정여부 확인
		c = suggestionSalesDAO.getSalesSchdlNotiCnt( memSeqno, suggstnSalesSeqno );
		if( c == 0 ) {
			// FRT - 분양알림 설정
			suggestionSalesDAO.setSalesSchdlNoti( memSeqno, suggstnSalesSeqno );
		}
	}
	
	/**
	 * FRT - 분양알림 해제
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void releaseSalesSchdlNoti( long memSeqno, long suggstnSalesSeqno )throws Exception {
		// FRT - 분양알림 해제
		suggestionSalesDAO.releaseSalesSchdlNoti( memSeqno, suggstnSalesSeqno );
	}
}