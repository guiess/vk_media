package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.PhotoWithImage;
import com.vk_media.vkmedia.service.MongoPhotoService;
import com.vk_media.vkmedia.service.VkPhotoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/photos")
public class PhotoRestController {

    VkPhotoService vkPhotoService;
    MongoPhotoService mongoPhotoService;

    PhotoRestController(VkPhotoService vkPhotoService, MongoPhotoService mongoPhotoService) {
        this.vkPhotoService = vkPhotoService;
        this.mongoPhotoService = mongoPhotoService;
    }

    @PostMapping("/addPhotoWithTagRest")
    public String addPhotoTags(String photoVkId,
                               String albumId,
                               String tags) {
        if (photoVkId == null) {
            return "Fail: PhotoId must not be empty";
        }
        try {
            PhotoWithImage photo = vkPhotoService.getPhotoById(photoVkId, albumId);
            photo.setTags(tags);
            mongoPhotoService.addPhotoWithTag(photo);
            vkPhotoService.savePhotoTags(photo);
        } catch (Exception e) {
            return "Fail: " + e.getMessage();
        }
        return "Success";
    }

    @GetMapping("/getPhotosByTagRest")
    public List<PhotoWithImage> getPhotoByTag(String tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return mongoPhotoService.getPhotosByTag(tags);
    }

    @GetMapping("/getExistingTagsRest")
    public List<String> getExistingTagsRest() {
        return mongoPhotoService.getExistingTags();
    }
}
