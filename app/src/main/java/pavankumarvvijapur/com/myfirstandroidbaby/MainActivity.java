package pavankumarvvijapur.com.myfirstandroidbaby;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    Location location;
    static TextView mTextView;

    static TextView mTextView1;

    static TextView mTextView2;

    static TextView mTextView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextView = (TextView) findViewById(R.id.text1);
        mTextView1  = (TextView) findViewById(R.id.text2);
                mTextView2  = (TextView) findViewById(R.id.text3);
        mTextView3 = (TextView) findViewById(R.id.text4);
        setSupportActionBar(toolbar);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        getLocationAddress();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, Help us locate you by enabling it ")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onResume() {
        getLocationAddress();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private boolean getLocationAddress()
    {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (locationManager != null) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return false;
                }
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Log.e("TAG", "GPS is on here");
                    AddressFromLocation.getAddressFromLocation(location.getLatitude(), location.getLongitude(), getApplicationContext(), new GeocoderHandler());

                    //latitude = location.getLatitude();
                    //longitude = location.getLongitude();
                    //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                }
                else{

                    Log.e("TAG", "GPS need tobe on ");
                    //This is what you need:
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                }
            }
        }
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

    @Override
    public void onLocationChanged(Location location) {
        Log.e("TAG", "GPS is on and got changed ");
        AddressFromLocation.getAddressFromLocation(location.getLatitude(), location.getLongitude(), getApplicationContext(), new GeocoderHandler());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

    class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress = null;
            String City = null;
            String postalCode = null;
            String mSubLocality = null;
            String locality = null;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    mSubLocality = bundle.getString("sublocality");
                    postalCode = bundle.getString("postalCode");
                    locality = bundle.getString("locality");
                    break;
                default:
                    locationAddress = null;
            }
            Log.e("TAG", "GPS " + "City" + locality + " SubLocality " + mSubLocality + "postal Code" + postalCode);
            MainActivity.mTextView.setText(locationAddress);
            MainActivity.mTextView1.setText(locality);
            MainActivity.mTextView2.setText(mSubLocality);
            MainActivity.mTextView3.setText(postalCode);
        }
    }