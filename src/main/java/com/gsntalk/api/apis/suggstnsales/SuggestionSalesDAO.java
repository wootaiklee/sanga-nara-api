package com.gsntalk.api.apis.suggstnsales;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.RegistrationTmpDataStepVO;
import com.gsntalk.api.common.vo.SuggestionSalesVO;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Repository( "com.gsntalk.api.apis.suggstnsales.SuggestionSalesDAO" )
public class SuggestionSalesDAO extends CommonDAO {

	public SuggestionSalesDAO() {
		super( SuggestionSalesDAO.class );
	}
	
	/**
	 * 추천분양시퀀스 유효성 검증
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public int isExistsSuggstnSalesSeqno( long suggstnSalesSeqno )throws Exception {
		return sqlSession.selectOne( "SuggestionSalesMapper.isExistsSuggstnSalesSeqno", suggstnSalesSeqno );
	}
	
	/**
	 * 추천분양 1단계 임시등록 키 조회
	 * 
	 * @param prptSeqno
	 * @return
	 */
	public String getSuggstnSalesTempdataOfRegistrationKey( long suggstnSalesSeqno ) {
		return sqlSession.selectOne( "SuggestionSalesMapper.getSuggstnSalesTempdataOfRegistrationKey", suggstnSalesSeqno );
	}
	
	/**
	 * 관리자 추천분양 매물등록
	 * 
	 * @param memSeqno
	 * @param suggstnSalesRegionGbCd
	 * @param addr
	 * @param addrShortNm
	 * @param bldNm
	 * @param lat
	 * @param lng
	 * @param suggstnSalesTtl
	 * @param salesDtlDscr
	 * @param minFlr
	 * @param maxFlr
	 * @param totBldCnt
	 * @param parkingCarCnt
	 * @param husHoldCnt
	 * @param lndArea
	 * @param bldArea
	 * @param totFlrArea
	 * @param flrAreaRatio
	 * @param bldToLndRatio
	 * @param cmpltnDate
	 * @param expctMovMonth
	 * @param devCompNm
	 * @param constCompNm
	 * @param matterPortLinkUrl
	 * @return
	 * @throws Exception
	 */
	public long registerAdminSuggstnSalesProperty( long memSeqno, String suggstnSalesRegionGbCd, String addr, String addrShortNm, String bldNm, double lat, double lng, String suggstnSalesTtl, String salesDtlDscr, int minFlr, int maxFlr, int totBldCnt,
			int parkingCarCnt, int husHoldCnt, double lndArea, double bldArea, double totFlrArea, double flrAreaRatio, double bldToLndRatio, String cmpltnDate, String expctMovMonth, String devCompNm, String constCompNm, String matterPortLinkUrl )throws Exception {
		
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesRegionGbCd( suggstnSalesRegionGbCd );
		suggestionSalesVO.setAddr( GsntalkUtil.addressTopLvlReplace( addr ) );
		suggestionSalesVO.setAddrShortNm( addrShortNm );
		suggestionSalesVO.setBldNm( bldNm );
		suggestionSalesVO.setLat( lat );
		suggestionSalesVO.setLng( lng );
		suggestionSalesVO.setSuggstnSalesTtl( suggstnSalesTtl );
		suggestionSalesVO.setSalesDtlDscr( salesDtlDscr );
		suggestionSalesVO.setMinFlr( minFlr );
		suggestionSalesVO.setMaxFlr( maxFlr );
		suggestionSalesVO.setTotBldCnt( totBldCnt );
		suggestionSalesVO.setParkingCarCnt( parkingCarCnt );
		suggestionSalesVO.setHusHoldCnt( husHoldCnt );
		suggestionSalesVO.setLndArea( lndArea );
		suggestionSalesVO.setBldArea( bldArea );
		suggestionSalesVO.setTotFlrArea( totFlrArea );
		suggestionSalesVO.setFlrAreaRatio( flrAreaRatio );
		suggestionSalesVO.setBldToLndRatio( bldToLndRatio );
		suggestionSalesVO.setCmpltnDate( cmpltnDate );
		suggestionSalesVO.setExpctMovMonth( expctMovMonth );
		suggestionSalesVO.setDevCompNm( devCompNm );
		suggestionSalesVO.setConstCompNm( constCompNm );
		suggestionSalesVO.setMatterPortLinkUrl( matterPortLinkUrl );
		
		sqlSession.insert( "SuggestionSalesMapper.registerAdminSuggstnSalesProperty", suggestionSalesVO );
		
		return suggestionSalesVO.getSuggstnSalesSeqno();
	}
	
