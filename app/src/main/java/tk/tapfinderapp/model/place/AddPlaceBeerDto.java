package tk.tapfinderapp.model.place;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddPlaceBeerDto {
    int beerId;
    String placeId;
    String description;
    double price;
}
