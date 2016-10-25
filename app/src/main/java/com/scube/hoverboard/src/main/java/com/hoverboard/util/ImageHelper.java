package com.scube.hoverboard.src.main.java.com.hoverboard.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fvelasquez on 26/02/15.
 */
public class ImageHelper
{
    public static URL imageUrlForImageSize(String imageUrl, ImageSize size) throws MalformedURLException
    {
        if (imageUrl != null) {
            if (imageUrl.contains("{size}")) {
                imageUrl = imageUrlForContentSize(imageUrl, size);
            } else if (imageUrl.contains("{width}")) {
                imageUrl = imageUrlForWidthHeight(imageUrl, size);
            }
        }

        return new URL(imageUrl);
    }

    public static String imageUrlForContentSize(String imageUrl, ImageSize size)
    {
        String styleSizeString = "";
        switch (size) {
            case IGNPICO:
                styleSizeString = "pico";
                break;
            case IGNICON:
                styleSizeString = "icon";
                break;
            case IGNTHUMB:
                styleSizeString = "thumb";
                break;
            case IGNSMALL:
                styleSizeString = "small";
                break;
            case IGNCOMPACT:
                styleSizeString = "compact";
                break;
            case IGNMEDIUM:
                styleSizeString = "medium";
                break;
            case IGNLARGE:
                styleSizeString = "large";
                break;
            case IGNGRANDE:
                styleSizeString = "grande";
                break;
            case IGNMOBILE:
                styleSizeString = "mobile";
                break;
            case IGNW60:
                styleSizeString = "60w";
                break;
            case IGNW80:
                styleSizeString = "80w";
                break;
            case IGNW160:
                styleSizeString = "160w";
                break;
            case IGNW180:
                styleSizeString = "180w";
                break;
            case IGNW247:
                styleSizeString = "247w";
                break;
            case IGNW320:
                styleSizeString = "320w";
                break;
            case IGNW358:
                styleSizeString = "358w";
                break;
            case IGNW400:
                styleSizeString = "400w";
                break;
            case IGNW480:
                styleSizeString = "480w";
                break;
            case IGNW560:
                styleSizeString = "560w";
                break;
            case IGNW610:
                styleSizeString = "610w";
                break;
            case IGNW624:
                styleSizeString = "624w";
                break;
            case IGNW640:
                styleSizeString = "640w";
                break;
            case IGNW676:
                styleSizeString = "676w";
                break;
            case IGNW765:
                styleSizeString = "765w";
                break;
            case IGNW800:
                styleSizeString = "800w";
                break;
            case IGNW948:
                styleSizeString = "948w";
                break;
            case IGNW960:
                styleSizeString = "960w";
                break;
            case IGNW1024:
                styleSizeString = "1024w";
                break;
            case IGNW1670:
                styleSizeString = "1670w";
                break;
            case IGNH45:
                styleSizeString = "45h";
                break;
            case IGNH75:
                styleSizeString = "75h";
                break;
            case IGNH90:
                styleSizeString = "90h";
                break;
            case IGNH160:
                styleSizeString = "160h";
                break;
            case IGNH180:
                styleSizeString = "180h";
                break;
            case IGNH225:
                styleSizeString = "225h";
                break;
            case IGNH246:
                styleSizeString = "246h";
                break;
            case IGNH270:
                styleSizeString = "270h";
                break;
            case IGNH315:
                styleSizeString = "315h";
                break;
            case IGNH360:
                styleSizeString = "360h";
                break;
            case IGNH450:
                styleSizeString = "450h";
                break;
            case IGNH540:
                styleSizeString = "540h";
                break;
            case IGNH576:
                styleSizeString = "576h";
                break;
        }

        if (imageUrl != null) {
            imageUrl = imageUrl.replace("{size}", styleSizeString);
        }

        return imageUrl;
    }

    public static String imageUrlForWidthHeight(String imageUrl, ImageSize size)
    {
        String widthString = "";
        String heightString = "";

        switch (size) {
            case IGNPICO:
                widthString = "16";
                heightString = "16";
                break;
            case IGNICON:
                widthString = "26";
                heightString = "15";
                break;
            case IGNTHUMB:
                widthString = "66";
                heightString = "37";
                break;
            case IGNSMALL:
                widthString = "146";
                heightString = "82";
                break;
            case IGNCOMPACT:
                widthString = "306";
                heightString = "172";
                break;
            case IGNMEDIUM:
                widthString = "466";
                heightString = "262";
                break;
            case IGNLARGE:
                widthString = "626";
                heightString = "352";
                break;
            case IGNGRANDE:
                widthString = "946";
                heightString = "532";
                break;
            case IGNMOBILE:
                widthString = "100";
                heightString = "60";
                break;
            case IGNW160:
                widthString = "160";
                heightString = "90";
                break;
            case IGNW320:
                widthString = "320";
                heightString = "180";
                break;
            case IGNW400:
                widthString = "400";
                heightString = "225";
                break;
            case IGNW480:
                widthString = "480";
                heightString = "270";
                break;
            case IGNW624:
                widthString = "624";
                heightString = "351";
                break;
            case IGNW640:
                widthString = "640";
                heightString = "360";
                break;
            case IGNW960:
                widthString = "960";
                heightString = "540";
                break;
            case IGNW1024:
                widthString = "1024";
                heightString = "576";
                break;
            case IGNW1280:
                widthString = "1280";
                heightString = "720";
                break;
        }

        if (imageUrl != null) {
            imageUrl = imageUrl.replace("{width}", widthString).replace("{height}", heightString);
        }

        return imageUrl;
    }
}
