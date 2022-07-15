package com.vk_media.vkmedia.service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.vk_media.vkmedia.dto.PhotoWithTags;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoPhotoService {

    @Autowired
    MongoCollection<PhotoWithTags> mongoPhotoCollection;

    public List<PhotoWithTags> getPhotosByTag(String tag) {
        List<PhotoWithTags> photos = new ArrayList<>();
        Bson regex = Filters.regex("tags", convertToRegex(tag));
        FindIterable<PhotoWithTags> iterable = getMongoCollection().find(regex, PhotoWithTags.class);
        iterable.forEach(photos::add);
        return photos;
    }

    private String convertToRegex(String tags) {
        return "^" +
                Arrays.stream(tags.split(" ")).map(tag -> "(?=.*?" + tag + ")").collect(Collectors.joining());

    }

    public void addPhotoWithTag(PhotoWithTags photo) {
        if (photo != null && photo.getTags() != null && !photo.getTags().isEmpty()) {
            getMongoCollection().insertOne(photo);
        }
    }

    public List<String> getExistingTags() {
        List<String> tags = new ArrayList<>();
        getMongoCollection().distinct("tags", String.class).forEach(tags::add);
        return tags.stream()
                .map(tag -> Arrays.asList(tag.split(" ")))
                .flatMap(List::stream)
                .filter(str -> str != null && !str.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<PhotoWithTags> getPhotosById(List<String> ids) {
        List<PhotoWithTags> result = new ArrayList<>();
        getMongoCollection()
                .find(Filters.in("_id", ids.stream().map(ObjectId::new).collect(Collectors.toList())))
                .forEach(result::add);
        return result;
    }

    protected MongoCollection<PhotoWithTags> getMongoCollection() {
        return mongoPhotoCollection;
    }
}
