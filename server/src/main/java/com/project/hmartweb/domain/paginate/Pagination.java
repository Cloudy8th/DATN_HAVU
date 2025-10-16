package com.project.hmartweb.domain.paginate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pagination {
    private Integer page;

    private Integer perPage;

    private Integer lastPage;

    private Long total;
}
