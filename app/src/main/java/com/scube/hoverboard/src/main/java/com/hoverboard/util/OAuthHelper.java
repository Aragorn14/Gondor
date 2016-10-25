package com.scube.hoverboard.src.main.java.com.hoverboard.util;

import org.apache.http.HttpRequest;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * Created by jschroeder on 1/21/14.
 */
public class OAuthHelper
{
    /**
     * *****************************************
     * // TODO: THESE NEED TO BE FOR ANDROID
     * *****************************************
     */
    public static final String APP_CONSUMER_KEY = "iOS-Next";
    public static final String APP_CONSUMER_SECRET = "07f4f194039e65696083dddd9c27c7cb";

    private static OAuthHelper instance;
    private OAuthConsumer mConsumer;

    public static OAuthHelper getInstance()
    {
        if (instance == null) {
            instance = new OAuthHelper(APP_CONSUMER_KEY, APP_CONSUMER_SECRET);
        }

        return instance;
    }

    public OAuthHelper(String consumerKey, String consumerSecret)
    {
        mConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
    }

    public oauth.signpost.http.HttpRequest signRequest(HttpRequest request)
            throws
            OAuthCommunicationException,
            OAuthExpectationFailedException,
            OAuthMessageSignerException
    {
        return mConsumer.sign(request);
    }
}
