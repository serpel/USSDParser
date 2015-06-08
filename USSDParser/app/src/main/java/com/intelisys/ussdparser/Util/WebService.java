package com.intelisys.ussdparser.Util;

/**
 * Created by serpe_000 on 22/05/2015.
 */

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebService {

    public static String TAG = "WebServiceUSSD";
    public static String NAMESPACE = "http://tempuri.org/";
    public static String URL = "http://200.59.27.28:8095/Servicios.asmx";
    public static int MSG_TIMEOUT = 15000;

    public static String getNextNumber(String webMethName) {

        String resTxt = null;

        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, MSG_TIMEOUT);

        try {
            // Invoke web com.intelisys.ussdparser.service SOAP_ACTION: http://tempuri.org/ObtenerSiguiente
            androidHttpTransport.call(NAMESPACE+webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            resTxt = response.toString();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            //Toast.makeText()
        }
        return resTxt;
    }

    public static String setUssdResponse(String webMethName, String number, String ussdTextResponse) {

        String resTxt=null;

        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo ussdNumber = new PropertyInfo();
        ussdNumber.setName("numero");
        ussdNumber.setValue(number);
        ussdNumber.setType(String.class);
        request.addProperty(ussdNumber);

        PropertyInfo ussdResponse = new PropertyInfo();
        ussdResponse.setName("respuesta");
        ussdResponse.setValue(ussdTextResponse);
        ussdResponse.setType(String.class);
        request.addProperty(ussdResponse);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, MSG_TIMEOUT);

        try {
            // Invoke web com.intelisys.ussdparser.service SOAP_ACTION: http://tempuri.org/ObtenerSiguiente
            androidHttpTransport.call(NAMESPACE+webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            resTxt = response.toString();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        //Return resTxt to calling object
        return resTxt;
    }

    private static final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        return envelope;
    }
}
