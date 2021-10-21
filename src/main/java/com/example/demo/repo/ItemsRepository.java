package com.example.demo.repo;

import com.example.demo.entity.Brands;
import com.example.demo.entity.Categories;
import com.example.demo.entity.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ItemsRepository extends JpaRepository<Items, Long> {
    List<Items> getAllByBrand(Brands brand);
    List<Items> getAllByCategory(Categories category);
    List<Items> getAllByInTopPageTrue();
    List<Items> findAllByBrandAndNameContainingAndPriceBetweenOrderByPriceDesc(Brands brand, String name, double priceFrom, double priceTo);

}
