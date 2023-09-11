
package com.smhrd.coco.service;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.smhrd.coco.domain.TB_BOARD;
import com.smhrd.coco.domain.TB_BOARD_IMG;

import com.smhrd.coco.domain.TB_BOARD_SKILL;
import com.smhrd.coco.mapper.BoardMapper;



@Service
public class BoardService {
	
	@Autowired
	private BoardMapper mapper;
	
	//TB_BOARD 정보 저장
	public int postSaveBoard(TB_BOARD board) {
		return mapper.postSaveBoard(board);
	}
	
	//TB_BOARD_SKILL 정보 저장
	public int postSaveSkill(TB_BOARD_SKILL skill) {
		return mapper.postSaveSkill(skill);
	}
	
	//TB_BOARD_IMG 정보저장
	public int postSaveImg(TB_BOARD_IMG img) {
		return mapper.postSaveImg(img);
	}
	

}
