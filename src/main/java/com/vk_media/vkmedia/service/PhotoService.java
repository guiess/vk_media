package com.vk_media.vkmedia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk_media.vkmedia.dto.PhotoWithTags;
import com.vk_media.vkmedia.dto.VkIdsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PhotoService {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${photo.api.endpoint}")
    private String apiEndpoint;

    @Value("${photo.api.write.token}")
    private String writeToken;

    public List<String> getExistingTags() {
        return restTemplate.exchange(
                        apiEndpoint + "/photos/getExistingTagsRest",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<String>>() {}).getBody();
    }

    public String putPhotoWithTags(PhotoWithTags photo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = null;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(photo), headers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return restTemplate.exchange(
                apiEndpoint + "/photos/putPhotoWithTagRest?token=" + writeToken,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<String>() {}).getBody();
    }

    public List<PhotoWithTags> getPhotosByVkIds(List<String> vkIds, Integer albumId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = null;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(new VkIdsDTO(vkIds, albumId)), headers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return restTemplate.exchange(
                apiEndpoint + "/photos/getPhotosByAlbumAndVkIdsRest",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<PhotoWithTags>>() {}).getBody();
    }

}
