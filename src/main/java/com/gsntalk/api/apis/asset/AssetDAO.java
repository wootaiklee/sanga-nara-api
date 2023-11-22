package com.gsntalk.api.apis.asset;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.AssetVO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Repository( "com.gsntalk.api.apis.asset.AssetDAO" )
public class AssetDAO extends CommonDAO {

	public AssetDAO() {
		super( AssetDAO.class );
	}
	
	/**
	 * 자산 기본정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	public AssetVO getAssetVO( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		return sqlSession.selectOne( "AssetMapper.getAssetVO", assetVO );
	}
	
	/**
	 * 자산 임시 1단계 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	public String getRegTmpKeyOfAssetStep1( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		return sqlSession.selectOne( "AssetMapper.getRegTmpKeyOfAssetStep1", assetVO );
	}
	
	/**
	 * 자산 1단계정보 업데이트
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tmpAddrYn
	 * @param unregistYn
	 * @param addr
	 * @param roadAddr
	 * @param dtlAddr
	 * @param lat
	 * @param lng
	 * @param splyArea
	 * @param prvArea
	 * @param inspGbCd
	 * @param bizmanGbCd
	 * @throws Exception
	 */
	public void updateAssetStep1Data( long memSeqno, long assetSeqno, String estateTypGbCd, String estateTypCd, String tmpAddrYn, String unregistYn,
			String addr, String roadAddr, String dtlAddr, double lat, double lng, double splyArea, double prvArea, String inspGbCd, String bizmanGbCd )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		assetVO.setEstateTypGbCd( estateTypGbCd );
		assetVO.setEstateTypCd( estateTypCd );
		assetVO.setTmpAddrYn( tmpAddrYn );
		assetVO.setUnregistYn( unregistYn );
		assetVO.setAddr( addr );
		assetVO.setRoadAddr( roadAddr );
		assetVO.setDtlAddr( dtlAddr );
		assetVO.setLat( lat );
		assetVO.setLng( lng );
		assetVO.setSplyArea( splyArea );
		assetVO.setPrvArea( prvArea );
		assetVO.setInspGbCd( inspGbCd );
		assetVO.setBizmanGbCd( bizmanGbCd );
		
