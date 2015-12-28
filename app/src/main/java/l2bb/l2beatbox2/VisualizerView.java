package l2bb.l2beatbox2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.PorterDuff.Mode;
import android.graphics.Matrix;

/**
 * Created by OZ on 12/27/2015.
 */

/**
 * A class that draws visualizations of data received from a
 * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture } and
 * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
 */
public class VisualizerView extends View {
    private static final String TAG = "VisualizerView";
    boolean mFlash = false;
    Bitmap mCanvasBitmap;
    Canvas mCanvas;
    private byte[] mBytes;
    private byte[] mFFTBytes;
    private Rect mRect = new Rect();
    private Visualizer mVisualizer;
    private Set<Renderer> mRenderers;
    private Paint mFlashPaint = new Paint();

    public VisualizerView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs){ this(context, attrs, 0); }

    public VisualizerView(Context context){ this(context, null, 0)}

    private void init(){
        mBytes = null;
        mFFTBytes = null;

        mFlashPaint.setColor(Color.argb(122,255,255,255));

        mRenderers = new HashSet<Renderer>();
    }

    /**
     * Links the visualizer to a player
     *
     * @param player - MediaPlayer instance to link to
     */
    public void link(MediaPlayer player){
        if (player == null){
            throw new NullPointerException("Cannot link to null MediaPlayer");
        }

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(player.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        // Pass through Visualizer data to VisualizerView
        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                updateVisualizer(bytes);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                updateVisualizerFFT(bytes);
            }
        };

        mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, true);

        // Enabled Visualizer and disable when we;re done with the stream
        mVisualizer.setEnabled(true);
    }

    public void addRenderer(Renderer renderer){
        if(renderer != null){
            mRenderers.add(renderer);
        }
    }
    public void clearRenderers() {mRenderers.clear();}

    /**
     * Call to release the resources used by VisualizerView.
     * Like with the MediaPlayer it is good practice to call this method
     */
    public void release(){mVisualizer.release();}

    /**
     * Pass data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See
     * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
     *
     * @param bytes
     */
    public void updateVisualizer(byte[] bytes){
        mBytes= bytes;
        invalidate();
    }

    /**
     * Pass FFT data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See
     * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
     *
     * @param bytes
     */
    public void updateVisualizerFFT(byte[] bytes){
        mFFTBytes = bytes;
        invalidate();
    }

    /**
     * Call this to make the visualizer flash. Useful for flashing at the start
     * of a song/loop etc..
     */
    public void flash(){
        mFlash = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // Create canvas once we're ready to draw
        mRect.set(0, 0, getWidth(), getHeight());

        if(mCanvasBitmap == null){
            mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        }
        if(mCanvas == null){
            mCanvas = new Canvas(mCanvasBitmap);
        }

        mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        if(mBytes != null){
            // Render all audio renderers
            AudioData audioData = new AudioData(mBytes);
            for(Renderer r : mRenderers){
                r.render(mCanvas, audioData, mRect);
            }
        }

        if(mFFTBytes != null){
            // Render all FFT renderers
            FFTData fftData = new FFTData(mFFTBytes);
            for( Renderer r : mRenderers){
                r.render(mCanvas, fftData, mRect);
            }
        }

        if(mFlash){
            mFlash = false;
            mCanvas.drawPaint(mFlashPaint);
        }

        canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
    }



}