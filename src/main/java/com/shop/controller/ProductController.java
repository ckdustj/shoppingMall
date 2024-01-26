package com.shop.controller;

import com.shop.dto.product.Category;
import com.shop.dto.product.ProductDTO;
import com.shop.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    // 해당 상품의 상세 페이지 View
    @GetMapping("/{no}")
    public String get_product(
            @PathVariable("no") int no,
            Model model){
        ProductDTO productDTO = productService.get_product(no);
        model.addAttribute("product", productDTO);
        return "main/product";
    }

    @GetMapping
    public String get_products_main(
            @RequestParam(value = "categoryNo", required = false) Integer categoryNo,
            @RequestParam(value = "search", required = false) String searchWord, //검색어
            @RequestParam(value = "order", defaultValue = "popular") String order, //인기순, 상품명순, 가격순...
            @RequestParam(value = "count", defaultValue = "8") int count, //인기순, 상품명순, 가격순...
            Model model
    ){
        log.info("categoryNo: " + categoryNo);
        log.info("searchWord: " + searchWord);
        log.info("order: " + order);
        log.info("count: " + count);
        // 존재하는 모든 카테고리(대분류)의 조회
        List<Category> categories = productService.get_categories();
        model.addAttribute("categories", categories);

        // 검색 조건에 맞는 상품들의 조회
        List<ProductDTO> productDTOS = productService.get_products(categoryNo, searchWord, order);
        model.addAttribute("products", productDTOS);
        return "main/search";
    }

    @ResponseBody
    @GetMapping("/category/{parentNo}")
    public List<Category> get_categories(@PathVariable("parentNo") int parentNo){
        return productService.get_categories(parentNo);
    }

}
