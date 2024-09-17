package ru.yandex.javacource.isaev.schedule.exceptions;

public class NotFoundException extends NullPointerException {
    public NotFoundException(String message) {
        super(message);
    }
}