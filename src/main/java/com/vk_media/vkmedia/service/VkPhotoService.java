package com.vk_media.vkmedia.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.dto.PhotoWithTags;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
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
                        .photos().getAlbums(vkAuthService.getActor()).needSystem(true).needCovers(true).execute().getItems();
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
                .photos().getAlbums(vkAuthService.getActor()).needSystem(true).albumIds(albumId).needCovers(true).execute().getItems();
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
        List<PhotoWithTags> photoWithTags = photoService.getPhotosByVkIds(vkIds, albumId);
        Map<String, PhotoWithTags> existingPhotos = photoWithTags != null? photoWithTags.stream()
                .collect(Collectors.toMap(PhotoWithTags::getVkId, photo -> photo)) : new HashMap<>();
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

    public void downloadAllPhotos() throws ClientException, ApiException, IOException {
        List<Album> albums = getPhotoAlbums();

        AtomicReference<Integer> failures = new AtomicReference<>(0);

        for (Album album : albums) {
            String albumFolderName = "downloads/photos/" + album.getTitle() + "_" + album.getId();
            File albumFolder = new File(albumFolderName);
            if (!albumFolder.exists()) {
                albumFolder.mkdirs();
            }
            System.out.println("Start downloading album: " + album.getTitle());
            for (int page = 0; page < album.getPages(); page++) {
                System.out.println("Downloading album: " + album.getTitle() + "; page " + page + " of " + album.getPages());
                List<Photo> photos = vkAuthService.getVkApiClient()
                        .photos()
                        .get(vkAuthService.getActor())
                        .albumId(album.getId().toString())
                        .offset(PHOTO_BATCH_SIZE * (page))
                        .extended(true)
                        .execute()
                        .getItems();

                /*photos.parallelStream().forEach(photo -> {
                    boolean result = false;
                    try {
                        result = downloadPhoto(photo, albumFolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!result) {
                        failures.getAndSet(failures.get() + 1);

                    }
                });

                if (failures.get() > MAX_ERRORS) {
                    System.out.println("Max errors amount reached. terminate");
                }*/

                List<Future<Void>> futures = new ArrayList<>();

                int index = PHOTO_BATCH_SIZE * (page) + 1;
                for (Photo photo : photos) {
                    int finalIndex = index;
                    futures.add(executorService.submit(() -> {
                        boolean result = false;
                        try {
                            result = downloadPhoto(finalIndex, photo, albumFolder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!result) {
                            failures.getAndSet(failures.get() + 1);
                        }
                        return null;
                    }));
                    index++;
                }

                for (Future future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Error in task: " + e.getMessage());
                    }
                }
            }
            System.out.println("Finish downloading album: " + album.getTitle());
        }
    }

    private final int MAX_ERRORS = 20;
    private static final int THREAD_POOL_SIZE = 30; // Adjust according to your needs
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private boolean downloadPhoto(int index, Photo photo, File albumFolder) throws IOException {

        String photoId = photo.getId().toString();
        File photoFile = new File(albumFolder, /*index + "_" + */photoId + ".jpg");

        if (photoFile.exists()) {
            System.out.println("File already exists: " + photoFile.getPath());
            return true;
        }

        String photoUrl = getImageURIFromSizes(photo.getSizes()).toString();

        try (InputStream in = new URL(photoUrl).openStream();
             OutputStream out = new FileOutputStream(photoFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // Delete the file if download failed
            System.out.println("Problem with downloading file: " + photoFile.getPath() + "; e: " + e.getMessage());
            if (photoFile.exists()) {
                photoFile.delete();
            }

            return false; // Rethrow the exception for further handling
        }
        return true;
    }
}
