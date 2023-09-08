package ru.practicum.request.repository;

public interface IConfirmedRequestsCount {
    Long getEventId();

    Integer getTotalRequest();
}