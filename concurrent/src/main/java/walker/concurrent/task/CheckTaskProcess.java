package walker.concurrent.task;


import walker.concurrent.task.delay.TaskDelayVo;

import java.util.concurrent.DelayQueue;

/**
 * @Author: huangYong
 * @Date: 2021/3/22 15:17
 */
public class CheckTaskProcess {
    private static DelayQueue<TaskDelayVo<String>> delayQueue = new DelayQueue<>();
    //单例
    private static volatile CheckTaskProcess checkTaskProcess;

    private CheckTaskProcess() {
    }

    public static CheckTaskProcess getInstance() {
        if (checkTaskProcess == null) {
            synchronized (CheckTaskProcess.class) {
                if (checkTaskProcess == null) {
                    checkTaskProcess = new CheckTaskProcess();
                }
            }
        }
        return checkTaskProcess;
    }

    //将处理完的任务放入延迟队列中
    public void putJob(String jobName, Long expireTime) {
        TaskDelayVo<String> taskDelayVo = new TaskDelayVo<>(expireTime, jobName);
        delayQueue.offer(taskDelayVo);
        System.out.println("Job[" + jobName + "已经放入了过期检查缓存，过期时长：" + expireTime);
    }

    //处理队列中到期任务
    private static class FetchJob implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    TaskDelayVo<String> take = delayQueue.take();
                    String jobName = take.getData();
                    PendingJobPool.getContainer().remove(jobName);
                    System.out.println(jobName + " is out of date,remove from map!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    {
        Thread thread = new Thread(new FetchJob());
        thread.setDaemon(true);
        thread.start();
        System.out.println("开启任务过期检查守护线程................");
    }


}
