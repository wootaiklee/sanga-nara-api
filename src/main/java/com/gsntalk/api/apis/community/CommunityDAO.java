package com.gsntalk.api.apis.community;


import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.CommunityAttachImgVO;
import com.gsntalk.api.common.vo.CommunityPostVO;
import com.gsntalk.api.common.vo.CommunityReplyVO;
import com.gsntalk.api.util.GsntalkXSSUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository( "com.gsntalk.api.apis.community.CommunityDAO" )
public class CommunityDAO extends CommonDAO {

    public CommunityDAO() {
        super( CommunityDAO.class );
    }


    /**
     * 커뮤니티 게시글 등록
     * @param memSeqno
     * @param comuPrefCd
     * @param comuTitle
     * @param comuContents
     * @return
     */
    public long registerCommunityPost( long memSeqno, String comuNotiYn, String comuTypeCd,  String comuPrefCd, String comuTitle, String comuContents, String comuVideoUrl ){
        CommunityPostVO vo = new CommunityPostVO();
        vo.setMemSeqno( memSeqno );
        vo.setComuNotiYn( comuNotiYn );
        vo.setComuTypeCd( comuTypeCd );
        vo.setComuPrefCd( comuPrefCd );
        vo.setComuTitle( GsntalkXSSUtil.encodeXss( comuTitle ) );
        vo.setComuContents( GsntalkXSSUtil.encodeXss( comuContents ) );
        vo.setComuVideoUrl( comuVideoUrl );

        sqlSession.insert( "CommunityMapper.registerCommunityPost", vo);
        return vo.getComuSeqno();
    }

    /**
     * 커뮤니티 게시글 이미지 등록
     * @param comuSeqno
     * @param uploadFileNm
     * @param saveFileNm
     * @param fileUrl
     * @param sortSerl
     */
    public void registerCommunityAttachImg( long comuSeqno, String uploadFileNm, String saveFileNm, String fileUrl, String sortSerl){
        CommunityAttachImgVO communityAttachImgVO = new CommunityAttachImgVO();
        communityAttachImgVO.setComuSeqno( comuSeqno );
        communityAttachImgVO.setUploadFileNm( uploadFileNm );
        communityAttachImgVO.setSaveFileNm( saveFileNm );
        communityAttachImgVO.setFileUrl( fileUrl );
        communityAttachImgVO.setSortSerl( sortSerl );

        sqlSession.insert( "CommunityMapper.registerCommunityAttachImg", communityAttachImgVO );
    }

    /**
     * 커뮤니티 게시글 수정
     * @param comuSeqno
     * @param comuPrefCd
     * @param comuTitle
     * @param comuContents
     */
    public void updateCommunityPost( long memSeqno, long comuSeqno, String comuNotiYn, String comuPrefCd, String comuTitle, String comuContents  ){
        CommunityPostVO vo = new CommunityPostVO();
        vo.setComuSeqno( comuSeqno );
        vo.setComuNotiYn( comuNotiYn );
        vo.setComuPrefCd( comuPrefCd );
        vo.setComuTitle( comuTitle );
        vo.setComuContents( comuContents );
        vo.setMemSeqno( memSeqno );

        sqlSession.update( "CommunityMapper.updateCommunityPost", vo );
    }

    /**
     * 커뮤니티 첨부 이미지 정렬순서 수정
     * @param comuSeqno
     * @param saveFileNm
     * @param sortSerl
     */
    public void updateCommunityAttachImgSortSerl( long comuSeqno, String saveFileNm, String sortSerl ){
        CommunityAttachImgVO vo = new CommunityAttachImgVO();
        vo.setComuSeqno( comuSeqno );
        vo.setSaveFileNm( saveFileNm );
        vo.setSortSerl( sortSerl );

        sqlSession.update( "CommunityMapper.updateCommunityAttachImgSortSerl", vo );
    }


    /**
     * 커뮤니티 게시글 삭제 
     * @param comuSeqno
     */
    public void deleteCommunityPost( long memSeqno, long comuSeqno ){
        CommunityPostVO vo = new CommunityPostVO();
        vo.setModMemSeqno( memSeqno );
        vo.setComuSeqno( comuSeqno );
        sqlSession.update( "CommunityMapper.deleteCommunityPost", comuSeqno );
    }

