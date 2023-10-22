package com.example.vblogserver.domain.bookmark.repository;

import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserIdAndType(Long userId, String type);

    List<Folder> findByNameAndUser(String folderName, User user);

    List<Folder> findByType(String type);
}
