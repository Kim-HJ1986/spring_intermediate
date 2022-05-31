package com.spring.springcore.repository;

import com.spring.springcore.model.Folder;
import com.spring.springcore.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUser(Users user);
    List<Folder> findByUserAndNameIs(Users user, String folderName);
}
