package app.shouoff.faceFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.google.android.gms.vision.face.Face;
import java.io.IOException;
import java.net.URL;

public class FaceGraphicNew extends GraphicOverlay.Graphic
{
    private volatile Face mFace;
    private int mFaceId;
    private Bitmap bitmap;
    private Bitmap op;
    Context context;
    private Float height,weight;

    public FaceGraphicNew(GraphicOverlay overlay, Context context,String source,Float height,Float weight)
    {
        super(overlay);
        this.context=context;
        this.height=height;
        this.weight=weight;

        try
        {
            URL url = new URL(source);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            op = bitmap;
        }
        catch(IOException e)
        {
            System.out.println(e);
        }

    }

    public FaceGraphicNew(GraphicOverlay overlay)
    {
        super(overlay);
    }

    public void setId(int id)
    {
        mFaceId = id;
    }

    public void updateFace(Face face)
    {
        mFace = face;
        op = Bitmap.createScaledBitmap(bitmap, (int) scaleX(face.getWidth()),
                (int) scaleY(((bitmap.getHeight() * face.getWidth()) / bitmap.getWidth())), false);
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas)
    {
        Face face = mFace;
        if (face == null)
        {
            return;
        }
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        float xOffset = scaleX(face.getWidth() / weight);
        float yOffset = scaleY(face.getHeight() / height);
        float left = x - xOffset;
        float top = y - yOffset;
        canvas.drawBitmap(op, left, top, new Paint());
    }
}