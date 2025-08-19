package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepogitory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    private final ItemImgRepogitory itemImgRepogitory;

    private final FileService fileService;

    @Value("${itemImgLocation}") //application.properties 사용할 때
    private String itemImgLocation;

    /* 클라이언트(브라우저)가 보낸 item 이미지 정보, 첨부파일
       -> 요청 body에서 데이터를 자바 객체로 바인딩하는 책임 => ItemController 호출
       서비스의 책임? -> 책임질 서비스?객체?를 호출해줌
       └> 1) MultipartFile에 들어있는 이미지 파일을 파일시스템에 저장 => FileService 호출
       └> 2) DB에 저장할 ItemImg 정보 -> DB에 저장 => ItemImgRepository 호출
                                          첨부파일 <┐ */
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {

        String oriImgName = itemImgFile.getOriginalFilename();

        String imgName = "";
        String imgUrl = "";
        // 파일 시스템에 파일 업로드 -> 서비스 책임1
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }
            itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        // DB에 저장된 파일 정보 저장 -> 서비스 책임2
        ItemImg saved = itemImgRepogitory.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepogitory.findById(itemImgId)
                                                    .orElseThrow(EntityNotFoundException::new);
            // 1. 기존 파일시스템에 저장되어있던 이미지 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }
            // 2. DB에 기존 레코드 내용 업데이트(새로 저장된 이미지의 경로, 저장된 이름, 원본 이미지 이름)
            // JPA Entity Update -> dirty checking -> 조회된 엔티티의 필드 변경
            String oriImgName = itemImgFile.getOriginalFilename();
            // 새 파일 저장하고 저장된 파일명 받기
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }

}
