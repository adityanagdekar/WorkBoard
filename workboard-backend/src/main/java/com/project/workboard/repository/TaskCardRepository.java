package com.project.workboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.workboard.entity.TaskCard;

@Repository
public interface TaskCardRepository extends JpaRepository<TaskCard, Long>{

}
