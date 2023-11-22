package com.gsntalk.api.apis.map;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.MapVO;
import com.gsntalk.api.util.GsntalkUtil;

@Repository( "com.gsntalk.api.apis.map.MapDAO" )
public class MapDAO extends CommonDAO {

	public MapDAO() {
		super( MapDAO.class );
	}
	
	/**
	 * 지도범위 내 매물 목록조회
	 * 
	 * @param memSeqno
	 * @param swLat
	 * @param swLng
	 * @param neLat
	 * @param neLng
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tranTypGbYn
	 * @param tranTypGbCdList
	 * @param dealAmtYn
	 * @param dealMinAmt
	 * @param dealMaxAmt
	 * @param dpstAmtYn
	 * @param dpstMinAmt
	 * @param dpstMaxAmt
	 * @param monRentAmtYn
	 * @param monRentMinAmt
	 * @param monRentMaxAmt
	 * @param splyAreaYn
	 * @param minSplyArea
	 * @param maxSplyArea
	 * @param prvAreaYn
	 * @param minPrvArea
	 * @param maxPrvArea
	 * @param lndAreaYn
	 * @param minLndArea
	 * @param maxLndArea
	 * @param totFlrAreaYn
	 * @param minTotFlrArea
	 * @param maxTotFlrArea
	 * @param monMntnceCostYn
	 * @param monMntnceMinCost
	 * @param monMntnceMaxCost
	 * @param useCnfrmYearSrchTypCd
	 * @param loanGbCd
	 * @param prmmAmtYn
	 * @param minPrmmAmt
	 * @param maxPrmmAmt
	 * @param sectrGbYn
	 * @param sectrGbCdList
	 * @param flrHghtTypGbCd
	 * @param elctrPwrTypGbCd
	 * @param sortItem
	 * @param sortTyp
	 * @return
	 * @throws Exception
	 */
	public List<MapVO> getPropertyListOfBounds( long memSeqno, double swLat, double swLng, double neLat, double neLng, String estateTypGbCd, String estateTypCd,
			String tranTypGbYn, List<String> tranTypGbCdList, String dealAmtYn, long dealMinAmt, long dealMaxAmt, String dpstAmtYn, long dpstMinAmt, long dpstMaxAmt,
			String monRentAmtYn, long monRentMinAmt, long monRentMaxAmt, String splyAreaYn, double minSplyArea, double maxSplyArea,
			String prvAreaYn, double minPrvArea, double maxPrvArea, String lndAreaYn, double minLndArea, double maxLndArea, String totFlrAreaYn, double minTotFlrArea, double maxTotFlrArea,
			String monMntnceCostYn, int monMntnceMinCost, int monMntnceMaxCost, String useCnfrmYearSrchTypCd, String loanGbCd, String prmmAmtYn, long minPrmmAmt, long maxPrmmAmt,
			String sectrGbYn, List<String> sectrGbCdList, String flrHghtTypGbCd, String elctrPwrTypGbCd, String sortItem, String sortTyp
	)throws Exception {
		MapVO mapVO = new MapVO();
		mapVO.setMemSeqno( memSeqno );
		mapVO.setSwLat( swLat );
		mapVO.setSwLng( swLng );
		mapVO.setNeLat( neLat );
		mapVO.setNeLng( neLng );
		mapVO.setEstateTypGbCd( estateTypGbCd );
		mapVO.setEstateTypCd( estateTypCd );
		mapVO.setTranTypGbYn( tranTypGbYn );
		mapVO.setTranTypGbCdList( tranTypGbCdList );
		mapVO.setDealAmtYn( dealAmtYn );
		mapVO.setDealMinAmt( dealMinAmt );
		mapVO.setDealMaxAmt( dealMaxAmt );
		mapVO.setDpstAmtYn( dpstAmtYn );
		mapVO.setDpstMinAmt( dpstMinAmt );
		mapVO.setDpstMaxAmt( dpstMaxAmt );
		mapVO.setMonRentAmtYn( monRentAmtYn );
		mapVO.setMonRentMinAmt( monRentMinAmt );
		mapVO.setMonRentMaxAmt( monRentMaxAmt );
		mapVO.setSplyAreaYn( splyAreaYn );
		mapVO.setMinSplyArea( GsntalkUtil.parseMetersToPyung( minSplyArea ) );
		mapVO.setMaxSplyArea( GsntalkUtil.parseMetersToPyung( maxSplyArea ) );
		mapVO.setPrvAreaYn( prvAreaYn );
		mapVO.setMinPrvArea( GsntalkUtil.parseMetersToPyung( minPrvArea ) );
		mapVO.setMaxPrvArea( GsntalkUtil.parseMetersToPyung( maxPrvArea ) );
		mapVO.setLndAreaYn( lndAreaYn );
		mapVO.setMinLndArea( GsntalkUtil.parseMetersToPyung( minLndArea ) );
		mapVO.setMaxLndArea( GsntalkUtil.parseMetersToPyung( maxLndArea ) );
		mapVO.setTotFlrAreaYn( totFlrAreaYn );
		mapVO.setMinTotFlrArea( GsntalkUtil.parseMetersToPyung( minTotFlrArea ) );
		mapVO.setMaxTotFlrArea( GsntalkUtil.parseMetersToPyung( maxTotFlrArea ) );
		mapVO.setMonMntnceCostYn( monMntnceCostYn );
		mapVO.setMonMntnceMinCost( monMntnceMinCost );
		mapVO.setMonMntnceMaxCost( monMntnceMaxCost );
		mapVO.setUseCnfrmYearSrchTypCd( useCnfrmYearSrchTypCd );
		mapVO.setLoanGbCd( loanGbCd );
		mapVO.setPrmmAmtYn( prmmAmtYn );
		mapVO.setMinPrmmAmt( minPrmmAmt );
		mapVO.setMaxPrmmAmt( maxPrmmAmt );
		mapVO.setSectrGbYn( sectrGbYn );
		mapVO.setSectrGbCdList( sectrGbCdList );
		mapVO.setFlrHghtTypGbCd( flrHghtTypGbCd );
		mapVO.setElctrPwrTypGbCd( elctrPwrTypGbCd );
		mapVO.setSortItem( sortItem );
		mapVO.setSortTyp( sortTyp );
		
		return sqlSession.selectList( "MapMapper.getPropertyListOfBounds", mapVO );
	}
	
