package tenpo.api.common.error;

import java.util.List;

public record ModelError(
        int status,
        String code,
        String message,
        List<String> errors
) {
}
