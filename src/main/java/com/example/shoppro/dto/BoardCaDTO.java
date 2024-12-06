package com.example.shoppro.dto;

import com.example.shoppro.entity.Category;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString //(exclude = "itemImgList")  //toString 변수 제외할 변수명
@NoArgsConstructor
public class BoardCaDTO {

    private Long id;

    private String title;

    private Long caid;

    private CategoryDTO categoryDTO;
}
