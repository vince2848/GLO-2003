package ca.ulaval.glo2003;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MainTest {

  @Test
  public void given_when_then() {
    assertTrue(true); // TODO : Remove dummy test
  }

  @Test
  public void aTest() {
    Main main = new Main();

    assertEquals(3, main.test("hello"));
  }
}
