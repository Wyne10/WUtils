package me.wyne.wutils.common.range;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import me.wyne.wutils.common.range.iterator.TimeSpanIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class TimeSpanRange extends Range<TimeSpan> {

    public TimeSpanRange(TimeSpan min, TimeSpan max) {
        super(new TimeSpan(Math.min(min.getMillis(), max.getMillis()), Durations.Millis),
                new TimeSpan(Math.max(min.getMillis(), max.getMillis()), Durations.Millis),
                new TimeSpan((min.getMillis() + max.getMillis()) / 2, Durations.Millis),
                new TimeSpan(max.getMillis() - min.getMillis(), Durations.Millis));
    }

    @Override
    public TimeSpan getRandom() {
        return new TimeSpan(ThreadLocalRandom.current().nextLong(getMin().getMillis(), getMax().getMillis() + 1), Durations.Millis);
    }

    @Override
    public boolean contains(TimeSpan value) {
        return value.getMillis() >= getMin().getMillis() && value.getMillis() <= getMax().getMillis();
    }

    @Override
    public @NotNull Iterator<TimeSpan> iterator() {
        return new TimeSpanIterator(getMin(), getMax(), new TimeSpan(1, Durations.Seconds));
    }

}
