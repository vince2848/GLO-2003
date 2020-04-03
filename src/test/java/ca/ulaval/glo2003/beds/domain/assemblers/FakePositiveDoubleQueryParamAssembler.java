package ca.ulaval.glo2003.beds.domain.assemblers;

import ca.ulaval.glo2003.beds.domain.BedQueryBuilder;
import ca.ulaval.glo2003.errors.exceptions.TestingException;
import java.util.List;
import java.util.Map;

public class FakePositiveDoubleQueryParamAssembler extends PositiveIntegerQueryParamAssembler {

  public static final String POSITIVE_DOUBLE_PARAM = "positiveDouble";

  @Override
  protected void throwException() {
    throw new TestingException();
  }

  @Override
  public BedQueryBuilder assemble(BedQueryBuilder builder, Map<String, List<String>> params) {
    parsePositiveInteger(params.get(POSITIVE_DOUBLE_PARAM).get(0));
    return builder;
  }
}
