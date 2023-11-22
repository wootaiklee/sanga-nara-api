package com.gsntalk.api.apis.community;


import com.gsntalk.api.apis.member.MemberDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.CommunityAttachImgVO;
import com.gsntalk.api.common.vo.CommunityPostVO;
import com.gsntalk.api.common.vo.CommunityReplyVO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service( "CommunityService" )
public class CommunityService extends CommonService {

    public CommunityService() {super( CommonService.class);}

    @Autowired
    private CommunityDAO communityDAO;

    @Autowired
    private MemberDAO memberDAO;


    /**
     * 커뮤니티 게시글 등록
     * @param memSeqno
     * @param comuPrefCd
     * @param comuTitle
     * @param comuContents
     * @param imgFiles
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void registerCommunityPostItem( long memSeqno, String comuNotiYn, String comuTypeCd, String comuPrefCd, String comuTitle, String comuContents, String comuVideoUrl, List<MultipartFile> imgFiles ) throws Exception {
        // 회원 조회
        MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

        // 대상 없으면 예외 처리
        if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        // 활동 가능 유저가 아니면 예외 처리
        if( ! "NOR".equals( memberVO.getActvStatGbCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ACTIVE_MEMBER );
        }

        // 공톡 등록시 관리자가 아니면 예외처리
        if( "G".equals( comuTypeCd ) && !"A".equals( memberVO.getMemTypCd() )  ){
            throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
        }

        // 공지가 Y 면
        if( "Y".equals( comuNotiYn ) ){
            // 관리자 아니면 예외처리
            if( !"A".equals( memberVO.getMemTypCd() )  ){
                throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
            }

            // 등록할려는 말머리에 공지가 몇개인지 확인
            int noticeCnt = communityDAO.getCommunityPostNoticeCnt( comuPrefCd );

            // 두개 이상 일 땐 최근 하나 빼고 공지 해제
            if( noticeCnt >= 2){
                for( int i = 0 ; i < ( noticeCnt - 1 ) ; i++ ){
                    communityDAO.updateCommunityPostOldNotice( memSeqno, comuPrefCd );
                }
            }
        }

        // 커뮤니티 게시글 글 등록
        long comuSeqno = communityDAO.registerCommunityPost( memSeqno, comuNotiYn, comuTypeCd, comuPrefCd, comuTitle, comuContents, comuVideoUrl);

        // 이미지가 있으면
        if( imgFiles != null ){
            // 이미지 파일 검증
            if( imgFiles.size() > 8 ) { // 첨부가능한 최대 파일수량 초과
                throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "imgFiles" );
            }

            String orgFileNm = "";
            String uploadFileFormat = "";
            for( MultipartFile file : imgFiles ) {
                orgFileNm = file.getOriginalFilename();
                uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
                if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
                    // 허용되지 않은 파일 포맷
                    throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "featrFiles -> " + orgFileNm );
                }
            }

            // 이미지 업로드 및 등록
            int sortSerl = 0;
            for( MultipartFile file : imgFiles ){
                sortSerl++;
                JSONObject uploadItem = gsntalkS3Util.uploadCommunityImageFile( comuSeqno, file );

                // db에 추가
                communityDAO.registerCommunityAttachImg(
                        comuSeqno,
                        GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ),
                        GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ),
                        GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ),
                        Integer.toString( sortSerl )
                );
            }
        }

    }

    /**
     * 커뮤니티 게시글 수정
     * @param comuSeqno
     * @param comuPrefCd
     * @param comuTitle
     * @param comuContents
     * @param imgItemsString
     * @param imgFiles
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCommunityPostItem( long memSeqno, long comuSeqno, String comuNotiYn, String comuPrefCd, String comuTitle, String comuContents, String imgItemsString, List<MultipartFile> imgFiles )throws Exception{
        // 회원 조회
        MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

        // 수정 대상 글 조회
        CommunityPostVO communityPostVO = communityDAO.getCommunityPostInfo( comuSeqno );
        if( communityPostVO == null ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        // 공톡 글의 경우 관리자면 수정 가능, 일반글은 본인만 수정가능
        if( "G".equals( communityPostVO.getComuTypeCd() ) ){
            // 관리자 아니면 예외처리
            if( !"A".equals( memberVO.getMemTypCd() )  ){
                throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
            }
        } else {
            // 본인글인지 확인
            if( memSeqno != communityPostVO.getMemSeqno() ){
                throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
            }
        }

        // 파일 포맷 검증
        String orgFileNm = "";
        String uploadFileFormat = "";
        for( MultipartFile file : imgFiles ) {
            orgFileNm = file.getOriginalFilename();
            uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
            if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) { // 허용되지 않은 파일 포맷
                throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "featrFiles -> " + orgFileNm );
            }
        }

        // 게시글 수정
        communityDAO.updateCommunityPost( memSeqno, comuSeqno, comuNotiYn, comuPrefCd, comuTitle, comuContents );

        // 이미지 파일 삭제/수정/추가 진행
        if( !GsntalkUtil.isEmpty( imgItemsString ) ){
            JSONArray imgItems =  (JSONArray) jsonParser.parse( imgItemsString );

            String changeType = null;
            String saveFileNm = null;
            String sortSerl = null;

            int saveFileCnt = 0 ;

            for( int i = 0; i < imgItems.size(); i++ ){
                JSONObject o = (JSONObject) imgItems.get(i);
                changeType = GsntalkUtil.getString( o.get( "changeType" ) );
                saveFileNm = GsntalkUtil.getString( o.get( "saveFileNm" ) );
                sortSerl = GsntalkUtil.getString( o.get( "sortSerl" ) );


                switch ( changeType ){
                    case "D" : // 삭제
                        communityDAO.deleteCommunityAttachImgBySaveFileName( comuSeqno, saveFileNm);
                        break;
                    case "U" : // 수정
                        communityDAO.updateCommunityAttachImgSortSerl( comuSeqno, saveFileNm, sortSerl );
                        saveFileCnt++;
                        break;
                    case "A" : // 추가
                        for( MultipartFile file : imgFiles ) {
                            orgFileNm = file.getOriginalFilename();
                            // 파일명이 같으면
                            if( orgFileNm.equals( saveFileNm ) ){
                                // 파일 업로드
                                JSONObject uploadItem = gsntalkS3Util.uploadCommunityImageFile( comuSeqno, file );

                                communityDAO.registerCommunityAttachImg(
                                        comuSeqno,
                                        GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ),
                                        GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ),
                                        GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ),
                                        sortSerl
                                );
                                saveFileCnt++;
                            }
                        }
                        break;
                }

                if( saveFileCnt > 8 ){ // 첨부가능한 최대 파일수량 초과
                    throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "imgFiles" );
                }
            }
        }
    }

    /**
     * 커뮤니티 게시글 삭제
     * @param memSeqno
     * @param comuSeqnoItems
     */
    @Transactional( rollbackFor = Exception.class)
    public void deleteCommunityPostItems( long memSeqno, JSONArray comuSeqnoItems )throws Exception{
        // 회원 조회
        MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

        long comuSeqno = 0L;
        CommunityPostVO vo;

        for( int i = 0; i < comuSeqnoItems.size(); i++ ){
            comuSeqno = GsntalkUtil.getLong( comuSeqnoItems.get( i ) );

            vo = communityDAO.getCommunityPostInfo( comuSeqno );
            if( vo == null ){
                throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
            }

            // 관리자가 아니면 본인글인지 확인
            if( !"A".equals( memberVO.getMemTypCd() ) ){
                if( memSeqno != vo.getMemSeqno() ){
                    throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
                }
            }

            // 게시글, 댓글 삭제
            communityDAO.deleteCommunityPost( memSeqno, comuSeqno );
            communityDAO.deleteCommunityReplyBycomuSeqno( memSeqno, comuSeqno );
            communityDAO.deleteCommunityAttachImgBycomuSeqno( comuSeqno );
        }
    }

