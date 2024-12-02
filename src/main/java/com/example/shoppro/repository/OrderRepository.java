package com.example.shoppro.repository;

import com.example.shoppro.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 구매이력
    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc")
    public List<Order> findOrders(String email, Pageable pageable);    //@Param("email")
    @Query("select count(o) from Order o where o.member.email = :email")
    public Long totalCount(String email);

    public List<Order> findByMemberEmailOrderByOrderDateDesc (String email, Pageable pageable);
}
