package com.sharedule.app.dto;

import lombok.*;
import jakarta.validation.constraints.Pattern;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditItemDTO {
        @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9\\s]{8,}$", message = "Item name must be at least 8 characters long and contain alphabets (numbers optional)")
        private String itemName;

        @Pattern(regexp = "^(?:\\S+\\s+){7,}\\S+$", message = "Item description must be at least 8 words long")
        private String itemDescription;

        private Double itemPrice;

        private Long itemStock;

        private Boolean itemAvailable;

        @Pattern(regexp = "^(http|https)://.*$", message = "Invalid image URL format")
        private String itemImage;
}
