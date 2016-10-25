package com.scube.hoverboard.src.main.java.com.hoverboard.util;

import java.util.List;
import java.util.Map;

/**
 * Created by jschroeder on 10/31/13.
 */
public class ModelHelper
{
    public static String closestBoxArtURLFromMedia(List<Map> boxArt, Integer width, Integer threshold)
    {
        String boxArtUrl = null;
        Integer closestWidth = threshold;

        try {
            for (Map map : boxArt) {
                if (map.containsKey("width")) {
                    Integer boxArtWidth = (Integer) map.get("width");
                    Integer boxArtWidthDelta = Math.abs(width - boxArtWidth);
                    Integer closestDelta = Math.abs(width - closestWidth);
                    if (boxArtWidthDelta < closestDelta) {
                        //TODO: Strip white spaces on ends
                        if (map.containsKey("url")) {
                            boxArtUrl = (String) map.get("url");
                            closestWidth = boxArtWidth;
                            if (boxArtWidthDelta == 0 && boxArtUrl != null) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                e.printStackTrace();
            }
        }

        return boxArtUrl;
    }
}
