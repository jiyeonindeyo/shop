package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static com.shop.entity.QItem.item;

@Slf4j
@SpringBootTest //bean 주입 그대로 다 받음, mock->가짜객체
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemRepositoryTest {

    @PersistenceContext //영속성
    EntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        //DB에 값 저장(INSERT)
        //1. entity 객체를 만든다.
        Item item = new Item();
        //2. entity 객체에 저장하고 싶은 값을 담는다.
        item.setItemNm("테스트상품");
        item.setPrice(10_000);
        item.setStockNumber(100);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        //3. JPA Repository를 이용해서 저장한다.( SAVE(= persist + flush) )
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem);
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        createDummyItems();
        List<Item> items = itemRepository.findByItemNm("테스트 상품3");
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("상품명 or 상세설명 테스트")
    public void findByItemNmOrItemDetail(){
        createDummyItems();
        List<Item> items = itemRepository.findByItemNmOrItemDetail(
                                "테스트 상품6",
                                "테스트 상품 상세 설명3");
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThan(){
        createDummyItems();
        List<Item> items = itemRepository.findByPriceLessThan(10_005);
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        createDummyItems();
        List<Item> items = itemRepository.findByPriceLessThanOrderByPriceDesc(10_006);
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetail(){
        createDummyItems();
        List<Item> items = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트 By Native")
    public void findByItemDetailByNative(){
        createDummyItems();
        List<Item> items = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("QueryDSL 조회 테스트1")
    public void queryDSLTest(){
        createDummyItems();
        // SELECT * FROM item WHERE item_sell_status = sell AND item_detail LIKE %테스트 상품 상세 설명% ORDER BY price DESC
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<Item> query = queryFactory.selectFrom(item) // SELECT * FROM item
                                            .where(item.itemSellStatus.eq(ItemSellStatus.SELL)) // WHERE item_sell_status = sell
                                            .where(item.itemDetail.like("%"+"테스트 상품 상세 설명"+"%")) // AND item_detail LIKE %테스트 상품 상세 설명%
                                            .orderBy(item.price.desc()); // ORDER BY price DESC

        List<Item> itemList = query.fetch();
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("상품 QueryDSL 조회 테스트2")
    public void queryDSLTest2(){
        createDummyItems2();
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10_003;
        String itemSellStatus = "SELL";
        int pageNum = 1;
        // 조건1:주어진 itemDetail 키워드를 포함,            조건2:상품가격이 주어진 price보다 큰 경우,
        // 조건3:조회하려는 상태가 SELL인 경우(판매상태=SELL), 조건4:한페이지당 5개씩 페이징된 데이터 조회
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<Item> baseQuery = queryFactory.selectFrom(item);

        //재사용성 up -> 조건을 따로 만든 뒤 쿼리에 추가
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%")); //조건1
        booleanBuilder.and(item.price.gt(price)); //조건2, goe(크거나같다) loe(작거나같다)
        if (itemSellStatus.equals(itemSellStatus)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL)); //조건3
        }
        // ↓ SELECT * FROM item WHERE itemDetail LIKE ? AND price > ? AND item_sell_status = 'SELL'
        JPAQuery<Item> conditionedQuery = baseQuery.where(booleanBuilder);

        Pageable pageable = PageRequest.of(pageNum-1, 5);
        JPAQuery<Item> pagedQuery = conditionedQuery.orderBy(item.id.desc()) //Paging Query는 기본적으로 내림차순 정렬을 해주어야함
                                                    .offset(pageable.getOffset()) //조건4
                                                    .limit(pageable.getPageSize()); // LIMIT 5 OFFSET ?
        // /* SELECT * FROM item WHERE itemDetail LIKE ? AND price > ? AND item_sell_status = 'SELL' ORDER BY id DESC LIMIT 5 OFFSET ? */
        //                       ↓
        List<Item> contents = pagedQuery.fetch(); //┌>SQL 내장함수 사용할때
        Long totalCount = queryFactory.select(Wildcard.count).from(item).where(booleanBuilder).fetchOne();
//        Long totalCount = queryFactory.select(Wildcard.count).from(item).fetchOne();
        //          ┌> SELECT * FROM item
        Page<Item> result = new PageImpl<>(contents, pageable, totalCount);

        //console 출력
        System.out.println("총 컨텐츠 요소의 수 : " + result.getTotalElements());
        System.out.println("조회가능한 총 페이지 수 : " + result.getTotalPages());
        List<Item> items = result.getContent();
        for (Item item : items) {
            System.out.println(item);
        }
    }

    public void createDummyItems() {
        for (int i=1; i<=10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setStockNumber(100 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    public void createDummyItems2() {
        for (int i=1; i<=5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setStockNumber(100 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for (int i=6; i<=10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setStockNumber(0);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }
}
