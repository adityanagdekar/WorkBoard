package com.project.workboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.workboard.entity.TaskMember;
import com.project.workboard.entity.TaskMemberId;

public interface TaskMemberRepository extends JpaRepository<TaskMember, TaskMemberId> {

}
