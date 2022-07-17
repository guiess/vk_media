package com.vk_media.vkmedia.service;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VkAuthService {

    @Value("${vk.app.id}")
    private Integer vkAppId;
    @Value("${vk.app.secret}")
    private String vkAppSecret;

    private String code;
    private final VkApiClient vkApiClient;
    private int userId;

    private UserActor actor;
    private final String authRedirectUrl;

    {
        TransportClient transportClient = new HttpTransportClient();
        vkApiClient = new VkApiClient(transportClient);
        authRedirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/init";
    }

    public UserActor getActor() {
        return actor;
    }

    public VkApiClient getVkApiClient() {
        return vkApiClient;
    }

    public boolean isAuthorized() {
        return actor != null;
    }

    public String getAuthUrl() {
        return "https://oauth.vk.com/authorize?client_id=" + vkAppId + "&display=page&scope=12&response_type=code&v=5.131&redirect_uri=" + authRedirectUrl;
    }

    public void initialize(String code) throws ApiException, ClientException {
        if (code == null || code.isEmpty()) {
            return;
        }

        this.code = code;
        UserAuthResponse authResponse = vkApiClient.oAuth()
                .userAuthorizationCodeFlow(
                        vkAppId,
                        vkAppSecret,
                        authRedirectUrl,
                        code
                )
                .execute();
        actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        userId = authResponse.getUserId();
    }
}
