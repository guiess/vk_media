package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.service.VkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MediaController {

    VkService vkService;

    public MediaController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping()
    public String isLoggedIn(Model model) {
        model.addAttribute("isAuthorized", vkService.isAuthorized());
        if (!vkService.isAuthorized()) {
            model.addAttribute("authUrl", vkService.getAuthUrl());
        }
        return "index";
    }
}
