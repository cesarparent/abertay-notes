package com.cesarparent.netnotes.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cesarparent.netnotes.CPApplication;
import com.cesarparent.netnotes.R;

import org.w3c.dom.Text;

/**
 * Created by cesar on 05/03/2016.
 */
public class NotesAdapter extends BaseAdapter {
    
    private Model _model;
    private LayoutInflater _inflater;
    
    public NotesAdapter(@NonNull Context context, @NonNull Model model) {
        _model = model;
        _inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return _model.getNotesCount();
    }

    @Override
    public Object getItem(int position) {
        return _model.getHandleAtIndex(position).title;
    }

    @Override
    public long getItemId(int position) {
        return _model.getHandleAtIndex(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
            view = _inflater.inflate(R.layout.view_note_list_item, null);
        }
        TextView titleView = (TextView)view.findViewById(R.id.noteTitle);
        titleView.setText(_model.getHandleAtIndex(position).title);
        return view;
    }
}
