package tenpo.api.calculation.mapper;

import org.springframework.stereotype.Component;
import tenpo.api.calculation.dto.CalculationResponse;
import tenpo.domain.calculation.model.CalculationResult;

@Component
public class CalculationResponseMapper {

    public CalculationResponse toResponse(CalculationResult model) {
        return new CalculationResponse(
                model.num1(),
                model.num2(),
                model.baseSum(),
                model.percentage(),
                model.finalAmount(),
                model.percentageSource()
        );
    }
}
