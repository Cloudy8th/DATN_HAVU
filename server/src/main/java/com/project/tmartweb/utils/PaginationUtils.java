package com.project.tmartweb.utils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationUtils {

    public Sort.Order orderBy(String field, String dir, Sort.Direction defaultDir) {
        Sort.Direction d = (dir == null)
                ? defaultDir
                : ("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC);
        return new Sort.Order(d, field);
    }

    public Sort.Direction parseDir(String s) {
        if (s == null) return null;
        if ("asc".equalsIgnoreCase(s)) return Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(s)) return Sort.Direction.DESC;
        return null; // invalid -> bỏ qua
    }

    public void addOrderIfPresent(List<Sort.Order> orders, String field, String dirStr) {
        Sort.Direction d = parseDir(dirStr);
        if (d != null) orders.add(new Sort.Order(d, field));
    }
}
