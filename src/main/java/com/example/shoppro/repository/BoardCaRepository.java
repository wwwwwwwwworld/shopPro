package com.example.shoppro.repository;

import com.example.shoppro.entity.BoardCa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCaRepository extends JpaRepository<BoardCa, Long> {

    public List<BoardCa> findByCategoryId (Long id);
}
