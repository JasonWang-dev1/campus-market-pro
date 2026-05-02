package com.campusmarket.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应体
 */
@Data
public class PageResult<T> implements Serializable {

    private long page;
    private long size;
    private long total;
    private List<T> records;

    public static <T> PageResult<T> of(long page, long size, long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setPage(page);
        result.setSize(size);
        result.setTotal(total);
        result.setRecords(records);
        return result;
    }
}
