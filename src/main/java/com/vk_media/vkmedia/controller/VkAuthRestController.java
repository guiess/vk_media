package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.service.VkAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/init")
public class VkAuthRestController {

    VkAuthService vkAuthService;

    public VkAuthRestController(VkAuthService vkAuthService) {
        this.vkAuthService = vkAuthService;
    }

    @GetMapping
    public ModelAndView setCode(@RequestParam("code") String code) {
        try {
            vkAuthService.initialize(code);
        } catch (Exception e) {
            System.out.println("!!!!! VkInitRestController.setCode Exception: ");
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/");
    }
}
