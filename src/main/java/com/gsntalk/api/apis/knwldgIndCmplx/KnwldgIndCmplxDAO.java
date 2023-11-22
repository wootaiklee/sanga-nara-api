package com.gsntalk.api.apis.knwldgIndCmplx;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.AttachmentVO;
import com.gsntalk.api.common.vo.KnowledgeIndustryComplexVO;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Repository( "com.gsntalk.api.apis.knwldgIndCmplx.KnwldgIndCmplxDAO" )
public class KnwldgIndCmplxDAO extends CommonDAO {

	public KnwldgIndCmplxDAO() {
		super( KnwldgIndCmplxDAO.class );
	}
	
	/**
	 * 지식산업센터 상세정보 조회
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @return
	 * @throws Exception
	 */
	public KnowledgeIndustryComplexVO getKnwldgIndCmplxDtlItem( long knwldgIndCmplxSeqno )throws Exception {
		return sqlSession.selectOne( "KnwldgIndCmplxMapper.getKnwldgIndCmplxDtlItem", knwldgIndCmplxSeqno );
	}
	
	/**
	 * 지식산업센터 첨부이미지 URL 목록조회
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @param knwldgCmplxAtchImgTypCd
	 * @return
	 * @throws Exception
	 */
	public List<AttachmentVO> getKnwldgIndCmplxImgUrlList( long knwldgIndCmplxSeqno, String knwldgCmplxAtchImgTypCd )throws Exception {
		AttachmentVO attachmentVO = new AttachmentVO();
		attachmentVO.setKnwldgIndCmplxSeqno( knwldgIndCmplxSeqno );
		attachmentVO.setKnwldgCmplxAtchImgTypCd( knwldgCmplxAtchImgTypCd );
		
		return sqlSession.selectList( "KnwldgIndCmplxMapper.getKnwldgIndCmplxImgUrlList", attachmentVO );
	}
	
