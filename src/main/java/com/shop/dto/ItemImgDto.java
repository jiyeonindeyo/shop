package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {

    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;

    // new- modelmapper 객체 생성 (싱글톤)
    private static ModelMapper modelMapper = new ModelMapper();

    private static ItemImgDto of(ItemImg itemImg) {
        //↓ 여러개가 호출될 경우 호출된만큼 객체가 생성되므로 -> 싱글톤 위배
//        ModelMapper mapper = new ModelMapper();
        // modelMapper를 사용해서 엔티티 -> DTO 변환된 객체 반환
        return modelMapper.map(itemImg, ItemImgDto.class);
    }

}
