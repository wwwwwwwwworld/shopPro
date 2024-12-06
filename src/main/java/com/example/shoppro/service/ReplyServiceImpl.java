package com.example.shoppro.service;


import com.example.shoppro.dto.*;
import com.example.shoppro.entity.Item;
import com.example.shoppro.entity.Member;
import com.example.shoppro.entity.Reply;
import com.example.shoppro.repository.ItemRepository;
import com.example.shoppro.repository.MemberRepository;
import com.example.shoppro.repository.ReplyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;    //null이였으나
    private final MemberRepository memberRepository;


    @Override
    public void register(ReplyDTO replyDTO, String email) {
        //등록    : 댓글은 참조관계로 부모가 Board이기에 Board에서 가지고 있는
        // entity를 set하고 나서 저장해야한다.
        // 일반적으로 Board board = new board(); 라고 만든
        // board.setBno(replyDTO.getBno) 로 만든 객체는 임의의객체임으로
        // 저장이 되지 않는다.

        Member member =
        memberRepository.findByEmail(email);


        Optional<Item> optionalBoard =
        itemRepository.findById(replyDTO.getBno());    //entity 를 찾아온다.

        Item item = optionalBoard.orElseThrow(EntityNotFoundException::new);
        //없을때는 예외처리
        
        Reply reply =
            modelMapper.map(replyDTO, Reply.class);
        reply.setItem(item);
        reply.setMember(member);

        replyRepository.save(reply);

    }

    @Override
    public ReplyDTO read(Long rno) {
        Reply reply =
        replyRepository.findById(rno).orElseThrow(EntityNotFoundException::new);

        ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class).setMemberDTO(
                modelMapper.map(reply.getMember(), MemberDTO.class)
        );
//        ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class).setWriterEmail(
//               reply.getMember().getEmail()
//        );

        return replyDTO;
    }

    @Override
    public List<ReplyDTO> list(Long bno) {

        List<Reply> replyList =
        replyRepository.findByItemId(bno);

        List<ReplyDTO> list =
        replyList.stream().map( reply ->  modelMapper.map(reply, ReplyDTO.class).setItemDTO( modelMapper.map(reply.getItem(), ItemDTO.class) ).setMemberDTO(modelMapper.map(reply.getMember(), MemberDTO.class))  )
                .collect(Collectors.toList());

        //list에서 하나순차적으로 0부터 꺼내서 꺼낸아이를 변수 reply라 칭하고
        // 이걸가지고 ReplyDTO 타입으로 변환한다음 리턴값으로 ReplyDTO 타입으로
        //받는데 받은 ReplyDTO에 setBoardDTO를 하기위해 아까 받은 reply에서
        // reply.getBoard를 받아서 다시 BoardDTO 타입으로 변화해서 set하기위해서
        // setBoardDTO의 리턴타입을 ReplyDTO로 줘서 리턴을 다시 ReplyDTO로 만든다.
        // 리턴값이 있어야지만 .collect(Collectors.toList());를 사용할 수 있어서

        return list;
    }

    @Override
    public Page<ReplyDTO> listPage(Long bno, Pageable pageable) {
        //pageable은 컨트롤러로부터 현재 페이지를 받아 -1을 줘서
        // 내부적으로 limit을 사용할 수 있도록 값을 주며 ,
        // 현재 페이지당 보여질 객체수를 결정해 줍니다.
        // 위 2가지만 설정가능하며(오버로딩), 추가로 정렬조건 또한 줄수 있습니다.
        // 현재는 만들어진 pageable을 받았으나 컨트롤러에서 만들어서 넣어주던
        // 아니면 내부적으로 페이지만 받아서 처리하던
        // select * from table order by 정렬조건 limit 현재페이지에서 시작할 번호, 몇개씩
        // select * from table (pageable) 파라미터로 넣어주면 페이지객체로 반환합니다.
        // 15페이지라하면 10개씩이라하면  10페이지부터 10부터 ~ 20까지 보여줄 예정
        // 내부에서 직접 만들고 page와 size를 받았다면
//        Pageable ex =
//                PageRequest
//                        .of((int) (bno-1),10, Sort.by("rno").descending() );
        Page<Reply> replyPage =
        replyRepository.findByItemId(bno, pageable);
        //pageImpl을 이용해서 재지정
        List<Reply> replyList =
                replyPage.getContent();

        List<ReplyDTO> list =
                replyList.stream().map( reply ->  modelMapper.map(reply, ReplyDTO.class).setItemDTO( modelMapper.map(reply.getItem(), ItemDTO.class) ).setMemberDTO(modelMapper.map(reply.getMember(), MemberDTO.class))  )
                        .collect(Collectors.toList());

        Page<ReplyDTO> replyDTOPage =

        new PageImpl<>(list , pageable, replyPage.getTotalElements());

        return replyDTOPage;
    }

    @Override
    public int totalEl() {
        Pageable pageable
                = PageRequest.of(0, 10);

        Page<Reply> replyPage =
                replyRepository.findAll(pageable);
        return (int) replyPage.getTotalElements();
    }

    @Override
    public PageResponseDTO<ReplyDTO> pageList(PageRequestDTO pageRequestDTO, Long ino) {

        Pageable pageable
                = pageRequestDTO.getPageable("rno");

        Page<Reply> replyPage =
        replyRepository.findByItemId(ino , pageable);

        List<Reply> replyList = replyPage.getContent();
        replyList.forEach(reply -> log.info(reply.getMember()));
        replyList.forEach(reply -> log.info(reply.getMember()));
        replyList.forEach(reply -> log.info(reply.getMember()));
        replyList.forEach(reply -> log.info(reply.getMember()));

        List<ReplyDTO> replyDTOList =
        replyList.stream()
                .map(
                        reply -> modelMapper.map( reply, ReplyDTO.class)
                        .setItemDTO( modelMapper.map(reply.getItem(), ItemDTO.class) )
                                .setMemberDTO(   modelMapper.map( reply.getMember() , MemberDTO.class  ))
                )
                .collect(Collectors.toList());


        replyDTOList.forEach(reply -> log.info(reply.getMemberDTO()));

        PageResponseDTO<ReplyDTO> responseDTO =
                PageResponseDTO.<ReplyDTO>withAll()
                        .dtoList(replyDTOList)
                        .total((int) replyPage.getTotalElements())
                        .pageRequestDTO(pageRequestDTO)
                        .build();

        return responseDTO;
    }

    @Override
    public ReplyDTO update(ReplyDTO replyDTO) {
        //들어온 replyDTO값을 찍어주고
        log.info("댓글서비스 replyDTO" + replyDTO);
        //수정할 데이터를 찾는다.
        Reply reply =
        replyRepository.findById(replyDTO.getRno())
                .orElseThrow(EntityNotFoundException::new);
        //수정할 데이터를 수정한다. //댓글 내용만 수정할 것이다.
        reply.setReplyText(replyDTO.getReplyText());

        //업데이트 수행
        // 수정한뒤 에 reply을 replyDTO로 변환해서 반환을 해야하지만....
        return modelMapper.map(reply , ReplyDTO.class);
    }

    @Override
    public void remove(Long rno) {
        log.info(rno);
//        Reply reply =
//        replyRepository.findById(rno).orElseThrow(EntityNotFoundException::new);
//        replyRepository.delete(reply);
//
        replyRepository.deleteById(rno);

    }





}
