package com.scube.Gondor.Search.models;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.scube.Gondor.Search.views.SearchCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vashoks on 05/30/15.
 */
public class SearchAdapter extends BaseAdapter
{
    private ArrayList dataSource = new ArrayList();

    private Context mContext;

    public SearchAdapter(Context context)
    {
        init(context);
    }

    public void init(Context context)
    {
        mContext = context;
    }

    @Override
    public int getCount()
    {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position)
    {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    public void addItems(List items)
    {
        dataSource.addAll(items);
        notifyDataSetChanged();
    }

    public void clear()
    {
        dataSource.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Object model = getItem(position);
        if (model instanceof SearchModel) {
            convertView = getSearchCell((SearchModel) model, convertView);
        }

        return convertView;
    }

    private SearchCell getSearchCell(SearchModel model, View convertView)
    {
        SearchCell searchCell;
        if (convertView == null || !(convertView instanceof SearchCell)) {
            searchCell = new SearchCell(mContext);
        } else {
            searchCell = (SearchCell) convertView;
        }
        searchCell.populate(model);

        return searchCell;
    }

    public void replaceItemWithItems(Object objectToReplace, ArrayList objects)
    {
        int indexOfObject = dataSource.indexOf(objectToReplace);
        dataSource.remove(indexOfObject);
        dataSource.addAll(indexOfObject, objects);
        notifyDataSetChanged();
    }

    public int getIndexOfObject(Object object)
    {
        return dataSource.indexOf(object);
    }
}
