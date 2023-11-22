package com.gsntalk.api.apis.gsntalk;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.CommonCodeVO;
import com.gsntalk.api.common.vo.EstateBrokerOfficeVO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.common.vo.NotificationVO;
import com.gsntalk.api.common.vo.RegistrationTmpDataStepVO;
import com.gsntalk.api.common.vo.StandardRegionVO;
import com.gsntalk.api.config.GsntalkConstants;

@Repository( "com.gsntalk.api.apis.gsntalk.GsntalkDAO" )
public class GsntalkDAO extends CommonDAO {

	public GsntalkDAO() {
		super( GsntalkDAO.class );
	}
	
	/**
	 * 공통코드 목록조회
	 * 
	 * @param comnCd
	 * @return
	 * @throws Excpetion
	 */
	public List<CommonCodeVO> getComnCdList( String comnCd )throws Exception {
		return sqlSession.selectList( "GsntalkMapper.getComnCdList", comnCd );
	}
	
	/**
	 * 단일 공통코드 목록조회 ( 상/하위코드 목록조회 )
	 * 
	 * @param comnCd
	 * @return
	 * @throws Excpetion
	 */
	public List<CommonCodeVO> getSingleComnCdItems( String upItemCd, String itemCd )throws Exception {
		CommonCodeVO commonCodeVO = new CommonCodeVO();
		commonCodeVO.setUpItemCd( upItemCd );
		commonCodeVO.setItemCd( itemCd );
		
		return sqlSession.selectList( "GsntalkMapper.getSingleComnCdItems", commonCodeVO );
	}
	
	/**
	 * 중개사 사무소 검색
	 * 
	 * @param srchVal
	 * @param lastRnum
	 * @return
	 * @throws Exception
	 */
	public List<EstateBrokerOfficeVO> srchEstBlkOfcItems(  String srchVal, int lastRnum  )throws Exception {
		EstateBrokerOfficeVO estateBrokerOfficeVO = new EstateBrokerOfficeVO();
		estateBrokerOfficeVO.setSrchVal( srchVal );
		estateBrokerOfficeVO.setRownum( lastRnum );
		estateBrokerOfficeVO.setCntPerPage( GsntalkConstants.COUNT_PER_PAGE_DEFAULT );
		
		return sqlSession.selectList( "GsntalkMapper.srchEstBlkOfcItems", estateBrokerOfficeVO );
	}
	
	/**
	 * 등록단계별 임시저장 JSON 조회
	 * 
	 * @param memSeqno
	 * @param regClasCd
	 * @param regTmpKey
	 * @param regStep
	 * @return
	 * @throws Exception
	 */
	public String getTempdataOfRegistrationStepJson( long memSeqno, String regClasCd, String regTmpKey, int regStep )throws Exception {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setMemSeqno( memSeqno );
		registrationTmpDataStepVO.setRegClasCd( regClasCd );
		registrationTmpDataStepVO.setRegTmpKey( regTmpKey );
		registrationTmpDataStepVO.setRegStep( regStep );
		
		return sqlSession.selectOne( "GsntalkMapper.getTempdataOfRegistrationStepJson", registrationTmpDataStepVO );
	}
	
	/**
	 * 등록단계별 임시저장 JSON 조회 from prptSeqno
	 * 
	 * @param memSeqno
	 * @param regClasCd
	 * @param prptSeqno
	 * @param regStep
	 * @return
	 * @throws Exception
	 */
	public RegistrationTmpDataStepVO getTempdataOfRegistrationStepJsonFromPrptSeqno( long memSeqno, String regClasCd, long prptSeqno, int regStep )throws Exception {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setMemSeqno( memSeqno );
		registrationTmpDataStepVO.setRegClasCd( regClasCd );
		registrationTmpDataStepVO.setPrptSeqno( prptSeqno );
		registrationTmpDataStepVO.setRegStep( regStep );
		
		return sqlSession.selectOne( "GsntalkMapper.getTempdataOfRegistrationStepJsonFromPrptSeqno", registrationTmpDataStepVO );
	}
	
