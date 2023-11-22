package com.gsntalk.api.apis.member;

import com.gsntalk.api.common.vo.*;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.util.GsntalkEncryptor;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

import java.util.List;

@Repository( "com.gsntalk.api.apis.member.MemberDAO" )
public class MemberDAO extends CommonDAO {

	public MemberDAO() {
		super( MemberDAO.class );
	}
	
	/**
	 * 로그인토큰 존재여부 확인
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param loginToken
	 * @return
	 * @throws Exception
	 */
	public int getExistsLoginTokenCnt( String memTypCd, long memSeqno, String loginToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMemTypCd( memTypCd );
		memberVO.setMemSeqno( memSeqno );
		memberVO.setLoginToken( loginToken );
		
		return sqlSession.selectOne( "MemberMapper.getExistsLoginTokenCnt", memberVO );
	}
	
	/**
	 * 로그인 토큰 등록
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param loginToken
	 * @throws Exception
	 */
	public void registerLoginToken( String memTypCd, long memSeqno, String loginToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMemTypCd( memTypCd );
		memberVO.setMemSeqno( memSeqno );
		memberVO.setLoginToken( loginToken );
		
		sqlSession.insert( "MemberMapper.registerLoginToken", memberVO );
	}
	
	/**
	 * 로그인 토큰 갱신
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param loginToken
	 * @throws Exception
	 */
	public void updateLoginToken( String memTypCd, long memSeqno, String loginToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMemTypCd( memTypCd );
		memberVO.setMemSeqno( memSeqno );
		memberVO.setLoginToken( loginToken );
		
		sqlSession.update( "MemberMapper.updateLoginToken", memberVO );
	}
	
	/**
	 * 로그인토큰 유효성 검증
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param loginToken
	 * @return
	 * @throws Exception
	 */
	public String validationLoginTokenExpireDttm( String memTypCd, long memSeqno, String loginToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMemTypCd( memTypCd );
		memberVO.setMemSeqno( memSeqno );
		memberVO.setLoginToken( loginToken );
		
		return sqlSession.selectOne( "MemberMapper.validationLoginTokenExpireDttm", memberVO );
	}
	
	/**
	 * 이메일 중복검증
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public int emailDupCheck( String email )throws Exception {
		return sqlSession.selectOne( "MemberMapper.emailDupCheck", email );
	}
	
	/**
	 * 휴대폰 본인인증 검증확인토큰 검증
	 * 
	 * @param mobNo
	 * @param vrfCnfrmToken
	 * @return
	 * @throws Exception
	 */
	public int validationMobNoCnfrmToken( String mobNo, String vrfCnfrmToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMobNo( mobNo );
		memberVO.setVrfCnfrmToken( vrfCnfrmToken );
		
		return sqlSession.selectOne( "MemberMapper.validationMobNoCnfrmToken", memberVO );
	}
	
	/**
	 * 회원 기본정보 등록
	 * 
	 * @param memTypCd
	 * @param acntAprvStatCd
	 * @param snsGbCd
	 * @param email
	 * @param memName
	 * @param mobNo
	 * @param pwd
	 * @param prflImgUrl
	 * @param snsId
	 * @param birthYear
	 * @param ageLvl
	 * @param genderCd
	 * @return
	 * @throws Exception
	 */
	public long signupMember( String memTypCd, String acntAprvStatCd, String snsGbCd, String email, String memName, String mobNo, String pwd, String prflImgUrl, String snsId, String birthYear, String ageLvl, String genderCd )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMemTypCd( memTypCd );
		memberVO.setAcntAprvStatCd( acntAprvStatCd );
		memberVO.setSnsGbCd( snsGbCd );
		memberVO.setEmail( email );
		memberVO.setMemName( memName );
		memberVO.setMobNo( GsntalkEncryptor.encrypt( mobNo ) );
		memberVO.setPwd( GsntalkUtil.isEmpty( pwd ) ? null : GsntalkEncryptor.sha512( pwd ) );
		memberVO.setPrflImgUrl( prflImgUrl );
		memberVO.setSnsId( snsId );
		memberVO.setBirthYear( birthYear );
		memberVO.setAgeLvl( ageLvl );
		memberVO.setGenderCd( genderCd );
		
