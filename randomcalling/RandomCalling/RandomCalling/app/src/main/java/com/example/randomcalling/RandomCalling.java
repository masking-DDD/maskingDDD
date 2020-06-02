package com.example.randomcalling;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.randomcalling.MainActivity;


import java.util.ArrayList;
import java.util.Collections;


public class RandomCalling extends AppCompatActivity {

    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        public ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> contacts = new ArrayList<String>();
            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
            while (c.moveToNext()) {
                //contactName(이름), phNumber(번호) 변수
                String phNumber = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(phNumber);
            }
            Collections.sort(contacts);
            c.close();
            //System.out.println(contacts.get(0));
            return contacts;
        }
        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            // TODO Auto-generated method stub
            super.onPostExecute(contacts);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults)
    {
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
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
