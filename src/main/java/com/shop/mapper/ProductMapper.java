package com.shop.mapper;

import com.shop.dto.product.Category;
import com.shop.dto.product.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    // 카테고리 대분류 값 가져오기
    List<Category> get_categories();
    // 카테고리 대분류 이외 값 전부 가져오기
    List<Category> get_category_by_no(int parentNo);
    // 특정 상품의 카테고리 분류 가져오기
    List<Category> get_category_of_product(Category category);

    // no에 해당하는 상품 하나의 정보 모두 가져오기
    ProductDTO get_product_by_no(int no);

    List<ProductDTO> get_products(
            @Param("categoryNo") Integer categoryNo,
            @Param("searchWord") String searchWord,
            @Param("order") String order
    );
}
