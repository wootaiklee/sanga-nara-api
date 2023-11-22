package com.gsntalk.api.apis.compproposal;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.CompanyProposalVO;
import com.gsntalk.api.common.vo.RegistrationTmpDataStepVO;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Repository( "com.gsntalk.api.apis.compproposal.CompanyProposalDAO" )
public class CompanyProposalDAO extends CommonDAO {

	public CompanyProposalDAO() {
		super( CompanyProposalDAO.class );
	}
	
	/**
	 * 기업명 중복검증
	 * 
	 * @param memSeqno
	 * @param denyCompSeqno
	 * @param compNm
	 * @return
	 * @throws Exception
	 */
	public int isExistsCompNm( long estBrkMemOfcSeqno, long denyCompSeqno, String compNm )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( denyCompSeqno );
		companyProposalVO.setCompNm( GsntalkXSSUtil.encodeXss( compNm ) );
		
		return sqlSession.selectOne( "CompanyProposalMapper.isExistsCompNm", companyProposalVO );
	}
	
	/**
	 * 신규기업 등록
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compNm
	 * @param estBrkDispPosNm
	 * @throws Exception
	 */
	public void registerNewCompanyItem( long estBrkMemOfcSeqno, String compNm, String estBrkDispPosNm )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompNm( GsntalkXSSUtil.encodeXss( compNm ) );
		companyProposalVO.setEstBrkDispPosNm( GsntalkXSSUtil.encodeXss( estBrkDispPosNm ) );
		
		sqlSession.insert( "CompanyProposalMapper.registerNewCompanyItem", companyProposalVO );
	}
	
	/**
	 * 기업 유효성 검증
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @return
	 * @throws Exception
	 */
	public int isUsersComp( long estBrkMemOfcSeqno, long compSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( compSeqno );
		
		return sqlSession.selectOne( "CompanyProposalMapper.isUsersComp", companyProposalVO );
	}
	
	/**
	 * 기업명 수정
	 * 
	 * @param compSeqno
	 * @param compNm
	 * @throws Exception
	 */
	public void updateCompNm( long compSeqno, String compNm )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setCompSeqno( compSeqno );
		companyProposalVO.setCompNm( GsntalkXSSUtil.encodeXss( compNm ) );
		
		sqlSession.update( "CompanyProposalMapper.updateCompNm", companyProposalVO );
	}
	
	/**
	 * 기업 삭제
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @throws Exception
	 */
	public void deleteCompanyItem( long estBrkMemOfcSeqno, long compSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( compSeqno );
		
		sqlSession.update( "CompanyProposalMapper.deleteCompanyItem", companyProposalVO );
	}
	
	/**
	 * 기업 이전제안 매물 삭제
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @throws Exception
	 */
	public void deleteCompanyPrpslPrptItems( long estBrkMemOfcSeqno, long compSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( compSeqno );
		
		sqlSession.update( "CompanyProposalMapper.deleteCompanyPrpslPrptItems", companyProposalVO );
	}
	
	/**
	 * 기업 목록 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @return
	 * @throws Exception
	 */
	public List<CompanyProposalVO> getCompItems( long estBrkMemOfcSeqno )throws Exception {
		return sqlSession.selectList( "CompanyProposalMapper.getCompItems", estBrkMemOfcSeqno );
	}
	
	/**
	 * 기업이전 제안매물 임시 1단계 정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSuggstReqSeqno
	 * @return
	 * @throws Exception
	 */
	public String getRegTmpKeyOfCompMovPrpslPrptStep1( long memSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setMemSeqno( memSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		
		return sqlSession.selectOne( "CompanyProposalMapper.getRegTmpKeyOfCompMovPrpslPrptStep1", companyProposalVO );
	}
	
	/**
	 * 기업이전제안매물 1단계정보 업데이트
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param movPrpslPrptSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tmpAddrYn
	 * @param addr
	 * @param roadAddr
	 * @param grpAddr
	 * @param bldNm
	 * @param lat
	 * @param lng
	 * @throws Exception
	 */
	public void updateCompMovPrpslPrptStep1Data( long estBrkMemOfcSeqno, long movPrpslPrptSeqno, String estateTypGbCd, String estateTypCd, String tmpAddrYn, String addr, String roadAddr, String grpAddr, String bldNm, double lat, double lng )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		companyProposalVO.setEstateTypGbCd( estateTypGbCd );
		companyProposalVO.setEstateTypCd( estateTypCd );
		companyProposalVO.setTmpAddrYn( tmpAddrYn );
		companyProposalVO.setAddr( addr );
		companyProposalVO.setRoadAddr( roadAddr );
		companyProposalVO.setGrpAddr( grpAddr );
		companyProposalVO.setBldNm( bldNm );
		companyProposalVO.setLat( lat );
		companyProposalVO.setLng( lng );
		
		sqlSession.update( "CompanyProposalMapper.updateCompMovPrpslPrptStep1Data", companyProposalVO );
	}
	
	/**
	 * 이전제안 건물(매물) 1단계 수정용 정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	public CompanyProposalVO getCompMovPrpslPrptStep1Data( long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( compSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
	
		return sqlSession.selectOne( "CompanyProposalMapper.getCompMovPrpslPrptStep1Data", companyProposalVO ); 
	}
	
	/**
	 * 기업이전제안매물 2단계정보 업데이트
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param movPrpslPrptSeqno
	 * @param tranTypGbCd
	 * @param salesCost
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param prmmAmt
	 * @param acqstnTaxRatio
	 * @param supprtAmt
	 * @param etcCost
	 * @param loanRatio1
	 * @param loanRatio2
	 * @param loanIntrRatio
	 * @param investYn
	 * @param investDpstAmt
	 * @param investMontRentAmt
	 * @throws Exception
	 */
	public void updateCompMovPrpslPrptStep2Data( long estBrkMemOfcSeqno, long movPrpslPrptSeqno, String tranTypGbCd, long salesCost, long dpstAmt, int montRentAmt, long prmmAmt, double acqstnTaxRatio, long supprtAmt, long etcCost,
			double loanRatio1, double loanRatio2, double loanIntrRatio, String investYn, long investDpstAmt, int investMontRentAmt )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		companyProposalVO.setTranTypGbCd( tranTypGbCd );
		companyProposalVO.setSalesCost( salesCost );
		companyProposalVO.setDpstAmt( dpstAmt );
		companyProposalVO.setMontRentAmt( montRentAmt );
		companyProposalVO.setPrmmAmt( prmmAmt );
		companyProposalVO.setAcqstnTaxRatio( acqstnTaxRatio );
		companyProposalVO.setSupprtAmt( supprtAmt );
		companyProposalVO.setEtcCost( etcCost );
		companyProposalVO.setLoanRatio1( loanRatio1 );
		companyProposalVO.setLoanRatio2( loanRatio2 );
		companyProposalVO.setLoanIntrRatio( loanIntrRatio );
		companyProposalVO.setInvestYn( investYn );
		companyProposalVO.setInvestDpstAmt( investDpstAmt );
		companyProposalVO.setInvestMontRentAmt( investMontRentAmt );
		
		sqlSession.update( "CompanyProposalMapper.updateCompMovPrpslPrptStep2Data", companyProposalVO );
	}
	
	/**
	 * 이전제안 건물(매물) 2단계 수정용 정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	public CompanyProposalVO getCompMovPrpslPrptStep2Data( long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( compSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
	
		return sqlSession.selectOne( "CompanyProposalMapper.getCompMovPrpslPrptStep2Data", companyProposalVO ); 
	}
	
	/**
	 * 기업이전제안 매물(건물) 등록
	 * 
	 * @param compSeqno
	 * @param estBrkMemOfcSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tranTypGbCd
	 * @param bldNm
	 * @param tmpAddrYn
	 * @param addr
	 * @param roadAddr
	 * @param grpAddr
	 * @param lat
	 * @param lng
	 * @param salesCost
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param prmmAmt
	 * @param acqstnTaxRatio
	 * @param supprtAmt
	 * @param etcCost
	 * @param loanRatio1
	 * @param loanRatio2
	 * @param loanIntrRatio
	 * @param investYn
	 * @param investDpstAmt
	 * @param investMontRentAmt
	 * @param prvArea
	 * @param flr
	 * @param allFlr
	 * @param monMntnceCost
	 * @param psblMovDayTypCd
	 * @param psblMovDate
	 * @param heatKindGbCd
	 * @param parkingCarCnt
	 * @param bldSpclAdvtgDscr
	 * @param reqDscr
	 * @return
	 * @throws Exception
	 */
	public long registerCompMovPrpslPrpt( long compSeqno, long estBrkMemOfcSeqno, String estateTypGbCd, String estateTypCd, String tranTypGbCd, String bldNm, String tmpAddrYn, String addr, String roadAddr, String grpAddr, double lat, double lng,
			long salesCost, long dpstAmt, int montRentAmt, long prmmAmt, double acqstnTaxRatio, long supprtAmt, long etcCost, double loanRatio1, double loanRatio2, double loanIntrRatio, String investYn, long investDpstAmt, int investMontRentAmt,
			double prvArea, int flr, int allFlr, int monMntnceCost, String psblMovDayTypCd, String psblMovDate, String heatKindGbCd, int parkingCarCnt, String bldSpclAdvtgDscr, String reqDscr )throws Exception {
		
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setCompSeqno( compSeqno );
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setEstateTypGbCd( estateTypGbCd );
		companyProposalVO.setEstateTypCd( estateTypCd );
		companyProposalVO.setTranTypGbCd( tranTypGbCd );
		companyProposalVO.setBldNm( bldNm );
		companyProposalVO.setTmpAddrYn( tmpAddrYn );
		companyProposalVO.setAddr( addr );
		companyProposalVO.setRoadAddr( roadAddr );
		companyProposalVO.setLat( lat );
		companyProposalVO.setLng( lng );
		companyProposalVO.setSalesCost( salesCost );
		companyProposalVO.setDpstAmt( dpstAmt );
		companyProposalVO.setMontRentAmt( montRentAmt );
		companyProposalVO.setPrmmAmt( prmmAmt );
		companyProposalVO.setAcqstnTaxRatio( acqstnTaxRatio );
		companyProposalVO.setSupprtAmt( supprtAmt );
		companyProposalVO.setEtcCost( etcCost );
		companyProposalVO.setLoanRatio1( loanRatio1 );
		companyProposalVO.setLoanRatio2( loanRatio2 );
		companyProposalVO.setLoanIntrRatio( loanIntrRatio );
		companyProposalVO.setInvestYn( investYn );
		companyProposalVO.setInvestDpstAmt( investDpstAmt );
		companyProposalVO.setInvestMontRentAmt( investMontRentAmt );
		companyProposalVO.setPrvArea( prvArea );
		companyProposalVO.setFlr( flr );
		companyProposalVO.setAllFlr( allFlr );
		companyProposalVO.setMonMntnceCost( monMntnceCost );
		companyProposalVO.setPsblMovDayTypCd( psblMovDayTypCd );
		companyProposalVO.setPsblMovDate( psblMovDate );
		companyProposalVO.setHeatKindGbCd( heatKindGbCd );
		companyProposalVO.setParkingCarCnt( parkingCarCnt );
		companyProposalVO.setBldSpclAdvtgDscr( bldSpclAdvtgDscr );
		companyProposalVO.setReqDscr( reqDscr );
		
		
		sqlSession.insert( "CompanyProposalMapper.registerCompMovPrpslPrpt", companyProposalVO );
		return companyProposalVO.getMovPrpslPrptSeqno();
	}
	
	/**
	 * 기업이전제안매물 최종(3)단계정보 업데이트
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param movPrpslPrptSeqno
	 * @param prvArea
	 * @param flr
	 * @param allFlr
	 * @param monMntnceCost
	 * @param psblMovDayTypCd
	 * @param psblMovDate
	 * @param heatKindGbCd
	 * @param parkingCarCnt
	 * @param bldSpclAdvtgDscr
	 * @param reqDscr
	 * @throws Exception
	 */
	public void updateCompMovPrpslPrptFinalStepData( long estBrkMemOfcSeqno, long movPrpslPrptSeqno, double prvArea, int flr, int allFlr, int monMntnceCost, String psblMovDayTypCd, String psblMovDate, String heatKindGbCd, int parkingCarCnt, String bldSpclAdvtgDscr, String reqDscr )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		companyProposalVO.setPrvArea( prvArea );
		companyProposalVO.setFlr( flr );
		companyProposalVO.setAllFlr( allFlr );
		companyProposalVO.setMonMntnceCost( monMntnceCost );
		companyProposalVO.setPsblMovDayTypCd( psblMovDayTypCd );
		companyProposalVO.setPsblMovDate( psblMovDate );
		companyProposalVO.setHeatKindGbCd( heatKindGbCd );
		companyProposalVO.setParkingCarCnt( parkingCarCnt );
		companyProposalVO.setBldSpclAdvtgDscr( bldSpclAdvtgDscr );
		companyProposalVO.setReqDscr( reqDscr );
		
		sqlSession.update( "CompanyProposalMapper.updateCompMovPrpslPrptFinalStepData", companyProposalVO );
	}
	
	/**
	 * 등록단계별임시정보 이전제안매물시퀀스 등록
	 * 
	 * @param regTmpKey
	 * @param movPrpslPrptSeqno
	 * @throws Exception
	 */
	public void updateTempDataMovPrpslPrptSeqno( String regTmpKey, long movPrpslPrptSeqno )throws Exception {
		RegistrationTmpDataStepVO registrationTmpDataStepVO = new RegistrationTmpDataStepVO();
		registrationTmpDataStepVO.setRegTmpKey( regTmpKey );
		registrationTmpDataStepVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		
		sqlSession.update( "CompanyProposalMapper.updateTempDataMovPrpslPrptSeqno", registrationTmpDataStepVO );
	}
	
	/**
	 * 건물시설 유형코드 전체 삭제
	 * 
	 * @param movPrpslPrptSeqno
	 * @throws Exception
	 */
	public void clearCompMovPrpslPrptFacTyps( long movPrpslPrptSeqno )throws Exception {
		sqlSession.delete( "CompanyProposalMapper.clearCompMovPrpslPrptFacTyps", movPrpslPrptSeqno );
	}
	
	/**
	 * 건물시설 유형코드 등록
	 * 
	 * @param movPrpslPrptSeqno
	 * @param compPrpslBldFacTypCd
	 * @throws Exception
	 */
	public void registerCompMovPrpslPrptFacTyps( long movPrpslPrptSeqno, String compPrpslBldFacTypCd )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		companyProposalVO.setCompPrpslBldFacTypCd( compPrpslBldFacTypCd );
		
		sqlSession.insert( "CompanyProposalMapper.registerCompMovPrpslPrptFacTyps", companyProposalVO );
	}
	
	/**
	 * 기업이전제안 매물 사진등록
	 * 
	 * @param movPrpslPrptSeqno
	 * @param sortSerl
	 * @param saveFileNm
	 * @param fileUrl
	 * @param photoGb
	 * @throws Exception
	 */
	public void registerCompMovPrpslPrptPhoto( long movPrpslPrptSeqno, double sortSerl, String saveFileNm, String fileUrl, String photoGb )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		companyProposalVO.setSortSerl( sortSerl );
		companyProposalVO.setSaveFileNm( saveFileNm );
		companyProposalVO.setFileUrl( fileUrl );
		companyProposalVO.setPhotoGb( photoGb );
		
		sqlSession.insert( "CompanyProposalMapper.registerCompMovPrpslPrptPhoto", companyProposalVO );
	}
	
	/**
	 * 기업이전제안 매물 외관사진 삭제
	 * 
	 * @param movPrpslPrptSeqno
	 * @throws Exception
	 */
	public void deleteCompMovPrpslPrptOuterPhoto( long movPrpslPrptSeqno )throws Exception {
		sqlSession.update( "CompanyProposalMapper.deleteCompMovPrpslPrptOuterPhoto", movPrpslPrptSeqno );
	}
	
	/**
	 * 기업이전제안 매물 내부사진 삭제
	 * 
	 * @param movPrpslPrptSeqno
	 * @param saveFileNm
	 * @throws Exception
	 */
	public void deleteCompMovPrpslPrptInnterPhoto( long movPrpslPrptSeqno, String saveFileNm )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		companyProposalVO.setSaveFileNm( saveFileNm );
		
		sqlSession.update( "CompanyProposalMapper.deleteCompMovPrpslPrptInnterPhoto", companyProposalVO );
	}
	
	/**
	 * 기업이전제안 매물 내부사진 정렬순서 변경
	 * 
	 * @param movPrpslPrptSeqno
	 * @param sortSerl
	 * @param saveFileNm
	 * @throws Exception
	 */
	public void updateCompMovPrpslPrptInnterPhotoSortSerl( long movPrpslPrptSeqno, double sortSerl, String saveFileNm )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		companyProposalVO.setSortSerl( sortSerl );
		companyProposalVO.setSaveFileNm( saveFileNm );
		
		sqlSession.update( "CompanyProposalMapper.updateCompMovPrpslPrptInnterPhotoSortSerl", companyProposalVO );
	}
	
	/**
	 * 이전제안 건물(매물) 최종단계 수정용 정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	public CompanyProposalVO getCompMovPrpslPrptFinalStepData( long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( compSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
	
		return sqlSession.selectOne( "CompanyProposalMapper.getCompMovPrpslPrptFinalStepData", companyProposalVO ); 
	}
	
	/**
	 * 기업이전제안 매물 건물시설 유형코드 목록조회
	 * 
	 * @param movPrpslPrptSeqno
	 * @return
	 */
	public List<String> getCompPrpslBldFacTypList( long movPrpslPrptSeqno ){
		return sqlSession.selectList( "CompanyProposalMapper.getCompPrpslBldFacTypList", movPrpslPrptSeqno );
	}
	
	/**
	 * 기업이전제안 매물사진 목록조회
	 * 
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	public List<CompanyProposalVO> getCompMovPrpslPrptPhotoList( long movPrpslPrptSeqno )throws Exception {
		return sqlSession.selectList( "CompanyProposalMapper.getCompMovPrpslPrptPhotoList", movPrpslPrptSeqno );
	}
	
	/**
	 * 기업 기본정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @return
	 * @throws Exception
	 */
	public CompanyProposalVO getCompItem( long estBrkMemOfcSeqno, long compSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setCompSeqno( compSeqno );
		
		return sqlSession.selectOne( "CompanyProposalMapper.getCompItem", companyProposalVO ); 
	}
	
	/**
	 * 기업 이전 제안메물 지번주소 그룹핑 목록조회
	 * 
	 * @param compSeqno
	 * @return
	 * @throws Exception
	 */
	public List<String> getCompMovPrpslAddressGroupList( long compSeqno )throws Exception {
		return sqlSession.selectList( "CompanyProposalMapper.getCompMovPrpslAddressGroupList", compSeqno );
	}
	
	/**
	 * 기업이전 제안매물 목록조회
	 * 
	 * @param compSeqno
	 * @param grpAddr
	 * @return
	 * @throws Exception
	 */
	public List<CompanyProposalVO> getCompMovPrpslPrptListOfGrpAddr( long compSeqno, String grpAddr )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setCompSeqno( compSeqno );
		companyProposalVO.setGrpAddr( grpAddr );
		
		return sqlSession.selectList( "CompanyProposalMapper.getCompMovPrpslPrptListOfGrpAddr", companyProposalVO );
	}
	
	/**
	 * 건물 유효성 검증
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	public int isUsersBld( long estBrkMemOfcSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		
		return sqlSession.selectOne( "CompanyProposalMapper.getCompItem", companyProposalVO );
	}
	
	/**
	 * 기업이전 제안 건물 삭제
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param movPrpslPrptSeqno
	 * @throws Exception
	 */
	public void deleteCompPrpslBldItem( long estBrkMemOfcSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		companyProposalVO.setMovPrpslPrptSeqno( movPrpslPrptSeqno );
		
		sqlSession.update( "CompanyProposalMapper.deleteCompPrpslBldItem", companyProposalVO );
	}
	
	/**
	 * Admin - 회원별 기업이전 제안서 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<CompanyProposalVO> getMemCompPrpslItems( String srchVal, int stRnum, int edRnum )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setSrchVal( srchVal );
		companyProposalVO.setStRnum( stRnum );
		companyProposalVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "CompanyProposalMapper.getMemCompPrpslItems", companyProposalVO );
	}
	
	/**
	 * 기업명 조회
	 * 
	 * @param compSeqno
	 * @return
	 * @throws Exception
	 */
	public String getCompNm( long compSeqno )throws Exception {
		return sqlSession.selectOne( "CompanyProposalMapper.getCompNm", compSeqno );
	}
	
	/**
	 * 기업 이전 제안메물 지번주소 그룹핑 목록조회 ( 최대 6건 )
	 * 
	 * @param compSeqno
	 * @return
	 * @throws Exception
	 */
	public List<String> getCompMovPrpslAddressGroup6List( long compSeqno )throws Exception {
		return sqlSession.selectList( "CompanyProposalMapper.getCompMovPrpslAddressGroup6List", compSeqno );
	}
	
	/**
	 * 기업이전 제안매물 목록조회 ( 최대 6건 )
	 * 
	 * @param compSeqno
	 * @param grpAddr
	 * @return
	 * @throws Exception
	 */
	public List<CompanyProposalVO> getCompMovPrpslPrpt6ListOfGrpAddr( long compSeqno, String grpAddr )throws Exception {
		CompanyProposalVO companyProposalVO = new CompanyProposalVO();
		companyProposalVO.setCompSeqno( compSeqno );
		companyProposalVO.setGrpAddr( grpAddr );
		
		return sqlSession.selectList( "CompanyProposalMapper.getCompMovPrpslPrpt6ListOfGrpAddr", companyProposalVO );
	}
	
	/**
	 * 건물내부사진 목록조회
	 * 
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	public List<String> getCompMovPrpslPrptInnerPhotoList( long movPrpslPrptSeqno )throws Exception {
		return sqlSession.selectList( "CompanyProposalMapper.getCompMovPrpslPrptInnerPhotoList", movPrpslPrptSeqno );
	}
}