package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.PhotoWithTags;
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
            PhotoWithTags photo = vkPhotoService.getPhotoById(photoVkId, albumId);
            photo.setTags(tags);
            mongoPhotoService.putPhotoWithTags(photo);
            vkPhotoService.savePhotoTags(photo);
        } catch (Exception e) {
            return "Fail: " + e.getMessage();
        }
        return "Success";
    }

    @GetMapping("/getPhotosByTagRest")
    public List<PhotoWithTags> getPhotoByTag(String tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return mongoPhotoService.getPhotosByTag(tags);
    }

    @GetMapping("/getExistingTagsRest")
    public List<String> getExistingTagsRest() {
        return mongoPhotoService.getExistingTags();
    }

    @GetMapping("/getPhotosByIdsRest")
    public List<PhotoWithTags> getPhotosByIds(@RequestBody List<String> ids) {
        return mongoPhotoService.getPhotosById(ids);
    }
}
