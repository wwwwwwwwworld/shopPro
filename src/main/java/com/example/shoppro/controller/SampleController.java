package com.example.shoppro.controller;

import com.example.shoppro.dto.BoardCaDTO;
import com.example.shoppro.dto.CategoryDTO;
import com.example.shoppro.service.BoardCaService;
import com.example.shoppro.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
public class SampleController {

    private final BoardCaService boardCaService;
    private final CategoryService categoryService;

    @GetMapping("/sample/sample")
    public void aaa(Model model, BoardCaDTO boardCaDTO){

        model.addAttribute("calist", categoryService.list());

        model.addAttribute("boardDTOList", boardCaService.list(boardCaDTO));

        // 자유게시판
        boardCaDTO.setCaid(1L);
        model.addAttribute("boardDTOListB", boardCaService.list(boardCaDTO));
        // 질문게시판
        boardCaDTO.setCaid(3L);
        model.addAttribute("boardDTOListA", boardCaService.list(boardCaDTO));
    }
    @PostMapping("/category/register")
    public String save(CategoryDTO categoryDTO){
        categoryService.saveca(categoryDTO);
        return "redirect:/sample/sample";
    }

    @PostMapping("/category/boardnew")
    public String save(BoardCaDTO boardCaDTO){
        boardCaService.saveca(boardCaDTO);
        return "redirect:/sample/sample";
    }
}
