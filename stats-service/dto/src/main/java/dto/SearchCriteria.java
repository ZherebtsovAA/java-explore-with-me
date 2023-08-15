package dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class SearchCriteria {
    private LocalDateTime start;
    LocalDateTime end;
    List<String> uris;
    boolean unique;
}