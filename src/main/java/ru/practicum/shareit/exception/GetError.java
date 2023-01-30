package ru.practicum.shareit.exception;

public class GetError extends RuntimeException {
    private final String error;

    public GetError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
