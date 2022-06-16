package com.vk_media.vkmedia.controller;

import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk_media.vkmedia.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/init")
public class VkAuthRestController {

    //@Autowired
    VkService vkService;

    public VkAuthRestController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping
    public ModelAndView setCode(@RequestParam("code") String code) {
        vkService.setCode(code);
        try {
            vkService.initialize();
        } catch (Exception e) {
            System.out.println("!!!!! VkInitRestController.setCode Exception: ");
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/");
    }
}
