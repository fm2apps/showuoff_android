package app.shouoff.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import app.shouoff.R;
import app.shouoff.interfaces.DeleteComment;
import app.shouoff.interfaces.DeletePost;

public class Alerts
{
    public static void showDialog(final Context context, String title, String msg)// Simple Alert
    {
        TextView txt_msg, ok;
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_message, null);
        txt_msg = (TextView) dialogView.findViewById(R.id.message);
        txt_msg.setText(msg);
        ok = (TextView) dialogView.findViewById(R.id.submit);
        dialogBuilder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /*Alert to delete comment*/
    public static void showDialog(final Context context, final int pos,final DeleteComment deleteComment,String temp)
    {
        TextView txt_msg, ok,cancel;
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.comment_alert_message, null);
        txt_msg = (TextView) dialogView.findViewById(R.id.message);
        txt_msg.setText(R.string.sure_to_delete);
        ok = (TextView) dialogView.findViewById(R.id.submit);
        cancel = (TextView) dialogView.findViewById(R.id.cancel);
        dialogBuilder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteComment.delete(v,pos);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /*Alert to delete post*/
    public static void showDialogDeletePost(final Context context,final DeletePost deletePost)
    {
        TextView txt_msg, ok,cancel;
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.comment_alert_message, null);
        txt_msg = (TextView) dialogView.findViewById(R.id.message);
        txt_msg.setText(R.string.delete_post);
        ok = (TextView) dialogView.findViewById(R.id.submit);
        cancel = (TextView) dialogView.findViewById(R.id.cancel);
        dialogBuilder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deletePost.deleteService(v);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
