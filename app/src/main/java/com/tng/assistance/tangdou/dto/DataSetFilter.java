package com.tng.assistance.tangdou.dto;

import io.reactivex.rxjava3.functions.Predicate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public final class DataSetFilter<T> {
    private final Class<T> dataType;
    private final Predicate<T> filter;

    @SuppressWarnings("unchecked")
    public Predicate<Object> getFilter() {
        return (Predicate<Object>) filter;
    }
}
