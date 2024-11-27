package com.example.shoppro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderDTO {

    @NotNull(message = "정상적인 주문페이지가 아닙니다. 상품페이지로 다시 와주세요")
    private Long itemId;    // 기존에는 id로 보냈는데
                            // itemId로 받기로 했다.
    @Min(value = 1, message = "최소 주문 수량은 1개입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개입니다.")
    private int count;
}
