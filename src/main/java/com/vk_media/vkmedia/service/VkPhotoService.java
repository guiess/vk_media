package com.vk_media.vkmedia.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.dto.PhotoWithImage;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VkPhotoService {

    VkAuthService vkAuthService;

    public VkPhotoService(VkAuthService vkAuthService) {
        this.vkAuthService = vkAuthService;
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
                .photos().get(vkAuthService.getActor()).albumId(albumId.toString()).extended(true).execute().getItems();
        return photos.stream()
                        .map(photo -> new PhotoWithImage(
                                ObjectId.get(),
                                photo.getId().toString(),
                                photo.getAlbumId(),
                                getImageURIFromSizes(photo.getSizes(), 133).toString(),
                                getImageURIFromSizes(photo.getSizes()).toString(),
                                photo.getText()))
                        .collect(Collectors.toList());
    }

    public PhotoWithImage getPhotoById(String photoId, String albumId) throws ClientException, ApiException {
        List<Photo> photos = vkAuthService.getVkApiClient()
                .photos().get(vkAuthService.getActor()).albumId(albumId).photoIds(photoId).photoSizes(true).extended(true).execute().getItems();
        if (photos.isEmpty()) {
            return null;
        }

        Photo photo = photos.get(0);
        return new PhotoWithImage(
                ObjectId.get(),
                photo.getId().toString(),
                photo.getAlbumId(),
                getImageURIFromSizes(photo.getSizes(), 133).toString(),
                getImageURIFromSizes(photo.getSizes()).toString(),
                photo.getText());
    }

    public void savePhotoTags(PhotoWithImage photo) throws ClientException, ApiException {
        vkAuthService.getVkApiClient()
                .photos().edit(vkAuthService.getActor(), Integer.parseInt(photo.getVkId())).caption(photo.getTags()).execute();
    }
}
