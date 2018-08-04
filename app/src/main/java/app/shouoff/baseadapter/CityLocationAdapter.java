package app.shouoff.baseadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.shouoff.R;
import app.shouoff.model.LocationModel;

public class CityLocationAdapter extends ArrayAdapter<LocationModel>
{
    Context context;
    int resource;
    List<LocationModel> gamesModels;
    public CityLocationAdapter(Context context, int resource, List<LocationModel> gamesModels)
    {
        super(context, resource,gamesModels);
        this.context = context;
        this.resource = resource;
        this.gamesModels = gamesModels;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
        }
        LocationModel people = gamesModels.get(position);
        if (people != null)
        {
            TextView textView=(TextView)view.findViewById(R.id.value);
            textView.setText(people.getName());
        }
        return view;
    }
}
