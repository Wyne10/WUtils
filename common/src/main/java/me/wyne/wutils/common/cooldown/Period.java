package me.wyne.wutils.common.cooldown;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.concurrent.TimeUnit;

public class Period {

    private Long finishAt;

    public Period() {
    }

    public Period(long finishAt) {
        this.finishAt = finishAt;
    }

    public boolean isExpired() {
        return finishAt == null || System.currentTimeMillis() > finishAt;
    }

    public void put(long durationMillis) {
        this.finishAt = System.currentTimeMillis() + durationMillis;
    }

    public void put(long duration, TimeUnit unit) {
        this.finishAt = System.currentTimeMillis() + unit.toMillis(duration);
    }

    public void stop() {
        finishAt = null;
    }

    public long getRemaining() {
        if (isExpired())
            return 0;

        return finishAt - System.currentTimeMillis();
    }

    public long getRemaining(TimeUnit unit) {
        return unit.convert(getRemaining(), TimeUnit.MILLISECONDS);
    }

    public String getRemainingStringFormat() {
        return DurationFormatUtils
                .formatDurationHMS(getRemaining());
    }

    public String getRemainingStringFormat(String format) {
        return DurationFormatUtils
                .formatDuration(getRemaining(), format);
    }

}
