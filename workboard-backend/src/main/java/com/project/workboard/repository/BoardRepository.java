package com.project.workboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.workboard.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

}
