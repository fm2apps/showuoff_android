package app.shouoff.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.shouoff.R;
import app.shouoff.ShopDetails;
import app.shouoff.ShopNow;
import app.shouoff.common.Constants;
import app.shouoff.model.ShopModel;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder>
{
    Context context;
    ArrayList<ShopModel> shopModels;
    ArrayList<ShopModel> getDataModels=new ArrayList<>();

    public ShopAdapter(Context context,ArrayList<ShopModel> shopModels)
    {
        this.context=context;
        this.shopModels=shopModels;
        getDataModels.clear();
        getDataModels.addAll(shopModels);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_adapter, viewGroup, false);
        return new ShopAdapter.ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Picasso.with(context).load(Constants.SHOP_PRODUCT_URL+shopModels.get(position).getStrings().get(0)).placeholder(R.drawable.noimage).into(holder.product_image);
    }

    @Override
    public int getItemCount() {
        return shopModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView product_image;
        public ViewHolder(View itemView)
        {
            super(itemView);
            product_image=(ImageView)itemView.findViewById(R.id.product_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ShopDetails.class)
                    .putExtra("data",shopModels.get(getAdapterPosition())));
                }
            });
        }
    }

    public void filterSnd(String text)
    {
        shopModels.clear();
        if (text.isEmpty())
        {
            shopModels.addAll(getDataModels);
        }
        else
        {
            text = text.toLowerCase();
            for (ShopModel item : getDataModels)
            {
                if (item.getName().toLowerCase().contains(text))
                {
                    shopModels.add(item);
                }
            }
        }

        if (shopModels.size() > 0)
        {
            ShopNow.shop_products.setVisibility(View.VISIBLE);
            ShopNow.error.setVisibility(View.GONE);
        }
        else
        {
            ShopNow.shop_products.setVisibility(View.GONE);
            ShopNow.error.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }
}
