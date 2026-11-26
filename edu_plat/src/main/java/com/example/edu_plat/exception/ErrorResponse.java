package com.example.edu_plat.exception;

public class ErrorResponse {
    private int status;
    private String message;

    // Пустой конструктор
    public ErrorResponse() {
    }

    // Тот самый конструктор, которого не хватало
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // Геттеры и сеттеры
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}