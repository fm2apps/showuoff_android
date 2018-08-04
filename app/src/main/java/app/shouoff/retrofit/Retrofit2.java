package app.shouoff.retrofit;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import app.shouoff.R;
import app.shouoff.common.Alerts;
import app.shouoff.common.ConnectionDetector;
import app.shouoff.common.Constants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2
{
    private Call<ResponseBody> call;
    private RetrofitResponse result;
    private JSONObject postParam;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private int requestCode;
    private String url;
    private static Dialog pd;
    private MultipartBody.Part part,part2;
    private HashMap<String, RequestBody> map;
    private String temp;

    /*********************For Sync Adapter *************************/

    public Retrofit2(Context mContext, RetrofitResponse result, int requestCode, String url)
    {
        this.mContext = mContext;
        this.result = result;
        this.requestCode = requestCode;
        this.url = url;
    }

    /***************** For Normal Service *********************/

    public Retrofit2(Context mContext, RetrofitResponse result, int requestCode, String url, JSONObject postParam)
    {
        this.mContext = mContext;
        this.result = result;
        this.requestCode = requestCode;
        this.url = url;
        this.postParam = postParam;
        pd = new Dialog(mContext);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setContentView(R.layout.progress_bar);
        Objects.requireNonNull(pd.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    public Retrofit2(Context mContext, RetrofitResponse result, int requestCode, String url,String temp)
    {
        this.mContext = mContext;
        this.result = result;
        this.requestCode = requestCode;
        this.url = url;
        pd = new Dialog(mContext);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setContentView(R.layout.progress_bar);
        Objects.requireNonNull(pd.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    //For single file send
    public Retrofit2(Context mContext, RetrofitResponse result , int requestCode, String url, HashMap<String, RequestBody> map, MultipartBody.Part part, String temp)//for POST URL
    {
        this.mContext = mContext;
        this.result = result;
        this.map = map;
        this.part = part;
        this.requestCode = requestCode;
        this.url = url;
        this.temp = temp;

        pd = new Dialog(mContext);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setContentView(R.layout.progress_bar);
        Objects.requireNonNull(pd.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    //For single file send
    public Retrofit2(Context mContext, RetrofitResponse result , int requestCode, String url,MultipartBody.Part part)//for POST URL
    {
        this.mContext = mContext;
        this.result = result;
        this.part = part;
        this.requestCode = requestCode;
        this.url = url;

        pd = new Dialog(mContext);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setContentView(R.layout.progress_bar);
        Objects.requireNonNull(pd.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    //For Double file send
    public Retrofit2(Context mContext, RetrofitResponse result , int requestCode, String url, HashMap<String, RequestBody> map,
                     MultipartBody.Part part,MultipartBody.Part part2, String temp)
    {
        this.mContext = mContext;
        this.result = result;
        this.map = map;
        this.part = part;
        this.part2=part2;
        this.requestCode = requestCode;
        this.url = url;
        this.temp = temp;

        pd = new Dialog(mContext);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setContentView(R.layout.progress_bar);
        Objects.requireNonNull(pd.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void callService(boolean dialog)
    {
        if (!ConnectionDetector.isInternetAvailable(mContext))
        {
            Alerts.showDialog(mContext,"Oops!","Please check Your Internet Connection");
            return;
        }

        if (dialog)
        {
            pd.show();
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();

        System.setProperty("http.keepAlive", "false");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Log.e("URL", "URL- " + url);
         if (postParam == null)
        {
            call = retrofitService.callGetService(url);
        }
        else
        {
            Log.e("Params", "Params- " + postParam.toString());
            call = retrofitService.callPostService(url, RequestBody.create(MediaType.parse("application/json"), postParam.toString()));
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> callback, Response<ResponseBody> response)
            {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                if (response.isSuccessful()) {
                    result.onServiceResponse(requestCode, response);
                } else {
                    Log.e("ERROR", "ERROR");
                    Alerts.showDialog(mContext, mContext.getString(R.string.sorry),
                            mContext.getResources().getString(R.string.Connection_Time_Out));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                call.cancel();
                Toast.makeText(mContext, "Server Connection Lost", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callServiceMultipart(boolean dialog)
    {
        if (!ConnectionDetector.isInternetAvailable(mContext))
        {
            Alerts.showDialog(mContext,"Oops!","Please check Your Internet Connection");
            return;
        }

        if (dialog) {
            pd.show();
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()//Use For Time Out
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .build();

        System.setProperty("http.keepAlive", "false");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Log.e("URL", "URL- " + url);

        if (temp.equalsIgnoreCase("1"))
        {
            call = retrofitService.callMultipartService(url,map,part);
        }
        else if (temp.equalsIgnoreCase("2"))
        {
            call=retrofitService.callMultipartService(url,map,part,part2);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> callback, Response<ResponseBody> response) {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                if (response.isSuccessful()) {
                    result.onServiceResponse(requestCode, response);
                } else {
                    Log.e("ERROR", "ERROR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String err = (t.getMessage() == null) ? "SD Card failed" : t.getMessage();
                Log.e("t", err);
                if (pd.isShowing()) {
                    pd.cancel();
                }
                call.cancel();
                Toast.makeText(mContext, "Server Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callServiceBack()
    {
        if (!ConnectionDetector.isInternetAvailable(mContext))
        {
            Alerts.showDialog(mContext,"Oops!","Please check Your Internet Connection");
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()//Use For Time Out
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();

        System.setProperty("http.keepAlive", "false");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Log.e("URL", "URL- " + url);
        if (postParam == null)
        {
            call = retrofitService.callGetService(url);
        }

        else
        {
            Log.e("Params", "Params- " + postParam.toString());
            call = retrofitService.callPostService(url, RequestBody.create(MediaType.parse("application/json"), postParam.toString()));
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> callback, Response<ResponseBody> response)
            {
                if (response.isSuccessful()) {
                    result.onServiceResponse(requestCode, response);
                } else {
                    Log.e("ERROR", "ERROR");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                call.cancel();
            }
        });
    }
}
