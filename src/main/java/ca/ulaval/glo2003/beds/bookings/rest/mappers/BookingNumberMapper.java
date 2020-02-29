package ca.ulaval.glo2003.beds.bookings.rest.mappers;

import ca.ulaval.glo2003.beds.bookings.rest.exceptions.BookingNotFoundException;
import java.util.UUID;

public class BookingNumberMapper {

  public UUID fromString(String number) {
    try {
      return UUID.fromString(number);
    } catch (IllegalArgumentException exception) {
      throw new BookingNotFoundException(number);
    }
  }
}