    /**
     * 커뮤니티 댓글 삭제
     * @param comuSeqno
     */
    public void deleteCommunityReplyBycomuSeqno( long memSeqno, long comuSeqno){
        CommunityReplyVO vo = new CommunityReplyVO();
        vo.setModMemSeqno( memSeqno );
        vo.setComuSeqno( comuSeqno );
        sqlSession.update( "CommunityMapper.deleteCommunityReplyBycomuSeqno", vo );
    }

    /**
     * 커뮤니티 첨부 이미지 삭제
     * @param comuSeqno
     */
    public void deleteCommunityAttachImgBycomuSeqno( long comuSeqno ){
        sqlSession.update( "CommunityMapper.deleteCommunityAttachImgBycomuSeqno", comuSeqno );
    }

    /**
     * 커뮤니티 첨부 이미지 삭제 (  저장파일명이 동일한 파일 )
     * @param comuSeqno
     * @param saveFileNm
     */
    public void deleteCommunityAttachImgBySaveFileName( long comuSeqno, String saveFileNm ){
        CommunityAttachImgVO vo = new CommunityAttachImgVO();
        vo.setComuSeqno( comuSeqno );
        vo.setSaveFileNm( saveFileNm );

        sqlSession.update( "CommunityMapper.deleteCommunityAttachImgBySaveFileName", vo );
    }


    /**
     * 커뮤니티 개시판 상세 조회
     * @param comuSeqno
     * @return
     */
    public CommunityPostVO getCommunityPostInfo( long comuSeqno ){
        return sqlSession.selectOne( "CommunityMapper.getCommunityPostInfo", comuSeqno );
    }

    /**
     * 커뮤니티 이미지 목록 조회
     * @param comuSeqno
     * @return
     */
    public List<CommunityAttachImgVO> getCommunityAttachImgList( long comuSeqno ){
        return sqlSession.selectList( "CommunityMapper.getCommunityAttachImgList", comuSeqno );
    }

    /**
     * 커뮤니티 댓글 목록 조회
     * @param comuSeqno
     * @param stRnum
     * @param edRnum
     * @return
     */
    public List<CommunityReplyVO> getCommunityReplyList( long comuSeqno, String orderSort,  int stRnum, int edRnum ){
        CommunityReplyVO communityReplyVO = new CommunityReplyVO();
        communityReplyVO.setComuSeqno( comuSeqno );
        communityReplyVO.setOrderSort( orderSort );
        communityReplyVO.setStRnum( stRnum );
        communityReplyVO.setEdRnum( edRnum );

        return sqlSession.selectList("CommunityMapper.getCommunityReplyList", communityReplyVO);
    }

    /**
     * 커뮤니티 게시글 목록 조회
     * @param memSeqno
     * @param comuPrefCd
     * @param comuTitle
     * @param stRnum
     * @param edRnum
     * @return
     */
    public List<CommunityPostVO> getCommunityPostList(long memSeqno, String comuTypeCd, String comuPrefCd, String comuTitle, String srchDateType, String srchVal, String orderColumn, String orderSort, int stRnum, int edRnum ){
        CommunityPostVO communityPostVO = new CommunityPostVO();
        if( memSeqno != 0 ){
            communityPostVO.setMemSeqno( memSeqno );
        }
        communityPostVO.setComuPrefCd( comuPrefCd );
        communityPostVO.setComuTypeCd( comuTypeCd );
        communityPostVO.setComuTitle( comuTitle );
        communityPostVO.setStRnum( stRnum );
        communityPostVO.setEdRnum( edRnum );
        communityPostVO.setSrchDateType( srchDateType );
        communityPostVO.setSrchVal( srchVal );
        communityPostVO.setOrderColumn( orderColumn );
        communityPostVO.setOrderSort( orderSort );

        return sqlSession.selectList("CommunityMapper.getCommunityPostList", communityPostVO);
    }