		sqlSession.update( "AssetMapper.updateAssetStep1Data", assetVO );
	}
	
	/**
	 * 자산 1단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	public AssetVO getAssetStep1Data( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		return sqlSession.selectOne( "AssetMapper.getAssetStep1Data", assetVO );
	}
	
	/**
	 * 기존 자산 주소 중복 확인
	 * 
	 * @param memSeqno
	 * @param addr
	 * @return
	 * @throws Exception
	 */
	public int getDuplicatedAssetAddressCnt( long memSeqno, String addr )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAddr( GsntalkXSSUtil.encodeXss( addr ) );
		
		return sqlSession.selectOne( "AssetMapper.getDuplicatedAssetAddressCnt", assetVO );
	}
	
	/**
	 * 자산 2단계정보 업데이트
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param contDate
	 * @param registDate
	 * @param acqstnTaxRatio
	 * @param etcCost
	 * @param taxofcCost
	 * @param estFeeAmt
	 * @param loanAmt
	 * @param loanIntrRatio
	 * @param loanMonTerm
	 * @param loanDate
	 * @param loanMthdGbCd
	 * @throws Exception
	 */
	public void updateAssetStep2Data( long memSeqno, long assetSeqno, String tranTypGbCd, long dealAmt, String contDate, String registDate,
			double acqstnTaxRatio, long etcCost, int taxofcCost, int estFeeAmt, long loanAmt, double loanIntrRatio, int loanMonTerm, String loanDate, String loanMthdGbCd )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		assetVO.setTranTypGbCd( tranTypGbCd );
		assetVO.setDealAmt( dealAmt );
		assetVO.setContDate( contDate );
		assetVO.setRegistDate( registDate );
		assetVO.setAcqstnTaxRatio( acqstnTaxRatio );
		assetVO.setEtcCost( etcCost );
		assetVO.setTaxofcCost( taxofcCost );
		assetVO.setEstFeeAmt( estFeeAmt );
		assetVO.setLoanAmt( loanAmt );
		assetVO.setLoanIntrRatio( loanIntrRatio );
		assetVO.setLoanMonTerm( loanMonTerm );
		assetVO.setLoanDate( loanDate );
		assetVO.setLoanMthdGbCd( loanMthdGbCd );
		
		sqlSession.update( "AssetMapper.updateAssetStep2Data", assetVO );
	}
	
	/**
	 * 자산 2단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	public AssetVO getAssetStep2Data( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		return sqlSession.selectOne( "AssetMapper.getAssetStep2Data", assetVO );
	}
	
	/**
	 * 자산 3단계정보 업데이트
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @param emptyTypGbCd
	 * @param leseeNm
	 * @param leseeTelNo
	 * @param rentAmtPayMthdGbCd
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param monthlyPayDayGbCd
	 * @param rentContStDate
	 * @param rentContEdDate
	 * @throws Exception
	 */
	public void updateAssetStep3Data( long memSeqno, long assetSeqno, String emptyTypGbCd, String leseeNm, String leseeTelNo,
			String rentAmtPayMthdGbCd, long dpstAmt, int montRentAmt, String monthlyPayDayGbCd, String rentContStDate, String rentContEdDate )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		assetVO.setEmptyTypGbCd( emptyTypGbCd );
		assetVO.setLeseeNm( leseeNm );
		assetVO.setLeseeTelNo( leseeTelNo );								// encrypted
		assetVO.setRentAmtPayMthdGbCd( rentAmtPayMthdGbCd );
		assetVO.setDpstAmt( dpstAmt );
		assetVO.setMontRentAmt( montRentAmt );
		assetVO.setMonthlyPayDayGbCd( monthlyPayDayGbCd );
		assetVO.setRentContStDate( rentContStDate );
		assetVO.setRentContEdDate( rentContEdDate );
		
		sqlSession.update( "AssetMapper.updateAssetStep3Data", assetVO );
	}
	
	/**
	 * 자산 3단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	public AssetVO getAssetStep3Data( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		return sqlSession.selectOne( "AssetMapper.getAssetStep3Data", assetVO );
	}
	
	/**
	 * 자산 등록
	 * 
	 * @param memSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tmpAddrYn
	 * @param unregistYn
	 * @param addr
	 * @param roadAddr
	 * @param dtlAddr
	 * @param lat
	 * @param lng
	 * @param splyArea
	 * @param prvArea
	 * @param lndArea
	 * @param totFlrArea
	 * @param inspGbCd
	 * @param bizmanGbCd
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param contDate
	 * @param registDate
	 * @param acqstnTaxRatio
	 * @param etcCost
	 * @param taxofcCost
	 * @param estFeeAmt
	 * @param loanAmt
	 * @param loanIntrRatio
	 * @param loanMonTerm
	 * @param loanDate
	 * @param loanMthdGbCd
	 * @param emptyTypGbCd
	 * @param leseeNm
	 * @param leseeTelNo
	 * @param rentAmtPayMthdGbCd
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param monthlyPayDayGbCd
	 * @param rentContStDate
	 * @param rentContEdDate
	 * @return
	 * @throws Exception
	 */
	public long registerAsset( long memSeqno, String estateTypGbCd, String estateTypCd, String tmpAddrYn, String unregistYn, String addr, String roadAddr, String dtlAddr, double lat, double lng, double splyArea, double prvArea, double lndArea, double totFlrArea, String inspGbCd, String bizmanGbCd,
			String tranTypGbCd, long dealAmt, String contDate, String registDate, double acqstnTaxRatio, long etcCost, int taxofcCost, int estFeeAmt, long loanAmt, double loanIntrRatio, int loanMonTerm, String loanDate, String loanMthdGbCd,
			String emptyTypGbCd, String leseeNm, String leseeTelNo, String rentAmtPayMthdGbCd, long dpstAmt, int montRentAmt, String monthlyPayDayGbCd, String rentContStDate, String rentContEdDate )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setEstateTypGbCd( estateTypGbCd );
		assetVO.setEstateTypCd( estateTypCd );
		assetVO.setTmpAddrYn( tmpAddrYn );
		assetVO.setUnregistYn( unregistYn );
		assetVO.setAddr( addr );									// xss encoded.
		assetVO.setRoadAddr( roadAddr );							// xss encoded.
		assetVO.setDtlAddr( dtlAddr ); 								// xss encoded.
		assetVO.setLat( lat );
		assetVO.setLng( lng );
		assetVO.setSplyArea( splyArea );
		assetVO.setPrvArea( prvArea );
		assetVO.setTotFlrArea( totFlrArea );
		assetVO.setPrvArea( prvArea );
		assetVO.setInspGbCd( inspGbCd );
		assetVO.setBizmanGbCd( bizmanGbCd );
		assetVO.setTranTypGbCd( tranTypGbCd );
		assetVO.setDealAmt( dealAmt );
		assetVO.setContDate( contDate );
		assetVO.setRegistDate( registDate );
		assetVO.setAcqstnTaxRatio( acqstnTaxRatio );
		assetVO.setEtcCost( etcCost );
		assetVO.setTaxofcCost( taxofcCost );
		assetVO.setEstFeeAmt( estFeeAmt );
		assetVO.setLoanAmt( loanAmt );
		assetVO.setLoanIntrRatio( loanIntrRatio );
		assetVO.setLoanMonTerm( loanMonTerm );
		assetVO.setLoanDate( loanDate );
		assetVO.setLoanMthdGbCd( loanMthdGbCd );
		assetVO.setEmptyTypGbCd( emptyTypGbCd );
		assetVO.setLeseeNm( leseeNm );
		assetVO.setLeseeTelNo( leseeTelNo );						// encrypted.
		assetVO.setRentAmtPayMthdGbCd( rentAmtPayMthdGbCd );
		assetVO.setDpstAmt( dpstAmt );
		assetVO.setMontRentAmt( montRentAmt );
		assetVO.setMonthlyPayDayGbCd( monthlyPayDayGbCd );
		assetVO.setRentContStDate( rentContStDate );
		assetVO.setRentContEdDate( rentContEdDate );
		
		sqlSession.insert( "AssetMapper.registerAsset", assetVO );
		return assetVO.getAssetSeqno();
	}
	
	/**
	 * 자산 첨부파일 삭제처리
	 * 
	 * @param assetSeqno
	 * @param assetAttDocGbCd
	 * @throws Exception
	 */
	public void deleteAssetAtchDocItem( long assetSeqno, String assetAttDocGbCd )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setAssetSeqno( assetSeqno );
		assetVO.setAssetAttDocGbCd( assetAttDocGbCd );
		
		sqlSession.update( "AssetMapper.deleteAssetAtchDocItem", assetVO );
	}
	
	/**
	 * 자산 첨부파일 등록
	 * 
	 * @param assetSeqno
	 * @param assetAttDocGbCd
	 * @param uploadFileNm
	 * @param saveFileNm
	 * @param fileUrl
	 * @throws Exception
	 */
	public void registerAssetAtchDocItem( long assetSeqno, String assetAttDocGbCd, String uploadFileNm, String saveFileNm, String fileUrl )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setAssetSeqno( assetSeqno );
		assetVO.setAssetAttDocGbCd( assetAttDocGbCd );
		assetVO.setUploadFileNm( uploadFileNm );
		assetVO.setSaveFileNm( saveFileNm );
		assetVO.setFileUrl( fileUrl );
		
		sqlSession.insert( "AssetMapper.registerAssetAtchDocItem", assetVO );
	}
	
	/**
	 * 자산 최종단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	public AssetVO getAssetFinalStepData( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		return sqlSession.selectOne( "AssetMapper.getAssetFinalStepData", assetVO );
	}
	
	/**
	 * 자산 수익정보 필드 업데이트
	 * 
	 * @param assetSeqno
	 * @param realInvestAmt
	 * @param loanIntrAmt
	 * @param acqstnTaxAmt
	 * @param totCost
	 * @param rtnRatio
	 * @param monProfitAmt
	 * @param pyUnitAmt
	 * @param mkProfitAmt
	 * @param trnsfTaxAmt
	 * @throws Exception
	 */
	public void updateAssetProfitInfo( long assetSeqno, long realInvestAmt, int loanIntrAmt, int acqstnTaxAmt, long totCost, double rtnRatio, int monProfitAmt, int pyUnitAmt, long mkProfitAmt, int trnsfTaxAmt )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setAssetSeqno( assetSeqno );
		assetVO.setRealInvestAmt( realInvestAmt );
		assetVO.setLoanIntrAmt( loanIntrAmt );
		assetVO.setAcqstnTaxAmt( acqstnTaxAmt );
		assetVO.setTotCost( totCost );
		assetVO.setRtnRatio( rtnRatio );
		assetVO.setMonProfitAmt( monProfitAmt );
		assetVO.setPyUnitAmt( pyUnitAmt );
		assetVO.setMkProfitAmt( mkProfitAmt );
		assetVO.setTrnsfTaxAmt( trnsfTaxAmt );
		
		sqlSession.update( "AssetMapper.updateAssetProfitInfo", assetVO );
	}
	
	/**
	 * 일반회원 자산 요약정보 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public AssetVO getAssetSummaryItem( long memSeqno )throws Exception {
		return sqlSession.selectOne( "AssetMapper.getAssetSummaryItem", memSeqno );
	}
	
	/**
	 * 내 자산 지번주소 그룹핑 목록조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public List<String> getAssetAddressGroupList( long memSeqno )throws Exception {
		return sqlSession.selectList( "AssetMapper.getAssetAddressGroupList", memSeqno );
	}
	
	/**
	 * 내 자산 목록조회
	 * 
	 * @param memSeqno
	 * @param addr
	 * @return
	 * @throws Exception
	 */
	public List<AssetVO> getMyAssetListOfAddr( long memSeqno, String addr )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAddr( addr );
		
		return sqlSession.selectList( "AssetMapper.getMyAssetListOfAddr", assetVO );
	}
	
	/**
	 * 자산 개별삭제
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @throws Exception
	 */
	public void deleteAsset( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		sqlSession.update( "AssetMapper.deleteAsset", assetVO );
	}
	
	/**
	 * 자산 일괄삭제
	 * 
	 * @param memSeqno
	 * @param addr
	 * @throws Exception
	 */
	public void deleteAssetByAddr( long memSeqno, String addr )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAddr( addr );
		
		sqlSession.update( "AssetMapper.deleteAssetByAddr", assetVO );
	}
	
	/**
	 * 자산 상세정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	public AssetVO getAssetDtlItem( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setMemSeqno( memSeqno );
		assetVO.setAssetSeqno( assetSeqno );
		
		return sqlSession.selectOne( "AssetMapper.getAssetDtlItem", assetVO );
	}
	
	/**
	 * Admin - 일반회원 자산 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<AssetVO> getMemAssetItems( String srchVal, int stRnum, int edRnum )throws Exception {
		AssetVO assetVO = new AssetVO();
		assetVO.setSrchVal( srchVal );
		assetVO.setStRnum( stRnum );
		assetVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "AssetMapper.getMemAssetItems", assetVO );
	}
	
	/**
	 * 자산 일반회원 상세정보 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public MemberVO getAssetMemDtlItem( long memSeqno )throws Exception {
		return sqlSession.selectOne( "AssetMapper.getAssetMemDtlItem", memSeqno );
	}
}