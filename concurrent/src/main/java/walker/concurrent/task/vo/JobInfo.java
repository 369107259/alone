package walker.concurrent.task.vo;

import walker.concurrent.task.CheckTaskProcess;
import walker.concurrent.task.ITaskProcess;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: huangYong
 * @Date: 2021/3/22 11:20
 */
public class JobInfo<R> {

    //任务名称
    private final String jobName;
    //任务的执行数量
    private final Integer jobLength;
    //任务处理器
    private final ITaskProcess<?, ?> taskProcess;
    //存放处理结果的队列 尾存，首拿
    private final LinkedBlockingDeque<TaskResult<R>> blockingDeque;
    //执行成功数量
    private final AtomicInteger successCount;
    //执行任务数量
    private final AtomicInteger taskProcessCount;
    //任务失效时间
    private final Long expireTime;

    public JobInfo(String jobName, Integer jobLength, ITaskProcess<?, ?> taskProcess, Long expireTime) {
        this.jobName = jobName;
        this.jobLength = jobLength;
        this.taskProcess = taskProcess;
        this.blockingDeque = new LinkedBlockingDeque<>(jobLength);
        this.successCount = new AtomicInteger(0);
        this.taskProcessCount = new AtomicInteger(0);
        this.expireTime = expireTime;
    }


    public ITaskProcess<?, ?> getTaskProcess() {
        return taskProcess;
    }

    //执行成功的数量
    public int getSuccessCount() {
        return successCount.get();
    }

    //执行任务的数量
    public int getTaskProcessCount() {
        return taskProcessCount.get();
    }

    //执行失败的数量
    public int getFailCount() {
        return taskProcessCount.get() - successCount.get();
    }

    //获取任务进度
    public String getTaskProcessInfo() {
        return "Success[" + successCount.get() + "]/Current[" + taskProcessCount.get() + "] Total[" + jobLength + "]";
    }

    //获取任务处理详情
    public List<TaskResult<R>> getTaskDetails(){
        List<TaskResult<R>> results = new LinkedList<>();
        TaskResult<R> taskResult;
        while ((taskResult = blockingDeque.pollFirst())!= null){
            results.add(taskResult);
        }
        return results;
    }
    //添加任务到队列
    public void addTaskResult(TaskResult<R> taskResult, CheckTaskProcess checkTaskProcess){
        if (TaskResultType.Success.equals(taskResult.getResultType())){
            successCount.incrementAndGet();
        }
        taskProcessCount.incrementAndGet();
        blockingDeque.addLast(taskResult);
        checkTaskProcess.putJob(jobName,expireTime);
    }

}
