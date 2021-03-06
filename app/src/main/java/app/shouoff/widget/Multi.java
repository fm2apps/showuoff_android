package app.shouoff.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.MultiAutoCompleteTextView;

@SuppressLint("AppCompatCustomView")
public class Multi extends MultiAutoCompleteTextView
{
    public Multi(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private KeyImeChange keyImeChangeListener;

    public void setKeyImeChangeListener(KeyImeChange listener)
    {
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange
    {
        void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if (keyImeChangeListener != null)
        {
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}
