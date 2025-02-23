package nechto.status;

import nechto.entity.Scores;
import nechto.enums.Status;
import nechto.exception.StatusNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StatusProcessorImpl implements StatusProcessor {

    private final Map<Status, StatusInterface> map;

    public StatusProcessorImpl(List<StatusInterface> statuses) {
        this.map = statuses.stream()
                .collect(Collectors.toMap(StatusInterface::getStatus, Function.identity()));
    }

    @Override
    public float processStatus(List<Status> statuses, List<Scores> scoresList, Status status) {
        var statusInterface = map.get(status);
        if (statusInterface == null) {
            throw new StatusNotFoundException("No status implementation for given status registered");
        }
        return statusInterface.count(statuses, scoresList);
    }
}
