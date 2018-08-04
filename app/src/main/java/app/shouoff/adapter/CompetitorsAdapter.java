package app.shouoff.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import app.shouoff.PostDetails;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.HomePostModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompetitorsAdapter extends RecyclerView.Adapter<CompetitorsAdapter.ViewHolder>
{
    ArrayList<HomePostModel> listModels;
    Context context;

    public CompetitorsAdapter(Context context, ArrayList<HomePostModel> listModels)
    {
        this.context=context;
        this.listModels=listModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.competitors_layout, viewGroup, false);
        return new CompetitorsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.user_name.setText(listModels.get(position).getFirst_name()+" "+listModels.get(position).getFamily_name());
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+listModels.
                get(position).getImage()).placeholder(R.drawable.noimage).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView image;
        public TextView user_name,view_more;

        public ViewHolder(View itemView)
        {
            super(itemView);

            image=(CircleImageView)itemView.findViewById(R.id.image);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            view_more=(TextView)itemView.findViewById(R.id.view_more);

            view_more.setOnClickListener(new View.OnClickListener()
             {
                @Override
                public void onClick(View view)
                {
                    try
                    {
                        context.startActivity(new Intent(context, PostDetails.class)
                                .putExtra("post_data",listModels.get(getAdapterPosition()))
                        .putExtra("from_comp","from_comp"));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
}
