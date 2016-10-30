package ihces.barganha.rest;

public interface ServiceResponseListener<T> {
    void onResponse(T response);
    void onError(Exception error);
}
