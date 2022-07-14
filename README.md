# VK Media

Web service providing an ability to add tags for your images in VK, 
add tags to any images on the internet and look for the images by tags.

VK is a social network (https://vk.com/) with a massive media support like storing and sharing of images, music, video

## Context
1. [VK related UI](#vk-related)
1. [Common UI](#common-ui)
1. [REST](#rest)
1. [Mobile App](#mobile)   
1. [Useful test links](#test)

## <a name="vk-related"></a>VK related UI

### Main page
initial page with VK auth and link to VK photo albums after the successful auth:  
https://vk-media-prod-vk-media-3oqoex.mo2.mogenius.io
   
### User Albums
   
### Album page
Album images page with an ability to add tags  
![album image](./img/album_screen.JPG)
Image tags information is stored in Mongo DB and also in the description of the photo in VK
   
## <a name="common-ui"></a>Common UI

### Add image with tag:  

https://vk-media-prod-vk-media-3oqoex.mo2.mogenius.io/photos/addPhotoWithTag

technically not related to VK but in order to reduce the access from outside it also requires VK auth.
![add image](./img/add_image_with_tag.JPG)
   
### Find photos by tag:  

https://vk-media-prod-vk-media-3oqoex.mo2.mogenius.io/photos/findPhotosByTag  

![find image](./img/find_image_by_tag.JPG)
   
##  <a name="rest"></a>REST

### Add photo with tags  
   Used by album page to add tags to the selected image via AJAX.  
   Requires VK auth both for work and outside access prohibition  

   `POST /photos/addPhotoWithTagRest`
   
#### Request

    curl --location --request POST 'http://localhost:8080/photos/addPhotoWithTagRest' \
    --header 'Content-Type: application/json' \
    --data-raw '{
       photoVkId: {photoVkId},
       albumId: {albumId},
       tags: {tag}
    }'
   
### Get Existing tags.  

`GET /photos/getExistingTagsRest`

#### Request

    curl --location --request GET 'http://localhost:8080/photos/getExistingTagsRest'

#### Response

    [
      "away",
      "cat"
    ]

### Get Photos by Tags

`GET /photos/getPhotosByTagRest`

#### Request

    curl --location --request GET 'http://localhost:8080/photos/getPhotosByTagRest?tags=cat'

#### Response

    [
       {
           "id": {
               "timestamp": 1655582281,
               "date": "2022-06-18T19:58:01.000+00:00"
           },
           "vkId": null,
           "albumId": 123123,
           "previewPhotoURI": "https://sun9-32.userapi.com/impf/tvia2JFbcot4B-tQ31KVwQEzNwWMt4VGkWA3sQ/vLJg8Zk1g_E.jpg?size=1200x1105&quality=95&sign=23c9728ebd3eba8aaaf18879ae9e3161&type=album",
           "photoURI": "https://sun9-32.userapi.com/impf/tvia2JFbcot4B-tQ31KVwQEzNwWMt4VGkWA3sQ/vLJg8Zk1g_E.jpg?size=1200x1105&quality=95&sign=23c9728ebd3eba8aaaf18879ae9e3161&type=album",
           "tags": "cat"
       }
    ]

## <a name="mobile"></a>Mobile app

### Google Play

https://play.google.com/store/apps/details?id=com.guiess.gr_media_app

## <a name="test"></a>Useful test links

test with sample docker containers  
https://www.testcontainers.org/quickstart/junit_5_quickstart/  
https://www.baeldung.com/spring-boot-testcontainers-integration-test