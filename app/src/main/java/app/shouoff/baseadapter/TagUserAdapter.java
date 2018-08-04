package app.shouoff.baseadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.SearchDataModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class TagUserAdapter extends ArrayAdapter<SearchDataModel>
{
    Context context;
    int textViewResourceId;
    List<SearchDataModel> items, tempItems, suggestions;

    public TagUserAdapter(Context context,int textViewResourceId, List<SearchDataModel> items)
    {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<SearchDataModel>(items);
        suggestions = new ArrayList<SearchDataModel>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(textViewResourceId, parent, false);
        }
        SearchDataModel people = items.get(position);
        if (people != null)
        {
            TextView textView=(TextView)view.findViewById(R.id.user_name);
            textView.setText(people.getNick());
            CircleImageView imageView=(CircleImageView)view.findViewById(R.id.profile_image);
            Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+people.getImage())
                    .placeholder(R.drawable.noimage).into(imageView);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter()
    {
        @Override
        public CharSequence convertResultToString(Object resultValue)
        {
            String str = ((SearchDataModel) resultValue).getUser_name();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            if (constraint != null) {
                suggestions.clear();
                for (SearchDataModel people : tempItems) {
                    if (people.getUser_name().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            List<SearchDataModel> filterList = (ArrayList<SearchDataModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (SearchDataModel people : filterList)
                {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}