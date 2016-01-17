package com.twitterclone;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Sgsudhir on 1/16/2016.
 */
public class ConvertInputStreamToString {
    InputStream inputStream;
    String line = "";
    String result = "";
    public ConvertInputStreamToString(InputStream inputStream){
        this.inputStream=inputStream;
    }
    public String getString(){
        try {
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            while((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