	/**
	 * 등록단계별 임시정보 데이터 등록
	 * 
	 * @param memSeqno
	 * @param regClasCd
	 * @param regTmpKey
	 * @param regStep
	 * @param tmpJsonData
	 * @param prptSeqno
	 * @param suggstnSalesSeqno
	 * @param prptSuggstReqSeqno
	 * @param movPrpslPrptSeqno
	 * @param assetSeqno
	 * @throws Exception
	 */
	public void registrationTempDataStep( long memSeqno, String regClasCd, String regTmpKey, int regStep, String tmpJsonData, long prptSeqno, long suggstnSalesSeqno, long prptSuggstReqSeqno, long movPrpslPrptSeqno, long assetSeqno )throws Exception {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setMemSeqno( memSeqno );
		registrationTmpDataStepVO.setRegClasCd( regClasCd );
		registrationTmpDataStepVO.setRegTmpKey( regTmpKey );
		registrationTmpDataStepVO.setRegStep( regStep );
		registrationTmpDataStepVO.setTmpJsonData( tmpJsonData );
		registrationTmpDataStepVO.setPrptSeqno( prptSeqno );
		registrationTmpDataStepVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		registrationTmpDataStepVO.setPrptSuggstReqSeqno( prptSuggstReqSeqno );
		registrationTmpDataStepVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		registrationTmpDataStepVO.setAssetSeqno( assetSeqno );
		
		sqlSession.insert( "GsntalkMapper.registrationTempDataStep", registrationTmpDataStepVO );
	}
	
	/**
	 * 등록단계별 임시정보 데이터 업데이트
	 * 
	 * @param memSeqno
	 * @param regClasCd
	 * @param regTmpKey
	 * @param regStep
	 * @param tmpJsonData
	 * @throws Exception
	 */
	public void updateTempDataStep( long memSeqno, String regClasCd, String regTmpKey, int regStep, String tmpJsonData )throws Exception {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setMemSeqno( memSeqno );
		registrationTmpDataStepVO.setRegClasCd( regClasCd );
		registrationTmpDataStepVO.setRegTmpKey( regTmpKey );
		registrationTmpDataStepVO.setRegStep( regStep );
		registrationTmpDataStepVO.setTmpJsonData( tmpJsonData );
		
		sqlSession.insert( "GsntalkMapper.updateTempDataStep", registrationTmpDataStepVO );
	}
	
	/**
	 * 등록단계별임시정보 매물시퀀스 등록
	 * 
	 * @param memSeqno
	 * @param regClasCd
	 * @param regTmpKey
	 * @throws Exception
	 */
	public void updateTempDataPrptSeqno( long memSeqno, String regClasCd, String regTmpKey, long prptSeqno )throws Exception {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setMemSeqno( memSeqno );
		registrationTmpDataStepVO.setRegClasCd( regClasCd );
		registrationTmpDataStepVO.setRegTmpKey( regTmpKey );
		registrationTmpDataStepVO.setPrptSeqno( prptSeqno );
		
		sqlSession.update( "GsntalkMapper.updateTempDataPrptSeqno", registrationTmpDataStepVO );
	}
	
	/**
	 * 공통코드 존재여부 검증 ( 삭제된 코드도 포함하여 검증 )
	 * 
	 * @param comnCd
	 * @param itemCd
	 * @return
	 * @throws Exception
	 */
	public int isInCommonCdWholeItems( String comnCd, String itemCd, String upItemCd )throws Exception {
		CommonCodeVO commonCodeVO = new CommonCodeVO();
		commonCodeVO.setComnCd( comnCd );
		commonCodeVO.setItemCd( itemCd );
		commonCodeVO.setUpItemCd( upItemCd );
		
		return sqlSession.selectOne( "GsntalkMapper.isInCommonCdWholeItems", commonCodeVO );
	}
	
	/**
	 * 공통코드 존재여부 검증 ( 삭제된 코드는 제외하고 검증 )
	 * 
	 * @param comnCd
	 * @param itemCd
	 * @return
	 * @throws Exception
	 */
	public int isInCommonCdUndeletedItems( String comnCd, String itemCd, String upItemCd )throws Exception {
		CommonCodeVO commonCodeVO = new CommonCodeVO();
		commonCodeVO.setComnCd( comnCd );
		commonCodeVO.setItemCd( itemCd );
		commonCodeVO.setUpItemCd( upItemCd );
		
		return sqlSession.selectOne( "GsntalkMapper.isInCommonCdUndeletedItems", commonCodeVO );
	}
	
