package com.friendly.services.infrastructure.base;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateXmlAdapter extends XmlAdapter<String, Date> {
    /**
     * Thread safe {@link DateFormat} using {@link SimpleDateFormat}.
     */
    private static final ThreadLocal<DateFormat> DATE_FORMAT_TL = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
    private static final String UTC = "UTC";

    /**
     * Converts dateString to {@link Date}
     *
     * @param dateString {@link String}
     * @return instance of {@link Date}
     * @throws ParseException if the dateString does not exist or corrupted
     */
    @Override
    public Date unmarshal(final String dateString) throws ParseException {
        final DateFormat dateFormat = DATE_FORMAT_TL.get();
        dateFormat.setTimeZone(TimeZone.getTimeZone(UTC));

        return dateFormat.parse(dateString);
    }

    /**
     * Formats {@link Date} to {@link String} using {@link SimpleDateFormat}
     *
     * @param date instance of {@link Date}
     * @return respectively formatted plain string from Date object
     */
    @Override
    public String marshal(final Date date) {
        final DateFormat dateFormat = DATE_FORMAT_TL.get();
        dateFormat.setTimeZone(TimeZone.getTimeZone(UTC));

        return dateFormat.format(date);
    }
}
