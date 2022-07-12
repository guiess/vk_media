package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.PhotoWithImage;
import com.vk_media.vkmedia.service.MongoPhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/photos")
public class PhotosController {

    MongoPhotoService mongoPhotoService;

    public PhotosController(MongoPhotoService mongoPhotoService) {
        this.mongoPhotoService = mongoPhotoService;
    }

    @GetMapping("/findPhotosByTag")
    public String findPhotosByTag(Model model) {
        model.addAttribute("tags", mongoPhotoService.getExistingTags());
        return "findPhotos";
    }

    @PostMapping("/findPhotosByTag")
    public String showFoundAlbum(
            @ModelAttribute("tag") String tag,
            BindingResult result,
            Model model
    ) {
        model.addAttribute("tags", mongoPhotoService.getExistingTags());
        if (tag == null || tag.isEmpty()) {
            model.addAttribute("result", "Not found");
        } else {
            model.addAttribute("tag", tag);
            try {
                List<PhotoWithImage> photos = mongoPhotoService.getPhotosByTag(tag.toLowerCase());
                if (!photos.isEmpty()) {
                    model.addAttribute("photos", photos);
                } else {
                    model.addAttribute("result", "Not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", e);
            }
        }
        return "findPhotos";
    }

    @GetMapping("/addPhotoWithTag")
    public String addPhoto(Model model) {
        return "addPhoto";
    }

    @PostMapping("/addPhotoWithTag")
    public String addPhoto(
            @ModelAttribute("imageUrl") String imageUrl,
            @ModelAttribute("tag") String tag,
            BindingResult result,
            Model model
    ) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            model.addAttribute("result", "Image url should be set");
        } else if (tag == null || tag.isEmpty()) {
            model.addAttribute("result", "tag should be set");
        } else {
            try {
                PhotoWithImage newPhoto = new PhotoWithImage();
                newPhoto.setTags(tag.toLowerCase());
                newPhoto.setPhotoURI(imageUrl);
                mongoPhotoService.addPhotoWithTag(newPhoto);
                model.addAttribute("result", "photo added");
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", e);
            }
        }
        return "addPhoto";
    }
}