    /**
     * 커뮤니티 게시글 상세 조회
     * @return
     */
    @Transactional( rollbackFor = Exception.class )
    public Map<String, Object> getCommunityPostInfoItem( long comuSeqno ) throws Exception {
        // 게시글 조회수 증가
        communityDAO.updateCommunityPostCnt( comuSeqno, "V", "P");
        // 상세 조회
        CommunityPostVO vo = communityDAO.getCommunityPostInfo( comuSeqno );
        if( vo == null){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        // 공톡 조회시 공실앤톡 계정 조회
        String adminPrflImgUrl = null;
        String adminMemName = null;
        if( "G".equals( vo.getComuTypeCd() ) ){
            MemberVO adminVo = memberDAO.getMemberInfo( 1 );
            adminPrflImgUrl = adminVo.getPrflImgUrl();
            adminMemName = adminVo.getMemName();
        }

        JSONObject item = new JSONObject();
        item.put( "comuReSeqno",        vo.getComuSeqno() );
        item.put( "comuSeqno",          vo.getComuSeqno() );
        item.put( "comuNotiYn",         vo.getComuNotiYn() );
        item.put( "comuTypeCd",         vo.getComuTypeCd() );
        item.put( "comuPrefCd",         vo.getComuPrefCd() );
        item.put( "comuPrefNm",         vo.getComuPrefNm() );
        item.put( "comuTitle",          vo.getComuTitle() );
        item.put( "comuContents",       vo.getComuContents() );
        item.put( "comuVideoUrl",       vo.getComuVideoUrl() );
        item.put( "comuViewCnt",        vo.getComuViewCnt() );
        item.put( "comuLikeCnt",        vo.getComuLikeCnt() );
        item.put( "comuReplyCnt",       vo.getComuReplyCnt() );
        item.put( "regDttm",           vo.getRegDttm() );
        item.put( "modDttm",            vo.getModDttm() );

        // 공톡은 작성자 강제 변경
        if( "G".equals( vo.getComuTypeCd() ) ){
            MemberVO adminVo = memberDAO.getMemberInfo( 1 );
            adminMemName = adminVo.getMemName();

            item.put( "memSeqno", 			1 );
            item.put( "memName",            adminVo.getMemName() );
            item.put( "prflImgUrl", 		adminVo.getPrflImgUrl() );
        }else{
            item.put( "memSeqno", 			vo.getMemSeqno() );
            item.put( "memName",            vo.getMemName() );
            item.put( "prflImgUrl", 		vo.getPrflImgUrl() );
        }

        // 이미지 조회
        List<CommunityAttachImgVO> imgList = communityDAO.getCommunityAttachImgList( comuSeqno );
        if( imgList != null ){

            JSONArray imgItems = new JSONArray();
            JSONObject imgItem;
            for( CommunityAttachImgVO img : imgList ){
                imgItem = new JSONObject();
                imgItem.put( "comuSeqno", img.getComuSeqno() );
                imgItem.put( "saveFileNm", img.getSaveFileNm() );
                imgItem.put( "fileUrl", img.getFileUrl() );
                imgItem.put( "sortSerl", img.getSortSerl() );

                imgItems.add( imgItem );
            }

            item.put( "imgItems", imgItems );
        }


        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put( "item", item );

        return resMap;
    }

    /**
     * 커뮤니티 게시글 목록 조회
     * @param memSeqno
     * @param comuPrefCd
     * @param comuTitle
     * @param pageCnt
     * @param nowPage
     * @param listPerPage
     * @return
     */
    public Map<String, Object> getCommunityPostItems( long memSeqno, String comuTypeCd, String comuPrefCd, String comuTitle, String srchDateType, String srchVal, String orderColumn, String orderSort, int pageCnt, int nowPage, int listPerPage )throws Exception {
        JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem(listPerPage, nowPage);
        int stRnum = GsntalkUtil.getInteger(reqPageItem.get("stRnum"));
        int edRnum = GsntalkUtil.getInteger(reqPageItem.get("edRnum"));

        List<CommunityPostVO> CommunityPostList = communityDAO.getCommunityPostList(memSeqno, comuTypeCd, comuPrefCd, comuTitle, srchDateType, srchVal, orderColumn, orderSort, stRnum, edRnum);
        int totList = 0;

        if ( GsntalkUtil.isEmptyList(CommunityPostList) ) {
            CommunityPostList = new ArrayList<CommunityPostVO>();
        } else {
            totList = CommunityPostList.get(0).getTotalCount();
        }

        // 공톡 조회시 공실앤톡 계정 조회
        String adminPrflImgUrl = null;
        String adminMemName = null;
        if( "G".equals( comuTypeCd ) ){
            MemberVO adminVo = memberDAO.getMemberInfo( 1 );
            adminPrflImgUrl = adminVo.getPrflImgUrl();
            adminMemName = adminVo.getMemName();
        }

        // 조회 데이터 가공
        JSONArray items = new JSONArray();
        JSONObject item = null;

        for( CommunityPostVO vo : CommunityPostList ){
            item = new JSONObject();
            item.put( "rnum", 				vo.getRownum() );
            item.put( "comuNotiYn",         vo.getComuNotiYn() );
            item.put( "comuSeqno",          vo.getComuSeqno() );
            item.put( "comuPrefCd",         vo.getComuPrefCd() );
            item.put( "comuPrefNm",         vo.getComuPrefNm() );
            item.put( "comuTitle",          vo.getComuTitle() );
            item.put( "comuContents",       vo.getComuContents() );
            item.put( "repImgUrl",          vo.getRepImgUrl() );
            item.put( "comuViewCnt",        vo.getComuViewCnt() );
            item.put( "comuLikeCnt",        vo.getComuLikeCnt() );
            item.put( "comuReplyCnt",       vo.getComuReplyCnt() );
            item.put( "regDttm",           vo.getRegDttm() );
            item.put( "modDttm",            vo.getModDttm() );

            // 공톡은 작성자 강제 변경
            if( "G".equals( vo.getComuTypeCd() ) ){
                item.put( "memSeqno", 			1 );
                item.put( "memName",            adminMemName );
                item.put( "prflImgUrl", 		adminPrflImgUrl );
            }else{
                item.put( "memSeqno", 			vo.getMemSeqno() );
                item.put( "memName",            vo.getMemName() );
                item.put( "prflImgUrl", 		vo.getPrflImgUrl() );
            }

            items.add( item );
        }

        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put( "items", items );
        resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );

        return resMap;
    }


