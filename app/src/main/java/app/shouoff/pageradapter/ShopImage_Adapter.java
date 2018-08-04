package app.shouoff.pageradapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;
import app.shouoff.R;
import app.shouoff.common.Constants;

public class ShopImage_Adapter extends PagerAdapter
{
    private List<String> listModelList;
    private LayoutInflater inflater;
    private Context context;

    public ShopImage_Adapter(Context context, List<String> listModelList)
    {
        this.context = context;
        this.listModelList=listModelList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listModelList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position)
    {
        View imageLayout = inflater.inflate(R.layout.storefile_adapter, view, false);
        ImageView post_image=(ImageView) imageLayout.findViewById(R.id.post_image);

        Picasso.with(context).load(Constants.SHOP_PRODUCT_URL+listModelList.get(position)).placeholder(R.drawable.noimage).into(post_image);

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}

