package net.nitorac.landscapeeditor;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.nitorac.landscapeeditor.colorview.ColorItem;
import net.nitorac.landscapeeditor.colorview.ColorListAdapter;
import net.nitorac.landscapeeditor.colorview.ColorView;
import net.nitorac.landscapeeditor.colorview.FloorListView;
import net.nitorac.landscapeeditor.drawview.BrushView;
import net.nitorac.landscapeeditor.drawview.DrawingView;
import net.nitorac.landscapeeditor.drawview.brushes.BrushSettings;
import net.nitorac.landscapeeditor.drawview.brushes.Brushes;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputFragment extends ActionMenuFragment {
    private OnFragmentInteractionListener mListener;

    public DrawingView drawingView;
    private ColorView colorView;
    private SeekBar sizeView;
    private BrushView brushView;

    private ImageView stylePreview;

    private Dialog colorDialog;
    private Dialog styleDialog;

    private Bundle currentBundle;


    public int lastPipetteBrush;

    public InputFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InputFragment newInstance() {
        InputFragment fragment = new InputFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_input, container, false);
        drawingView = v.findViewById(R.id.drawingView);
        ((BrushView)v.findViewById(R.id.brushView)).setDrawingView(drawingView);

        sizeView = v.findViewById(R.id.sizeSeekBar);

        colorView = v.findViewById(R.id.colorView);

        stylePreview = v.findViewById(R.id.stylePreview);

        brushView = v.findViewById(R.id.brushView);

        brushView.setOnClickListener(v12 -> {
            BrushSettings set = drawingView.getBrushSettings();
            if (set.getSelectedBrush() == Brushes.PEN) {
                set.setSelectedBrush(Brushes.FILL);
            } else if (set.getSelectedBrush() == Brushes.FILL) {
                set.setSelectedBrush(Brushes.ERASER);
            } else if (set.getSelectedBrush() == Brushes.ERASER) {
                set.setSelectedBrush(Brushes.PEN);
            }
        });

        stylePreview.setOnClickListener(v1 -> {
            styleDialog = new Dialog(MainActivity.getInstance());
            styleDialog.setContentView(R.layout.style_dialog);

            ListView listView = styleDialog.findViewById(R.id.styleDialogList);
            StyleListAdapter adapter = new StyleListAdapter(InputFragment.this, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

            listView.setAdapter(adapter);
            styleDialog.show();
        });

        colorView.setOnClickListener(v1 -> {
            colorDialog = new Dialog(MainActivity.getInstance());
            colorDialog.setContentView(R.layout.color_dialog);

            FloorListView listView = colorDialog.findViewById(R.id.colorList);
            ColorListAdapter adapter = new ColorListAdapter(InputFragment.this, ColorView.itemColors);

            listView.setAdapter(adapter);
            colorDialog.show();
        });

        sizeView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.getBrushSettings().setSelectedBrushSize(progress/100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(currentBundle != null){
            drawingView.setBackgroundImage(MainActivity.getInstance().inputImage);
            sizeView.setProgress(currentBundle.getInt("savedSize"));
            updateColor(ColorView.getColorFromInt(currentBundle.getInt("savedColor")));
            drawingView.getBrushSettings().setSelectedBrush(currentBundle.getInt("savedBrush"));
            updateStyle(MainActivity.getInstance().savedStyle);
        }else{
            sizeView.setProgress(25);
            updateColor(colorView.getColor());
            updateStyle(0);
        }
        return v;
    }

    public Dialog getColorDialog(){
        return colorDialog;
    }

    public Dialog getStyleDialog() {
        return styleDialog;
    }

    public void updateStyle(int style) {
        stylePreview.setImageResource(getResources().getIdentifier("s" + style, "drawable", MainActivity.getInstance().getPackageName()));
        MainActivity.getInstance().savedStyle = style;
    }

    public void updateColor(ColorItem color) {
        colorView.setColor(color);
        drawingView.getBrushSettings().setColor(color.getColor());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NotNull Context context) {
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
        currentBundle = new Bundle();
        MainActivity.getInstance().inputImage = drawingView.exportDrawing();
        currentBundle.putInt("savedSize", sizeView.getProgress());
        currentBundle.putInt("savedColor", colorView.getColor().getColor());
        currentBundle.putInt("savedBrush", drawingView.getBrushSettings().getSelectedBrush());
    }

    @Override
    public void getActionbarMenu(Menu menu) {
        MainActivity.getInstance().getMenuInflater().inflate(R.menu.input_menu, menu);

        MenuItem blankImage = menu.findItem(R.id.menu_blank_image);
        blankImage.setOnMenuItemClickListener(blankItem -> {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.getInstance())
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment créer une nouvelle image ?")
                    .setNegativeButton("Annuler", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Oui", (dialogInterface, i) -> drawingView.clear())
                    .create();
            dialog.show();
            return true;
        });

        MenuItem pipette = menu.findItem(R.id.menu_pipette);
        pipette.setOnMenuItemClickListener(pipetteItem -> {
            lastPipetteBrush = drawingView.getBrushSettings().getSelectedBrush();
            drawingView.getBrushSettings().setSelectedBrush(Brushes.PICK);
            return true;
        });
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
}
