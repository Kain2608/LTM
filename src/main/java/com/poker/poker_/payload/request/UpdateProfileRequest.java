package com.poker.poker_.payload.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 255)
    private String avatar;
    
    @Size(max = 100)
    private String email;
}
