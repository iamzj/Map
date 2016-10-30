package com.baidumap;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidumap.bean.Geo;
import com.baidumap.bean.Status;
import com.baidumap.bean.StatusList;
import com.baidumap.bean.User;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yifengZhang on 2016/9/18 19:10.
 * 描述:(请用一句话描述这个内容)
 */
public class SecondActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListenner();
    public LocationClient mLocClient;
            
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mMapView = (MapView) findViewById(R.id.bmapView);

        initBaiduMap();
    }

    private void initBaiduMap(){
//        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数

        mBaiduMap = mMapView.getMap();

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();  //开始定位

    }


    private void loadData() throws IOException {
        InputStream is = getAssets().open("json.js");

        byte[] by = new byte[30];
        int length=0;
        StringBuilder sb = new StringBuilder();
        while((length=is.read(by))!=-1){
            sb.append(new String(by,0,length));
        }
        String message = sb.toString();

        StatusList weiboStatuses = StatusList.parse(message);

        for (Status status : weiboStatuses.getStatusList()){
            initOverlay(status);
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {

                Bundle bundle = marker.getExtraInfo();
                final User user = (User) bundle.getSerializable("user");

                InfoWindow.OnInfoWindowClickListener listener  = new InfoWindow.OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        Toast.makeText(getApplicationContext(),user.getScreen_name()+","+user.getId(),Toast.LENGTH_LONG).show();
                    }
                };

                View view = LayoutInflater.from(SecondActivity.this).inflate(R.layout.activity_main_user_name,null);
                TextView textView = (TextView) view.findViewById(R.id.tv);
                textView.setText(user.getScreen_name());

                LatLng latLng = marker.getPosition();
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(view), latLng, -47, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    public void initOverlay(final Status status) {
//        BitmapDescriptor bd = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_gcoding);

        final View view = LayoutInflater.from(this).inflate(R.layout.activity_main_user,null);
        final CircleImageView imageView = (CircleImageView) view.findViewById(R.id.view);
        WxhlImageLoader.loaderHeadImage(this, imageView, status.getUser().getProfile_image_url(), new BitmapCache.ImageLoaderCallBack() {
            @Override
            public void imageLoaderSuccess(String url, Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);

                BitmapDescriptor bd = BitmapDescriptorFactory.fromView(view);
                Geo geo = status.getGeo();
                LatLng ll = new LatLng(Double.parseDouble(geo.getLatitude()), Double.parseDouble(geo.getLongitude()));

                MarkerOptions ooA = new MarkerOptions().position(ll).icon(bd)
                        .zIndex(9).draggable(true);
                // 掉下动画
                ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

                Bundle bundle = new Bundle();
                bundle.putSerializable("user",status.getUser());
                mMarkerA.setExtraInfo(bundle);
            }
        });

    }


    /**
     * 定位SDK监听函数
     */
    class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);

            mLocClient.stop();  //定位成功后，关闭定位

            try {
                loadData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
