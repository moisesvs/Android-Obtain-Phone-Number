package com.moises.android_obtain_phone_number;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG          = "MainActivity";
    private static final int RESULT_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    requestHint();
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Error", e);
                }
            }
        });
    }

    private void requestHint() throws IntentSender.SendIntentException {

        if (checkPlayServices(this)) {
            GoogleApiClient client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(Auth.CREDENTIALS_API).build();
            client.connect();

            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build();

            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                    client, hintRequest);
            startIntentSenderForResult(intent.getIntentSender(),
                                       RESULT_ACTIVITY, null, 0, 0, 0);

        }

    }

    public static boolean checkPlayServices(Activity activity) {
        final int             PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability                  = GoogleApiAvailability.getInstance();
        int                   resultCode                       = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.v("", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String     id         = credential.getId();
                if (id != null) {
                    Log.v("Telephone ID", id);
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

}
