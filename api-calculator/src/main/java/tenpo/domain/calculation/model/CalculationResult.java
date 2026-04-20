package tenpo.domain.calculation.model;

import java.math.BigDecimal;

public record CalculationResult(
        BigDecimal num1,
        BigDecimal num2,
        BigDecimal baseSum,
        BigDecimal percentage,
        BigDecimal finalAmount,
        String percentageSource
) {
}
