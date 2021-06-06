package walker.concurrent.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import walker.concurrent.task.vo.JobInfo;
import walker.concurrent.task.vo.TaskResult;
import walker.concurrent.task.vo.TaskResultType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: huangYong
 * @Date: 2021/3/22 11:21
 */
public class PendingJobPool {
    //需要个容器存放Job
    private static ConcurrentHashMap<String, JobInfo<?>> jobInfoMap = new ConcurrentHashMap<>();

    //需要个线程池执行任务
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Pending-Job-Pool-%d").build();
    private static ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000), threadFactory);

    //检测队列过期类
    private static CheckTaskProcess checkTaskProcess = CheckTaskProcess.getInstance();

    //单例模式
    private static volatile PendingJobPool pendingJobPool;

    private PendingJobPool() {
    }

    public static PendingJobPool getInstance() {
        if (pendingJobPool == null) {
            synchronized (PendingJobPool.class) {
                if (pendingJobPool == null) {
                    pendingJobPool = new PendingJobPool();
                }
            }
        }
        return pendingJobPool;
    }

    //获取任务的容器
    public static Map<String, JobInfo<?>> getContainer() {
        return jobInfoMap;
    }

    private static class PendingTask<T, R> implements Runnable {

        private JobInfo<R> jobInfo;

        private T processData;

        public PendingTask(JobInfo<R> jobInfo, T processData) {
            this.jobInfo = jobInfo;
            this.processData = processData;
        }

        @Override
        public void run() {
            TaskResult<R> taskResult = null;
            try {
                ITaskProcess<T, R> taskProcess = (ITaskProcess<T, R>) jobInfo.getTaskProcess();
                //执行业务人员具体方法
                taskResult = taskProcess.execute(processData);
                if (taskResult == null) {
                    taskResult = new TaskResult<>(TaskResultType.Exception, null, "result is null");
                }
                if (taskResult.getResultType() == null) {
                    if (taskResult.getReason() == null) {
                        taskResult = new TaskResult<>(TaskResultType.Exception, null, "reason is null");
                    }
                    taskResult = new TaskResult<>(TaskResultType.Exception, null, "ResultType is null");
                }
            } catch (Exception e) {
                taskResult = new TaskResult<>(TaskResultType.Exception, null, e.getMessage());
                e.printStackTrace();
            } finally {
                jobInfo.addTaskResult(taskResult,checkTaskProcess);
            }
        }
    }

    //根据名称检索工作
    private <R> JobInfo<R> getJob(String jobName) {
        JobInfo<R> jobInfo = (JobInfo<R>) jobInfoMap.get(jobName);
        if (null == jobInfo) {
            throw new RuntimeException(jobName + "是个非法任务。");
        }
        return jobInfo;
    }

    //调用者注册任务
    public <R> void registerJob(String jobName, Integer jobLength, ITaskProcess<?, ?> taskProcess, Long expireTime) {
        JobInfo<R> jobInfo = new JobInfo<>(jobName, jobLength, taskProcess, expireTime);
        if (jobInfoMap.putIfAbsent(jobName, jobInfo) != null)
            throw new RuntimeException(jobName + "已经注册了！");
    }

    //调用者提交工作中的任务
    public <T, R> void executeTask(String jobName, T data) {
        JobInfo<R> jobInfo = getJob(jobName);
        PendingTask<T, R> pendingTask = new PendingTask<>(jobInfo, data);
        executorService.execute(pendingTask);
    }

    //根据名称获取每个任务处理详情
    public <R> List<TaskResult<R>> getTaskResultDetails(String jobName) {
        JobInfo<R> job = getJob(jobName);
        return job.getTaskDetails();
    }

    //获取任务的进度
    public <R> String getTaskProcess(String jobName) {
        JobInfo<R> job = getJob(jobName);
        return job.getTaskProcessInfo();
    }


}
