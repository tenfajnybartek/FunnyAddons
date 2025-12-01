package pl.tenfajnybartek.funnyaddons.utils;

import org.apache.commons.lang3.Validate;

import java.util.Random;
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
        // ThreadLocalRandom.nextInt(origin, bound) is exclusive for bound, so +1 to include max
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
