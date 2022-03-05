package cangjie.scale.sorting.scale;

import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;

/**
 * @author: guruohan
 * @date: 2022/3/5
 */
public class SystemClockEx {

    /**
     * 等待指定的若干毫秒。
     * 类似 {@link Thread#sleep(long)}，不过不会抛出{@link InterruptedException}异常;
     *
     * @param ms 指定等待的时间，毫秒
     * @param forceDuration true，强制经过指定的时候后，方法才会返回；false，{@link Thread#interrupt()}后立即返回
     */
    private static void sleep(long ms, boolean forceDuration) {

        long start = uptimeMillis();
        long duration = ms;
        boolean interrupted = false;
        do {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            duration = start + ms - uptimeMillis();
        } while (forceDuration && duration > 0);

        if (interrupted) {
            // Important: we don't want to quietly eat an interrupt() event,
            // so we make sure to re-interrupt the thread so that the next
            // call to Thread.sleep() or Object.wait() will be interrupted.
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 等待指定的若干毫秒。
     * 类似 {@link Thread#sleep(long)}，不过不会抛出{@link InterruptedException}异常;
     * {@link Thread#interrupt()}后立即返回。
     */
    public static void sleep(long ms) {
        sleep(ms, false);
    }

    /**
     * 等待指定的若干毫秒。睡够才会停。
     * 类似 {@link Thread#sleep(long)}，不过不会抛出{@link InterruptedException}异常;
     * 调用{@link Thread#interrupt()}后，方法也不会立即返回，必须经过指定的时间后，方法才会返回。
     *
     * @param ms
     */
    public static void sleepUntil(long ms) {
        sleep(ms, true);
    }

    /**
     * 开机到现在的时间(不包含睡眠时间)，毫秒
     *
     * @return
     */
    public static long uptimeMillis() {
        return SystemClock.uptimeMillis();
    }

    /**
     * 开机到现在的时间(包含睡眠时间)，毫秒
     *
     * @return
     */
    public static long elapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static long elapsedRealtimeNanos() {
        return SystemClock.elapsedRealtimeNanos();
    }

    /**
     * 当前线程运行时间
     *
     * @return
     */
    public static long currentThreadTimeMillis() {
        return SystemClock.currentThreadTimeMillis();
    }
}
