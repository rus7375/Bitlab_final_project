package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.ItemsService;
import com.example.demo.service.UsersService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
@RequestMapping("/moderator-panel")
public class ModeratorController {

    @Autowired
    ItemsService itemsService;

    @Autowired
    UsersService usersService;


    @Value("${file.card.picture.uploadPath}")
    private String uploadPath;



    @GetMapping("/items")
    public String getItems(Model model) {
        List<Items> items = itemsService.getAllItems();

        model.addAttribute("items", items);
        model.addAttribute("CURRENT_USER", getUser());

        return "items";
    }

    @GetMapping("/add-item")
    public String addItemPage(Model model) {
        List<Brands> brands = itemsService.getAllBrands();
        List<Categories> categories = itemsService.getCategories();

        model.addAttribute("CURRENT_USER", getUser());
        model.addAttribute("brands", brands);
        model.addAttribute("categories", categories);

        return "add_item";
    }

    @PostMapping("/add-item")
    public String addItem(@RequestParam(name = "name") String name,
                          @RequestParam(name = "description") String description,
                          @RequestParam(name = "price") Double price,
                          @RequestParam(name = "category") Long categoryId,
                          @RequestParam(name = "brand") Long brandId) {

        Categories category = itemsService.getCategory(categoryId);
        Brands brand = itemsService.getBrand(brandId);

        if(category != null && brand != null) {
            Items item = new Items();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategory(category);
            item.setStars(0);
            item.setBrand(brand);
            item.setInTopPage(false);

            itemsService.addItem(item);

            return "redirect:/moderator-panel/add-item?success";
        }
        return "redirect:/moderator-panel/add-item?error";
    }

    @GetMapping("/item-details/{id}")
    public String itemDetails(Model model,
                              @PathVariable(name = "id") Long itemId) {
        Items item = itemsService.getItem(itemId);
        List<Images> images =  itemsService.getAllImagesByItemId(itemId);
        List<Brands> brands = itemsService.getAllBrands();
        List<Categories> categories = itemsService.getCategories();
        if(item != null) {
            model.addAttribute("item", item);
            model.addAttribute("images", images);
            model.addAttribute("brands", brands);
            model.addAttribute("categories", categories);
            model.addAttribute("CURRENT_USER", getUser());

            return "item_details";
        }
        return "redirect:/moderator-panel/items?error";
    }

