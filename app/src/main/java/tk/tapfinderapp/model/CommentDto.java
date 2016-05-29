package tk.tapfinderapp.model;

import java.util.Date;

import lombok.Data;

@Data
public class CommentDto {
    String text;
    String userName;
    Date date;
    String placeId;
}
