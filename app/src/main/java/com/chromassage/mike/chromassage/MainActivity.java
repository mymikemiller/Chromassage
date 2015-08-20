package com.chromassage.mike.chromassage;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    PHHueSDK phHueSDK = PHHueSDK.getInstance();

    // Local SDK Listener
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List accessPoint) {
            // Handle your bridge search results here.  Typically if multiple results are returned you will want to display them in a list
            // and let the user select their bridge.   If one is found you may opt to connect automatically to that bridge.
            PHAccessPoint ap = (PHAccessPoint)accessPoint.get(0);
            phHueSDK.connect(ap);
        }

        @Override
        public void onCacheUpdated(List cacheNotificationsList, PHBridge bridge) {
            // Here you receive notifications that the BridgeResource Cache was updated. Use the PHMessageType to
            // check which cache was updated, e.g.
            if (cacheNotificationsList.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
                System.out.println("Lights Cache Updated ");
            }
        }

        @Override
        public void onBridgeConnected(PHBridge b, String username) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            // Here it is recommended to set your connected bridge in your sdk object (as above) and start the heartbeat.
            // At this point you are connected to a bridge so you should pass control to your main program/activity.
            // The username is generated randomly by the bridge.
            // Also it is recommended you store the connected IP Address/ Username in your app here.  This will allow easy automatic connection on subsequent use.

            bridgeConnected(b);
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            phHueSDK.startPushlinkAuthentication(accessPoint);
            // Arriving here indicates that Pushlinking is required (to prove the User has physical access to the bridge).  Typically here
            // you will display a pushlink image (with a timer) indicating to to the user they need to push the button on their bridge within 30 seconds.
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            // Here you would handle the loss of connection to your bridge.
        }

        @Override
        public void onError(int code, final String message) {
            // Here you can handle events such as Bridge Not Responding, Authentication Failed and Bridge Not Found
        }

        @Override
        public void onParsingErrors(List parsingErrorsList) {
            // Any JSON parsing errors are returned here.  Typically your program should never return these.
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phHueSDK.setAppName("HueSandbox");     // e.g. phHueSDK.setAppName("QuickStartApp");
        phHueSDK.setDeviceName(android.os.Build.MODEL);  // e.g. If you are programming for Android: phHueSDK.setDeviceName(android.os.Build.MODEL);


        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(listener);
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);


    }

    public void bridgeConnected(final PHBridge bridge) {
        //PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();

        for(int i = 0; i < 46920; i+=200) {
            final PHLightState lightState = new PHLightState();
            lightState.setHue(i);
            PHBridgeResourcesCache cache = bridge.getResourceCache();
            // And now you can get any resource you want, for example:
            List myLights = cache.getAllLights();
            PHLight light = (PHLight) myLights.get(2);
            bridge.updateLightState(light, lightState);
            try {
                Thread.sleep(100);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