	/**
	 * 지도범위 내 지식산업센터 목록조회
	 * 
	 * @param swLat
	 * @param swLng
	 * @param neLat
	 * @param neLng
	 * @return
	 * @throws Exception
	 */
	public List<MapVO> getKnowledgeIndustryComplexListOfBounds( double swLat, double swLng, double neLat, double neLng )throws Exception {
		MapVO mapVO = new MapVO();
		mapVO.setSwLat( swLat );
		mapVO.setSwLng( swLng );
		mapVO.setNeLat( neLat );
		mapVO.setNeLng( neLng );
		
		return sqlSession.selectList( "MapMapper.getKnowledgeIndustryComplexListOfBounds", mapVO );
	}
	
	/**
	 * 지도범위 내 중개사 목록조회
	 * 
	 * @param swLat
	 * @param swLng
	 * @param neLat
	 * @param neLng
	 * @return
	 * @throws Exception
	 */
	public List<MapVO> getEstateBrokerOfficeListOfBounds( double swLat, double swLng, double neLat, double neLng )throws Exception {
		MapVO mapVO = new MapVO();
		mapVO.setSwLat( swLat );
		mapVO.setSwLng( swLng );
		mapVO.setNeLat( neLat );
		mapVO.setNeLng( neLng );
		
		return sqlSession.selectList( "MapMapper.getEstateBrokerOfficeListOfBounds", mapVO );
	}
	
}