	/**
	 * 기본 지역 및 지역코드 조회
	 * 
	 * @param sidoCd
	 * @param sggCd
	 * @param umdCd
	 * @return
	 * @throws Exception
	 */
	public List<StandardRegionVO> searchStandardRegionList( String sidoCd, String sggCd, String umdCd )throws Exception {
		StandardRegionVO standardRegionVO = new StandardRegionVO();
		standardRegionVO.setSidoCd( sidoCd );
		standardRegionVO.setSggCd( sggCd );
		standardRegionVO.setUmdCd( umdCd );
		
		return sqlSession.selectList( "GsntalkMapper.searchStandardRegionList", standardRegionVO );
	}
	
	/**
	 * 기본지역 주소 명칭 검색
	 * 
	 * @param sidoCd
	 * @param sggCd
	 * @param umdCd
	 * @return
	 * @throws Exception
	 */
	public List<StandardRegionVO> searchStandardRegionAddrNmList( String srchVal, String matchTag )throws Exception {
		StandardRegionVO standardRegionVO = new StandardRegionVO();
		standardRegionVO.setSrchVal( srchVal );
		standardRegionVO.setMatchTag( matchTag );
		
		return sqlSession.selectList( "GsntalkMapper.searchStandardRegionAddrNmList", standardRegionVO );
	}
	
	/**
	 * 매물 등록단계별 1단계 임시등록 키 조회
	 * 
	 * @param prptSeqno
	 * @return
	 */
	public String getPropertyTempdataOfRegistrationKey( long memSeqno, String regClasCd, long prptSeqno ) {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setMemSeqno( memSeqno );
		registrationTmpDataStepVO.setRegClasCd( regClasCd );
		registrationTmpDataStepVO.setPrptSeqno( prptSeqno );
		
		return sqlSession.selectOne( "GsntalkMapper.getPropertyTempdataOfRegistrationKey", registrationTmpDataStepVO );
	}
	
	/**
	 * 서비스이용약관 동의여부 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public MemberVO checkForServiceTermsAgreeItem( long memSeqno )throws Exception {
		return sqlSession.selectOne( "GsntalkMapper.checkForServiceTermsAgreeItem", memSeqno );
	}
	
	/**
	 * 서울/인천/경기지역 주소 명칭 검색 ( 시도->시군구 단위명칭 까지 )
	 * 
	 * @param srchVal
	 * @return
	 * @throws Exception
	 */
	public List<StandardRegionVO> searchMetroRegionAddrNmItems( String srchVal )throws Exception {
		return sqlSession.selectList( "GsntalkMapper.searchMetroRegionAddrNmItems", srchVal );
	}
	
	/**
	 * 알림 등록
	 * 
	 * @param receipentMemSeqno
	 * @param notiGbCd
	 * @param notiTypGbCd
	 * @param notiTtl
	 * @param notiDscr
	 * @param prptSeqno
	 * @param suggstnSalesSeqno
	 * @throws Exception
	 */
	public void registerNotification( long receipentMemSeqno, String notiGbCd, String notiTypGbCd, String notiTtl, String notiDscr, long prptSeqno, long suggstnSalesSeqno )throws Exception {
		NotificationVO notificationVO = new NotificationVO();
		notificationVO.setMemSeqno( receipentMemSeqno );
		notificationVO.setNotiGbCd( notiGbCd );
		notificationVO.setNotiTypGbCd( notiTypGbCd );
		notificationVO.setNotiTtl( notiTtl );
		notificationVO.setNotiDscr( notiDscr );
		notificationVO.setPrptSeqno( prptSeqno );
		notificationVO.setSuggstnSalesSeqno( suggstnSalesSeqno );
		
		sqlSession.insert( "GsntalkMapper.registerNotification", notificationVO );
	}
	
	/**
	 * 공통코드명 조회
	 * 
	 * @param comnCd
	 * @param upItemCd
	 * @param itemCd
	 * @return
	 * @throws Exception
	 */
	public String getCommonCodeNm( String comnCd, String upItemCd, String itemCd )throws Exception {
		CommonCodeVO commonCodeVO = new CommonCodeVO();
		commonCodeVO.setComnCd( comnCd );
		commonCodeVO.setUpItemCd( upItemCd );
		commonCodeVO.setItemCd( itemCd );
		
		return sqlSession.selectOne( "GsntalkMapper.getCommonCodeNm", commonCodeVO );
	}
}