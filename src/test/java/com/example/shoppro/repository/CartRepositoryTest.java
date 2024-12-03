package com.example.shoppro.repository;

import com.example.shoppro.entity.Cart;
import com.example.shoppro.entity.CartItem;
import com.example.shoppro.entity.Member;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CartRepositoryTest {

    // 카트만들기 할 때 필요한 내용
    // 로그인 상태에서 email을 가지고 member entity를 가져와서
    // memberid로 혹은 email을 가지고
    // cart를 검색해서 if(cart == null){
    //      저장가능;
    // }else{  저장불가     }
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @Transactional
    public void readTest(){

        String email = "bang@a.a";
        Cart cart =
                cartRepository.findByMemberEmail(email);
        log.info(cart);

        List<CartItem> cartItemList =
        cartItemRepository.findByCartId(cart.getId());
        cartItemList.forEach(cartItem -> log.info(cartItem));
    }

    @Test
    public void makeCart(){
        // 먼저 회원을 찾는다.
        String email = "bang@a.a";

        Member member =
                memberRepository.findByEmail(email);

        // 찾은회원으로 장바구니가 있는지 확인

        Cart cart =
                cartRepository.findByMemberId(member.getId());
        if (cart == null){
            log.info("현재 카트가 존재하지 않습니다.");
            log.info("카트 생성");
            cart =
                    Cart.createCart(member);
            cartRepository.save(cart);

            // cartItem 추가
        }else {
            log.info("카트 생성 불가");

            // cartItem 추가
        }
    }
}