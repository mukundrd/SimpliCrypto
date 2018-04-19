package com.trayis.simplicrypto.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trayis.simplicrypto.CryptoException;
import com.trayis.simplicrypto.Cryptography;


public class MainActivity extends AppCompatActivity {

    static final String TAG = "SimpleKeystoreApp";

    private static final String USER_DATA = "user_data";

    private View encButton, decButton;

    private static final Gson GSON = new Gson();

    String data = "{\n" +
            "      \"accountType\": 2,\n" +
            "      \"createTime\": 1523873479388,\n" +
            "      \"name\": \"Mukund Desai\",\n" +
            "      \"emailId\": \"muks.dev@gmail.com\",\n" +
            "      \"firstName\": \"Mukund\",\n" +
            "      \"_id\": \"1651085911646378\",\n" +
            "      \"idToken\": \"EAAKWXN0ZApK8BAAnPWxhg9bLDhAvW2MdnWBbV1ZC25W6t7IgYZAyCsBMm6mHT44a6lBrOxSMaP9q4TeDgQkNpSXwSiF8Ck3oYXvhDzzz5EQPAUhpdV2koAIHI4fak1ng5PJ3KJuhquE9xZCy55p3Ts2B6nNx3BC963vYmoKWcXFlDJ62ltuvWQlK0ZBvPg91aZB20QaATj6h8R51yZCNJasekQrDaQiLz4ZD\",\n" +
            "      \"lastName\": \"Desai\",\n" +
            "      \"location\": \"\",\n" +
            "      \"oAuth\": {\n" +
            "        \"token\": \"EAAKWXN0ZApK8BAAnPWxhg9bLDhAvW2MdnWBbV1ZC25W6t7IgYZAyCsBMm6mHT44a6lBrOxSMaP9q4TeDgQkNpSXwSiF8Ck3oYXvhDzzz5EQPAUhpdV2koAIHI4fak1ng5PJ3KJuhquE9xZCy55p3Ts2B6nNx3BC963vYmoKWcXFlDJ62ltuvWQlK0ZBvPg91aZB20QaATj6h8R51yZCNJasekQrDaQiLz4ZD\",\n" +
            "        \"type\": \"fb\"\n" +
            "      },\n" +
            "      \"picUrl\": \"https://graph.facebook.com/1651085911646378/picture?height=400&width=400&migration_overrides=%7Boctober_2012%3Atrue%7D\",\n" +
            "      \"serverAuthCode\": \"\",\n" +
            "      \"username\": \"Mukund Desai\"\n" +
            "    }";

    private Cryptography mCryptography;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        encButton = findViewById(R.id.encryptButton);
        decButton = findViewById(R.id.decryptButton);

        mCryptography = new Cryptography(this);

        encButton.setOnClickListener(view -> {
            try {
                String text = mCryptography.encrypt(data);
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                preferences.edit().putString(USER_DATA, text).commit();
            } catch (CryptoException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            encButton.setEnabled(false);
        });

        decButton.setOnClickListener(view -> {
            try {
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                String string = preferences.getString(USER_DATA, null);
                String text = mCryptography.decrypt(string);
                UserModel userModel = GSON.fromJson(text, UserModel.class);
                Toast.makeText(MainActivity.this, userModel.toString(), Toast.LENGTH_SHORT).show();
            } catch (CryptoException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });

        new Thread(() -> {
            try {
                mCryptography.initKeys();
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                String string = preferences.getString(USER_DATA, null);
                final boolean empty = TextUtils.isEmpty(string);
                runOnUiThread(() -> {
                    encButton.setEnabled(empty);
                    decButton.setEnabled(true);
                });
            } catch (CryptoException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }).start();

    }

}
