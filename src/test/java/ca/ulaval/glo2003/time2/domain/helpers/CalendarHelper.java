package ca.ulaval.glo2003.time2.domain.helpers;

import com.github.javafaker.Faker;
import java.util.Calendar;

public class CalendarHelper {

  public static int validMonthOfQuarter(int quarter) {
    return Faker.instance()
        .random()
        .nextInt(firstMonthOfQuarter(quarter), lastMonthOfQuarter(quarter));
  }

  private static int firstMonthOfQuarter(int quarter) {
    return (quarter * 3) - 3;
  }

  private static int lastMonthOfQuarter(int quarter) {
    return (quarter * 3) - 1;
  }

  public static int validDayOfMonth(int year, int month) {
    return Faker.instance()
        .random()
        .nextInt(firstDayOfMonth(year, month), lastDayOfMonth(year, month));
  }

  public static int firstDayOfMonth(int year, int month) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    return calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
  }

  public static int lastDayOfMonth(int year, int month) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
  }
}
