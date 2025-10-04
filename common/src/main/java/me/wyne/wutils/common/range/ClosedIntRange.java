package me.wyne.wutils.common.range;

import java.util.concurrent.ThreadLocalRandom;

public class ClosedIntRange extends Range<Integer> {

    public ClosedIntRange(Integer min, Integer max) {
        super(min, max);
    }

    @Override
    public Integer getRandom() {
        return ThreadLocalRandom.current().nextInt(getMin(), getMax() + 1);
    }

}
