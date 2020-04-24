package ca.ulaval.glo2003.time2.domain;

import java.time.ZonedDateTime;
import java.util.Calendar;

public class TimeQuarter extends TimeCalendar {

  TimePeriod period;
  int firstMonthOfQuarter;
  int lastMonthOfQuarter;

  public TimeQuarter(ZonedDateTime zonedDateTime) {
    super(zonedDateTime);
    calendar.set(Calendar.YEAR, getYear());
    firstMonthOfQuarter = (getMonth() / 3) * 3;
    lastMonthOfQuarter = firstMonthOfQuarter + 2;
    period = new TimePeriod(firstDate(), lastDate());
  }

  public TimePeriod toPeriod() {
    return period;
  }

  private TimeDate firstDate() {
    calendar.set(Calendar.MONTH, firstMonthOfQuarter);
    int firstDayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
    return thatDate(firstMonthOfQuarter, firstDayOfMonth);
  }

  private TimeDate lastDate() {
    calendar.set(Calendar.MONTH, lastMonthOfQuarter);
    int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    return thatDate(lastMonthOfQuarter, lastDayOfMonth);
  }

  private TimeDate thatDate(int month, int dayOfMonth) {
    Calendar date = Calendar.getInstance();
    date.set(Calendar.YEAR, getYear());
    date.set(Calendar.MONTH, month);
    date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    setAtMidnight(date);
    return new TimeDate(date.getTime());
  }

  private int getQuarter() {
    return (firstMonthOfQuarter / 3) + 1;
  }

  @Override
  public String toString() {
    return "q".concat(Integer.toString(getQuarter()));
  }

  @Override
  public int compareTo(TimeCalendar other) {
    return getYearQuarter() - other.getYearQuarter();
  }

  // TODO : Test equals and hashCode (can't TimeCalendar do that?)
  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;

    TimeCalendar other = (TimeCalendar) object;

    return getYearQuarter() == other.getYearQuarter();
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(getYearQuarter());
  }
}
