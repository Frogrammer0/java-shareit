package booking.dto;

import booking.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    long id;
    LocalDateTime start;
    LocalDateTime end;
    long itemId;
    long bookerId;
    Status status;
}
