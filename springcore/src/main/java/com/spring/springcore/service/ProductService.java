package com.spring.springcore.service;

import com.spring.springcore.model.Product;
import com.spring.springcore.dto.ProductMypriceRequestDto;
import com.spring.springcore.repository.ProductRepository;
import com.spring.springcore.dto.ProductRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//@Component
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired // productRepository라는 빈을 넣어줌 (DI: 의존성 주입)
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    public Product createProduct(ProductRequestDto requestDto){
        // 요청받은 DTO 로 DB에 저장할 객체 만들기
        Product product = new Product(requestDto);

        productRepository.save(product);

        return product;
    }

    public Product updateProduct(Long id, ProductMypriceRequestDto requestDto){
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 아이디가 존재하지 않습니다.")
        );

        int myprice = requestDto.getMyprice();
        product.setMyprice(myprice);
        // myprice만 변경했기 때문에 myprice만 변경된 채 저장됨
        productRepository.save(product);

        return product;
    }

    public List<Product> getProducts(){
        List<Product> products = productRepository.findAll();

        return products;
    }
}
