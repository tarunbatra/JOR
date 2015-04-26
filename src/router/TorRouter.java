/*
 * Copyright 2015 tbking.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package router;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author tbking
 */
public class TorRouter {
    
    static final Logger routerlog= Logger.getLogger("router");
    private DataInputStream din;
    private  DataOutputStream dout;
    private BufferedReader br ;
    private Socket router;
    private String[] E,N,D;
    private byte[] data,decryptedData;
    private final String DirIP="192.168.0.111";
    private Random r;
    private BigInteger p,q,e,d,n,phi;
    private final int RouterPort=9091,DirPort=9090;
    
    TorRouter()
    {
        routerlog.info("Tor Router Initialized.");
        //routerlog.setUseParentHandlers(false);
        E=new String[3];
        N=new String[3];
        D=new String[3];
    }
    public static void main(String args[])
    {
        routerlog.info("Tor Router running");
        TorRouter OR=new TorRouter();
        OR.loginit();
        OR.genKey();
        OR.sendToDir();
        while(true)
        {
            OR.data=OR.getData();
            System.out.println(OR.data.length);
            OR.decryptedData=OR.decrypt(OR.data.length);
            System.out.println(bytesToString(OR.decryptedData));
            OR.sendData();

        } 
        
    }
    
    private void loginit()
    {
        FileHandler logFile;
        try
        {
            logFile=new FileHandler("/home/tbking/Development/netbeans-8.0.2/Projects/TOR/src/router/TorRouter.log");
            routerlog.addHandler(logFile);
            SimpleFormatter formatter = new SimpleFormatter();
            logFile.setFormatter(formatter);
        }
        catch (IOException | SecurityException ex) {
            routerlog.severe("Exception raised in creating log file. Exiting program.");
        }
    }
    
    private void genKey()
    {
        routerlog.info("RSA keys being generated...");
        key(256,0);
        key(512,1);
        key(1024,2);
        routerlog.info("RSA keys generated.");
    }
    
    private void key(int bitlength,int index)
    {
        r=new Random();
        p=BigInteger.probablePrime(bitlength, r);
        q=BigInteger.probablePrime(bitlength, r);
        n=p.multiply(q);
        phi=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e=BigInteger.probablePrime(bitlength/2, r);

        while(phi.gcd(e).compareTo(BigInteger.ONE)>0 && e.compareTo(phi)<0)
        {
        e.add(BigInteger.ONE);
        }

        d=e.modInverse(phi);
        E[index]=e.toString();
        N[index]=n.toString();
        D[index]=d.toString();
    }
    
    private void sendToDir()
    {
        try 
        {
            router=new Socket(DirIP,DirPort);
            dout=new DataOutputStream(router.getOutputStream());
            dout.writeUTF("0/"+E[0]+"/"+N[0]+"/"+E[1]+"/"+N[1]+"/"+E[2]+"/"+N[2]);
            
            for(int i=0;i<3;i++)
            {
                System.out.println("E["+i+"]="+E[i]);
                System.out.println("N["+i+"]="+N[i]);
            }
            dout.flush();
            dout.close();
            router.close();
        } 
        catch (IOException ex) 
        {
            routerlog.severe("Unable to connect to Directory. Exiting program.");
            System.exit(0);
        }
    }
    
    private byte[] getData()
    {
        routerlog.info("Waiting to receive data.");
        try {
            ServerSocket RouterAsServer=new ServerSocket(RouterPort,10);
            Socket DataSender=RouterAsServer.accept();
            routerlog.info("Connection with client established.");
            din=new DataInputStream(DataSender.getInputStream());
            int len=din.readInt();
            byte[] receivedData = new byte[len];
            din.readFully(receivedData);
            din.close();
            DataSender.close();
            RouterAsServer.close();
            routerlog.info("Data Received.");
            System.out.println("Data received in Bytes"+bytesToString(receivedData));
            return receivedData;
        } 
        catch (IOException ex) 
        {
            routerlog.severe("Data receiving failed.");
        }
        return null;
    }
    private byte[] decrypt(int len)
    {
        int key=0;
        if(len==256|| len==257)
        {
            key=2;
        }
        else if(len==128 || len==129)
        {
            key=1;
        }
        System.out.println("len="+len);
        System.out.println("keys to be used "+key);
        System.out.println("Data to decrypt in bytes: "+bytesToString(data));
        return (new BigInteger(data)).modPow(new BigInteger(D[key]),new BigInteger(N[key])).toByteArray();
    }
    
    private void sendData()
    {
        System.out.println("decypted data in string: "+new String(decryptedData));
        StringTokenizer st=new StringTokenizer(new String(decryptedData),"::");
        String nextIp=st.nextToken();
        String m=st.nextToken();
        byte[] msg=m.trim().getBytes();
        System.out.println("next peel: "+new String(msg));
        int l=msg.length;
        try 
          {
              
//              byte[] ip = new byte[15];
//              System.arraycopy(decryptedData, 0, ip, 0, 15);
//              System.out.println("ip:"+bytesToString(ip));
//              byte[] len = new byte[4];
//              System.arraycopy(decryptedData, 15, len, 0, 4);
//              System.out.println("length:"+bytesToString(len));
//              
//              int l = Integer.parseInt(new String(len).trim());
//              System.out.println("so the length is : "+l);
//              byte[] msg = new byte[l];
//              System.arraycopy(decryptedData, 19, msg, 0, l);
//              String nextIp=new String(ip).trim();
              System.out.println("so you want me to send to : "+nextIp);
              System.out.println("the data i am sending is : "+bytesToString(msg));
            Socket RouterAsClient=new Socket(nextIp,RouterPort);
            dout=new DataOutputStream(RouterAsClient.getOutputStream());
            dout.writeInt(l);
            dout.write(msg);
            dout.flush();
            RouterAsClient.close();
        } 
        catch (IOException ex) 
        {
            routerlog.severe("Attempt to establish connection with other router failed. Exiting progream.");
        }
    }
    private static String bytesToString(byte[] e) {
        String test = "";
        for (byte b : e) {
            test += Byte.toString(b);
        }
        return test;
    }
    
}
