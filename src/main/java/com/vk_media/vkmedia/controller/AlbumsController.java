package com.vk_media.vkmedia.controller;

import com.vk.api.sdk.objects.photos.Photo;
import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    @Autowired
    VkService vkService;


    /*public AlbumsController(VkService vkService) {
        this.vkService = vkService;
    }*/

    @GetMapping
    public String viewAlbums(Model model) {
        try {
            model.addAttribute("albums", vkService.getPhotoAlbums());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Error", e);
        }
        return "albums";
    }

    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable String id, Model model) {
        try {
            Album album = vkService.getAlbumById(Integer.parseInt(id));
            model.addAttribute("album", album);
            model.addAttribute("photos", vkService.getPhotosByAlbum(album));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Error", e);
        }
        return "album";
    }
}
