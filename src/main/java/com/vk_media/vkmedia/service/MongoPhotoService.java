package com.vk_media.vkmedia.service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.vk_media.vkmedia.dto.PhotoWithImage;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoPhotoService {

    @Autowired
    MongoCollection<PhotoWithImage> mongoPhotoCollection;

    public List<PhotoWithImage> getPhotosByTag(String tag) {
        List<PhotoWithImage> photos = new ArrayList<>();
        Bson regex = Filters.regex("tags", convertToRegex(tag));
        FindIterable<PhotoWithImage> iterable = getMongoCollection().find(regex, PhotoWithImage.class);
        iterable.forEach(photos::add);
        return photos;
    }

    private String convertToRegex(String tags) {
        return "^" +
                Arrays.stream(tags.split(" ")).map(tag -> "(?=.*?" + tag + ")").collect(Collectors.joining());

    }

    public void addPhotoWithTag(PhotoWithImage photo) {
        if (photo != null && photo.getTags() != null && !photo.getTags().isEmpty()) {
            getMongoCollection().insertOne(photo);
        }
    }

    public List<String> getExistingTags() {
        List<String> tags = new ArrayList<>();
        DistinctIterable<String> iterable = getMongoCollection().distinct("tags", String.class);
        iterable.forEach(tags::add);
        return tags.stream()
                .map(tag -> Arrays.asList(tag.split(" ")))
                .flatMap(List::stream)
                .filter(str -> str != null && !str.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    protected MongoCollection<PhotoWithImage> getMongoCollection() {
        return mongoPhotoCollection;
    }
}
