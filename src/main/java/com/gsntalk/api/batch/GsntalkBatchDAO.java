package com.gsntalk.api.batch;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.BatchVO;
import com.gsntalk.api.common.vo.KakaoMessageVO;
import com.gsntalk.api.common.vo.NotificationVO;
import com.gsntalk.api.common.vo.StandardRegionVO;
import com.gsntalk.api.common.vo.SuggestionSalesVO;

@Repository( "com.gsntalk.api.batch.GsntalkBatchDAO" )
public class GsntalkBatchDAO extends CommonDAO {

	public GsntalkBatchDAO() {
		super( GsntalkBatchDAO.class );
	}
	
	/**
	 * 오래된( 30일 이상 지난 ) 배치 수행이력 데이터 삭제
	 * 
	 * @throws Exception
	 */
	public int clearBatchExecuteOldHistData()throws Exception {
		return sqlSession.delete( "BatchMapper.clearBatchExecuteOldHistData" );
	}
	
	/**
	 * 카카오 메시지 발송결과 갱신대상 기간정보 조회
	 * 
	 * @return
	 * @throws Exception
	 */
	public KakaoMessageVO getKakaoMessageSendRsltDatesInfo()throws Exception {
		return sqlSession.selectOne( "BatchMapper.getKakaoMessageSendRsltDatesInfo" );
	}
	
	/**
	 * 카카오 메시지 발송결과 갱신
	 * 
	 * @param reqId
	 * @param sendRslt
	 * @param resendStatCd
	 * @throws Exception
	 */
	public void updateKakaoSendMessageResult( String reqId, String sendRslt, String resendStatCd )throws Exception {
		KakaoMessageVO kakaoMessageVO = new KakaoMessageVO();
		kakaoMessageVO.setReqId( reqId );
		kakaoMessageVO.setSendRslt( sendRslt );
		kakaoMessageVO.setResendStatCd( resendStatCd );
		
		sqlSession.update( "BatchMapper.updateKakaoSendMessageResult", kakaoMessageVO );
	}
	/**
	 * 기본지역코드 정보 조회
	 * 
	 * @param regionCd
	 * @return
	 * @throws Exception
	 */
	public StandardRegionVO getStandardRegionVO( String regionCd )throws Exception {
		return sqlSession.selectOne( "BatchMapper.getStandardRegionVO", regionCd );
	}
	
	/**
	 * 기본지역코드 신규등록
	 * 
	 * @param regionCd
	 * @param sidoCd
	 * @param sggCd
	 * @param umdCd
	 * @param riCd
	 * @param localAddrNm
	 * @param localLowNm
	 * @param localOrder
	 * @throws Exception
	 */
	public int insertNewStandardRegisionData( String regionCd, String sidoCd, String sggCd, String umdCd, String riCd, String localAddrNm, String localLowNm, int localOrder )throws Exception {
		StandardRegionVO standardRegionVO = new StandardRegionVO();
		standardRegionVO.setRegionCd( regionCd );
		standardRegionVO.setSidoCd( sidoCd );
		standardRegionVO.setSggCd( sggCd );
		standardRegionVO.setUmdCd( umdCd );
		standardRegionVO.setRiCd( riCd );
		standardRegionVO.setLocalAddrNm( localAddrNm );
		standardRegionVO.setLocalLowNm( localLowNm );
		standardRegionVO.setLocalOrder( localOrder );
		
		return sqlSession.insert( "BatchMapper.insertNewStandardRegisionData", standardRegionVO );
	}
	
	/**
	 * 기본지역코드 갱신
	 * 
	 * @param regionCd
	 * @param sidoCd
	 * @param sggCd
	 * @param umdCd
	 * @param riCd
	 * @param localAddrNm
	 * @param localLowNm
	 * @param localOrder
	 * @throws Exception
	 */
	public int updateStandardRegisionData( String regionCd, String sidoCd, String sggCd, String umdCd, String riCd, String localAddrNm, String localLowNm, int localOrder )throws Exception {
		StandardRegionVO standardRegionVO = new StandardRegionVO();
		standardRegionVO.setRegionCd( regionCd );
		standardRegionVO.setSidoCd( sidoCd );
		standardRegionVO.setSggCd( sggCd );
		standardRegionVO.setUmdCd( umdCd );
		standardRegionVO.setRiCd( riCd );
		standardRegionVO.setLocalAddrNm( localAddrNm );
		standardRegionVO.setLocalLowNm( localLowNm );
		standardRegionVO.setLocalOrder( localOrder );
		
		return sqlSession.update( "BatchMapper.updateStandardRegisionData", standardRegionVO );
	}
	
	/**
	 * 배치 수행이력 적재
	 * 
	 * @param batchNm
	 * @param succsYn
	 * @param execRslt
	 * @throws Exception
	 */
	public void registerBatchExecuteHist( String batchNm, String succsYn, String execRslt )throws Exception {
		BatchVO batchVO = new BatchVO();
		batchVO.setBatchNm( batchNm );
		batchVO.setSuccsYn( succsYn );
		batchVO.setExecRslt( execRslt );
		
		sqlSession.insert( "BatchMapper.registerBatchExecuteHist", batchVO );
	}
	
	/**
	 * 당일 공지알림 발송대상 목록조회
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<NotificationVO> getTodayPublicNotificationList()throws Exception {
		return sqlSession.selectList( "BatchMapper.getTodayPublicNotificationList" );
	}
	
	/**
	 * 공지알림 일괄 적재
	 * 
	 * @param notiTypGbCd
	 * @param notiTtl
	 * @param notiDscr
	 * @throws Exception
	 */
	public void registerPublicNotification( String notiTypGbCd, String notiTtl, String notiDscr )throws Exception {
		NotificationVO notificationVO = new NotificationVO();
		notificationVO.setNotiTypGbCd( notiTypGbCd );
		notificationVO.setNotiTtl( notiTtl );
		notificationVO.setNotiDscr( notiDscr );
		
		sqlSession.insert( "BatchMapper.registerPublicNotification", notificationVO );
	}
	
	/**
	 * 공지알림 상태 갱신
	 * 
	 * @param publicNotiSeqno
	 * @param rcpntCnt
	 * @throws Exception
	 */
	public void updatePublicNotificationState( long publicNotiSeqno, int rcpntCnt )throws Exception {
		NotificationVO notificationVO = new NotificationVO();
		notificationVO.setPublicNotiSeqno( publicNotiSeqno );
		notificationVO.setRcpntCnt( rcpntCnt );
		
		sqlSession.update( "BatchMapper.updatePublicNotificationState", notificationVO );
	}
	
	/**
	 * N일 후 분양일정 목록조회
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SuggestionSalesVO> getAfterNDaySalesSchdlList( int addDays )throws Exception {
		return sqlSession.selectList( "BatchMapper.getAfterNDaySalesSchdlList", addDays );
	}
	
	/**
	 * 분양일정 발송대상 회원 시퀀스 목록조회
	 * 
	 * @param suggstnSalesSeqno
	 * @return
	 * @throws Exception
	 */
	public List<Long> getSalesSchdlNotiTargetMemberList( long suggstnSalesSeqno )throws Exception {
		return sqlSession.selectList( "BatchMapper.getSalesSchdlNotiTargetMemberList", suggstnSalesSeqno );
	}
}