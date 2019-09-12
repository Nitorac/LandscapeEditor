package net.nitorac.landscapeeditor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements InputFragment.OnFragmentInteractionListener, ResultsFragment.OnFragmentInteractionListener {

    private static MainActivity INSTANCE;

    public static String REQ_URL = "";
    public static String RECEIVE_URL = "";

    public InputFragment inputFragment;
    public ResultsFragment resultsFragment;

    public static Fragment currentFragment;

    public Bitmap inputImage;
    public int savedStyle;

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
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_input);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

    public static MainActivity getInstance() {
        return INSTANCE;
    }
}
