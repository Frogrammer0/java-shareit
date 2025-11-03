package request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequestDto {

    long id;
    String description;
    long requestorId;
    LocalDate created;
}
