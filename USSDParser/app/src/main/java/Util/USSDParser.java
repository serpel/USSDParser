package Util;

/**
 * Created by serpe_000 on 21/05/2015.
 */

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class USSDParser {

    private static String startmsg = "displayMMIComplete";
    private static String endmsg = "MMI code has finished running";
    private static String trimmsg = "- using text from MMI message: '";
    private String Command = "logcat -v time -b main PhoneUtils:D";

    private long before = 3000; // delay (ms) before creation of the class
    private long after = 3000; // delay (ms) after creation of the class

    private String msg = ""; // the USSD message
    private boolean found = false;
    private long timeLog = -1; // timestamp of the found log

    public USSDParser() {
        this(3000, 3000);
    }

    public USSDParser(long before_creation, long after_creation) {
        before = before_creation;
        after = after_creation;
    }

    public String getMsg() {
        long timestamp = System.currentTimeMillis();
        Log.d("USSDClass",
                "Class creation - timestamp: " + String.valueOf(timestamp));
        try {
            Process logcatProc = Runtime.getRuntime().exec(
                    "logcat -v time -b main PhoneUtils:D");
            BufferedReader mReader = new BufferedReader(new InputStreamReader(
                    logcatProc.getInputStream()), 1024 * 2);

            String line = "";
            boolean tostop = false;
            long stop = timestamp + after; // to stop the while after "after" ms
            while (((line = mReader.readLine()) != null)
                    && (System.currentTimeMillis() < stop) && (tostop == false)) {
                if (line.length() > 19) // the line should be at least with a length of 19 !
                {
                    if (line.contains(startmsg)) // check if it is a USSD msg
                    {
                        timeLog = extracttimestamp(line);
                        Log.d("USSDClass", "Found line at timestamp : "
                                + String.valueOf(timeLog));
                        if (timeLog >= timestamp - before)
                            found = true; // start of an USDD is found & is recent !
                    } else if (found) {
                        // log example :
                        // "12-10 20:36:39.321 D/PhoneUtils(  178): displayMMIComplete: state=COMPLETE"
                        if (line.contains(endmsg))
                            tostop = true;
                        else {
                            // log example :
                            // "12-10 20:36:39.321 D/PhoneUtils(  178): - using text from MMI message: 'Your USSD message with one or several lines"
                            Log.d("USSDClass", "Line content : " + line);
                            String[] v = line.split("\\): "); // doesn't need
                            // log
                            // information
                            // --> split
                            // with "): "
                            // separator
                            if (v.length > 1)
                                msg = v[1].replace(trimmsg, "").trim() + "\n";

                            tostop = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.d("USSDClass", "Exception:" + e.toString());
        }

        return msg;
    }

    public String getMsg2() {

        long timestamp = System.currentTimeMillis();
        Log.d("USSDClass",
                "Class creation - timestamp: " + String.valueOf(timestamp));
        try {
            Process logcatProc = Runtime.getRuntime().exec(
                    "logcat -v time -b main PhoneUtils:D");
            BufferedReader mReader = new BufferedReader(new InputStreamReader(
                    logcatProc.getInputStream()), 1024 * 2);

            String line = "";
            boolean tostop = false;
            long stopTime = timestamp - before; // to stop the while after "after" ms
            while (((line = mReader.readLine()) != null)
                   && (tostop == false)) {
                if (line.length() > 19)
                {
                    timeLog = extracttimestamp(line);
                    if ((timeLog > 0) && (stopTime >= timeLog)){
                        tostop = true;
                    }
                    if (line.contains(startmsg)) // check if it is a USSD msg
                    {
                        timeLog = extracttimestamp(line);
                        Log.d("USSDClass", "Found line at timestamp : "
                                + String.valueOf(timeLog));
                        if (timeLog >= timestamp - before)
                            found = true; // start of an USDD is found & is recent !
                    } else if (found) {
                        // log example :
                        // "12-10 20:36:39.321 D/PhoneUtils(  178): displayMMIComplete: state=COMPLETE"
                        if (line.contains(endmsg))
                            tostop = true;
                        else {
                            // log example :
                            // "12-10 20:36:39.321 D/PhoneUtils(  178): - using text from MMI message: 'Your USSD message with one or several lines"
                            Log.d("USSDClass", "Line content : " + line);
                            String[] v = line.split("\\): ");
                            if (v.length > 1)
                                msg += v[1].replace(trimmsg, "").trim() + "\n";
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.d("USSDClass", "Exception:" + e.toString());
        }

        return msg;
    }


    // extract timestamp from a log line with format
    // "MM-dd HH:mm:ss.ms Level/App:msg" Example : 12-10 20:36:39.321
    // Note : known bug : happy new year check will not work !!!
    private long extracttimestamp(String line) {
        long timestamp = -1; // default value if no timestamp is found
        String[] v = line.split(" ");
        if (v.length > 1) // check if there is space
        {
            Calendar C = Calendar.getInstance();
            int y = C.get(Calendar.YEAR);
            String txt = v[0] + "-" + y + " " + v[1]; // transform in format
            // "MM-dd-yyyy HH:mm:ss"
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "MM-dd-yyyy HH:mm:ss");
            try {
                Date tmp = formatter.parse(txt);
                timestamp = tmp.getTime();

            } catch (ParseException e) {
                Log.d("USSDClass",
                        "USDD.extractimestamp exception:" + e.toString());
            }
        }
        return timestamp;
    }

    public static Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }

}
