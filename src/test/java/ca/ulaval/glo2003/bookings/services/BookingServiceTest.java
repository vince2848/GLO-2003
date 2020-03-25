package ca.ulaval.glo2003.bookings.services;

import static ca.ulaval.glo2003.beds.domain.helpers.BedBuilder.aBed;
import static ca.ulaval.glo2003.beds.domain.helpers.BedObjectMother.createBedNumber;
import static ca.ulaval.glo2003.bookings.domain.helpers.BookingBuilder.aBooking;
import static ca.ulaval.glo2003.bookings.domain.helpers.BookingObjectMother.*;
import static ca.ulaval.glo2003.bookings.rest.helpers.BookingRequestBuilder.aBookingRequest;
import static ca.ulaval.glo2003.bookings.rest.helpers.BookingResponseBuilder.aBookingResponse;
import static ca.ulaval.glo2003.bookings.rest.helpers.CancelationResponseBuilder.aCancelationResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import ca.ulaval.glo2003.beds.domain.Bed;
import ca.ulaval.glo2003.beds.services.BedService;
import ca.ulaval.glo2003.bookings.domain.Booking;
import ca.ulaval.glo2003.bookings.domain.BookingFactory;
import ca.ulaval.glo2003.bookings.domain.BookingTotalCalculator;
import ca.ulaval.glo2003.bookings.mappers.BookingMapper;
import ca.ulaval.glo2003.bookings.mappers.BookingNumberMapper;
import ca.ulaval.glo2003.bookings.rest.BookingRequest;
import ca.ulaval.glo2003.bookings.rest.BookingResponse;
import ca.ulaval.glo2003.bookings.rest.CancelationResponse;
import ca.ulaval.glo2003.transactions.domain.Price;
import ca.ulaval.glo2003.transactions.services.TransactionService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BookingServiceTest {

  private static BookingService bookingService;
  private static BedService bedService = mock(BedService.class);
  private static CancelationService cancelationService = mock(CancelationService.class);
  private static TransactionService transactionService = mock(TransactionService.class);
  private static BookingMapper bookingMapper = mock(BookingMapper.class);
  private static BookingFactory bookingFactory = mock(BookingFactory.class);
  private static BookingTotalCalculator bookingTotalCalculator = mock(BookingTotalCalculator.class);
  private static BookingNumberMapper bookingNumberMapper = mock(BookingNumberMapper.class);

  private static UUID bedNumber = createBedNumber();
  private static Bed bed = buildBed();
  private static UUID bookingNumber = createBookingNumber();
  private static Booking booking = buildBooking();
  private static BookingRequest bookingRequest = aBookingRequest().build();
  private static BookingResponse bookingResponse = aBookingResponse().build();
  private static CancelationResponse cancelationResponse = aCancelationResponse().build();
  private static Price total = createTotal();

  @BeforeAll
  public static void setUpService() {
    bookingService =
        new BookingService(
            bedService,
            cancelationService,
            transactionService,
            bookingMapper,
            bookingFactory,
            bookingTotalCalculator,
            bookingNumberMapper);
  }

  private void setUpMocksForAdd() {
    resetMocks();
    when(bedService.get(bedNumber.toString())).thenReturn(bed);
    when(bookingMapper.fromRequest(bookingRequest)).thenReturn(booking);
    when(bookingTotalCalculator.calculateTotal(bed, booking)).thenReturn(total);
    when(bookingFactory.create(booking, total)).thenReturn(booking);
  }

  private void setUpMocksForGetResponse() {
    resetMocks();
    bed.book(booking);
    when(bookingNumberMapper.fromString(bookingNumber.toString())).thenReturn(bookingNumber);
    when(bedService.get(bedNumber.toString())).thenReturn(bed);
    when(bookingMapper.toResponse(booking)).thenReturn(bookingResponse);
  }

  private void setUpMocksForCancel() {
    resetMocks();
    bed.book(booking);
    when(bookingNumberMapper.fromString(bookingNumber.toString())).thenReturn(bookingNumber);
    when(bedService.get(bedNumber.toString())).thenReturn(bed);
    when(cancelationService.cancel(booking, bed.getOwnerPublicKey().toString()))
        .thenReturn(cancelationResponse);
  }

  private void resetMocks() {
    bed = buildBed();
    booking = buildBooking();
    reset(bedService, transactionService, bookingFactory);
  }

  private static Bed buildBed() {
    return aBed().withBedNumber(bedNumber).build();
  }

  private static Booking buildBooking() {
    return aBooking()
        .withBookingNumber(bookingNumber)
        .withPackage(bed.getPricesPerNight().keySet().iterator().next())
        .build();
  }

  @Test
  public void add_shouldUpdateBedInRepository() {
    setUpMocksForAdd();

    bookingService.add(bedNumber.toString(), bookingRequest);

    verify(bedService).update(bed);
  }

  @Test
  public void add_shouldReturnBookingNumber() {
    setUpMocksForAdd();

    String actualBookingNumber = bookingService.add(bedNumber.toString(), bookingRequest);

    assertEquals(bookingNumber.toString(), actualBookingNumber);
  }

  @Test
  public void add_shouldBookToBed() {
    setUpMocksForAdd();

    bookingService.add(bedNumber.toString(), bookingRequest);
    List<Booking> bookings = bed.getBookings();

    assertTrue(bookings.contains(booking));
  }

  @Test
  public void add_shouldAddStayBookedTransaction() {
    setUpMocksForAdd();

    bookingService.add(bedNumber.toString(), bookingRequest);

    verify(transactionService).addStayBooked(booking.getTenantPublicKey().getValue(), total);
  }

  @Test
  public void add_shouldAddStayCompletedTransaction() {
    setUpMocksForAdd();

    bookingService.add(bedNumber.toString(), bookingRequest);

    verify(transactionService)
        .addStayCompleted(bed.getOwnerPublicKey().getValue(), total, booking.getNumberOfNights());
  }

  @Test
  public void getResponse_shouldGetBookingResponse() {
    setUpMocksForGetResponse();

    BookingResponse bookingResponse =
        bookingService.getResponse(bedNumber.toString(), bookingNumber.toString());

    assertEquals(bookingResponse, bookingResponse);
  }

  @Test
  public void cancel_shouldCancelBooking() {
    setUpMocksForCancel();

    CancelationResponse actualCancelationResponse =
        bookingService.cancel(bedNumber.toString(), bookingNumber.toString());

    assertEquals(cancelationResponse, actualCancelationResponse);
  }
}
