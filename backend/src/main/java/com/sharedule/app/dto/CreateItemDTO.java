package com.sharedule.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemDTO {
    @NotBlank(message = "Item name is required")
    @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9\\s]{8,}$", 
            message = "Item name must be at least 8 characters long and contain alphabets (numbers optional)")
    private String itemName;

    @NotBlank(message = "Item description is required")
    @Pattern(regexp = "^(?:\\S+\\s+){7,}\\S+$", 
            message = "Item description must be at least 8 words long")
    private String itemDescription;

    @Min(value = 0, message = "Price must be 0 or above")
    private double itemPrice;

    @Min(value = 1, message = "Stock must be 1 or above")
    private long itemStock;

    private boolean itemAvailable;

    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid image URL format")
    private String itemImage;
}
