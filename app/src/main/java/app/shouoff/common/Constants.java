package app.shouoff.common;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.shouoff.R;

import static com.isseiaoki.simplecropview.util.Utils.isDownloadsDocument;
import static com.isseiaoki.simplecropview.util.Utils.isExternalStorageDocument;
import static com.isseiaoki.simplecropview.util.Utils.isMediaDocument;

public class Constants
{
    /*Base url*/
    public static final String BASE_URL="http://54.187.221.184:3006/";
    public static final String FILTER_IMAGE_URL="http://54.187.221.184/showuoff/public/app_filter_images/";
    public static final String PROFILE_IMAGE_URL="http://54.187.221.184/public/showuoff/user_images/";
    public static final String AWARDS_IMAGE_URL="http://54.187.221.184/public/showuoff/awards_image/";
    public static final String POST_URL="http://54.187.221.184/public/showuoff/post_files/";
    public static final String CATEGORY_URL="http://54.187.221.184/public/showuoff/mystore/category_image/";
    public static final String SHOP_PRODUCT_URL="http://54.187.221.184/public/showuoff/mystore/product_image/";
    public static final String CONTEST_UTL="http://54.187.221.184/public/showuoff/contest_files/";
    public static final String PROMO_VIDEO_URL="http://frikihubb.com/public/showuoff/promo_video/";
    public static final String ADS_VIDEO="http://54.187.221.184/showuoff/public/advertisments/";

    /*Services*/
    public static final String countryGame="countryGame";
    public static final String register="register";
    public static final String login="login";
    public static final String ForgetPassword="ForgetPassword";
    public static final String countryCity="countryCity";
    public static final String UpdateUser="UpdateUser";
    public static final String getAwards="getAwards";
    public static final String createpost="createpost";
    public static final String deleteFiles="deleteFiles";
    public static final String discription="discription";
    public static final String About_Us="About_Us";
    public static final String listUsers="listUsers";
    public static final String profileSearch="profileSearch";
    public static final String postList="postList";
    public static final String perticularUserPost="perticularUserPost";
    public static final String CurrentMonth_qualifiedPosts="CurrentMonth_qualifiedPosts";
    public static final String highlyLikedPost="highlyLikedPost";
    public static final String createcomment="createcomment";
    public static final String postlike="postlike";
    public static final String postunlike="postunlike";
    public static final String perticular_post="perticular_post/";
    public static final String contact_support="contact_support";
    public static final String FansReq="FansReq";
    public static final String UserUnsubscribe="UserUnsubscribe";
    public static final String Post_report_byUser="Post_report_byUser";
    public static final String User_sharing_post="User_sharing_post";
    public static final String User_got_Shared_post="User_got_Shared_post/";
    public static final String CountsOf_post_Fans_contasts="CountsOf_post_Fans_contasts/";
    public static final String Myfollowers_list="Myfollowers_list/";
    public static final String myStore_Categories="myStore_Categories";
    public static final String mystore_productList="mystore_productList/";
    public static final String Product_modification="Product_modification/";
    public static final String MonthlyWinner="MonthlyWinner/";
    public static final String postList_filter="postList_filter";
    public static final String User_see_AllContest="User_see_AllContest/";
    public static final String UserApplyingToContest="UserApplyingToContest";
    public static final String Compititor_list="Compititor_list";
    public static final String User_contestDetails="User_contestDetails/";
    public static final String deletecomment="deletecomment";
    public static final String offensive_Words="offensive_Words";
    public static final String Terms="Terms";
    public static final String Update_password="Update_password";
    public static final String create_user_device_info="create_user_device_info";
    public static final String RegisterationNotification="RegisterationNotification/";
    public static final String DeleteDevice="DeleteDevice";
    public static final String MyNotifications="MyNotifications/";
    public static final String PostDetailsByPostId="PostDetailsByPostId";
    public static final String UserToggleNotification="UserToggleNotification";
    public static final String listUsersCustomizeProduct="listUsersCustomizeProduct";
    public static final String logForUsersCustomizeProduct="logForUsersCustomizeProduct";
    public static final String UserDeletingNotifications="UserDeletingNotifications";
    public static final String ReadStatusOfNotification="ReadStatusOfNotification";
    public static final String PrivacyPolicy="PrivacyPolicy";
    public static final String Myfollowing_list="Myfollowing_list/";
    public static final String SEARCH_POST="Post_searching";

    public static final String Post_delete="Post_delete/";
    public static final String User_editPost="User_editPost";
    public static final String User_winningDetails="User_winningDetails/";
    public static final String otp_verification="otp_verification";
    public static final String user_report="user_report";
    public static final String User_qualifiedPost="User_qualifiedPost";
    public static final String UserAutoLogoutIfAdminDeactivate="UserAutoLogoutIfAdminDeactivate/";
    public static final String LogoutOldDeviceIfNewfound="LogoutOldDeviceIfNewfound";
    public static final String Hash_Words="Hash_Words";
    public static final String PromoVideo="PromoVideo";
    public static final String UserInviteReq="UserInviteReq";
    public static final String InviteResponse="InviteResponse";
    public static final String USER_BLOCK="blockUser";
    public static final String DELETE_ACCOUNT="DeleteUserAccount";
    public static final String LIKED_USER="WhoAllLikedThePost";
    public static final String HASHTAG_POST="NewHashReatedData";

    public static final String BLOCKED_USERS="GetAllBlockedUsers/";
    public static final String UNBLOCK_USER="UnblockUser";

    public static final String getRandomAdvertisement="getRandomAdvertisement/";
    public static final String incrementAdvertisementClicks="incrementAdvertisementClicks";
    public static final String incrementAdvertisementViews="incrementAdvertisementViews";

