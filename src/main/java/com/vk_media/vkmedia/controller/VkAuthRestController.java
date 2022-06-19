package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.dto.PhotoWithImage;
import com.vk_media.vkmedia.repository.MongoDBRepository;
import com.vk_media.vkmedia.service.VkAuthService;
import com.vk_media.vkmedia.service.VkPhotoService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

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


    @GetMapping("/recognize")
    public String getTextFromImage() throws IOException {
        String imageURL = "https://sun9-8.userapi.com/impf/pdRtd00XMhtcIeRAWjthZSiCPEKkas1_w419jA/qWybBC2nH6U.jpg?size=604x383&quality=96&sign=e9e35bc840f4ba3c2f277814c982a1ec&type=album";
        String result = "no result";
        //File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        URL url = new URL(imageURL);
        BufferedImage img = ImageIO.read(url);
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("rus");
        tesseract.setDatapath("tessdata");
        try {
            result = tesseract.doOCR(img);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }

}
