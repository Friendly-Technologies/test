package com.friendly.services.infrastructure.utils;

import com.friendly.commons.models.FTPageDetails;
import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.OrderDirection;
import com.friendly.services.device.info.utils.SortColumn;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PageUtils {

    public static List<Pageable> createPageRequest(final List<Integer> pages, final Integer size,
                                                   final List<FieldSort> sorts, final String defaultSortField) {
        final int pageSize = size != null && size > 0 ? size : Integer.MAX_VALUE;
        final Sort finalSort = getOrders(sorts, defaultSortField);

        return buildPageable(pages, pageSize, finalSort);
    }

    public static Sort getOrders(final List<FieldSort> sorts, final String defaultSortField) {
        final boolean sortsNotEmpty = sorts != null && !sorts.isEmpty();
        final Sort sort = Sort.by(sortsNotEmpty ? Sort.Direction.valueOf(sorts.get(0).getDirection().name())
                                          : Sort.Direction.DESC,
                                  sortsNotEmpty ? sorts.get(0).getField() : defaultSortField);
        return sortsNotEmpty ?
                sorts.stream()
                     .map(s -> Sort.by(Sort.Direction.valueOf(s.getDirection().name()), mapColumn(s.getField())))
                     .reduce(Sort::and)
                     .orElse(sort)
                : sort;
    }

    private static String mapColumn(String field) {
        if ("protocolType".equals(field)) {
            return "protocolId";
        }
        return field;
    }

    public static List<Pageable> buildPageable(final List<Integer> pages, final int pageSize,
                                               final Sort finalSort) {
        if (pages == null) {
            return Collections.singletonList(PageRequest.of(0, pageSize, finalSort));
        }
        return pages.stream()
                    .map(page -> PageRequest.of(page != null && page > 1 ? page - 1 : 0, pageSize, finalSort))
                    .collect(Collectors.toList());
    }

    public static <T> FTPageDetails buildPageDetails(List<Page<T>> pageList) {
        return FTPageDetails.builder()
                          .pageItems(pageList.stream()
                                             .mapToInt(Page::getNumberOfElements)
                                             .sum())
                          .totalPages(pageList.stream()
                                              .mapToInt(Page::getTotalPages)
                                              .findAny().orElse(0))
                          .totalItems(pageList.stream()
                                              .mapToLong(Page::getTotalElements)
                                              .findAny().orElse(0))
                          .build();
    }

    public static List<Pageable> createPageRequest(final List<Integer> pages,
                                                    final Integer size,
                                                    final List<FieldSort> sorts,
                                                    final List<ColumnEntity> columns) {
        final int pageSize = size != null && size > 0 ? size : Integer.MAX_VALUE;
        final Sort interfaceSort = PageUtils.getOrders(interfaceSortToSort(sorts), "id");
        final Optional<Sort> viewSorts = columns.stream()
                .filter(c -> c.getOrderIndex() != null)
                .sorted(Comparator.nullsLast(Comparator.comparing(
                        ColumnEntity::getOrderIndex,
                        Comparator.nullsLast(Comparator.naturalOrder()))))
                .map(PageUtils::columnToSort)
                .reduce(Sort::and);

        if (sorts == null || sorts.isEmpty()) {
            return PageUtils.buildPageable(pages, pageSize, viewSorts.orElse(interfaceSort));
        } else {
            return PageUtils.buildPageable(pages, pageSize, viewSorts.map(interfaceSort::and)
                    .orElse(interfaceSort));
        }

    }

    private static Sort columnToSort(final ColumnEntity column) {
        return Sort.by(column.getDirection().equals(OrderDirection.ASC)
                        ? Sort.Direction.ASC : Sort.Direction.DESC,
                SortColumn.getSortValue(column.getColumnKey()));
    }

    private static List<FieldSort> interfaceSortToSort(final List<FieldSort> sorts) {
        if (sorts == null) {
            return null;
        }
        return sorts.stream()
                .map(s -> s.toBuilder()
                        .field(SortColumn.getSortValue(s.getField()))
                        .build())
                .collect(Collectors.toList());
    }
}
