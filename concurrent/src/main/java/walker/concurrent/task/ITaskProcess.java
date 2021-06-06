package walker.concurrent.task;


import walker.concurrent.task.vo.TaskResult;

/**
 * @Author: huangYong
 * @Date: 2021/3/22 11:15
 */
public interface ITaskProcess<T,R> {
    TaskResult<R> execute(T data);
}
