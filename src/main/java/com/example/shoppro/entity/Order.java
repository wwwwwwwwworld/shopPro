package com.example.shoppro.entity;

import com.example.shoppro.constant.OrderStatus;
import com.example.shoppro.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString //(exclude = "itemImgList")  //toString 변수 제외할 변수명
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @Column(name = "order_id")       //테이블에서 매핑될 컬럼
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne   // 회원 엔티티와 일대일로 매핑
    @JoinColumn(name = "member_id") // 매핑할 외래키 지정
    private Member member;

    // 주문상품 엔티티와 일대 다 매핑
    // 외래키(order_id)가 order_item 테이블에 있으면 연관관계의
    // 주인은 orderItem 엔티티 order는 주인이 아니므로
    // mappedby 속성으로 주인설정 : order를 mappedby에 적어준 이유
    // orderItem에 있는 order에 의해 관리된다
    // orphanremoval = true

    // Entity를 가져왔을때 order.orderItemList 값이 있다면
    // order.orderItemList.remove(0);

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList = new ArrayList<>();

    public void setOrderItemList(OrderItem item) {
        this.orderItemList.add(item);
    }

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
