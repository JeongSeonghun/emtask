package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    DirectionsJSONParser parser;
    String pathCk_s="";

    Vector<Vector<Vector<LatLng>>> nodeVec= new Vector();

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

        receiveNodes(receive);

        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int leg_i, step_i;

                try{
                    if(searchleg.getText().toString().equals(""))
                        leg_i=0;
                    else
                        leg_i=Integer.valueOf(searchleg.getText().toString());

                    if(searchstep.getText().toString().equals(""))
                        step_i=0;
                    else
                        step_i=Integer.valueOf(searchstep.getText().toString());

                    if(pathCk_s.equals("OK")){
                        setList(leg_i,step_i);
                    }
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void receiveNodes(String receive){

        try {
            JSONObject gDirectJo = new JSONObject(receive);

            pathCk_s=gDirectJo.getString("status");

            parser = new DirectionsJSONParser();

            nodeVec=parser.parse(gDirectJo);    //ndedVec(rout):[legs:[steps:[point:{LatLng},...],...],...]


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(pathCk_s.equals("OK")){
            setList(0,0);
        }

    }


    //경로 node들 리스트뷰에 표시
    //public void setList(String receive, int leg_Num, int step_Num){
    public void setList(int leg_Num, int step_Num){
        //num: step 인덱스, 0=전체

        Vector<LatLng> nodes2= new Vector();
        String legNum="";
        String stepNum="";

        System.out.println("test000 :"+leg_Num+"/"+step_Num);


        if(nodeVec.size()<1){
            legNum+="0";
            legTotal.setText(legNum);
            stepNum+="0";
            stepTotal.setText(stepNum);
        }else{
            for(int i=0; i<nodeVec.size(); i++){
                if(i==0){
                    legNum+=String.valueOf(i+1);
                    stepNum+=String.valueOf(nodeVec.get(i).size());
                }else{
                    legNum+="/"+String.valueOf(i+1);
                    stepNum+="/"+String.valueOf(nodeVec.get(i).size());
                }
            }
            legTotal.setText(legNum);
            stepTotal.setText(stepNum);
        }


        if(leg_Num==0&&step_Num==0){
            for(int i=0; i<nodeVec.size(); i++){
                for(int j=0; j<nodeVec.get(i).size(); j++){
                    nodes2.addAll(nodeVec.get(i).get(j));
                }
            }
            node.setText(String.valueOf(nodes2.size()));
            list.setAdapter(new NodeAdapter(getApplicationContext(), R.layout.info, nodes2));
        }else if(leg_Num>0&&step_Num>=0){
            if(step_Num==0&&leg_Num<=Integer.valueOf(legNum)){
                for(int i=0; i<nodeVec.get(leg_Num-1).size(); i++){
                    nodes2.addAll(nodeVec.get(leg_Num-1).get(i));
                }

            }else if(leg_Num<=Integer.valueOf(legNum)&&
                    step_Num<=Integer.valueOf(stepNum)&&step_Num>0){
                nodes2.addAll(nodeVec.get(leg_Num-1).get(step_Num-1));
            }
            node.setText(String.valueOf(nodes2.size()));
            list.setAdapter(new NodeAdapter(getApplicationContext(), R.layout.info, nodes2));
        }

    }
}
