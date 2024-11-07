package fr.insee.protools.backend.restclient.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponse<T> {
    @Builder.Default
    private List<T> content = new ArrayList<>();
    @Builder.Default
    private Integer currentPage = 0;
    @Builder.Default
    private Integer pageSize = 5000;
    @Builder.Default
    private Long totalElements = 0L;
    @Builder.Default
    private Integer pageCount = 0;

    public Boolean isLastPage() {
        if (currentPage == null || pageCount == null) {
            return true;
        } else return ((currentPage + 1) >= pageCount);
    }

}

