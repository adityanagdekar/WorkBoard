package com.project.workboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.workboard.entity.BoardMember;
import com.project.workboard.entity.BoardMemberId;

public interface BoardMemberRepository extends JpaRepository<BoardMember, BoardMemberId> {
}
