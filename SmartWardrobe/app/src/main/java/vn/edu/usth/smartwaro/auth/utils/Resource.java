package vn.edu.usth.smartwaro.auth.utils;

public class Resource<T> {
    private Status status;
    private T data;
    private String error;

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    private Resource(Status status, T data, String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String error) {
        return new Resource<>(Status.ERROR, null, error);
    }

    public Status getStatus() { return status; }
    public T getData() { return data; }
    public String getError() { return error; }
}
