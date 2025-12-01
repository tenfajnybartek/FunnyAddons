package pl.tenfajnybartek.funnyaddons.utils;


import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public final class RandomUtils {

    private RandomUtils() {}

    public static int getRandomInt(int min, int max, Logger logger) {
        if (max < min) {
            if (logger != null) {
                logger.warning("[FunnyAddons] getRandomInt: max < min ("
                        + max + " < " + min + ") - zamieniam wartości, aby uniknąć wyjątku.");
            }
            int tmp = min;
            min = max;
            max = tmp;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
