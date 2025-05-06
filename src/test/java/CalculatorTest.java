import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CalculatorTest {

  @Test
  void add() {
    assertEquals(4, Calculator.add(2, 2));
    assertEquals(4, Calculator.add(8, -4));
  }
}