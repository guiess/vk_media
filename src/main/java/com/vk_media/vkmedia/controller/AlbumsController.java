package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.service.PhotoService;
import com.vk_media.vkmedia.service.VkPhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    VkPhotoService vkPhotoService;
    PhotoService photoService;

    public AlbumsController(VkPhotoService vkPhotoService, PhotoService photoService) {
        this.vkPhotoService = vkPhotoService;
        this.photoService = photoService;
    }

    @GetMapping
    public String viewAlbums(Model model) {
        try {
            model.addAttribute("albums", vkPhotoService.getPhotoAlbums());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
        }
        return "albums";
    }

    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable int id, @RequestParam(defaultValue="1") Integer page, Model model) {
        try {
            model.addAttribute("album", vkPhotoService.getAlbumById(id));
            model.addAttribute("photos", vkPhotoService.getPhotosByAlbumId(id, page));
            model.addAttribute("tags", photoService.getExistingTags());
            model.addAttribute("page", page);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
        }
        return "album";
    }

    @GetMapping("/find")
    public String findAlbum(Model model) {
        return "findAlbum";
    }

    @PostMapping("/find")
    public String showFoundAlbum(@ModelAttribute("id") Integer id, Model model) {
        if (id != 0) {
            try {
            Album album = vkPhotoService.getAlbumById(id);
            if (album != null) {
                model.addAttribute("album", vkPhotoService.getAlbumById(id));
                return "redirect:/albums/" + id;
            } else {
                model.addAttribute("result", "Not found");
            }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", e);
            }
        }
        model.addAttribute("result", "Not found");
        return "findAlbum";
    }
}
