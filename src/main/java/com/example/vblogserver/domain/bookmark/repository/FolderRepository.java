package com.example.vblogserver.domain.bookmark.repository;

import com.example.vblogserver.domain.bookmark.entity.Folder;
import com.example.vblogserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByNameAndUser(String folderName, User user);

    Folder findByNameAndUserAndType(String folderName, User user, String type);

    Optional<Folder> findByTypeAndNameAndUser(String type, String name, User user);

    List<Folder> findByTypeAndUser(String type, User user);
}
