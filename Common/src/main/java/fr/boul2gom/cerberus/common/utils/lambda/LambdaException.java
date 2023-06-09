package fr.boul2gom.cerberus.common.utils.lambda;

public class LambdaException extends Exception {

    public LambdaException(String message) {
        super(message);
    }

    public LambdaException(Throwable cause) {
        super(cause);
    }

    public LambdaException(String message, Throwable cause) {
        super(message, cause);
    }
}
