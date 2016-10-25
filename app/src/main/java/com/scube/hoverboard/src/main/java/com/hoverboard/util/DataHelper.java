package com.scube.hoverboard.src.main.java.com.hoverboard.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by jschroeder on 10/29/13.
 */
public class DataHelper
{
    public static <T extends Comparable> List<Map.Entry> sortedMapByValue(Map<Object, T> map)
    {
        List<Map.Entry> list = new ArrayList<Map.Entry>(map.entrySet());
        Collections.sort(list,
                new Comparator()
                {
                    public int compare(Object o1, Object o2)
                    {
                        Map.Entry e1 = (Map.Entry) o1;
                        Map.Entry e2 = (Map.Entry) o2;
                        return ((Comparable) e1.getValue()).compareTo(e2.getValue());
                    }
                });

        return list;
    }
}


