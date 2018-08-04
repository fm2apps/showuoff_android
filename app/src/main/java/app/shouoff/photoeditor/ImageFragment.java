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

public class ImageFragment extends Fragment implements ImageAdapter.OnImageClickListener {

    private ArrayList<String> stickerBitmaps;
    private PhotoEditorActivity photoEditorActivity;
    RecyclerView imageRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoEditorActivity = (PhotoEditorActivity) getActivity();

        TypedArray images = getResources().obtainTypedArray(R.array.photo_editor_photos);

        stickerBitmaps = new ArrayList<>();
        stickerBitmaps.add("crakers.png");
        stickerBitmaps.add("camera_image.png");
        stickerBitmaps.add("podium.png");
        stickerBitmaps.add("trophy.png");
        stickerBitmaps.add("medal.png");
        stickerBitmaps.add("jersey.png");
        stickerBitmaps.add("basketball_jersey.png");
        stickerBitmaps.add("basketball_jersey_new.png");
        stickerBitmaps.add("kitty.png");
        stickerBitmaps.add("basketball.png");
        stickerBitmaps.add("basketball_new.png");
        stickerBitmaps.add("field_hockey.png");
        stickerBitmaps.add("snake.png");
        stickerBitmaps.add("football.png");
        stickerBitmaps.add("pikachu.png");
        stickerBitmaps.add("halloween_bats.png");
        stickerBitmaps.add("party.png");
        stickerBitmaps.add("party_new.png");
        stickerBitmaps.add("eye_ne.png");
        stickerBitmaps.add("mask_new.png");
        stickerBitmaps.add("eyeglasses.png");

        /*for (int i = 0; i < images.length(); i++)
        {
            stickerBitmaps.add(decodeSampledBitmapFromResource(photoEditorActivity.getResources(),
                    images.getResourceId(i, -1), 120, 120));
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_photo_edit_image, container, false);

        imageRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_main_photo_edit_image_rv);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(photoEditorActivity, 3));
        ImageAdapter adapter = new ImageAdapter(photoEditorActivity, stickerBitmaps);
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
        try {

            photoEditorActivity.addImage(image);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
