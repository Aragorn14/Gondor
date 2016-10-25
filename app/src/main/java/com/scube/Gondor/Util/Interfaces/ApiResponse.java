package com.scube.Gondor.Util.Interfaces;

/**
 * Created by vashoka on 7/17/15.
 */
public class ApiResponse<T> {

    public interface Listener<T> {
        void onResponse(T var1);
    }
}
