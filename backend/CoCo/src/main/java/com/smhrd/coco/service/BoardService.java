package com.smhrd.coco.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.smhrd.coco.domain.SkillImgList;
import com.smhrd.coco.domain.TB_APPLY;
import com.smhrd.coco.domain.TB_BOARD;
import com.smhrd.coco.domain.TB_BOARD_IMG;

import com.smhrd.coco.domain.TB_BOARD_SKILL;
import com.smhrd.coco.domain.TB_BOOKMARK;
import com.smhrd.coco.domain.TB_CUST;
import com.smhrd.coco.domain.TB_NOTICE;
import com.smhrd.coco.mapper.BoardMapper;
import com.smhrd.coco.converter.ImageConverter;
import com.smhrd.coco.converter.ImageToBase64;

@Service
public class BoardService {

   @Autowired
   ResourceLoader resourceLoader;
   // 특정 경로에 있는 파일을 가지고 오기
   
   @Autowired
   private MainService mainService;

   @Autowired
   private BoardMapper mapper;

	// TB_BOARD 정보 저장
	public int postSaveBoard(TB_BOARD board) {
		return mapper.postSaveBoard(board);
	}

	// TB_BOARD 정보 업데이트
	public int postUpdateBoard(TB_BOARD board) {
		return mapper.postUpdateBoard(board);
	}

	// TB_BOARD_SKILL 정보 저장
	public int postSaveSkill(String skill, int board_id) {
		return mapper.postSaveSkill(board_id, skill);
	}

	// TB_BOARD_SKILL 정보 삭제
	public int postDeleteSkill(int board_id) {
		return mapper.postDeleteSkill(board_id);
	}

	// TB_BOARD_SKILL 정보 업데이트
	public int postUpdateSkill(TB_BOARD_SKILL skill, int board_id) {
		return mapper.postUpdateSkill(skill, board_id);
	}

	// TB_BOARD_IMG 정보저장
	public int postSaveImg(TB_BOARD_IMG img) {
		return mapper.postSaveImg(img);
	}

	// TB_BOARD_IMG 정보 업데이트
	public int postUpdateImg(TB_BOARD_IMG img, int board_id) {
		return mapper.postUpdateImg(img, board_id);
	}

	// 게시글 작성시 APPLY 게시판 응모여부에 H(호스트)로 저장하기
	public int postSaveApply(int board_id, String cust_id) {
		return mapper.postSaveApply(board_id, cust_id);
	}

	// 선택한 게시글 내용 보내기 (TB_BOARD, TB_BOARD_SKILL, TB_BOARD_IMG, TB_BOOKMARK,
	// TB_APPLY)
	public JSONArray selectPostViews(int board_id, String cust_id) {

		mainService.increaseViews(board_id);
		TB_BOARD board = mapper.selectPostBoard(board_id);
		List<TB_BOARD_SKILL> skill = mapper.selectPostSkill(board_id);
		TB_BOARD_IMG img = mapper.selectPostImag(board_id);
		int bmk = mapper.selectPostBmk(board_id, cust_id);
		int apply = mapper.selectPostApply(board_id, cust_id);
		TB_CUST createCust = mapper.selectPostCust(board.getCust_id());

		//
		board = setDeadline(board);
		String dDay = calculateDday(board);

		// 각자 있는 스킬 배열로 묶기
		String[] skillList = new String[skill.size()];

		for (int i = 0; i < skill.size(); i++) {
			skillList[i] = skill.get(i).getSKILL_NAME();
		}
		// 게시판 사진 파일 찾아서 바이트형태로 변환하기
		if (img != null) {
			img.setBOARD_IMG(converter(img.getBOARD_IMG()));
		}

		// 회원 프로필 사진 찾아서 바이트 형태로 변환하기
		if (createCust != null)
			createCust.setCust_img(converter(createCust.getCust_img()));

      // JSONArray 에 모두 담기
      JSONArray jsonArray = new JSONArray();

      JSONObject obj = new JSONObject();
      obj.put("TB_BOARD", board);
      obj.put("TB_BOARD_SKILL", skillList);
      obj.put("TB_BOARD_IMG", img);
      obj.put("TB_BOOKMARK", bmk);
      obj.put("TB_APPLY", apply);
      obj.put("D_day", dDay);
      obj.put("createCust", createCust);

		jsonArray.add(obj);
		return jsonArray;
	}

	// 게시글에 지원시 APPLY 테이블에 정보 추가
	public int postApply(int board_id, String sender_id) {
		return mapper.postApply(board_id, sender_id);

	}

	// 게시글 지원취소하기
	public int unPostApply(int board_id, String cust_id) {
		return mapper.unPostApply(board_id, cust_id);
	}


	// 프로젝트 링크 보내기(없는지 확인하여 있다면 생성)
	public String getOrCreateProLink(int board_id) {
		return mapper.getOrCreateProLink(board_id);
	}

	// 게시글 모집마감
	public int postDeadline(int board_id) {

		Date date = new Date();
		SimpleDateFormat toDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = toDay.format(date);

		return mapper.postDeadline(board_id, today);
	}

	// 게시글 삭제 클릭시 board_id => admin 관리자로 변경
	public int postDelete(int board_id) {
		return mapper.postDelete(board_id);
	}

/////////////////////////////////// 메서드 ////////////////////////////////////////////

	public String calculateDday(TB_BOARD board) {
		Date date = new Date();
		SimpleDateFormat toDay = new SimpleDateFormat("yyyy-MM-dd");
		String today = toDay.format(date);
		String secondDate = board.getBoard_deadline();

		String[] start = today.split("-");
		int year1 = Integer.parseInt(start[0]);
		int month1 = Integer.parseInt(start[1]);
		int day1 = Integer.parseInt(start[2]);

		String[] end = secondDate.split("-");
		int year2 = Integer.parseInt(end[0]);
		int month2 = Integer.parseInt(end[1]);
		int day2 = Integer.parseInt(end[2]);

		LocalDate startDate = LocalDate.of(year1, month1, day1);
		LocalDate endDate = LocalDate.of(year2, month2, day2);
		System.out.println("start :" + startDate);
		System.out.println("end :" + endDate);

		long per = ChronoUnit.DAYS.between(startDate, endDate);

		String dDay = per + "";

		return dDay;
	}

	public TB_BOARD setDeadline(TB_BOARD board) {
		// 게시글 등록일시 시간 빼서 저장하기
		String date = board.getBoard_dt();
		String[] dateSplit = date.split(" ");
		String boardDay = dateSplit[0];
		board.setBoard_dt(boardDay);

		// 게시글 모집마감일 시간빼서 저장하기
		String startDay = board.getBoard_deadline();
		String[] startSplit = startDay.split(" ");
		String boardStart = startSplit[0];
		board.setBoard_deadline(boardStart);

		return board;

	}

	public String converter(String img) {
		ImageConverter<File, String> converter = new ImageToBase64();
		String fileStringValue = null;
		try {
			File file = new File("c:\\cocoImage\\" + img);
			try {
				fileStringValue = converter.convert(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return fileStringValue;
	}

}