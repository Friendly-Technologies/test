package com.friendly.web.client.util;

import com.google.common.base.Joiner;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builds a query string for the specified operation for filter
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class QueryModifierBuilder {

    final private static Joiner JOINER = Joiner.on(',').skipNulls();
    final private StringBuilder builder = new StringBuilder();

    /**
     * Filter by a property
     *
     * @param property property to filter on
     * @param value    value to filter on
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterOnProperty(final String property,
                                                       final String value) {
        addAmpIfBuilderIsNotEmpty();
        builder.append(property)
               .append('=')
               .append(value);
        return this;
    }

    /**
     * Filter by several property
     *
     * @param values value to filter on
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterInProperty(final String property,
                                                       final List<String> values) {
        addAmpIfBuilderIsNotEmpty();
        appendFilterOnProperty(property, "in");
        for (final String value : values) {
            builder.append(',')
                   .append(value);
        }
        return this;
    }

    /**
     * Filter by a property
     *
     * @param keyValues keys and their values to filter on
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterAndProperty(final Map<String, String> keyValues) {
        addAmpIfBuilderIsNotEmpty();
        final Iterator<String> mapIterator = keyValues.keySet().iterator();

        while (mapIterator.hasNext()) {
            final String key = mapIterator.next();
            appendFilterOnProperty(key, keyValues.get(key));
            if (mapIterator.hasNext()) {
                builder.append('&');
            }
        }
        return this;
    }

    /**
     * Filter against a property
     *
     * @param property property to filter on
     * @param value    value to filter out
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterNotProperty(final String property,
                                                        final String value) {
        appendFilterOnProperty(property, "ne");
        builder.append(',')
               .append(value);
        return this;

    }

    /**
     * Filter by a property using pattern matching
     *
     * @param property property to filter on
     * @param value    value to filter on
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterLikeProperty(final String property,
                                                         final String value) {

        appendFilterOnProperty(property, "like");
        builder.append(',')
               .append(value);
        return this;
    }

    /**
     * Filter by a property containing a value
     *
     * @param property property to filter on
     * @param value    value to filter on
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterContainsProperty(final String property,
                                                             final String value) {

        appendFilterOnProperty(property, "contains");
        builder.append(',')
               .append(value);
        return this;


    }


    /**
     * Filter by a nested property
     *
     * @param parentProperty parent property to filter on
     * @param childProperty  child property to filter on
     * @param value          value to filter on
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterNestedProperty(final String parentProperty,
                                                           final String childProperty,
                                                           final String value) {

        appendFilterOnProperty(parentProperty + '.' + childProperty, value);
        return this;
    }


    /**
     * Limit responses to value of limit
     *
     * @param limit the limit of entities to return
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendLimit(final int limit) {
        appendFilterOnProperty("_limit", Integer.toString(limit));
        return this;
    }

    /**
     * Filter by a range for a property
     *
     * @param rangeOperator the type of range to filter on
     * @param property      property to filter on
     * @param value         value to filter on
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFilterRangeProperty(final RangeOperator rangeOperator,
                                                          final String property,
                                                          final String value) {

        appendFilterOnProperty(property, rangeOperator.toString());
        builder.append(',')
               .append(value);
        return this;
    }

    /**
     * Sorts based on the field and in the specified order
     *
     * @param sortField the field to sort on
     * @param order     the order to sort in
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendSortProperty(final String sortField,
                                                   final SortOrder order) {

        appendSortProperty(sortField, order.toString());
        return this;
    }

    /**
     * Sorts based on the field and in the specified order
     *
     * @param sortFields the fields to sort on
     * @param orders     the orders to sort in
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendSortProperty(final List<String> sortFields,
                                                   final List<SortOrder> orders) {

        appendSortProperty(JOINER.join(sortFields), JOINER.join(orders));
        return this;
    }

    /**
     * Specifies what fields to return
     *
     * @param fields the fields to return
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendFields(final List<String> fields) {
        addAmpIfBuilderIsNotEmpty();
        builder.append("_fields=")
               .append(JOINER.join(fields));
        return this;
    }

    /**
     * Appends the page to the query filter
     *
     * @param page page to return
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendPage(final int page) {
        addAmpIfBuilderIsNotEmpty();
        builder.append("_page=")
               .append(page);
        return this;
    }

    /**
     * Appends the page to the query filter
     *
     * @param page     page to return
     * @param pageSize the size of the page to return
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendPage(final int page,
                                           final int pageSize) {
        addAmpIfBuilderIsNotEmpty();
        builder.append("_page=")
               .append(page)
               .append("&_limit=")
               .append(pageSize);
        return this;
    }

    /**
     * Appends the slice parameter to the query
     *
     * @param start where to start the slice
     * @param end   where to end the slice
     * @return StringBuilder with the additional slice
     */
    public QueryModifierBuilder appendSlice(final int start,
                                            final int end) {
        addAmpIfBuilderIsNotEmpty();
        builder.append("_start=")
               .append(start)
               .append("&_end=")
               .append(end);
        return this;
    }

    /**
     * Appends the embedded request
     *
     * @param entity the entity to embed
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendEmbeded(final String entity) {
        addAmpIfBuilderIsNotEmpty();
        builder.append("_embed=")
               .append(entity);
        return this;
    }

    /**
     * Appends the expanded request
     *
     * @param entity the entity to expand
     * @return {@link QueryModifierBuilder} this instance
     */
    public QueryModifierBuilder appendExpanded(final String entity) {
        addAmpIfBuilderIsNotEmpty();
        builder.append("_expand=")
               .append(entity);
        return this;
    }

    public QueryModifierBuilder appendCursorWithLimit(final String cursor, final int limit) {
        appendCursor(cursor);
        addAmpIfBuilderIsNotEmpty();
        builder.append("_limit=")
               .append(limit);
        return this;
    }

    public QueryModifierBuilder appendCursor(final String cursor) {
        if (Objects.nonNull(cursor)) {
            addAmpIfBuilderIsNotEmpty();
            builder.append("_cursor=")
                   .append(cursor);
        }
        return this;
    }

    private StringBuilder appendSortProperty(final String sort,
                                             final String order) {
        addAmpIfBuilderIsNotEmpty();
        return builder.append("_sort=")
                      .append(sort)
                      .append("&_order=")
                      .append(order);
    }

    private void addAmpIfBuilderIsNotEmpty() {
        if (!(builder.toString().isEmpty())) {
            builder.append('&');
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
