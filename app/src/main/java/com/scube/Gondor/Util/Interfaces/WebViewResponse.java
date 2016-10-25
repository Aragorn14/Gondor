package com.scube.Gondor.Util.Interfaces;

/**
 * Created by vashoka on 7/17/15.
 */
public class WebViewResponse<T> {

    public interface Listener<T> {
        void onReady(T var1);
    }
}
