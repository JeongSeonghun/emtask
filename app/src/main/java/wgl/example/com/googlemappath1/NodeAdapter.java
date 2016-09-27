package wgl.example.com.googlemappath1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Vector;

public class NodeAdapter extends BaseAdapter {
    Context context;
    Vector<LatLng> al= new Vector();
    int layout;

    LayoutInflater inf;// xml에 정의된 자원(resource)들을 view로 반환

    public NodeAdapter(Context context, int layout, Vector<LatLng> al){
        this.context= context;
        this.layout= layout;
        this.al=al;
        this.inf= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 어댑터로 처리될 데이터 개수 반환
    @Override
    public int getCount() {
        return al.size();
    }

    // 특정 위치 데이터 반환
    @Override
    public Object getItem(int i) {
        return al.get(i);
    }

    // 특정 위치 데이터 ID 반환
    @Override
    public long getItemId(int i) {
        return i;
    }

    // 특정 위치의 데이터를 출력할 뷰를 얻음
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {//(해당행 위치, 해당행 레이아웃, 해당행 부모뷰(리스트뷰))

        if (view == null) {//해당하는 행에 레이아웃객체 없을 시
            //xml파일로 레이아웃 객체 생성
            view = inf.inflate(layout, null);
        }
        TextView num = (TextView) view.findViewById(R.id.num);//view레이아웃 객체내의 텍스트뷰 id 사용
        TextView value = (TextView) view.findViewById(R.id.wgs);

        num.setText(String.valueOf(i+1));
        value.setText(al.get(i).toString());


        return view;
    }
    /*
    View inflate( int resource, ViewGroup root ) 현제 사용
    View inflate( XmlPullParser parser, ViewGroup root )
    View inflate( XMLPullParser parser, ViewGroup root, boolean attachToRoot )
    View inflate( int resource, ViewGroup root, boolean attachToRoot )
    */
}
