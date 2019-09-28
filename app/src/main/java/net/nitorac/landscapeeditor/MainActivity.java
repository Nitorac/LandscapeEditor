package net.nitorac.landscapeeditor;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.roger.catloadinglibrary.CatLoadingView;

import net.nitorac.landscapeeditor.providers.NitoInstaller;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements InputFragment.OnFragmentInteractionListener, ResultsFragment.OnFragmentInteractionListener {

    public static final String SAVEDINPUT_KEY = "savedInput";

    private static MainActivity INSTANCE;

    public static String REQ_URL = "";
    public static String RECEIVE_URL = "";
    public static String RANDOM_URL = "";

    public InputFragment inputFragment;
    public ResultsFragment resultsFragment;

    public static Fragment currentFragment;
    public static AppUpdaterUtils appUpdater;

    public SharedPreferences sharedPreferences;
    public CatLoadingView catDialog;

    public Bitmap inputImage;
    public String savedStyle;

    public Bitmap BASE_BITMAP;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_input:
                    if(inputFragment == null){
                        inputFragment = InputFragment.newInstance();
                    }
                    loadFragment(inputFragment);
                    break;
                case R.id.navigation_results:
                    if(resultsFragment == null){
                        resultsFragment = ResultsFragment.newInstance();
                    }
                    loadFragment(resultsFragment);
                    break;
                default:
                    return false;
            }
            invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
            return true;
        }
    };

    public MainActivity() {
        INSTANCE = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        byte[] BBBase64 = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAYAAAD0eNT6AAAIaElEQVR4nO3WMQ2AQBAAwfMvgaAHESRYwMK/Cyh2ium33DnfZwEALfN3AADwPQMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIMgAAECQAQCAIAMAAEEGAACCDAAABBkAAAgyAAAQZAAAIGiO614AQIsBAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgyAAAQJABAIAgAwAAQQYAAIIMAAAEGQAACDIAABBkAAAgaANTMHfPGeOA9gAAAABJRU5ErkJggg==", Base64.DEFAULT);
        byte[] savedInputB64 = Base64.decode(sharedPreferences.getString(SAVEDINPUT_KEY, "ZW1wdHk="), Base64.DEFAULT);
        BASE_BITMAP = BitmapFactory.decodeByteArray(BBBase64, 0, BBBase64.length);
        inputImage = !Arrays.equals(savedInputB64, "empty".getBytes()) ? BitmapFactory.decodeByteArray(savedInputB64, 0, savedInputB64.length) : BASE_BITMAP;

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_input);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        appUpdater = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/Nitorac/LandscapeEditor/master/update.json")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        if (!isUpdateAvailable) {
                            return;
                        }

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Mise à jour disponible")
                                .setIcon(R.drawable.ic_download)
                                .setMessage("Une nouvelle mise à jour est disponible (" + BuildConfig.VERSION_NAME + " -> " + update.getLatestVersion() + ")\n\nVoici les nouveautés : \n" + update.getReleaseNotes() + "\n\nVoulez-vous mettre à jour l'application ?")
                                .setPositiveButton("Mettre à jour", (dialogInterface, i) -> {
                                    catDialog = new CatLoadingView();
                                    catDialog.setCanceledOnTouchOutside(false);
                                    catDialog.show(getSupportFragmentManager(), "CatLoading");
                                    catDialog.setText("S M I R K . . .");

                                    new NitoInstaller.Builder(MainActivity.this)
                                            .setMode(NitoInstaller.MODE.AUTO_ONLY)
                                            .setCacheDirectory(Environment.getExternalStorageDirectory().getAbsolutePath())
                                            .build()
                                            .installFromUrl(update.getUrlToDownload().toExternalForm());
                                })
                                .setNegativeButton("Annuler", (dialogInterface, i) -> dialogInterface.dismiss())
                                .setCancelable(false)
                                .create().show();
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {

                    }
                });
        appUpdater.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (catDialog != null) {
            catDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Fragment f = currentFragment;
        Log.i(getClass().getName(), "Fragment onCreateOptionsMenu : " + f.getClass().getName());
        if(f instanceof ActionMenuFragment){
            ((ActionMenuFragment)f).getActionbarMenu(menu);
            Log.i(getClass().getName(), "Fragment onCreateOptionsMenu");
            return true;
        }
        return false;
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            MainActivity.currentFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case 1:
            case 2:
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Ignoooore
                } else {
                    AlertDialog diag = new AlertDialog.Builder(this)
                            .setTitle("Attention !")
                            .setMessage("Vous devez autoriser la sauvegarde des fichiers pour enregistrer les images !")
                            .setCancelable(false)
                            .setNeutralButton("OK", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode + 1);
                            })
                            .create();
                    diag.show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void saveImage() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        inputImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        editor.putString(SAVEDINPUT_KEY, Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
        editor.apply();
    }

    public static MainActivity getInstance() {
        return INSTANCE;
    }
}
