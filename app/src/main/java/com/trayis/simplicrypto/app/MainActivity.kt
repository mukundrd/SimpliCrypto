package com.trayis.simplicrypto.app

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.trayis.simplicrypto.CryptoException
import com.trayis.simplicrypto.Cryptography


class MainActivity : AppCompatActivity() {

    private var encButton: View? = null
    private var decButton: View? = null

    internal var data = "{\n" +
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
            "    }"

    private var mCryptography: Cryptography? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        encButton = findViewById(R.id.encryptButton)
        decButton = findViewById(R.id.decryptButton)

        mCryptography = Cryptography(this)

        encButton!!.setOnClickListener { view ->
            try {
                val text = mCryptography!!.encrypt(data)
                getPreferences(Context.MODE_PRIVATE).edit().putString(USER_DATA, text).apply()
            } catch (e: CryptoException) {
                Log.e(TAG, e.message, e)
            }

            encButton!!.isEnabled = false
        }

        decButton!!.setOnClickListener { view ->
            try {
                val string = getPreferences(Context.MODE_PRIVATE).getString(USER_DATA, null)
                val text = mCryptography!!.decrypt(string!!)
                val userModel = GSON.fromJson(text, UserModel::class.java)
                Toast.makeText(this@MainActivity, userModel.toString(), Toast.LENGTH_SHORT).show()
            } catch (e: CryptoException) {
                Log.e(TAG, e.message, e)
            }
        }

        Thread {
            try {
                mCryptography!!.initKeys()
                val string = getPreferences(Context.MODE_PRIVATE).getString(USER_DATA, null)
                val empty = TextUtils.isEmpty(string)
                runOnUiThread {
                    encButton!!.isEnabled = empty
                    decButton!!.isEnabled = true
                }
            } catch (e: CryptoException) {
                Log.e(TAG, e.message, e)
            }
        }.start()

    }

    companion object {

        internal const val TAG = "SimpleKeystoreApp"

        private const val USER_DATA = "user_data"

        private val GSON = Gson()
    }

}
