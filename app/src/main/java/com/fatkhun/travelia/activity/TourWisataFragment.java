package com.fatkhun.travelia.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fatkhun.travelia.Utils.ItemClickSupportUtils;
import com.fatkhun.travelia.Utils.NetworkUtils;
import com.fatkhun.travelia.adapter.TourWisataAdapter;
import com.fatkhun.travelia.model.TourWisata;
import com.fatkhun.travelia.service.TourWisataService;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private LinearLayout layoutHigh, layoutLow;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
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

        fab = (FloatingActionButton) viewRoot.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) viewRoot.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) viewRoot.findViewById(R.id.fab2);
        layoutHigh = (LinearLayout) viewRoot.findViewById(R.id.layoutFabHigh);
        layoutLow = (LinearLayout) viewRoot.findViewById(R.id.layoutFabLow);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortLowPrice();
                mTourWisataAdapter.notifyDataSetChanged();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
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

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            layoutHigh.startAnimation(fab_close);
            layoutLow.startAnimation(fab_close);
            layoutHigh.setClickable(false);
            layoutLow.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            layoutHigh.startAnimation(fab_open);
            layoutLow.startAnimation(fab_open);
            layoutHigh.setClickable(true);
            layoutLow.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }


    /*
     * SORT
     */
    private void sortLowPrice()
    {
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
