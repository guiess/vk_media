package com.vk_media.vkmedia.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.dto.PhotoWithTags;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@Service
public class VkPhotoService {

    public final int PHOTO_BATCH_SIZE = 50;

    VkAuthService vkAuthService;
    PhotoService photoService;

    public VkPhotoService(VkAuthService vkAuthService, PhotoService photoService) {
        this.vkAuthService = vkAuthService;
        this.photoService = photoService;
    }

    public List<Album> getPhotoAlbums() {
        if (vkAuthService.isAuthorized()) {
            try {
                List<PhotoAlbumFull> photoAlbumFulls = vkAuthService.getVkApiClient()
                        .photos().getAlbums(vkAuthService.getActor()).needCovers(true).execute().getItems();
                return photoAlbumFulls.stream()
                        .map(album -> new Album(album.getId(), album.getTitle(), album.getThumbSrc(),
                                getPagesAmount(album.getSize())))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.out.println("!!!!! getPhotoAlbums Exception: ");
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    private URI getPreviewImageURIFromSizes(List<PhotoSizes> sizes) {
        if (sizes != null && !sizes.isEmpty()) {
            OptionalInt optionalHeight = sizes.stream()
                    .mapToInt(PhotoSizes::getHeight)
                    .filter(x -> x > 100)
                    .min();
            int height = optionalHeight.isPresent() ? optionalHeight.getAsInt() :
                    sizes.stream().mapToInt(PhotoSizes::getHeight).max().getAsInt();

            Optional<PhotoSizes> result = sizes.stream()
                    .filter(size -> size.getHeight() == height)
                    .min(Comparator.comparingInt(PhotoSizes::getWidth));
            if (result.isPresent()) {
                return result.get().getUrl();
            }
        }
        return null;
    }

    private URI getImageURIFromSizes(List<PhotoSizes> sizes) {
        if (sizes != null && !sizes.isEmpty()) {
            int maxHeight = sizes.stream()
                    .mapToInt(PhotoSizes::getHeight)
                    .max().getAsInt();
            Optional<PhotoSizes> result = sizes.stream()
                    .filter(size -> size.getHeight() == maxHeight)
                    .max(Comparator.comparingInt(PhotoSizes::getWidth));
            if (result.isPresent()) {
                return result.get().getUrl();
            }
        }
        return null;
    }

    public Album getAlbumById(Integer albumId) throws ClientException, ApiException {
        List<PhotoAlbumFull> photoAlbumFulls = vkAuthService.getVkApiClient()
                .photos().getAlbums(vkAuthService.getActor()).albumIds(albumId).needCovers(true).execute().getItems();
        if (photoAlbumFulls != null && !photoAlbumFulls.isEmpty()) {
            PhotoAlbumFull photoAlbumFull = photoAlbumFulls.get(0);
            return new Album(photoAlbumFull.getId(), photoAlbumFull.getTitle(),
                    photoAlbumFull.getThumbSrc(), getPagesAmount(photoAlbumFull.getSize()));
        }
        return null;
    }

    public List<PhotoWithTags> getPhotosByAlbumId(Integer albumId, int page) throws ClientException, ApiException {
        List<Photo> photos = vkAuthService.getVkApiClient()
                .photos()
                .get(vkAuthService.getActor())
                .albumId(albumId.toString())
                .offset(PHOTO_BATCH_SIZE * (page - 1))
                .extended(true)
                .execute()
                .getItems();

        List<String> vkIds = photos.stream().map(photo -> photo.getId().toString()).collect(Collectors.toList());
        Map<String, PhotoWithTags> existingPhotos = photoService.getPhotosByVkIds(vkIds, albumId).stream()
                .collect(Collectors.toMap(PhotoWithTags::getVkId, photo -> photo));
        return photos.stream()
                .map(photo -> getPhotoWithTags(photo, albumId, existingPhotos.get(photo.getId().toString())))
                .collect(Collectors.toList());
    }

    private PhotoWithTags getPhotoWithTags(Photo photo, Integer albumId, PhotoWithTags existingPhoto) {
        if (existingPhoto != null) {
            if (StringUtils.isNotEmpty(photo.getText()) &&
                    !existingPhoto.getTags().equals(photo.getText())) {
                existingPhoto.mergeTags(photo.getText());
                photoService.putPhotoWithTags(existingPhoto);
                try {
                    savePhotoTags(existingPhoto);
                } catch (ClientException | ApiException e) {
                    e.printStackTrace();
                }
            }
            return existingPhoto;
        }
        PhotoWithTags newPhoto = new PhotoWithTags(
                null,
                photo.getId().toString(),
                albumId,
                getPreviewImageURIFromSizes(photo.getSizes()).toString(),
                getImageURIFromSizes(photo.getSizes()).toString(),
                photo.getText());
        if (StringUtils.isNotEmpty(newPhoto.getTags().trim())) {
            photoService.putPhotoWithTags(newPhoto);
        }
        return newPhoto;
    }

    public PhotoWithTags getPhotoById(String photoId, String albumId) throws ClientException, ApiException {
        List<Photo> photos = vkAuthService.getVkApiClient()
                .photos().get(vkAuthService.getActor()).albumId(albumId).photoIds(photoId).photoSizes(true).extended(true).execute().getItems();
        if (photos.isEmpty()) {
            return null;
        }

        Photo photo = photos.get(0);
        return new PhotoWithTags(
                null,
                photo.getId().toString(),
                photo.getAlbumId(),
                getPreviewImageURIFromSizes(photo.getSizes()).toString(),
                getImageURIFromSizes(photo.getSizes()).toString(),
                photo.getText());
    }

    public void savePhotoTags(PhotoWithTags photo) throws ClientException, ApiException {
        vkAuthService.getVkApiClient()
                .photos().edit(vkAuthService.getActor(), Integer.parseInt(photo.getVkId())).caption(photo.getTags()).execute();
    }

    private int getPagesAmount(int photosAmount) {
        return (photosAmount + PHOTO_BATCH_SIZE - 1) / PHOTO_BATCH_SIZE;
    }
}
