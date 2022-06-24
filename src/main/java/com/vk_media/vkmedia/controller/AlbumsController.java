package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.service.VkPhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    VkPhotoService vkPhotoService;


    public AlbumsController(VkPhotoService vkPhotoService) {
        this.vkPhotoService = vkPhotoService;
    }

    @GetMapping
    public String viewAlbums(Model model) {
        try {
            model.addAttribute("albums", vkPhotoService.getPhotoAlbums());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Error", e);
        }
        return "albums";
    }

    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable int id, Model model) {
        try {
            model.addAttribute("album", vkPhotoService.getAlbumById(id));
            model.addAttribute("photos", vkPhotoService.getPhotosByAlbumId(id));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Error", e);
        }
        return "album";
    }

    @GetMapping("/find")
    public String findAlbum(Model model) {
        return "findAlbum";
    }

    @PostMapping("/find")
    public String showFoundAlbum(@ModelAttribute("id") Integer id, BindingResult result, Model model) {
        if (id == 0) {
            model.addAttribute("result", "Not found");
        }
        try {
            Album album = vkPhotoService.getAlbumById(id);
            if (album != null) {
                model.addAttribute("album", vkPhotoService.getAlbumById(id));
                model.addAttribute("photos", vkPhotoService.getPhotosByAlbumId(id));
            } else {
                model.addAttribute("result", "Not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Error", e);
        }
        return "findAlbum";
    }
}
