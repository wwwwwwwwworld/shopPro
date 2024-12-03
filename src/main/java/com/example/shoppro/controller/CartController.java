package com.example.shoppro.controller;

import com.example.shoppro.dto.CartItemDTO;
import com.example.shoppro.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;

    // 장바구니에 등록
    @PostMapping("/cart")
    public ResponseEntity order(@Valid CartItemDTO cartItemDTO, BindingResult bindingResult, Principal principal){

        log.info("브러우저에서 넘어온 값" + cartItemDTO);
        log.info("로그인이 되어있다면" + principal);

        // 유효성 검사
        if (bindingResult.hasErrors()){
            StringBuffer sb = new StringBuffer();

            List<FieldError> fieldErrorList
                    = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrorList){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 값이 잘 넘어왔다면
        String email = principal.getName();

        try {
            Long cartItemId = cartService.addCart(cartItemDTO, email);
            // 저장이 되었다면 브라우저로 다시 전송
            return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
