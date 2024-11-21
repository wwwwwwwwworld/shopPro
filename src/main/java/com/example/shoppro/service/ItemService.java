package com.example.shoppro.service;

import com.example.shoppro.dto.ItemDTO;
import com.example.shoppro.entity.Item;
import com.example.shoppro.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    
    // 이미지 등록할 itemimgservice 의존성주입
    private final ItemImgService itemImgService;

    // 상품 등록

    public Long saveItem(ItemDTO itemDTO, List<MultipartFile> multipartFiles) throws IOException {

        // 아이템 dto를 엔티티로 변환
        Item item = modelMapper.map(itemDTO, Item.class);

        item =
        itemRepository.save(item);

        // 이미지등록 추가할예정
        itemImgService.saveImg(item.getId(), multipartFiles);

        return item.getId();
    }

    public ItemDTO read(Long id){

        Item item =
        itemRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);

        return itemDTO;
    }
}
