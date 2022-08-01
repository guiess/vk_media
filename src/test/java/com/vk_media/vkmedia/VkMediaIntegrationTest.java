package com.vk_media.vkmedia;

import com.vk_media.vkmedia.controller.ExceptionHandlerController;
import com.vk_media.vkmedia.dto.PhotoWithTags;
import com.vk_media.vkmedia.exception.VkUnauthenticatedException;
import com.vk_media.vkmedia.service.PhotoService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = VkMediaApplication.class
)
@AutoConfigureMockMvc
public class VkMediaIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PhotoService photoService;

    @Test
    public void givenUnauthorizedSession_WhenGetAlbumCall_ThenRedirecteWithErrorMessage() throws Exception {
        mockMvc.perform(get("/albums/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof VkUnauthenticatedException))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", ExceptionHandlerController.NOT_AUTHORIZED_IN_VK));
    }
}
