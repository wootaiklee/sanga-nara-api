package com.gsntalk.api.batch;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsntalk.api.apis.gsntalk.GsntalkDAO;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.KakaoMessageVO;
import com.gsntalk.api.common.vo.NotificationVO;
import com.gsntalk.api.common.vo.StandardRegionVO;
import com.gsntalk.api.common.vo.SuggestionSalesVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkUtil;

@Service( "GsntalkBatchService" )
public class GsntalkBatchService extends CommonService {
	
	@Value("${profile}")
	private String PROFILE;
	
	private String[] API_GOV_DATA_POTAL_STANDARD_REGION_LOCAL_LIST = new String[] {
			"서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "경기", "강원",
			"충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주"
	};
	
	@Autowired
	private GsntalkDAO gsntalkDAO;
	
	@Autowired
	private GsntalkBatchDAO gsntalkBatchDAO;
	
	public GsntalkBatchService() {
		super( GsntalkBatchService.class );
	}
	
	/**
	 * 오래된( 30일 이상 지난 ) 배치 수행이력 데이터 삭제
	 * 
	 * ※ 일 1회 | 매일 0시 0분 1초에 수행
	 * 
	 * @throws Exception
	 */
	@Scheduled( cron = "1 0 0 * * *" )
	public void clearBatchExecuteOldHistData()throws Exception {
		// 로컬에서는 수행안함.
		if( !"prd".equals( this.PROFILE ) ) {
			return;
		}
		
		int delCnt = 0;
		try {
			delCnt = gsntalkBatchDAO.clearBatchExecuteOldHistData();
			
			gsntalkBatchDAO.registerBatchExecuteHist( "오래된 배치 수행이력 데이터 삭제", GsntalkConstants.YES, "delete " + GsntalkUtil.set1000Comma( delCnt ) + " record(s)"  );
		}catch( Exception e ) {
			gsntalkBatchDAO.registerBatchExecuteHist( "오래된 배치 수행이력 데이터 삭제", GsntalkConstants.NO, GsntalkUtil.getMaximumErrorMessage( e, 1000 ) );
		}
		
	}
	
	
	/**
	 * 카카오 메시지 발송결과 갱신
	 * 
	 * ※ 10분마다 1회 | 매 10분마다 05초에 수행
	 * 
	 * @throws Exception
	 */
	@Scheduled( cron = "5 0/10 * * * *" )
	@Transactional( rollbackFor = Exception.class )
	public void kakaoMessageSendResultUpdate()throws Exception {
		// 로컬에서는 수행안함.
		if( !"prd".equals( this.PROFILE ) ) {
			return;
		}
		
		try {
			// 카카오 메시지 발송결과 갱신대상 기간정보 조회
			KakaoMessageVO kakaoMessageVO = gsntalkBatchDAO.getKakaoMessageSendRsltDatesInfo();
			if( kakaoMessageVO == null || GsntalkUtil.isEmpty( kakaoMessageVO.getSrchStDttm() ) || GsntalkUtil.isEmpty( kakaoMessageVO.getSrchEdDttm() ) ) {
				gsntalkBatchDAO.registerBatchExecuteHist( "카카오 메시지 발송결과 갱신", GsntalkConstants.YES, "갱신데이터 없음" );
				return;
			}
			
			JSONArray sendRsltItems = gsntalkIFUtil.getKakaoMessageSendResultItems( kakaoMessageVO.getSrchStDttm(), kakaoMessageVO.getSrchEdDttm() );
			JSONObject sendRsltItem = null;
			String reqId = null;
			String sendRslt = null;
			String resendStatCd = null;
			
			for( int i = 0; i < sendRsltItems.size(); i ++ ) {
				sendRsltItem = (JSONObject)sendRsltItems.get( i );
				reqId = GsntalkUtil.getString( sendRsltItem.get( "requestId" ) );
				sendRslt = GsntalkUtil.getString( sendRsltItem.get( "resultCode" ) ) + " : " + GsntalkUtil.getString( sendRsltItem.get( "resultCodeName" ) );
				resendStatCd = GsntalkUtil.getString( sendRsltItem.get( "resendStatus" ) );
				
				// 카카오 메시지 발송결과 갱신
				gsntalkBatchDAO.updateKakaoSendMessageResult( reqId, sendRslt, resendStatCd );
			}
			
			gsntalkBatchDAO.registerBatchExecuteHist( "카카오 메시지 발송결과 갱신", GsntalkConstants.YES, "SUCCESS" );
		}catch( Exception e ) {
			gsntalkBatchDAO.registerBatchExecuteHist( "카카오 메시지 발송결과 갱신", GsntalkConstants.NO, GsntalkUtil.getMaximumErrorMessage( e, 1000 ) );
		}
	}
	
