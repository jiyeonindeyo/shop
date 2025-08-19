package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImgRepogitory extends JpaRepository<ItemImg, Long> {

    // FK인 item_id로 ItemImg를 조회
    // SELECT * FROM item_img WHERE item_id = ? ORDER BY item_img_id ASC
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId); //← PK(item_id)로 오름차순 정렬

    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);

}
