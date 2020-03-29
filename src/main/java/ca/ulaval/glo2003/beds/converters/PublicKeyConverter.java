package ca.ulaval.glo2003.beds.converters;

import ca.ulaval.glo2003.beds.domain.PublicKey;
import ca.ulaval.glo2003.beds.exceptions.InvalidPublicKeyException;

public class PublicKeyConverter {

  public static final String PUBLIC_KEY_PATTERN = "([A-F]|[0-9]){64}";

  public PublicKey fromString(String value) {
    String upperCaseValue = value.toUpperCase();

    if (!upperCaseValue.matches(PUBLIC_KEY_PATTERN)) throw new InvalidPublicKeyException();

    return new PublicKey(upperCaseValue);
  }

  public String toString(PublicKey publicKey) {
    return publicKey.getValue();
  }
}