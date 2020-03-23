package ca.ulaval.glo2003.beds.infrastructure;

import ca.ulaval.glo2003.beds.domain.*;
import ca.ulaval.glo2003.beds.infrastructure.filters.*;
import ca.ulaval.glo2003.bookings.domain.BookingDate;
import ca.ulaval.glo2003.locations.domain.Location;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InMemoryBedQueryBuilder implements BedQueryBuilder {

  private List<InMemoryBedFilter> filters = new ArrayList<>();

  private static final int UNSET_INT = 0;
  private static final int DEFAULT_NUMBER_OF_NIGHTS = 3;
  private static final int DEFAULT_MAX_DISTANCE = 10;

  private int minCapacity = UNSET_INT;
  private BookingDate arrivalDate = null;
  private int numberOfNights = UNSET_INT;
  private Location origin = null;
  private int maxDistance = UNSET_INT;

  @Override
  public BedQueryBuilder aBedQuery() {
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withBedType(BedTypes bedType) {
    this.filters.add(new InMemoryBedTypeFilter(bedType));
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withCleaningFrequency(CleaningFrequencies cleaningFrequency) {
    this.filters.add(new InMemoryCleaningFrequencyFilter(cleaningFrequency));
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withBloodTypes(List<BloodTypes> bloodTypes) {
    this.filters.add(new InMemoryBloodTypesFilter(bloodTypes));
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withPackage(Packages packageName) {
    this.filters.add(new InMemoryPackageFilter(packageName));
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withMinCapacity(int minCapacity) {
    this.minCapacity = minCapacity;
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withArrivalDate(BookingDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withNumberOfNights(int numberOfNights) {
    this.numberOfNights = numberOfNights;
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withLodgingMode(LodgingModes lodgingMode) {
    this.filters.add(new InMemoryLodgingModeFilter(lodgingMode));
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withOrigin(Location origin) {
    this.origin = origin;
    return this;
  }

  @Override
  public InMemoryBedQueryBuilder withMaxDistance(int maxDistance) {
    this.maxDistance = maxDistance;
    return this;
  }

  @Override
  public InMemoryBedQuery build() {
    addAvailabilityFilter();
    addDistanceFilter();
    return new InMemoryBedQuery(filters);
  }

  private void addAvailabilityFilter() {
    if (minCapacity != UNSET_INT) {
      filters.add(
          new InMemoryAvailabilityFilter(minCapacity, getArrivalDate(), getNumberOfNights()));
    } else {
      if (arrivalDate != null) throw new RuntimeException(); // TODO : Throw correct exception

      if (numberOfNights != UNSET_INT)
        throw new RuntimeException(); // TODO : Throw correct exception
    }
  }

  private void addDistanceFilter() {
    if (origin != null) {
      filters.add(new InMemoryDistanceFilter(origin, getMaxDistance()));
    } else {
      if (maxDistance != UNSET_INT) throw new RuntimeException(); // TODO : Throw correct exception
    }
  }

  // TODO : Use default constructor
  private BookingDate getArrivalDate() {
    return arrivalDate == null ? new BookingDate(LocalDate.now()) : arrivalDate;
  }

  private int getNumberOfNights() {
    return numberOfNights == UNSET_INT ? DEFAULT_NUMBER_OF_NIGHTS : numberOfNights;
  }

  private int getMaxDistance() {
    return maxDistance == UNSET_INT ? DEFAULT_MAX_DISTANCE : maxDistance;
  }
}
