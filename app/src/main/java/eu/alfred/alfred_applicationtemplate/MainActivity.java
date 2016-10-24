package eu.alfred.alfred_applicationtemplate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Map;

import eu.alfred.api.PersonalAssistant;
import eu.alfred.api.PersonalAssistantConnection;
import eu.alfred.api.proxies.interfaces.ICadeCommand;
import eu.alfred.ui.AppActivity;
import eu.alfred.ui.CircleButton;

public class MainActivity extends AppActivity implements ICadeCommand {

    private final static String TAG = "MA:MainActivity";
    private PersonalAssistant PA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PA = new PersonalAssistant(this);
        PA.setOnPersonalAssistantConnectionListener(new PersonalAssistantConnection() {
            @Override
            public void OnConnected() {
                Log.i(TAG, "PersonalAssistantConnection connected");
            }
            @Override
            public void OnDisconnected() {
                Log.i(TAG, "PersonalAssistantConnection disconnected");
            }
        });

        PA.Init();


        circleButton = (CircleButton) findViewById(R.id.voiceControlBtn);
        circleButton.setOnTouchListener(new MicrophoneTouchListener());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        getSharedPreferences("global_settings", MODE_ENABLE_WRITE_AHEAD_LOGGING);
    }
    @Override
    public void performAction(String s, Map<String, String> map) {
        Log.d("Perform Action string", s);
        Log.d("Perform Action string", map.toString());
    }

    @Override
    public void performWhQuery(String s, Map<String, String> map) {

    }

    @Override
    public void performValidity(String s, Map<String, String> map) {

    }

    @Override
    public void performEntityRecognizer(String s, Map<String, String> map) {

    }
}
