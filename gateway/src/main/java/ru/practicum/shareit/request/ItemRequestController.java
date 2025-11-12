package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestBody @Valid ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") long xUserId
            ) {
        log.info("Create ItemRequest with userId = {}, request = {}", xUserId, itemRequestDto);
        return requestClient.create(xUserId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestByUser(
            @RequestHeader("X-Sharer-User-Id") long xUserId
    ) {
        log.info("Get ItemRequest with userId = {}", xUserId);
        return requestClient.getRequestByUser(xUserId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") long xUserId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get users with  from={}, size={}", from, size);
        return requestClient.getAllRequests(xUserId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") long xUserId,
            @PathVariable Long requestId
    ) {
        log.info("Get request with requestId={}", requestId);
        return requestClient.getRequestById(requestId);
    }


}
