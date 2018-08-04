package app.shouoff.photoeditor;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.shouoff.R;

public class GifFragment extends Fragment implements GifImageAdapter.OnImageClickListener
{
    private ArrayList<String> stickerBitmaps;
    private PhotoEditorActivity photoEditorActivity;
    RecyclerView imageRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        photoEditorActivity = (PhotoEditorActivity) getActivity();

        stickerBitmaps = new ArrayList<>();
        stickerBitmaps.add("batball.gif");
        stickerBitmaps.add("football_new.gif");
        stickerBitmaps.add("cycling.gif");
        stickerBitmaps.add("boxing.gif");
        stickerBitmaps.add("gymnastic.gif");
        stickerBitmaps.add("krate.gif");
        stickerBitmaps.add("run.gif");
        stickerBitmaps.add("running_player.gif");
        stickerBitmaps.add("sliding.gif");
        stickerBitmaps.add("tennis.gif");
        stickerBitmaps.add("archery.gif");
        stickerBitmaps.add("badminton.gif");
        stickerBitmaps.add("disk.gif");
        stickerBitmaps.add("basket_ball.gif");
        stickerBitmaps.add("chess.gif");
        stickerBitmaps.add("dice_gif.gif");
        stickerBitmaps.add("horse_race.gif");
        stickerBitmaps.add("pushups.gif");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_photo_edit_image, container, false);

        imageRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_main_photo_edit_image_rv);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(photoEditorActivity, 3));
        GifImageAdapter adapter = new GifImageAdapter(photoEditorActivity, stickerBitmaps);
        adapter.setOnImageClickListener(this);
        imageRecyclerView.setAdapter(adapter);

        return rootView;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onImageClickListener(String image)
    {
        photoEditorActivity.addImageGif(image);
    }
}
