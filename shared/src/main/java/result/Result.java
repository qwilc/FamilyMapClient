package result;

import model.Model;

/**
 * The Result class stores the response body data for clear, fill, load, and register requests
 */
public class Result {
    /**
     * The message to return
     */
    protected String message;
    /**
     * Indicates whether the task was successfully accomplished
     */
    protected boolean success;

    /**
     * Creates a Result object
     *
     * @param message the result message
     * @param success whether the task was successful
     */
    public Result(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public void setDataFromModel(Model model) {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
