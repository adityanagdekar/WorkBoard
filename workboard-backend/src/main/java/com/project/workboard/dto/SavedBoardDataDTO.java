package com.project.workboard.dto;

import java.util.Arrays;

public class SavedBoardDataDTO {
	private int boardId;
	private MemberDataDTO[] members;
	private String boardName;
	private String boardDesc;
	// private int[] memberIds;
	
	public static class MemberDataDTO {
		private int memberId;
		private int memberRole;
		
		public MemberDataDTO() {}

		public MemberDataDTO(int memberId, int memberRole) {
			super();
			this.memberId = memberId;
			this.memberRole = memberRole;
		}

		public int getMemberId() {
			return memberId;
		}

		public void setMemberId(int memberId) {
			this.memberId = memberId;
		}

		public int getMemberRole() {
			return memberRole;
		}

		public void setMemberRole(int memberRole) {
			this.memberRole = memberRole;
		}

		@Override
		public String toString() {
			return "MemberIdRole [memberId=" + memberId + ", memberRole=" + memberRole + "]";
		}
		
	}
	
	public SavedBoardDataDTO() {}
	
	public SavedBoardDataDTO(int boardId, MemberDataDTO[] members, 
			String boardName, String boardDesc) {
		
		// prev. onstructor args. -> int boardId, int[] memberIds, 
		// String boardName, String boardDesc
		super();
		this.boardId = boardId;
		this.members = members;
		this.boardName = boardName;
		this.boardDesc = boardDesc;
		// this.memberIds = memberIds;
	}

	public int getBoardId() {
		return boardId;
	}
	
	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public MemberDataDTO[] getMembers() {
		return members;
	}

	public void setMembers(MemberDataDTO[] members) {
		this.members = members;
	}
	
	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	
	public String getBoardDesc() {
		return boardDesc;
	}

	public void setBoardDesc(String boardDesc) {
		this.boardDesc = boardDesc;
	}

	@Override
	public String toString() {
		return "SavedBoardDataDTO [boardId=" + boardId + 
				", members=" + memberToString(members) + ", boardName="
				+ boardName + ", boardDesc=" + boardDesc + "]";
	}
	
	private String memberToString(MemberDataDTO[] members) {
		StringBuffer membersSbf = new StringBuffer();
		for(MemberDataDTO member: members) {
			membersSbf.append(" "+member.toString()+"\n");
		}
		return membersSbf.toString();
	}
	
	
	/*@Override
	public String toString() {
		return "SavedBoardDataDTO [boardId=" + boardId + ", memberIds=" + Arrays.toString(memberIds) + ", boardName="
				+ boardName + ", boardDesc=" + boardDesc + "]";
	}*/

	
	/*public int[] getMemberIds() {
		return memberIds;
	}
	
	public void setMemberIds(int[] memberIds) {
		this.memberIds = memberIds;
	}*/
}
