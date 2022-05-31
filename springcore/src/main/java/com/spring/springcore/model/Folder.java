package com.spring.springcore.model;

import com.spring.springcore.dto.ProductRequestDto;
import com.spring.springcore.validator.ProductValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Folder {
    // ID가 자동으로 생성 및 증가합니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    // 반드시 값을 가지도록 합니다.
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    // 관심 상품 생성 시 이용합니다.
    public Folder(String name, Users user) {
        this.name = name;
        this.user = user;
    }
}
