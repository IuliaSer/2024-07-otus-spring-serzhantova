package nechto.status;

import nechto.entity.Scores;
import nechto.enums.Status;
import org.springframework.stereotype.Component;

import java.util.List;

import static nechto.enums.Status.CONTAMINATED;
import static nechto.enums.Status.WON;

@Component
public class Contaminated implements StatusInterface {

    @Override
    public float count(List<Status> statuses, List<Scores> scoresList) {
        return statuses.contains(WON) ? 1 : -1;
    }

    @Override
    public Status getStatus() {
        return CONTAMINATED;
    }
}
