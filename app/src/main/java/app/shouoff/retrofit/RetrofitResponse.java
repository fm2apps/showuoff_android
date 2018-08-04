package app.shouoff.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Response;

public interface RetrofitResponse
{
    void onServiceResponse(int requestCode, Response<ResponseBody> response);
}