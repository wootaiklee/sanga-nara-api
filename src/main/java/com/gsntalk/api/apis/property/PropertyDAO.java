package com.gsntalk.api.apis.property;

import java.util.List;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.common.vo.PropertyOptionVO;
import com.gsntalk.api.common.vo.PropertyPhotoVO;
import com.gsntalk.api.common.vo.PropertySuggestRequestVO;
import com.gsntalk.api.common.vo.PropertyVO;
import com.gsntalk.api.util.GsntalkEncryptor;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Repository( "com.gsntalk.api.apis.property.PropertyDAO" )
public class PropertyDAO extends CommonDAO {

	public PropertyDAO() {
		super( PropertyDAO.class );
	}
	
	/**
	 * 매물등록번호 K - 다음 순번 조회 ( 지식산업센터 )
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNextKPrptRegNum()throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getNextKPrptRegNum" );
	}
	
	/**
	 * 매물등록번호 P - 다음 순번 조회 ( 매물 )
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNextPPrptRegNum()throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getNextPPrptRegNum" );
	}
	
	/**
	 * 매물 등록 ( 1 ~ 4 단계 정보 )
	 */
	public long registerProperty( long memSeqno, long estBrkMemOfcSeqno, String prptRegNo, String estateTypGbCd, String estateTypCd, String tranTypGbCd, long dealAmt, String dealAmtDiscsnPsblYn, long dpstAmt, int montRentAmt, String existngLeaseExstsYn, long crntDpstAmt, int crntMontRentAmt,
			String keyMonExstsYn, long keyMonAmt, long prmmAmt, String cmpltExpctDate, String addr, String dtlAddr, String addrShortNm, double lat, double lng, String mapDispYn, String tmpAddrYn, String unregistYn, int flr, int allFlr, int minFlr, int maxFlr,
			double splyArea, double prvArea, double lndArea, double totFlrArea, String useCnfrmDate, String bldUsageGbCd, String suggstnBldUsageGbCd, String lndCrntUsageGbCd, String psblMovDayTypCd, String psblMovDate, int monMntnceCost, String loanGbCd, long loanAmt, String parkingPsblYn, int parkingCost,
			int roomCnt, int bathRoomCnt, String crntSectrGbCd, String suggstnSectrGbCd, String bldDirctnGbCd, String heatKindGbCd, double wghtPerPy, String elvFcltExstsYn, String frghtElvExstsYn, String intrrYn, String dockExstsYn, String hoistExstsYn, String flrHghtTypGbCd,
			String elctrPwrTypGbCd, String intnlStrctrTypCd, String bultInYn, String movInReprtPsblYn, String cityPlanYn, String bldCnfrmIssueYn, String lndDealCnfrmApplYn, String entrnceRoadExstsYn, String optionExstsYn
	)throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		propertyVO.setPrptRegNo( prptRegNo );
		propertyVO.setEstateTypGbCd( estateTypGbCd );
		propertyVO.setEstateTypCd( estateTypCd );
		propertyVO.setTranTypGbCd( tranTypGbCd );
		propertyVO.setDealAmt( dealAmt );
		propertyVO.setDealAmtDiscsnPsblYn( dealAmtDiscsnPsblYn );
		propertyVO.setDpstAmt( dpstAmt );
		propertyVO.setMontRentAmt( montRentAmt );
		propertyVO.setExistngLeaseExstsYn( existngLeaseExstsYn );
		propertyVO.setCrntDpstAmt( crntDpstAmt );
		propertyVO.setCrntMontRentAmt( crntMontRentAmt );
		propertyVO.setKeyMonExstsYn( keyMonExstsYn );
		propertyVO.setKeyMonAmt( keyMonAmt );
		propertyVO.setPrmmAmt( prmmAmt );
		propertyVO.setCmpltExpctDate( cmpltExpctDate );
		propertyVO.setAddr( GsntalkXSSUtil.encodeXss( GsntalkUtil.addressTopLvlReplace( addr ) ) );
		propertyVO.setDtlAddr( GsntalkXSSUtil.encodeXss( dtlAddr ) );
		propertyVO.setAddrShortNm( addrShortNm );
		propertyVO.setLat( lat );
		propertyVO.setLng( lng );
		propertyVO.setMapDispYn( mapDispYn );
		propertyVO.setTmpAddrYn( tmpAddrYn );
		propertyVO.setUnregistYn( unregistYn );
		propertyVO.setFlr( flr );
		propertyVO.setAllFlr( allFlr );
		propertyVO.setMinFlr( minFlr );
		propertyVO.setMaxFlr( maxFlr );
		propertyVO.setSplyArea( splyArea );
		propertyVO.setPrvArea( prvArea );
		propertyVO.setLndArea( lndArea );
		propertyVO.setTotFlrArea( totFlrArea );
		propertyVO.setUseCnfrmDate( useCnfrmDate );
		propertyVO.setBldUsageGbCd( bldUsageGbCd );
		propertyVO.setSuggstnBldUsageGbCd( suggstnBldUsageGbCd );
		propertyVO.setLndCrntUsageGbCd( lndCrntUsageGbCd );
		propertyVO.setPsblMovDayTypCd( psblMovDayTypCd );
		propertyVO.setPsblMovDate( psblMovDate );
		propertyVO.setMonMntnceCost( monMntnceCost );
		propertyVO.setLoanGbCd( loanGbCd );
		propertyVO.setLoanAmt( loanAmt );
		propertyVO.setParkingPsblYn( parkingPsblYn );
		propertyVO.setParkingCost( parkingCost );
		propertyVO.setRoomCnt( roomCnt );
		propertyVO.setBathRoomCnt( bathRoomCnt );
		propertyVO.setCrntSectrGbCd( crntSectrGbCd );
		propertyVO.setSuggstnSectrGbCd( suggstnSectrGbCd );
		propertyVO.setBldDirctnGbCd( bldDirctnGbCd );
		propertyVO.setHeatKindGbCd( heatKindGbCd );
		propertyVO.setWghtPerPy( wghtPerPy );
		propertyVO.setElvFcltExstsYn( elvFcltExstsYn );
		propertyVO.setFrghtElvExstsYn( frghtElvExstsYn );
		propertyVO.setIntrrYn( intrrYn );
		propertyVO.setDockExstsYn( dockExstsYn );
		propertyVO.setHoistExstsYn( hoistExstsYn );
		propertyVO.setFlrHghtTypGbCd( flrHghtTypGbCd );
		propertyVO.setElctrPwrTypGbCd( elctrPwrTypGbCd );
		propertyVO.setIntnlStrctrTypCd( intnlStrctrTypCd );
		propertyVO.setBultInYn( bultInYn );
		propertyVO.setMovInReprtPsblYn( movInReprtPsblYn );
		propertyVO.setCityPlanYn( cityPlanYn );
		propertyVO.setBldCnfrmIssueYn( bldCnfrmIssueYn );
		propertyVO.setLndDealCnfrmApplYn( lndDealCnfrmApplYn );
		propertyVO.setEntrnceRoadExstsYn( entrnceRoadExstsYn );
		propertyVO.setOptionExstsYn( optionExstsYn );
		propertyVO.setRegMemSeqno( memSeqno );
		
