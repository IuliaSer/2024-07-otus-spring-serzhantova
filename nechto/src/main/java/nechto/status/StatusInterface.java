package nechto.status;

import nechto.entity.Scores;
import nechto.enums.Status;

import java.util.List;

public interface StatusInterface {

    float count(List<Status> statuses, List<Scores> scoresList);

    Status getStatus();
}
