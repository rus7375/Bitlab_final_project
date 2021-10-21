package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.ItemsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    @Value("${file.card.picture.viewPath}")
    private String viewPath;

    @Value("${file.card.picture.defaultPicture}")
    private String defaultCardPicture;

    @Autowired
    private ItemsService itemsService;

    @GetMapping("/")
    public String index(Model model){
        List<CarouselPicture> carouselPictures = itemsService.getAllCarouselPics();
        List<Items> topPageItems = itemsService.getAllInTopPage();


        model.addAttribute("CURRENT_USER", getUser());
        model.addAttribute("items", topPageItems);
        model.addAttribute("brands", getBrands());
        model.addAttribute("categories", getCategories());
        model.addAttribute("carouselPictures", carouselPictures);

        return "index";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @GetMapping("/moderator-panel")
    public String modPanel(Model model) {
        model.addAttribute("CURRENT_USER", getUser());
        return "moderator_panel";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/admin-panel")
    public String adminPanel(Model model) {
        model.addAttribute("CURRENT_USER", getUser());
        return "admin_panel";
    }

    @GetMapping("/search")
    public String searchSortedPage(Model model,
                                   @RequestParam(name = "name") String itemName,
                                   @RequestParam(name = "brand") String brandName,
                                   @RequestParam(name = "price_from") int priceFrom,
                                   @RequestParam(name = "price_to") int priceTo,
                                   @RequestParam(name = "search_type") String searchType) {
        Brands brand = itemsService.getBrandByName(brandName);
        if (brand != null) {
            List<Items> sortedItemsList = itemsService.getAllByBrandAndNameAndPriceBetween(brand, itemName, priceFrom, priceTo);

            if(searchType.equals("desc")) Collections.reverse(sortedItemsList);
            model.addAttribute("items", sortedItemsList);
            model.addAttribute("CURRENT_USER", getUser());
            model.addAttribute("brands", getBrands());
            model.addAttribute("categories", getCategories());
            System.out.println(sortedItemsList);
            return "sortedItems";
        }
        return "redirect:/?error";
    }

    @GetMapping("/categories/{name}")
    public String sortByCategoryPage(Model model,
                                   @PathVariable(name = "name") String name) {
        Categories category = itemsService.getCategoryByName(name);
        if (category != null) {
            List<Items> sortedCategoriesList = itemsService.getAllByCategory(category);

            model.addAttribute("items", sortedCategoriesList);
            model.addAttribute("CURRENT_USER", getUser());
            model.addAttribute("brands", getBrands());
            model.addAttribute("categories", getCategories());
            return "sortedItems";
        }
        return "redirect:/?error";

    }

    @GetMapping("/brands/{name}")
    public String sortByBrandsPage(Model model,
                                     @PathVariable(name = "name") String name) {
        Brands brand = itemsService.getBrandByName(name);
        if (brand != null) {
            List<Items> sortedCategoriesList = itemsService.getAllByBrand(brand);

            model.addAttribute("items", sortedCategoriesList);
            model.addAttribute("CURRENT_USER", getUser());
            model.addAttribute("brands", getBrands());
            model.addAttribute("categories", getCategories());
            return "sortedItems";
        }
        return "redirect:/?error";

    }

    @GetMapping(value = "/view-cardpicture/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody
    byte[] viewCardPicture(@PathVariable(name = "url") String url) throws  IOException {
        String cardPictureURL = viewPath + defaultCardPicture;
        if(url != null && !url.equals("null")) {
            cardPictureURL = viewPath + url + ".jpg";
        }
        InputStream inputStream;

        try{
            ClassPathResource resource = new ClassPathResource(cardPictureURL);
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultCardPicture);
            inputStream = resource.getInputStream();
            e.printStackTrace();
        }

        return IOUtils.toByteArray(inputStream);
    }

    @GetMapping("/item/{id}")
    public String itemPage(Model model,
                           @PathVariable(name = "id") Long id) {
        Items item = itemsService.getItem(id);
        List<Images> images = itemsService.getAllImagesByItemId(id);
        List<Comments> comments = itemsService.getAllCommentsByItemId(id);
        if(item != null) {
            model.addAttribute("item", item);
            model.addAttribute("comments", comments);
            model.addAttribute("images", images);
            model.addAttribute("CURRENT_USER", getUser());
            model.addAttribute("brands", getBrands());
            model.addAttribute("categories", getCategories());

            return "item";
        }
        return "redirect:/?error";
    }

    @PostMapping("/add-comment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@RequestParam(name = "item_id") Long itemId,
                             @RequestParam(name = "comment") String commentText) {
        Items item = itemsService.getItem(itemId);
        if(item != null && getUser() != null) {
            Comments comment = new Comments();
            comment.setComment(commentText);
            comment.setItem(item);
            comment.setUser(getUser());

            itemsService.addComment(comment);

            return "redirect:/item/" + itemId;
        }
        return "redirect:/item/" + itemId + "?error";
    }

    @PostMapping("/delete-comment")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@RequestParam(name = "comment_id") Long commentId) {
        Comments comment = itemsService.getComment(commentId);
        Long itemId = 1L;
        if(getUser().getId().equals(comment.getUser().getId())) {
            itemId = comment.getItem().getId();
            if(comment != null) {
                itemsService.deleteComment(commentId);

                return "redirect:/item/" + itemId;
            }
        }
        return "redirect:/item/" + itemId + "?error";
    }

    @PostMapping("/view-basket-items")
    public String viewItems(@RequestParam(name = "basket_array") ArrayList<Long> basketIdList,
                            Model model) {
        List<Items> basketItems = new ArrayList<>();
        if(!basketIdList.isEmpty() && basketIdList != null){
            Items item = null;
            for(Long itemId : basketIdList) {
                item = itemsService.getItem(itemId);
                if(item != null) basketItems.add(item);
            }

            model.addAttribute("items", basketItems);
        }
        model.addAttribute("CURRENT_USER", getUser());
        model.addAttribute("brands", getBrands());
        model.addAttribute("categories", getCategories());
        return "basket";
    }




        public Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            return (Users) authentication.getPrincipal();
        }
        return null;
    }

    public List<Categories> getCategories() {
        List<Categories> categories = itemsService.getCategories();
        return categories;
    }

    public List<Brands> getBrands() {
        List<Brands> brands = itemsService.getAllBrands();
        return brands;
    }

}
