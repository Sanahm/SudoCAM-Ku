package com.example.mohamed.sudoku;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.lang.annotation.Repeatable;

/**
 * Created by Mohamed on 03/06/2017.
 * This Activity is informative activity it also allow you to launch the camera activity
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    TextView text = null;
    final int ACTION_REQUEST_GALLERY = 1;
    ImageView imgView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        text = (TextView) findViewById(R.id.git);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Sanahm/")));
            }
        });


        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(this);



        /*imgView = (ImageView) findViewById(R.id.img_trans);
        Drawable [] drawable = new Drawable[] { getResources().getDrawable(R.drawable.pn1), getResources().getDrawable(R.drawable.pn2),getResources().getDrawable(R.drawable.pn3)};
        TransitionDrawableExt drawableExt = new TransitionDrawableExt(drawable);

        imgView.setBackground(drawableExt);*/
        //((TransitionDrawable) imgView.getDrawable()).startTransition(3000);

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.solve_grid);
            String[] items = new String[] {"Live resolution","Select your Sudoku picture"};
            Integer[] icons = new Integer[]{R.drawable.ic_menu_camera1,R.drawable.ic_menu_gallery};
            ListAdapter adapter = new ArrayAdapterWithIcon(MainActivity.this,items,icons);
            builder.setAdapter(adapter,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            Intent camActivity = new Intent(MainActivity.this,CameraProcessActivity.class);
                            startActivity(camActivity);
                            break;
                        case 1:
                            Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
                            chooser.setType("image/*");
                            startActivityForResult(Intent.createChooser(chooser,"choose your Sudoku picture"),ACTION_REQUEST_GALLERY);

                        default:
                            break;
                    }
                }
            });


            builder.show();

        } else if (id == R.id.nav_manage) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.numColor)
                    .setItems(R.array.numColor, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            Processing.setNumberColor(which);
                        }
                    });
           builder.show();



        } else if (id == R.id.nav_help) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(getResources().getString(R.string.msg) + "\n\n" + getResources().getString(R.string.note))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                        /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                        });*/
            builder.show();
        }

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Intent display = new Intent(this,DisplayView.class);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case ACTION_REQUEST_GALLERY:
                    Uri imageUri = data.getData();

                    try {
                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        //imgView = (ImageView)findViewById(R.id.imgView);
                        //imgView.setImageBitmap(bitmap);
                        //Intent display = new Intent(this,DisplayView.class);
                        display.putExtra("image-uri",imageUri);
                        //startActivity(display);
                        //startActivity(new Intent(Intent.ACTION_VIEW,imageUri));


                    }catch (Exception e){}

                    //Intent help_activity = new Intent(MainActivity.this,DisplayView.class);
                    //startActivity(help_activity);
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }

        startActivity(display);

    }
}
