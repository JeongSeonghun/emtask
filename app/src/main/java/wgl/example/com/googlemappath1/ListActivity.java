package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
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

    boolean click_Ck=true;
    LatLng searchVal_s, searchVal_e;

    Vector<Vector<Vector<LatLng>>> nodeVec= new Vector();
    Vector<LatLng> nodes2;

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
    public void setList(final int leg_Num, final int step_Num){
        //num: step 인덱스, 0=전체

        String legNum="";
        String stepNum="";
        nodes2= new Vector();

        System.out.println("test000 :"+leg_Num+"/"+step_Num);
        
        if(nodeVec.size()<1){
            legNum+="0";
            stepNum+="0";
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
        }
        legTotal.setText(legNum);
        stepTotal.setText(stepNum);


        if(leg_Num==0&&step_Num==0){
            for(int i=0; i<nodeVec.size(); i++){
                for(int j=0; j<nodeVec.get(i).size(); j++){
                    nodes2.addAll(nodeVec.get(i).get(j));
                }
            }
        }else if(leg_Num>0&&step_Num>=0){
            if(step_Num==0&&leg_Num<=Integer.valueOf(legNum)){
                for(int i=0; i<nodeVec.get(leg_Num-1).size(); i++){
                    nodes2.addAll(nodeVec.get(leg_Num-1).get(i));
                }
            }else if(leg_Num<=Integer.valueOf(legNum)&&
                    step_Num<=Integer.valueOf(stepNum)&&step_Num>0){
                nodes2.addAll(nodeVec.get(leg_Num-1).get(step_Num-1));
            }
        }
        node.setText(String.valueOf(nodes2.size()));

        list.setAdapter(new NodeAdapter(getApplicationContext(), R.layout.info, nodes2));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(click_Ck){
                    view.setBackgroundColor(Color.CYAN);


                    TextView tv= (TextView)view.findViewById(R.id.num);
                    tv.setBackgroundColor(Color.WHITE);
                    System.out.println("test4: "+nodes2.get(i));
                    System.out.println("test4: "+view.getId());
                    searchVal_s=nodes2.get(i);
                    click_Ck=false;
                }else{
                    view.setBackgroundColor(Color.GREEN);
                    searchVal_e=nodes2.get(i);
                    showMainActivity(sendJSONString(searchPath(searchVal_s, searchVal_e)));
                }
            }
        });
    }
    
    public Vector<Vector<Vector<LatLng>>> searchPath(LatLng searchL_s, LatLng searchL_e){
        Vector<Vector<Vector<LatLng>>> searchVec= new Vector();
        boolean saveCk=false;
        boolean stopCk=false;
        for(int i=0; i<nodeVec.size(); i++){
            Vector<Vector<LatLng>> sLegs= new Vector();
            for(int j=0; j<nodeVec.get(i).size();j++){
                Vector<LatLng> sSteps= new Vector();
                for(int k=0; k<nodeVec.get(i).get(j).size();k++){
                    if(searchL_s.equals(nodeVec.get(i).get(j).get(k))){
                        saveCk=true;
                    }else if(searchL_e.equals(nodeVec.get(i).get(j).get(k))){
                        saveCk=false;
                        stopCk=true;
                    }
                    if(saveCk){
                        sSteps.add(nodeVec.get(i).get(j).get(k));
                    }else if(stopCk){
                        sSteps.add(nodeVec.get(i).get(j).get(k));
                        break;
                    }
                }
                if(saveCk){
                    sLegs.add(sSteps);
                }else if(stopCk){
                    sLegs.add(sSteps);
                    break;
                }
            }
            if(saveCk) {
                searchVec.add(sLegs);
            }else if(stopCk){
                searchVec.add(sLegs);
                break;
            }
        }
        return searchVec;
    }

    public void showMainActivity(String sendJSON){
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("list",sendJSON);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public String sendJSONString(Vector<Vector<Vector<LatLng>>> searchPath){
        String sendString="{\"routes\":[";

        System.out.println("test000: rout"+searchPath.size());
        for(int i=0; i<searchPath.size();i++){//legs
            if(i==0) sendString+="{";
            else sendString+=",{";
            sendString+="\"legs\":[";
            System.out.println("test000: legs"+searchPath.get(i).size());
            for(int j=0; j<searchPath.get(i).size();j++){//steps
                if(j==0) sendString+="{";
                else sendString+=",{";
                sendString+="\"steps\":[";
                System.out.println("test000: steps"+searchPath.get(i).get(j).size());
                for(int k=0; k<searchPath.get(i).get(j).size();k++){//point
                    if(k==0) sendString+="{";
                    else sendString+=",{";
                    sendString+="\"lat\":\""+String.valueOf(searchPath.get(i).get(j).get(k).latitude)
                            +"\",\"lng\":\""+String.valueOf(searchPath.get(i).get(j).get(k).longitude)
                            +"\"}";
                }
                sendString+="]}";
            }
            sendString+="]}";
        }
        sendString+="]}";

        System.out.println("test005 : "+sendString);


        return sendString;
    }
}
