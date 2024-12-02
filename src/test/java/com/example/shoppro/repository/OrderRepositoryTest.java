package com.example.shoppro.repository;

import com.example.shoppro.dto.OrderDTO;
import com.example.shoppro.dto.OrderHistDTO;
import com.example.shoppro.dto.OrderItemDTO;
import com.example.shoppro.entity.Order;
import com.example.shoppro.entity.OrderItem;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    @Transactional
    public void re() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = orderRepository.findOrders("bang@a.a", pageable);
        orderList.forEach(order -> log.info(order));

        ModelMapper modelMapper = new ModelMapper();

        List<OrderHistDTO> orderHistDTOList =
                orderList.stream().map(order -> modelMapper.map(order, OrderHistDTO.class)
                        .setOrderItemDTOList(
                                order.getOrderItemList().stream().map(orderItem ->
                                        modelMapper.map(orderItem, OrderItemDTO.class)).collect(Collectors.toList())
                        )).collect(Collectors.toList());

        orderHistDTOList.forEach(a -> log.info(a));
    }

    @Test
    @Transactional
    public void findorder(){
        // 나의 장바구니 찾기 // 단방향이라는 가정 자식의 데이터가 오겠지만
        // 안온다 생각
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList =
        orderRepository.findByMemberEmailOrderByOrderDateDesc("bang@a.a",pageable);

        ModelMapper modelMapper = new ModelMapper();

        List<OrderHistDTO> orderDTOList =
                orderList.stream().map(order -> modelMapper.map(order, OrderHistDTO.class)).collect(Collectors.toList());

        // 부모에 속하는 자식의 모든값
        orderItemRepository.findByOrderId(orderList.get(0).getId());

        // 9번 10번 11번의 주문들이 있음
        for (OrderHistDTO orderHistDTO : orderDTOList){
            // pk 값으로 부모 pk값으로 자식들을 가져온다.
            List<OrderItem> orderItemList =
            orderItemRepository.findByOrderId(orderHistDTO.getOrderId());

            List<OrderItemDTO> orderItemDTOS =
            orderItemList.stream().map(orderItem -> modelMapper.map(orderItem, OrderItemDTO.class)
                    .setPkid(orderItem.getOrder()
                            .getId().intValue())).collect(Collectors.toList());
            orderHistDTO.setOrderItemDTOList(orderItemDTOS);

        }
        orderDTOList.forEach(a -> log.info(a));
    }

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
    }
}