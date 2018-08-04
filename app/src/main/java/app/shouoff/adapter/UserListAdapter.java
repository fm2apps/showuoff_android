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
import app.shouoff.common.Constants;
import app.shouoff.model.SearchDataModel;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>
{
    Context context;
    ArrayList<SearchDataModel> dataModels;
    ArrayList<SearchDataModel> getDataModels=new ArrayList<>();
    UserSelect userSelect;

    public UserListAdapter(Context context, ArrayList<SearchDataModel> dataModels)
    {
        getDataModels.clear();
        getDataModels.addAll(dataModels);
        this.context = context;
        this.dataModels = dataModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_adapter, viewGroup, false);
        return new UserListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        if (dataModels.get(position).isaBoolean())
        {
            holder.share_select.setText("Added");
        }
        else
        {
            holder.share_select.setText("Share");
        }

        holder.user_name.setText(dataModels.get(position).getFamily_name());
        holder.user_email.setText(dataModels.get(position).getEmail());
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+dataModels.get(position).getImage())
                .placeholder(R.drawable.noimage).into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public RoundedImageView profile_image;
        public TextView user_name,user_email,share_select;

        public ViewHolder(View itemView)
        {
            super(itemView);
            share_select=(TextView)itemView.findViewById(R.id.share_select);
            user_email=(TextView)itemView.findViewById(R.id.user_email);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            profile_image=(RoundedImageView)itemView.findViewById(R.id.profile_image);

            share_select.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    userSelect.show(view,getAdapterPosition());
                }
            });
        }
    }

    public void click(UserSelect userSelect)
    {
        this.userSelect=userSelect;
    }

    public interface UserSelect
    {
        void show(View view,int pos);
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
                if (item.getName().toLowerCase().contains(text)||item.getEmail().toLowerCase().contains(text))
                {
                    dataModels.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
