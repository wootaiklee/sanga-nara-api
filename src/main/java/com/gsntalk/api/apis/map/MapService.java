package com.gsntalk.api.apis.map;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.MapVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkUtil;

@Service( "MapService" )
public class MapService extends CommonService {

	@Autowired
	private MapDAO mapDAO;
	
	public MapService() {
		super( MapService.class );
	}
	
	/**
	 * 매물지도 검색
	 * 
	 * @param memSeqno
	 * @param swLat
	 * @param swLng
	 * @param neLat
	 * @param neLng
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tranTypGbCdItems
	 * @param dealMinAmt
	 * @param dealMaxAmt
	 * @param dpstMinAmt
	 * @param dpstMaxAmt
	 * @param monRentMinAmt
	 * @param monRentMaxAmt
	 * @param minSplyArea
	 * @param maxSplyArea
	 * @param minPrvArea
	 * @param maxPrvArea
	 * @param minLndArea
	 * @param maxLndArea
	 * @param minTotFlrArea
	 * @param maxTotFlrArea
	 * @param monMntnceMinCost
	 * @param monMntnceMaxCost
	 * @param useCnfrmYearSrchTypCd
	 * @param loanGbCd
	 * @param minPrmmAmt
	 * @param maxPrmmAmt
	 * @param sectrGbCdItems
	 * @param flrHghtTypGbCd
	 * @param elctrPwrTypGbCd
	 * @param sortItem
	 * @param sortTyp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getPropertyMapItem( long memSeqno, double swLat, double swLng, double neLat, double neLng, String estateTypGbCd, String estateTypCd,
			JSONArray tranTypGbCdItems, String dealMinAmt, String dealMaxAmt, String dpstMinAmt, String dpstMaxAmt, String monRentMinAmt, String monRentMaxAmt,
			String minSplyArea, String maxSplyArea, String minPrvArea, String maxPrvArea, String minLndArea, String maxLndArea, String minTotFlrArea, String maxTotFlrArea,
			String monMntnceMinCost, String monMntnceMaxCost, String useCnfrmYearSrchTypCd, String loanGbCd, String minPrmmAmt, String maxPrmmAmt, JSONArray sectrGbCdItems,
			String flrHghtTypGbCd, String elctrPwrTypGbCd, String sortItem, String sortTyp
	)throws Exception {
		
		List<String> tranTypGbCdList = GsntalkUtil.jsonStringArrayToList( tranTypGbCdItems );
		List<String>  sectrGbCdList = GsntalkUtil.jsonStringArrayToList( sectrGbCdItems );
		String tranTypGbYn = tranTypGbCdList.size() == 0 ? GsntalkConstants.NO : GsntalkConstants.YES;
		String sectrGbYn = sectrGbCdList.size() == 0 ? GsntalkConstants.NO : GsntalkConstants.YES;
		
		String dealAmtYn = GsntalkUtil.isEmpty( dealMinAmt ) && GsntalkUtil.isEmpty( dealMaxAmt ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String dpstAmtYn = GsntalkUtil.isEmpty( dpstMinAmt ) && GsntalkUtil.isEmpty( dpstMaxAmt ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String monRentAmtYn = GsntalkUtil.isEmpty( monRentMinAmt ) && GsntalkUtil.isEmpty( monRentMaxAmt ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String splyAreaYn = GsntalkUtil.isEmpty( minSplyArea ) && GsntalkUtil.isEmpty( maxSplyArea ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String prvAreaYn = GsntalkUtil.isEmpty( minPrvArea ) && GsntalkUtil.isEmpty( maxPrvArea ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String lndAreaYn = GsntalkUtil.isEmpty( minLndArea ) && GsntalkUtil.isEmpty( maxLndArea ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String totFlrAreaYn = GsntalkUtil.isEmpty( minTotFlrArea ) && GsntalkUtil.isEmpty( maxTotFlrArea ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String monMntnceCostYn = GsntalkUtil.isEmpty( monMntnceMinCost ) && GsntalkUtil.isEmpty( monMntnceMaxCost ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		String prmmAmtYn = GsntalkUtil.isEmpty( minPrmmAmt ) && GsntalkUtil.isEmpty( maxPrmmAmt ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		
		if( GsntalkUtil.isEmpty( sortItem ) || !GsntalkUtil.isIn( sortItem, "DATA", "COST", "AREA" ) ) {
			sortItem = "DATA";
		}
		if( GsntalkUtil.isEmpty( sortTyp ) || !GsntalkUtil.isIn( sortTyp, "ASC", "DESC" ) ) {
			sortTyp = "ASC";
		}
		
		JSONArray items = null;
		JSONObject item = null;
		
		/* 지도범위 내 매물 목록조회 */
		List<MapVO> propertyList = mapDAO.getPropertyListOfBounds( memSeqno, swLat, swLng, neLat, neLng, estateTypGbCd, estateTypCd,
				tranTypGbYn, tranTypGbCdList, dealAmtYn, GsntalkUtil.parseKrAmtToLongAmt( dealMinAmt ), GsntalkUtil.parseKrAmtToLongAmt( dealMaxAmt ), dpstAmtYn, GsntalkUtil.parseKrAmtToLongAmt( dpstMinAmt ), GsntalkUtil.parseKrAmtToLongAmt( dpstMaxAmt ),
				monRentAmtYn, GsntalkUtil.parseKrAmtToLongAmt( monRentMinAmt ), GsntalkUtil.parseKrAmtToLongAmt( monRentMaxAmt ), splyAreaYn, GsntalkUtil.getDouble( minSplyArea ), GsntalkUtil.getDouble( maxSplyArea ),
				prvAreaYn, GsntalkUtil.getDouble( minPrvArea ), GsntalkUtil.getDouble( maxPrvArea ), lndAreaYn, GsntalkUtil.getDouble( minLndArea ), GsntalkUtil.getDouble( maxLndArea ), totFlrAreaYn, GsntalkUtil.getDouble( minTotFlrArea ), GsntalkUtil.getDouble( maxTotFlrArea ),
				monMntnceCostYn, GsntalkUtil.parseKrAmtToIntegerAmt( monMntnceMinCost ), GsntalkUtil.parseKrAmtToIntegerAmt( monMntnceMaxCost ), useCnfrmYearSrchTypCd, loanGbCd, prmmAmtYn, GsntalkUtil.parseKrAmtToLongAmt( minPrmmAmt ), GsntalkUtil.parseKrAmtToLongAmt( maxPrmmAmt ),
				sectrGbYn, sectrGbCdList,  flrHghtTypGbCd, elctrPwrTypGbCd, sortItem, sortTyp );
		
