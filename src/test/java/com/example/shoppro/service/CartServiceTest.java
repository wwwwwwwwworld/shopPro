package com.example.shoppro.service;

import com.example.shoppro.dto.CartItemDTO;
import com.example.shoppro.repository.CartItemRepository;
import com.example.shoppro.repository.CartRepository;
import com.example.shoppro.repository.ItemRepository;
import com.example.shoppro.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Test
    public void addCartTest(){
        // 회원, 들어갈 아이템id, 수량
        String email = "bang@a.a";
        // 209, 8개
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setItemid(209L);
        cartItemDTO.setCount(8);

        Long result = cartService.addCart(cartItemDTO, email);

        log.info("저장된 장바구니아이템의 번호는? :" + result);
    }
}