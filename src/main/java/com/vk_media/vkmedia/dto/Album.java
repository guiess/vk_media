package com.vk_media.vkmedia.dto;

import lombok.*;

import java.net.URI;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    private Integer id;
    private String title;
    private URI coverURI;
}
