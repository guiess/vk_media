package com.vk_media.vkmedia.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "photos")
public class PhotoWithTags {
    @Id
    @BsonId
    @BsonProperty("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String vkId;
    private int albumId;
    private String previewPhotoURI;
    private String photoURI;
    private String tags;

    public void mergeTags(String tagsToMerge) {
        if (StringUtils.isEmpty(tags)) {
            tags = tagsToMerge;
            return;
        }
        List<String> mergedTags = Arrays.stream(tags.split(" ")).collect(Collectors.toList());
        mergedTags.addAll(Arrays.stream(tagsToMerge.split(" ")).collect(Collectors.toList()));
        tags = mergedTags.stream().distinct().sorted().collect(Collectors.joining(" "));
    }
}
