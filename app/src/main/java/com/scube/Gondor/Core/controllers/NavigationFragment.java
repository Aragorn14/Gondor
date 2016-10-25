package com.scube.Gondor.Core.controllers;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.scube.Gondor.Home.controllers.HomeActivity;

/**
 * Created by vashoka on 05/30/15.
 */
public abstract class NavigationFragment extends Fragment
{
    public HomeActivity navigationController;
    public String analyticsName;
    public String getTitleName()
    {
        return titleName;
    }

    public void setTitleName(String titleName)
    {
        this.titleName = titleName;
    }

    protected String titleName;
    public String tagName;
    public boolean showBottomToolbar;

    public NavigationFragment()
    {
        setHasOptionsMenu(true);
    }

    public void viewWillAppear()
    {
        trackPageView();
    }

    public void viewWillDisappear()
    {

    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return  this.tagName;
    }

    // Implement in subclass
    public void trackPageView()
    {

    }

    // Implement in subclass
    public Intent getDefaultShareIntent()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Scube Android App");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out the ScubeNow App for Android!");

        return intent;
    }

    public boolean shouldShowToolbars()
    {
        return true;
    }
}
