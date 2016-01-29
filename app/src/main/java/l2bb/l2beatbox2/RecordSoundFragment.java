package l2bb.l2beatbox2;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.File;
import java.io.IOException;

/**
 * Created by OZ on 12/28/2015.
 */
public class RecordSoundFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    VisualizerView mVisualizerView;
    Button start;
    Button stop;
    Button play;
    MediaRecorder mr;
    MediaPlayer mp;
    String fileName;
    String path;

    public RecordSoundFragment(){

    }

    public static RecordSoundFragment newInstance(int sectionNumber){
        RecordSoundFragment fragment = new RecordSoundFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_record_sound, container, false);
        start = (Button) rootView.findViewById(R.id.button);
        stop = (Button) rootView.findViewById(R.id.button2);
        play = (Button) rootView.findViewById(R.id.button3);

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startRecording(v);
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                stopRecording(v);
            }
        });
        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playRecording(v);
            }
        });

        newMediaRecorder();

        path = (getNextFileName(rootView.getContext()).toString());
        mr.setOutputFile(path);

        try {
            mr.prepare();
        } catch(IOException e){
            e.printStackTrace();
        }

        mp = new MediaPlayer();

        mVisualizerView = (VisualizerView) rootView.findViewById(R.id.visualizerView);
        addLineRenderer();

        mVisualizerView.link(mp);

        return rootView;
    }

    public void startRecording(View v) {
        mr.start();

        start.setEnabled(false);
        stop.setEnabled(true);
    }

    private void stopRecording(View v){
        mr.stop();
        mr.reset();

        mr.release();

        stop.setEnabled(false);
        play.setEnabled(true);

        BeatDatabase bd = BeatDatabase.getInstance(null);
        bd.insertData(fileName, path);

        SelectSoundFragment f = (SelectSoundFragment)getActivity().getSupportFragmentManager().findFragmentById(R.layout.fragment_select_sound);
        f.beatAdapter.notifyDataSetChanged();
    }

    private void playRecording(View v){
        try {
            mp.setDataSource(path);
            mp.prepare();
        } catch(IOException e){
            e.printStackTrace();
        }

        play.setEnabled(false);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp){
                mp.stop();
                mp.reset();
                play.setEnabled(true);
                mVisualizerView.setEnabled(false);
            }
        });
    }

    private void newMediaRecorder(){
        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    private File getNextFileName(Context c){
        fileName = "Audio_" + System.currentTimeMillis() + ".3gp";
        File file = new File(c.getFilesDir(), fileName);
        return file;
    }

    private void addLineRenderer(){
        LineRenderer lineRenderer = new LineRenderer(setupPaint());
        mVisualizerView.addRenderer(lineRenderer);
    }
    private Paint setupPaint(){
        Paint linePaint =  new Paint();
        linePaint.setStrokeWidth(7f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(getResources().getColor(R.color.colorPrimary));
        return linePaint;
    }
}