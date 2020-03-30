package ca.ulaval.glo2003.beds.domain;

import static ca.ulaval.glo2003.beds.domain.helpers.BedBuilder.aBed;
import static ca.ulaval.glo2003.beds.domain.helpers.BedObjectMother.createLocation;
import static ca.ulaval.glo2003.beds.rest.helpers.PackageRequestBuilder.aPackageRequest;
import static ca.ulaval.glo2003.interfaces.helpers.Randomizer.randomEnum;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import ca.ulaval.glo2003.beds.converters.PackageConverter;
import ca.ulaval.glo2003.beds.exceptions.AllYouCanDrinkDependencyException;
import ca.ulaval.glo2003.beds.exceptions.ExceedingAccommodationCapacityException;
import ca.ulaval.glo2003.beds.exceptions.SweetToothDependencyException;
import ca.ulaval.glo2003.beds.rest.PackageRequest;
import ca.ulaval.glo2003.locations.domain.Location;
import ca.ulaval.glo2003.transactions.converters.PriceConverter;
import ca.ulaval.glo2003.transactions.domain.Price;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BedFactoryTest {

  // TODO : Refactor this test class

  private static BedFactory bedFactory;
  private static PriceConverter priceConverter;
  private static PackageConverter packageConverter;

  private List<PackageRequest> packageRequests = getPackageRequest();
  private List<PackageRequest> otherPackageRequests = getPackageRequest();
  private Map<Packages, Price> packages = packageConverter.fromRequests(packageRequests);
  private Map<Packages, Price> otherPackages = packageConverter.fromRequests(otherPackageRequests);
  private Bed bed = aBed().withPricesPerNights(packages).build();
  private Bed otherBed = aBed().withPricesPerNights(otherPackages).build();
  private Location location = createLocation();

  BedFactoryTest() {}

  @BeforeAll
  public static void setUpFactory() {
    bedFactory = new BedFactory();
    priceConverter = mock(PriceConverter.class);
    packageConverter = new PackageConverter(priceConverter);
  }

  @Test
  public void create_shouldSetBedNumber() {
    bed = bedFactory.create(bed, location);

    assertNotNull(bed.getNumber());
  }

  @Test
  public void create_shouldSetZipCode() {
    bed = bedFactory.create(bed, location);

    assertEquals(location, bed.getLocation());
  }

  @Test
  public void create_shouldSetDifferentBedNumbers() {
    bed = bedFactory.create(bed, location);
    otherBed = bedFactory.create(otherBed, location);

    assertNotEquals(bed.getNumber(), otherBed.getNumber());
  }

  @ParameterizedTest
  @EnumSource(BedTypes.class)
  public void create_withExceedingCapacity_shouldThrowExceedingAccommodationCapacityException(
      BedTypes bedType) {
    int maxCapacity = BedTypesCapacities.get(bedType);
    bed =
        aBed()
            .withBedType(bedType)
            .withCapacity(maxCapacity + 1)
            .withPricesPerNights(packages)
            .build();

    assertThrows(
        ExceedingAccommodationCapacityException.class, () -> bedFactory.create(bed, location));
  }

  @ParameterizedTest
  @EnumSource(Packages.class)
  public void create_withDependencies_shouldThrowNoThrow(Packages testPackage) {
    List<String> requestPackagesNames = new ArrayList<>();
    do {
      requestPackagesNames.add(testPackage.toString());
      testPackage = PackagesDependencies.getDependency(testPackage);
    } while (testPackage != null);
    packageRequests =
        requestPackagesNames.stream()
            .map(s -> aPackageRequest().withName(s).build())
            .collect(Collectors.toList());
    packages = packageConverter.fromRequests(packageRequests);
    bed = aBed().withPricesPerNights(packages).build();

    assertDoesNotThrow(() -> bedFactory.create(bed, location));
  }

  @Test
  public void create_withoutAllYouCanDrinkDependencies_shouldThrowCantOfferAllYouCanDrinkPackage() {
    String packageName = Packages.ALL_YOU_CAN_DRINK.toString();
    PackageRequest request = aPackageRequest().withName(packageName).build();
    packageRequests = Collections.singletonList(request);
    packages = packageConverter.fromRequests(packageRequests);
    bed = aBed().withPricesPerNights(packages).build();

    assertThrows(AllYouCanDrinkDependencyException.class, () -> bedFactory.create(bed, location));
  }

  @Test
  public void create_withoutSweetToothDependencies_shouldThrowCantOfferAllYouCanDrinkPackage() {
    String packageName = Packages.BLOODTHIRSTY.toString();
    String otherPackageName = Packages.SWEET_TOOTH.toString();
    PackageRequest request = aPackageRequest().withName(packageName).build();
    PackageRequest otherRequest = aPackageRequest().withName(otherPackageName).build();
    packageRequests = Arrays.asList(request, otherRequest);
    packages = packageConverter.fromRequests(packageRequests);
    bed = aBed().withPricesPerNights(packages).build();

    assertThrows(SweetToothDependencyException.class, () -> bedFactory.create(bed, location));
  }

  @Test
  public void
      create_withSWAmdAYCNWithoutAYCDDependencies_shouldThrowCantOfferAllYouCanDrinkPackage() {
    String packageName = Packages.ALL_YOU_CAN_DRINK.toString();
    String otherPackageName = Packages.SWEET_TOOTH.toString();
    PackageRequest request = aPackageRequest().withName(packageName).build();
    PackageRequest otherRequest = aPackageRequest().withName(otherPackageName).build();
    packageRequests = Arrays.asList(request, otherRequest);
    packages = packageConverter.fromRequests(packageRequests);
    bed = aBed().withPricesPerNights(packages).build();

    assertThrows(AllYouCanDrinkDependencyException.class, () -> bedFactory.create(bed, location));
  }

  @Test
  public void create_withOnlySweetToothDependencies_shouldThrowCantOfferAllYouCanDrinkPackage() {
    String packageName = Packages.SWEET_TOOTH.toString();
    PackageRequest request = aPackageRequest().withName(packageName).build();
    packageRequests = Collections.singletonList(request);
    packages = packageConverter.fromRequests(packageRequests);
    bed = aBed().withPricesPerNights(packages).build();

    assertThrows(SweetToothDependencyException.class, () -> bedFactory.create(bed, location));
  }

  private List<PackageRequest> getPackageRequest() {
    Packages randPackage = randomEnum(Packages.class);
    List<String> requestPackagesNames = new ArrayList<>();
    do {
      requestPackagesNames.add(randPackage.toString());
      randPackage = PackagesDependencies.getDependency(randPackage);
    } while (randPackage != null);
    return requestPackagesNames.stream()
        .map(s -> aPackageRequest().withName(s).build())
        .collect(Collectors.toList());
  }
}
