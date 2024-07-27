package ru.yandex.javacource.isaev.schedule.exceptions;

public class ManagerLoadException extends RuntimeException {

    public ManagerLoadException(String message, Exception e) {
        super(message, e);
    }
}
