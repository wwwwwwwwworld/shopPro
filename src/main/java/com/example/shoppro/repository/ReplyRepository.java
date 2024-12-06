package com.example.shoppro.repository;

import com.example.shoppro.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    public Page<Reply> findByItemId (Long itemid, Pageable pageable);

    public List<Reply> findByItemId (Long itemid);
}
