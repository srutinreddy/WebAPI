package com.example.srutinreddy.webapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button mBtnLoad;
    private TextView mTvData;
    private String TAG="Network activity";
    private Context mCtx;
    private MyHandler Handler;
    private ProgressBar mPbar;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnLoad =(Button)findViewById(R.id.id_btn);
        mTvData=(TextView)findViewById(R.id.id_textView);
        mBtnLoad.setOnClickListener(this);
        Handler=new MyHandler();
        mPbar=(ProgressBar)findViewById(R.id.id_progress);
    }
    public void onClick(View v)
    {
        if (v==mBtnLoad)
        {
            getData();
        }
    }

    private void getData()
    {
        mCtx=getApplicationContext();
        boolean isConnected= isNetworkAvailable(mCtx);

        if(isConnected)
        {
            loadInWorkerThread();
        }
        else
        {
            Toast.makeText(mCtx,"No Network",Toast.LENGTH_LONG).show();
        }
        //String URL="http://androidexample.com/media/webservice/JsonReturn.php";
    }


        public class MyHandler extends android.os.Handler
        {
            public void handleMessage(Message msg)
            {
                if(msg.what==1)
                {
                    mTvData.setText(response);
                    mPbar.setVisibility(ProgressBar.GONE);
                }
            }
        }
    private void loadInWorkerThread()
    {
        mPbar.setVisibility(ProgressBar.VISIBLE);
        Runnable r= new Runnable()
        {
            @Override
            public void run()
            {
                fetchData();
               Handler.sendEmptyMessage(1);
              // mTvData.setText(response);
            }
        };
        Thread th=new Thread(r);
        th.start();
    }

    String response="";
    private void fetchData()
    {
        try
        {
            String path="http://demo.codeofaninja.com/tutorials/json-example-with-php/index.php";
            URL url=new URL(path);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            int respCode=con.getResponseCode();
            if (respCode!=-1)
            {
                InputStream isr=con.getInputStream();
                response= convertStreamToString(isr);
                Log.w(TAG,response);
            }
            else
            {
                Toast.makeText(mCtx, "some error "+respCode,Toast.LENGTH_LONG).show();
                response="Some Error";
            }
        }
        catch (MalformedURLException e)
        {
            Log.w(TAG," error: "+e.toString());
            response=e.toString();
        }
        catch (IOException e)
        {
            Log.w(TAG, " error: " + e.toString());
            response=e.toString();
        }
    }

    public static boolean isNetworkAvailable(Context ctx)
    {
        ConnectivityManager cm =(ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf= cm.getActiveNetworkInfo();
        if(nf!=null && nf.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String convertStreamToString(InputStream stream) throws IOException,UnsupportedEncodingException
    {
        InputStreamReader isr=new InputStreamReader(stream);
        BufferedReader buf = new BufferedReader(isr);
        StringBuffer response= new StringBuffer();
        String data="";
        while((data=buf.readLine())!=null)
        {
            response.append(data);
        }
        return  response.toString();
    }
}