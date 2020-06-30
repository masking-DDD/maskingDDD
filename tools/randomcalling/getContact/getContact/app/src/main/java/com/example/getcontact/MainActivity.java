package com.example.getcontact;
import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;

import com.ram.displayallcontacts.R;

public class MainActivity extends Activity {
    ListView list;
    LinearLayout ll;
    Button loadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.listView1);

        loadBtn =  findViewById(R.id.button1);
        loadBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
            }
        });

    }

    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd = ProgressDialog.show(MainActivity.this, "Loading Contacts",
                    "Please Wait");
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            ArrayList<String> contacts = new ArrayList<String>();

            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            while (c.moveToNext()) {

                String contactName = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phNumber = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contacts.add(contactName + ":" + phNumber);

            }
            c.close();

            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            // TODO Auto-generated method stub
            super.onPostExecute(contacts);

            pd.cancel();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getApplicationContext(), R.layout.text, contacts);

            list.setAdapter(adapter);

        }
    }
    public void callPhoneNumber(){
        String TAG = "RandomCalling";
        try
        {
            Log.e(TAG,"try문 진입");
            if(Build.VERSION.SDK_INT > 22)
            {
                Log.e(TAG,"if문 진입");
                if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RandomCalling.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    Log.e(TAG, "ifif문 진입");
                    return;
                }
                Log.e(TAG, "action call 실행");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                //lca의 값 받아와
                ArrayList<String> contacts = lca.doInBackground();

                callIntent.setData(Uri.parse("tel:%s" + contacts.get(0)));
                startActivity(callIntent);

            }
            else {
                Log.e(TAG,"else문 진입");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                //lca의 값 받아와
                ArrayList<String> contacts = lca.doInBackground();

                callIntent.setData(Uri.parse("tel:%s" + contacts.get(0)));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG,"catch");
            ex.printStackTrace();
        }
    }

}