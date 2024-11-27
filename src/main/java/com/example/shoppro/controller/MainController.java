package com.example.shoppro.controller;

import com.example.shoppro.dto.ItemDTO;
import com.example.shoppro.dto.PageRequestDTO;
import com.example.shoppro.dto.PageResponseDTO;
import com.example.shoppro.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final ItemService itemService;

    // 작성자에 의해서 // 판매중 // 일자 순으로 검색하는 ItemSearchDTO
    // page 현재 페이지 받고 // size받고 // Pageable도 페이징처리를 위해서 받고

    @GetMapping(value = "/")
    public String main(PageRequestDTO pageRequestDTO, Model model){ //  Optional<Integer> page

        // Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,10);        // 기존에 만들었던 list는 pageable과 email을 받음으로
        // 자신이 만든 item들의 list만 받아왔다.
        // 그러면 email이 null 그러니까 로그인을 안한사람은 못본다
        // 그래서 list를 추가로 만들던가 아니면 null일 경우와
        // null이 아닐 경우에 따라서 동적인 쿼리를 만들면 된다. 로그인한 사람은 유저야 메인이야
        // 그러면 자신이 만든 아이템만 봐야해 그래서 새로 만들자

        pageRequestDTO.setSize(6);
        PageResponseDTO<ItemDTO> pageResponseDTO =
        itemService.mainlist(pageRequestDTO);
        // 현재 페이지, 1페이지당 사이즈, 검색 키워드 등등이 있다면 그에 맞는 값들
        // start, end, pre, next 이런것들 총페이지수 등등
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "main";
    }
}
