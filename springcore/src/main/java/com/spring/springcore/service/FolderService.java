package com.spring.springcore.service;

import com.spring.springcore.model.Folder;
import com.spring.springcore.model.Product;
import com.spring.springcore.model.Users;
import com.spring.springcore.repository.FolderRepository;
import com.spring.springcore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public FolderService(FolderRepository folderRepository, ProductRepository productRepository){
        this.folderRepository = folderRepository;
        this.productRepository = productRepository;
    }
    public List<Folder> addFolders(List<String> folderNames, Users user) {
        List<Folder> folderList = new ArrayList<>();

        for(String folderName : folderNames){
            if((folderRepository.findByUserAndNameIs(user, folderName)).size() >= 1){
                continue;
            }
            Folder folder = new Folder(folderName, user);
            folderList.add(folder);
        }
        return folderRepository.saveAll(folderList);
    }

    public List<Folder> getFolders(Users user) {
        return folderRepository.findAllByUser(user);
    }

    public Page<Product> getProductsInFolder(
            Long folderId, int page, int size, String sortBy, boolean isAsc, Users user
    ) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products =  productRepository.findAllByUserIdAndFolderList_Id(user.getId(), folderId, pageable);
        return products;
    }
}
