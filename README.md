# ALFRED ApplicationTemplate
A template to create new Android apps for the ALFRED project


<div style="text-align:center"><img src="http://alfred.eu/wp-content/uploads/logo1.png" /></div>

## The library for the Personal Assistant
The library is already included in this project, there is no need to add it or to include it in another way.

 Make sure your MainActivity can extend from „AppActivity“, which is part of the Personal Assistant core library
* What the AppActivity does:
 * It integrates all modules of the ALFRED ecosystem
 * It registers the buttons to interact with ALFRED by using speech commands
 * It registers receivers for the speech interaction


The code should like somehow like this:
```Java
public class MainActivity extends AppActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                circleButton = (CircleButton) findViewById(R.id.voiceControlBtn);
                circleButton.setOnTouchListener(new CircleTouchListener());
        }
}
```
As you can see, you are referencing to a „CircleButton“. It is an overlay to your app GUI, so that you also will be able to continue speaking, although you are not in the Personal Assistent App itself. Define the Button also in your activity_main.xml-file:
```XML
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="eu.alfred.alfred_applicationtemplate.MainActivity">

    <include layout="@layout/voice_btn_layout"
        android:visibility="visible" />
</RelativeLayout>
```

Till now, the app has a microphone button. Of course, you want to use the GameManager, HealthMonitor etc. as well. In addition, some methods only should be called if they are accessed via speech. Thats why you AppActivity forces you to override the following four methods:
```Java
public class MainActivity extends AppActivity {
  final static String HELP_TO_POSTURE_ACTION = "HowToPostureAction";
  @Override protected void onCreate(Bundle savedInstanceState)
  {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);
     circleButton = (CircleButton) findViewById(R.id.voiceControlBtn);
     circleButton.setOnTouchListener(new CircleTouchListener());
   }

   @Override public void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
  }
  @Override public void performAction(final String calledAction, final Map<String, String> map)
  {
    switch (calledAction)
    { case HELP_TO_POSTURE_ACTION:
      HelpToPostureAction htpa = new HelpToPostureAction(this, cade);
      htpa.performAction(calledAction, map); break;
      default: break;
    }
  }
```

* PerformAction is called every time, when your DDD sends an action command to your device
(for instance, call my buddy John)
* PerformWhQuery is called every time, when your DDD sends a query to your device
(for instance, return the phone number for John)
* PerformValidity is called every time, when your DDD sends a validity request to your device (for instance, is there a phone number for my parameter „John“?)
* PerformEntityRecognizer is called every time, when your DDD asks your device, if is has entries for a request (for instance, are there phone numbers for my parameter „John“?)
* Every of these methods have firstly the name of the action as a parameter, secondly a map of other arguments your action to call might use. Here, actionname is „HowToPostureAction“, which is figured out in the switch-construct.

## Example
In this example, we want to call an action, which shows an image with body posture instructions. Because we may launch different kinds of actions / query, it is recommended to use own action / query classes for this, like „HelpToPostureAction“. You could give the context and wrappers, you want to use as parameters.
<br />
<br />
<br />
Lets see the action we just called.<br />
Important here: Implement ICadeCommand as an interface, so that „performAction(…)“ and others are callable in this class as well.<br /><br />
Here, we use the arguments, our called action has beside it (Map<String,String>). For this example, the map has one key-value-pair. The key is called „selected_posture“ and is self explaining. The value however could be another String called „lie“, „sit“ or „stand“. Dependent on what the user said, the app sets an ImageView to its screen, telling how to sit, stand or lie correctly.
```Java
public class HelpToPostureAction implements ICadeCommand {
  final static String LIE = "lie";
  final static String SIT = "sit";
  final static String STAND = "stand";
  MainActivity main; Cade cade;

  public HelpToPostureAction(MainActivity main, Cade cade) {
    this.main = main; this.cade = cade;
  }

 @Override public void performAction(String s, Map<String, String> map) {
    ImageView howToImage = (ImageView) main.findViewById(R.id.image_howto);
    String posture = map.get("selected_posture");
    if(posture.equals(STAND)) {
      howToImage.setImageResource(R.drawable.howto_stand);
    } else if(posture.equals(SIT)) {
      howToImage.setImageResource(R.drawable.howto_sit);
    } else {
      howToImage.setImageResource(R.drawable.howto_lie);
    }

    cade.sendActionResult(true);
  }

  @Override public void performWhQuery(String s, Map<String, String> map) {

  }

  @Override public void performValidity(String s, Map<String, String> map) {

  }

  @Override public void performEntityRecognizer(String s, Map<String, String> map) {
  }
}
```
So, we have launched our app with speech, and did an action with it. Finally, we have to inform CADE, that our speech ended, to CADE my say something like a results report.
Therefore, we use „cade.sendActionResult(true)“. The same procedure has to be followed for queries, validity checks, and entity recognizers.


Of course, as said in the beginning, you are able to use other APIs like KIS. Just use them in your action / query just before you call „cade.sendActionResult(true)“. Here an example for KIS, reading from a bucket created before, searching for entries for a specific date.
Except for CADE, most of other API methods have responses. So, continue coding when OnSuccess was called.


```Java
JSONObject obj = new JSONObject();
try {
  obj.put("date", day+"_"+(month+1)+"_"+year);
} catch (Exception e) {
  throw e;
}
cloudStorage.readJsonArray("CalendarBucket", obj, new BucketResponse() {

  @Override public void OnSuccess(JSONObject jsonObject) {
  }

  @Override public void OnSuccess(JSONArray jsonArray) {
    try {
      for(int i = 0; i < jsonArray.length(); i++) {
        TextView tv = new TextView(getApplicationContext());
         tv.setText(jsonArray.getJSONObject(i).toString());
          eventView.addView(tv);
        }
      }
      catch (JSONException e) {
         e.printStackTrace();
       }
     }
     @Override public void OnSuccess(byte[] bytes) {

     }

     @Override public void OnError(Exception e) {
     }
    });
```

Got lost in the tutorial? Just contact us: <a href="mailto:alfred@alfred.eu"> alfred@alfred.eu </a> or visit our website at <a href="http://www.alfred.eu/"> http://www.alfred.eu/ </a>

## Contribute
 Contributions are always welcome! Just drop us an E-Mail <a href="mailto:alfred@alfred.eu"> alfred@alfred.eu </a>
