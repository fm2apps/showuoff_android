package app.shouoff;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.shouoff.adapter.ModificationAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.ModificationModel;
import app.shouoff.model.ShopModel;
import app.shouoff.pageradapter.ShopImage_Adapter;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ShopDetails extends Drawer implements RetrofitResponse
{
    private ViewPager pager;
    private TextView count,buy_product;
    private Context context = this;
    private ShopImage_Adapter shopImage_adapter;
    private ShopModel shopModel;
    private TextView name, price, post_description,modification;
    private RecyclerView modification_list;
    private ArrayList<ModificationModel> modelArrayList=new ArrayList<>();
    private ModificationAdapter modificationAdapter;
    private android.support.v7.app.AlertDialog alertDialog;
    private double modification_price=0,product_price=0;
    private String temp="",customized_id="",customize="",price_update="",price_change="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopModel = (ShopModel) getIntent().getSerializableExtra("data");
        initialized();
        title1.setText("Product Details");

        new Retrofit2(context,this,1,Constants.Product_modification+shopModel.getId(),"").callService(true);
    }

    private void initialized()
    {
        buy_product=(TextView)findViewById(R.id.buy_product);
        pager = (ViewPager) findViewById(R.id.pager);
        modification = (TextView) findViewById(R.id.modification);
        count = (TextView) findViewById(R.id.count);
        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        post_description = (TextView) findViewById(R.id.post_description);

        pager();
        setData();

        buy_product.setOnClickListener(this);
        modification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.modification:
                if (modelArrayList.size()>0)
                {
                    showModificationDialog();
                }
                else
                {
                    Alerts.showDialog(context,"","No modification available this time");
                }
                break;
            case R.id.buy_product:
                    if (temp.equalsIgnoreCase("modify"))
                    {
                        confirmModification();
                    }
                    else
                    {
                        Alerts.showDialog(context,"","Development Mode");
                    }
                break;
        }
    }

    private void setData()
    {
        name.setText(shopModel.getName());
        price.setText("Price : "+shopModel.getCurrency()+" " + shopModel.getPrice());
        DataHandler.forContest(post_description,"Description : ",shopModel.getDescription());
    }

    private void pager() {
        shopImage_adapter = new ShopImage_Adapter(context, shopModel.getStrings());
        pager.setAdapter(shopImage_adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                count.setText(String.valueOf(shopModel.getStrings().size()) + "/" + (pager.getCurrentItem() + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        count.setText(String.valueOf(shopModel.getStrings().size()) + "/1");
    }

    public void showModificationDialog()
    {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.modification_layout, null);
        modification_list=(RecyclerView)dialogView.findViewById(R.id.modification_list);
        TextView done=(TextView)dialogView.findViewById(R.id.done);
        TextView cancel=(TextView)dialogView.findViewById(R.id.cancel);

        setAdapter();
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                logForUsersCustomizeProduct(customize);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                customize="";

                for (int i=0;i<modelArrayList.size();i++)
                {
                    modelArrayList.get(i).setaBoolean(false);
                }
                modificationAdapter.notifyDataSetChanged();
                product_price=0;
                price.setText("Price : "+shopModel.getCurrency()+" " + shopModel.getPrice());
                temp="not_modify";

                logForUsersCustomizeProduct(customize);
                alertDialog.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width,height);
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.show();
    }


    public void confirmModification()
    {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.payment_alert_message, null);
        modification_list=(RecyclerView)dialogView.findViewById(R.id.modification_list);

        TextView submit,cancel;
        submit=(TextView)dialogView.findViewById(R.id.submit);
        cancel=(TextView)dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                customize="";
                for (int i=0;i<modelArrayList.size();i++)
                {
                    modelArrayList.get(i).setaBoolean(false);
                }
                modificationAdapter.notifyDataSetChanged();
                product_price=0;

                price.setText("Price : "+shopModel.getCurrency()+" " + shopModel.getPrice());
                temp="not_modify";
                logForUsersCustomizeProduct(customize);
                alertDialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Alerts.showDialog(context,"","Development Mode");
                alertDialog.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        alertDialog.getWindow().setLayout(width,1000);
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.show();
    }

    private void setAdapter()
    {
        modification_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        modification_list.setLayoutManager(layoutManager);
        modificationAdapter = new ModificationAdapter(context,modelArrayList);
        modification_list.setAdapter(modificationAdapter);
        modificationAdapter.click(new ModificationAdapter.ShowModify()
        {
            @Override
            public void show(View view, int pos)
            {
                modelArrayList.get(pos).setaBoolean(true);
                for (int i=0;i<modelArrayList.size();i++)
                {
                    if (!modelArrayList.get(i).getId().equalsIgnoreCase(modelArrayList.get(pos).getId()))
                    {
                        modelArrayList.get(i).setaBoolean(false);
                    }
                }

                modification_price=Double.valueOf(modelArrayList.get(pos).getPrice());
                product_price=0;
                product_price=modification_price+Double.valueOf(shopModel.getPrice().replace(",",""));
                String priceSet= String.valueOf(product_price);
                price.setText("Price : "+shopModel.getCurrency()+" " + priceSet);
                modificationAdapter.notifyDataSetChanged();
                temp="modify";
                customize=modelArrayList.get(pos).getId();
            }
        });
    }

    private void listUsersCustomizeProduct()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("product_id",shopModel.getId());
            new Retrofit2(context,this,2, Constants.listUsersCustomizeProduct,object).callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void logForUsersCustomizeProduct(String customize)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("product_id",shopModel.getId());
            object.put("product_addon_id",customize);
            object.put("customize_id",customized_id);
            new Retrofit2(context,this,3, Constants.logForUsersCustomizeProduct,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
                        JSONObject data=result.getJSONObject("data");
                        JSONArray product_addon=data.getJSONArray("product_addon");
                        modelArrayList.clear();
                        listUsersCustomizeProduct();
                        if (product_addon.length()>0)
                        {
                            for (int i=0;i<product_addon.length();i++)
                            {
                                JSONObject value=product_addon.getJSONObject(i);
                                ModificationModel modificationModel=new ModificationModel(value.getString("id"),
                                        value.getString("name"),
                                        value.getString("price"),false,
                                        value.getString("currency_type"));
                                modelArrayList.add(modificationModel);
                            }
                            setAdapter();
                        }

                        break;
                    case 2:
                        JSONObject customize1=result.getJSONObject("data");
                        customized_id=customize1.getString("id");
                        price_update=customize1.getString("product_addon_id");
                        price_change=customize1.getJSONObject("users_product_addon_details").getString("price");
                        customize=price_update;
                        modification_price=Double.valueOf(price_change);
                        product_price=0;
                        product_price=modification_price+Double.valueOf(shopModel.getPrice().replace(",",""));
                        String priceSet= String.valueOf(product_price);
                        price.setText("Price : "+shopModel.getCurrency()+" " + priceSet);
                        temp="modify";
                        for (int i=0;i<modelArrayList.size();i++)
                        {
                            if (price_update.equalsIgnoreCase(modelArrayList.get(i).getId()))
                            {
                                modelArrayList.get(i).setaBoolean(true);
                            }
                        }
                        modificationAdapter.notifyDataSetChanged();

                        break;
                    case 3:
                        JSONObject customize11=result.getJSONObject("data");
                        customized_id=customize11.getString("id");
                        Log.e("DONE","DONE");
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
