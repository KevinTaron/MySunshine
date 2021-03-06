package com.example.android.sunshine.watchface;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

public class SunshineWatchWeatherService extends WearableListenerService {


        private static final String TAG = "SunshineActivity";

        private static final String START_ACTIVITY_PATH = "/start-activity";
        private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
        public static final String COUNT_PATH = "/count";
        public static final String WEATHER_PATH = "/weather";
        public static final String IMAGE_KEY = "photo";
        GoogleApiClient mGoogleApiClient;

        @Override
        public void onCreate() {
            super.onCreate();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();
            mGoogleApiClient.connect();
            Log.d(TAG, "connected");
        }

    @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
            if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
                ConnectionResult connectionResult = mGoogleApiClient
                        .blockingConnect(30, TimeUnit.SECONDS);
                if (!connectionResult.isSuccess()) {
                    Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient, "
                            + "error code: " + connectionResult.getErrorCode());
                    return;
                }
            }

            // Loop through the events and send a message back to the node that created the data item.
            for (DataEvent event : dataEvents) {
                Uri uri = event.getDataItem().getUri();
                String path = uri.getPath();
                if (COUNT_PATH.equals(path)) {
                    // Get the node id of the node that created the data item from the host portion of
                    // the uri.
                    String nodeId = uri.getHost();
                    // Set the data of the message to be the bytes of the Uri.
                    byte[] payload = uri.toString().getBytes();

                    // Send the rpc
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                            payload);
                }
            }
        }

        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            LOGD(TAG, "onMessageReceived: " + messageEvent);

        }

        @Override
        public void onPeerConnected(Node peer) {
            LOGD(TAG, "onPeerConnected: " + peer);
        }

        @Override
        public void onPeerDisconnected(Node peer) {
            LOGD(TAG, "onPeerDisconnected: " + peer);
        }

        public static void LOGD(final String tag, String message) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message);
            }
        }
    }