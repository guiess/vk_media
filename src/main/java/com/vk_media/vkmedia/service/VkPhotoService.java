package com.vk_media.vkmedia.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.dto.PhotoWithImage;
import com.vk_media.vkmedia.repository.MongoDBRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VkPhotoService {

    VkAuthService vkAuthService;
    MongoDBRepository mongoDBRepository;

    public VkPhotoService(VkAuthService vkAuthService, MongoDBRepository mongoDBRepository) {
        this.vkAuthService = vkAuthService;
        this.mongoDBRepository = mongoDBRepository;
    }

    public List<Album> getPhotoAlbums() {
        if (vkAuthService.isAuthorized()) {
            try {
                List<PhotoAlbumFull> photoAlbumFulls = vkAuthService.getVkApiClient()
                        .photos().getAlbums(vkAuthService.getActor()).needCovers(true).execute().getItems();
                return photoAlbumFulls.stream()
                        .map(album -> new Album(album.getId(), album.getTitle(), album.getThumbSrc()))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.out.println("!!!!! getPhotoAlbums Exception: ");
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    private URI getImageURIFromSizes(List<PhotoSizes> sizes) {
        return getImageURIFromSizes(sizes, 0);
    }

    private URI getImageURIFromSizes(List<PhotoSizes> sizes, int height) {
        if (sizes != null && !sizes.isEmpty()) {
            if (height > 0) {
                Optional<PhotoSizes> result = sizes.stream().filter(photoSizes -> photoSizes.getHeight() == height).findFirst();
                if (result.isPresent()) {
                    return result.get().getUrl();
                }
            }
            return sizes.stream().max(Comparator.comparingInt(PhotoSizes::getHeight)).get().getUrl();
        }
        return null;
    }

    public Album getAlbumById(Integer albumId) throws ClientException, ApiException {
        List<PhotoAlbumFull> photoAlbumFulls = vkAuthService.getVkApiClient()
                .photos().getAlbums(vkAuthService.getActor()).albumIds(albumId).needCovers(true).execute().getItems();
        if (photoAlbumFulls != null && !photoAlbumFulls.isEmpty()) {
            PhotoAlbumFull photoAlbumFull = photoAlbumFulls.get(0);
            return new Album(photoAlbumFull.getId(), photoAlbumFull.getTitle(), photoAlbumFull.getThumbSrc());
        }
        return null;
    }

    public List<PhotoWithImage> getPhotosByAlbumId(Integer albumId) throws ClientException, ApiException {
        List<Photo> photos = vkAuthService.getVkApiClient()
                .photos().get(vkAuthService.getActor()).albumId(albumId.toString()).execute().getItems();
        return photos.stream()
                        .map(photo -> new PhotoWithImage(
                                photo.getId().toString(),
                                photo.getAlbumId(),
                                getImageURIFromSizes(photo.getSizes(), 133),
                                getImageURIFromSizes(photo.getSizes()),
                                null))
                        .collect(Collectors.toList());
    }

    public List<PhotoWithImage> getPhotosByTag(String tag, boolean isRegex) {
        if (isRegex) {
            return mongoDBRepository.findByTagsRegex(tag);
        }
        return mongoDBRepository.findByTags(tag);
    }

    public void addPhotoWithTag(PhotoWithImage photo) {
        if (photo != null && photo.getTags() != null && !photo.getTags().isEmpty()) {
            mongoDBRepository.insert(photo);
        }
    }

}
