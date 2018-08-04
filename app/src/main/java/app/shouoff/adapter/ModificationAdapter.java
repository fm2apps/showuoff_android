package app.shouoff.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import app.shouoff.R;
import app.shouoff.model.ModificationModel;

public class ModificationAdapter extends RecyclerView.Adapter<ModificationAdapter.ViewHolder>
{
    ArrayList<ModificationModel> listModels;
    Context context;
    ShowModify modify;

    public ModificationAdapter(Context context, ArrayList<ModificationModel> listModels)
    {
        this.context=context;
        this.listModels=listModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.modofication_layout, viewGroup, false);
        return new ModificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if (listModels.get(position).isaBoolean())
        {
            holder.tick.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.tick.setVisibility(View.GONE);
        }

        holder.name.setText(listModels.get(position).getName());
        holder.price.setText(listModels.get(position).getCurrency()+" "+listModels.get(position).getPrice());
    }

    @Override
    public int getItemCount()
    {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name,price;
        public ImageView tick;
        public ViewHolder(View itemView)
        {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            price=(TextView)itemView.findViewById(R.id.price);
            tick=(ImageView)itemView.findViewById(R.id.tick);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    modify.show(view,getAdapterPosition());
                }
            });
        }
    }

    public void click(ShowModify modify)
    {
        this.modify=modify;
    }

    public interface ShowModify
    {
        void show(View view,int pos);
    }
}
