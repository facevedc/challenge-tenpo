package tenpo.api.history.dto;

import lombok.Getter;
import lombok.Setter;
import tenpo.api.common.validation.constraint.ValidPage;
import tenpo.api.common.validation.constraint.ValidSize;

@Getter
@Setter
public class HistoryApiRequest {

    @ValidPage
    private Integer page;

    @ValidSize
    private Integer size;
}
