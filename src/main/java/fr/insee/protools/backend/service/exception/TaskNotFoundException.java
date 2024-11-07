package fr.insee.protools.backend.service.exception;

import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.task.api.Task;

/**
 * This exception is thrown when you try to access a task that does not exist
 */
@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class TaskNotFoundException extends FlowableObjectNotFoundException {

    /**
     * the id of the task
     */
    private final String taskId;

    public TaskNotFoundException(String taskId) {
        super("No task found with id '" + taskId + "'.", Task.class);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return this.taskId;
    }

}
