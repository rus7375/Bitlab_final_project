package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemsService{

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private BrandsRepository brandsRepository;

    @Autowired
    private CountriesRepository countriesRepository;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    CategoriesRepository categoriesRepository;

    @Autowired
    CarouselPictureRepository carouselPictureRepository;

    @Autowired
    CommentsRepository commentsRepository;

    @Override
    public List<Items> getAllItems() {
        return itemsRepository.findAll();
    }

    @Override
    public List<Items> getAllByBrand(Brands brand) {
        return itemsRepository.getAllByBrand(brand);
    }

    @Override
    public List<Items> getAllByCategory(Categories category) {
        return itemsRepository.getAllByCategory(category);
    }

    @Override
    public List<Items> getAllInTopPage() {
        return itemsRepository.getAllByInTopPageTrue();
    }

    @Override
    public List<Items> getAllByBrandAndNameAndPriceBetween(Brands brand, String name, double priceFrom, double priceTo) {
        return itemsRepository.findAllByBrandAndNameContainingAndPriceBetweenOrderByPriceDesc(brand, name, priceFrom, priceTo);
    }

    @Override
    public Items getItem(Long id) {
        return itemsRepository.findById(id).orElse(null);
    }

    @Override
    public Items addItem(Items item) {
        return itemsRepository.save(item);
    }

    @Override
    public Items updateItem(Items item) {
        return itemsRepository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        itemsRepository.deleteById(id);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandsRepository.findAll();
    }

    @Override
    public Brands getBrand(Long id) {
        return brandsRepository.findById(id).orElse(null);
    }

    @Override
    public Brands getBrandByName(String name) {
        return brandsRepository.findByName(name);
    }

    @Override
    public Brands addBrand(Brands brand) {
        return brandsRepository.save(brand);
    }

    @Override
    public Brands updateBrand(Brands brand) {
        return brandsRepository.save(brand);
    }

    @Override
    public void deleteBrand(Long id) {
        brandsRepository.deleteById(id);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countriesRepository.findAll();
    }

    @Override
    public Countries getCountry(Long id) {
        return countriesRepository.findById(id).orElse(null);
    }

    @Override
    public Countries getCountryByName(String name) {
        return countriesRepository.getCountriesByName(name);
    }

    @Override
    public Countries addCountry(Countries country) {
        return countriesRepository.save(country);
    }

    @Override
    public Countries updateCountry(Countries country) {
        return countriesRepository.save(country);
    }

    @Override
    public void deleteCountry(Long id) {
        countriesRepository.deleteById(id);
    }

    @Override
    public List<Images> getAllImages() {
        return imagesRepository.findAll();
    }

    @Override
    public List<Images> getAllImagesByItemId(Long id) {
        return imagesRepository.findImagesByItemId(id);
    }

    @Override
    public Images getImage(Long id) {
        return imagesRepository.findById(id).orElse(null);
    }

    @Override
    public Images addImage(Images image) {
        return imagesRepository.save(image);
    }

    @Override
    public Images updateImage(Images image) {
        return imagesRepository.save(image);
    }

    @Override
    public void deleteAllImagesByItemId(Long id) {
        imagesRepository.deleteAllByItemId(id);
    }

    @Override
    public void deleteImage(Long id) {
        imagesRepository.deleteById(id);
    }

    @Override
    public List<Categories> getCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories getCategory(Long id) {
        return categoriesRepository.findById(id).orElse(null);
    }

    @Override
    public Categories getCategoryByName(String name) {
        return categoriesRepository.findCategoriesByCategory(name);
    }

    @Override
    public Categories addCategory(Categories category) {
        return categoriesRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoriesRepository.deleteById(id);
    }

    @Override
    public List<CarouselPicture> getAllCarouselPics() {
        return carouselPictureRepository.findAll();
    }

    @Override
    public CarouselPicture getCarouselPicture(Long id) {
        return carouselPictureRepository.findById(id).orElse(null);
    }

    @Override
    public CarouselPicture addCarouselPicture(CarouselPicture picture) {
        return carouselPictureRepository.save(picture);
    }

    @Override
    public void deleteCarouselPicture(Long id) {
        carouselPictureRepository.deleteById(id);
    }

    @Override
    public List<Comments> getAllCommentsByItemId(Long id) {
        return commentsRepository.findAllByItemId(id);
    }

    @Override
    public Comments getComment(Long id) {
        return commentsRepository.findById(id).orElse(null);
    }

    @Override
    public Comments addComment(Comments comment) {
        return commentsRepository.save(comment);
    }

    @Override
    public Comments updateComment(Comments comment) {
        return commentsRepository.save(comment);
    }

    @Override public void deleteComment(Long id) {
        commentsRepository.deleteById(id);
    }
}
