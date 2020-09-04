package com.yangzhou.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

  public static Date getDate() {
    final Date date = new Date();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    final String workDate = sdf.format(date);
    Date date1 = null;
    try {
      date1 = sdf.parse(workDate);
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return date1;
  }

  public static LocalDate getDate(String dateStr) {
    return LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE);
  }

  public static LocalDate dateAfterToday(int days) {
    return LocalDate.now().plusDays(days);
  }

  public static LocalDateTime startTimeOfDate(LocalDateTime date) {
    return date.toLocalDate().atStartOfDay();
  }

  public static LocalDateTime startTimeOfMonth(LocalDateTime date) {
    date = date.withDayOfMonth(1);
    return date.toLocalDate().atStartOfDay();
  }
}
