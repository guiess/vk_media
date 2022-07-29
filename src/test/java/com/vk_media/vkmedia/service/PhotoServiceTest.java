package com.vk_media.vkmedia.service;

import com.vk_media.vkmedia.dto.PhotoWithTags;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PhotoServiceTest {

    private final static int MONGO_PORT = 27017;

    private PhotoService photoService;

    @ClassRule
    @Container
    public static GenericContainer<?> mongo = new GenericContainer<>(DockerImageName.parse("mongo"))
            .withExposedPorts(MONGO_PORT);


}