    @PostMapping("/upload-cardpicture/{id}")
    public String uploadCardPicture(@PathVariable(name = "id") Long itemId,
                                    @RequestParam(name = "card_picture") MultipartFile file) {
        Items item = itemsService.getItem(itemId);
        if(item != null && file != null) {
            if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
                try {
                    String cardPicName = DigestUtils.sha1Hex("card_" + item.getId() + "_!Picture");
                    byte [] bytes = file.getBytes();

                    Path path = Paths.get(uploadPath + cardPicName + ".jpg");
                    Files.write(path, bytes);

                    item.setSmallPicURL(cardPicName);
                    itemsService.updateItem(item);

                    return "redirect:/moderator-panel/item-details/" + item.getId() + "?success";

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
        return "redirect:/moderator-panel/item-details/" + item.getId() +"?error";
    }


    @PostMapping("/upload-image/{id}")
    public String uploadItemImages(@PathVariable(name = "id") Long itemId,
                                    @RequestParam(name = "item_image") MultipartFile file) {
        Items item = itemsService.getItem(itemId);
        if(item != null && file != null) {
            if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
                try {
                    List<Images> images = itemsService.getAllImagesByItemId(itemId);
                    int size = images.size() +1;

                    String imageName = DigestUtils.sha1Hex("item_" + item.getId()+"_image_"+ size + "_!Image");
                    byte [] bytes = file.getBytes();

                    Path path = Paths.get(uploadPath + imageName + ".jpg");
                    Files.write(path, bytes);

                    Images image = new Images();
                    image.setUrl(imageName);
                    image.setItem(item);
                    itemsService.addImage(image);

                    return "redirect:/moderator-panel/item-details/" + item.getId() + "?success";

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
        return "redirect:/moderator-panel/item-details/" + item.getId() +"?error";
    }

    @PostMapping("/update-item/{id}")
    public String updateItemPage(@PathVariable(name = "id") Long itemId,
                                 @RequestParam(name = "name") String name,
                                 @RequestParam(name = "description") String description,
                                 @RequestParam(name = "price") Double price,
                                 @RequestParam(name = "category") Long categoryId,
                                 @RequestParam(name = "brand") Long brandId) {
        Items item = itemsService.getItem(itemId);
        Categories category = itemsService.getCategory(categoryId);
        Brands brand = itemsService.getBrand(brandId);
       if(item != null) {
           item.setName(name);
           item.setDescription(description);
           item.setPrice(price);
           item.setCategory(category);
           item.setBrand(brand);

           itemsService.updateItem(item);
           return "redirect:/moderator-panel/item-details/" + item.getId() + "?success";
       }
        return "redirect:/moderator-panel/item-details/" + item.getId() + "?error";
    }

    @PostMapping("/top-page-item")
    public String setTopPage(@RequestParam(name = "item_id") Long itemId,
                             @RequestParam(name = "top_page") boolean inTopPage) {

        Items item = itemsService.getItem(itemId);
        if(item != null) {
            item.setInTopPage(!inTopPage);
            itemsService.updateItem(item);
            return "redirect:/moderator-panel/items?top-page";
        }
        return "redirect:/moderator-panel/items?error";

    }

    @PostMapping("/delete-item")
    public String deleteItem(@RequestParam(name = "item_id") Long itemId) {
        Items item = itemsService.getItem(itemId);
        List<Images> images = itemsService.getAllImagesByItemId(itemId);
        String redirect = "deleteerror";
        if(item != null) {
            for(Images image : images) {
                try {

                    Files.deleteIfExists(Paths.get(uploadPath + image.getUrl() + ".jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            itemsService.deleteAllImagesByItemId(itemId);
            itemsService.deleteItem(itemId);

            redirect = "deletesuccess";
        }
        return "redirect:/moderator-panel/items?" + redirect;
    }

    @GetMapping("/categories")
    public String categoriesPage(Model model) {
        List<Categories> categories = itemsService.getCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("CURRENT_USER", getUser());

        return "categories";
    }
    @GetMapping("/brands")
    public String brandsPage(Model model) {
        List<Brands> brands = itemsService.getAllBrands();
        List<Countries> countries = itemsService.getAllCountries();

        model.addAttribute("brands", brands);
        model.addAttribute("countries", countries);
        model.addAttribute("CURRENT_USER", getUser());

        return "brands";
    }

    @GetMapping("/countries")
    public String countriesPage(Model model) {
        List<Countries> countries = itemsService.getAllCountries();

        model.addAttribute("countries", countries);
        model.addAttribute("CURRENT_USER", getUser());

        return "countries";
    }

    @PostMapping("add-brand")
    public String  addBrand(@RequestParam(name = "brand_name") String name,
                            @RequestParam(name = "country_id") Long countryId) {
        Brands brand = itemsService.getBrandByName(name);
        Countries country = itemsService.getCountry(countryId);
        if(brand == null && country != null) {
            brand = new Brands();
            brand.setName(name);
            brand.setCountry(country);

            itemsService.addBrand(brand);

            return "redirect:/moderator-panel/brands?success";
        }

        return "redirect:/moderator-panel/brands?error";
    }

    @PostMapping("add-category")
    public String  addCategory(@RequestParam(name = "category_name") String name){
        Categories category = itemsService.getCategoryByName(name);
        if(category == null) {
            category = new Categories();
            category.setCategory(name);

            itemsService.addCategory(category);

            return "redirect:/moderator-panel/categories?success";
        }

        return "redirect:/moderator-panel/categories?error";
    }

    @PostMapping("add-country")
    public String  addCountry(@RequestParam(name = "country_name") String name,
                              @RequestParam(name = "country_code") String code){
        Countries country = itemsService.getCountryByName(name);
        if(country == null) {
            country = new Countries();
            country.setName(name);
            country.setCode(code);

            itemsService.addCountry(country);

            return "redirect:/moderator-panel/countries?success";
        }

        return "redirect:/moderator-panel/countries?error";
    }

    @GetMapping(value = "/carousel")
    public String carouselPage(Model model) {
        List<CarouselPicture> carouselPictures = itemsService.getAllCarouselPics();

        model.addAttribute("carouselPictures", carouselPictures);
        model.addAttribute("CURRENT_USER", getUser());

        return "carousel_pictures";
    }

    @PostMapping("/upload-carousel-pic")
    public String uploadCarouselPicture(@RequestParam(name = "carousel_picture") MultipartFile file) {
            if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
                try {
                    List<CarouselPicture> carouselPictures = itemsService.getAllCarouselPics();
                    int size = carouselPictures.size() + 1;
                    String carouselPicName = DigestUtils.sha1Hex("carousel" + size + "_!Picture");
                    byte [] bytes = file.getBytes();

                    Path path = Paths.get(uploadPath + carouselPicName + ".jpg");
                    Files.write(path, bytes);

                    CarouselPicture picture = new CarouselPicture();
                    picture.setUrl(carouselPicName);

                    itemsService.addCarouselPicture(picture);

                    return "redirect:/moderator-panel/carousel" + "?success";

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        return "redirect:/moderator-panel/carousel/" + "?error";
    }





    public Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            return (Users) authentication.getPrincipal();
        }

        return null;
    }
}
