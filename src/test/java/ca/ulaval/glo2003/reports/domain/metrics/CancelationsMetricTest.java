package ca.ulaval.glo2003.reports.domain.metrics;

import static ca.ulaval.glo2003.time.domain.helpers.TimestampObjectMother.createTimestamp;
import static ca.ulaval.glo2003.transactions.domain.helpers.TransactionBuilder.aTransaction;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo2003.time.domain.Timestamp;
import ca.ulaval.glo2003.transactions.domain.Transaction;
import ca.ulaval.glo2003.transactions.domain.TransactionReasons;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CancelationsMetricTest extends ReportMetricTest {

  private static Timestamp cancelationTimestamp = createTimestamp();
  private static Timestamp otherCancelationTimestamp = createTimestamp();

  private static Transaction reservationTransaction =
      aTransaction().withReason(TransactionReasons.STAY_BOOKED).build();
  private static Transaction cancelationTransaction =
      aTransaction()
          .withReason(TransactionReasons.STAY_CANCELED)
          .withTimestamp(cancelationTimestamp)
          .build();
  private static Transaction sameCancelationTransaction =
      aTransaction()
          .withReason(TransactionReasons.STAY_CANCELED)
          .withTimestamp(cancelationTimestamp)
          .build();
  private static Transaction otherCancelationTransaction =
      aTransaction()
          .withReason(TransactionReasons.STAY_CANCELED)
          .withTimestamp(otherCancelationTimestamp)
          .build();
  private static Transaction refundTransaction =
      aTransaction().withReason(TransactionReasons.STAY_CANCELED).forRefund().build();

  @Override
  protected ReportMetrics metricName() {
    return ReportMetrics.CANCELATIONS;
  }

  @BeforeAll
  public static void setUpMetric() {
    metric = new CancelationsMetric();
  }

  private static void setUpDataWithoutCancelation() {
    setUpData(Arrays.asList(reservationTransaction, refundTransaction));
  }

  private static void setUpDataWithSingleCancelation() {
    setUpData(Collections.singletonList(cancelationTransaction));
  }

  private static void setUpDataWithMultipleCancelations() {
    setUpData(Arrays.asList(cancelationTransaction, otherCancelationTransaction));
  }

  private static void setUpDataWithMultiPartedCancelation() {
    setUpData(Arrays.asList(cancelationTransaction, sameCancelationTransaction));
  }

  @Test
  public void calculate_withoutCancelation_shouldCalculateZero() {
    setUpDataWithoutCancelation();

    metric.calculate(data);

    assertEquals(0, data.getMetrics().get(0).getValue());
  }

  @Test
  public void calculate_withSingleCancelation_shouldCalculateOne() {
    setUpDataWithSingleCancelation();

    metric.calculate(data);

    assertEquals(1, data.getMetrics().get(0).getValue());
  }

  @Test
  public void calculate_withMultipleCancelations_shouldCalculateNumberOfCancelations() {
    setUpDataWithMultipleCancelations();

    metric.calculate(data);

    assertEquals(2, data.getMetrics().get(0).getValue());
  }

  @Test
  public void calculate_withMultiPartedCancelation_shouldCalculateOne() {
    setUpDataWithMultiPartedCancelation();

    metric.calculate(data);

    assertEquals(1, data.getMetrics().get(0).getValue());
  }
}
