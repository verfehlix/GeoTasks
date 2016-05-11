package pc.com.geotasks.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import pc.com.geotasks.model.Location;

/**
 * Created by fmagnus, totto on 05.05.2016.
 */
public class Utils {

    /*
    * turns an array list of locations into a json array so we can save it into the database
    * author: fmagnus
    * */
   public static String serializeLocations(ArrayList<Location> locations) {

        JSONObject jResult = new JSONObject();// main object
        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

        for (int i = 0; i < locations.size(); i++) {
            JSONObject jGroup = new JSONObject();// /sub Object

            try {
                jGroup.put("locationName", locations.get(i).getLocationName());
                jGroup.put("locationAddress", locations.get(i).getLocationAddress());
                jGroup.put("longitude", locations.get(i).getLongitude());
                jGroup.put("latitude", locations.get(i).getLatitude());
                jGroup.put("radius", locations.get(i).getRadius());

                jArray.put(jGroup);

                // /itemDetail Name is JsonArray Name
                jResult.put("locations", jArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

       return jResult.toString();
    }

    /*
    * turns a json array into an array list of locations so we can use the location data from the database
    * author: fmagnus
    * */
    public static ArrayList<Location> deserializeLocations(String jsonString) {
        if(jsonString.length() != 0 && ! jsonString.equals("{}") ){
            try {
                JSONObject json = new JSONObject(jsonString);
                JSONArray  jsonArray = json.optJSONArray("locations");

                ArrayList<Location> locs = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonLoc = new JSONObject(jsonArray.getString(i));
                    Location loc = new Location(jsonLoc.getString("locationName"), jsonLoc.getString("locationAddress"), jsonLoc.getDouble("latitude"), jsonLoc.getDouble("longitude"), jsonLoc.getInt("radius"));
                    locs.add(loc);
                }

//            for (Location locTest: locs) {
//                Log.d("JSONSTUFF",locTest.getLocationAddress());
//                Log.d("JSONSTUFF",locTest.getLocationName());
//                Log.d("JSONSTUFF",locTest.getLongitude()+"");
//                Log.d("JSONSTUFF",locTest.getLatitude()+"");
//                Log.d("JSONSTUFF","------------------------------");
//            }

                return locs;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<Location>();
    }

    public static byte[] convertToBytes(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object convertFromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
