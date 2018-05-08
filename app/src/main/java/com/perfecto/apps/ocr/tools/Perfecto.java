package com.perfecto.apps.ocr.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.perfecto.apps.ocr.models.User;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;

/**
 * Created by Hosam Azzam on 13/08/2017.
 */

public class Perfecto {
    public static String BASE_URL = "http://qarar.co/ocr/api/";
    public static String BASE_IMAGE_URL = "http://qarar.co/ocr/";
    public static boolean USER_ISLOGIN = false;
    public static HashMap<String, String> LangCode = new HashMap<String, String>();


    public static void registerUserLogin(Context context, User userModule) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserObject", userModule.GetUserJson());
        editor.apply();
    }

    public static void unRegisterUserLogin(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserObject", null);
        editor.apply();
    }

    public static Boolean getUserLoginState(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userobject = preferences.getString("UserObject", "");
        USER_ISLOGIN = !userobject.equals("");
        return USER_ISLOGIN;

    }

    public static User getUserLoginInfo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userobject = preferences.getString("UserObject", null);
        if (userobject != null) {
            try {
                JSONObject jsonObject;
                JSONParser jsonParser = new JSONParser();
                Object obj = jsonParser.parse(userobject);
                jsonObject = (JSONObject) obj;
                return new User(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    public static void initLangCode() {
        LangCode.put("Afrikaans", "af");
        LangCode.put("Albanian", "sq");
        LangCode.put("Amharic", "am");
        LangCode.put("Arabic", "ar");
        LangCode.put("Armenian", "hy");
        LangCode.put("Azeerbaijani", "az");
        LangCode.put("Basque", "eu");
        LangCode.put("Belarusian", "be");
        LangCode.put("Bengali", "bn");
        LangCode.put("Bosnian", "bs");
        LangCode.put("Bulgarian", "bg");
        LangCode.put("Catalan", "ca");
        LangCode.put("Cebuano", "ceb");
        LangCode.put("Chinese (Simplified)", "zh-CN");
        LangCode.put("Chinese (Traditional)", "zh-TW");
        LangCode.put("Corsican", "co");
        LangCode.put("Croatian", "hr");
        LangCode.put("Czech", "cs");
        LangCode.put("Danish", "da");
        LangCode.put("Dutch", "nl");
        LangCode.put("English", "en");
        LangCode.put("Esperanto", "eo");
        LangCode.put("Estonian", "et");
        LangCode.put("Finnish", "fi");
        LangCode.put("French", "fr");
        LangCode.put("Frisian", "fy");
        LangCode.put("Galician", "gl");
        LangCode.put("Georgian", "ka");
        LangCode.put("German", "de");
        LangCode.put("Greek", "el");
        LangCode.put("Gujarati", "gu");
        LangCode.put("Haitian Creole", "ht");
        LangCode.put("Hausa", "ha");
        LangCode.put("Hawaiian", "haw");
        LangCode.put("Hebrew", "iw");
        LangCode.put("Hindi", "hi");
        LangCode.put("Hmong", "hmn");
        LangCode.put("Hungarian", "hu");
        LangCode.put("Icelandic", "is");
        LangCode.put("Igbo", "ig");
        LangCode.put("Indonesian", "id");
        LangCode.put("Irish", "ga");
        LangCode.put("Italian", "it");
        LangCode.put("Japanese", "ja");
        LangCode.put("Javanese", "jw");
        LangCode.put("Kannada", "kn");
        LangCode.put("Kazakh", "kk");
        LangCode.put("Khmer", "km");
        LangCode.put("Korean", "ko");
        LangCode.put("Kurdish", "ku");
        LangCode.put("Kyrgyz", "ky");
        LangCode.put("Lao", "lo");
        LangCode.put("Latin", "la");
        LangCode.put("Latvian", "lv");
        LangCode.put("Lithuanian", "lt");
        LangCode.put("Luxembourgish", "lb");
        LangCode.put("Macedonian", "mk");
        LangCode.put("Malagasy", "mg");
        LangCode.put("Malay", "ms");
        LangCode.put("Malayalam", "ml");
        LangCode.put("Maori", "mi");
        LangCode.put("Marathi", "mr");
        LangCode.put("Mongolian", "mn");
        LangCode.put("Myanmar (Burmese)", "my");
        LangCode.put("Nepali", "ne");
        LangCode.put("Norwegian", "no");
        LangCode.put("Nyanja (Chichewa)", "ny");
        LangCode.put("Pashto", "ps");
        LangCode.put("Persian", "fa");
        LangCode.put("Polish", "pl");
        LangCode.put("Portuguese", "pt");
        LangCode.put("Punjabi", "pa");
        LangCode.put("Romanian", "ro");
        LangCode.put("Russian", "ru");
        LangCode.put("Samoan", "sm");
        LangCode.put("Scots Gaelic", "gd");
        LangCode.put("Serbian", "sr");
        LangCode.put("Sesotho", "st");
        LangCode.put("Shona", "sn");
        LangCode.put("Sindhi", "sd");
        LangCode.put("Sinhala (Sinhalese)", "si");
        LangCode.put("Slovak", "sk");
        LangCode.put("Slovenian", "sl");
        LangCode.put("Somali", "so");
        LangCode.put("Spanish", "es");
        LangCode.put("Sundanese", "su");
        LangCode.put("Swahili", "sw");
        LangCode.put("Swedish", "sv");
        LangCode.put("Tagalog (Filipino)", "tl");
        LangCode.put("Tajik", "tg");
        LangCode.put("Tamil", "ta");
        LangCode.put("Telugu", "te");
        LangCode.put("Thai", "th");
        LangCode.put("Turkish", "tr");
        LangCode.put("Ukrainian", "uk");
        LangCode.put("Urdu", "ur");
        LangCode.put("Uzbek", "uz");
        LangCode.put("Vietnamese", "vi");
        LangCode.put("Welsh", "cy");
        LangCode.put("Xhosa", "xh");
        LangCode.put("Yiddish", "yi");
        LangCode.put("Yoruba", "yo");
        LangCode.put("Zulu", "zu");
    }

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
