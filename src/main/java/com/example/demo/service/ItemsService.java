package com.example.demo.service;

import com.example.demo.entity.*;

import java.util.List;

public interface ItemsService {

    List<Items> getAllItems();
    List<Items> getAllByBrand(Brands brand);
    List<Items> getAllByCategory(Categories category);
    List<Items> getAllInTopPage();
    List<Items> getAllByBrandAndNameAndPriceBetween(Brands brand, String name, double priceFrom, double priceTo);
    Items getItem(Long id);
    Items addItem(Items item);
    Items updateItem(Items item);
    void deleteItem(Long id);

    List<Brands> getAllBrands();
    Brands getBrand(Long id);
    Brands getBrandByName(String name);
    Brands addBrand(Brands brand);
    Brands updateBrand(Brands brand);
    void deleteBrand(Long id);

    List<Countries> getAllCountries();
    Countries getCountry(Long id);
    Countries getCountryByName(String name);
    Countries addCountry(Countries country);
    Countries updateCountry(Countries country);
    void deleteCountry(Long id);

    List<Images> getAllImages();
    List<Images> getAllImagesByItemId(Long id);
    Images getImage(Long id);
    Images addImage(Images image);
    Images updateImage(Images image);
    void deleteAllImagesByItemId(Long id);
    void deleteImage(Long id);

    List<Categories> getCategories();
    Categories getCategory(Long id);
    Categories getCategoryByName(String name);
    Categories addCategory(Categories category);
    void deleteCategory(Long id);

    List<CarouselPicture> getAllCarouselPics();
    CarouselPicture getCarouselPicture(Long id);
    CarouselPicture addCarouselPicture(CarouselPicture picture);
    void deleteCarouselPicture(Long id);

    List<Comments> getAllCommentsByItemId(Long id);
    Comments getComment(Long id);
    Comments addComment(Comments comment);
    Comments updateComment(Comments comment);
    void deleteComment(Long id);

}
