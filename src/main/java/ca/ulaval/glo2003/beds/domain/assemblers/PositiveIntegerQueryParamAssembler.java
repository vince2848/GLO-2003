package ca.ulaval.glo2003.beds.domain.assemblers;

public abstract class PositiveIntegerQueryParamAssembler implements BedQueryParamAssembler {

  protected abstract void throwException();

  protected int parsePositiveInteger(String value) {
    int parsedValue = -1;

    try {
      parsedValue = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throwException();
    }

    if (parsedValue <= 0) {
      throwException();
    }

    return parsedValue;
  }
}