    /**
     * 커뮤니티 댓글 등록
     * @param memSeqno
     * @param comuSeqno
     * @param toMemSeqno
     * @param comuReContents
     */
    public void registerCommunityReply ( long memSeqno, long comuSeqno, long toMemSeqno, String comuReContents ){
        CommunityReplyVO vo = new CommunityReplyVO();
        vo.setMemSeqno( memSeqno );
        vo.setComuSeqno( comuSeqno );
        vo.setToMemSeqno( toMemSeqno );
        vo.setComuReContents( GsntalkXSSUtil.encodeXss( comuReContents ) );

        sqlSession.insert( "CommunityMapper.registerCommunityReply" , vo );
    }

    /**
     * 커뮤니티 댓글 정보 조회
     * @param comuReSeqno
     * @return
     */
    public CommunityReplyVO getCommunityReplyInfo( long comuReSeqno ){
        return sqlSession.selectOne( "CommunityMapper.getCommunityReplyInfo" , comuReSeqno );
    }

    /**
     * 커뮤니티 댓글 수정
     * @param comuReSeqno
     * @param memSeqno
     * @param comuSeqno
     * @param toMemSeqno
     * @param comuReContents
     */
    public void updateCommunityReply ( long comuReSeqno, long memSeqno, long comuSeqno, long toMemSeqno, String comuReContents ){
        CommunityReplyVO vo = new CommunityReplyVO();
        vo.setComuReSeqno( comuReSeqno );
        vo.setMemSeqno( memSeqno );
        vo.setComuSeqno( comuSeqno );
        vo.setToMemSeqno( toMemSeqno );
        vo.setComuReContents( GsntalkXSSUtil.encodeXss( comuReContents ) );
        vo.setModMemSeqno( memSeqno );

        sqlSession.update( "CommunityMapper.updateCommunityReply" , vo );
    }

    /**
     * 커뮤니티 댓글 삭제
     * @param comuReSeqno
     * @param memSeqno
     * @param comuSeqno
     */
    public void deleteCommunityReply( long comuReSeqno, long memSeqno, long comuSeqno ){
        CommunityReplyVO vo = new CommunityReplyVO();
        vo.setComuReSeqno( comuReSeqno );
        vo.setMemSeqno( memSeqno );
        vo.setModMemSeqno( memSeqno );
        vo.setComuSeqno( comuSeqno );

        sqlSession.update( "CommunityMapper.deleteCommunityReply" , vo );
    }

    /**
     * 공지 갯수 조회
     * @return
     */
    public int getCommunityPostNoticeCnt( String comuPrefCd ){
        return sqlSession.selectOne( "CommunityMapper.getCommunityPostNoticeCnt", comuPrefCd );
    }

    /**
     * 오래된 공지 하나 해제
     */
    public void updateCommunityPostOldNotice( long memSeqno, String comuPrefCd ){
        CommunityPostVO vo = new CommunityPostVO();
        vo.setModMemSeqno( memSeqno );
        vo.setComuPrefCd( comuPrefCd );

        sqlSession.update( "CommunityMapper.updateCommunityPostOldNotice", vo  );
    }

    /**
     * 커뮤니티 게시글 공지로 수정
     * @param comuSeqno
     */
    public void updateCommunityPostNoticeYn( long memSeqno, long comuSeqno, String comuNotiYn ){
        CommunityPostVO vo = new CommunityPostVO();
        vo.setModMemSeqno( memSeqno );
        vo.setComuSeqno( comuSeqno );
        vo.setComuNotiYn( comuNotiYn );

        sqlSession.update( "CommunityMapper.updateCommunityPostNoticeYn" , vo  );
    }

    /**
     * 커뮤니티 게시글 카운트 수정
     * @param comuSeqno
     * @param cntType       // R : 댓글, L : 좋아요, V : 조회수
     * @param cntState      // P : +1 , M : -1
     */
    public void updateCommunityPostCnt( long comuSeqno, String cntType, String cntState ){
        CommunityPostVO vo = new CommunityPostVO();
        vo.setComuSeqno( comuSeqno );
        vo.setCntType( cntType );
        vo.setCntState( cntState );

        sqlSession.update( "CommunityMapper.updateCommunityPostCnt" , vo  );
    }

    /**
     * 커뮤니티 게시글 공지사항 목록 조회
     * @return
     */
    public List<CommunityPostVO> getCommunityPostNoticeList(){
        return sqlSession.selectList( "CommunityMapper.getCommunityPostNoticeList" );
    }
}
