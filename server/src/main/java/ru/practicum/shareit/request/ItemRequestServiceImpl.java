package ru.practicum.shareit.request;


import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemShortDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository requestRepository;
    ItemRequestMapper requestMapper;
    UserValidator userValidator;
    UserRepository userRepository;
    ItemRepository itemRepository;
    ItemMapper itemMapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, ItemRequestMapper requestMapper,
                                  UserRepository userRepository, UserValidator userValidator,
                                  ItemRepository itemRepository, ItemMapper itemMapper) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) {
        log.info("создание запроса в ItemRequestService {}", itemRequestDto);
        User user = getUserOrThrow(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto, user);
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest),
                getItemsByRequest(itemRequest.getId()));
    }

    @Override
    public List<ItemRequestDto> getRequestsByUser(long userId) {
        log.info("вызван метод getRequestsByUser в ItemRequestService");
        userValidator.isUserExists(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(userId);

        List<Long> requestsId = requests.stream().map(ItemRequest::getId).toList();

        Map<Long, List<ItemShortDto>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestsId).stream()
                .map(itemMapper::toItemShortDto)
                .collect(Collectors.groupingBy(ItemShortDto::getRequestId));



        return requests.stream()
                .map(ir -> requestMapper.toItemRequestDto(ir, itemsByRequest.get(ir.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int from, int size) {
        log.info("вызван метод getAllRequests в ItemRequestService");

        List<ItemRequest> requests = requestRepository.findAllRequests(from, size);

        List<Long> requestsId = requests.stream().map(ItemRequest::getId).toList();

        Map<Long, List<ItemShortDto>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestsId).stream()
                .map(itemMapper::toItemShortDto)
                .collect(Collectors.groupingBy(ItemShortDto::getRequestId));


        return requests.stream()
                .map(ir -> requestMapper.toItemRequestDto(ir, itemsByRequest.get(ir.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(long requestId) {
        log.info("вызван метод getRequestById в ItemRequestService");
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("запрос с введенным id = " + requestId + " не найден")
        );

        return requestMapper.toItemRequestDto(request, getItemsByRequest(request.getId()));
    }

    private User getUserOrThrow(long userId) {
        log.info("вызван метод getUserOrThrow в ItemRequestService");
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("пользователь с введенным id = " + userId + " не найден")
        );
    }

    private List<ItemShortDto> getItemsByRequest(long requestId) {
        log.info("вызван метод getItemsByRequest в ItemRequestService");
        return itemRepository.findAllByRequestId(requestId).stream()
                .map(itemMapper::toItemShortDto)
                .collect(Collectors.toList());
    }
}
