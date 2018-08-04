package app.shouoff;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import app.shouoff.adapter.ShopAdapter;
import app.shouoff.adapter.ShopCategoryAdapter;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.ShopCategoriesModel;
import app.shouoff.model.ShopModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ShopNow extends Drawer implements RetrofitResponse
{
    Context context=this;
    public static TextView error;
    public static RecyclerView shop_categories,shop_products;
    private ArrayList<ShopModel> models=new ArrayList<>();
    private ArrayList<ShopCategoriesModel> categoriesModels=new ArrayList<>();
    private ShopAdapter shopAdapter;
    private ShopCategoryAdapter categoryAdapter;
    private EditText search_data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_now);

        initialized();
        title1.setText("Shop Now");

        new Retrofit2(context,this,1,Constants.myStore_Categories,"").callService(true);
    }

    private void initialized()
    {
        error=(TextView)findViewById(R.id.error);
        search_data=(EditText)findViewById(R.id.search_data);
        shop_products=(RecyclerView)findViewById(R.id.shop_products);
        shop_categories=(RecyclerView)findViewById(R.id.shop_categories);
        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.user,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop_more,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact);

        ((Drawer)context).showText(creat_post1,my_post,search_user,my_profile,highly_liked,contest,prefrence,winners,about,contact_support);
        showView(create_view,search_view,profile_view,liked_view,post_view,contests_view,pref_view,winner_view,about_view,support_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,R.drawable.profile_gray,R.drawable.creatpost_gray,R.drawable.notification_gray,R.drawable.more_colord,more,profile,discovery,notification,creat_post);

        search_data.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                shopAdapter.filterSnd(editable.toString());
            }
        });


        search_data.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId != 0 || event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    Constants.hideKeyboard(context,v);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        shopAdapter();
        categoryAdapter();
    }

    private void shopAdapter()
    {
        shop_products.setHasFixedSize(true);
        StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        shop_products.setLayoutManager(gridLayoutManager);
        shopAdapter = new ShopAdapter(context,models);
        shop_products.setAdapter(shopAdapter);
        shop_products.setNestedScrollingEnabled(false);
    }

    private void categoryAdapter()
    {
        shop_categories.setHasFixedSize(true);
        StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL);
        shop_categories.setLayoutManager(gridLayoutManager);
        categoryAdapter= new ShopCategoryAdapter(context,categoriesModels);
        shop_categories.setAdapter(categoryAdapter);
        shop_categories.setNestedScrollingEnabled(false);
        categoryAdapter.click(new ShopCategoryAdapter.ShowProduct()
        {
            @Override
            public void show(View view, int pos)
            {
                categoriesModels.get(pos).setaBoolean(true);
                for (int i=0;i<categoriesModels.size();i++)
                {
                    if (!categoriesModels.get(pos).getId().equalsIgnoreCase(categoriesModels.get(i).getId()))
                    {
                        categoriesModels.get(i).setaBoolean(false);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
                getShopProductService(pos);
            }
        });
    }

    private void getShopProductService(int pos)
    {
        new Retrofit2(context,this,2,Constants.mystore_productList+categoriesModels.get(pos).getId(),"").callService(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try
        {
            JSONObject result=new JSONObject(response.body().string().toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 1:
                        categoriesModels.clear();
                        JSONArray data=result.getJSONArray("data");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject value=data.getJSONObject(i);
                                ShopCategoriesModel categoriesModel=new ShopCategoriesModel(value.getString("id"),
                                        value.getString("name"),
                                        value.getString("image"),false);
                                categoriesModels.add(categoriesModel);
                            }

                            categoriesModels.get(0).setaBoolean(true);
                            categoryAdapter();
                            getShopProductService(0);
                        }
                        break;
                    case 2:
                        models.clear();
                        JSONArray product=result.getJSONArray("data");
                        if (product.length()>0)
                        {
                            for (int i=0;i<product.length();i++)
                            {
                                JSONObject value=product.getJSONObject(i);

                                ArrayList<String> images=new ArrayList<>();
                                JSONArray image=value.getJSONArray("product_image");
                                if (image.length()>0)
                                {
                                    for (int j=0;j<image.length();j++)
                                    {
                                        JSONObject imageValue=image.getJSONObject(j);
                                        images.add(imageValue.getString("image"));
                                    }
                                }

                                ShopModel model=new ShopModel(value.getString("id"),
                                        value.getString("name"),
                                        value.getString("price"),
                                        value.getString("description"),
                                        images,
                                        value.getString("category_id"),
                                        value.getString("currency_type"));
                                models.add(model);
                            }
                            shopAdapter();
                            shop_products.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            shop_products.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
