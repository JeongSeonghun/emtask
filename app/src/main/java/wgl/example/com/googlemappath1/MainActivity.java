package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.graphics.Color;
//import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
        ,GoogleMap.OnMapClickListener{
    private GoogleMap map;
    Button pathBt, resetBt;
    TextView startTxt, stopTxt;
    Marker startMark, stopMark;
    boolean sMarkAdd=true, eMarkAdd=true; //마커 표시 여부
    SupportMapFragment mapfrag;
    Button listShow;
    int rePolyCheck=0;  //경로 개수 확인
    RadioButton startR, stopR;

    String list_val="";    //intent전달용 json값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pathBt= (Button)findViewById(R.id.path);
        resetBt= (Button)findViewById(R.id.reset);

        startTxt= (TextView)findViewById(R.id.start_t);
        stopTxt= (TextView)findViewById(R.id.stop_t);

        startR= (RadioButton)findViewById(R.id.start_r);
        stopR= (RadioButton)findViewById(R.id.stop_r);

        mapfrag= ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapfrag.getMapAsync(this);

        pathBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NodeWgs nodeWgs= new NodeWgs();
                //nodeWgs.execute("https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyCc2PqOCbvrNGtDRwINl4X_tiywxt9TDPA\n");
                //nodeWgs.execute("https://maps.googleapis.com/maps/api/directions/json?origin=-33.866,151.195&destination=-33.866,148.195&key=AIzaSyCc2PqOCbvrNGtDRwINl4X_tiywxt9TDPA");

                if(rePolyCheck<3){
                    nodeWgs.execute("https://maps.googleapis.com/maps/api/directions/json?"
                            +"origin="+startTxt.getText().toString()
                            +"&destination="+stopTxt.getText().toString()
                            +"&key=AIzaSyCc2PqOCbvrNGtDRwINl4X_tiywxt9TDPA");
                }else{
                    Toast.makeText(getApplicationContext(),"3개 이하만 표시합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        resetBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                map.clear();
                sMarkAdd=true;
                eMarkAdd=true;
                rePolyCheck=0;
                list_val="";
                startTxt.setText("");
                stopTxt.setText("");
            }
        });

        listShow= (Button)findViewById(R.id.list_show);
        listShow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), ListActivity.class);

                intent.putExtra("list",list_val);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Vector<Vector<Vector<LatLng>>> searchPath;
        try {
            JSONObject gDirectJo = new JSONObject(intent.getStringExtra("list"));
            
            DirectionsJSONParser2 parser2= new DirectionsJSONParser2();
            searchPath=parser2.parse(gDirectJo);

            System.out.println("test 002 : "+searchPath.size());
            //System.out.println("test 002 : "+searchPath.get(0).get(0).get(0).toString());


            
        } catch (JSONException e) {

            e.printStackTrace();

        }
        
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

       //LatLng seul= new LatLng(37.4632016047, 126.9345984302);
        LatLng la= new LatLng(34.052, -118.246);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(la, 10));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    //google 서비스
    private class NodeWgs extends AsyncTask<String, Integer,String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {

                // 연결 url 설정
                URL url2 = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
                // 연결되었으면.
                if (conn2 != null) {
                    conn2.setConnectTimeout(10000);//최대 연결시간(10초)
                    conn2.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.

                    if (conn2.getResponseCode() == HttpURLConnection.HTTP_OK) {//url주소 사이에 띄어쓰기 존제시 다른코드 반환됨

                        BufferedReader br2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br2.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line );
                        }
                        br2.close();
                    }
                    conn2.disconnect();
                }

            } catch (Exception ex) {
                ex.printStackTrace();

            }

            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {

            String pathCk_s;    //경로 데이터 확인

            Vector<Vector<Vector<LatLng>>> nodeVec= new Vector();

            try {
                JSONObject gDirectJo = new JSONObject(str);

                pathCk_s=gDirectJo.getString("status"); //길찾기 응답요소, 상태코드 참고

                //경로 얻기
                DirectionsJSONParser parser = new DirectionsJSONParser();
                //nodeVec=parser.getall();
                nodeVec=parser.parse(gDirectJo);

                if(rePolyCheck==0)  //세가지 경로 중 처음 경로만 저장
                list_val=gDirectJo.toString();

                if(pathCk(pathCk_s)){
                    addPolyline(nodeVec);
                }else
                    Toast.makeText(getApplicationContext(),"지원되지 않아요!:"+pathCk_s,Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

        //경로결과 확인
        public boolean pathCk(String checkStr){
            boolean check;
            //경로 결과가 없을시 status는 OK가 아닌 다른 값을 보임
            if(checkStr.equals("OK"))
                check=true;
            else
                check=false;
            return check;
        }

        //OnpreExecute 백그라운드 작업 전, 초기화, 셋팅
        //OnPostEexcute 백그란운드 작업 후
        //OnProgressUpdate 백그라운드 중간에 ui(main 매서드

    }

    //polyline 그리기
    public void addPolyline(Vector<Vector<Vector<LatLng>>> node){

        PolylineOptions poly= new PolylineOptions().geodesic(true);

        int[] polColor={Color.RED, Color.BLUE, Color.YELLOW};//횟수에 따른 경로 색 변경용
        //node-> rout:[{legs:[{steps:[{LatLng},...]},...]},...]
        for(int i=0; i<node.size(); i++){
            for(int j=0; j<node.get(i).size(); j++){
                poly.addAll(node.get(i).get(j));
                poly.width(3);
                poly.color(polColor[rePolyCheck]);
            }
        }

        map.addPolyline(poly);

        rePolyCheck+=1;

    }

    @Override
    public void onMapClick(LatLng latLng) {

        if(startR.isChecked()){   //startTxt 선택시
            if(sMarkAdd){   //start Marker 존제 여부 확인 후 추가
                startMark=map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(30)));
                sMarkAdd=false;
            }else{  //start Marker 위치 변경
                startMark.setPosition(latLng);
            }

            //start 좌표 표시 "latitude,longitude"
            startTxt.setText(latLng.latitude+","+latLng.longitude);
        }

        if(stopR.isChecked()){
            if(eMarkAdd){
                stopMark=map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(90)));
                eMarkAdd=false;
            }else{
                stopMark.setPosition(latLng);
            }
            stopTxt.setText(latLng.latitude+","+latLng.longitude);
        }

    }
}
