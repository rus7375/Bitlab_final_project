package com.example.demo.repo;

import com.example.demo.entity.Brands;
import com.example.demo.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ImagesRepository extends JpaRepository<Images, Long> {
    List<Images> findImagesByItemId(Long itemId);
    void deleteAllByItemId(Long id);
}
