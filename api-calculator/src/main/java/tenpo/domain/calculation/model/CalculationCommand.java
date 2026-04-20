package tenpo.domain.calculation.model;

import java.math.BigDecimal;

public record CalculationCommand(BigDecimal num1, BigDecimal num2) {
}
