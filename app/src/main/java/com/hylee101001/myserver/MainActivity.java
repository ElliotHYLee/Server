package com.hylee101001.myserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView txtLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLog = (TextView) findViewById(R.id.txtLog);

        txtLog.setText(getIPAddress(true)); // IPv4

        Button btnStart = (Button) findViewById(R.id.button);
        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ServerThread thread = new ServerThread(txtLog);
                thread.start();
                //Log.d("MyServer", getIPAddress(true));
            }
            });
        }

        class ServerThread extends Thread{
            TextView tv;
            public ServerThread(TextView tv){
                this.tv = tv;
            }

            public void run(){
                int port = 8769;
                try{

                    ServerSocket server = new ServerSocket(port);
                    Log.d("MyServer", "Server Started.");
                    InetAddress iAddress = InetAddress.getLocalHost();
                    String server_IP = getIPAddress(true);//iAddress.getHostAddress();
                    //tv.setText("Server Started.\nIP: " + server_IP + "\nPort: " + String.valueOf(port));
                    int i=0;
                    Log.d("MyServer", "Server ready.");
                    while(i<1){
                        //tv.setText("listening");
                        Log.d("MyServer", "Server listening.");
                        Socket socket = server.accept();
                        //tv.setText("connected");
                        ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                        Object input = instream.readObject();
                        Log.d("MyServer", "input: " + input);
                        //tv.setText((String) input);


                        ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                        outstream.writeObject(input + " from server.");
                        outstream.flush();
                        Log.d("ServerThread", "output sent");
                        //tv.setText("output sent");
                        socket.close();
                        i++;
                    }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
