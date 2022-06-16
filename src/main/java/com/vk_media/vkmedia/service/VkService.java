package com.vk_media.vkmedia.service;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk_media.vkmedia.dto.Album;
import com.vk_media.vkmedia.dto.PhotoWithImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VkService {

    @Value("${vk.app.id}")
    private Integer vkAppId;
    @Value("${vk.app.secret}")
    private String vkAppSecret;

    private static final String AUTH_REDIRECT_URL = "http://localhost:8080/init";

    private String code;
    private final VkApiClient vk;
    private UserActor actor;
    private int userId;
    private List<Album> albums;

    {
        TransportClient transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isAuthorized() {
        return code != null;
    }

    public String getAuthUrl() {
        return "https://oauth.vk.com/authorize?client_id=" + vkAppId + "&display=page&scope=12&response_type=code&v=5.131&redirect_uri=" + AUTH_REDIRECT_URL;
    }

    public void initialize() throws ApiException, ClientException {

        if (!isAuthorized()) {
            return;
        }
        UserAuthResponse authResponse = vk.oAuth()
                .userAuthorizationCodeFlow(
                        vkAppId,
                        vkAppSecret,
                        AUTH_REDIRECT_URL,
                        code
                )
                .execute();
        actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        userId = authResponse.getUserId();
    }

    public List<Album> getPhotoAlbums() {
        if (albums != null) {
            return albums;
        }
        loadAlbums();
        return albums;
    }

    public Album getAlbumById(int albumId) {
        Optional<Album> albumOptional = albums.stream().filter(album -> album.getId() == albumId).findFirst();
        return albumOptional.orElse(null);
    }

    private void loadAlbums() {
        if (actor == null) {
            return;
        }
        try {
            List<PhotoAlbumFull> photoAlbumFulls = vk.photos().getAlbums(actor).photoSizes(true).needCovers(true).execute().getItems();
            albums = photoAlbumFulls.stream()
                    .map(album -> new Album(album.getId(), album.getTitle(), getImageURIFromSizes(album.getSizes())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("!!!!! getPhotoAlbums Exception: ");
            e.printStackTrace();
        }
    }

    private URI getImageURIFromSizes(List<PhotoSizes> sizes) {
        if (sizes != null && !sizes.isEmpty()) {
            return sizes.stream().max(Comparator.comparingInt(PhotoSizes::getHeight)).get().getUrl();
        }
        return null;
    }

    public List<PhotoWithImage> getPhotosByAlbum(Album album) throws ClientException, ApiException {
        List<Photo> photos = vk.photos().get(actor).albumId(album.getId().toString()).execute().getItems();
        return photos.stream()
                        .map(photo -> new PhotoWithImage(photo.getId(), photo.getAlbumId(), getImageURIFromSizes(photo.getSizes())))
                        .collect(Collectors.toList());
    }

}