		sqlSession.insert( "PropertyMapper.registerProperty", propertyVO );
		return propertyVO.getPrptSeqno();
	}
	
	/**
	 * 매물 수정 ( 1 ~ 4 단계 정보 )
	 * 
	 * @param prptSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param dealAmtDiscsnPsblYn
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param existngLeaseExstsYn
	 * @param crntDpstAmt
	 * @param crntMontRentAmt
	 * @param keyMonExstsYn
	 * @param keyMonAmt
	 * @param prmmAmt
	 * @param cmpltExpctDate
	 * @param addr
	 * @param dtlAddr
	 * @param addrShortNm
	 * @param lat
	 * @param lng
	 * @param mapDispYn
	 * @param tmpAddrYn
	 * @param unregistYn
	 * @param flr
	 * @param allFlr
	 * @param minFlr
	 * @param maxFlr
	 * @param splyArea
	 * @param prvArea
	 * @param lndArea
	 * @param totFlrArea
	 * @param useCnfrmDate
	 * @param bldUsageGbCd
	 * @param suggstnBldUsageGbCd
	 * @param lndCrntUsageGbCd
	 * @param psblMovDayTypCd
	 * @param psblMovDate
	 * @param monMntnceCost
	 * @param loanGbCd
	 * @param loanAmt
	 * @param parkingPsblYn
	 * @param parkingCost
	 * @param roomCnt
	 * @param bathRoomCnt
	 * @param crntSectrGbCd
	 * @param suggstnSectrGbCd
	 * @param bldDirctnGbCd
	 * @param heatKindGbCd
	 * @param wghtPerPy
	 * @param elvFcltExstsYn
	 * @param frghtElvExstsYn
	 * @param intrrYn
	 * @param dockExstsYn
	 * @param hoistExstsYn
	 * @param flrHghtTypGbCd
	 * @param elctrPwrTypGbCd
	 * @param intnlStrctrTypCd
	 * @param bultInYn
	 * @param movInReprtPsblYn
	 * @param cityPlanYn
	 * @param bldCnfrmIssueYn
	 * @param lndDealCnfrmApplYn
	 * @param entrnceRoadExstsYn
	 * @param optionExstsYn
	 * @param deniedExprDateYn
	 * @param fromAdminYn
	 * @return
	 * @throws Exception
	 */
	public long updateProperty( long prptSeqno, String estateTypGbCd, String estateTypCd, String tranTypGbCd, long dealAmt, String dealAmtDiscsnPsblYn, long dpstAmt, int montRentAmt, String existngLeaseExstsYn, long crntDpstAmt, int crntMontRentAmt,
			String keyMonExstsYn, long keyMonAmt, long prmmAmt, String cmpltExpctDate, String addr, String dtlAddr, String addrShortNm, double lat, double lng, String mapDispYn, String tmpAddrYn, String unregistYn, int flr, int allFlr, int minFlr, int maxFlr,
			double splyArea, double prvArea, double lndArea, double totFlrArea, String useCnfrmDate, String bldUsageGbCd, String suggstnBldUsageGbCd, String lndCrntUsageGbCd, String psblMovDayTypCd, String psblMovDate, int monMntnceCost, String loanGbCd, long loanAmt, String parkingPsblYn, int parkingCost,
			int roomCnt, int bathRoomCnt, String crntSectrGbCd, String suggstnSectrGbCd, String bldDirctnGbCd, String heatKindGbCd, double wghtPerPy, String elvFcltExstsYn, String frghtElvExstsYn, String intrrYn, String dockExstsYn, String hoistExstsYn, String flrHghtTypGbCd,
			String elctrPwrTypGbCd, String intnlStrctrTypCd, String bultInYn, String movInReprtPsblYn, String cityPlanYn, String bldCnfrmIssueYn, String lndDealCnfrmApplYn, String entrnceRoadExstsYn, String optionExstsYn, String deniedExprDateYn, String fromAdminYn
	)throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setPrptSeqno( prptSeqno );
		propertyVO.setEstateTypGbCd( estateTypGbCd );
		propertyVO.setEstateTypCd( estateTypCd );
		propertyVO.setTranTypGbCd( tranTypGbCd );
		propertyVO.setDealAmt( dealAmt );
		propertyVO.setDealAmtDiscsnPsblYn( dealAmtDiscsnPsblYn );
		propertyVO.setDpstAmt( dpstAmt );
		propertyVO.setMontRentAmt( montRentAmt );
		propertyVO.setExistngLeaseExstsYn( existngLeaseExstsYn );
		propertyVO.setCrntDpstAmt( crntDpstAmt );
		propertyVO.setCrntMontRentAmt( crntMontRentAmt );
		propertyVO.setKeyMonExstsYn( keyMonExstsYn );
		propertyVO.setKeyMonAmt( keyMonAmt );
		propertyVO.setPrmmAmt( prmmAmt );
		propertyVO.setCmpltExpctDate( cmpltExpctDate );
		propertyVO.setAddr( GsntalkXSSUtil.encodeXss( GsntalkUtil.addressTopLvlReplace( addr ) ) );
		propertyVO.setDtlAddr( GsntalkXSSUtil.encodeXss( dtlAddr ) );
		propertyVO.setAddrShortNm( addrShortNm );
		propertyVO.setLat( lat );
		propertyVO.setLng( lng );
		propertyVO.setMapDispYn( mapDispYn );
		propertyVO.setTmpAddrYn( tmpAddrYn );
		propertyVO.setUnregistYn( unregistYn );
		propertyVO.setFlr( flr );
		propertyVO.setAllFlr( allFlr );
		propertyVO.setMinFlr( minFlr );
		propertyVO.setMaxFlr( maxFlr );
		propertyVO.setSplyArea( splyArea );
		propertyVO.setPrvArea( prvArea );
		propertyVO.setLndArea( lndArea );
		propertyVO.setTotFlrArea( totFlrArea );
		propertyVO.setUseCnfrmDate( useCnfrmDate );
		propertyVO.setBldUsageGbCd( bldUsageGbCd );
		propertyVO.setSuggstnBldUsageGbCd( suggstnBldUsageGbCd );
		propertyVO.setLndCrntUsageGbCd( lndCrntUsageGbCd );
		propertyVO.setPsblMovDayTypCd( psblMovDayTypCd );
		propertyVO.setPsblMovDate( psblMovDate );
		propertyVO.setMonMntnceCost( monMntnceCost );
		propertyVO.setLoanGbCd( loanGbCd );
		propertyVO.setLoanAmt( loanAmt );
		propertyVO.setParkingPsblYn( parkingPsblYn );
		propertyVO.setParkingCost( parkingCost );
		propertyVO.setRoomCnt( roomCnt );
		propertyVO.setBathRoomCnt( bathRoomCnt );
		propertyVO.setCrntSectrGbCd( crntSectrGbCd );
		propertyVO.setSuggstnSectrGbCd( suggstnSectrGbCd );
		propertyVO.setBldDirctnGbCd( bldDirctnGbCd );
		propertyVO.setHeatKindGbCd( heatKindGbCd );
		propertyVO.setWghtPerPy( wghtPerPy );
		propertyVO.setElvFcltExstsYn( elvFcltExstsYn );
		propertyVO.setFrghtElvExstsYn( frghtElvExstsYn );
		propertyVO.setIntrrYn( intrrYn );
		propertyVO.setDockExstsYn( dockExstsYn );
		propertyVO.setHoistExstsYn( hoistExstsYn );
		propertyVO.setFlrHghtTypGbCd( flrHghtTypGbCd );
		propertyVO.setElctrPwrTypGbCd( elctrPwrTypGbCd );
		propertyVO.setIntnlStrctrTypCd( intnlStrctrTypCd );
		propertyVO.setBultInYn( bultInYn );
		propertyVO.setMovInReprtPsblYn( movInReprtPsblYn );
		propertyVO.setCityPlanYn( cityPlanYn );
		propertyVO.setBldCnfrmIssueYn( bldCnfrmIssueYn );
		propertyVO.setLndDealCnfrmApplYn( lndDealCnfrmApplYn );
		propertyVO.setEntrnceRoadExstsYn( entrnceRoadExstsYn );
		propertyVO.setOptionExstsYn( optionExstsYn );
		propertyVO.setDeniedExprDateYn( deniedExprDateYn );
		propertyVO.setFromAdminYn( fromAdminYn );
		
		sqlSession.insert( "PropertyMapper.updateProperty", propertyVO );
		return propertyVO.getPrptSeqno();
	}
	
	/**
	 * 매물 관리비유형 전체삭제
	 * 
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void removeAllPropertyMaintenanceTyps( long prptSeqno )throws Exception {
		sqlSession.delete( "PropertyMapper.removeAllPropertyMaintenanceTyps", prptSeqno );
	}
	
	/**
	 * 매물 관리비유형 등록
	 * 
	 * @param prptSeqno
	 * @param mntnceCostTypCd
	 * @throws Exception
	 */
	public void registerPropertyMaintenanceTyps( long prptSeqno, String mntnceCostTypCd )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setPrptSeqno( prptSeqno );
		propertyVO.setMntnceCostTypCd( mntnceCostTypCd );
		
		sqlSession.insert( "PropertyMapper.registerPropertyMaintenanceTyps", propertyVO );
	}
	
	/**
	 * 매물 옵션유형 전체삭제
	 * 
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void removeAllPropertyOptionTyps( long prptSeqno )throws Exception {
		sqlSession.delete( "PropertyMapper.removeAllPropertyOptionTyps", prptSeqno );
	}
	
	/**
	 * 매물 옵션유형 등록
	 * 
	 * @param prptSeqno
	 * @param optionItemTypGbCd
	 * @param optionItemTypCd
	 * @throws Exception
	 */
	public void registerPropertyOptionTyps( long prptSeqno, String optionItemTypGbCd, String optionItemTypCd )throws Exception {
		PropertyOptionVO propertyOptionVO = new PropertyOptionVO();
		propertyOptionVO.setPrptSeqno( prptSeqno );
		propertyOptionVO.setOptionItemTypGbCd( optionItemTypGbCd );
		propertyOptionVO.setOptionItemTypCd( optionItemTypCd );
		
		sqlSession.insert( "PropertyMapper.registerPropertyOptionTyps", propertyOptionVO );
	}
	
	/**
	 * 매물 사진정보 등록
	 * 
	 * @param prptSeqno
	 * @param smplSmrDscr
	 * @param dtlExplntnDscr
	 * @param matterPortLinkUrl
	 * @param prvtMemoDscr
	 * @throws Exception
	 */
	public void registerPropertyPhotoInfo( long prptSeqno, String smplSmrDscr, String dtlExplntnDscr, String matterPortLinkUrl, String prvtMemoDscr )throws Exception {
		PropertyPhotoVO propertyPhotoVO = new PropertyPhotoVO();
		propertyPhotoVO.setPrptSeqno( prptSeqno );
		propertyPhotoVO.setSmplSmrDscr( GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		propertyPhotoVO.setDtlExplntnDscr( GsntalkXSSUtil.encodeXss( dtlExplntnDscr ) );
		propertyPhotoVO.setMatterPortLinkUrl( GsntalkXSSUtil.encodeXss( matterPortLinkUrl ) );
		propertyPhotoVO.setPrvtMemoDscr( GsntalkXSSUtil.encodeXss( prvtMemoDscr ) );
		
		sqlSession.insert( "PropertyMapper.registerPropertyPhotoInfo", propertyPhotoVO );
	}
	
	/**
	 * 매물 사진정보 업데이트
	 * 
	 * @param prptSeqno
	 * @param smplSmrDscr
	 * @param dtlExplntnDscr
	 * @param prvtMemoDscr
	 * @throws Exception
	 */
	public void updatePropertyPhotoInfo( long prptSeqno, String smplSmrDscr, String dtlExplntnDscr, String prvtMemoDscr )throws Exception {
		PropertyPhotoVO propertyPhotoVO = new PropertyPhotoVO();
		propertyPhotoVO.setPrptSeqno( prptSeqno );
		propertyPhotoVO.setSmplSmrDscr( GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		propertyPhotoVO.setDtlExplntnDscr( GsntalkXSSUtil.encodeXss( dtlExplntnDscr ) );
		propertyPhotoVO.setPrvtMemoDscr( GsntalkXSSUtil.encodeXss( prvtMemoDscr ) );
		
		sqlSession.insert( "PropertyMapper.updatePropertyPhotoInfo", propertyPhotoVO );
	}
	
	/**
	 * 관리자 - 매물 사진정보 업데이트
	 * 
	 * @param prptSeqno
	 * @param smplSmrDscr
	 * @param dtlExplntnDscr
	 * @param matterPortLinkUrl
	 * @throws Exception
	 */
	public void updatePropertyPhotoInfoFromAdmin( long prptSeqno, String smplSmrDscr, String dtlExplntnDscr, String matterPortLinkUrl )throws Exception {
		PropertyPhotoVO propertyPhotoVO = new PropertyPhotoVO();
		propertyPhotoVO.setPrptSeqno( prptSeqno );
		propertyPhotoVO.setSmplSmrDscr( GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		propertyPhotoVO.setDtlExplntnDscr( GsntalkXSSUtil.encodeXss( dtlExplntnDscr ) );
		propertyPhotoVO.setMatterPortLinkUrl( GsntalkXSSUtil.encodeXss( matterPortLinkUrl ) );
		
		sqlSession.insert( "PropertyMapper.updatePropertyPhotoInfoFromAdmin", propertyPhotoVO );
	}
	
	/**
	 * 매물 대표사진 삭제처리
	 * 
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void removePropertyRepPhoto( long prptSeqno )throws Exception {
		sqlSession.update( "PropertyMapper.removePropertyRepPhoto", prptSeqno );
	}
	
	/**
	 * 매물 사진 등록
	 * 
	 * @param prptSeqno
	 * @param uploadFileNm
	 * @param saveFileNm
	 * @param fileUrl
	 * @param repPhotoYn
	 * @throws Exception
	 */
	public void registerPropertyPhoto( long prptSeqno, String uploadFileNm, String saveFileNm, String fileUrl, String repPhotoYn )throws Exception {
		PropertyPhotoVO propertyPhotoVO = new PropertyPhotoVO();
		propertyPhotoVO.setPrptSeqno( prptSeqno );
		propertyPhotoVO.setUploadFileNm( uploadFileNm );
		propertyPhotoVO.setSaveFileNm( saveFileNm );
		propertyPhotoVO.setFileUrl( fileUrl );
		propertyPhotoVO.setRepPhotoYn( repPhotoYn );
		
		sqlSession.insert( "PropertyMapper.registerPropertyPhoto", propertyPhotoVO );
	}
	
	/**
	 * 매물사진 삭제처리
	 * 
	 * @param prptSeqno
	 * @param fileUrl
	 * @throws Exception
	 */
	public void deletePropertyPhoto( long prptSeqno, String fileUrl )throws Exception {
		PropertyPhotoVO propertyPhotoVO = new PropertyPhotoVO();
		propertyPhotoVO.setPrptSeqno( prptSeqno );
		propertyPhotoVO.setFileUrl( fileUrl );
		
		sqlSession.update( "PropertyMapper.deletePropertyPhoto", propertyPhotoVO );
	}
	
	/**
	 * 매물시퀀스 유효성 검증
	 * 
	 * @param prptSeqno
	 * @return
	 */
	public int isAvailProperty( long prptSeqno ) {
		return sqlSession.selectOne( "PropertyMapper.isAvailProperty", prptSeqno );
	}
	
	/**
	 * 일반회원 등록 매물시퀀스 유효성 검증
	 * 
	 * @param prptSeqno
	 * @return
	 */
	public PropertyVO isAvailMembersProperty( long prptSeqno ) {
		return sqlSession.selectOne( "PropertyMapper.isAvailMembersProperty", prptSeqno );
	}

	/**
	 * 관심매물 등록여부 확인
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public int isExistsFavProperty( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.isExistsFavProperty", propertyVO );
	}
	
	/**
	 * 관심매물 등록
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void registerFavProperty( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setRegMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		sqlSession.insert( "PropertyMapper.registerFavProperty", propertyVO );
	}
	
	/**
	 * 관심매물 해제
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void releaseFavProperty( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		sqlSession.delete( "PropertyMapper.releaseFavProperty", propertyVO );
	}
	
	/**
	 * 중개사 매물목록 조회 ( 중개사무소 상세페이지 )
	 * 
	 * @param memSeqno
	 * @param estBrkMemOfcSeqno
	 * @param tranTypGbCd
	 * @param sortItem
	 * @param sortTyp
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getEstBrkOfcPropertyList( long memSeqno, long estBrkMemOfcSeqno, String tranTypGbCd, String sortItem, String sortTyp )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		propertyVO.setTranTypGbCd( tranTypGbCd );
		propertyVO.setSortItem( sortItem );
		propertyVO.setSortTyp( sortTyp );
		
		return sqlSession.selectList( "PropertyMapper.getEstBrkOfcPropertyList", propertyVO );
	}
	
	/**
	 * 중개사 최근 매물목록 조회 ( 30일 이내기준, 3건까지 )
	 * 
	 * @param exceptPrptSeqno
	 * @param estBrkMemOfcSeqno
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getEstBrkOfcPropertyRecent3List( long exceptPrptSeqno, long estBrkMemOfcSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setExceptPrptSeqno( exceptPrptSeqno );
		propertyVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		
		return sqlSession.selectList( "PropertyMapper.getEstBrkOfcPropertyRecent3List", propertyVO );
	}
	
	/**
	 * 매물 상세정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getPropertyDtlItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.getPropertyDtlItem", propertyVO );
	}
	
	/**
	 * 매물 사진 목록조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public List<PropertyPhotoVO> getPropertyPhotoList( long prptSeqno )throws Exception {
		return sqlSession.selectList( "PropertyMapper.getPropertyPhotoList", prptSeqno );
	}
	
	/**
	 * 옵션 항목유형 목록 조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public List<PropertyOptionVO> getOptionItemTypeList( long prptSeqno )throws Exception {
		return sqlSession.selectList( "PropertyMapper.getOptionItemTypeList", prptSeqno );
	}
	
	/**
	 * 옵션 상세항목 목록조회
	 * 
	 * @param prptSeqno
	 * @param optionItemTypGbCd
	 * @return
	 * @throws Exception
	 */
	public List<PropertyOptionVO> getOptionDtlList( long prptSeqno, String optionItemTypGbCd )throws Exception {
		PropertyOptionVO propertyOptionVO = new PropertyOptionVO();
		propertyOptionVO.setPrptSeqno( prptSeqno );
		propertyOptionVO.setOptionItemTypGbCd( optionItemTypGbCd );
		
		return sqlSession.selectList( "PropertyMapper.getOptionDtlList", propertyOptionVO );
	}
	
	/**
	 * 중개회원 - 내 매물 목록 조회 ( 페이징 )
	 * 
	 * @return
	 */
	public List<PropertyVO> getEstBrkPropertyItems( long memSeqno, String srchClasGbCd, String tranTypGbCd, String estateTypCd, String srchVal, int stRnum, int edRnum )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setSrchGbCd( srchClasGbCd );
		propertyVO.setTranTypGbCd( tranTypGbCd );
		propertyVO.setEstateTypCd( estateTypCd );
		propertyVO.setSrchVal( srchVal );
		propertyVO.setStRnum( stRnum );
		propertyVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "PropertyMapper.getEstBrkPropertyItems", propertyVO );
	}
	
	/**
	 * 매물 소유권 검증
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public int isMembersProperty( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.isMembersProperty", propertyVO );
	}
	
	/**
	 * 중개회원 - 내 매물 삭제
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void deleteEstBrkProperty( long prptSeqno )throws Exception {
		sqlSession.update( "PropertyMapper.deleteEstBrkProperty", prptSeqno );
	}
	
	/**
	 * 중개회원 - 내 매물 등록만료 여부 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getPropertyRegExprYn( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.getPropertyRegExprYn", propertyVO );
	}
	
	/**
	 * 매물 상태변경 
	 * 
	 * @param prptSeqno
	 * @param dealStatGbCd
	 * @throws Exception
	 */
	public void updatePropertyDealStatGbCd( long prptSeqno, String dealStatGbCd )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setPrptSeqno( prptSeqno );
		propertyVO.setDealStatGbCd( dealStatGbCd );
		
		sqlSession.update( "PropertyMapper.updatePropertyDealStatGbCd", propertyVO );
	}
	
	/**
	 * 중개회원 - 등록만료 매물 재등록
	 * 
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void regenEstBrkProperty( long prptSeqno )throws Exception {
		sqlSession.update( "PropertyMapper.regenEstBrkProperty", prptSeqno );
	}
	
	/**
	 * 매물등록 1단계 필드정보 조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getEstBrkPrptStep1DataItem( long prptSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getEstBrkPrptStep1DataItem", prptSeqno );
	}
	
	/**
	 * 매물등록 2단계 필드정보 조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getEstBrkPrptStep2DataItem( long prptSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getEstBrkPrptStep2DataItem", prptSeqno );
	}
	
	/**
	 * 매물등록 3단계 필드정보 조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getEstBrkPrptStep3DataItem( long prptSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getEstBrkPrptStep3DataItem", prptSeqno );
	}
	
	/**
	 * 매물등록 4단계 필드정보 조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getEstBrkPrptStep4DataItem( long prptSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getEstBrkPrptStep4DataItem", prptSeqno );
	}
	
	/**
	 * 매물등록 5단계 필드정보 조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyPhotoVO getEstBrkPrptStep5DataItem( long prptSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getEstBrkPrptStep5DataItem", prptSeqno );
	}
	
	/**
	 * 선택 관리비 유형코드 목록조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public List<String> getPrptMntnceCostTypList( long prptSeqno )throws Exception {
		return sqlSession.selectList( "PropertyMapper.getPrptMntnceCostTypList", prptSeqno );
	}
	
	/**
	 * 선택 옵션유형 목록조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public List<PropertyOptionVO> getPrptOptionTypList( long prptSeqno )throws Exception {
		return sqlSession.selectList( "PropertyMapper.getPrptOptionTypList", prptSeqno );
	}
	
	/**
	 * 매물사진 목록조회 ( 대표사진 포함 )
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public List<PropertyPhotoVO> getPrptPhotoList( long prptSeqno )throws Exception {
		return sqlSession.selectList( "PropertyMapper.getPrptPhotoList", prptSeqno );
	}
	
	/**
	 * 일반회원 매물등록
	 * 
	 * @param memSeqno
	 * @param prptRegNo
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param dealAmtDiscsnPsblYn
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param existngLeaseExstsYn
	 * @param crntDpstAmt
	 * @param crntMontRentAmt
	 * @param keyMonExstsYn
	 * @param keyMonAmt
	 * @param prmmAmt
	 * @param cmpltExpctDate
	 * @param addr
	 * @param dtlAddr
	 * @param addrShortNm
	 * @param lat
	 * @param lng
	 * @param mapDispYn
	 * @param tmpAddrYn
	 * @param unregistYn
	 * @param prvArea
	 * @param lndArea
	 * @param totFlrArea
	 * @return
	 * @throws Exception
	 */
	public long registerMembersProperty( long memSeqno, String prptRegNo, String estateTypGbCd, String estateTypCd, String tranTypGbCd, long dealAmt, String dealAmtDiscsnPsblYn, long dpstAmt, int montRentAmt, String existngLeaseExstsYn, long crntDpstAmt, int crntMontRentAmt,
			String keyMonExstsYn, long keyMonAmt, long prmmAmt, String cmpltExpctDate, String addr, String dtlAddr, String addrShortNm, double lat, double lng, String mapDispYn, String tmpAddrYn, String unregistYn, double prvArea, double lndArea, double totFlrArea
	)throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setPrptRegNo( prptRegNo );
		propertyVO.setEstateTypGbCd( estateTypGbCd );
		propertyVO.setEstateTypCd( estateTypCd );
		propertyVO.setTranTypGbCd( tranTypGbCd );
		propertyVO.setDealAmt( dealAmt );
		propertyVO.setDealAmtDiscsnPsblYn( dealAmtDiscsnPsblYn );
		propertyVO.setDpstAmt( dpstAmt );
		propertyVO.setMontRentAmt( montRentAmt );
		propertyVO.setExistngLeaseExstsYn( existngLeaseExstsYn );
		propertyVO.setCrntDpstAmt( crntDpstAmt );
		propertyVO.setCrntMontRentAmt( crntMontRentAmt );
		propertyVO.setKeyMonExstsYn( keyMonExstsYn );
		propertyVO.setKeyMonAmt( keyMonAmt );
		propertyVO.setPrmmAmt( prmmAmt );
		propertyVO.setCmpltExpctDate( cmpltExpctDate );
		propertyVO.setAddr( GsntalkXSSUtil.encodeXss( GsntalkUtil.addressTopLvlReplace( addr ) ) );
		propertyVO.setDtlAddr( GsntalkXSSUtil.encodeXss( dtlAddr ) );
		propertyVO.setAddrShortNm( addrShortNm );
		propertyVO.setLat( lat );
		propertyVO.setLng( lng );
		propertyVO.setMapDispYn( mapDispYn );
		propertyVO.setTmpAddrYn( tmpAddrYn );
		propertyVO.setUnregistYn( unregistYn );
		propertyVO.setPrvArea( prvArea );
		propertyVO.setLndArea( lndArea );
		propertyVO.setTotFlrArea( totFlrArea );
		propertyVO.setRegMemSeqno( memSeqno );
		
		sqlSession.insert( "PropertyMapper.registerMembersProperty", propertyVO );
		return propertyVO.getPrptSeqno();
	}
	
	/**
	 * 일반회원 - 내매물 목록조회 ( 페이징 )
	 * 
	 * @param memSeqno
	 * @param regStatGbCd
	 * @param tranTypGbCd
	 * @param estateTypCd
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getMyPropertyList( long memSeqno, String regStatGbCd, String tranTypGbCd, String estateTypCd, String srchVal, int stRnum, int edRnum )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setRegStatGbCd( regStatGbCd );
		propertyVO.setTranTypGbCd( tranTypGbCd );
		propertyVO.setEstateTypCd( estateTypCd );
		propertyVO.setSrchVal( srchVal );
		propertyVO.setStRnum( stRnum );
		propertyVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "PropertyMapper.getMyPropertyList", propertyVO );
	}
	
	/**
	 * 일반회원 내 매물 유효성 검증 ( 삭제된 매물 포함 )
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getMyPropertyInfo( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.getMyPropertyInfo", propertyVO );
	}
	
	/**
	 * 일반회원 내 매물 삭제
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void deleteMyPropertyItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		sqlSession.update( "PropertyMapper.deleteMyPropertyItem", propertyVO );
	}
	
	/**
	 * Admin - 일반회원 매물 목록조회 ( 페이징 )
	 * 
	 * @param regDtSrchTyp
	 * @param dealStatGbCdItems
	 * @param tranTypGbCdItems
	 * @param treatStatGbCd
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getMembersPropertyList( String regDtSrchTyp, JSONArray dealStatGbCdItems, JSONArray tranTypGbCdItems, String treatStatGbCd, String srchVal, int stRnum, int edRnum )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setSrchDateType( regDtSrchTyp );
		propertyVO.setDealStatGbCdItems( dealStatGbCdItems );
		propertyVO.setTranTypGbCdItems( tranTypGbCdItems );
		propertyVO.setTreatStatGbCd( treatStatGbCd );
		propertyVO.setSrchVal( srchVal );
		propertyVO.setStRnum( stRnum );
		propertyVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "PropertyMapper.getMembersPropertyList", propertyVO );
	}
	
	/**
	 * Admin - 일반회원 매물 목록조회 ( 엑셀 다운용 )
	 * 
	 * @param regDtSrchTyp
	 * @param dealStatGbCd
	 * @param tranTypGbCd
	 * @param treatStatGbCd
	 * @param srchVal	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getMembersPropertyExcelDownloadList( String regDtSrchTyp, String dealStatGbCd, String tranTypGbCd, String treatStatGbCd, String srchVal )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setSrchDateType( regDtSrchTyp );
		propertyVO.setDealStatGbCd( dealStatGbCd );
		propertyVO.setTranTypGbCd( tranTypGbCd );
		propertyVO.setTreatStatGbCd( treatStatGbCd );
		propertyVO.setSrchVal( srchVal );
		
		return sqlSession.selectList( "PropertyMapper.getMembersPropertyExcelDownloadList", propertyVO );
	}
	
	/**
	 * Admin - 일반회원 매물 삭제
	 * 
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void deleteMembersPropertyItem( long prptSeqno )throws Exception {
		sqlSession.update( "PropertyMapper.deleteMembersPropertyItem", prptSeqno );
	}
	
	/**
	 * Admin - 일반회원/중개회원 매물 상세정보 조회( 수정용 )
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getMembersPropertyDtlItem( long prptSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getMembersPropertyDtlItem", prptSeqno );
	}
	
	/**
	 * Admin - 중개사 매물 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param dealStatGbCdItems
	 * @param tranTypGbCdItems
	 * @param regiEstTypCdItems
	 * @param commEstTypCdItems
	 * @param preEstTypCdItems
	 * @param prptStatGbCd
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getEstBrkPrptItems( String srchVal, JSONArray dealStatGbCdItems, JSONArray tranTypGbCdItems, JSONArray regiEstTypCdItems, JSONArray commEstTypCdItems, JSONArray preEstTypCdItems, String prptStatGbCd, int stRnum, int edRnum )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setSrchVal( srchVal );
		propertyVO.setDealStatGbCdItems( dealStatGbCdItems );
		propertyVO.setTranTypGbCdItems( tranTypGbCdItems );
		propertyVO.setRegiEstTypCdItems( regiEstTypCdItems );
		propertyVO.setCommEstTypCdItems( commEstTypCdItems );
		propertyVO.setPreEstTypCdItems( preEstTypCdItems );
		propertyVO.setPrptStatGbCd( prptStatGbCd );
		propertyVO.setStRnum( stRnum );
		propertyVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "PropertyMapper.getEstBrkPrptItems", propertyVO );
	}
	
	/**
	 * Admin - 중개사 매물 목록조회 ( 엑셀 다운용 )
	 * 
	 * @param srchVal
	 * @param dealStatGbCdItems
	 * @param tranTypGbCdItems
	 * @param regiEstTypCdItems
	 * @param commEstTypCdItems
	 * @param preEstTypCdItems
	 * @param prptStatGbCd
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getEstBrkPrptDownloadList( String srchVal, JSONArray dealStatGbCdItems, JSONArray tranTypGbCdItems, JSONArray regiEstTypCdItems, JSONArray commEstTypCdItems, JSONArray preEstTypCdItems, String prptStatGbCd )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setSrchVal( srchVal );
		propertyVO.setDealStatGbCdItems( dealStatGbCdItems );
		propertyVO.setTranTypGbCdItems( tranTypGbCdItems );
		propertyVO.setRegiEstTypCdItems( regiEstTypCdItems );
		propertyVO.setCommEstTypCdItems( commEstTypCdItems );
		propertyVO.setPreEstTypCdItems( preEstTypCdItems );
		propertyVO.setPrptStatGbCd( prptStatGbCd );
		
		return sqlSession.selectList( "PropertyMapper.getEstBrkPrptDownloadList", propertyVO );
	}
	
	/**
	 * 매물제안요청 등록
	 * 
	 * @param memSeqno
	 * @param estateTypCd
	 * @param tranTypGbCd
	 * @param wishArea
	 * @param sectrGbCd
	 * @param usePplCnt
	 * @param psblMovDayTypCd
	 * @param psblMovStDate
	 * @param psblMovEdDate
	 * @param dealAmt
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param clientNm
	 * @param compNm
	 * @param suggstSectrCd
	 * @param wishFlrTypCd
	 * @param intrrYn
	 * @param reqDscr
	 * @return
	 * @throws Exception
	 */
	public long registerPropertySuggestRequest( long memSeqno, String estateTypCd, String tranTypGbCd, double wishArea, String sectrGbCd, int usePplCnt, String psblMovDayTypCd, String psblMovStDate, String psblMovEdDate,
			long dealAmt, long dpstAmt, int montRentAmt, String clientNm, String compNm, String suggstSectrCd, String wishFlrTypCd, String intrrYn, String reqDscr )throws Exception {
		
		PropertySuggestRequestVO propertySuggestRequestVO = new PropertySuggestRequestVO();
		propertySuggestRequestVO.setMemSeqno( memSeqno );
		propertySuggestRequestVO.setEstateTypCd( estateTypCd );
		propertySuggestRequestVO.setTranTypGbCd( tranTypGbCd );
		propertySuggestRequestVO.setWishArea( wishArea );
		propertySuggestRequestVO.setSectrGbCd( sectrGbCd );
		propertySuggestRequestVO.setUsePplCnt( usePplCnt );
		propertySuggestRequestVO.setPsblMovDayTypCd( psblMovDayTypCd );
		propertySuggestRequestVO.setPsblMovStDate( psblMovStDate );
		propertySuggestRequestVO.setPsblMovEdDate( psblMovEdDate );
		propertySuggestRequestVO.setDealAmt( dealAmt );
		propertySuggestRequestVO.setDpstAmt( dpstAmt );
		propertySuggestRequestVO.setMontRentAmt( montRentAmt );
		propertySuggestRequestVO.setClientNm( clientNm );
		propertySuggestRequestVO.setCompNm( compNm );
		propertySuggestRequestVO.setSuggstSectrCd( suggstSectrCd );
		propertySuggestRequestVO.setWishFlrTypCd( wishFlrTypCd );
		propertySuggestRequestVO.setIntrrYn( intrrYn );
		propertySuggestRequestVO.setReqDscr( reqDscr );
		
		sqlSession.insert( "PropertyMapper.registerPropertySuggestRequest", propertySuggestRequestVO );
		return propertySuggestRequestVO.getPrptSuggstReqSeqno();
	}
	
	/**
	 * 매물제안요청지역 등록
	 * 
	 * @param prptSuggstReqSeqno
	 * @param regionNm
	 * @throws Exception
	 */
	public void registerPropertySuggestRegion( long prptSuggstReqSeqno, String regionNm )throws Exception {
		PropertySuggestRequestVO propertySuggestRequestVO = new PropertySuggestRequestVO();
		propertySuggestRequestVO.setPrptSuggstReqSeqno( prptSuggstReqSeqno );
		propertySuggestRequestVO.setRegionNm( regionNm );
		
		sqlSession.insert( "PropertyMapper.registerPropertySuggestRegion", propertySuggestRequestVO );
	}
	
	/**
	 * 매물제안 매칭 매물조회 및 제안등록처리
	 * 
	 * @param prptSuggstReqSeqno
	 * @param suggstRegionItems
	 * @throws Exception
	 */
	public void registerMatchingPropertySuggest( long prptSuggstReqSeqno, JSONArray suggstRegionItems )throws Exception {
		PropertySuggestRequestVO propertySuggestRequestVO = new PropertySuggestRequestVO();
		propertySuggestRequestVO.setPrptSuggstReqSeqno( prptSuggstReqSeqno );
		propertySuggestRequestVO.setSuggstRegionItems( suggstRegionItems );
		
		sqlSession.insert( "PropertyMapper.registerMatchingPropertySuggest", propertySuggestRequestVO );
	}
	
	/**
	 * FRT - 내 매물 제안서 목록조회 ( 일반/중개회원 공통 )
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public List<PropertySuggestRequestVO> getMyPrptSuggstItems( long memSeqno )throws Exception {
		return sqlSession.selectList( "PropertyMapper.getMyPrptSuggstItems", memSeqno );
	}
	
	/**
	 * 매물제안 요청지역 목록조회
	 * 
	 * @param prptSuggstReqSeqno
	 * @return
	 * @throws Exception
	 */
	public List<String> getSuggstRegionNmList( long prptSuggstReqSeqno )throws Exception {
		return sqlSession.selectList( "PropertyMapper.getSuggstRegionNmList", prptSuggstReqSeqno );
	}
	
	/**
	 * FRT - 내 제안 요청정보 조회
	 * 
	 * @param prptSuggstReqSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertySuggestRequestVO getMyPropertySuggestRequestVO( long memSeqno, long prptSuggstReqSeqno )throws Exception {
		PropertySuggestRequestVO propertySuggestRequestVO = new PropertySuggestRequestVO();
		propertySuggestRequestVO.setMemSeqno( memSeqno );
		propertySuggestRequestVO.setPrptSuggstReqSeqno( prptSuggstReqSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.getMyPropertySuggestRequestVO", propertySuggestRequestVO );
	}
	
	/**
	 * 제안자 정보 조회
	 * 
	 * @param suggstMemTypCd
	 * @param suggstMemSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertySuggestRequestVO getMyPropertyRequestSuggesterInfo( String suggstMemTypCd, long suggstMemSeqno )throws Exception {
		PropertySuggestRequestVO propertySuggestRequestVO = new PropertySuggestRequestVO();
		propertySuggestRequestVO.setSuggstMemTypCd( suggstMemTypCd );
		propertySuggestRequestVO.setSuggstMemSeqno( suggstMemSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.getMyPropertyRequestSuggesterInfo", propertySuggestRequestVO );
	}
	
	/**
	 * 제안받은 매물 목록조회
	 * 
	 * @param memSeqno
	 * @param prptSuggstReqSeqno
	 * @param suggstMemTypCd
	 * @param suggstMemSeqno
	 * @return
	 * @throws Exception
	 */
	public List<PropertyVO> getSuggestRequestPropertyList( long memSeqno, long prptSuggstReqSeqno, String suggstMemTypCd, long suggstMemSeqno )throws Exception {
		PropertySuggestRequestVO propertySuggestRequestVO = new PropertySuggestRequestVO();
		propertySuggestRequestVO.setMemSeqno( memSeqno );
		propertySuggestRequestVO.setPrptSuggstReqSeqno( prptSuggstReqSeqno );
		propertySuggestRequestVO.setSuggstMemTypCd( suggstMemTypCd );
		propertySuggestRequestVO.setSuggstMemSeqno( suggstMemSeqno );
		
		return sqlSession.selectList( "PropertyMapper.getSuggestRequestPropertyList", propertySuggestRequestVO );
	}
	
	/**
	 * Admin - 매물 제안요청 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<PropertySuggestRequestVO> getPrptSuggstReqItems( String srchVal, int stRnum, int edRnum )throws Exception {
		PropertySuggestRequestVO propertyVO = new PropertySuggestRequestVO();
		propertyVO.setSrchVal( srchVal );
		propertyVO.setSrchValEnc( GsntalkEncryptor.encrypt( srchVal ) );
		propertyVO.setStRnum( stRnum );
		propertyVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "PropertyMapper.getPrptSuggstReqItems", propertyVO );
	}
	
	/**
	 * 투어요청 검증정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public PropertyVO getPrptTourValidationInfo( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		return sqlSession.selectOne( "PropertyMapper.getPrptTourValidationInfo", propertyVO );
	}
	
	/**
	 * 투어 요청 등록
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void registerTourRequest( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = new PropertyVO();
		propertyVO.setMemSeqno( memSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		sqlSession.insert( "PropertyMapper.registerTourRequest", propertyVO );
	}
	
	/**
	 * 매물제안 삭제대상 매물시퀀스 목록조회
	 * 
	 * @param prptSuggstReqSeqno
	 * @param suggstMemTypCd
	 * @param suggstMemSeqno
	 * @return
	 * @throws Exception
	 */
	public List<Long> getDeletePropertySuggestPrptSeqnoList( long prptSuggstReqSeqno, String suggstMemTypCd, long suggstMemSeqno )throws Exception {
		PropertySuggestRequestVO propertyVO = new PropertySuggestRequestVO();
		propertyVO.setPrptSuggstReqSeqno( prptSuggstReqSeqno );
		propertyVO.setSuggstMemTypCd( suggstMemTypCd );
		propertyVO.setSuggstMemSeqno( suggstMemSeqno );
		
		return sqlSession.selectList( "PropertyMapper.getDeletePropertySuggestPrptSeqnoList", propertyVO );
	}
	
	/**
	 * FRT - 내 매물 제안서 삭제 ( 일반/중개회원 공통 )
	 * 
	 * @param prptSuggstReqSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void deletePropertySuggest( long prptSuggstReqSeqno, long prptSeqno )throws Exception {
		PropertySuggestRequestVO propertyVO = new PropertySuggestRequestVO();
		propertyVO.setPrptSuggstReqSeqno( prptSuggstReqSeqno );
		propertyVO.setPrptSeqno( prptSeqno );
		
		sqlSession.delete( "PropertyMapper.deletePropertySuggest", propertyVO );
	}
	
	/**
	 * 관리자 확인여부 조회
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	public String getAdminCnfrmYn( long prptSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getAdminCnfrmYn", prptSeqno );
	}
	
	/**
	 * 매물제안 회원 상세정보 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public MemberVO getSuggstnPrptMemDtlItem( long memSeqno )throws Exception {
		return sqlSession.selectOne( "PropertyMapper.getSuggstnPrptMemDtlItem", memSeqno );
	}
}