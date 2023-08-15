package dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class SearchCriteria {
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;
}