package com.example.shoppro.controller;

import com.example.shoppro.dto.CartDetailDTO;
import com.example.shoppro.dto.CartItemDTO;
import com.example.shoppro.dto.CartOrderDTO;
import com.example.shoppro.service.CartItemService;
import com.example.shoppro.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;

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

    @GetMapping("/cart")
    public String orderHist(Principal principal, Model model){
        // principal을 사용한다는 건 시큐리티에서 현재 로그인한 사람의 정보
        // 시큐리티의 .usernameParameter("email")에서 지정된 값

//        if (principal.getName() == null){
//            // 리다이렉트 // 시큐리티를 사용하고 있으니 안해도 된다.
//        }
        // 사용자에게 보여줄 장바구니 목록
        List<CartDetailDTO> cartDetailDTOList =
                cartService.getCartList(principal.getName());

        // 사용자에게 보여줄 장바구니 목록중에 CartDetailDTO (꼭 필요한 정보만 가공한 DTO)로 담은 List
        model.addAttribute("cartDetailDTOList", cartDetailDTOList);

        return "cart/cartList";
    }

    @PostMapping("/cartItem")
    public ResponseEntity updateCartItem(@Valid CartItemDTO cartItemDTO, BindingResult bindingResult, Principal principal){
        String email = principal.getName();

        log.info("수량변경을 위해서 넘어온 값 : " + cartItemDTO);
        if (bindingResult.hasErrors()){
            StringBuffer sb = new StringBuffer();

            List<FieldError> fieldErrorList =
                    bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrorList){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // service에서 카트아이템아이디를 통해서 카트아이템을 찾아서
        // 카트아이템의 수량을 현재 받은 cartItemIdDTO의 count로 변경

        try {
            cartItemService.updateCartItemId(cartItemDTO, email);
        }catch (Exception e){
            return new ResponseEntity<String>("장바구니 수량변경이 잘못되었습니다. 고객센터문의", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemDTO.getItemid(), HttpStatus.OK);
    }

    @DeleteMapping("/carItem/{cartItemId}")
    public ResponseEntity deleteCartItem(@PathVariable("cartItemId")Long cartItemId,
                                         Principal principal){
        if (!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정권한 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 장바구니 아이템 삭제
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping("/cart/orders")
    public ResponseEntity orderCartItem(
            @RequestBody CartOrderDTO cartOrderDTO, Principal principal
            ){
        log.info(cartOrderDTO);
        List<CartOrderDTO> cartOrderDTOList = cartOrderDTO.getOrderDTOList();

        if (cartOrderDTOList == null || cartOrderDTOList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }
        for (CartOrderDTO cartOrder : cartOrderDTOList){
            if (!cartService.validateCartItem(cartOrder.getCartItemId(),principal.getName())){
                return new ResponseEntity<String>("주문권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        Long orderId = cartService.orderCartItem(cartOrderDTOList, principal.getName());

        return new ResponseEntity<Long>(1L, HttpStatus.OK);
    }
}
