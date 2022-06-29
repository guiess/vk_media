package com.vk_media.vkmedia.dto;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "photos")
public class PhotoWithImage {
    @Id
    @BsonId
    @BsonProperty("_id")
    private ObjectId id;
    private String vkId;
    private int albumId;
    private String previewPhotoURI;
    private String photoURI;
    private String tags;
}
