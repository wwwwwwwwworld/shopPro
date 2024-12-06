package com.example.shoppro.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CartOrderDTO {

    private Long cartItemId;

    private List<CartOrderDTO> orderDTOList;
    // 장바구니에서 여러개의 상품을 주문하므로
    // 자기 자신을 List로 가지고 있도록
}
