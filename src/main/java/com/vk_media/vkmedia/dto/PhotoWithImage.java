package com.vk_media.vkmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoWithImage {
    private int id;
    private int albumId;
    private URI previewPhotoURI;
    private URI photoURI;
}
