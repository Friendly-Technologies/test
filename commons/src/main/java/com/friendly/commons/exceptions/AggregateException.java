package com.friendly.commons.exceptions;

import com.friendly.commons.errors.ErrorApi;
import com.google.common.base.Joiner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Defines error which aggregates information about exceptions which can occur simultaneously in parallel threads
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class AggregateException extends BaseFriendlyException {

    private final List<BaseFriendlyException> innerExceptions;

    public AggregateException(final List<BaseFriendlyException> innerExceptions) {
        super(
                String.format(
                    "The following errors occur simultaneously: %s",
                    Joiner.on("\r\n\t")
                          .join(IntStream.range(0, innerExceptions.size())
                                         .mapToObj(i -> String.format(
                                                 "%d. %s",
                                                 i + 1,
                                                 innerExceptions.get(i).toString()))
                                         .collect(Collectors.toList()))),
                ErrorApi.UNKNOWN_ERROR_CODE);
        this.innerExceptions = innerExceptions;
    }

    public List<BaseFriendlyException> getInnerExceptions() {
        return innerExceptions;
    }
}
