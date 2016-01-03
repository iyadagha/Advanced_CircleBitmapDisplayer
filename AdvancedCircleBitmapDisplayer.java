import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * When using <a href="https://github.com/nostra13/Android-Universal-Image-Loader">Android Universal Image Loader</a> This
 * Displayer can display bitmap cropped by a circle with a fade-in animation and a border.
 * This implementation works only with ImageViews wrapped in ImageViewAware.
 * <br />
 *
 * @author Iyad Agha
 * @since 1.9.5
 */
public class AdvancedCircleBitmapDisplayer implements BitmapDisplayer {

  private final int fadeInAnimationDuration;
  private final int borderThickness;
  private final int borderColor;

  /**
   * @param fadeInAnimationDuration The duration of "fade-in" animation (in milliseconds). If fadeInAnimationDuration < 0 then the animation
   * will be deactivated
   * @param borderThickness The thickness of the circle border. If borderThickness < 0 then no border will be drawn.
   * @param borderColor The color (including alpha) of the circle border.
   **/
  public AdvancedCircleBitmapDisplayer(int fadeInAnimationDuration, int borderThickness, int borderColor) {
    this.fadeInAnimationDuration = fadeInAnimationDuration > 0 ? fadeInAnimationDuration : 0;
    this.borderThickness = borderThickness > 0 ? borderThickness : 0;
    this.borderColor = borderColor;

  }

  @Override
  public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
    if (!(imageAware instanceof ImageViewAware)) {
      throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
    }

    imageAware.setImageDrawable(new CircleDrawable(bitmap, borderThickness, borderColor));
    if (fadeInAnimationDuration > 0) {
      animate(imageAware.getWrappedView(), fadeInAnimationDuration);
    }

  }

  public static void animate(View imageView, int durationMillis) {
    if (imageView != null) {
      AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
      fadeImage.setDuration(durationMillis);
      fadeImage.setInterpolator(new DecelerateInterpolator());
      imageView.startAnimation(fadeImage);
    }
  }

  public static class CircleDrawable extends Drawable {

    protected float radius;

    protected final RectF mRect = new RectF();
    protected final RectF mBitmapRect;
    protected final BitmapShader bitmapShader;
    protected final Paint paint;
    protected Paint backgroundPaint;
    protected final int borderThickness;

    public CircleDrawable(Bitmap bitmap, int borderThickness, int borderColor) {
      radius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2;
      this.borderThickness = borderThickness;
      bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      mBitmapRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

      paint = new Paint();
      paint.setAntiAlias(true);
      paint.setShader(bitmapShader);
      paint.setFilterBitmap(true);
      paint.setDither(true);
      if (borderThickness > 0) {
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(borderColor);
        backgroundPaint.setAntiAlias(true);
      }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
      super.onBoundsChange(bounds);
      mRect.set(0, 0, bounds.width(), bounds.height());
      radius = Math.min(bounds.width(), bounds.height()) / 2;

      Matrix shaderMatrix = new Matrix();
      shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
      bitmapShader.setLocalMatrix(shaderMatrix);
    }

    @Override
    public void draw(Canvas canvas) {
      if (backgroundPaint != null) {
        canvas.drawCircle(radius, radius, radius + borderThickness, backgroundPaint);
      }
      canvas.drawCircle(radius, radius, radius, paint);
    }

    @Override
    public int getOpacity() {
      return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
      paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
      paint.setColorFilter(cf);
    }
  }
}
