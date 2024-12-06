package com.example.shoppro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {

    private Long rno;

    private Long bno;

    @NotBlank(message = "댓글은 공백일수 없습니다.")
    @Size(min = 2, max = 255, message = "댓글은 2~255 글자 사이여야합니다.")
    private String replyText;

    private ItemDTO itemDTO;    //  dto는 dto를
    private MemberDTO memberDTO;
    private String writerEmail;

    public ReplyDTO setMemberDTO(MemberDTO memberDTO) {
        this.memberDTO = memberDTO;
        return this;
    }

    public ReplyDTO setWriterEmail(String writerEmail){
        this.writerEmail = writerEmail;
        return this;
    }

    // 날짜
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
    public ReplyDTO setItemDTO(ItemDTO itemDTO){
        this.itemDTO = itemDTO;
        return this;
    }
}
