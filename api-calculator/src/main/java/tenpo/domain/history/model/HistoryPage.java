package tenpo.domain.history.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryPage {

    private final List<HistoryEntry> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}
