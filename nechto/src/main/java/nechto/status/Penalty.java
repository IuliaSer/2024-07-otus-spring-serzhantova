package nechto.status;

import nechto.entity.Scores;
import nechto.enums.Status;
import org.springframework.stereotype.Component;

import java.util.List;

import static nechto.enums.Status.PENALTY;

@Component
public class Penalty implements StatusInterface {

    @Override
    public float count(List<Status> statuses, List<Scores> scoresList) {
        return -2;
    }

    @Override
    public Status getStatus() {
        return PENALTY;
    }
}
