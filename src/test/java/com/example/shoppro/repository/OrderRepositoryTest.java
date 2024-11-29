package com.example.shoppro.repository;

import com.example.shoppro.entity.Order;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    @Transactional
    public void findOrderlistEmailTest(){
            // 에러나는 이유 현재 페이지어블을 안넣어줌 만들어서 넣어줄것

        Pageable pageable = PageRequest.of(0, 20);

        String email = "bang@a.a";

        List<Order> orderList =
        orderRepository.findByMemberEmailOrderByOrderDateDesc(email, pageable);
        orderList.forEach(a -> log.info(a));
        System.out.println("==================");
        System.out.println("==================");
        System.out.println("==================");

        List<Order> orderListAAA =
                orderRepository.findOrders(email, pageable);
        orderList.forEach(a -> log.info(a));
    }
    @Test
    @Transactional
    public void totaltest(){
        Long totalE = orderRepository.totalCount("bang@a.a");
        System.out.println(totalE);
        System.out.println(totalE);
        System.out.println(totalE);
        System.out.println(totalE);
        System.out.println(totalE);
        System.out.println(totalE);
        System.out.println(totalE);
        System.out.println(totalE);
        System.out.println(totalE);
    }
}