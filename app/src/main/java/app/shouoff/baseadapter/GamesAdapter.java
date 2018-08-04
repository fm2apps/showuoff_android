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
import app.shouoff.model.GamesModel;

public class GamesAdapter extends ArrayAdapter<GamesModel>
{

    Context context;
    int resource;
    List<GamesModel> gamesModels;
    public GamesAdapter(Context context, int resource,List<GamesModel> gamesModels)
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
        GamesModel people = gamesModels.get(position);
        if (people != null)
        {
            TextView textView=(TextView)view.findViewById(R.id.value);
            textView.setText(people.getName());
        }
        return view;

    }
}