	/**
	 * 관리자 추천분양 대표이미지 정보 업데이트
	 * 
	 * @param suggstnSalesSeqno
	 * @param repImgSaveFileNm
	 * @param repImgUrl
	 * @throws Exception
	 */
	public void updateAdminSuggstnSalesRepImageInfo( long suggstnSalesSeqno, String repImgSaveFileNm, String repImgUrl )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setRepImgSaveFileNm( repImgSaveFileNm );
		suggestionSalesVO.setRepImgUrl( repImgUrl );
		
		sqlSession.update( "SuggestionSalesMapper.updateAdminSuggstnSalesRepImageInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자 추천분양 교육자료 첨부파일 정보 등록
	 * 
	 * @param suggstnSalesSeqno
	 * @param saveFileNm
	 * @param fileUrl
	 * @throws Exception
	 */
	public void registerAdminSuggstnSalesEducationDataAttch( long suggstnSalesSeqno, String saveFileNm, String fileUrl )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setSaveFileNm( saveFileNm );
		suggestionSalesVO.setFileUrl( fileUrl );
		
		sqlSession.insert( "SuggestionSalesMapper.registerAdminSuggstnSalesEducationDataAttch", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 동정보 등록
	 * 
	 * @param suggstnSalesSeqno
	 * @param dongNm
	 * @return
	 * @throws Exception
	 */
	public long registerAdminSuggstnSalesDongInfo( long suggstnSalesSeqno, String dongNm )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongNm( dongNm );
		
		sqlSession.insert( "SuggestionSalesMapper.registerAdminSuggstnSalesDongInfo", suggestionSalesVO );
		return suggestionSalesVO.getDongSeqno();
	}
	
	/**
	 * 관리자추천분양 동정보 업데이트
	 * 
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @param dongNm
	 * @return
	 * @throws Exception
	 */
	public void updateAdminSuggstnSalesDongInfo( long dongSeqno, long suggstnSalesSeqno, String dongNm )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		suggestionSalesVO.setDongNm( dongNm );
		
		sqlSession.update( "SuggestionSalesMapper.updateAdminSuggstnSalesDongInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 동 하위 동별 용도 일괄 삭제
	 * 
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public void deleteAdminSuggstnSalesDongUnderUages( long dongSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		
		sqlSession.update( "SuggestionSalesMapper.deleteAdminSuggstnSalesDongUnderUages", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 동 하위 층별정보 일괄 삭제
	 * 
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public void deleteAdminSuggstnSalesDongUnderFloors( long dongSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		
		sqlSession.update( "SuggestionSalesMapper.deleteAdminSuggstnSalesDongUnderFloors", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 동정보 삭제
	 * 
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public void deleteAdminSuggstnSalesDongInfo( long dongSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		
		sqlSession.update( "SuggestionSalesMapper.deleteAdminSuggstnSalesDongInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 동별 용도정보 등록
	 * 
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @param flrUsageGbCd
	 * @return
	 * @throws Exception
	 */
	public long registerAdminSuggstnSalesUsageByDongInfo( long dongSeqno, long suggstnSalesSeqno, String flrUsageGbCd )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setDongSeqno( dongSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setFlrUsageGbCd( flrUsageGbCd );
	
		sqlSession.insert( "SuggestionSalesMapper.registerAdminSuggstnSalesUsageByDongInfo", suggestionSalesVO );
		return suggestionSalesVO.getUsageByDongSeqno();
	}
	
	/**
	 * 관리자추천분양 동별 용도정보 업데이트
	 * 
	 * @param usageByDongSeqno
	 * @param suggstnSalesSeqno
	 * @param flrUsageGbCd
	 * @return
	 * @throws Exception
	 */
	public void updateAdminSuggstnSalesUsageByDongInfo( long usageByDongSeqno, long suggstnSalesSeqno, String flrUsageGbCd )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setUsageByDongSeqno( usageByDongSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setFlrUsageGbCd( flrUsageGbCd );
		
		sqlSession.update( "SuggestionSalesMapper.updateAdminSuggstnSalesUsageByDongInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 동별 용도정보 삭제
	 * 
	 * @param usageByDongSeqno
	 * @param suggstnSalesSeqno
	 * @param flrUsageGbCd
	 * @return
	 * @throws Exception
	 */
	public void deleteAdminSuggstnSalesUsageByDongInfo( long usageByDongSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setUsageByDongSeqno( usageByDongSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.update( "SuggestionSalesMapper.deleteAdminSuggstnSalesUsageByDongInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 동별 용도 하위 층별정보 일괄 삭제
	 * 
	 * @param usageByDongSeqno
	 * @param suggstnSalesSeqno
	 * @param flrUsageGbCd
	 * @return
	 * @throws Exception
	 */
	public void deleteAdminSuggstnSalesUsageByDongUnderFloors( long usageByDongSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setUsageByDongSeqno( usageByDongSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.update( "SuggestionSalesMapper.deleteAdminSuggstnSalesUsageByDongUnderFloors", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 층별정보 등록
	 * 
	 * @param usageByDongSeqno
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @param stFlr
	 * @param edFlr
	 * @param flrPlanSaveFileNm
	 * @param flrPlanFileUrl
	 * @throws Exception
	 */
	public void registerAdminSuggstnSalesFloorInfo( long usageByDongSeqno, long dongSeqno, long suggstnSalesSeqno, int stFlr, int edFlr, String flrPlanSaveFileNm, String flrPlanFileUrl )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setUsageByDongSeqno( usageByDongSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setStFlr( stFlr );
		suggestionSalesVO.setEdFlr( edFlr );
		suggestionSalesVO.setFlrPlanSaveFileNm( flrPlanSaveFileNm );
		suggestionSalesVO.setFlrPlanFileUrl( flrPlanFileUrl );
		
		sqlSession.insert( "SuggestionSalesMapper.registerAdminSuggstnSalesFloorInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 층별정보 업데이트
	 * 
	 * @param suggstnSalesFlrSeqno
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @param stFlr
	 * @param edFlr
	 * @param flrPlanSaveFileDelYn
	 * @param flrPlanSaveFileNm
	 * @param flrPlanFileUrl
	 * @throws Exception
	 */
	public void updateAdminSuggstnSalesFloorInfo( long suggstnSalesFlrSeqno, long dongSeqno, long suggstnSalesSeqno, int stFlr, int edFlr, String flrPlanSaveFileDelYn, String flrPlanSaveFileNm, String flrPlanFileUrl )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesFlrSeqno( suggstnSalesFlrSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setStFlr( stFlr );
		suggestionSalesVO.setEdFlr( edFlr );
		suggestionSalesVO.setFlrPlanSaveFileDelYn( flrPlanSaveFileDelYn );
		suggestionSalesVO.setFlrPlanSaveFileNm( flrPlanSaveFileNm );
		suggestionSalesVO.setFlrPlanFileUrl( flrPlanFileUrl );
		
		sqlSession.update( "SuggestionSalesMapper.updateAdminSuggstnSalesFloorInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 층별정보 삭제
	 * 
	 * @param suggstnSalesFlrSeqno
	 * @param dongSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void deleteAdminSuggstnSalesFloorInfo( long suggstnSalesFlrSeqno, long dongSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesFlrSeqno( suggstnSalesFlrSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.update( "SuggestionSalesMapper.deleteAdminSuggstnSalesFloorInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 프리미엄정보 등록
	 * 
	 * @param suggstnSalesSeqno
	 * @param prmmTtl
	 * @param prmmDscr
	 * @throws Exception
	 */
	public void registerAdminSuggstnSalesPremiumInfo( long suggstnSalesSeqno, String prmmTtl, String prmmDscr )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setPrmmTtl( prmmTtl );
		suggestionSalesVO.setPrmmDscr( prmmDscr );
		
		sqlSession.insert( "SuggestionSalesMapper.registerAdminSuggstnSalesPremiumInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 프리미엄정보 일괄 삭제
	 * 
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void deleteAllAdminSuggstnSalesPremiumInfo( long suggstnSalesSeqno )throws Exception {
		sqlSession.delete( "SuggestionSalesMapper.deleteAllAdminSuggstnSalesPremiumInfo", suggstnSalesSeqno );
	}
	
	/**
	 * 관리자추천분양 일정정보 등록
	 * 
	 * @param suggstnSalesSeqno
	 * @param schdlNm
	 * @param schdlStDate
	 * @param schdlEdDate
	 * @throws Exception
	 */
	public void registerAdminSuggstnSalesScheduleInfo( long suggstnSalesSeqno, String schdlNm, String schdlStDate, String schdlEdDate )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setSchdlNm( schdlNm );
		suggestionSalesVO.setSchdlStDate( schdlStDate );
		suggestionSalesVO.setSchdlEdDate( schdlEdDate );
		
		sqlSession.insert( "SuggestionSalesMapper.registerAdminSuggstnSalesScheduleInfo", suggestionSalesVO );
	}
	
	/**
	 * 관리자추천분양 일정정보 일괄 삭제
	 * 
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void deleteAllAdminSuggstnSalesScheduleInfo( long suggstnSalesSeqno )throws Exception {
		sqlSession.delete( "SuggestionSalesMapper.deleteAllAdminSuggstnSalesScheduleInfo", suggstnSalesSeqno );
	}
	
	/**
	 * 등록단계별임시정보 추천분양시퀀스 등록
	 * 
	 * @param memSeqno
	 * @param regClasCd
	 * @param regTmpKey
	 * @throws Exception
	 */
	public void updateTempDataSuggstnSalesSeqno( String regTmpKey, long suggstnSalesSeqno )throws Exception {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setRegTmpKey( regTmpKey );
		registrationTmpDataStepVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.update( "SuggestionSalesMapper.updateTempDataSuggstnSalesSeqno", registrationTmpDataStepVO );
	}
	
	/**
	 * 추천분양 매물 목록조회 ( 페이징 )
	 * 
	 * @param suggstnSalesRegionGbCd
	 * @param poStatGbCd
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getSuggstnSalesPrptListItems( String suggstnSalesRegionGbCd, String poStatGbCd, String srchVal, int stRnum, int edRnum )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesRegionGbCd( suggstnSalesRegionGbCd );
		suggestionSalesVO.setPoStatGbCd( poStatGbCd );
		suggestionSalesVO.setSrchVal( srchVal );
		suggestionSalesVO.setStRnum( stRnum );
		suggestionSalesVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "SuggestionSalesMapper.getSuggstnSalesPrptListItems", suggestionSalesVO );
	}
	
	/**
	 * Admin - 추천분양 분양상태 업데이트
	 * 
	 * @param suggstnSalesSeqno
	 * @param poStatGbCd
	 * @throws Exception
	 */
	public void updateSuggstnSalesStatItem( long suggstnSalesSeqno, String poStatGbCd )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setPoStatGbCd( poStatGbCd );
		
		sqlSession.update( "SuggestionSalesMapper.updateSuggstnSalesStatItem", suggestionSalesVO );
	}
	
	/**
	 * Admin - 추천분양 삭제
	 * 
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void deleteSuggstnSalesItem( long suggstnSalesSeqno )throws Exception {
		sqlSession.update( "SuggestionSalesMapper.deleteSuggstnSalesItem", suggstnSalesSeqno );
	}
	
	/**
	 * 추천분양 매물 기본정보 조회
	 * 
	 * @param suggstnSalesSeqno
	 * @param regStep
	 * @return
	 * @throws Exception
	 */
	public SuggestionSalesVO getSuggstnSalesPropertyInfo( long memSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		return sqlSession.selectOne( "SuggestionSalesMapper.getSuggstnSalesPropertyInfo", suggestionSalesVO );
	}
	
	/**
	 * 추천분양 매물 동 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getSuggstnSalesDongList( long suggstnSalesSeqno )throws Exception {
		return sqlSession.selectList( "SuggestionSalesMapper.getSuggstnSalesDongList", suggstnSalesSeqno );
	}
	
	/**
	 * 추천분양 매물 동별 용도정보 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @param dongSeqno
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getSuggstnSalesUsageByDongList( long suggstnSalesSeqno, long dongSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		
		return sqlSession.selectList( "SuggestionSalesMapper.getSuggstnSalesUsageByDongList", suggestionSalesVO );
	}
	
	/**
	 * 추천분양 매물 층별정보 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @param dongSeqno
	 * @param usageByDongSeqno
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getSuggstnSalesFloorList( long suggstnSalesSeqno, long dongSeqno, long usageByDongSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		suggestionSalesVO.setUsageByDongSeqno( usageByDongSeqno );
		
		return sqlSession.selectList( "SuggestionSalesMapper.getSuggstnSalesFloorList", suggestionSalesVO );
	}
	
	/**
	 * 추천분양 매물 층 및 층별 용도정보 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @param dongSeqno
	 * @return
	 */
	public List<SuggestionSalesVO> getSuggstnSalesFloorWithUsageListOfDong( long suggstnSalesSeqno, long dongSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setDongSeqno( dongSeqno );
		
		return sqlSession.selectList( "SuggestionSalesMapper.getSuggstnSalesFloorWithUsageListOfDong", suggestionSalesVO );
	}
	
	/**
	 * 교육자료 첨부파일 URL 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getEducationDataAttachmentList( long suggstnSalesSeqno )throws Exception {
		return sqlSession.selectList( "SuggestionSalesMapper.getEducationDataAttachmentList", suggstnSalesSeqno );
	}
	
	/**
	 * 추천분양 프리미엄정보 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getSuggstnSalesPrmmList( long suggstnSalesSeqno )throws Exception {
		return sqlSession.selectList( "SuggestionSalesMapper.getSuggstnSalesPrmmList", suggstnSalesSeqno );
	}
	
	/**
	 * 추천분양 일정 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getSuggstnSalesSchdlList( long suggstnSalesSeqno )throws Exception {
		return sqlSession.selectList( "SuggestionSalesMapper.getSuggstnSalesSchdlList", suggstnSalesSeqno );
	}
	
	/**
	 * 관리자 추천분양 매물정보 수정
	 * 
	 * @param suggstnSalesSeqno
	 * @param suggstnSalesRegionGbCd
	 * @param addr
	 * @param addrShortNm
	 * @param bldNm
	 * @param lat
	 * @param lng
	 * @param suggstnSalesTtl
	 * @param salesDtlDscr
	 * @param minFlr
	 * @param maxFlr
	 * @param totBldCnt
	 * @param parkingCarCnt
	 * @param husHoldCnt
	 * @param lndArea
	 * @param bldArea
	 * @param totFlrArea
	 * @param flrAreaRatio
	 * @param bldToLndRatio
	 * @param cmpltnDate
	 * @param expctMovMonth
	 * @param devCompNm
	 * @param constCompNm
	 * @param matterPortLinkUrl
	 * @param repImgSaveFileNm
	 * @param repImgUrl
	 * @throws Exception
	 */
	public void updateAdminSuggstnSalesProperty( long suggstnSalesSeqno, String suggstnSalesRegionGbCd, String addr, String addrShortNm, String bldNm, double lat, double lng, String suggstnSalesTtl, String salesDtlDscr, int minFlr, int maxFlr, int totBldCnt, int parkingCarCnt, int husHoldCnt,
			double lndArea, double bldArea, double totFlrArea, double flrAreaRatio, double bldToLndRatio, String cmpltnDate, String expctMovMonth, String devCompNm, String constCompNm, String matterPortLinkUrl, String repImgSaveFileNm, String repImgUrl )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setSuggstnSalesRegionGbCd( suggstnSalesRegionGbCd );
		suggestionSalesVO.setAddr( GsntalkXSSUtil.encodeXss( GsntalkUtil.addressTopLvlReplace( addr ) ) );
		suggestionSalesVO.setAddrShortNm( addrShortNm );
		suggestionSalesVO.setBldNm( GsntalkXSSUtil.encodeXss( bldNm ) );
		suggestionSalesVO.setLat( lat );
		suggestionSalesVO.setLng( lng );
		suggestionSalesVO.setSuggstnSalesTtl( GsntalkXSSUtil.encodeXss( suggstnSalesTtl ) );
		suggestionSalesVO.setSalesDtlDscr( GsntalkXSSUtil.encodeXss( salesDtlDscr ) );
		suggestionSalesVO.setMinFlr( minFlr );
		suggestionSalesVO.setMaxFlr( maxFlr );
		suggestionSalesVO.setTotBldCnt( totBldCnt );
		suggestionSalesVO.setParkingCarCnt( parkingCarCnt );
		suggestionSalesVO.setHusHoldCnt( husHoldCnt );
		suggestionSalesVO.setLndArea( lndArea );
		suggestionSalesVO.setBldArea( bldArea );
		suggestionSalesVO.setTotFlrArea( totFlrArea );
		suggestionSalesVO.setFlrAreaRatio( flrAreaRatio );
		suggestionSalesVO.setBldToLndRatio( bldToLndRatio );
		suggestionSalesVO.setCmpltnDate( cmpltnDate );
		suggestionSalesVO.setExpctMovMonth( expctMovMonth );
		suggestionSalesVO.setDevCompNm( GsntalkXSSUtil.encodeXss( devCompNm ) );
		suggestionSalesVO.setConstCompNm( GsntalkXSSUtil.encodeXss( constCompNm ) );
		suggestionSalesVO.setMatterPortLinkUrl( GsntalkXSSUtil.encodeXss( matterPortLinkUrl ) );
		suggestionSalesVO.setRepImgSaveFileNm( repImgSaveFileNm );
		suggestionSalesVO.setRepImgUrl( repImgUrl );
		
		sqlSession.update( "SuggestionSalesMapper.updateAdminSuggstnSalesProperty", suggestionSalesVO );
	}
	
	/**
	 * 교육자료 첨부파일 삭제
	 * 
	 * @param suggstnSalesSeqno
	 * @param fileUrl
	 * @throws Exception
	 */
	public void deleteEducationDataAttachment( long suggstnSalesSeqno, String fileUrl )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		suggestionSalesVO.setFileUrl( fileUrl );
		
		sqlSession.update( "SuggestionSalesMapper.deleteEducationDataAttachment", suggestionSalesVO );
	}
	
	/**
	 * FRT - 실시간 분양현장 목록조회 ( 페이징 )
	 * 
	 * @param memSeqno
	 * @param poStatGbCd
	 * @param suggstnSalesRegionGbCd
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getRealtimeSalesItems( long memSeqno, String poStatGbCd, String suggstnSalesRegionGbCd, int stRnum, int edRnum )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setPoStatGbCd( poStatGbCd );
		suggestionSalesVO.setSuggstnSalesRegionGbCd( suggstnSalesRegionGbCd );
		suggestionSalesVO.setStRnum( stRnum );
		suggestionSalesVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "SuggestionSalesMapper.getRealtimeSalesItems", suggestionSalesVO );
	}
	
	/**
	 * 관심 추천분양 존재여부 검증
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public int isExistsFavSuggstnSales( long memSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		return sqlSession.selectOne( "SuggestionSalesMapper.isExistsFavSuggstnSales", suggestionSalesVO );
	}
	
	/**
	 * 관심 추천분양 등록
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void registerFavSuggstnSales( long memSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.insert( "SuggestionSalesMapper.registerFavSuggstnSales", suggestionSalesVO );
	}
	
	/**
	 * 관심 추천분양 해제
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void releaseFavSuggstnSales( long memSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.delete( "SuggestionSalesMapper.releaseFavSuggstnSales", suggestionSalesVO );
	}
	
	/**
	 * 분양알림 설정여부 확인
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public int getSalesSchdlNotiCnt( long memSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		return sqlSession.selectOne( "SuggestionSalesMapper.getSalesSchdlNotiCnt", suggestionSalesVO );
	}
	
	/**
	 * FRT - 분양알림 설정
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void setSalesSchdlNoti( long memSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.insert( "SuggestionSalesMapper.setSalesSchdlNoti", suggestionSalesVO );
	}
	
	/**
	 * FRT - 분양알림 해제
	 * 
	 * @param memSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void releaseSalesSchdlNoti( long memSeqno, long suggstnSalesSeqno )throws Exception {
		SuggestionSalesVO suggestionSalesVO = new SuggestionSalesVO();
		suggestionSalesVO.setMemSeqno( memSeqno );
		suggestionSalesVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.delete( "SuggestionSalesMapper.releaseSalesSchdlNoti", suggestionSalesVO );
	}
}