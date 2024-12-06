package com.example.shoppro.service;

import com.example.shoppro.constant.OrderStatus;
import com.example.shoppro.dto.OrderDTO;
import com.example.shoppro.dto.OrderHistDTO;
import com.example.shoppro.dto.OrderItemDTO;
import com.example.shoppro.entity.*;
import com.example.shoppro.repository.ItemRepository;
import com.example.shoppro.repository.MemberRepository;
import com.example.shoppro.repository.OrderRepository;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    // 주문 order, orderItem
    // 주문할 아이템, 주문할 수량, 주문하는 사람 이런게 있는게 아니라
    // 위에 내용이 있는 주문리스트만 필요

    // 단 주문목록들이 들어있는 주문 row한개는 누구의 주문인지 알기 위해서
    // email을 받는다.

    // 내 주문이 맞는지 체크
    public boolean validateOrder(Long orderId, String email){
        Member member =
                memberRepository.findByEmail(email);
        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(EntityNotFoundException::new);
        // 주문 id로 찾은 주문테이블의 회원 참조 eamil과 현재 로그인 한사람을 비교
        if (!StringUtils.equals(member.getEmail(), order.getMember().getEmail())){
            return false;
        }
        return true;
    }

    // 주문 취소
    public void cancelOrder(Long orderId){
        // 삭제할 번호를 받아서 삭제

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // 주문상태인 orderStatus를 주문취소상태로 변경
        order.setOrderStatus(OrderStatus.CANCEL);
        // 주문 자식인 주문아이템들의 주문수량을 가지고 item의 주문수량에 더한다.

        for (OrderItem orderItem : order.getOrderItemList()){
            orderItem.getItem().setStockNumber(
              orderItem.getItem().getStockNumber() + orderItem.getCount()
            );
        }
    }

    public Long order(OrderDTO orderDTO, String email){ // principal.getName()로 가져온다. 로그인을 했다면
        // 현재 선택한 아이템의 id는 orderDTO로 들어온다 이값으로 판매중인 item Entity를 가져온다.
        Item item = itemRepository.findById(orderDTO.getItemId())
                .orElseThrow(EntityNotFoundException::new); // 사려는 아이템을 찾지 못했다면 예외처리

        // email을 통해서 현재 로그인한 사용자를 가져온다.
        Member member =
                memberRepository.findByEmail(email);

        // 조건 : 현재 판매중인 item의 수량이 구매하려는 수량보다 크거나 같아야 한다.
        if (item.getStockNumber() >= orderDTO.getCount()){
            // orderItem은 구매하려는 아이템들이고 구매아이템들은 구매목록을 참조한다.

            // orderItem 생성
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);    // 구매 item
            orderItem.setCount(orderDTO.getCount());    // 수량
            orderItem.setOrderPrice(item.getPrice());   // 구매 아이템 금액

            // 판매하려는 아이템의 수량은 구매수량을 뺀 수량으로 변경해야한다.
            item.setStockNumber(item.getStockNumber() - orderDTO.getCount());
            // sql update 구문확인 필요

            // 주문아이템들이 들어갈 주문테이블을 만들어준다. 주문아이템들이 참조하는 주문
            Order order = new Order();
            order.setMember(member);    // 구매한 사람 email로 찾아온 entity객체

            order.setOrderItemList(orderItem);  // 주문목록 이건 새로 만든 setOrderItemList이다.
            // 어떻게 만들었는지 Order객체를 참조할 것     그리고 아래 저장직전 주석 참조

            order.setOrderStatus(OrderStatus.ORDER);    // 주문상태
            order.setOrderDate(LocalDateTime.now());    // 주문시간
            // 이렇게 만들어진 order 주문 객체를 저장하기 전에
            // orderItem에서 private Order order;를 set해줌으로써
            // 양방향이기에 같이 등록되며 같이 등록될때 pk값도 같이 참조해준다.
            orderItem.setOrder(order);  // 이 부분이 빠졌었음

            // 실제 저장은 order만 하지만 order에
            // @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            //         orphanRemoval = true, fetch = FetchType.LAZY)
            // private List<OrderItem> orderItemList = new ArrayList<>();
            // 이부분을 set해줬기에 둘다 저장된다.
            order = orderRepository.save(order);

            return order.getId();
        }else {
            return null;
        }
    }
    public Long orders(List<OrderDTO> orderDTOList, String email){

        // 주문을 했다면 판매하고 있는 상품의 수량 변경

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();
        Order order = new Order();

        for (OrderDTO orderDTO : orderDTOList){
            Item item =
                    itemRepository.findById(orderDTO.getItemId())
                            .orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(orderDTO.getCount());
            orderItem.setOrderPrice(item.getPrice());
            orderItem.setOrder(order);

            item.setStockNumber(item.getStockNumber() - orderDTO.getCount());

            orderItemList.add(orderItem);
        }
        order.setMember(member);
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItemList(orderItemList);

        orderRepository.save(order);

        return order.getId();
    }
    // 구매이력
    public Page<OrderHistDTO> getOrderList(String email, Pageable pageable){
                                // repository에서 필요한 email
        // 구매목록
        List<Order> orderList = orderRepository.findOrders(email, pageable);

        // 페이징처리를 위한 총 구매목록의 수
        Long totalCount = orderRepository.totalCount(email);

        // 구매목록의 구매아이템들을 만들어주기위한 List
        List<OrderHistDTO> orderHistDTOList = new ArrayList<>();

        // EntityToDto // 주문, 주문아이템들, 주문아이템들의 이미지
        for (Order order : orderList){
            OrderHistDTO orderHistDTO = new OrderHistDTO();
            orderHistDTO.setOrderId(order.getId());
            orderHistDTO.setOrderDate(order.getOrderDate());
            orderHistDTO.setOrderStatus(order.getOrderStatus());

            List<OrderItem> orderItemList = order.getOrderItemList();

            for (OrderItem orderItem : orderItemList){
                OrderItemDTO orderItemDTO = new OrderItemDTO();
                orderItemDTO.setId(order.getId());
                orderItemDTO.setItemNm(orderItem.getItem().getItemNm());
                orderItemDTO.setOrderPrice(orderItem.getOrderPrice());
                orderItemDTO.setCount(orderItem.getCount());

                // 아이템 주문아이템들중 1개 A
                List<ItemImg> itemImgList = orderItem.getItem().getItemImgList();
                // A에 달려있는 이미지들
                // 그중에 대표이미지

                for (ItemImg itemImg : itemImgList){
                    if (itemImg.getRepimgYn().equals("Y")){
                        orderItemDTO.setImgUrl(itemImg.getImgUrl());
                    }
                }
                orderHistDTO.addOrderItemDTO(orderItemDTO);

                // 디비에서 새로 가져온다 쿼리 부담 있다 Y
                // 현재 orderItem에서 for문으로 Y대표이미지 찾는다.
            }
            orderHistDTOList.add(orderHistDTO);
        }
        return new PageImpl<OrderHistDTO>(orderHistDTOList, pageable, totalCount);
    }
}
