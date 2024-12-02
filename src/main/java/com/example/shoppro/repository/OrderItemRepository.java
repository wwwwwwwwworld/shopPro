package com.example.shoppro.repository;

import com.example.shoppro.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 구매이력
    public List<OrderItem> findByOrderId(Long itemid);
}
