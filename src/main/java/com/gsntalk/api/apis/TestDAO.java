package com.gsntalk.api.apis;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.EstateBrokerOfficeVO;
import com.gsntalk.api.common.vo.KnowledgeIndustryComplexVO;
import com.gsntalk.api.common.vo.PropertyVO;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Repository( "com.gsntalk.api.apis.TestDAO" )
public class TestDAO extends CommonDAO {

	public TestDAO() {
		super( TestDAO.class );
	}
	
	public String getServerTime()throws Exception {
		return sqlSession.selectOne( "TestMapper.getServerTime" );
	}
	
	/**
	 * 공인중개사 사무소 업데이트 유형 조회
	 * 
	 * @param estateBrokerOfficeVO
	 * @return
	 * @throws Exception
	 */
	public String getEstateBrokerOfficeDataUpdateType( EstateBrokerOfficeVO estateBrokerOfficeVO )throws Exception {
		return sqlSession.selectOne( "TestMapper.getEstateBrokerOfficeDataUpdateType", estateBrokerOfficeVO );
	}
	
	/**
	 * 공인중개사 사무소 정보 등록
	 * 
	 * @param estateBrokerOfficeVO
	 * @throws Exception
	 */
	public void registerEstateBrokerOffice( EstateBrokerOfficeVO estateBrokerOfficeVO )throws Exception {
		sqlSession.insert( "TestMapper.registerEstateBrokerOffice", estateBrokerOfficeVO );
	}
	
	/**
	 * 공인중개사 사무소 정보 업데이트
	 * 
	 * @param estateBrokerOfficeVO
	 * @throws Exception
	 */
	public void updateEstateBrokerOffice( EstateBrokerOfficeVO estateBrokerOfficeVO )throws Exception {
		sqlSession.insert( "TestMapper.updateEstateBrokerOffice", estateBrokerOfficeVO );
	}
	
	/**
	 * 지식산업센터 매물 등록
	 * 
	 * @param prptRegNo
	 * @param addr
	 * @param lat
	 * @param lng
	 * @param bldNm
	 * @param cmpltnDate
	 * @param askSalesMinPrc
	 * @param askSalesAvgPrc
	 * @param askSalesMaxPrc
	 * @param askLeaseMinPrc
	 * @param askLeaseAvgPrc
	 * @param askLeaseMaxPrc
	 * @param lndArea
	 * @param bldArea
	 * @param totFlrArea
	 * @param minFlr
	 * @param maxFlr
	 * @param parkingCarCnt
	 * @param husHoldCnt
	 * @param devCompNm
	 * @param constCompNm
	 * @param trfcInfo
	 * @param siteExplntn
	 * @param smplSmrDscr
	 * @throws Exception
	 */
	public void registerKnowlegdeIndustryComplex( String prptRegNo, String addr, double lat, double lng, String bldNm, String cmpltnDate,
			long askSalesMinPrc, long askSalesAvgPrc, long askSalesMaxPrc, long askLeaseMinPrc, long askLeaseAvgPrc, long askLeaseMaxPrc, double lndArea, double bldArea, double totFlrArea,
			int minFlr, int maxFlr, int parkingCarCnt, int husHoldCnt, String devCompNm, String constCompNm, String trfcInfo, String siteExplntn, String smplSmrDscr )throws Exception {
		
		KnowledgeIndustryComplexVO knowledgeIndustryComplexVO = new KnowledgeIndustryComplexVO();
		knowledgeIndustryComplexVO.setPrptRegNo( prptRegNo );
		knowledgeIndustryComplexVO.setAddr( GsntalkXSSUtil.encodeXss( addr ) );
		knowledgeIndustryComplexVO.setLat( lat );
		knowledgeIndustryComplexVO.setLng( lng );
		knowledgeIndustryComplexVO.setBldNm( GsntalkXSSUtil.encodeXss( bldNm ) );
		knowledgeIndustryComplexVO.setCmpltnDate( cmpltnDate );
		knowledgeIndustryComplexVO.setAskSalesMinPrc( askSalesMinPrc );
		knowledgeIndustryComplexVO.setAskSalesAvgPrc( askSalesAvgPrc );
		knowledgeIndustryComplexVO.setAskSalesMaxPrc( askSalesMaxPrc );
		knowledgeIndustryComplexVO.setAskLeaseMinPrc( askLeaseMinPrc );
		knowledgeIndustryComplexVO.setAskLeaseAvgPrc( askLeaseAvgPrc );
		knowledgeIndustryComplexVO.setAskLeaseMaxPrc( askLeaseMaxPrc );
		knowledgeIndustryComplexVO.setLndArea( lndArea );
		knowledgeIndustryComplexVO.setBldArea( bldArea );
		knowledgeIndustryComplexVO.setTotFlrArea( totFlrArea );
		knowledgeIndustryComplexVO.setMinFlr( minFlr );
		knowledgeIndustryComplexVO.setMaxFlr( maxFlr );
		knowledgeIndustryComplexVO.setParkingCarCnt( parkingCarCnt );
		knowledgeIndustryComplexVO.setHusHoldCnt( husHoldCnt );
		knowledgeIndustryComplexVO.setDevCompNm( GsntalkXSSUtil.encodeXss( devCompNm ) );
		knowledgeIndustryComplexVO.setConstCompNm( GsntalkXSSUtil.encodeXss( constCompNm ) );
		knowledgeIndustryComplexVO.setTrfcInfo( GsntalkXSSUtil.encodeXss( trfcInfo ) );
		knowledgeIndustryComplexVO.setSiteExplntn( GsntalkXSSUtil.encodeXss( siteExplntn ) );
		knowledgeIndustryComplexVO.setSmplSmrDscr( GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		
		sqlSession.insert( "TestMapper.registerKnowlegdeIndustryComplex", knowledgeIndustryComplexVO );
	}
	
	/**
	 * 지식산업센터 전체 목록조회
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<KnowledgeIndustryComplexVO> getAllKnowledgeIndustryComplexList()throws Exception {
		return sqlSession.selectList( "TestMapper.getAllKnowledgeIndustryComplexList" );
	}
	
	/**
	 * 매물 전체 목록조회
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getAllPropertyList()throws Exception {
		return sqlSession.selectList( "TestMapper.getAllPropertyList" );
	}
	
	/**
	 * 지식산업센터 단축주소 업데이트
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @param addrShortNm
	 * @throws Exception
	 */
	public void updateAddrShortNmToKnowledgeIndustryComplex( long knwldgIndCmplxSeqno, String addrShortNm )throws Exception {
		KnowledgeIndustryComplexVO knowledgeIndustryComplexVO = new KnowledgeIndustryComplexVO();
		knowledgeIndustryComplexVO.setKnwldgIndCmplxSeqno( knwldgIndCmplxSeqno );
		knowledgeIndustryComplexVO.setAddrShortNm( addrShortNm );
		
		sqlSession.update( "TestMapper.updateAddrShortNmToKnowledgeIndustryComplex", knowledgeIndustryComplexVO );
	}
	
	/**
	 * 매물 단축주소 업데이트
	 * 
	 * @param prptSeqno
	 * @param addrShortNm
	 * @throws Exception
	 */
	public void updateAddrShortNmToProperty( long prptSeqno, String addrShortNm )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setPrptSeqno( prptSeqno );
		propertyVO.setAddrShortNm( addrShortNm );
		
		sqlSession.update( "TestMapper.updateAddrShortNmToProperty", propertyVO );
	}
}