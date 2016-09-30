package wgl.example.com.googlemappath1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LogActivity extends AppCompatActivity {
    TextView logContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        logContent= (TextView)findViewById(R.id.log_content);

        readLog();
    }

    public void readLog(){
        String log="";
        try {
            FileInputStream fin = openFileInput("log.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));


            for(;;){
                String line= br.readLine();
                if(line==null)
                    break;
                log+=line+"\n";
            }
            /*
            String line= br.readLine();
            log+=line;
            */
            br.close();
            logContent.setText(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
