package com.example.shoppro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString //(exclude = "itemImgList")  //toString 변수 제외할 변수명
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart_item")
public class CartItem {

    @Id
    @Column(name = "cart_item_id")       //테이블에서 매핑될 컬럼
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id") // 매핑할 외래키 지정
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") // 매핑할 외래키 지정
    private Item item;

    // 수량 산수량 장바구니에 담긴수량
    // 아이템 한 row당
    private int count;
}
