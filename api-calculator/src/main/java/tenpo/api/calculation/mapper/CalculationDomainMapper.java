package tenpo.api.calculation.mapper;

import org.springframework.stereotype.Component;
import tenpo.api.calculation.dto.CalculationApiRequest;
import tenpo.domain.calculation.model.CalculationCommand;

@Component
public class CalculationDomainMapper {

    public CalculationCommand toModel(CalculationApiRequest request) {
        return new CalculationCommand(request.getNum1(), request.getNum2());
    }
}

