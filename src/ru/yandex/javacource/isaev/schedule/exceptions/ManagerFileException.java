package ru.yandex.javacource.isaev.schedule.exceptions;

public class ManagerFileException extends RuntimeException {
    private static final String LOAD_MESSAGE = "Ошибка чтения файла!";
    private static final String SAVE_MESSAGE = "Ошибка при сохранении файла!";

    public ManagerFileException(String message, Exception e) {
        super(message, e);
    }

    public static ManagerFileException saveExceptions(Exception e) {
        return new ManagerFileException(SAVE_MESSAGE, e);
    }

    public static ManagerFileException loadExceptions(Exception e) {
        return new ManagerFileException(LOAD_MESSAGE, e);
    }

}
