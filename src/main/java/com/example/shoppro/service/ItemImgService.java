package com.example.shoppro.service;

import com.example.shoppro.entity.Item;
import com.example.shoppro.entity.ItemImg;
import com.example.shoppro.repository.ItemImgRepository;
import com.example.shoppro.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ItemImgService {

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;
    private final ItemRepository itemRepository;

    @Value("${itemImgLocation}")
    private String value;
    //이미지 등록
    public void saveImg(Long id, List<MultipartFile> multipartFile) throws IOException {
        //이미지 등록은 어디에 무엇을 할것인가
        //이미지는 아이템꺼
        //아이템 pk 이미지 파일 이미지파일을 경로를 잘라서
        // 경로와 함께 이름을 저장한다.

        log.info("아이템이미지서비스로 들어온 id" + id);
        if(multipartFile != null){
            for (MultipartFile img : multipartFile) {
                if(!img.isEmpty()){
                    log.info("아이템이미지서비스로 들어온 이미지" + img.getOriginalFilename());
                    //물리적인 저장
                    String savedFileName =      //uuid가 포함된 물리적인 파일이름
                    fileService.uploadFile(img);


                    // db저장
                    //엔티티를 가져왔다면 중복코드를 사용할 필요가 없어진다.해볼것
                    Item item =
                    itemRepository.findById(id).get();

                    String imgUrl = "/images/item/" + savedFileName;

                    ItemImg itemImg = new ItemImg();
                    itemImg.setItem(item);      //본문 // 이미지가 달릴 아이템
                    itemImg.setImgName(savedFileName);       //uuid포함 저장될 이름
                    itemImg.setImgUrl(imgUrl);        //경로
                    itemImg.setOriImgName(img.getOriginalFilename());    // 원래이름
                    //대표이미지 여부 확인
                    if(multipartFile.indexOf(img) == 0){
                        itemImg.setRepimgYn("Y");      //대표이미지
                    }else {
                        itemImg.setRepimgYn("N");      //대표이미지
                    }
                    itemImgRepository.save(itemImg);

                }
            }
        }
    }

    public void update(Long id, List<MultipartFile> multipartFile, Long mainino) throws IOException {

        log.info("아이템이미지서비스로 들어온 id" + id);
        if(multipartFile != null){
            for (MultipartFile img : multipartFile) {
                if(!img.isEmpty()){
                    log.info("아이템이미지서비스로 들어온 이미지" + img.getOriginalFilename());
                    //물리적인 저장
                    String savedFileName =      //uuid가 포함된 물리적인 파일이름
                            fileService.uploadFile(img);

                    // db저장
                    //엔티티를 가져왔다면 중복코드를 사용할 필요가 없어진다.해볼것
                    Item item =
                            itemRepository.findById(id).get();

                    String imgUrl = "/images/item/" + savedFileName;

                    ItemImg itemImg = new ItemImg();
                    itemImg.setItem(item);      //본문 // 이미지가 달릴 아이템
                    itemImg.setImgName(savedFileName);       //uuid포함 저장될 이름
                    itemImg.setImgUrl(imgUrl);        //경로
                    itemImg.setOriImgName(img.getOriginalFilename());    // 원래이름
                    //대표이미지 여부 확인
                    if (mainino == null){
                        if(multipartFile.indexOf(img) == 0){

                            itemImg =
                            itemImgRepository.findByItemIdAndRepimgYn(id, "Y");
                            itemImg.setItem(item);      //본문 // 이미지가 달릴 아이템
                            itemImg.setImgName(savedFileName);       //uuid포함 저장될 이름
                            itemImg.setImgUrl(imgUrl);        //경로
                            itemImg.setOriImgName(img.getOriginalFilename());    // 원래이름

//                            itemImg.setRepimgYn("Y");      //대표이미지
                        }else {
                            itemImg.setRepimgYn("N");
                        }
                    }else {
                        itemImg.setRepimgYn("N");
                    }

                    itemImgRepository.save(itemImg);

                }
            }
        }
    }

    public void removeimg(Long id){
        // 물리적파일을 삭제하기위해서 데이터를 가져온다.
        ItemImg itemImg =
                itemImgRepository.findById(id).get();

        // 저장경로 value 위에 있는 value 이미 이건 파일 service 안에 있음

        // 물리적파일 삭제
        // 삭제하려면 경로 및 사진파일명 필요
        fileService.removefile(itemImg.getImgName());
        // 반환값에 따라 물리파일이 삭제가 되었다면 안되었다면에 따라
        // db를 지우거나 지우지 않거나

        // db에서 값을지운다
        itemImgRepository.deleteById(id);

    }
}
