package com.scube.Gondor.Menu.model;

/**
 * Created by vashoka on 6/20/15.
 */
public class FilterMenuModel
{
    private String displayTitle;
    private Object object;
    private Integer index;

    public FilterMenuModel(String displayTitle, Object object, Integer index)
    {
        this.displayTitle = displayTitle;
        this.object = object;
        this.index = index;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Integer getIndex() {
        return index;
    }
}
