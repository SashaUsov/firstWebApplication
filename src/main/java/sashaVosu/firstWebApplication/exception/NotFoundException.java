package sashaVosu.firstWebApplication.exception;

public class NotFoundException extends RuntimeException {

    private String text;

    public NotFoundException(String s) {
        text = s ;
    }

    public String toString() {
        return  "UserExistsException : " + text;
    }
}