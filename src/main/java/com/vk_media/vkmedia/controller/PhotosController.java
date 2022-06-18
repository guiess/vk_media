package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.dto.PhotoWithImage;
import com.vk_media.vkmedia.service.VkPhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/photos")
public class PhotosController {

    VkPhotoService vkPhotoService;

    public PhotosController(VkPhotoService vkPhotoService) {
        this.vkPhotoService = vkPhotoService;
    }

    @GetMapping("/findPhotosByTag")
    public String findPhotosByTag(Model model) {
        return "findPhotos";
    }

    @PostMapping("/findPhotosByTag")
    public String showFoundAlbum(
            @ModelAttribute("tag") String tag,
            BindingResult result,
            Model model
    ) {
        System.out.println("!! model" + result.getModel());
        if (tag == null || tag.isEmpty()) {
            model.addAttribute("result", "Not found");
        }
        model.addAttribute("tag", tag);
        try {
            List<PhotoWithImage> photos = vkPhotoService.getPhotosByTag(tag, true);
            if (!photos.isEmpty()) {
                model.addAttribute("photos", photos);
            } else {
                model.addAttribute("result", "Not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Error", e);
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
        }
        if (tag == null || tag.isEmpty()) {
            model.addAttribute("result", "tag should be set");
        }
        try {
            PhotoWithImage newPhoto = new PhotoWithImage();
            newPhoto.setTags(tag);
            newPhoto.setPhotoURI(URI.create(imageUrl));
            vkPhotoService.addPhotoWithTag(newPhoto);
            model.addAttribute("result", "photo added");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Error", e);
        }
        return "addPhoto";
    }
}
