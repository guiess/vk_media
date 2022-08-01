package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.PhotoWithTags;
import com.vk_media.vkmedia.service.PhotoService;
import com.vk_media.vkmedia.service.VkPhotoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/photos")
public class PhotoRestController {

    static final String SUCCESS_RESPONSE = "Success";

    VkPhotoService vkPhotoService;
    PhotoService photoService;

    PhotoRestController(VkPhotoService vkPhotoService, PhotoService photoService) {
        this.vkPhotoService = vkPhotoService;
        this.photoService = photoService;
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
            String result = photoService.putPhotoWithTags(photo);
            if (!SUCCESS_RESPONSE.equals(result)) {
                return result;
            }
            vkPhotoService.savePhotoTags(photo);
        } catch (Exception e) {
            return "Fail: " + e.getMessage();
        }
        return SUCCESS_RESPONSE;
    }
}
