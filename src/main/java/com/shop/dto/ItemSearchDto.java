package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType; // 검색 기간
    private ItemSellStatus searchSellStatus;
    private String searchBy;
    private String searchQuery = ""; // null값 방지를 위해 빈문자열로 초기화 시킴


}
