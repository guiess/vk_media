package com.vk_media.vkmedia.service;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vk_media.vkmedia.dto.PhotoWithTags;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoPhotoServiceTest {

    private final static int MONGO_PORT = 27017;

    private MongoClient mongoClient;
    private MongoCollection<PhotoWithTags> collection;

    private MongoPhotoService mongoPhotoService;

    @ClassRule
    @Container
    public static GenericContainer<?> mongo = new GenericContainer<>(DockerImageName.parse("mongo"))
            .withExposedPorts(MONGO_PORT);

    @BeforeEach
    public void setUp() {
        mongoClient = MongoClients.create("mongodb://" + mongo.getHost() + ":" + mongo.getMappedPort(MONGO_PORT));
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = mongoClient.getDatabase("test").withCodecRegistry(pojoCodecRegistry);
        collection = database.getCollection("testCollection", PhotoWithTags.class);
        PhotoWithTags image1 = new PhotoWithTags(
                ObjectId.get(),
                "1",
                10,
                "http://test.com/previewImgUrl1.jpg",
                "http://test.com/imgUrl1.jpg",
                "Btag Ztag Mtag Atag");
        PhotoWithTags image2 = new PhotoWithTags(
                ObjectId.get(),
                "2",
                10,
                "http://test.com/previewImgUrl2.jpg",
                "http://test.com/imgUrl2.jpg",
                "Qtag Atag Mtag");
        collection.insertOne(image1);
        collection.insertOne(image2);

        mongoPhotoService = new TestableMongoPhotoService(collection);
    }

    @AfterEach
    public void afterEach() {
        collection.drop();
        mongoClient.close();
    }

    @Test
    public void getExistingTags() {
        String existingTags = String.join(";", mongoPhotoService.getExistingTags());
        assertEquals("Atag;Btag;Mtag;Qtag;Ztag", existingTags, "Error on getExistingTags");
    }

    @Test
    public void getPhotosByTagTest() {
        List<PhotoWithTags> result = mongoPhotoService.getPhotosByTag("Atag Mtag");
        assertEquals(result.size(), 2);

        result = mongoPhotoService.getPhotosByTag("Qtag Mtag");
        assertEquals(result.size(), 1);
    }

    static class TestableMongoPhotoService extends MongoPhotoService {

        private final MongoCollection<PhotoWithTags> mongoCollection;

        public TestableMongoPhotoService(MongoCollection<PhotoWithTags> mongoCollection) {
            this.mongoCollection = mongoCollection;
        }

        protected MongoCollection<PhotoWithTags> getMongoCollection() {
            return mongoCollection;
        }
    }
}
