package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /*  SELECT * FROM item WHERE item_nm = ?  */
    List<Item> findByItemNm(String itemNm); //findItemByItemNm 해도 괜찮음

    /*  SELECT * FROM item WHERE item_nm = ? OR item_detail = ?  */
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    /*  SELECT * FROM item WHERE price < ?  */
    List<Item> findByPriceLessThan(Integer price);

    /*  SELECT * FROM item WHERE price < ? ORDER BY price DESC  */
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    //     (≒ *) <┐        ┌> entity class명 (DB 테이블명 X)
    @Query("SELECT i FROM Item i WHERE i.itemDetail like %:itemDetail% ORDER BY i.price DESC")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //                              ┌> DB 테이블명    ┌> DB 컬럼명
    @Query(value = "SELECT * FROM item i WHERE i.item_detail like %:itemDetail% ORDER BY i.price DESC",
            nativeQuery = true)
    List<Item> findByItemDetailByNative(String itemDetail);
}