    /**
     * 커뮤니티 댓글 목록 조회
     * @param pageCnt
     * @param nowPage
     * @param listPerPage
     * @return
     */
    public Map<String, Object>getCommunityReplyItems( long comuSeqno, String orderSort, int pageCnt, int nowPage, int listPerPage)throws Exception{
        JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem(listPerPage, nowPage);
        int stRnum = GsntalkUtil.getInteger(reqPageItem.get("stRnum"));
        int edRnum = GsntalkUtil.getInteger(reqPageItem.get("edRnum"));

        List<CommunityReplyVO> communityReplyList = communityDAO.getCommunityReplyList( comuSeqno, orderSort, stRnum, edRnum );

        int totList = 0;

        if ( GsntalkUtil.isEmptyList(communityReplyList) ) {
            communityReplyList = new ArrayList<CommunityReplyVO>();
        } else {
            totList = communityReplyList.get(0).getTotalCount();
        }

        // 조회 데이터 가공
        JSONArray items = new JSONArray();
        JSONObject item = null;
        for( CommunityReplyVO vo : communityReplyList ){
            item = new JSONObject();
            item.put( "rnum", 				vo.getRownum() );
            item.put( "comuReSeqno",        vo.getComuReSeqno() );
            item.put( "comuSeqno",          vo.getComuSeqno() );
            item.put( "memSeqno", 			vo.getMemSeqno() );
            item.put( "memName", 			vo.getMemName() );
            item.put( "prflImgUrl", 		vo.getPrflImgUrl() );
            item.put( "toMemSeqno", 		vo.getToMemSeqno() );
            item.put( "toMemName", 			vo.getToMemName() );
            item.put( "comuReContents", 	vo.getComuReContents() );
            item.put( "regDttm",            vo.getRegDttm() );
            item.put( "modDttm",            vo.getModDttm() );

            items.add( item );
        }

        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put( "items", items );
        resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );

