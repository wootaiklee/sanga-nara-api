package com.gsntalk.api.apis;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gsntalk.api.apis.property.PropertyDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.EstateBrokerOfficeVO;
import com.gsntalk.api.common.vo.KnowledgeIndustryComplexVO;
import com.gsntalk.api.common.vo.PropertyVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

@Service( "TestService" )
public class TestService extends CommonService {

	@Autowired
	private TestDAO testDAO;
	
	@Autowired
	public PropertyDAO propertyDAO;
	
	public TestService() {
		super( TestService.class );
	}
	
	public String getServerTime()throws Exception {
		return testDAO.getServerTime();
	}
	
	/**
	 * 전국 공인중개사 사무소 엑셀 데이터 파일 업로드 ( 테스트용 )
	 * 
	 * @param file
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void uploadEstateAgentOfficeExlFile( MultipartFile file )throws Exception {
		if( file == null || file.getSize() == 0 ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT );
		}
		
		String uploadFileName = file.getOriginalFilename();
		String uploadFileFormat = uploadFileName.substring( uploadFileName.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isIn( uploadFileFormat, "xls", "xlsx" ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT );
		}
		
		InputStream is = file.getInputStream();
		
		HSSFWorkbook hssfWorkbook = null;
		HSSFSheet hssfSheet = null;
		HSSFRow hssfRow = null;
		
		XSSFWorkbook xssfWorkbook = null;
		XSSFSheet xssfSheet = null;
		XSSFRow xssfRow = null;
		
		EstateBrokerOfficeVO estateBrokerOfficeVO = null;
		String updateTyp = null;
		int stRowNum = 1;
		int edRowNum = 0;

		if( "xls".equals( uploadFileFormat ) ) {
			hssfWorkbook = new HSSFWorkbook( is );
			hssfSheet = hssfWorkbook.getSheetAt( 0 );
			edRowNum = hssfSheet.getLastRowNum();
		}else {
			xssfWorkbook = new XSSFWorkbook( is );
			xssfSheet = xssfWorkbook.getSheetAt( 0 );
			edRowNum = xssfSheet.getLastRowNum();
		}
		
		LOGGER.info( GsntalkUtil.set1000Comma( edRowNum ) + " data treat start!" );
		
		for( int i = stRowNum; i <= edRowNum; i ++ ) {
			estateBrokerOfficeVO = new EstateBrokerOfficeVO();
			
			// 필수값 존재여부 1차 확인
			if( "xls".equals( uploadFileFormat ) ) {
				hssfRow = hssfSheet.getRow( i );
				
				estateBrokerOfficeVO.setOfcNm( GsntalkUtil.getString( hssfRow.getCell( 0 ).getStringCellValue() ) );											// 중개사무소명
				estateBrokerOfficeVO.setOpenRegNo( GsntalkUtil.getString( hssfRow.getCell( 1 ).getStringCellValue() ) );										// 개설등록번호
				estateBrokerOfficeVO.setReprNm( GsntalkUtil.getString( hssfRow.getCell( 8 ).getStringCellValue() ) );											// 대표자명
				
			}else {
				xssfRow = xssfSheet.getRow( i );
				
				estateBrokerOfficeVO.setOfcNm( GsntalkUtil.getString( xssfRow.getCell( 0 ).getStringCellValue() ) );											// 중개사무소명
				estateBrokerOfficeVO.setOpenRegNo( GsntalkUtil.getString( xssfRow.getCell( 1 ).getStringCellValue() ) );										// 개설등록번호
				estateBrokerOfficeVO.setReprNm( GsntalkUtil.getString( xssfRow.getCell( 8 ).getStringCellValue() ) );											// 대표자명
				
			}
			if(
				GsntalkUtil.isEmpty( estateBrokerOfficeVO.getOfcNm() )
				||
				GsntalkUtil.isEmpty( estateBrokerOfficeVO.getOpenRegNo() )
				||
				GsntalkUtil.isEmpty( estateBrokerOfficeVO.getReprNm() )
			) {
				LOGGER.info( "##### pass empty data (" + i + ")" );
				continue;
			}
			
			// 나머지 값 추출
			if( "xls".equals( uploadFileFormat ) ) {
				estateBrokerOfficeVO.setOpenEstBrkClasGb( GsntalkUtil.getString( hssfRow.getCell( 2 ).getStringCellValue() ) );									// 개업공인중개사종별구분
				estateBrokerOfficeVO.setAddrRoad( GsntalkUtil.getString( hssfRow.getCell( 3 ).getStringCellValue() ) );											// 소재지도로명주소
				estateBrokerOfficeVO.setAddrPost( GsntalkUtil.getString( hssfRow.getCell( 4 ).getStringCellValue() ) );											// 소재지지번주소
				estateBrokerOfficeVO.setTelNo( GsntalkUtil.getString( hssfRow.getCell( 5 ).getStringCellValue() ) );											// 전화번호
				estateBrokerOfficeVO.setOpenRegDate( GsntalkUtil.parseNumberString( GsntalkUtil.getString( hssfRow.getCell( 6 ).getStringCellValue() ) ) );		// 개설등록일자
				estateBrokerOfficeVO.setMltJoinYn( GsntalkUtil.ifEmptyString( hssfRow.getCell( 7 ).getStringCellValue(), GsntalkConstants.NO ) );				// 공제가입여부
				estateBrokerOfficeVO.setLat( GsntalkUtil.getDouble( hssfRow.getCell( 9 ).getStringCellValue() ) );												// 위도
				estateBrokerOfficeVO.setLng( GsntalkUtil.getDouble( hssfRow.getCell( 10 ).getStringCellValue() ) );												// 경도
				estateBrokerOfficeVO.setEstAsstCnt( GsntalkUtil.getInteger( hssfRow.getCell( 11 ).getStringCellValue() ) );										// 중개 보조원 수
				estateBrokerOfficeVO.setEstBrkCnt( GsntalkUtil.getInteger( hssfRow.getCell( 12 ).getStringCellValue() ) );										// 소속 공인중개사 수
				estateBrokerOfficeVO.setDataStndDate( GsntalkUtil.parseNumberString( GsntalkUtil.getString( hssfRow.getCell( 14 ).getStringCellValue() ) ) );	// 데이터 기준일자
				estateBrokerOfficeVO.setOffrInstCd( GsntalkUtil.getString( hssfRow.getCell( 15 ).getStringCellValue() ) );										// 제공기관 코드
				estateBrokerOfficeVO.setOffrInstNm( GsntalkUtil.getString( hssfRow.getCell( 16 ).getStringCellValue() ) );										// 제공기관 명
				
			}else {
				estateBrokerOfficeVO.setOpenEstBrkClasGb( GsntalkUtil.getString( xssfRow.getCell( 2 ).getStringCellValue() ) );									// 개업공인중개사종별구분
				estateBrokerOfficeVO.setAddrRoad( GsntalkUtil.getString( xssfRow.getCell( 3 ).getStringCellValue() ) );											// 소재지도로명주소
				estateBrokerOfficeVO.setAddrPost( GsntalkUtil.getString( xssfRow.getCell( 4 ).getStringCellValue() ) );											// 소재지지번주소
				estateBrokerOfficeVO.setTelNo( GsntalkUtil.getString( xssfRow.getCell( 5 ).getStringCellValue() ) );											// 전화번호
				estateBrokerOfficeVO.setOpenRegDate( GsntalkUtil.parseNumberString( GsntalkUtil.getString( xssfRow.getCell( 6 ).getStringCellValue() ) ) );		// 개설등록일자
				estateBrokerOfficeVO.setMltJoinYn( GsntalkUtil.ifEmptyString( xssfRow.getCell( 7 ).getStringCellValue(), GsntalkConstants.NO ) );				// 공제가입여부
				estateBrokerOfficeVO.setLat( GsntalkUtil.getDouble( xssfRow.getCell( 9 ).getStringCellValue() ) );												// 위도
				estateBrokerOfficeVO.setLng( GsntalkUtil.getDouble( xssfRow.getCell( 10 ).getStringCellValue() ) );												// 경도
				estateBrokerOfficeVO.setEstAsstCnt( GsntalkUtil.getInteger( xssfRow.getCell( 11 ).getStringCellValue() ) );										// 중개 보조원 수
				estateBrokerOfficeVO.setEstBrkCnt( GsntalkUtil.getInteger( xssfRow.getCell( 12 ).getStringCellValue() ) );										// 소속 공인중개사 수
				estateBrokerOfficeVO.setDataStndDate( GsntalkUtil.parseNumberString( GsntalkUtil.getString( xssfRow.getCell( 14 ).getStringCellValue() ) ) );	// 데이터 기준일자
				estateBrokerOfficeVO.setOffrInstCd( GsntalkUtil.getString( xssfRow.getCell( 15 ).getStringCellValue() ) );										// 제공기관 코드
				estateBrokerOfficeVO.setOffrInstNm( GsntalkUtil.getString( xssfRow.getCell( 16 ).getStringCellValue() ) );										// 제공기관 명
				
			}
			
			// 공인중개사 사무소 업데이트 유형 조회
			updateTyp = testDAO.getEstateBrokerOfficeDataUpdateType( estateBrokerOfficeVO );
			if( GsntalkUtil.isEmpty( updateTyp ) ) {
				testDAO.registerEstateBrokerOffice( estateBrokerOfficeVO );
				
			}else if( "UPDATE".equals( updateTyp ) ) {
				testDAO.updateEstateBrokerOffice( estateBrokerOfficeVO );
				
			}
			
			if( i % 500 == 0 ) {
				LOGGER.info( "treat " + GsntalkUtil.set1000Comma( i ) + " / " + GsntalkUtil.set1000Comma( edRowNum ) + " data completed..." );
			}
			
		}
		
		if( "xls".equals( uploadFileFormat ) ) {
			hssfWorkbook.close();
			
		}else {
			xssfWorkbook.close();
			
		}
		is.close();
		
		LOGGER.info( GsntalkUtil.set1000Comma( edRowNum ) + " data treat finished!" );
	}
	
	/**
	 * 지식산업센터 데이터 마이그레이션
	 * 
	 * @param file
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void uploadKnwldgMigration( MultipartFile file )throws Exception {
		if( file == null || file.getSize() == 0 ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT );
		}
		
		InputStream is = file.getInputStream();
		
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook( is );
		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt( 0 );
		XSSFRow xssfRow = null;
		
		int stRowNum = 1;
		int edRowNum = xssfSheet.getLastRowNum();
		
		int prptRegNum = 0;
		String prptRegNo = "";
		String addr = "";
		JSONObject posItem = null;
		String bldNm = "";
		String cmpltnDate = "";
		long askSalesMinPrc = 0L;
		long askSalesAvgPrc = 0L;
		long askSalesMaxPrc = 0L;
		long askLeaseMinPrc = 0L;
		long askLeaseAvgPrc = 0L;
		long askLeaseMaxPrc = 0L;
		double lndArea = 0D;
		double bldArea = 0D;
		double totFlrArea = 0D;
		String[] flr = null;
		int minFlr = 0;
		int maxFlr = 0;
		int parkingCarCnt = 0;
		int husHoldCnt = 0;
		String devCompNm = "";
		String constCompNm = "";
		String trfcInfo = "";
		String siteExplntn = "";
		String smplSmrDscr = "";
		
		long t = System.currentTimeMillis();
		LOGGER.info( GsntalkUtil.set1000Comma( edRowNum ) + " data migration start!" );
		
		for( int i = stRowNum; i <= edRowNum; i ++ ) {
			try {
				xssfRow = xssfSheet.getRow( i );
				
				bldNm = GsntalkUtil.getString( xssfRow.getCell( 0 ).getStringCellValue() );										// 건물명
				addr = GsntalkUtil.getString( xssfRow.getCell( 1 ).getStringCellValue() );										// 주소
				cmpltnDate = GsntalkUtil.getString( xssfRow.getCell( 2 ).getStringCellValue() );								// 준공일
				if( !GsntalkUtil.is8DateFormat( cmpltnDate, true ) ) {
					cmpltnDate = null;
				}
				
				askSalesMinPrc = (long) ( GsntalkUtil.getDouble( xssfRow.getCell( 3 ).getRawValue() ) * 10000 );					// 최저 매매호가
				askSalesAvgPrc = (long) ( GsntalkUtil.getDouble( xssfRow.getCell( 4 ).getRawValue() ) * 10000 );					// 평균 매매호가
				askSalesMaxPrc = (long) ( GsntalkUtil.getDouble( xssfRow.getCell( 5 ).getRawValue() ) * 10000 );					// 최고 매매호가
				askLeaseMinPrc = (long) ( GsntalkUtil.getDouble( xssfRow.getCell( 6 ).getRawValue() ) * 10000 );					// 최저 임대호가
				askLeaseAvgPrc = (long) ( GsntalkUtil.getDouble( xssfRow.getCell( 7 ).getRawValue() ) * 10000 );					// 평균 임대호가
				askLeaseMaxPrc = (long) ( GsntalkUtil.getDouble( xssfRow.getCell( 8 ).getRawValue() ) * 10000 );					// 최고 임대호가
				lndArea = GsntalkUtil.getDouble( GsntalkUtil.parseDoubleString( xssfRow.getCell( 9 ).getRawValue() ) );		// 대지면적
				bldArea = GsntalkUtil.getDouble( GsntalkUtil.parseDoubleString( xssfRow.getCell( 10 ).getRawValue() ) );		// 건축면적
				totFlrArea = GsntalkUtil.getDouble( GsntalkUtil.parseDoubleString( xssfRow.getCell( 11 ).getRawValue() ) );		// 연면적
				flr = GsntalkUtil.getString( xssfRow.getCell( 12 ).getStringCellValue() ).split( " ~ " );								// 층수
				minFlr = Integer.valueOf( flr[0] );																				// 최저층수
				maxFlr = Integer.valueOf( flr[1] );																				// 최고층수
				parkingCarCnt = GsntalkUtil.getInteger( xssfRow.getCell( 13 ).getRawValue() );									// 총 주차대수
				husHoldCnt = 0;																									// 총 세대원수 ( 엑셀에 자료없음 )
				constCompNm = GsntalkUtil.getString( xssfRow.getCell( 14 ).getStringCellValue() );								// 시공사명
				devCompNm = GsntalkUtil.getString( xssfRow.getCell( 15 ).getStringCellValue() );								// 시행사명
				trfcInfo = GsntalkUtil.getString( xssfRow.getCell( 16 ).getStringCellValue() );									// 교통정보
				siteExplntn = GsntalkUtil.getString( xssfRow.getCell( 17 ).getStringCellValue() );								// 현장설명
				smplSmrDscr = GsntalkUtil.getString( xssfRow.getCell( 18 ).getStringCellValue() );								// 한줄요약
				
				posItem = gsntalkIFUtil.getGeocode( addr, !GsntalkConstants.TEST_MODE );
				
				prptRegNum = propertyDAO.getNextKPrptRegNum();
				prptRegNo = GsntalkUtil.createKPrptRegno( prptRegNum );
				
				testDAO.registerKnowlegdeIndustryComplex( prptRegNo, addr, GsntalkUtil.getDouble( posItem.get( "lat" ) ), GsntalkUtil.getDouble( posItem.get( "lng" ) ), bldNm, cmpltnDate,
						askSalesMinPrc, askSalesAvgPrc, askSalesMaxPrc, askLeaseMinPrc, askLeaseAvgPrc, askLeaseMaxPrc,
						lndArea, bldArea, totFlrArea, minFlr, maxFlr, parkingCarCnt, husHoldCnt,
						devCompNm, constCompNm, trfcInfo, siteExplntn, smplSmrDscr);
				
				if( i % 50 == 0 ) {
					this.LOGGER.info( "#################################################### " + i + " / " + edRowNum );
				}
			}catch( Exception e ) {
				this.LOGGER.info( " ########################### : " + i );
				this.LOGGER.info( " #### bldNm : " + bldNm );
				this.LOGGER.info( " #### addr : " + addr );
				this.LOGGER.info( " #### cmpltnDate : " + cmpltnDate );
				this.LOGGER.info( " #### askSalesMinPrc : " + askSalesMinPrc );
				this.LOGGER.info( " #### askSalesAvgPrc : " + askSalesAvgPrc );
				this.LOGGER.info( " #### askSalesMaxPrc : " + askSalesMaxPrc );
				this.LOGGER.info( " #### askLeaseMinPrc : " + askLeaseMinPrc );
				this.LOGGER.info( " #### askLeaseAvgPrc : " + askLeaseAvgPrc );
				this.LOGGER.info( " #### askLeaseMaxPrc : " + askLeaseMaxPrc );
				this.LOGGER.info( " #### lndArea : " + lndArea );
				this.LOGGER.info( " #### bldArea : " + bldArea );
				this.LOGGER.info( " #### totFlrArea : " + totFlrArea );
				this.LOGGER.info( " #### flr : " + flr.toString() );
				this.LOGGER.info( " #### minFlr : " + minFlr );
				this.LOGGER.info( " #### maxFlr : " + maxFlr );
				this.LOGGER.info( " #### parkingCarCnt : " + parkingCarCnt );
				this.LOGGER.info( " #### husHoldCnt : " + husHoldCnt );
				this.LOGGER.info( " #### constCompNm : " + constCompNm );
				this.LOGGER.info( " #### devCompNm : " + devCompNm );
				this.LOGGER.info( " #### trfcInfo : " + trfcInfo );
				this.LOGGER.info( " #### siteExplntn : " + siteExplntn );
				this.LOGGER.info( " #### smplSmrDscr : " + smplSmrDscr );
				this.LOGGER.info( " #### posItem : " + posItem.toJSONString() );
				this.LOGGER.info( " #### prptRegNum : " + prptRegNum );
				this.LOGGER.info( " #### prptRegNo : " + prptRegNo );
				this.LOGGER.info( " ########################### : " + i );
				throw e;
			}

		}
		
		xssfWorkbook.close();
		is.close();
		
		LOGGER.info( GsntalkUtil.set1000Comma( edRowNum ) + " data migration finished! ( " + ( System.currentTimeMillis() - t ) + " milliseconds. )" );
	}
	
	/**
	 * 지식산업센터 단축주소 업데이트
	 * 
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void updateAddrShortNmToKnowledgeIndustryComplex()throws Exception {
		List<KnowledgeIndustryComplexVO> list = testDAO.getAllKnowledgeIndustryComplexList();
		
		this.LOGGER.info( " ################### updateAddrShortNmToKnowledgeIndustryComplex " + list.size() + " records start." );
		
		int i = 1;
		JSONObject geocodeItem = null;
		for( KnowledgeIndustryComplexVO vo : list ) {
			geocodeItem = gsntalkIFUtil.getGeocode( vo.getAddr(), false );
			
			try {
				testDAO.updateAddrShortNmToKnowledgeIndustryComplex( vo.getKnwldgIndCmplxSeqno(), GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) ) );
			}catch( Exception e ) {
				this.LOGGER.info( " ##### exception #### " + vo.getAddr() );
				throw e;
			}
			
			if( i % 50 == 0 ) {
				this.LOGGER.info( " ################### " + i + " / " + list.size() + " records finished." );
			}
			
			i ++;
		}
		
		this.LOGGER.info( " ################### updateAddrShortNmToKnowledgeIndustryComplex " + list.size() + " records done." );
	}
	
	/**
	 * 매물 단축주소 업데이트
	 * 
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void updateAddrShortNmToProperty()throws Exception {
		List<PropertyVO> list = testDAO.getAllPropertyList();
		
		this.LOGGER.info( " ################### updateAddrShortNmToProperty " + list.size() + " records start." );
		
		int i = 1;
		JSONObject geocodeItem = null;
		for( PropertyVO vo : list ) {
			geocodeItem = gsntalkIFUtil.getGeocode( vo.getAddr(), false );
			
			try {
				testDAO.updateAddrShortNmToProperty( vo.getPrptSeqno(), GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) ) );
			}catch( Exception e ) {
				this.LOGGER.info( " ##### exception #### " + vo.getAddr() );
				throw e;
			}
			
			if( i % 50 == 0 ) {
				this.LOGGER.info( " ################### " + i + " / " + list.size() + " records finished." );
			}
			
			i ++;
		}
		
		this.LOGGER.info( " ################### updateAddrShortNmToProperty " + list.size() + " records done." );
	}
	
	/**
	 * 첨부파일 및 파라메터 업로드 테스트
	 * 
	 * @param param1
	 * @param param2
	 * @param file1
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject multipartUploadTest( String param1, String param2, MultipartFile file1, List<MultipartFile> files )throws Exception {
		String orgFileNm = file1.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT );
		}
		for( MultipartFile file : files ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT );
			}
		}
		
		JSONObject item = new JSONObject();
		item.put( "param1", param1 );
		item.put( "param2", param2 );
		item.put( "file1Url", gsntalkS3Util.uploadTestImageFile( file1 ) );
		
		JSONArray fileUrlItems = new JSONArray();
		for( MultipartFile file : files ) {
			fileUrlItems.add( gsntalkS3Util.uploadTestImageFile( file ) );
		}
		item.put( "fileUrlItems", fileUrlItems );
		
		return item;
	}
	
	/**
	 * 주소 -> 위경도 변환 테스트
	 * 
	 * @param address
	 * @return
	 * @throws Exception
	 */
	public JSONObject geocodeTest( String address )throws Exception {
		
		return gsntalkIFUtil.getGeocode( address, !GsntalkConstants.TEST_MODE );
	}
	
	public String moveS3Test( String tmpFileNm )throws Exception {
		return gsntalkS3Util.moveTmpFileToSuggstnRepFile( 0, tmpFileNm );
	}
}