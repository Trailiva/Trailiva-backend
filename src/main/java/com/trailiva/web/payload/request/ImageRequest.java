package com.trailiva.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ImageRequest {
    @NotBlank(message = "image url cannot be blank")
    private String url;

    @JsonProperty("public_id")
    @NotBlank(message = "image public id cannot be blank")
    private String publicId;
}
