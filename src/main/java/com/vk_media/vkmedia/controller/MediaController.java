package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.service.VkAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MediaController {

    //@Autowired
    VkAuthService vkAuthService;

    public MediaController(VkAuthService vkAuthService) {
        this.vkAuthService = vkAuthService;
    }

    @GetMapping()
    public String isLoggedIn(Model model) {
        model.addAttribute("isAuthorized", vkAuthService.isAuthorized());
        if (!vkAuthService.isAuthorized()) {
            model.addAttribute("authUrl", vkAuthService.getAuthUrl());
        }
        return "index";
    }
}