        return resMap;
    }


    /**
     * 커뮤니티 댓글 등록
     * @param memSeqno
     * @param comuSeqno
     * @param toMemSeqno
     * @param comuReContents
     * @throws Exception
     */
    @Transactional( rollbackFor = Exception.class )
    public void registerCommunityReplyItem( long memSeqno, long comuSeqno, long toMemSeqno, String comuReContents )throws Exception{
        // 회원 조회
        MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

        // 대상 없으면 예외 처리
        if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        if( ! "NOR".equals( memberVO.getActvStatGbCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ACTIVE_MEMBER );
        }

        // 댓글 등록
        communityDAO.registerCommunityReply( memSeqno, comuSeqno, toMemSeqno, comuReContents);
        // 게시글 댓글수 증가
        communityDAO.updateCommunityPostCnt( comuSeqno, "R", "P");
    }

    /**
     * 커뮤니티 댓글 수정
     * @param comuReSeqno
     * @param memSeqno
     * @param comuSeqno
     * @param toMemSeqno
     * @param comuReContents
     * @throws Exception
     */
    public void updateCommunityReplyItem( long comuReSeqno, long memSeqno, long comuSeqno, long toMemSeqno, String comuReContents )throws Exception{
        // 회원 조회
        MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );
        // 댓글 조회
        CommunityReplyVO replyVo = communityDAO.getCommunityReplyInfo( comuReSeqno );

        // 대상 없으면 예외 처리
        if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        if( ! "NOR".equals( memberVO.getActvStatGbCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ACTIVE_MEMBER );
        }

        if( replyVo == null  ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        // 본인댓글인지 확인
        if( memSeqno != replyVo.getMemSeqno() ){
            throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
        }

        // 댓글 수정
        communityDAO.updateCommunityReply( comuReSeqno, memSeqno, comuSeqno, toMemSeqno, comuReContents);
    }

    /**
     * 커뮤니티 댓글 삭제
     * @param memSeqno
     * @param comuSeqno
     * @throws Exception
     */
    public void deleteCommunityReplyItem( long comuReSeqno, long memSeqno, long comuSeqno )throws Exception{
        // 회원 조회
        MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );
        // 댓글 조회
        CommunityReplyVO replyVo = communityDAO.getCommunityReplyInfo( comuReSeqno );

        // 대상 없으면 예외 처리
        if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        if( ! "NOR".equals( memberVO.getActvStatGbCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ACTIVE_MEMBER );
        }

        if( replyVo == null  ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        // 관리자가 아니면 본인 댓글인지 확인
        if( !"A".equals( memberVO.getMemTypCd() ) ){
            if( memSeqno != replyVo.getMemSeqno() ){
                throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
            }
        }

        // 댓글 삭제
        communityDAO.deleteCommunityReply( comuReSeqno, memSeqno, comuSeqno );
        // 게시글 댓글수 감소
        communityDAO.updateCommunityPostCnt( comuSeqno, "R", "M");

    }


    /**
     * 커뮤니티 게시글 공지 설정 및 해제
     * @param memSeqno
     * @param comuSeqno
     * @param comuNotiYn
     * @throws Exception
     */
    @Transactional( rollbackFor = Exception.class )
    public void updateCommunityPostNoticeYn( long memSeqno, long comuSeqno, String comuNotiYn) throws Exception{
        // 회원 조회
        MemberVO memberVO =	memberDAO.getMemberInfo( memSeqno );

        // 대상 없으면 예외 처리
        if( memberVO == null || "Y".equals( memberVO.getDelYn() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        if( !"A".equals( memberVO.getMemTypCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS );
        }

        // 커뮤니티게시글 조회
        CommunityPostVO postVo = communityDAO.getCommunityPostInfo( comuSeqno );

        // 대상 없으면 예외처리
        if( postVo == null ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET );
        }

        // 공지가 Y 면
        if( "Y".equals( comuNotiYn ) ){
            // 등록할려는 말머리에 공지가 몇개인지 확인
            int noticeCnt = communityDAO.getCommunityPostNoticeCnt( postVo.getComuPrefCd() );

            // 두개 이상 일 땐 최근 하나 빼고 공지 해제
            if( noticeCnt >= 2){
                for( int i = 0 ; i < ( noticeCnt - 1 ) ; i++ ){
                    communityDAO.updateCommunityPostOldNotice( memSeqno, postVo.getComuPrefCd() );
                }
            }
        }

        // 공지 설정 업데이트
        communityDAO.updateCommunityPostNoticeYn( memSeqno, comuSeqno, comuNotiYn);
    }

    /**
     * 커뮤니티 게시글 좋아요 수 증가
     * @param memSeqno
     * @param comuSeqno
     * @throws Exception
     */
    public void updateCommunityPostLikeCnt( long memSeqno, long comuSeqno ) throws Exception{
        if( memSeqno != 0 ) {
            CommunityPostVO vo = communityDAO.getCommunityPostInfo( comuSeqno );

            if( vo.getMemSeqno() == memSeqno ){
                throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_LIKE_MY_POST );
            }
        }
        // 게시글 좋아요 증가
        communityDAO.updateCommunityPostCnt( comuSeqno, "L", "P");
    }

    /**
     * 커뮤니티 게시글 공지사항 목록 조회
     * @return
     */
    public Map<String, Object>getCommunityPostNoticeItems() throws Exception{
        List<CommunityPostVO> communityPostList = communityDAO.getCommunityPostNoticeList();

        if( communityPostList.size() == 0 ){
            return new HashMap<String,Object>();
        }

        MemberVO adminVo = memberDAO.getMemberInfo( 1 );
        String adminPrflImgUrl = adminVo.getPrflImgUrl();
        String adminMemName = adminVo.getMemName();

        JSONArray items = new JSONArray();
        JSONObject item = null;
        for( CommunityPostVO vo : communityPostList ){
            item = new JSONObject();
            item.put( "comuSeqno",         vo.getComuSeqno() );
            item.put( "comuTitle",         vo.getComuTitle() );
            item.put( "regDttm",           vo.getRegDttm() );
            item.put( "modDttm",           vo.getModDttm() );

            // 공톡은 작성자 강제 변경
            if( "G".equals( vo.getComuTypeCd() ) ){
                item.put( "memSeqno", 			1 );
                item.put( "memName",            adminMemName );
                item.put( "prflImgUrl", 		adminPrflImgUrl );
            }else{
                item.put( "memSeqno", 			vo.getMemSeqno() );
                item.put( "memName",            vo.getMemName() );
                item.put( "prflImgUrl", 		vo.getPrflImgUrl() );
            }
            items.add( item );
        }

        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put( "items", items );

        return resMap;
    }

}
