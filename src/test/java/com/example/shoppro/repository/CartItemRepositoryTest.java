package com.example.shoppro.repository;

import com.example.shoppro.dto.CartDetailDTO;
import com.example.shoppro.entity.CartItem;
import com.example.shoppro.entity.ItemImg;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CartItemRepositoryTest {

    @Autowired
    CartItemRepository cartItemRepository;

    @Test
    @Transactional
    public void findCartDetailDTOListtest(){

        List<CartDetailDTO> cartDetailDTOList =
        cartItemRepository.findCartDetailDTOList(1L);

        cartDetailDTOList.forEach(cartDetailDTO -> log.info(cartDetailDTO));

        List<CartItem> cartItemList =
                cartItemRepository.findByCartId(1L);

        cartItemList.forEach(cartItem -> log.info(cartItem));

        for (CartItem cartItem : cartItemList){
            CartDetailDTO detailDTO = new CartDetailDTO();
            detailDTO.setItemNm(cartItem.getItem().getItemNm());
            detailDTO.setCount(cartItem.getCount());
            detailDTO.setPrice(cartItem.getItem().getPrice());
            List<ItemImg> itemImgList =
                cartItem.getItem().getItemImgList();

            for (ItemImg itemImg : itemImgList){
                if (itemImg != null && itemImg.getRepimgYn().equals("Y")){
                    detailDTO.setImgUrl(itemImg.getImgUrl());
                }
            }
            log.info(detailDTO);
        }
    }
}