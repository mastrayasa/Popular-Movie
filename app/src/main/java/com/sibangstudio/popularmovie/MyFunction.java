package com.sibangstudio.popularmovie;

/**
 * Created by wayanmastrayasa on 11/12/17.
 */

import android.net.ParseException;

import com.sibangstudio.popularmovie.data.MovieData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class MyFunction {

    public static final String TAG_TITLE = "title";
    public static final String TAG_POSTER = "poster_path";
    public static final String TAG_OVERVIEW = "overview";
    public static String rupiah(int harga){


        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(harga);

    }


    public static MovieData setDariJson(JSONObject json_data){
        MovieData dirs = new MovieData();
        try {
            dirs.setTitle(json_data.getString(TAG_TITLE));
            dirs.setPoster_path(json_data.getString(TAG_POSTER));
            dirs.setOverview(json_data.getString(TAG_OVERVIEW));


        } catch (JSONException e1) {

        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return dirs;
    }
}
