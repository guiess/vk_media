package com.vk_media.vkmedia.repository;

import com.vk_media.vkmedia.dto.PhotoWithTags;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * easier version that MongoTemplate (and no need with Codec), but can't be used in tests with test DB instance
 */
@RepositoryRestResource(collectionResourceRel = "photos", path = "photos")
public interface MongoDBRepository extends MongoRepository<PhotoWithTags, String> {

    @Query(value = "{tags: {$regex : ?0} }")
    List<PhotoWithTags> findByTagsRegex(@Param("tags") String tags);

}
