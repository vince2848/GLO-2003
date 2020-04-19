package ca.ulaval.glo2003.reports.domain.dimensions;

import static ca.ulaval.glo2003.beds.domain.helpers.BedBuilder.aBed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo2003.beds.domain.Bed;
import ca.ulaval.glo2003.beds.domain.LodgingModes;
import ca.ulaval.glo2003.reports.domain.ReportPeriodData;
import ca.ulaval.glo2003.transactions.domain.Transaction;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class LodgingModeDimensionTest extends ReportDimensionTest {

  private static Transaction privateTransaction = mock(Transaction.class);
  private static Transaction cohabitationTransaction = mock(Transaction.class);

  @BeforeAll
  public static void setUpDimension() {
    dimension = new LodgingModeDimension();
    setUpTransactions();
  }

  private static void setUpTransactions() {
    Bed privateBed = aBed().withLodgingModeType(LodgingModes.PRIVATE).build();
    Bed cohabitationBed = aBed().withLodgingModeType(LodgingModes.COHABITATION).build();
    when(privateTransaction.getBed()).thenReturn(privateBed);
    when(cohabitationTransaction.getBed()).thenReturn(cohabitationBed);
  }

  @Override
  protected List<Transaction> buildTransactions() {
    return Arrays.asList(privateTransaction, cohabitationTransaction);
  }

  @Override
  protected int numberOfValues() {
    return LodgingModes.values().length;
  }

  @ParameterizedTest
  @EnumSource(LodgingModes.class)
  public void splitAll_shouldSplitWithLodgingModeValues(LodgingModes lodgingMode) {
    List<ReportPeriodData> splitData = dimension.splitAll(singleData);

    assertTrue(
        splitData.stream()
            .anyMatch(data -> data.getDimensions().get(0).getValue().equals(lodgingMode)));
  }

  @Test
  public void splitAll_shouldSplitWithLodgingModeDimensionName() {
    List<ReportPeriodData> splitData = dimension.splitAll(singleData);

    assertTrue(
        splitData.stream()
            .allMatch(
                data ->
                    data.getDimensions().get(0).getName().equals(ReportDimensions.LODGING_MODE)));
  }

  @ParameterizedTest
  @MethodSource("provideLodgingModeTransactions")
  public void splitAll_withSingleTransactionPerPackage_shouldSplitTransactionsByPackage(
      LodgingModes value, Transaction transaction) {
    List<ReportPeriodData> splitData = dimension.splitAll(singleData);
    List<ReportPeriodData> filteredData = filterData(splitData, value);

    assertEquals(1, filteredData.size());
    assertEquals(1, filteredData.get(0).getTransactions().size());
    assertEquals(transaction, filteredData.get(0).getTransactions().get(0));
  }

  private static Stream<Arguments> provideLodgingModeTransactions() {
    return Stream.of(
        Arguments.of(LodgingModes.PRIVATE, privateTransaction),
        Arguments.of(LodgingModes.COHABITATION, cohabitationTransaction));
  }

  private List<ReportPeriodData> filterData(List<ReportPeriodData> data, LodgingModes value) {
    return data.stream()
        .filter(datum -> datum.getDimensions().get(0).getValue().equals(value))
        .collect(Collectors.toList());
  }
}
