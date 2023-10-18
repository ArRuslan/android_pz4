package me.ruslan.task4.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.List;

import me.ruslan.task4.R;
import me.ruslan.task4.models.Note;

public class NotesListAdapter extends ArrayAdapter<Note> implements Filterable {
    private ArrayList<Note> dataSet;
    private ArrayList<Note> dataSetFiltered;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtText;
        TextView txtTime;
        ImageView imgPriority;
        ImageView imgIcon;
    }

    public NotesListAdapter(ArrayList<Note> data, Context context) {
        super(context, R.layout.notes_list, data);
        dataSetFiltered = data;
        dataSet = new ArrayList<>(data);
        this.mContext = context;

    }

    private Drawable getPriorityDrawable(int priority) {
        switch (priority) {
            case 1:
                return AppCompatResources.getDrawable(getContext(), R.drawable.arrow_down);
            case 4:
                return AppCompatResources.getDrawable(getContext(), R.drawable.arrow_up);
            default:
                return AppCompatResources.getDrawable(getContext(), R.drawable.line);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notes_list, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.title);
            viewHolder.txtText = convertView.findViewById(R.id.text);
            viewHolder.txtTime = convertView.findViewById(R.id.time);
            viewHolder.imgPriority = convertView.findViewById(R.id.priority);
            viewHolder.imgIcon = convertView.findViewById(R.id.icon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(note.getTitle());
        viewHolder.txtText.setText(note.getText() == null ? "(null)" : note.getText());
        viewHolder.txtTime.setText(note.getTime());
        viewHolder.imgPriority.setImageDrawable(getPriorityDrawable(note.getPriority()));
        if (note.getImage() == null) {
            viewHolder.imgIcon.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.no_image));
        } else {
            viewHolder.imgIcon.setImageURI(note.getImage());
        }
        return convertView;
    }

    public void filter(String query, int priority) {
        ArrayList<Note> result = new ArrayList<>();

        for(Note note : dataSet) {
            if(!query.isEmpty() && (!note.getTitle().contains(query) && !note.getText().contains(query)))
                continue;
            if(priority != 0 && priority != 7 && ((note.getPriority() & priority) != note.getPriority()))
                continue;
            result.add(note);
        }

        dataSetFiltered.clear();
        dataSetFiltered.addAll(result);
        notifyDataSetChanged();
    }
}