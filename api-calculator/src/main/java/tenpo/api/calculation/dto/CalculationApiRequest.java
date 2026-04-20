package tenpo.api.calculation.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import tenpo.api.common.validation.constraint.RequiredDecimal;

@Getter
@Setter
public class CalculationApiRequest {

    @RequiredDecimal(field = "num1")
    private BigDecimal num1;

    @RequiredDecimal(field = "num2")
    private BigDecimal num2;
}
