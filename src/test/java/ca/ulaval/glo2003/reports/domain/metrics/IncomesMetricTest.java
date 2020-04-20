package ca.ulaval.glo2003.reports.domain.metrics;

import static ca.ulaval.glo2003.reports.domain.helpers.ReportPeriodDataBuilder.aReportPeriodData;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo2003.transactions.domain.Price;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class IncomesMetricTest extends ReportMetricTest {

  @Override
  protected ReportMetrics metricName() {
    return ReportMetrics.INCOMES;
  }

  @BeforeAll
  public static void setUpMetric() {
    metric = new IncomesMetric();
  }

  @ParameterizedTest
  @MethodSource("provideIncomes")
  public void calculate_withIncomes_shouldCalculateIncomes(
      int numberOfReservations, Price incomes, Price sumIncomes) {
    data =
        aReportPeriodData()
            .withIncomes(incomes)
            .withANumberOfReservations(numberOfReservations)
            .build();

    metric.calculate(data);

    assertEquals(sumIncomes, data.getMetrics().get(0).getValue());
  }

  @ParameterizedTest
  @MethodSource("provideIncomes")
  public void calculate_withIncomesAndCancelations_shouldCalculateIncomes(
      int numberOfType, Price incomes, Price sumIncomes) {
    data =
        aReportPeriodData()
            .withIncomes(incomes)
            .withANumberOfReservations(numberOfType)
            .withANumberOfCancelations(numberOfType)
            .build();

    metric.calculate(data);

    assertEquals(sumIncomes, data.getMetrics().get(0).getValue());
  }

  private static Stream<Arguments> provideIncomes() {
    return Stream.of(
        Arguments.of(0, Price.zero(), Price.zero()),
        Arguments.of(3, new Price(123.123), new Price(369.369)),
        Arguments.of(5, new Price(49.99), new Price(249.95)));
  }

  @Test
  public void calculate_withoutEvent_shouldCalculateZero() {
    metric.calculate(data);

    assertEquals(Price.zero(), data.getMetrics().get(0).getValue());
  }
}