	/**
	 * 지식산업센터 등록 
	 * 
	 * @param memSeqno
	 * @param prptRegNo
	 * @param addr
	 * @param addrShortNm
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
	 * @return
	 * @throws Exception
	 */
	public long registerKnwldgIndCmplxItem( long memSeqno, String prptRegNo, String addr, String addrShortNm, double lat, double lng, String bldNm, String cmpltnDate, long askSalesMinPrc, long askSalesAvgPrc, long askSalesMaxPrc, long askLeaseMinPrc, long askLeaseAvgPrc, long askLeaseMaxPrc,
			double lndArea, double bldArea, double totFlrArea, int minFlr, int maxFlr, int parkingCarCnt, int husHoldCnt, String devCompNm, String constCompNm, String trfcInfo, String siteExplntn, String smplSmrDscr )throws Exception {
		
		KnowledgeIndustryComplexVO knowldgeIndustryComplexVO = new KnowledgeIndustryComplexVO();
		knowldgeIndustryComplexVO.setMemSeqno( memSeqno );
		knowldgeIndustryComplexVO.setPrptRegNo( prptRegNo );
		knowldgeIndustryComplexVO.setAddr( GsntalkXSSUtil.encodeXss( GsntalkUtil.addressTopLvlReplace( addr ) ) );
		knowldgeIndustryComplexVO.setAddrShortNm( addrShortNm );
		knowldgeIndustryComplexVO.setLat( lat );
		knowldgeIndustryComplexVO.setLng( lng );
		knowldgeIndustryComplexVO.setBldNm( GsntalkXSSUtil.encodeXss( bldNm ) );
		knowldgeIndustryComplexVO.setCmpltnDate( cmpltnDate );
		knowldgeIndustryComplexVO.setAskSalesMinPrc( askSalesMinPrc );
		knowldgeIndustryComplexVO.setAskSalesAvgPrc( askSalesAvgPrc );
		knowldgeIndustryComplexVO.setAskSalesMaxPrc( askSalesMaxPrc );
		knowldgeIndustryComplexVO.setAskLeaseMinPrc( askLeaseMinPrc );
		knowldgeIndustryComplexVO.setAskLeaseAvgPrc( askLeaseAvgPrc );
		knowldgeIndustryComplexVO.setAskLeaseMaxPrc( askLeaseMaxPrc );
		knowldgeIndustryComplexVO.setLndArea( lndArea );
		knowldgeIndustryComplexVO.setBldArea( bldArea );
		knowldgeIndustryComplexVO.setTotFlrArea( totFlrArea );
		knowldgeIndustryComplexVO.setMinFlr( minFlr );
		knowldgeIndustryComplexVO.setMaxFlr( maxFlr );
		knowldgeIndustryComplexVO.setParkingCarCnt( parkingCarCnt );
		knowldgeIndustryComplexVO.setHusHoldCnt( husHoldCnt );
		knowldgeIndustryComplexVO.setDevCompNm( GsntalkXSSUtil.encodeXss( devCompNm ) );
		knowldgeIndustryComplexVO.setConstCompNm( GsntalkXSSUtil.encodeXss( constCompNm ) );
		knowldgeIndustryComplexVO.setTrfcInfo( GsntalkXSSUtil.encodeXss( trfcInfo ) );
		knowldgeIndustryComplexVO.setSiteExplntn( GsntalkXSSUtil.encodeXss( siteExplntn ) );
		knowldgeIndustryComplexVO.setSmplSmrDscr( GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		
		sqlSession.insert( "KnwldgIndCmplxMapper.registerKnwldgIndCmplxItem", knowldgeIndustryComplexVO );
		return knowldgeIndustryComplexVO.getKnwldgIndCmplxSeqno();
	}
	
	/**
	 * 지식산업센터 이미지 등록
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @param knwldgCmplxAtchImgTypCd
	 * @param uploadFileNm
	 * @param saveFileNm
	 * @param fileUrl
	 * @throws Exception
	 */
	public void registerKnwldgIndCmplxImage( long knwldgIndCmplxSeqno, String knwldgCmplxAtchImgTypCd, String uploadFileNm, String saveFileNm, String fileUrl )throws Exception {
		AttachmentVO attachmentVO = new AttachmentVO();
		attachmentVO.setKnwldgIndCmplxSeqno( knwldgIndCmplxSeqno );
		attachmentVO.setKnwldgCmplxAtchImgTypCd( knwldgCmplxAtchImgTypCd );
		attachmentVO.setUploadFileNm( uploadFileNm );
		attachmentVO.setSaveFileNm( saveFileNm );
		attachmentVO.setFileUrl( fileUrl );
		
		sqlSession.insert( "KnwldgIndCmplxMapper.registerKnwldgIndCmplxImage", attachmentVO );
	}
	
	/**
	 * 지식산업센터 목록조회 ( 페이징 )
	 * 
	 * @param regDtSrchTyp
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<KnowledgeIndustryComplexVO> getKnwldgIndCmplxItems( String regDtSrchTyp, String srchVal, int stRnum, int edRnum )throws Exception {
		KnowledgeIndustryComplexVO knowledgeIndustryComplexVO = new KnowledgeIndustryComplexVO();
		knowledgeIndustryComplexVO.setSrchDateType( regDtSrchTyp );
		knowledgeIndustryComplexVO.setSrchVal( srchVal );
		knowledgeIndustryComplexVO.setStRnum( stRnum );
		knowledgeIndustryComplexVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "KnwldgIndCmplxMapper.getKnwldgIndCmplxItems", knowledgeIndustryComplexVO );
	}
	
	/**
	 * 지식산업센터 + 첨부 이미지 삭제
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @throws Exception
	 */
	public void deleteKnwldgIndCmplxItems( long knwldgIndCmplxSeqno )throws Exception {
		sqlSession.update( "KnwldgIndCmplxMapper.deleteKnwldgIndCmplxItems", knwldgIndCmplxSeqno );
	}
	
	/**
	 * 지식산업센터 이미지 - 조감도 삭제
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @throws Exception
	 */
	public void deleteKnwldgIndCmplxVwImage( long knwldgIndCmplxSeqno )throws Exception {
		sqlSession.update( "KnwldgIndCmplxMapper.deleteKnwldgIndCmplxVwImage", knwldgIndCmplxSeqno );
	}
	
	/**
	 * 지식산업센터 이미지 삭제
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @param knwldgCmplxAtchImgTypCd
	 * @param fileUrl
	 * @throws Exception
	 */
	public void deleteKnwldgIndCmplxImage( long knwldgIndCmplxSeqno, String knwldgCmplxAtchImgTypCd, String fileUrl )throws Exception {
		AttachmentVO attachmentVO = new AttachmentVO();
		attachmentVO.setKnwldgIndCmplxSeqno( knwldgIndCmplxSeqno );
		attachmentVO.setKnwldgCmplxAtchImgTypCd( knwldgCmplxAtchImgTypCd );
		attachmentVO.setFileUrl( fileUrl );
		
		sqlSession.update( "KnwldgIndCmplxMapper.deleteKnwldgIndCmplxImage", attachmentVO );
	}
	
	/**
	 * 지식산업센터 수정 
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @param addr
	 * @param addrShortNm
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
	 * @return
	 * @throws Exception
	 */
	public void updateKnwldgIndCmplxItem( long knwldgIndCmplxSeqno, String addr, String addrShortNm, double lat, double lng, String bldNm, String cmpltnDate, long askSalesMinPrc, long askSalesAvgPrc, long askSalesMaxPrc, long askLeaseMinPrc, long askLeaseAvgPrc, long askLeaseMaxPrc,
			double lndArea, double bldArea, double totFlrArea, int minFlr, int maxFlr, int parkingCarCnt, int husHoldCnt, String devCompNm, String constCompNm, String trfcInfo, String siteExplntn, String smplSmrDscr )throws Exception {
		
		KnowledgeIndustryComplexVO knowldgeIndustryComplexVO = new KnowledgeIndustryComplexVO();
		knowldgeIndustryComplexVO.setKnwldgIndCmplxSeqno( knwldgIndCmplxSeqno );
		knowldgeIndustryComplexVO.setAddr( GsntalkXSSUtil.encodeXss( GsntalkUtil.addressTopLvlReplace( addr ) ) );
		knowldgeIndustryComplexVO.setAddrShortNm( addrShortNm );
		knowldgeIndustryComplexVO.setLat( lat );
		knowldgeIndustryComplexVO.setLng( lng );
		knowldgeIndustryComplexVO.setBldNm( GsntalkXSSUtil.encodeXss( bldNm ) );
		knowldgeIndustryComplexVO.setCmpltnDate( cmpltnDate );
		knowldgeIndustryComplexVO.setAskSalesMinPrc( askSalesMinPrc );
		knowldgeIndustryComplexVO.setAskSalesAvgPrc( askSalesAvgPrc );
		knowldgeIndustryComplexVO.setAskSalesMaxPrc( askSalesMaxPrc );
		knowldgeIndustryComplexVO.setAskLeaseMinPrc( askLeaseMinPrc );
		knowldgeIndustryComplexVO.setAskLeaseAvgPrc( askLeaseAvgPrc );
		knowldgeIndustryComplexVO.setAskLeaseMaxPrc( askLeaseMaxPrc );
		knowldgeIndustryComplexVO.setLndArea( lndArea );
		knowldgeIndustryComplexVO.setBldArea( bldArea );
		knowldgeIndustryComplexVO.setTotFlrArea( totFlrArea );
		knowldgeIndustryComplexVO.setMinFlr( minFlr );
		knowldgeIndustryComplexVO.setMaxFlr( maxFlr );
		knowldgeIndustryComplexVO.setParkingCarCnt( parkingCarCnt );
		knowldgeIndustryComplexVO.setHusHoldCnt( husHoldCnt );
		knowldgeIndustryComplexVO.setDevCompNm( GsntalkXSSUtil.encodeXss( devCompNm ) );
		knowldgeIndustryComplexVO.setConstCompNm( GsntalkXSSUtil.encodeXss( constCompNm ) );
		knowldgeIndustryComplexVO.setTrfcInfo( GsntalkXSSUtil.encodeXss( trfcInfo ) );
		knowldgeIndustryComplexVO.setSiteExplntn( GsntalkXSSUtil.encodeXss( siteExplntn ) );
		knowldgeIndustryComplexVO.setSmplSmrDscr( GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		
		sqlSession.insert( "KnwldgIndCmplxMapper.updateKnwldgIndCmplxItem", knowldgeIndustryComplexVO );
	}
}