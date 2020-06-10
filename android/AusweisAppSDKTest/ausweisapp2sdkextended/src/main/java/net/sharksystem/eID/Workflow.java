package net.sharksystem.eID;

import android.util.AndroidException;

public interface Workflow<R> {
    class WorkflowException extends AndroidException {
        public WorkflowException(String msg) {
            super(msg);
        }
    }

    /**
     * Method called by the {@link Connector).
     * @param receive messages from the connector.
     * @param send messages to the connector.
     * @return the workflow result.
     * @throws WorkflowException
     * @throws InterruptedException
     */
    R run(OutChan<String> receive, InChan<String> send) throws WorkflowException, InterruptedException;

    /**
     * Sends a cancel request and returns immediately.
     * A previous call to {@link #run} may throw an {@link #(WorkflowException)}.
     */
    void cancel(InChan<String> send);
}