		sqlSession.insert( "MemberMapper.signupMember", memberVO );
		return memberVO.getMemSeqno();
	}
	
	/**
	 * 약관 동의정보 삭제
	 * 
	 * @param memSeqno
	 * @throws Exception
	 */
	public void deleteTermsAgreeIfExists( long memSeqno )throws Exception {
		sqlSession.delete( "MemberMapper.deleteTermsAgreeIfExists", memSeqno );
	}
	
	/**
	 * 약관 동의정보 등록
	 * 
	 * @param memSeqno
	 * @param age14OvrAgreYn
	 * @param svcUseAgreYn
	 * @param prsnlInfAgreYn
	 * @param mktRcvAgreYn
	 * @throws Exception
	 */
	public void registerTermsAgree( long memSeqno, String age14OvrAgreYn, String svcUseAgreYn, String prsnlInfAgreYn, String mktRcvAgreYn )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMemSeqno( memSeqno );
		memberVO.setAge14OvrAgreYn( age14OvrAgreYn );
		memberVO.setSvcUseAgreYn( svcUseAgreYn );
		memberVO.setPrsnlInfAgreYn( prsnlInfAgreYn );
		memberVO.setMktRcvAgreYn( mktRcvAgreYn );
		
		sqlSession.insert( "MemberMapper.registerTermsAgree", memberVO );
	}
	
	/**
	 * 휴대폰 본인인증정보 삭제
	 * 
	 * @param mobNo
	 * @throws Exception
	 */
	public void deleteMobNoVerification( String mobNo )throws Exception {
		sqlSession.delete( "MemberMapper.deleteMobNoVerification", mobNo );
	}
	
	/**
	 * SNS회원등록정보 존재여부 확인
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public int isExistsSnsRegInfo( String email )throws Exception {
		return sqlSession.selectOne( "MemberMapper.isExistsSnsRegInfo", email );
	}
	
	/**
	 * SNS회원정보 등록
	 * 
	 * @param authCd
	 * @param xssToken
	 * @param snsGbCd
	 * @param snsId
	 * @param email
	 * @param userNm
	 * @param prflImgUrl
	 * @param mobNo
	 * @param genderCd
	 * @param birthYear
	 * @param birthDay
	 * @param ageLvl
	 * @throws Exception
	 */
	public void registerSNSUserRegInfo( String authCd, String xssToken, String snsGbCd, String snsId, String email, String userNm, String prflImgUrl, String mobNo, String genderCd, String birthYear, String birthDay, String ageLvl )throws Exception {
		SnsVO snsVO = new SnsVO();
		snsVO.setAuthCd( authCd );
		snsVO.setXssToken( xssToken );
		snsVO.setSnsGbCd( snsGbCd );
		snsVO.setSnsId( snsId );
		snsVO.setEmail( email );
		snsVO.setUserNm( userNm );
		snsVO.setPrflImgUrl( prflImgUrl );
		snsVO.setMobNo( mobNo );
		snsVO.setGenderCd( genderCd );
		snsVO.setBirthYear( birthYear );
		snsVO.setBirthDay( birthDay );
		snsVO.setAgeLvl( ageLvl );
		
		sqlSession.insert( "MemberMapper.registerSNSUserRegInfo", snsVO );
	}
	
	/**
	 * SNS회원정보 업데이트
	 * 
	 * @param authCd
	 * @param xssToken
	 * @param snsGbCd
	 * @param snsId
	 * @param email
	 * @param userNm
	 * @param prflImgUrl
	 * @param mobNo
	 * @param genderCd
	 * @param birthYear
	 * @param birthDay
	 * @param ageLvl
	 * @throws Exception
	 */
	public void updateSNSUserRegInfo( String authCd, String xssToken, String snsGbCd, String snsId, String email, String userNm, String prflImgUrl, String mobNo, String genderCd, String birthYear, String birthDay, String ageLvl )throws Exception {
		SnsVO snsVO = new SnsVO();
		snsVO.setAuthCd( authCd );
		snsVO.setXssToken( xssToken );
		snsVO.setSnsGbCd( snsGbCd );
		snsVO.setSnsId( snsId );
		snsVO.setEmail( email );
		snsVO.setUserNm( userNm );
		snsVO.setPrflImgUrl( prflImgUrl );
		snsVO.setMobNo( mobNo );
		snsVO.setGenderCd( genderCd );
		snsVO.setBirthYear( birthYear );
		snsVO.setBirthDay( birthDay );
		snsVO.setAgeLvl( ageLvl );
		
		sqlSession.update( "MemberMapper.updateSNSUserRegInfo", snsVO );
	}
	
	/**
	 * SNS회원 등록정보 조회
	 * 
	 * @param snsGbCd
	 * @param authCd
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public SnsVO getSnsUserRegInfo( String snsGbCd, String authCd, String email )throws Exception {
		SnsVO snsVO = new SnsVO();
		snsVO.setSnsGbCd( snsGbCd );
		snsVO.setAuthCd( authCd );
		snsVO.setEmail( email );
		
		return sqlSession.selectOne( "MemberMapper.getSnsUserRegInfo", snsVO );
	}
	
	/**
	 * SNS회원 등록정보 삭제
	 * 
	 * @param snsGbCd
	 * @param authCd
	 * @param email
	 * @throws Exception
	 */
	public void deleteSnsUserRegInfo( String snsGbCd, String authCd, String email )throws Exception {
		SnsVO snsVO = new SnsVO();
		snsVO.setSnsGbCd( snsGbCd );
		snsVO.setAuthCd( authCd );
		snsVO.setEmail( email );
		
		sqlSession.delete( "MemberMapper.deleteSnsUserRegInfo", snsVO );
	}
	
	/**
	 * 선택한 중개사무소 정보 조회
	 * 
	 * @param estBrkOfcSeqno
	 * @return
	 * @throws Exception
	 */
	public EstateBrokerOfficeVO getSelectedEstateBrokerOfficeVO( long estBrkOfcSeqno )throws Exception {
		return sqlSession.selectOne( "MemberMapper.getSelectedEstateBrokerOfficeVO", estBrkOfcSeqno );
	}
	
	/**
	 * 중개회원 첨부파일 등록
	 * 
	 * @param memSeqno
	 * @param attchFileGbCd
	 * @param uploadFileNm
	 * @param saveFileNm
	 * @param fileUrl
	 * @throws Exception
	 */
	public void registerEstateBrokerMemberAttachment( long memSeqno, String attchFileGbCd, String uploadFileNm, String saveFileNm, String fileUrl )throws Exception {
		AttachmentVO attachmentVO = new AttachmentVO();
		attachmentVO.setMemSeqno( memSeqno );
		attachmentVO.setAttchFileGbCd( attchFileGbCd );
		attachmentVO.setUploadFileNm( uploadFileNm );
		attachmentVO.setSaveFileNm( saveFileNm );
		attachmentVO.setFileUrl( fileUrl );
		
		sqlSession.insert( "MemberMapper.registerEstateBrokerMemberAttachment", attachmentVO );
	}
	
	/**
	 * 회원 비밀번호 찾기 ( 변경, 이메일을 통한 가입자만 가능 )
	 * 
	 * @param email
	 * @param mobNo
	 * @param pwd
	 * @throws Exception
	 */
	public void resetUserPwd( String email, String mobNo, String pwd )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setEmail( email );
		memberVO.setMobNo( GsntalkEncryptor.encrypt( mobNo ) );
		memberVO.setPwd( GsntalkEncryptor.sha512( pwd ) );
		
		sqlSession.update( "MemberMapper.resetUserPwd", memberVO );
	}
	
	/**
	 * FRT - 이메일 가입회원 중 이메일 존재여부 확인
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public long isExistsEmailSignupMember( String email )throws Exception {
		return sqlSession.selectOne( "MemberMapper.isExistsEmailSignupMember", email );
	}
	
	/**
	 * FRT - 일반회원 / 중개회원 이메일로 로그인
	 * 
	 * @param email
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public MemberVO frtLogin( String email, String pwd )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setEmail( email );
		memberVO.setPwd( GsntalkEncryptor.sha512( pwd ) );
		
		return sqlSession.selectOne( "MemberMapper.frtLogin", memberVO );
	}
	
	/**
	 * 자동로그인 토큰 등록
	 * 
	 * @param autoLoginToken
	 * @param userAgent
	 * @param memSeqno
	 * @throws Exception
	 */
	public void registerAutoLotinToken( String autoLoginToken, String userAgent, long memSeqno )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setAutoLoginToken( autoLoginToken );
		memberVO.setUserAgent( userAgent );
		memberVO.setMemSeqno( memSeqno );
		
		sqlSession.insert( "MemberMapper.registerAutoLotinToken", memberVO );
	}
	
	/**
	 * FRT - 일반회원 / 중개회원 자동로그인
	 *
	 * @param userAgent
	 * @param autoLoginToken
	 * @return
	 * @throws Exception
	 */
	public MemberVO frtAutoLogin( String userAgent, String autoLoginToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setUserAgent( userAgent );
		memberVO.setAutoLoginToken( autoLoginToken );
		
		return sqlSession.selectOne( "MemberMapper.frtAutoLogin", memberVO );
	}
	
	/**
	 * 자동로그인 토큰 갱신
	 * 
	 * @param autoLoginToken
	 * @param newAutoLoginToken
	 * @throws Exception
	 */
	public void updateAutoLotinToken( String autoLoginToken, String newAutoLoginToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setAutoLoginToken( autoLoginToken );
		memberVO.setNewAutoLoginToken( newAutoLoginToken );
		
		sqlSession.update( "MemberMapper.updateAutoLotinToken", memberVO );
	}
	
	/**
	 * FRT - 일반회원 SNS 로그인
	 * 
	 * @param snsGbCd
	 * @param snsId
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public MemberVO frtSnsLogin( String snsGbCd, String snsId, String email )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setSnsGbCd( snsGbCd );
		memberVO.setSnsId( snsId );
		memberVO.setEmail( email );
		
		return sqlSession.selectOne( "MemberMapper.frtSnsLogin", memberVO );
	}
	
	/**
	 * 회원 로그인이력 등록
	 * 
	 * @param memSeqno
	 * @param accsIp
	 * @param userAgent
	 * @param loginHistGbCd
	 * @throws Exception
	 */
	public void registerMemberLoginHist( long memSeqno, String accsIp, String userAgent, String loginHistGbCd )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMemSeqno( memSeqno );
		memberVO.setAccsIp( accsIp );
		memberVO.setUserAgent( userAgent );
		memberVO.setLoginHistGbCd( loginHistGbCd );
		
		sqlSession.insert( "MemberMapper.registerMemberLoginHist", memberVO );
	}
	
	/**
	 * 중개회원 중개사무소 정보 등록
	 * 
	 * @param memSeqno
	 * @param bizNo
	 * @param openRegNo
	 * @param openRegDate
	 * @param ofcNm
	 * @param reprNm
	 * @param telNo
	 * @param addr
	 * @param addrShortNm
	 * @param lat
	 * @param lng
	 * @throws Exception
	 */
	public void registerEstateBrokerOfficeInfo( long memSeqno, String bizNo, String openRegNo, String openRegDate, String ofcNm, String reprNm, String telNo, String addr, String addrShortNm, double lat, double lng )throws Exception {
		EstateBrokerOfficeVO estateBrokerOfficeVO = new EstateBrokerOfficeVO();
		estateBrokerOfficeVO.setMemSeqno( memSeqno );
		estateBrokerOfficeVO.setBizNo( GsntalkUtil.parseNumberString( bizNo ) );
		estateBrokerOfficeVO.setOpenRegNo( openRegNo );
		estateBrokerOfficeVO.setOpenRegDate( GsntalkUtil.parseNumberString( openRegDate ) );
		estateBrokerOfficeVO.setOfcNm( ofcNm );
		estateBrokerOfficeVO.setReprNm( reprNm );
		estateBrokerOfficeVO.setTelNo( telNo );
		estateBrokerOfficeVO.setAddr( GsntalkXSSUtil.encodeXss( GsntalkUtil.addressTopLvlReplace( addr ) ) );
		estateBrokerOfficeVO.setAddrShortNm( addrShortNm );
		estateBrokerOfficeVO.setLat( GsntalkUtil.trimLatLng( lat ) );
		estateBrokerOfficeVO.setLng( GsntalkUtil.trimLatLng( lng ) );
		
		sqlSession.insert( "MemberMapper.registerEstateBrokerOfficeInfo", estateBrokerOfficeVO );
	}
	
	/**
	 * 중개사무소 상세정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @return
	 * @throws Exception
	 */
	public EstateBrokerOfficeVO getEstBrkOfcItem( long estBrkMemOfcSeqno )throws Exception {
		return sqlSession.selectOne( "MemberMapper.getEstBrkOfcItem", estBrkMemOfcSeqno );
	}
	
	/**
	 * 중개회원 사무소시퀀스 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public long getEstBrkMemOfcSeqno( long memSeqno )throws Exception {
		return sqlSession.selectOne( "MemberMapper.getEstBrkMemOfcSeqno", memSeqno );
	}


	/**
	 * FRT - 이메일 가입회원 중 이메일 존재여부 확인
	 *
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public long isExistsEmailSignupAdmin( String email )throws Exception {
		return sqlSession.selectOne( "MemberMapper.isExistsEmailSignupAdmin", email );
	}

	/**
	 * FRT - 일반회원 / 중개회원 이메일로 로그인
	 *
	 * @param email
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public MemberVO adminLogin( String email, String pwd )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setEmail( email );
		memberVO.setPwd( GsntalkEncryptor.sha512( pwd ) );

		return sqlSession.selectOne( "MemberMapper.adminLogin", memberVO );
	}


	/**
	 * Admin - 관리자 자동로그인
	 * @param userAgent
	 * @param autoLoginToken
	 * @return
	 * @throws Exception
	 */
	public MemberVO adminAutoLogin( String userAgent, String autoLoginToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setUserAgent( userAgent );
		memberVO.setAutoLoginToken( autoLoginToken );

		return sqlSession.selectOne( "MemberMapper.adminAutoLogin", memberVO );
	}

	/**
	 *
	 * Admin - 중개사 회원 목록
	 * @param acntAprvStatCd 		승인유무
	 * @param delYn				탈퇴유무
	 * @param srchDateType		기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )\
	 * @param srchVal			검색어
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<MemberVO> getEstateBrokerMemberList( String viewTaget, String acntAprvStatCd,  String actvStatGbCd, String srchDateType, String srchVal, int stRnum, int edRnum )throws Exception{
		MemberVO memberVO = new MemberVO();
		memberVO.setViewTaget( viewTaget );
		memberVO.setAcntAprvStatCd( acntAprvStatCd );
		memberVO.setActvStatGbCd( actvStatGbCd );

		memberVO.setSrchDateType( srchDateType );
		memberVO.setSrchVal( srchVal );
		memberVO.setSrchValEnc( GsntalkEncryptor.encrypt( srchVal ) );
		memberVO.setStRnum( stRnum );
		memberVO.setEdRnum( edRnum );

		return sqlSession.selectList( "MemberMapper.getEstateBrokerMemberList", memberVO );
	}


	/**
	 * Admin - 중개사 회원 승인 처리
	 * @param aprvTreatMemSeqno
	 * @param memSeqno
	 * @param acntAprvStatCd
	 */
	public void updateAprvEstateBrokerMember ( long aprvTreatMemSeqno, long memSeqno, String acntAprvStatCd ){
		MemberVO memberVO = new MemberVO();
		memberVO.setAprvTreatMemSeqno( aprvTreatMemSeqno );
		memberVO.setMemSeqno( memSeqno );
		memberVO.setAcntAprvStatCd( acntAprvStatCd );

		sqlSession.update( "MemberMapper.updateAprvEstateBrokerMember", memberVO );
	}

	/**
	 * ADMNIN - 회원 상세 조회
	 * @param memSeqno
	 * @return
	 */
	public MemberVO getMemberInfo ( long memSeqno ){
		MemberVO memberVO = new MemberVO();
		memberVO.setMemSeqno( memSeqno );

		return sqlSession.selectOne( "MemberMapper.getMemberInfo", memberVO );
	}

	/**
	 * Admin - 중개사 회원 첨부 파일 조회
	 * @param memSeqno
	 * @return
	 */
	public List<AttachmentVO> getEstateBrokerMemberAttachmentList( long memSeqno ){
		AttachmentVO attachmentVO = new AttachmentVO();
		attachmentVO.setMemSeqno( memSeqno );

		return sqlSession.selectList( "MemberMapper.getEstateBrokerMemberAttachmentList", attachmentVO );
	}

	/**
	 * Admin - 중개사 회원 활동 상태 변경
	 * @param memSeqno
	 * @param actvStatGbCd
	 */
	public void updateActvStatMember( long memSeqno,  String actvStatGbCd ){
		MemberVO memberVO = new MemberVO();
		memberVO.setMemSeqno( memSeqno );
		memberVO.setActvStatGbCd( actvStatGbCd );


		sqlSession.update( "MemberMapper.updateActvStatMember", memberVO );
	}

	/**
	 * Admin - 일반 회원 목록 조회  ( 페이징 )
	 * @param actvStatGbCd
	 * @param delYn
	 * @param srchDateType
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<MemberVO> getNormalMemberList ( String delYn, String actvStatGbCd, String srchDateType, String srchVal, int stRnum, int edRnum ) throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setDelYn( delYn );
		memberVO.setActvStatGbCd( actvStatGbCd );

		memberVO.setSrchDateType( srchDateType );
		memberVO.setSrchVal( srchVal );
		memberVO.setSrchValEnc( GsntalkEncryptor.encrypt( srchVal ) );
		memberVO.setStRnum( stRnum );
		memberVO.setEdRnum( edRnum );

		return sqlSession.selectList( "MemberMapper.getNormalMemberList", memberVO );
	}

	/**
	 * 내 정보 수정
	 * @param memSeqno
	 * @param mobNo
	 * @param mobVrfNo
	 * @param memName
	 * @throws Exception
	 */
	public void updateMemberInfo( long memSeqno, String mobNo, String mobVrfNo, String memName ) throws Exception{
		MemberVO memberVO = new MemberVO();
		memberVO.setMemSeqno( memSeqno );
		memberVO.setMobNo( GsntalkEncryptor.encrypt( mobNo ) );
		memberVO.setMobVrfNo( mobVrfNo );
		memberVO.setMemName( memName );

		sqlSession.update( "MemberMapper.updateMemberInfo", memberVO );
	}

	/**
	 * 중개회원 중개사무소 정보 수정
	 * @param memSeqno
	 * @param estBrkMemOfcSeqno
	 * @param telNo
	 * @throws Exception
	 */
	public void updateOfcInfo( long memSeqno, long estBrkMemOfcSeqno, String telNo )throws Exception{
		EstateBrokerOfficeVO estateBrokerOfficeVO = new EstateBrokerOfficeVO();
		estateBrokerOfficeVO.setMemSeqno( memSeqno );
		estateBrokerOfficeVO.setEstBrkMemOfcSeqno( estBrkMemOfcSeqno );
		estateBrokerOfficeVO.setTelNo( GsntalkUtil.parseTelnoFormat( telNo ) );

		sqlSession.update( "MemberMapper.updateOfcInfo", estateBrokerOfficeVO );
	}

	/**
	 * 회원 비밀번호 수정
	 * @param memSeqno
	 * @param pwd
	 * @throws Exception
	 */
	public void updatePassowrd ( long memSeqno, String pwd )throws Exception{
		MemberVO memberVO = new MemberVO();
		memberVO.setMemSeqno( memSeqno );
		memberVO.setPwd( GsntalkEncryptor.sha512( pwd ) );

		sqlSession.update( "MemberMapper.updatePassowrd", memberVO );
	}

	/**
	 * 회원 프로필 이미지 수정
	 * @param memSeqno
	 * @param prflImgUrl
	 * @throws Exception
	 */
	public void updateMemberPrflImg( long memSeqno, String prflImgUrl ) throws Exception{
		MemberVO memberVO = new MemberVO();
		memberVO.setMemSeqno( memSeqno );
		memberVO.setPrflImgUrl( prflImgUrl );

		sqlSession.update( "MemberMapper.updateMemberInfo", memberVO );
	}
	
	/**
	 * FRT - 관심매물 목록 조회 ( 페이징 )
	 * 
	 * @param memSeqno
	 * @param prptTyp
	 * @param tranTypGbCd
	 * @param estateTypCd
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<FavRecentPrptVO> getFavPropertyItems( long memSeqno, String prptTyp, String tranTypGbCd, String estateTypCd, int stRnum, int edRnum )throws Exception {
		FavRecentPrptVO favRecentPrptVO = new FavRecentPrptVO();
		favRecentPrptVO.setMemSeqno( memSeqno );
		favRecentPrptVO.setPrptTyp( prptTyp );
		favRecentPrptVO.setTranTypGbCd( tranTypGbCd );
		favRecentPrptVO.setEstateTypCd( estateTypCd );
		favRecentPrptVO.setStRnum( stRnum );
		favRecentPrptVO.setEdRnum( edRnum );
		
		return sqlSession.selectList( "MemberMapper.getFavPropertyItems", favRecentPrptVO );
	}
	
	/**
	 * FRT - 최근 본 매물 목록조회
	 * 
	 * @param memSeqno
	 * @param resentItems
	 * @return
	 * @throws Exception
	 */
	public List<FavRecentPrptVO> getRecentPropertyItems( long memSeqno, JSONArray resentItems )throws Exception {
		FavRecentPrptVO favRecentPrptVO = new FavRecentPrptVO();
		favRecentPrptVO.setMemSeqno( memSeqno );
		favRecentPrptVO.setResentItems( resentItems );
		
		return sqlSession.selectList( "MemberMapper.getRecentPropertyItems", favRecentPrptVO );
	}

	/**
	 * 알림 목록 조회
	 * @param memSeqno
	 * @param notiGbCd
	 * @return
	 * @throws Exception
	 */
	public List<NotificationVO> getNotificationList ( long memSeqno, String notiGbCd ) throws Exception{
		NotificationVO vo = new NotificationVO();
		vo.setMemSeqno( memSeqno );
		vo.setNotiGbCd( notiGbCd );

		return sqlSession.selectList( "MemberMapper.getNotificationList", vo );
	}

	/**
	 * 미확인 알림 갯수 조회
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	public NotificationVO getNotCnfrmNotificationCnt( long memSeqno ) throws Exception{
		return sqlSession.selectOne( "MemberMapper.getNotCnfrmNotificationCnt", memSeqno );
	}

	/**
	 * 알림 확인 처리
	 * @param memSeqno
	 * @param notiSeqno
	 * @throws Exception
	 */
	public void updateNotificationCnfrm( long memSeqno, long notiSeqno ) throws Exception{
		NotificationVO vo = new NotificationVO();
		vo.setMemSeqno( memSeqno );
		vo.setNotiSeqno( notiSeqno );

		sqlSession.update( "MemberMapper.updateNotificationCnfrm", vo );
	}

	/**
	 * Admin - 일반 회원 목록 조회 - Excel Download
	 * @param delYn
	 * @param actvStatGbCd
	 * @param srchDateType
	 * @param srchVal
	 * @return
	 * @throws Exception
	 */
	public List<MemberVO> getNormalMemberExcelDownloadList( String delYn, String actvStatGbCd, String srchDateType, String srchVal ) throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setDelYn( delYn );
		memberVO.setActvStatGbCd( actvStatGbCd );
		memberVO.setSrchDateType( srchDateType );
		memberVO.setSrchVal( srchVal );
		memberVO.setSrchValEnc( GsntalkEncryptor.encrypt( srchVal ) );
		return sqlSession.selectList( "MemberMapper.getNormalMemberExcelDownloadList", memberVO );
	}

	/**
	 * Admin - 중개사 회원 목록 조회 - Excel Download
	 * @param viewTaget
	 * @param acntAprvStatCd
	 * @param actvStatGbCd
	 * @param srchDateType
	 * @param srchVal
	 * @return
	 * @throws Exception
	 */
	public List<MemberVO> getEstateBrokerMemberExcelDownloadList( String viewTaget, String acntAprvStatCd,  String actvStatGbCd, String srchDateType, String srchVal )throws Exception{
		MemberVO memberVO = new MemberVO();
		memberVO.setViewTaget( viewTaget );
		memberVO.setAcntAprvStatCd( acntAprvStatCd );
		memberVO.setActvStatGbCd( actvStatGbCd );
		memberVO.setSrchDateType( srchDateType );
		memberVO.setSrchVal( srchVal );
		memberVO.setSrchValEnc( GsntalkEncryptor.encrypt( srchVal ) );
		return sqlSession.selectList( "MemberMapper.getEstateBrokerMemberExcelDownloadList", memberVO );
	}

	/**
	 * 공지 알림 등록
	 * @param notiTypGbCd
	 * @param sendDt
	 * @param notiDscr
	 * @throws Exception
	 */
	public void registerPublicNotification( String notiTypGbCd, String sendDt, String notiDscr ) throws Exception{
		PublicNotificationVO vo = new PublicNotificationVO();
		vo.setNotiTypGbCd( notiTypGbCd );
		vo.setSendDt( sendDt );
		vo.setNotiDscr( GsntalkXSSUtil.encodeXss( notiDscr ) );
		sqlSession.insert( "MemberMapper.registerPublicNotification", vo );
	}

	/**
	 * 공지 알림 삭제
	 * @param publicNotiSeqno
	 * @throws Exception
	 */
	public void deletePublicNotification( long publicNotiSeqno ) throws Exception{
		sqlSession.update( "MemberMapper.deletePublicNotification", publicNotiSeqno );
	}

	/**
	 * 공지 알림 수정
	 * @param publicNotiSeqno
	 * @param notiTypGbCd
	 * @param sendDt
	 * @param notiDscr
	 * @throws Exception
	 */
	public void updatePublicNotification( long publicNotiSeqno, String notiTypGbCd, String sendDt, String notiDscr ) throws Exception{
		PublicNotificationVO vo = new PublicNotificationVO();
		vo.setPublicNotiSeqno( publicNotiSeqno );
		vo.setNotiTypGbCd( notiTypGbCd );
		vo.setSendDt( sendDt );
		vo.setNotiDscr( GsntalkXSSUtil.encodeXss( notiDscr ) );
		sqlSession.update( "MemberMapper.updatePublicNotification", vo );
	}

	/**
	 * 공지 알림 상세 조회
	 * @param publicNotiSeqno
	 * @return
	 * @throws Exception
	 */
	public PublicNotificationVO getPublicNotificationInfo( long publicNotiSeqno )throws Exception{
		return sqlSession.selectOne( "MemberMapper.getPublicNotificationInfo", publicNotiSeqno );
	}

	/**
	 * 공지 알림 목록 조회 ( 페이징 )
	 * @param srchDateType
	 * @param srchVal
	 * @param stRnum
	 * @param edRnum
	 * @return
	 * @throws Exception
	 */
	public List<PublicNotificationVO> getPublicNotificationList( String srchDateType, String srchVal, int stRnum, int edRnum )throws Exception{
		PublicNotificationVO vo = new PublicNotificationVO();
		vo.setSrchDateType( srchDateType );
		vo.setSrchVal( srchVal );
		vo.setStRnum( stRnum );
		vo.setEdRnum( edRnum );
		return sqlSession.selectList( "MemberMapper.getPublicNotificationList", vo );
	}
}