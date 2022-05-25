package com.spring.springcore.controller;

import com.spring.springcore.model.Product;
import com.spring.springcore.dto.ProductMypriceRequestDto;
import com.spring.springcore.dto.ProductRequestDto;
import com.spring.springcore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

//@RequiredArgsConstructor // final로 선언된 멤버 변수를 자동으로 생성합니다.
@RestController // JSON으로 데이터를 주고받음을 선언합니다.
public class ProductController {
    private final ProductService productService;

    //아래 @Autowired가 붙은 생성자의 역할은
    //클래스 위 @RequiredArgsConstructor에 의해 완벽히 대체된다. 따라서 같이 쓰면 오류남
    // productService라는 빈을 넣어줌 (DI: 의존성 주입)
    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    // 신규 상품 등록
    @PostMapping("/api/products")
    public Product createProduct(@RequestBody ProductRequestDto requestDto) throws SQLException {
        Product product = productService.createProduct(requestDto);

        // 응답 보내기
        return product;
    }

    // 설정 가격 변경
    @PutMapping("/api/products/{id}")
    public Long updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto) throws SQLException {
        Product product = productService.updateProduct(id, requestDto);

        // 응답 보내기 (업데이트된 상품 id)
        return product.getId();
    }

    // 등록된 전체 상품 목록 조회
    @GetMapping("/api/products")
    public List<Product> getProducts() throws SQLException {
        List<Product> products = productService.getProducts();

        // 응답 보내기
        return products;
    }
}