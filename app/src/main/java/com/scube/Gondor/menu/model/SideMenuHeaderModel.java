package com.scube.Gondor.Menu.model;

/**
 * Created by jschroeder on 1/15/14.
 */
public class SideMenuHeaderModel
{
    public long id;
    public String title;
    public boolean isEditable = false;
    public boolean isEditing = false;
    public int numChildrenChecked = 0;

    public SideMenuHeaderModel(long id, String title)
    {
        this.id = id;
        this.title = title;
    }
}
