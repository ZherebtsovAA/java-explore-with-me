package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.RequestParticipation;

import java.util.Collection;
import java.util.List;

public interface RequestParticipationRepository extends JpaRepository<RequestParticipation, Long> {
    @Query(value = "SELECT r.event AS eventId, COUNT(r.*) AS totalRequest " +
            "FROM requests r " +
            "WHERE r.status = :status AND r.event IN (:eventIds) " +
            "GROUP BY r.event " +
            "ORDER BY r.event ASC",
            nativeQuery = true)
    List<IConfirmedRequestsCount> countTotalRequestsByEventId(String status, Collection<Long> eventIds);

    List<RequestParticipation> findAllByEvent_Id(Long eventId);
}