	/**
	 * 행정안전부_행정표준코드_법정동코드 조회
	 * 
	 * ※ 주 1회 | 매 주 월요일 03시 15분 10초에 수행
	 * 
	 * @throws Exception
	 */
	@Scheduled( cron = "10 15 3 ? * MON" )
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void updateStandardRegionData()throws Exception {
		// 로컬에서는 수행안함.
		if( !"prd".equals( this.PROFILE ) ) {
			return;
		}
		
		try {
			JSONObject standardRegionItem = null;
			JSONArray regionItems = null;
			JSONObject regionItem = null;
			JSONObject alreadyExistsItem = null;
			StandardRegionVO vo = null;
			
			String regionCd;
			String sidoCd;
			String sggCd;
			String umdCd;
			String riCd;
			String localAddrNm;
			String localLowNm;
			int localOrder;
			
			int pageNo = 1;
			int searchedCnt = 0;
			int totalCount = 0;
			
			long t = System.currentTimeMillis();
			long s = 0L;
			
			int allTotCount = 0;
			int allSearchedCount = 0;
			
			int x = 0;
			int allInsertCnt = 0;
			int allUpdateCnt = 0;
			int allPassedCnt = 0;
			
			this.LOGGER.info( "START updateStandardRegionData" );
			for( String searchLocalNm : this.API_GOV_DATA_POTAL_STANDARD_REGION_LOCAL_LIST ) {
				pageNo = 1;
				regionItems = null;
				totalCount = 0;
				searchedCnt = 0;
				x = 0;
				
				
				standardRegionItem = gsntalkIFUtil.getStandardRegionData( searchLocalNm, pageNo, 0, null );
				
				totalCount = GsntalkUtil.getInteger( standardRegionItem.get( "totalCount" ) );
				if( totalCount == 0 ) {
					continue;
				}
				s = System.currentTimeMillis();
				this.LOGGER.info( "[ " + searchLocalNm + " ] START." );
				
				regionItems = GsntalkUtil.getJSONArray( standardRegionItem, "regionItems" );
				allSearchedCount += regionItems.size();
				searchedCnt = regionItems.size();
				
				// insert or update standard region data
				for( int i = 0; i < regionItems.size(); i ++ ) {
					x++;
					regionItem = (JSONObject)regionItems.get( i );
					
					regionCd = GsntalkUtil.getString( regionItem.get( "region_cd" ) );
					sidoCd = GsntalkUtil.getString( regionItem.get( "sido_cd" ) );
					sggCd = GsntalkUtil.getString( regionItem.get( "sgg_cd" ) );
					umdCd = GsntalkUtil.getString( regionItem.get( "umd_cd" ) );
					riCd = GsntalkUtil.getString( regionItem.get( "ri_cd" ) );
					localAddrNm = GsntalkUtil.getString( regionItem.get( "locatadd_nm" ) );
					localLowNm = GsntalkUtil.getString( regionItem.get( "locallow_nm" ) );
					localOrder = GsntalkUtil.getInteger( regionItem.get( "locat_order" ) );
					
					vo = gsntalkBatchDAO.getStandardRegionVO( regionCd );
					
					if( vo == null ) {
						// 기본지역코드 신규등록
						allInsertCnt += gsntalkBatchDAO.insertNewStandardRegisionData( regionCd, sidoCd, sggCd, umdCd, riCd, localAddrNm, localLowNm, localOrder );
						
					}else {
						if(
								regionCd.equals( vo.getRegionCd() )
								&&
								sidoCd.equals( vo.getSidoCd() )
								&&
								sggCd.equals( vo.getSggCd() )
								&&
								umdCd.equals( vo.getUmdCd() )
								&&
								riCd.equals( vo.getRiCd() )
								&&
								localAddrNm.equals( vo.getLocalAddrNm() )
								&&
								localLowNm.equals( vo.getLocalLowNm() )
								&&
								localOrder == vo.getLocalOrder()
						) {
							// already exists same data - just passing
							allPassedCnt ++;
							
						}else {
							// 기본지역코드 갱신
							allUpdateCnt += gsntalkBatchDAO.updateStandardRegisionData( regionCd, sidoCd, sggCd, umdCd, riCd, localAddrNm, localLowNm, localOrder );
							
							this.LOGGER.info( "*****************************************************************************************************************");
							alreadyExistsItem = new JSONObject();
							alreadyExistsItem.put( "region_cd", vo.getRegionCd() );
							alreadyExistsItem.put( "sido_cd", vo.getSidoCd() );
							alreadyExistsItem.put( "sgg_cd", vo.getSggCd() );
							alreadyExistsItem.put( "umd_cd", vo.getUmdCd() );
							alreadyExistsItem.put( "ri_cd", vo.getRiCd() );
							alreadyExistsItem.put( "locatadd_nm", vo.getLocalAddrNm() );
							alreadyExistsItem.put( "locallow_nm", vo.getLocalLowNm() );
							alreadyExistsItem.put( "locat_order", vo.getLocalOrder() );
							
							this.LOGGER.info( "preExistsItem : " + alreadyExistsItem.toJSONString() );
							this.LOGGER.info( "afterItem : " + regionItem.toJSONString() );
							
							this.LOGGER.info( "*****************************************************************************************************************");
						}
					}
					
					if( x % 100 == 0 ) {
						this.LOGGER.info( "... " + GsntalkUtil.set1000Comma( x ) + " / " + GsntalkUtil.set1000Comma( totalCount ) );
					}
				}
				
				while( totalCount > searchedCnt ) {
					pageNo = pageNo + 1;
					
					standardRegionItem = gsntalkIFUtil.getStandardRegionData( searchLocalNm, pageNo, 0, null );
					
					totalCount = GsntalkUtil.getInteger( standardRegionItem.get( "totalCount" ) );
					if( totalCount == 0 ) {
						continue;
					}
					
					regionItems = GsntalkUtil.getJSONArray( standardRegionItem, "regionItems" );
					allSearchedCount += regionItems.size();
					searchedCnt = searchedCnt += regionItems.size();
					
					// insert or update standard region data
					for( int i = 0; i < regionItems.size(); i ++ ) {
						x++;
						regionItem = (JSONObject)regionItems.get( i );
						
						regionCd = GsntalkUtil.getString( regionItem.get( "region_cd" ) );
						sidoCd = GsntalkUtil.getString( regionItem.get( "sido_cd" ) );
						sggCd = GsntalkUtil.getString( regionItem.get( "sgg_cd" ) );
						umdCd = GsntalkUtil.getString( regionItem.get( "umd_cd" ) );
						riCd = GsntalkUtil.getString( regionItem.get( "ri_cd" ) );
						localAddrNm = GsntalkUtil.getString( regionItem.get( "locatadd_nm" ) );
						localLowNm = GsntalkUtil.getString( regionItem.get( "locallow_nm" ) );
						localOrder = GsntalkUtil.getInteger( regionItem.get( "locat_order" ) );
						
						vo = gsntalkBatchDAO.getStandardRegionVO( regionCd );
						
						if( vo == null ) {
							// 기본지역코드 신규등록
							allInsertCnt += gsntalkBatchDAO.insertNewStandardRegisionData( regionCd, sidoCd, sggCd, umdCd, riCd, localAddrNm, localLowNm, localOrder );
							
						}else {
							if(
									regionCd.equals( vo.getRegionCd() )
									&&
									sidoCd.equals( vo.getSidoCd() )
									&&
									sggCd.equals( vo.getSggCd() )
									&&
									umdCd.equals( vo.getUmdCd() )
									&&
									riCd.equals( vo.getRiCd() )
									&&
									localAddrNm.equals( vo.getLocalAddrNm() )
									&&
									localLowNm.equals( vo.getLocalLowNm() )
									&&
									localOrder == vo.getLocalOrder()
							) {
								// already exists same data - just passing
								allPassedCnt ++;
								
							}else {
								// 기본지역코드 갱신
								allUpdateCnt += gsntalkBatchDAO.updateStandardRegisionData( regionCd, sidoCd, sggCd, umdCd, riCd, localAddrNm, localLowNm, localOrder );
							}
						}
						
						if( x % 100 == 0 ) {
							this.LOGGER.info( "... " + GsntalkUtil.set1000Comma( x ) + " / " + GsntalkUtil.set1000Comma( totalCount ) );
						}
					}
				}
				
				allTotCount += totalCount;
				
				this.LOGGER.info( "[ " + searchLocalNm + " ] all " + GsntalkUtil.set1000Comma( totalCount ) + " records FIN --> " + GsntalkUtil.set1000Comma( System.currentTimeMillis() - s ) + "ms." );
				this.LOGGER.info( "================================================================================================================" );
			}
			
			this.LOGGER.info( "FIN updateStandardRegionData all processes done. [ " + GsntalkUtil.set1000Comma( allInsertCnt ) + " inserted, " + GsntalkUtil.set1000Comma( allUpdateCnt ) + " updated, " + GsntalkUtil.set1000Comma( allPassedCnt ) + " passed ] [ " + GsntalkUtil.set1000Comma( allSearchedCount ) + " / " + GsntalkUtil.set1000Comma( allTotCount ) + " ] " + GsntalkUtil.set1000Comma( System.currentTimeMillis() - t ) + "ms." );
			gsntalkBatchDAO.registerBatchExecuteHist( "행정안전부_행정표준코드_법정동코드 조회", GsntalkConstants.YES, "FIN updateStandardRegionData all processes done. [ " + GsntalkUtil.set1000Comma( allInsertCnt ) + " inserted, " + GsntalkUtil.set1000Comma( allUpdateCnt ) + " updated, " + GsntalkUtil.set1000Comma( allPassedCnt ) + " passed ] [ " + GsntalkUtil.set1000Comma( allSearchedCount ) + " / " + GsntalkUtil.set1000Comma( allTotCount ) + " ] " + GsntalkUtil.set1000Comma( System.currentTimeMillis() - t ) + "ms." );
		}catch( Exception e ) {
			gsntalkBatchDAO.registerBatchExecuteHist( "행정안전부_행정표준코드_법정동코드 조회", GsntalkConstants.NO, GsntalkUtil.getMaximumErrorMessage( e, 1000 ) );
		}
	}
	
