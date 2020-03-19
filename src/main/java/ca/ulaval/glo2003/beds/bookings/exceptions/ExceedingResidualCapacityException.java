package ca.ulaval.glo2003.beds.bookings.exceptions;

public class ExceedingResidualCapacityException extends BookingException {

  public ExceedingResidualCapacityException() {
    super("EXCEEDING_RESIDUAL_CAPACITY");
  }
}
