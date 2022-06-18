package com.vk_media.vkmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URI;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "photos")
public class PhotoWithImage {
    @Id
    private String id;
    private int albumId;
    private URI previewPhotoURI;
    private URI photoURI;
    private String tags;
}
