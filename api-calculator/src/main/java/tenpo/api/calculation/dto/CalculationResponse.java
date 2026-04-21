package tenpo.api.calculation.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class CalculationResponse {

    private final BigDecimal num1;
    private final BigDecimal num2;
    private final BigDecimal baseSum;
    private final BigDecimal percentage;
    private final BigDecimal finalAmount;
    private final String percentageSource;
}
