package nechto.status;

import nechto.entity.Scores;
import nechto.enums.Status;

import java.util.List;

public interface StatusProcessor {
    float processStatus(List<Status> statuses, List<Scores> scoresList, Status status);
}
