package com.example.demo.repo;

import com.example.demo.entity.Brands;
import com.example.demo.entity.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CountriesRepository extends JpaRepository<Countries, Long> {
    Countries getCountriesByName(String name);
}
