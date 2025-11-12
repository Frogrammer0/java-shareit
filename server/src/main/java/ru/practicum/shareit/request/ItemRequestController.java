package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.requestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemRequestDto requestDto) {
        log.info("create in ItemRequestController");
        return requestService.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("getRequestsByUser in ItemRequestController");
        return requestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get users with  from={}, size={}", from, size);
        return requestService.getAllRequests(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @PathVariable Long requestId
    ) {
        log.info("Get request with requestId={}", requestId);
        return requestService.getRequestById(requestId);
    }
}
