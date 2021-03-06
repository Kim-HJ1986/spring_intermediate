package com.spring.springcore.controller;

import com.spring.springcore.dto.ProductMypriceRequestDto;
import com.spring.springcore.dto.ProductRequestDto;
import com.spring.springcore.model.ApiUseTime;
import com.spring.springcore.model.Product;
import com.spring.springcore.model.UserRoleEnum;
import com.spring.springcore.model.Users;
import com.spring.springcore.repository.ApiUseTimeRepository;
import com.spring.springcore.security.UserDetailsImpl;
import com.spring.springcore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RequiredArgsConstructor // final로 선언된 멤버 변수를 자동으로 생성합니다.
@RestController // JSON으로 데이터를 주고받음을 선언합니다.
public class ProductController {
    private final ProductService productService;
    private final ApiUseTimeRepository apiUseTimeRepository;

    //아래 @Autowired가 붙은 생성자의 역할은
    //클래스 위 @RequiredArgsConstructor에 의해 완벽히 대체된다. 따라서 같이 쓰면 오류남
    // productService라는 빈을 넣어줌 (DI: 의존성 주입)
    @Autowired
    public ProductController(
            ProductService productService,
            ApiUseTimeRepository apiUseTimeRepository){
        this.productService = productService;
        this.apiUseTimeRepository = apiUseTimeRepository;
    }

    // 신규 상품 등록
    @PostMapping("/api/products")
    public Product createProduct(@RequestBody ProductRequestDto requestDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 로그인 되어 있는 회원의 Id
        Long userId = userDetails.getUser().getId();
        Product product = productService.createProduct(requestDto, userId);

        // 응답 보내기
        return product;
    }

    // 설정 가격 변경
    @PutMapping("/api/products/{id}")
    public Long updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto){
        Product product = productService.updateProduct(id, requestDto);

        // 응답 보내기 (업데이트된 상품 id)
        return product.getId();
    }

    // 로그인한 회원이 등록한 관심 상품 목록 조회
    @GetMapping("/api/products")
    public Page<Product> getProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        page -= 1;
        return productService.getProducts(userId, page, size, sortBy, isAsc);

    }

    // 관리자용 전체 상품 조회
    @Secured(UserRoleEnum.Authority.ADMIN) // "ROLE_ADMIN"
    @GetMapping("/api/admin/products")
    public Page<Product> getAllProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println(userDetails.getUser().getRole()); // ROLE NAME 리턴
        System.out.println(userDetails.getAuthorities());// ROLE 전체 리스트 리턴
        page -= 1;
        return productService.getAllProducts(page, size, sortBy, isAsc);
    }

    @PostMapping("/api/products/{productId}/folder")
    public Long addFolder(
            @PathVariable Long productId,
            @RequestParam Long folderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Users user = userDetails.getUser();
        Product product = productService.addFolder(productId, folderId, user);
        return product.getId();
    }
}