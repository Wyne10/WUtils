package me.wyne.wutils.common.cooldown;

import me.wyne.wutils.common.duration.TimeSpan;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * A single expiry timestamp, e.g. a standalone cooldown not keyed by anything. Unlike
 * {@link CooldownMap}, holds exactly one deadline. A {@code Period} with no deadline
 * set (never {@link #put}, or after {@link #stop}) is always considered expired.
 */
public class Period {

    private Long finishAt;

    /** Creates a {@code Period} with no deadline set; {@link #isExpired()} is {@code true}. */
    public Period() {}

    /** Creates a {@code Period} expiring at the given epoch-millisecond timestamp. */
    public Period(long finishAt) {
        this.finishAt = finishAt;
    }

    public Period(long duration, @NotNull TimeUnit unit) {
        put(duration, unit);
    }

    public Period(@NotNull TimeSpan duration) {
        put(duration.getMillis());
    }

    /** The absolute expiry timestamp in epoch milliseconds, or {@code null} if never set. */
    public @Nullable Long getFinishAt() {
        return finishAt;
    }

    /** Whether no deadline is set, or the deadline has passed. */
    public boolean isExpired() {
        return finishAt == null || System.currentTimeMillis() > finishAt;
    }

    /** Starts (or restarts) the period, expiring after {@code durationMillis}. */
    public void put(long durationMillis) {
        this.finishAt = System.currentTimeMillis() + durationMillis;
    }

    public void put(long duration, @NotNull TimeUnit unit) {
        this.finishAt = System.currentTimeMillis() + unit.toMillis(duration);
    }

    public void put(@NotNull TimeSpan duration) {
        put(duration.getMillis());
    }

    /** Clears the deadline; {@link #isExpired()} becomes {@code true}. */
    public void stop() {
        finishAt = null;
    }

    /** Milliseconds until expiry, or {@code 0} if already expired or never set. */
    public long getRemaining() {
        if (isExpired())
            return 0;

        return finishAt - System.currentTimeMillis();
    }

    public long getRemaining(@NotNull TimeUnit unit) {
        return unit.convert(getRemaining(), TimeUnit.MILLISECONDS);
    }

    public @NotNull String getRemainingStringFormat() {
        return DurationFormatUtils
                .formatDurationHMS(getRemaining());
    }

    public @NotNull String getRemainingStringFormat(@NotNull String format) {
        return DurationFormatUtils
                .formatDuration(getRemaining(), format);
    }

}