	/**
	 * 공지 알림 적재(전송)
	 * 
	 * @throws Exception
	 */
	@Scheduled( cron = "0 0 9 * * *" )
	@Transactional( rollbackFor = Exception.class )
	public void broadcastPublicNotificatioin()throws Exception {
		// 로컬에서는 수행안함.
		if( !"prd".equals( this.PROFILE ) ) {
			return;
		}
		
		// 당일 공지알림 발송대상 목록조회
		List<NotificationVO> notificationList = gsntalkBatchDAO.getTodayPublicNotificationList();
		if( GsntalkUtil.isEmptyList( notificationList ) ) {
			notificationList = new ArrayList<NotificationVO>();
		}
		
		String notiTtl = null;
		for( NotificationVO vo : notificationList ) {
			notiTtl = "E".equals( vo.getNotiTypGbCd() ) ? "이벤트 안내" : "U".equals( vo.getNotiTypGbCd() ) ? "업데이트 안내" : "서비스 안내";
			
			// 공지알림 일괄 적재
			gsntalkBatchDAO.registerPublicNotification( vo.getNotiTypGbCd(), notiTtl, vo.getNotiDscr() );
			
			// 공지알림 상태 갱신
			gsntalkBatchDAO.updatePublicNotificationState( vo.getPublicNotiSeqno(), vo.getRcpntCnt() );
		}
		
		this.LOGGER.info( "@@ GsntalkBatchService =====>> 공지 알림 적재(전송) - 공지 : " + notificationList.size() + " 건." );
	}
	
