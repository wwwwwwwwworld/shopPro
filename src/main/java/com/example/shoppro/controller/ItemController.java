package com.example.shoppro.controller;

import com.example.shoppro.dto.ItemDTO;
import com.example.shoppro.dto.PageRequestDTO;
import com.example.shoppro.dto.PageResponseDTO;
import com.example.shoppro.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ItemController {
    //이전 boardController 라고 생각하면 됨

    private final ItemService itemService;

    @GetMapping("/admin/item/new")
    public String itemForm(Model model, Principal principal){
        if(principal == null){
            //로그인이 안되어있다. 그래서 못들어온다.
//            return "redirect:/";
        }
        if(principal != null){
            log.info("현재 로그인 한 사람");
            log.info(principal.getName());
        }
        model.addAttribute("itemDTO", new ItemDTO());
        return "/item/itemForm";
    }
    @PostMapping("/admin/item/new")
    public String itemFormPost(@Valid ItemDTO itemDTO, BindingResult bindingResult,
                               List<MultipartFile> multipartFile, Model model){
        //들어오는값 확인
        log.info("들어오는값 확인" + itemDTO);

        if(multipartFile.get(0).isEmpty()){
            model.addAttribute("msg", "대표이미지는 꼭 등록해주세요");
            return "/item/itemForm";
        }

        // 파일 형식 검증: 모든 파일이 이미지 파일인지 확인
        for (MultipartFile img : multipartFile) {
            if (!img.isEmpty()) {
                // 로그 추가: 파일 정보 확인
                log.info("파일 이름: " + img.getOriginalFilename());
                log.info("파일 크기: " + img.getSize() + " bytes");
                log.info("파일 타입: " + img.getContentType());

                // 파일 형식 검증
                String contentType = img.getContentType();  // MIME 타입 확인
                log.info("업로드된 파일 MIME 타입: " + contentType);

                if (contentType == null || !contentType.startsWith("image")) {
                    model.addAttribute("msg", "이미지 파일만 업로드 가능합니다.");
                    return "/item/itemForm";  // 이미지 파일이 아니면 다시 폼으로 돌아감
                }
            }
        }

        if (bindingResult.hasErrors()){
            log.info("유효성검사 에러");
            log.info(bindingResult.getAllErrors()); //확인된 모든 에러 콘솔창 출력


            return "/item/itemForm";        //다시 이전 페이지
        }
        try {

            Long savedItemid =
                    itemService.saveItem(itemDTO, multipartFile);

            log.info("상품등록됨!!!!");
            log.info("상품등록됨!!!!" );
            log.info("상품등록됨!!!!");
            log.info("상품등록됨!!!!");

            return  "redirect:/admin/item/read?id=" + savedItemid;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("파일등록간 문제가 발생했습니다.");
            model.addAttribute("msg" , "파일등록을 잘해주세요");
            return "/item/itemForm";        //다시 이전 페이지
        }

    }

    @GetMapping("/admin/item/read")
    public String adminread(Long id, Model model, RedirectAttributes redirectAttributes){

        try {
            ItemDTO itemDTO =
                    itemService.read(id);
            log.info("itemDTO: " + itemDTO);  // 추가된 디버그 로그
            model.addAttribute("itemDTO", itemDTO);

            return "item/read";

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/admin/item/list";
            //item/list?msg=존재하지
        }

    }

    @GetMapping("/admin/item/list")
    public String adminlist(PageRequestDTO pageRequestDTO,
                            Model model, Principal principal){



//        model.addAttribute("list", itemService.list());
//      principal  로그인시 세션에 등록된 이름 우리는 email
        PageResponseDTO<ItemDTO> pageResponseDTO =
        itemService.list(pageRequestDTO, principal.getName());

        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "item/list";

    }


    @GetMapping("/admin/item/update")
    public String adminupdateGet(Long id, PageRequestDTO pageRequestDTO,
                                 Model model, Principal principal){


        //기존 read는 email을 확인하지 않았다. 
        //관리자는 자신의 글만 봐야함으로 자신의 상품을 검색하는 쿼리는 추가하자
        // 1 검색하고 값을가지고 확인하고 다시 맞다면 list , 만들기 쉽다
        // 2 검색부터 자신의 값을 가져오자  , 정확하다?
//        ItemDTO itemDTO =
//        itemService.read(id);
//        if(itemDTO.getCreateBy().equals(principal.getName())){
//            model.addAttribute("itemDTO", itemDTO);
//            return  "item/update";
//        }else {
//            return "redirect:/admin/item/list";
//        }

        ItemDTO itemDTO = itemService.read(id, principal.getName());
        if(itemDTO != null){
            model.addAttribute("itemDTO", itemDTO);
            return  "item/update";
        }else {
            return "redirect:/admin/item/list";
        }
    }

    @PostMapping("/admin/item/update")
    public String itemupdate(@Valid ItemDTO itemDTO, BindingResult bindingResult, List<MultipartFile> multipartFiles,
                             Integer[] delino, Long mainino){

        if (bindingResult.hasErrors()){
            log.info("유효성검사 에러");
            log.info(bindingResult.getAllErrors()); //확인된 모든 에러 콘솔창 출력


            return "/item/update";        //다시 이전 페이지
        }

        // 삭제번호가 없다면
//        if (delino != null){
//            log.info("삭제할 이미지가 없습니다.");
//        }else {
//            // 삭제 번호가 있다면
//            for (Integer ino : delino){
//                if (ino != null && ino.equals("")){
//                    log.info("삭제할 번호는 ino" + ino);
//                }
//            }
//        }

//        if (mainino == null){
//            // 대표이미지가 변경 ( itemid로 찾는다. )
//            // select * from item_img where item_id = 450 and repimg_yn = 'Y';
//            // 대표이미지를 0번 파일이 y로 하고
//            // 기존이미지 대표이미지 삭제 또는 기존대표이미지 url변경
//            log.info("대표이미지 변경");
//
//        }else {
//            // 대표 이미지가 변경 x
//            // 대표 이미지 체크여부가 다 N
//            log.info("대표이미지 미변경");
//        }
        itemService.update(itemDTO, itemDTO.getId(), multipartFiles, delino, mainino);

        return null;
    }

    @PostMapping("admin/item/del")
    public String delitem(Long id){

        log.info("삭제할 아이템번호 : " + id);

        itemService.remove(id);

        return "redirect:/admin/item/list";
    }

    @GetMapping("item/read")
    public String read(Long id, Model model, RedirectAttributes redirectAttributes){

        try {
            ItemDTO itemDTO =
                    itemService.read(id);
            model.addAttribute("itemDTO", itemDTO);

            return "item/itemDtl";

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/";
            //item/list?msg=존재하지
        }
    }
}
