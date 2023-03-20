package me.wyne.wutils.log;

/**
 * Used to control Log.info(String), Log.warn(String) and Log.error(String) logging from {@link Log}.
 */
public interface LogConfig {

    /**
     * @return Do info logging?
     */
    boolean logInfo();
    /**
     * @return Do warning logging?
     */
    boolean logWarn();
    /**
     * @return Do error logging?
     */
    boolean logError();

}
