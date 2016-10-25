package com.scube.Gondor.Menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.scube.Gondor.Core.views.GhostView;
import com.scube.Gondor.Menu.views.SideMenuCell;
import com.scube.Gondor.Menu.views.SideMenuHeaderCell;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

//import com.mobile.ign.app.views.GhostView;
//import com.mobile.ign.menu.views.SideMenuCell;
//import com.mobile.ign.menu.views.SideMenuHeaderCell;

/**
 * Created by vashoka on 05/30/15.
 */
public class SideMenuAdapter extends BaseAdapter implements StickyListHeadersAdapter
{
    public ArrayList<SideMenuModel> getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(ArrayList<SideMenuModel> dataSource)
    {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    private ArrayList<SideMenuModel> dataSource = new ArrayList<SideMenuModel>();
    private Context mContext;
    private SideMenuListener listener;

    public SideMenuAdapter(Context context, SideMenuListener listener)
    {
        mContext = context;
        this.listener = listener;
    }

    public int getCount()
    {
        return dataSource.size();
    }

    public SideMenuModel getItem(int position)
    {
        return dataSource.get(position);
    }

    public void clear()
    {
        dataSource.clear();
        notifyDataSetChanged();
    }

    public void addItem(SideMenuModel item)
    {
        dataSource.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(SideMenuModel item)
    {
        dataSource.remove(item);
    }

    public void addItems(List<SideMenuModel> items)
    {
        addItems(items, dataSource.size());
    }

    public void addItems(List<SideMenuModel> items, int position)
    {
        dataSource.addAll(position, items);
        notifyDataSetChanged();
    }

    public long getItemId(int position)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        SideMenuCell cell;
        final SideMenuModel model = getItem(position);
        if (convertView == null) {
            cell = new SideMenuCell(mContext);
        } else {
            cell = (SideMenuCell) convertView;
        }
        cell.populate(model);

        return cell;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        ViewGroup headerCell = new GhostView(mContext);
        final SideMenuModel model = getItem(position);
        if (model.headerModel != null) {
            if (convertView == null || !(convertView instanceof SideMenuCell)) {
                headerCell = new SideMenuHeaderCell(mContext);
            } else if (convertView instanceof SideMenuCell){
                headerCell = (SideMenuHeaderCell) convertView;
            }
            SideMenuHeaderCell sideMenuHeaderCell = (SideMenuHeaderCell) headerCell;
            //sideMenuHeaderCell.populate(model.headerModel);
            sideMenuHeaderCell.editButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    toggleHeaderEditBar(model.headerModel);
                }
            });
            sideMenuHeaderCell.doneButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    toggleHeaderEditBar(model.headerModel);
                }
            });
            sideMenuHeaderCell.deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.headerDeleteButtonClicked(model.headerModel);
                }
            });
        }

        return headerCell;
    }

    private void toggleHeaderEditBar(SideMenuHeaderModel model)
    {
        model.isEditing = !model.isEditing;
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int position)
    {
        long id = 0;

        SideMenuModel model = getItem(position);
        if (model.headerModel != null) {
            id = model.headerModel.id;
        }

        return id;
    }

    public interface SideMenuListener
    {
        public void headerDeleteButtonClicked(SideMenuHeaderModel model);
    }
}
