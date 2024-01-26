package com.shop.service;

import com.shop.dto.product.Category;
import com.shop.dto.product.ProductDTO;
import com.shop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    public List<Category> get_categories(){
        return productMapper.get_categories();
    }

    public List<Category> get_categories(int parentNo){
        return productMapper.get_category_by_no(parentNo);
    }

    public List<Category> get_categories(Category category){
        return productMapper.get_category_of_product(category);
    }

    // 상품 정보 가져오기
    public ProductDTO get_product(int no){
        return productMapper.get_product_by_no(no);
    }

    public List<ProductDTO> get_products(Integer categoryNo, String searchWord, String order){
        return productMapper.get_products(categoryNo, searchWord, order);
    }



}
