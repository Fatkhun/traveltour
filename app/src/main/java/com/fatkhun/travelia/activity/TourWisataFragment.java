package com.fatkhun.travelia.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.travelia.Utils.ItemClickSupportUtils;
import com.fatkhun.travelia.Utils.NetworkUtils;
import com.fatkhun.travelia.adapter.TourWisataAdapter;
import com.fatkhun.travelia.model.TourWisata;
import com.fatkhun.travelia.service.TourWisataService;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourWisataFragment extends android.support.v4.app.Fragment {
    private LinearLayout tapactionlayout;
    private RelativeLayout laylowprice, layhighprice;
    private ImageView imglowprice, imghighprice;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private View bottomSheet;
    private TextView tapactionview;
    public TourWisataFragment(){}

    /**
     *
     */
    public static final String KEY_TOURWISATA = "key_tourwisata";
    private static final String TAG = "TourWisataFragment";
    /**
     *
     */
    private static final String BASE_URL = "http://192.168.43.36:8000/";

    /**
     *
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     *
     */
    private ArrayList<TourWisata> mTourWisatas;

    /**
     *
     */
    private TourWisataAdapter mTourWisataAdapter;

    /**
     *
     */
    private TextView mTvSwipeDownInfo;

    /**
     *
     */
    private Context mContext;

    /**
     *
     */
    private Disposable mNetworkConnectivityObserver;

    /**
     *
     */
    private TourWisataService mTourWisataService;

    /**
     *
     */
    private Call<String> mCall;

    /**
     *
     */

    private Callback<String> mCallback = new Callback<String>() {
        @Override
        public void onResponse(@NonNull Call<String> call, @NonNull final Response<String> response) {
            String jsonResponse = response.body();
            mTourWisatas.clear();

            try {
                JSONObject object = new JSONObject(jsonResponse);
                JSONArray jsonArray = object.getJSONArray("result");

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int WisataId = jsonObject.getInt("id");
                        String WisataNama = jsonObject.getString("nama");
                        String WisataDeskripsi = jsonObject.getString("deskripsi");
                        String WisataInfo = jsonObject.getString("info");
                        String WisataPenginapan = jsonObject.getString("penginapan");
                        String WisataTranport = jsonObject.getString("transportasi");
                        String WisataMakan = jsonObject.getString("makan");
                        String WisataLokasi = jsonObject.getString("lokasi");
                        String WisataGambar = jsonObject.getString("gambar");
                        int WisataTiket = jsonObject.getInt("tiket");
                        int WisataHarga = jsonObject.getInt("harga");


                        mTourWisatas.add(
                                new TourWisata(
                                        WisataId,
                                        WisataNama,
                                        WisataDeskripsi,
                                        WisataInfo,
                                        WisataPenginapan,
                                        WisataTranport,
                                        WisataMakan,
                                        WisataLokasi,
                                        WisataGambar,
                                        WisataTiket,
                                        WisataHarga
                                        ));

                    }
                }

                mTourWisataAdapter.notifyDataSetChanged();
                mTvSwipeDownInfo.setText(R.string.swipe_down_info);
                mSwipeRefreshLayout.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(@NonNull Call<String> mCall, @NonNull Throwable t) {
            t.printStackTrace();
        }
    };


    @Override
    public void onAttach(Context mContext) {
        super.onAttach(mContext);
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_tour_wisata, container, false);
        if (savedInstanceState != null) {
            mTourWisatas = savedInstanceState.getParcelableArrayList(KEY_TOURWISATA);
        } else {
            mTourWisatas = new ArrayList<>();
        }

        tapactionlayout = (LinearLayout) viewRoot.findViewById(R.id.tap_action_layout);
        tapactionview = (TextView) viewRoot.findViewById(R.id.tvtapaction);
        imglowprice = (ImageView) viewRoot.findViewById(R.id.imglowprice);
        imghighprice = (ImageView) viewRoot.findViewById(R.id.imghighprice);
        layhighprice = (RelativeLayout) viewRoot.findViewById(R.id.relativehighprice);
        laylowprice = (RelativeLayout) viewRoot.findViewById(R.id.relativelowprice);
        bottomSheet = (View) viewRoot.findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapactionlayout.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionview.setText("Tap to hide");
                    tapactionlayout.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionview.setText("Tap to show");
                    tapactionlayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        tapactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_COLLAPSED){
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else if (mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_EXPANDED){
                    tapactionview.setText("Tap to show");
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }else {
                    tapactionview.setText("Tap to show");
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_DRAGGING);
                }
            }
        });


        laylowprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortLowPrice();
                mTourWisataAdapter.notifyDataSetChanged();
            }
        });

        layhighprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortHighPrice();
                mTourWisataAdapter.notifyDataSetChanged();
            }
        });


        mTourWisataService = (TourWisataService) NetworkUtils.fetchUrl(BASE_URL, TourWisataService.class);

        mTvSwipeDownInfo = viewRoot.findViewById(R.id.tv_swipe_down_info);

        mSwipeRefreshLayout = viewRoot.findViewById(R.id.srl_list);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        mNetworkConnectivityObserver = ReactiveNetwork.observeNetworkConnectivity(mContext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.getState() == NetworkInfo.State.CONNECTED) {
                        if (savedInstanceState == null) {
                            mSwipeRefreshLayout.setEnabled(true);
                            mSwipeRefreshLayout.setRefreshing(true);
                            mTvSwipeDownInfo.setText(R.string.synchronizing);

                            mCall = mTourWisataService.getTourWisata();
                            mCall.enqueue(mCallback);
                        }
                    } else {
                        mTvSwipeDownInfo.setText(R.string.network_is_not_connected);
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mTvSwipeDownInfo.setText(R.string.synchronizing);

            mCall = mTourWisataService.getTourWisata();
            mCall.enqueue(mCallback);
        });

        RecyclerView wisataRecyclerView = viewRoot.findViewById(R.id.rv_list);
        wisataRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        wisataRecyclerView.setLayoutManager(layoutManager);

        mTourWisataAdapter = new TourWisataAdapter(mTourWisatas);
        wisataRecyclerView.setAdapter(mTourWisataAdapter);

        ItemClickSupportUtils.addTo(wisataRecyclerView)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Intent WisataDetailIntent = new Intent(mContext, DeatilWisataActivity.class);
                    TourWisata tourWisata = mTourWisatas.get(position);

                    WisataDetailIntent.putExtra("nama", tourWisata.getNama());
                    WisataDetailIntent.putExtra("deskripsi", tourWisata.getDeskripsi());
                    WisataDetailIntent.putExtra("info", tourWisata.getInfo());
                    WisataDetailIntent.putExtra("penginapan", tourWisata.getPenginapan());
                    WisataDetailIntent.putExtra("transportasi", tourWisata.getTransportasi());
                    WisataDetailIntent.putExtra("makan", tourWisata.getMakan());
                    WisataDetailIntent.putExtra("lokasi", tourWisata.getLokasi());
                    WisataDetailIntent.putExtra("gambar", tourWisata.getGambar());
                    WisataDetailIntent.putExtra("tiket", tourWisata.getTiket());
                    WisataDetailIntent.putExtra("harga", tourWisata.getHarga());


                    startActivity(WisataDetailIntent);
                });

        return viewRoot;
    }


    /*
     * SORT
     */
    private void sortLowPrice()
    {
        Toast.makeText(getActivity(),"Low Price", Toast.LENGTH_SHORT).show();
        Collections.sort(mTourWisatas, new Comparator<TourWisata>() {
            @Override
            public int compare(TourWisata o1, TourWisata o2) {
                int b1 = o1.getHarga();
                int b2 = o2.getHarga();
                return Integer.compare(b1, b2);
            }
        });
    }

    private void sortHighPrice()
    {
        Toast.makeText(getActivity(),"High Price", Toast.LENGTH_SHORT).show();
        Collections.sort(mTourWisatas, new Comparator<TourWisata>() {
            @Override
            public int compare(TourWisata o1, TourWisata o2) {
                int b1 = o1.getHarga();
                int b2 = o2.getHarga();
                return Integer.compare(b2, b1);
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(true);
        mTvSwipeDownInfo.setText(R.string.synchronizing);

        mCall = mTourWisataService.getTourWisata();
        mCall.enqueue(mCallback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_TOURWISATA, mTourWisatas);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNetworkConnectivityObserver.dispose();
    }


}
