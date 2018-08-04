package app.shouoff.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import app.shouoff.HashTagData;
import app.shouoff.UserProfile;
import app.shouoff.login.MyProfile;
import app.shouoff.model.CommentListModel;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.TagUserData;

public class DataHandler
{
    public static void notificationText(TextView textView, String first, String six)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString str1= new SpannableString(first);
        str1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, str1.length(), 0); /*Black color*/
        str1.setSpan(new StyleSpan(Typeface.BOLD), 0, str1.length(), 0);
        builder.append(str1);

        SpannableString str6= new SpannableString(six);
        str6.setSpan(new ForegroundColorSpan(Color.parseColor("#A0A0A0")), 0, str6.length(), 0); /*gray color*/
        str6.setSpan(new RelativeSizeSpan(0.7f),0,str6.length(),0);
        builder.append(str6);
        textView.setHint(builder);
    }

    public static void addTextForPost(final Context context, TextView textView, String name, String name_Two, final HomePostModel postModel)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString str1= new SpannableString(name);
        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View textView)
            {
                context.startActivity(new Intent(context, UserProfile.class)
                        .putExtra("profile_id",postModel.getShare_user_id()));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        str1.setSpan(clickableSpan, 0, str1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, str1.length(), 0); /*Black color*/
        builder.append(str1);

        SpannableString str5= new SpannableString(" shared ");
        str5.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, str5.length(), 0); /*Black color*/
        str5.setSpan(new StyleSpan(Typeface.BOLD), 0, str5.length(), 0);
        builder.append(str5);

        SpannableString str2= new SpannableString(name_Two);
        ClickableSpan clickableSpan1 = new ClickableSpan()
        {
            @Override
            public void onClick(View textView)
            {
                context.startActivity(new Intent(context, UserProfile.class)
                        .putExtra("profile_id",postModel.getUser_id()));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        str2.setSpan(clickableSpan1, 0, str2.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str2.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, str2.length(), 0); /*Black color*/
        builder.append(str2);

        SpannableString str4= new SpannableString(" post");
        str4.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, str4.length(), 0); /*Black color*/
        str4.setSpan(new StyleSpan(Typeface.BOLD), 0, str4.length(), 0);
        builder.append(str4);

        textView.append(builder);
    }

    public static void forContest(TextView textView, String name, String name_Two)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str5= new SpannableString(name);
        str5.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, str5.length(), 0); /*Black color*/
        str5.setSpan(new StyleSpan(Typeface.BOLD), 0, str5.length(), 0);
        builder.append(str5);

        SpannableString str1= new SpannableString(name_Two);
        str1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, str1.length(), 0); /*Black color*/
        builder.append(str1);

        textView.setHint(builder);
    }

    /*Tag and HashTag data and click event*/
    public static void tagdata(final Context context, TextView textView,
                               final HomePostModel postModels)
    {
        SpannableString ss = new SpannableString(postModels.getDescription());
        Pattern p = Pattern.compile("[#][a-zA-Z0-9]+");
        Matcher m = p.matcher(postModels.getDescription());
        while (m.find())
        {
            ClickableSpan clickableSpan = new ClickableSpan()
            {
                @Override
                public void onClick(View textView)
                {
                    TextView tv = (TextView) textView;
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);

                    context.startActivity(new Intent(context, HashTagData.class)
                            .putExtra("TagWord",String.valueOf(s.subSequence(start,end))));
                }
            };
            ss.setSpan(clickableSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        Pattern pp = Pattern.compile("[@][a-zA-Z0-9]+");
        Matcher mm = pp.matcher(postModels.getDescription());
        while (mm.find())
        {
            ClickableSpan clickableSpan = new ClickableSpan()
            {
                @Override
                public void onClick(View textView)
                {
                    TextView tv = (TextView) textView;
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);

                    for (int i = 0; i < postModels.getTagUserData().size(); i++)
                    {
                        if (postModels.getTagUserData().get(i).getUsername().
                                equalsIgnoreCase(SharedPreference.retriveData(context,Constants.USER_NAME)))
                        {
                            context.startActivity(new Intent(context, MyProfile.class));
                        }
                        else
                        {
                            if (postModels.getTagUserData().get(i).getUsername()
                                    .equalsIgnoreCase(String.valueOf(s.subSequence(start,end))
                                            .replace("@","")))
                            {
                                context.startActivity(new Intent(context, UserProfile.class)
                                        .putExtra("profile_id",postModels.getTagUserData().get(i).getId()));

                            }
                        }
                    }
                }
            };
            ss.setSpan(clickableSpan, mm.start(), mm.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*Tag and HashTag data and click event*/
    public static void commentTagData(final Context context, TextView textView,
                               final CommentListModel postModels)
    {
        SpannableString ss = new SpannableString(postModels.getDescription());
        Pattern p = Pattern.compile("[#][a-zA-Z0-9]+");
        Matcher m = p.matcher(postModels.getDescription());
        while (m.find())
        {
            ClickableSpan clickableSpan = new ClickableSpan()
            {
                @Override
                public void onClick(View textView)
                {
                    TextView tv = (TextView) textView;
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);

                    context.startActivity(new Intent(context, HashTagData.class)
                            .putExtra("TagWord",String.valueOf(s.subSequence(start,end))));
                }
            };
            ss.setSpan(clickableSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        Pattern pp = Pattern.compile("[@][a-zA-Z0-9]+");
        Matcher mm = pp.matcher(postModels.getDescription());
        while (mm.find())
        {
            ClickableSpan clickableSpan = new ClickableSpan()
            {
                @Override
                public void onClick(View textView)
                {
                    TextView tv = (TextView) textView;
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);

                    for (int i = 0; i < postModels.getTagUserData().size(); i++)
                    {

                        if (postModels.getTagUserData().get(i).getUsername().
                                equalsIgnoreCase(SharedPreference.retriveData(context,Constants.USER_NAME)))
                        {
                            context.startActivity(new Intent(context, MyProfile.class));
                        }
                        else
                        {
                            if (postModels.getTagUserData().get(i).getUsername()
                                    .equalsIgnoreCase(String.valueOf(s.subSequence(start,end))
                                            .replace("@","")))
                            {
                                context.startActivity(new Intent(context, UserProfile.class)
                                        .putExtra("profile_id",postModels.getTagUserData().get(i).getId()));

                            }
                        }
                    }
                }
            };
            ss.setSpan(clickableSpan, mm.start(), mm.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*Tag and HashTag data and click event*/
    public static void notificationTagData(final Context context, TextView textView,String value,
                                           final ArrayList<TagUserData> tagUserData)
    {
        SpannableString ss = new SpannableString(value);
        Pattern p = Pattern.compile("[#][a-zA-Z0-9]+");
        Matcher m = p.matcher(value);
        while (m.find())
        {
            ClickableSpan clickableSpan = new ClickableSpan()
            {
                @Override
                public void onClick(View textView)
                {
                    TextView tv = (TextView) textView;
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);

                    context.startActivity(new Intent(context, HashTagData.class)
                            .putExtra("TagWord",String.valueOf(s.subSequence(start,end))));
                }
            };
            ss.setSpan(clickableSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        Pattern pp = Pattern.compile("[@][a-zA-Z0-9]+");
        Matcher mm = pp.matcher(value);
        while (mm.find())
        {
            ClickableSpan clickableSpan = new ClickableSpan()
            {
                @Override
                public void onClick(View textView)
                {
                    TextView tv = (TextView) textView;
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);

                    for (int i = 0; i <tagUserData.size(); i++)
                    {
                        if (tagUserData.get(i).getUsername().
                                equalsIgnoreCase(SharedPreference.retriveData(context,Constants.USER_NAME)))
                        {
                            context.startActivity(new Intent(context, MyProfile.class));
                        }
                        else
                        {
                            if (tagUserData.get(i).getUsername()
                                    .equalsIgnoreCase(String.valueOf(s.subSequence(start,end))
                                            .replace("@","")))
                            {
                                context.startActivity(new Intent(context, UserProfile.class)
                                        .putExtra("profile_id",tagUserData.get(i).getId()));

                            }
                        }
                    }
                }
            };
            ss.setSpan(clickableSpan, mm.start(), mm.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
