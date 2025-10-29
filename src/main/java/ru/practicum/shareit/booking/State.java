package ru.practicum.shareit.booking;

import ru.practicum.shareit.exceptions.ValidationException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State from(String value) {
        try {
            return State.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("не известный тип: " + value);
        }
    }
}

