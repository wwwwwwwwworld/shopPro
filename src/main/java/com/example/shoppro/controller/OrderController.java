package com.example.shoppro.controller;

import com.example.shoppro.dto.OrderDTO;
import com.example.shoppro.dto.OrderHistDTO;
import com.example.shoppro.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

        // 만약에 아이템 id가 없다면
        // 만약에 수량이 없다면
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
        Long result =
        orderService.order(orderDTO, principal.getName());

        if (result == null){
            return new ResponseEntity<String>("주문수량이 판매가능수량보다 많습니다.", HttpStatus.OK);
        }
        return new ResponseEntity<String>("주문완료", HttpStatus.OK);
    }

    @GetMapping({"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page,
                            Principal principal, Model model){

        log.info("진입");
        if (principal == null){
            log.info("로그인이 필요함");

            return "redirect:/members/login";
        }else {
            log.info("로그인 상태");

        }
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        log.info(pageable);

        String email = principal.getName();

        Page<OrderHistDTO> orderHistDTOPage =
        orderService.getOrderList(email, pageable);
        // 페이징처리에 필요하던것들 start end next prev t/f    total

        // 단방향이라면
        // order, orderItem을 가져온다 pk값 email을 가져온다

        model.addAttribute("orders", orderHistDTOPage);
        // html 들어가서 getContent() 함수 호출
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity cancelOrder(
            @PathVariable("orderId") Long orderId, Principal principal){

        // orderId는 취소할 orderId이다.
        // orderId를 삭제하고, orderItem에서 orderId를 참조하고 있는 orderItem을 삭제한다.
        // 단방향일 경우 orderItem을 먼저 삭제(자식부터 삭제) 하고
        // orderId를 삭제 하면 된다. 부모에 달린 댓글을 먼저 삭제하고 부모글을 지운다
        log.info("취소할 주문번호" + orderId);
        log.info("취소할 주문번호로 달린 아이템들");

        if (!orderService.validateOrder(orderId, principal.getName())){
            // 내 제품이 아니다.
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 취소를 한다. orderStatus를 cancel로 바꾸고, 주문했던 아이템들의 수량도 돌려놓고
        // 주문에 달린 주문아이템들은 데이터를 가지고 있다.

        orderService.cancelOrder(orderId);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
