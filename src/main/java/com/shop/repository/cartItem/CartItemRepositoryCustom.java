package com.shop.repository.cartItem;

import com.shop.dto.CartDetailDto;

import java.util.List;

public interface CartItemRepositoryCustom {

    List<CartDetailDto> findCartDetailDtoList(Long cartId);

    /* SELECT ci.cart_item_id, i.item_nm, i.price, ci.count, im.imgUrl
       FROM cart_item ci
       INNER JOIN item i
            ON ci.item_id = i.item_id
       INNER JOIN item_img im
            ON i.item_id = im.item_id
       WHERE ci.cart_id = ?
         AND im.repimg = 'Y'
       ORDER BY ci.regTime DESC;
    */
    List<CartDetailDto> findCartDetailDtoListByItemId(Long itemId);

}
