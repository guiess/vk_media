FROM java
ADD /target/*.war vk_media.war
ENTRYPOINT ["java", "-jar", "/vk_media.war"]