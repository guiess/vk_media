package com.vk_media.vkmedia.controller;

import com.vk_media.vkmedia.exception.VkUnauthenticatedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ExceptionHandlerController {

    public static String NOT_AUTHORIZED_IN_VK = "You are not authorized in VK. Please authorize first";

    @ExceptionHandler({VkUnauthenticatedException.class})
    public ModelAndView vkAuthException(RedirectAttributes attributes) {
        attributes.addFlashAttribute("error", NOT_AUTHORIZED_IN_VK);
        return new ModelAndView("redirect:/");
    }

}
