package item;

import booking.dto.BookingShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import user.dto.UserShortDto;

import java.util.List;


@AllArgsConstructor
@Builder
@Data
public class ItemDto {

    long id;
    UserShortDto owner;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingShortDto lastBooking;
    BookingShortDto nextBooking;
    List<CommentDto> comments;

}
