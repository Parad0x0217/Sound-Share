package l2bb.l2beatbox2;

import android.support.v4.app.ListFragment;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.io.IOException;

public class SelectSoundFragment extends ListFragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static BeatAdapter beatAdapter;
    VisualizerView mVisualizerView;
    MediaPlayer mPlayer;
    BeatDatabase bd = BeatDatabase.getInstance(null);

    public SelectSoundFragment()
    {

    }

    public static Fragment newInstance(int sectionNumber) {
        SelectSoundFragment fragment = new SelectSoundFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_select_sound, container, false);

        beatAdapter = new BeatAdapter();
        setListAdapter(beatAdapter);
        setupVisualizer(rootView);
<<<<<<< HEAD
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        myUpdateOperation();
                    }
                }
        );
=======

>>>>>>> 26cb5bd2969d78bc8df9a3dd3ef8232594d8fa18
        return rootView;
    }

    private void myUpdateOperation(){
        SelectSoundFragment selectSoundFragment = (SelectSoundFragment)getActivity().getSupportFragmentManager().findFragmentById(R.layout.fragment_select_sound);
        selectSoundFragment.beatAdapter.notifyDataSetChanged();
    }

    private void setupVisualizer(View rootView){
        mPlayer = new MediaPlayer();
        mVisualizerView = (VisualizerView) rootView.findViewById(R.id.visualizerView);
        addLineRenderer();
        mVisualizerView.link(mPlayer);
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        playRecording(v, bd.getBeat(position + 1).getPath());
    }

    private void playRecording(View v, String path) {
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
        } catch(IOException e){
            e.printStackTrace();
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.reset();
                mVisualizerView.setEnabled(false);
            }
        });
    }
}