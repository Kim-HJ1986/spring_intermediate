package com.spring.springcore.controller;

import com.spring.springcore.dto.FolderRequestDto;
import com.spring.springcore.exception.RestApiException;
import com.spring.springcore.model.Folder;
import com.spring.springcore.model.Product;
import com.spring.springcore.model.Users;
import com.spring.springcore.security.UserDetailsImpl;
import com.spring.springcore.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FolderController {
    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService){
        this.folderService = folderService;
    }


    @PostMapping("/api/folders")
    public List<Folder> addFolders(
            @RequestBody FolderRequestDto folderRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        List<String> folderNames = folderRequestDto.getFolderNames();
        Users user = userDetails.getUser();

        return folderService.addFolders(folderNames, user);
    }

    @GetMapping("/api/folders")
    public List<Folder> getFolders(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Users user = userDetails.getUser();
        return folderService.getFolders(user);
    }

    @GetMapping("/api/folders/{folderId}/products")
    public Page<Product> getProductsInFolder(
            @PathVariable Long folderId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        page -= 1;
        Page<Product> products = folderService.getProductsInFolder(
                folderId,
                page,
                size,
                sortBy,
                isAsc,
                userDetails.getUser()
        );
        return products;
    }

    //Global ExceptionHandler로 대체.
//    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
//    public ResponseEntity handleException(IllegalArgumentException e){
//        RestApiException restApiException = new RestApiException();
//        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
//        restApiException.setErrorMessage(e.getMessage());
//        return new ResponseEntity(
//                // HTTP body
//                restApiException,
//                // HTTP status code
//                HttpStatus.BAD_REQUEST
//        );
//    }
}