	/**
	 * 분양일정 알림 적재(전송)
	 * 
	 * @throws Exception
	 */
	@Scheduled( cron = "2 0 9 * * *" )
	@Transactional( rollbackFor = Exception.class )
	public void broadcastSalesSchdlNotificatioin()throws Exception {
		// 로컬에서는 수행안함.
		if( !"prd".equals( this.PROFILE ) ) {
			return;
		}
		
		// 일주일 후 분양일정 목록조회
		List<SuggestionSalesVO> lessWeekSalesSchdlList = gsntalkBatchDAO.getAfterNDaySalesSchdlList( 7 );
		if( GsntalkUtil.isEmptyList( lessWeekSalesSchdlList ) ) {
			lessWeekSalesSchdlList = new ArrayList<SuggestionSalesVO>();
		}
		
		// 다음날 분양일정 목록조회
		List<SuggestionSalesVO> tomorrowSalesSchdlList = gsntalkBatchDAO.getAfterNDaySalesSchdlList( 1 );
		if( GsntalkUtil.isEmptyList( tomorrowSalesSchdlList ) ) {
			tomorrowSalesSchdlList = new ArrayList<SuggestionSalesVO>();
		}
		
		// 당일 분양일정 목록조회
		List<SuggestionSalesVO> todaySalesSchdlList = gsntalkBatchDAO.getAfterNDaySalesSchdlList( 0 );
		if( GsntalkUtil.isEmptyList( todaySalesSchdlList ) ) {
			todaySalesSchdlList = new ArrayList<SuggestionSalesVO>();
		}
		
		List<Long> targetMemberList = null;
		
		// 일주일 후 분양일정 알림 발송
		for( SuggestionSalesVO vo : lessWeekSalesSchdlList ) {
			// 분양일정 발송대상 회원 목록조회
			targetMemberList = gsntalkBatchDAO.getSalesSchdlNotiTargetMemberList( vo.getSuggstnSalesSeqno() );
			if( GsntalkUtil.isEmptyList( targetMemberList ) ) {
				targetMemberList = new ArrayList<Long>();
			}
			
			for( Long memSeqno : targetMemberList ) {
				// 알림 등록
				gsntalkDAO.registerNotification( memSeqno, "SALES", null, vo.getSchdlNm() + " D-7", vo.getBldNm(), 0L, vo.getSuggstnSalesSeqno() );
			}
		}
		
		this.LOGGER.info( "@@ GsntalkBatchService =====>> 분양일정 알림 적재(전송) - 일주일 후 분양일정 알림 발송 - 대상 일정 : " + lessWeekSalesSchdlList.size() + " 건." );
		
		// 다음날 분양일정 알림 발송
		for( SuggestionSalesVO vo : tomorrowSalesSchdlList ) {
			// 분양일정 발송대상 회원 목록조회
			targetMemberList = gsntalkBatchDAO.getSalesSchdlNotiTargetMemberList( vo.getSuggstnSalesSeqno() );
			if( GsntalkUtil.isEmptyList( targetMemberList ) ) {
				targetMemberList = new ArrayList<Long>();
			}
			
			for( Long memSeqno : targetMemberList ) {
				// 알림 등록
				gsntalkDAO.registerNotification( memSeqno, "SALES", null, vo.getSchdlNm() + " D-1", vo.getBldNm(), 0L, vo.getSuggstnSalesSeqno() );
			}
		}
		
		this.LOGGER.info( "@@ GsntalkBatchService =====>> 분양일정 알림 적재(전송) - 다음날 분양일정 알림 발송 - 대상 일정 : " + tomorrowSalesSchdlList.size() + " 건." );
		
		// 당일 분양일정 알림 발송
		for( SuggestionSalesVO vo : todaySalesSchdlList ) {
			// 분양일정 발송대상 회원 목록조회
			targetMemberList = gsntalkBatchDAO.getSalesSchdlNotiTargetMemberList( vo.getSuggstnSalesSeqno() );
			if( GsntalkUtil.isEmptyList( targetMemberList ) ) {
				targetMemberList = new ArrayList<Long>();
			}
			
			for( Long memSeqno : targetMemberList ) {
				// 알림 등록
				gsntalkDAO.registerNotification( memSeqno, "SALES", null, vo.getSchdlNm() + " D-DAY", vo.getBldNm(), 0L, vo.getSuggstnSalesSeqno() );
			}
		}
		
		this.LOGGER.info( "@@ GsntalkBatchService =====>> 분양일정 알림 적재(전송) - 당일 분양일정 알림 발송 - 대상 일정 : " + todaySalesSchdlList.size() + " 건." );
	}
}