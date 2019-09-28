package net.nitorac.landscapeeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.codekidlabs.storagechooser.StorageChooser;
import com.dd.processbutton.iml.ActionProcessButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends ActionMenuFragment {

    private OnFragmentInteractionListener mListener;

    public Bitmap currentBitmap = createImage(512, 512, -1);
    public Bitmap lastBitmap;

    public ActionProcessButton validateBtn;
    public ImageView resView;
    public RequestTask reqTask;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance() {
        ResultsFragment fragment = new ResultsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume(){
        super.onResume();
        if(reqTask == null || !reqTask.getStatus().equals(AsyncTask.Status.RUNNING)){
            validateBtn.setProgress(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_results, container, false);
        resView = v.findViewById(R.id.resultImageView);
        resView.setImageBitmap(currentBitmap);
        validateBtn = v.findViewById(R.id.validateBtn);
        validateBtn.setMode(ActionProcessButton.Mode.ENDLESS);

        validateBtn.setOnClickListener(v1 -> {
            if (validateBtn.getProgress() == 0) {
                boolean hasToRandom = MainActivity.getInstance().inputImage.sameAs(lastBitmap) && MainActivity.getInstance().savedStyle.equals("random");
                reqTask = new RequestTask(ResultsFragment.this, hasToRandom);
                Log.i("ResultFrag", "HasToUpdateRandom : " + hasToRandom);
                lastBitmap = MainActivity.getInstance().inputImage;
                reqTask.execute();
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void getActionbarMenu(Menu menu) {
        MainActivity.getInstance().getMenuInflater().inflate(R.menu.results_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setOnMenuItemClickListener(menuItem -> {
            StorageChooser chooser = new StorageChooser.Builder()
                    .withActivity(getActivity())
                    .withFragmentManager(MainActivity.getInstance().getFragmentManager())
                    .withMemoryBar(true)
                    .allowCustomPath(true)
                    .setType(StorageChooser.DIRECTORY_CHOOSER)
                    .withPredefinedPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath())
                    .build();
            chooser.show();
            chooser.setOnSelectListener(path -> saveImageExternal(new File(path), currentBitmap));
            return true;
        });
        // Fetch and store ShareActionProvider
        /*shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, saveImageExternal(currentBitmap));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        shareActionProvider.setShareIntent(intent);*/
    }

    private void saveImageExternal(File directory, Bitmap image) {
        try {
            File file = new File(directory, "Paysage_" + new SimpleDateFormat("dd-MM-yyyy HH.mm.ss", Locale.FRANCE).format(new Date()) + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            Toast.makeText(getContext(), "L'image a bien été sauvegardée !", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.d("SaveImage", "IOException while trying to write file for sharing: " + e.getMessage());
            Toast.makeText(getContext(), "Impossible de sauvegarder l'image :(", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }
}
