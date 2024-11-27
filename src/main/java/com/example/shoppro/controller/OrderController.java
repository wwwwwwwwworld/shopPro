package com.example.shoppro.controller;

import com.example.shoppro.dto.OrderDTO;
import com.example.shoppro.service.OrderService;
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
public class OrderController {

    private final OrderService orderService;

    // 주문하기     // 주문하기는 상품의 읽기 페이지에서
    // 사용자가 볼수 있으므로, 따로 get으로 읽기페이지는 만들지 않는다.
    // 대신 그 페이지에서 보내주는 데이터를 바탕으로 orders, orderItem
    // 에 데이터를 입력하는 역할을 한다.
    // 2가지 방법
    // 1. 일반적인 컨트롤러로 데이터를 받고 다시 주문내역으로 이동한다.
        // 일반적인 컨트롤러를 쓰더라도 responseEntity를 사용하여 rest처럼 데이터만을
        // 받을 수 있다.
    // 2. rest 컨트롤러로 데이터를 받고 다시 주문내역으로 이동한다.
    // @RequestBody 쓰면 application/json; charset = utf-8, 이건 꼭 post에서 씀
    // 키 : 벨류 중에 키가 "키" 이렇게 들어가야 함

    @PostMapping("/order")
    public ResponseEntity order(@Valid OrderDTO orderDTO, BindingResult bindingResult, Principal principal){

        // 유효성 검사
        if (bindingResult.hasErrors()){
            StringBuffer sb = new StringBuffer();   // String

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();  // 각 필드 에러

            for (FieldError fieldError : fieldErrors){
                log.info( "필드 : " + fieldError.getField() + "메시지 : " + fieldError.getDefaultMessage());
                sb.append(fieldError.getDefaultMessage());
            }
            log.info(sb.toString());
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 주문은 로그인한 사용자 물론 판매자또한 다른아이템을 자기아이템을
        // 살수 있다. 로그인한 사람만 이 주소로 들어올 수 있다. 시큐리티에서 확인
        // 또는 들어올때 principal!=null 이라면 로그인을 한사람이다.

        log.info("사용자가 현재 주문하려는 내용" + orderDTO);

        // 저장을 해야한다.
        orderService.order(orderDTO, principal.getName());

        return new ResponseEntity<String>("주문완료", HttpStatus.OK);
    }
}
