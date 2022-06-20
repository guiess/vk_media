package com.vk_media.vkmedia.repository;

import com.vk_media.vkmedia.dto.PhotoWithImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "photos", path = "photos")
public interface MongoDBRepository extends MongoRepository<PhotoWithImage, String> {

    List<PhotoWithImage> findByTags(@Param("tags") String tags);

    @Query(value = "{tags: {$regex : ?0} }")
    List<PhotoWithImage> findByTagsRegex(@Param("tags") String tags);

}