    /*Content*/
    public static final String NAME="name";
    public static final String ID="id";
    public static final String FAMILY_NAME="f_name";
    public static final String DOB="dob ";
    public static final String EMAIL="email";
    public static final String NICK_NAME="nick";
    public static final String SCHOOL_NAME="school";
    public static final String USER_NAME="user_name";
    public static final String GAME="game";
    public static final String CITY="city";
    public static final String COUNTRY="country";
    public static final String Image="image";
    public static final String ABOUT="about";
    public static final String COUNTRY_ID="country_id";
    public static final String CITY_ID="city_id";
    public static final String GAMES_NAME="games_name";
    public static final String FATHER="father";
    public static final String GENDER ="other";
    public static final String CONTACT="contact";

    /*To generate device id for fcm Used on Login/Register Screen*/
    public static String getUniqueId(Context mContext)
    {
        String android_id = "";
        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    //To generate device id for fcm Used on Dashboard Screen
    public static String getFcmDeviceID(Context mContext)
    {
        String refreshedToken = "";
        if (SharedPreference.retrieveFcmDeviceId(mContext) == null) {
            refreshedToken = FirebaseInstanceId.getInstance().getToken();
            SharedPreference.storeFcmDeviceId(mContext, refreshedToken);
            return refreshedToken;
        } else {
            refreshedToken = SharedPreference.retrieveFcmDeviceId(mContext);
        }
        return refreshedToken;
    }

    /*Find null value*/
    public static String setNullValue(JSONObject value,String result)
    {
        String data="";
        try {
            data = value.getString(result);
            if (data.equalsIgnoreCase("null") || data.equalsIgnoreCase(""))
            {
                data="";
            }
            else
            {
                return data;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    /*Edittext drawable */
    public static void focus(EditText editText,boolean hasFocus, int rec_focus, int rec_unselect)
    {
        if (hasFocus)
        {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0,rec_focus, 0);
        }
        else
        {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0,rec_unselect, 0);
        }
    }

    public static void showKeyboard(EditText yourEditText, Activity activity) {
        try {
            InputMethodManager input = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            input.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Context context, View view)
    {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view!=null)
        {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    // TODO: 12/3/18 Check Duplicate Items
    public static String checkRepeated(String s)
    {
        String output="";
        try
        {
            String input=s;
            String[] words=input.split(", ");
            for(int i=0;i<words.length;i++)
            {
                if(words[i]!=null)
                {
                    String tmp_va=words[i];
                    for(int j=i+1;j<words.length;j++)
                    {
                        if(tmp_va.equals(words[j]))
                        {
                            words[j]=null;
                            words[i]=null;
                        }
                    }
                }
            }
            for(int k=0;k<words.length;k++)
            {
                if(words[k]!=null)
                {
                    if (k<words.length-1)
                    {
                        output = output + words[k] + ", ";
                    }
                    else
                    {
                        output = output + words[k];
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }

    /*Todo saving image*/
    public static void saveImage(Bitmap bitmap)
    {
        File storagepath = new File(Environment.getExternalStorageDirectory() + "/photos/");
        storagepath.mkdirs();

        File image = new File(storagepath, Long.toString(System.currentTimeMillis()) + ".jpg");

        try {
            FileOutputStream outputStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: 3/8/17 get path  for video
    public static String getRealPathFromURIPath(Uri contentURI, Activity activity)
    {
        Cursor cursor = activity.getContentResolver().query(contentURI, null,
                null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    /*Path for video thumbnail*/
    public static File pathImage(Intent data, Context context)
    {
        String imagepath;

        Uri selectedFileURI = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedFileURI, filePathColumn, null, null, null);

        if (cursor==null)
        {
            imagepath = selectedFileURI.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int colIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagepath = cursor.getString(colIndex);
        }

        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imagepath,
                MediaStore.Images.Thumbnails.MINI_KIND);

        File file;
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        file=new File(dir, System.currentTimeMillis() + ".png");
        if (file.exists())
        {
            file.delete();
        }
        file=new File(dir, System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(file.getPath());
            thumb.compress(Bitmap.CompressFormat.PNG, 55, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                {
                    out.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return file;
    } /*Path for video thumbnail*/

    public static File pathImage(String data)
    {

        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(data,
                MediaStore.Images.Thumbnails.MINI_KIND);

        File file;
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        file=new File(dir, System.currentTimeMillis() + ".png");
        if (file.exists())
        {
            file.delete();
        }
        file=new File(dir, System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(file.getPath());
            thumb.compress(Bitmap.CompressFormat.PNG, 55, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                {
                    out.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return file;
    }

    /*Get date format*/
    public static String date(String time)
    {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputPattern = "MM/dd/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = "";
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /*Get date format*/
    public static String monthYear(String time)
    {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputPattern = "MMM/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = "";
        try
        {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return str;
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getTimeAgo(long time,Context context)
    {
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return context.getString(R.string.time_just_now);
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getString(R.string.time_just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getString(R.string.time_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " "+context.getString(R.string.time_min_ago);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getString(R.string.time_an_hr_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " "+context.getString(R.string.time_hr_ago);
        }
        else
        {
            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(time);
            return DateFormat.format("MM/dd/yyyy", smsTime).toString();
        }
    }

    public static DatePickerDialog.OnDateSetListener date_picker(final TextView textView, final java.util.Calendar myCalendar) {
        final DatePickerDialog.OnDateSetListener datee;

        datee = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(java.util.Calendar.YEAR,year);
                myCalendar.set(java.util.Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                textView.setText(sdf.format(myCalendar.getTime()));
            }
        };
        return datee;
    }

    public static void freeMemory(){
        /*System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();*/
    }

    public static void alphabet(EditText editText)
    {
        editText.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[a-zA-Z ]+")){
                            return src;
                        }
                        return "";
                    }
                }
        });
    }
}
