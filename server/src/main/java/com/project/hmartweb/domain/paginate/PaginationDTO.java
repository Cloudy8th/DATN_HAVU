package com.project.hmartweb.domain.paginate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationDTO<T> {
    private List<T> data;

    private Pagination pagination;
}
