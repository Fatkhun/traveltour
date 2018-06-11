package com.fatkhun.travelia.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.GlideApp;
import com.fatkhun.travelia.model.TourWisata;

import java.util.ArrayList;

public class DeatilWisataActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private FloatingActionButton floatingActionButton;
    private ArrayList<TourWisata> mTourWisatas;

    private Menu collapsedMenu;
    private boolean appBarExpanded = true;

    private static final String TAG = "DetailWisataActivity";

    private static final String BASE_IMAGE_URL = "http://192.168.43.36:8000/uploads/file/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatil_wisata);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fbRate);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String nama = intent.getStringExtra("nama");

                Toast.makeText(getApplicationContext(), "Tour Rating", Toast.LENGTH_SHORT).show();
                Intent fbRate = new Intent(getApplicationContext(), RateWisataActivity.class);
                fbRate.putExtra("nama_wisata", nama);
                fbRate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(fbRate);
            }
        });


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(DeatilWisataActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);

                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

        ImageView DetailImage = findViewById(R.id.headerDetailImage);
        TextView DetailDeskripsi = findViewById(R.id.tvDeskripsiWisata);
        TextView DetailHarga = findViewById(R.id.tvHargaWisata);
        TextView DetailInfo = findViewById(R.id.tvInfoWisata);
        TextView DetailPenginapan = findViewById(R.id.tvPenginapan);
        TextView DetailTransportasi = findViewById(R.id.tvTranspotasiWisata);
        TextView DetailMakan = findViewById(R.id.tvMakanWisata);
        TextView DetailTiket = findViewById(R.id.tvTiketWisata);
        TextView DetailLokasi = findViewById(R.id.tvLokasi);
        Intent intent = getIntent();

        String imagePath = intent.getStringExtra("gambar");
        String nama = intent.getStringExtra("nama");
        String deskripsi = intent.getStringExtra("deskripsi");
        String info = intent.getStringExtra("info");
        String penginapan = intent.getStringExtra("penginapan");
        String transportasi = intent.getStringExtra("transportasi");
        String makan = intent.getStringExtra("makan");
        String lokasi = intent.getStringExtra("lokasi");
        int tiket = intent.getIntExtra("tiket", 0);
        int harga = intent.getIntExtra("harga", 0);

        Uri imageUrl = Uri.parse(BASE_IMAGE_URL + imagePath);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(nama);
        }

        GlideApp.with(DetailImage)
                .load(imageUrl)
                .thumbnail(.25f)
                .placeholder(R.drawable.anggrek)
                .into(DetailImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DetailDeskripsi.setText(Html.fromHtml(deskripsi, Html.FROM_HTML_MODE_COMPACT));
        } else {
            DetailDeskripsi.setText(Html.fromHtml(deskripsi));
        }

        DetailInfo.setText(info);
        DetailPenginapan.setText(penginapan);
        DetailTransportasi.setText(transportasi);
        DetailMakan.setText(makan);
        DetailLokasi.setText(lokasi);
        DetailTiket.setText( "Rp." + String.valueOf(tiket));
        DetailHarga.setText( "Rp." + String.valueOf(harga));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null
                && (!appBarExpanded || collapsedMenu.size() != 1)) {
            //collapsed
            collapsedMenu.add("Rate")
                    .setIcon(R.drawable.ic_mood)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            //expanded
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        if (item.getTitle() == "Rate") {
            Intent intent = getIntent();
            String nama = intent.getStringExtra("nama");

            Toast.makeText(this, "Tour Rating", Toast.LENGTH_SHORT).show();
            Intent fbRate = new Intent(getApplicationContext(), RateWisataActivity.class);
            fbRate.putExtra("nama_wisata", nama);
            fbRate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(fbRate);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
