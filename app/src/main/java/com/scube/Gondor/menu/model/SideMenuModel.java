package com.scube.Gondor.Menu.model;

import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Menu.controllers.SideMenuFragmentDrawer;
import com.scube.Gondor.R;

/**
 * Created by jschroeder on 10/9/13.
 */
public class SideMenuModel
{
    public String title;
    public int imageId;
    private Class<? extends NavigationFragment> fragmentClass;
    public boolean isSelected;
    public SideMenuHeaderModel headerModel;
    public String imageUrlString = null;
    public boolean isChecked = false;
    public SideMenuFragmentDrawer.SideMenuBlock selectedBlock;
    public int supplementaryImageId = -1;
    public String supplementaryString = null;
    public boolean roundedCorners = false;
    public boolean isEnabled = true;
    public int textColorId = R.color.GRAY;

    public Class<? extends NavigationFragment> getFragmentClass() {
        return fragmentClass;
    }

    public void setFragmentClass(Class fragmentClass)
    {
        this.fragmentClass = fragmentClass;
    }

    public SideMenuModel(String title, Class fragmentClass, int imageViewId)
    {
        this.title = title;
        this.fragmentClass = fragmentClass;
        this.imageId = imageViewId;
    }

    public SideMenuModel(String title, Class fragmentClass, int imageViewId, SideMenuHeaderModel headerModel)
    {
        this.title = title;
        this.fragmentClass = fragmentClass;
        this.imageId = imageViewId;
        this.headerModel = headerModel;
    }

    public SideMenuModel(String title, int imageViewId, SideMenuHeaderModel headerModel, SideMenuFragmentDrawer.SideMenuBlock block)
    {
        this.title = title;
        this.selectedBlock = block;
        this.imageId = imageViewId;
        this.headerModel = headerModel;
    }
}
