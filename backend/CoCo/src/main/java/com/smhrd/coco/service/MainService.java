package com.smhrd.coco.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.smhrd.coco.converter.ImageConverter;
import com.smhrd.coco.converter.ImageToBase64;
import com.smhrd.coco.domain.TB_BOARD;
import com.smhrd.coco.domain.TB_BOARD_SKILL;
import com.smhrd.coco.domain.TB_BOOKMARK;
import com.smhrd.coco.domain.TB_CUST;
import com.smhrd.coco.mapper.BoardMapper;
import com.smhrd.coco.mapper.MainMapper;

@Service
public class MainService {
	@Autowired
	private MainMapper mapper;
	
	@Autowired
	private BoardMapper boardMapper; 

	// 프로필 이미지 보내기 
	public JSONObject profileImg(String cust_id) {
		
		JSONObject obj = new JSONObject();
		
		TB_CUST ImgPath = mapper.ImgPath(cust_id); 
		
		if(ImgPath != null) {
			// 이미지 변환
			ImageConverter<File, String> converter = new ImageToBase64();
			File file = new File("c:\\cocoImage\\" + ImgPath.getCust_img());

			String fileStringValue = null;
			
			try {
				fileStringValue = converter.convert(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			obj.put("CUST_IMG", fileStringValue);
		} else {
			obj.put("CUST_IMG", null); 
		}
		
		
		return obj; 
		
	
	}
	
	
	// 조회수 증가
	public int increaseViews(int board_id) {
		return mapper.increaseViews(board_id);
	}

	// 인기글(조회수기반) 가져오기
	public JSONArray popularList(String cust_id) {
		List<TB_BOARD> list = mapper.popularList();
		return boardList(list, cust_id);
	}

	// 북마크 저장
	public int bookmarkCheck(TB_BOOKMARK book) {
		return mapper.bookmarkCheck(book.getCust_id(), book.getBoard_id());
	}

	// 북마크 해제
	public int unBookmark(TB_BOOKMARK book) {
		System.out.println(book.getCust_id());
		System.out.println(book.getBoard_id());
		return mapper.unBookmark(book.getCust_id(), book.getBoard_id());
	}

	// 북마크된 게시글만 불러오기
	public JSONArray bookmarkList(String cust_id) {
		List<TB_BOARD> list = mapper.bookmarkList(cust_id);
		return boardList(list);

	}

	// 게시글 불러오기
	// 기술스택명 = null 포지션 = null 엔드포인트 : 1 최신순 게시글 가져오기
	// 기술스택명 = React 포지션 = null 엔드포인트 : 1 기술스택명에 맞는 최신순 게시글 가져오기
	// 기술스택명 = null, 포지션 = 백엔드 엔드포인트 : 1 포지션에 맞는 최신순 게시글 가져오기
	// 기술스택명 = React 포지션 = 백엔드 엔드포인트 : 1 기술스택과 포지션에 맞는 최신순 게시글 가져오기
	public JSONArray selectList(Map<String, Object> map) {
		System.out.println(map);

		String skill_name = (String) map.get("skill_name"); // 기술스택명
		String board_position = (String) map.get("board_position"); // 포지션
		int endpoint = (int) map.get("endpoint"); // 엔드포인트
		String cust_id = (String)map.get("cust_id"); 

		// 최신순 게시글 가져오기
		if (skill_name == null && board_position == null) {
			return recentList(cust_id, endpoint);
			// 기술스택명에 맞는 최신순 게시글 가져오기
		} else if (skill_name != null && board_position == null) {
			return skillList(cust_id, skill_name, endpoint);
			// 포지션에 맞는 최신순 게시글 가져오기
		} else if (skill_name == null && board_position != null) {
			return positionList(cust_id, board_position, endpoint);
		} else { // 기술스택과 포지션에 맞는 최신순 게시글 가져오기
			return skillPositionList(cust_id, skill_name, board_position, endpoint);
		}

	}

	// 최신순 게시글 가져오기
	public JSONArray recentList(String cust_id, int endpoint) {
		List<TB_BOARD> list = mapper.recentList( cust_id, endpoint);
		return boardList(list, cust_id);
	}

	// 스킬에 맞는 최신순 게시글 가져오기
	public JSONArray skillList(String cust_id,String skill_name, int endpoint) {
		List<TB_BOARD> list = mapper.skillList(cust_id, skill_name, endpoint);
		return boardList(list, cust_id);
	}

	// 포지션에 맞는 최신순 게시글 가져오기
	public JSONArray positionList(String cust_id,String board_position, int endpoint) {
		List<TB_BOARD> list = mapper.positionList( cust_id, board_position, endpoint);
		return boardList(list, cust_id);
	}

	// 기술스택과 포지션에 맞는 최신순 게시글 가져오기
	public JSONArray skillPositionList(String cust_id,String skill_name, String board_position, int endpoint) {
		List<TB_BOARD> list = mapper.skillPositionList(cust_id, skill_name, board_position, endpoint );
		return boardList(list, cust_id);
	}

	// 지원한 게시글 보기
	public JSONArray applyList(String cust_id) {

		List<TB_BOARD> list = mapper.applyList(cust_id);
		return boardList(list, cust_id);
	}

	// 내가 작성한 글 보기
	public JSONArray writeList(String cust_id) {

		List<TB_BOARD> list = mapper.writeList(cust_id);
		return boardList(list, cust_id);
	}

	// 각 게시글의 스킬리스트 + 게시글 정보를 보여주는 메서드
	public JSONArray boardList(List<TB_BOARD> list , String cust_id) {

		JSONArray jsonArray = new JSONArray();

		for (TB_BOARD pb : list) {

			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("board_id", pb.getBoard_id());
			map.put("cust_id", pb.getCust_id());
			map.put("board_title", pb.getBoard_title());
			map.put("board_period", pb.getBoard_period());
			map.put("board_deadline", pb.getBoard_deadline());
			map.put("board_openlink", pb.getBoard_openlink());
			map.put("board_content", pb.getBoard_content());
			map.put("board_dt", pb.getBoard_dt());
			map.put("board_views", pb.getBoard_views());
			map.put("board_position", pb.getBoard_position());
			map.put("board_members" , pb.getBoard_members());
			map.put("pro_title", pb.getPro_title());
			map.put("pro_img", pb.getPro_img());
			map.put("pro_link", pb.getPro_link());

			// 해당 게시글 프로필사진
			TB_CUST ImgPath = mapper.ImgPath(pb.getCust_id()); 
			
			if(ImgPath != null) {
				// 이미지 변환
				ImageConverter<File, String> converter = new ImageToBase64();
				File file = new File("c:\\cocoImage\\" + ImgPath.getCust_img());

				String fileStringValue = null;
				
				try {
					fileStringValue = converter.convert(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				map.put("cust_img", fileStringValue);
			} else {
				map.put("cust_img", null); 
			}
			
			// 해당 게시글 북마크 체크여부 
			int bmk = boardMapper.selectPostBmk(pb.getBoard_id(), cust_id);
			if( bmk >0) {
				map.put("bmkimg", "true");
			}else {
				map.put("bmkimg", "false");
			}
			
			
			// 해당게시글의 닉네임 가져오기
			String custNick = mapper.custNick(pb.getCust_id());
			map.put("cust_nick", custNick);

			// 해당게시글의 스킬리스트 가져오기
			List<TB_BOARD_SKILL> skillList = mapper.boardIdList(pb.getBoard_id());
			List<String> skillNames = new ArrayList<>();

			for (TB_BOARD_SKILL sk : skillList) {
				skillNames.add(sk.getSKILL_NAME());
			}

			map.put("skill_names", skillNames);
			jsonArray.add(new JSONObject(map));

		}

		return jsonArray;

	}

	public JSONArray boardList(List<TB_BOARD> list) {

		JSONArray jsonArray = new JSONArray();

		for (TB_BOARD pb : list) {

			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("board_id", pb.getBoard_id());
			map.put("cust_id", pb.getCust_id());
			map.put("board_title", pb.getBoard_title());
			map.put("board_period", pb.getBoard_period());
			map.put("board_deadline", pb.getBoard_deadline());
			map.put("board_openlink", pb.getBoard_openlink());
			map.put("board_content", pb.getBoard_content());
			map.put("board_dt", pb.getBoard_dt());
			map.put("board_views", pb.getBoard_views());
			map.put("board_position", pb.getBoard_position());
			map.put("board_members" , pb.getBoard_members());
			map.put("pro_title", pb.getPro_title());
			map.put("pro_img", pb.getPro_img());
			map.put("pro_link", pb.getPro_link());

			// 해당 게시글 프로필사진
			TB_CUST ImgPath = mapper.ImgPath(pb.getCust_id()); 
			
			if(ImgPath != null) {
				// 이미지 변환
				ImageConverter<File, String> converter = new ImageToBase64();
				File file = new File("c:\\cocoImage\\" + ImgPath.getCust_img());

				String fileStringValue = null;
				
				try {
					fileStringValue = converter.convert(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				map.put("cust_img", fileStringValue);
			} else {
				map.put("cust_img", null); 
			}
			
	

			// 해당게시글의 닉네임 가져오기
			String custNick = mapper.custNick(pb.getCust_id());
			map.put("cust_nick", custNick);

			// 해당게시글의 스킬리스트 가져오기
			List<TB_BOARD_SKILL> skillList = mapper.boardIdList(pb.getBoard_id());
			List<String> skillNames = new ArrayList<>();

			for (TB_BOARD_SKILL sk : skillList) {
				skillNames.add(sk.getSKILL_NAME());
			}

			map.put("skill_names", skillNames);
			jsonArray.add(new JSONObject(map));

		}

		return jsonArray;

	}

}