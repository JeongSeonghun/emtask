package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class ListActivity extends AppCompatActivity {
    ListView list;
    String receive="";
    TextView legTotal;
    TextView stepTotal;
    TextView node;
    EditText searchstep;
    EditText searchleg;
    Button searchBt;
    Vector legs= new Vector();
    Vector<Vector> steps= new Vector();
    DirectionsJSONParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        list= (ListView)findViewById(R.id.list);
        legTotal= (TextView)findViewById(R.id.leg_total);
        stepTotal= (TextView)findViewById(R.id.step_total);
        node= (TextView)findViewById(R.id.node);
        searchstep= (EditText)findViewById(R.id.step_num);
        searchleg= (EditText)findViewById(R.id.leg_num);
        searchBt= (Button)findViewById(R.id.search);

        Intent intent= getIntent();

        receive= intent.getStringExtra("list");

        setList(receive,0,0);

        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setList(receive, Integer.valueOf(searchleg.getText().toString())
                        ,Integer.valueOf(searchstep.getText().toString()));
            }
        });
    }

    //경로 node들 리스트뷰에 표시
    public void setList(String receive, int leg_Num, int step_Num){
        //num: step 인덱스, 0=전체
        Vector<Vector<LatLng>> nodeVec= new Vector();
        String pathCk_s="";

        try {


            JSONObject gDirectJo = new JSONObject(receive);

            pathCk_s=gDirectJo.getString("status");

            parser = new DirectionsJSONParser();

            nodeVec=parser.parse(gDirectJo);//routs<i_legs<j_step>>


        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(pathCk_s.equals("OK")){
            Vector<LatLng> nodeAll= new Vector();
            String legNum="";
            String stepNum="";

            System.out.println("test000:"+nodeVec.size());

            if(nodeVec.size()<2){
                legNum+="1";
                legTotal.setText(legNum);
                stepNum+=String.valueOf(parser.getStepsSize(0));
                stepTotal.setText(stepNum);
            }
            else{
                for(int i=0; i<nodeVec.size(); i++){
                    legNum+="/"+String.valueOf(i+1);
                    stepNum+="/"+String.valueOf(parser.getStepsSize(i));
                }
                legTotal.setText(legNum);
                stepTotal.setText(stepNum);
            }



            if(leg_Num==0&&step_Num==0){
                for(int i=0; i<nodeVec.size(); i++){        //i번째 leg의 좌표Vector Vector<Vector<LatLng>>
                    for(int j=0; j<nodeVec.get(i).size(); j++){ //배열의 j번째 LatLng 객체 Vector<LatLng>
                        nodeAll.add(nodeVec.get(i).get(j));
                    }
                }
                node.setText(String.valueOf(nodeAll.size()));
                list.setAdapter(new NodeAdapter(getApplicationContext(), R.layout.info, nodeAll));
            }else{
                if(step_Num==0){
                    for(int i=0; i<nodeVec.get(leg_Num-1).size(); i++){
                        nodeAll.add(nodeVec.get(leg_Num-1).get(i));
                    }

                }else if(leg_Num<=Integer.valueOf(legNum)&&
                        step_Num<=Integer.valueOf(stepNum)){
                    nodeAll=parser.getStep(leg_Num-1,step_Num-1);
                }
                node.setText(String.valueOf(nodeAll.size()));
                list.setAdapter(new NodeAdapter(getApplicationContext(), R.layout.info, nodeAll));
            }

        }

    }
}
