package com.project.workboard.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.project.workboard.dto.BoardEvent;

@Component
public class NotifyService {
	public void notifyBoardViewers(int boardId, Object payload, BoardEvent.Type type, 
			SimpMessagingTemplate messagingTemplate) {
		System.out.println("Inside NotifyService :: notifyBoardViewers");
		BoardEvent evt = new BoardEvent();
		evt.setType(type);
		evt.setBoardId(boardId);
		evt.setPayload(payload);
		evt.setVersion(System.currentTimeMillis());

		String dest = "/topic/board." + boardId;
		messagingTemplate.convertAndSend(dest, evt);
	}
}
