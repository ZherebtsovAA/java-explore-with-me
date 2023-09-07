package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.RequestParticipation;
import ru.practicum.request.model.RequestParticipationState;

import java.util.List;

public interface RequestParticipationRepository extends JpaRepository<RequestParticipation, Long> {
    @Query("SELECT rp.event AS eventId, COUNT(rp.id) AS totalRequest " +
            "FROM RequestParticipation AS rp " +
            "WHERE rp.status = :status AND rp.event IN (:events) " +
            "GROUP BY rp.event " +
            "ORDER BY rp.event ASC")
    List<IConfirmedRequests> findByStatusAndEventIn(RequestParticipationState status, List<Event> events);

    List<RequestParticipation> findAllByEvent_Id(Long eventId);


/*    @Query("SELECT rp " +
            "FROM RequestParticipation rp " +
            "WHERE rp.requester = :requester AND rp.event.initiator != :requester")
    List<RequestParticipation> getRequestParticipationCurrentUser(@Param("requester") User requester);


    @Query(value = "" +
            "SELECT * " +
            "FROM requests " +
            "WHERE requester = :userId",
            nativeQuery = true)
    List<RequestParticipation> getRequestParticipationCurrentUser(@Param("userId") Long userId);*/

}