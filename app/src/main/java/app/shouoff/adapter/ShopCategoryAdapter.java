package app.shouoff.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.ShopCategoriesModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShopCategoryAdapter extends RecyclerView.Adapter<ShopCategoryAdapter.ViewHolder>
{
    Context context;
    ArrayList<ShopCategoriesModel> shopModels;
    ShowProduct showProduct;

    public ShopCategoryAdapter(Context context, ArrayList<ShopCategoriesModel> shopModels)
    {
        this.context = context;
        this.shopModels = shopModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_cat_adapter, viewGroup, false);
        return new ShopCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if (shopModels.get(position).isaBoolean())
        {
            holder.cat_image.setAlpha((float) 0.5);
        }
        else
        {
            holder.cat_image.setAlpha((float) 1);
        }
        holder.cat_name.setText(shopModels.get(position).getName());
        Picasso.with(context).load(Constants.CATEGORY_URL+shopModels.get(position).getImage())
                .placeholder(R.drawable.noimage).into(holder.cat_image);
    }

    @Override
    public int getItemCount()
    {
        return shopModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView cat_image;
        public TextView cat_name;

        public ViewHolder(View itemView)
        {
            super(itemView);
            cat_name=(TextView)itemView.findViewById(R.id.cat_name);
            cat_image=(CircleImageView)itemView.findViewById(R.id.cat_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProduct.show(view,getAdapterPosition());
                }
            });
        }
    }

    public void click(ShowProduct showProduct)
    {
        this.showProduct=showProduct;
    }

    public interface ShowProduct
    {
        void show(View view,int pos);
    }
}
