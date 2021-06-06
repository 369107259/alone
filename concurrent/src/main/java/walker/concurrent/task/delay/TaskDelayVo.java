package walker.concurrent.task.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Author: huangYong
 * @Date: 2021/3/22 15:26
 */
public class TaskDelayVo<T> implements Delayed {
    private Long expireTime;
    private T data;

    public TaskDelayVo(Long expireTime, T data) {
        //将传入的时长转换成超时的时刻
        this.expireTime = TimeUnit.NANOSECONDS.convert(expireTime, TimeUnit.MILLISECONDS)
                + System.nanoTime();
        this.data = data;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public T getData() {
        return data;
    }


    //获取剩余时间
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expireTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long res = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return res == 0 ? 0 : (res > 0 ? 1 : -1);
    }
}
