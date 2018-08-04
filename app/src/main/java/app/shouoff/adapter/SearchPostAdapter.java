package app.shouoff.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import app.shouoff.R;
import app.shouoff.SearchUser;
import app.shouoff.common.Constants;
import app.shouoff.model.SearchDataModel;

public class SearchPostAdapter extends RecyclerView.Adapter<SearchPostAdapter.ViewHolder>
{
    Context context;
    ArrayList<SearchDataModel> dataModels;
    ArrayList<SearchDataModel> getDataModels=new ArrayList<>();
    SelectUser selectUser;

    public SearchPostAdapter(Context context,ArrayList<SearchDataModel> dataModels)
    {
        this.context=context;
        this.dataModels=dataModels;
        getDataModels.clear();
        getDataModels.addAll(dataModels);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_adapter, viewGroup, false);
        return new SearchPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.user_name.setText(dataModels.get(position).getNick());
        holder.user_email.setText(dataModels.get(position).getEmail());
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+dataModels.get(position).getImage())
                .placeholder(R.drawable.noimage).into(holder.profile_image);
    }

    @Override
    public int getItemCount()
    {
        return dataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public RoundedImageView profile_image;
        public TextView user_name,user_email;
        public ViewHolder(View itemView)
        {
            super(itemView);
            user_email=(TextView)itemView.findViewById(R.id.user_email);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            profile_image=(RoundedImageView)itemView.findViewById(R.id.profile_image);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    selectUser.show(view,getAdapterPosition());
                }
            });
        }
    }

    public void filterSnd(String text)
    {
        dataModels.clear();
        if (text.isEmpty())
        {
            dataModels.addAll(getDataModels);
        }
        else
        {
            text = text.toLowerCase();
            for (SearchDataModel item : getDataModels)
            {
                if (item.getNick().toLowerCase().contains(text)||
                        item.getUser_name().toLowerCase().contains(text))
                {
                    dataModels.add(item);
                }
            }
        }

        if (dataModels.size() > 0)
        {
            SearchUser.recycler.setVisibility(View.VISIBLE);
            SearchUser.error.setVisibility(View.GONE);
        }
        else
        {
            SearchUser.recycler.setVisibility(View.GONE);
            SearchUser.error.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    public void click(SelectUser selectUser)
    {
        this.selectUser=selectUser;
    }

    public interface SelectUser
    {
        void show(View view,int pos);
    }
}