		if( GsntalkUtil.isEmptyList( propertyList ) ) {
			propertyList = new ArrayList<MapVO>();
		}
		
		JSONObject propertyItem = new JSONObject();
		propertyItem.put( "size", propertyList.size() );
		
		items = new JSONArray();
		for( MapVO vo : propertyList ) {
			item = new JSONObject();
			
			item.put( "prptSeqno",			vo.getPrptSeqno() );
			item.put( "estateTypGbCd",		vo.getEstateTypGbCd() );
			item.put( "estateTypGbNm",		vo.getEstateTypGbNm() );
			item.put( "estateTypCd",		vo.getEstateTypCd() );
			item.put( "estateTypNm",		vo.getEstateTypNm() );
			item.put( "tranTypGbCd",		vo.getTranTypGbCd() );
			item.put( "tranTypGbNm",		vo.getTranTypGbNm() );
			item.put( "cost",				GsntalkUtil.parseAmtToKr( vo.getCost() ) );
			item.put( "montRentAmt",		GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() ) );
			item.put( "splyArea",			GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
			item.put( "prvArea",			GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
			item.put( "lndArea",			GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			item.put( "totFlrArea",			GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			item.put( "flr",				vo.getFlr() );
			item.put( "smplSmrDscr",		vo.getSmplSmrDscr() );
			item.put( "lat",				vo.getLat() );
			item.put( "lng",				vo.getLng() );
			item.put( "addrShortNm",		vo.getAddrShortNm() );
			item.put( "reprImgUrl",			GsntalkUtil.nullToEmptyString( vo.getReprImgUrl() ) );
			item.put( "favYn",				vo.getFavYn() );
			
			items.add( item );
		}
		propertyItem.put( "items", items );
		
		
		/* 지도범위 내 지식산업센터 목록조회
		List<MapVO> knwldgIndCmplxList = mapDAO.getKnowledgeIndustryComplexListOfBounds( swLat, swLng, neLat, neLng );
		if( GsntalkUtil.isEmptyList( knwldgIndCmplxList ) ) {
			knwldgIndCmplxList = new ArrayList<MapVO>();
		}
		
		JSONObject knwldgIndCmplxItem = new JSONObject();
		knwldgIndCmplxItem.put( "size", knwldgIndCmplxList.size() );
		
		items = new JSONArray();
		for( MapVO vo : knwldgIndCmplxList ) {
			item = new JSONObject();
			
			item.put( "knwldgIndCmplxSeqno",	vo.getKnwldgIndCmplxSeqno() );
			item.put( "bldNm",					vo.getBldNm() );
			item.put( "lndArea",				GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			item.put( "bldArea",				GsntalkUtil.parsePyungToMeters( vo.getBldArea() ) );
			item.put( "totFlrArea",				GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			item.put( "minFlr",					vo.getMinFlr() );
			item.put( "maxFlr",					vo.getMaxFlr() );
			item.put( "smplSmrDscr",			vo.getSmplSmrDscr() );
			item.put( "lat",					vo.getLat() );
			item.put( "lng",					vo.getLng() );
			item.put( "addrShortNm",			vo.getAddrShortNm() );
			
			items.add( item );
		}
		knwldgIndCmplxItem.put( "items", items );
		 */
		
		/* 지도범위 내 중개사 목록조회 */
		List<MapVO> estBrkOfcList = mapDAO.getEstateBrokerOfficeListOfBounds( swLat, swLng, neLat, neLng );
		if( GsntalkUtil.isEmptyList( estBrkOfcList ) ) {
			estBrkOfcList = new ArrayList<MapVO>();
		}
		
		JSONObject estateBrokerOfficeItem = new JSONObject();
		estateBrokerOfficeItem.put( "size", estBrkOfcList.size() );
		
		items = new JSONArray();
		for( MapVO vo : estBrkOfcList ) {
			item = new JSONObject();
			
			item.put( "estBrkMemOfcSeqno",		vo.getEstBrkMemOfcSeqno() );
			item.put( "ofcNm",					vo.getOfcNm() );
			item.put( "lat",					vo.getLat() );
			item.put( "lng",					vo.getLng() );
			item.put( "addr",					vo.getAddr() );
			item.put( "addrShortNm",			vo.getAddrShortNm() );
			item.put( "prflImgUrl",				vo.getPrflImgUrl() );
			
			items.add( item );
		}
		estateBrokerOfficeItem.put( "items", items );
		
		
		
		JSONObject resObj = new JSONObject();
		resObj.put( "propertyItem",				propertyItem );
		// resObj.put( "knwldgIndCmplxItem",		knwldgIndCmplxItem );
		resObj.put( "estateBrokerOfficeItem",	estateBrokerOfficeItem );
		
		return resObj;
	}
	
	/**
	 * 실거래가 지도 검색
	 * 
	 * @param memSeqno
	 * @param swLat
	 * @param swLng
	 * @param neLat
	 * @param neLng
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getRealDealMapItem( long memSeqno, double swLat, double swLng, double neLat, double neLng )throws Exception {
		JSONArray items = null;
		JSONObject item = null;
		
		/* 지도범위 내 지식산업센터 목록조회 */
		List<MapVO> knwldgIndCmplxList = mapDAO.getKnowledgeIndustryComplexListOfBounds( swLat, swLng, neLat, neLng );
		if( GsntalkUtil.isEmptyList( knwldgIndCmplxList ) ) {
			knwldgIndCmplxList = new ArrayList<MapVO>();
		}
		
		JSONObject knwldgIndCmplxItem = new JSONObject();
		knwldgIndCmplxItem.put( "size", knwldgIndCmplxList.size() );
		
		items = new JSONArray();
		for( MapVO vo : knwldgIndCmplxList ) {
			item = new JSONObject();
			
			item.put( "knwldgIndCmplxSeqno",	vo.getKnwldgIndCmplxSeqno() );
			item.put( "bldNm",					vo.getBldNm() );
			item.put( "lat",					vo.getLat() );
			item.put( "lng",					vo.getLng() );
			item.put( "addr",					vo.getAddr() );
			item.put( "askSalesMinPrc",			GsntalkUtil.parseAmtToKr( vo.getAskSalesMinPrc() ) );
			item.put( "askSalesAvgPrc",			GsntalkUtil.parseAmtToKr( vo.getAskSalesAvgPrc() ) );
			item.put( "askSalesMaxPrc",			GsntalkUtil.parseAmtToKr( vo.getAskSalesMaxPrc() ) );
			item.put( "askLeaseMinPrc",			GsntalkUtil.parseAmtToKr( vo.getAskLeaseMinPrc() ) );
			item.put( "askLeaseAvgPrc",			GsntalkUtil.parseAmtToKr( vo.getAskLeaseAvgPrc() ) );
			item.put( "askLeaseMaxPrc",			GsntalkUtil.parseAmtToKr( vo.getAskLeaseMaxPrc() ) );

			item.put( "askSalesMinPrcNum",		vo.getAskSalesMinPrc() / 10000 );
			item.put( "askSalesMaxPrcNum",		vo.getAskSalesMaxPrc() / 10000 );
			item.put( "askLeaseMinPrcNum",		vo.getAskLeaseMinPrc() / 10000 );
			item.put( "askLeaseMaxPrcNum",		vo.getAskLeaseMaxPrc() / 10000 );
			
			items.add( item );
		}
		knwldgIndCmplxItem.put( "items", items );
		
		JSONObject resObj = new JSONObject();
		resObj.put( "knwldgIndCmplxItem",		knwldgIndCmplxItem );
		
		return resObj;
	}
}