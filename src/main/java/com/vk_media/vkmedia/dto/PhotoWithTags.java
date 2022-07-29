package com.vk_media.vkmedia.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhotoWithTags {
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
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
