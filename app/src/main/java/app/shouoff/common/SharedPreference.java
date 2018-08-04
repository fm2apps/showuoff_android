package app.shouoff.common;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreference
{
    private static SharedPreferences sharedPreference;
    private static SharedPreferences.Editor editor;

    public static void storeData(Context context, String key, String value)
    {
        sharedPreference=context.getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor=sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String retriveData(Context context, String Key)
    {
        sharedPreference=context.getSharedPreferences("Info", Context.MODE_PRIVATE);
        return sharedPreference.getString(Key,null);
    }

    public static void removeAll(Context context)
    {
        sharedPreference = context.getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor=sharedPreference.edit();
        editor.clear();
        editor.commit();
    }

    public static void removeKey(Context context, String Key)
    {
        sharedPreference = context.getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor=sharedPreference.edit();
        editor.remove(Key);
        editor.commit();
    }

    public static void storeAndParseJsonData(Context context, JSONObject object)
    {
        sharedPreference=context.getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor=sharedPreference.edit();
        try
        {
            editor.putString(Constants.ID,object.getString("id"));
            editor.putString(Constants.NAME,Constants.setNullValue(object,"first_name"));
            editor.putString(Constants.USER_NAME,Constants.setNullValue(object,"username"));
            editor.putString(Constants.EMAIL,Constants.setNullValue(object,"email"));
            editor.putString(Constants.DOB,Constants.setNullValue(object,"dob"));
            editor.putString(Constants.NICK_NAME,Constants.setNullValue(object,"nick_name"));
            editor.putString(Constants.SCHOOL_NAME,Constants.setNullValue(object,"school_name"));


            if (object.getString("cityDetails").equalsIgnoreCase("null"))
            {
                editor.putString(Constants.CITY,"empty");
                editor.putString(Constants.CITY_ID,"0");
            }
            else
            {
                editor.putString(Constants.CITY,object.getJSONObject("cityDetails").getString("name"));
                editor.putString(Constants.CITY_ID,object.getJSONObject("cityDetails").getString("id"));
            }

            if (!object.getString("countryDetails").equalsIgnoreCase("null"))
            {
                editor.putString(Constants.COUNTRY,object.getJSONObject("countryDetails").getString("name"));
                editor.putString(Constants.COUNTRY_ID,object.getJSONObject("countryDetails").getString("id"));
            }
            else
            {
                editor.putString(Constants.COUNTRY_ID,"0");
                editor.putString(Constants.COUNTRY,"empty");
            }

            editor.putString(Constants.FAMILY_NAME,Constants.setNullValue(object,"family_name"));
            editor.putString(Constants.Image,Constants.setNullValue(object,"image"));
            editor.putString(Constants.ABOUT,Constants.setNullValue(object,"about_us"));
            editor.putString(Constants.FATHER,Constants.setNullValue(object,"father_name"));
            editor.putString(Constants.GENDER,Constants.setNullValue(object,"mother_name"));
            editor.putString(Constants.CONTACT,Constants.setNullValue(object,"gardian_contact"));


            StringBuilder stringBuilder=new StringBuilder();
            StringBuilder stringBuilder1=new StringBuilder();


                JSONArray game=object.getJSONArray("gameDetails");
                if (game.length()>0)
                {
                    for (int i=0;i<game.length();i++)
                    {
                        JSONObject data=game.getJSONObject(i);

                        if (!data.getString("fullGameDetails").equalsIgnoreCase("null"))
                        {
                            stringBuilder.append(data.getString("game_id"));
                            stringBuilder.append(",");

                            stringBuilder1.append(data.getJSONObject("fullGameDetails").getString("name"));
                            stringBuilder1.append(",");
                        }
                    }

                    editor.putString(Constants.GAMES_NAME,String.valueOf(stringBuilder1).substring(0,String.valueOf(stringBuilder1).length()-1));
                    editor.putString(Constants.GAME,String.valueOf(stringBuilder).substring(0,String.valueOf(stringBuilder).length()-1));
                }

            editor.commit();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /*To Store Fcm Device ID*/
    public static void storeFcmDeviceId(Context context, String val)
    {
        sharedPreference = context.getSharedPreferences("FCM", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        editor.putString("DEVICE", val);
        editor.commit();
    }

    public static String retrieveFcmDeviceId(Context context)
    {
        sharedPreference = context.getSharedPreferences("FCM", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        return sharedPreference.getString("DEVICE", null);
    }

    public static void storeDataForPermission(Context context, String key, String value)
    {
        sharedPreference=context.getSharedPreferences("Perm", Context.MODE_PRIVATE);
        editor=sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String retriveDataForPermission(Context context, String Key)
    {
        sharedPreference=context.getSharedPreferences("Perm", Context.MODE_PRIVATE);
        return sharedPreference.getString(Key,null);
    }
}
