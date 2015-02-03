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
package directory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author tbking
 */
public class TorDirectory {
    static final Logger dirlog= Logger.getLogger("directory");
    DataInputStream req;
    private String input;
    private String[] IP,token;
    private String[][] RSA;
    private boolean[] status;
    private int N,count,DirPort=9090;
    private int id;
    static ServerSocket directory;
    
    TorDirectory() {
        
        dirlog.info("Tor Directory initialized."); 
        input="";
        id=3;
        N=50;
        IP=new String[N];
        RSA=new String[N][2];
        status=new boolean[N];
        count=0;
        dirlog.info("Number of routers online : "+count);
    }
    
    
    public static void main(String args[])
    {
        try
        {
            dirlog.info("Tor Directory running.");
            TorDirectory tordir = new TorDirectory(); //create object
            tordir.loginit();
            directory = new ServerSocket(tordir.DirPort,10);
            while(true)
            {
                tordir.connect();                     //connect to node
                
            }
        } 
        catch (IOException ex)
        {
            dirlog.severe("Directory Port busy. Exiting program.");
        }
    }
    
    
    private void loginit()
    {
        FileHandler logFile;
        try
        {
            logFile=new FileHandler("/home/tbking/Development/netbeans-8.0.2/Projects/TOR/src/directory/TorDir.log");
            dirlog.addHandler(logFile);
            SimpleFormatter formatter = new SimpleFormatter();
            logFile.setFormatter(formatter);
        }
        catch (IOException | SecurityException ex) {
            dirlog.severe("Exception raised in creating log file. Exiting program.");
        }
    }
    
    private void connect()
    {
        try
        {
            dirlog.info("Waiting to connect");
            Socket incoming = new Socket();
            try
            {
                incoming = directory.accept();                                  //accept incoming socket request
            }
            catch(Exception ex)
            {
                dirlog.warning("Couldn't connect to the node.");
                return;
            }
            dirlog.info("Node connected.");
            req = new DataInputStream(incoming.getInputStream());
            input = req.readUTF();                                               //read input from node
            //System.out.println(input);
            token =input.split("/");
            switch(token[0])                                                    //check for header
            {
                case "0":                                                       // if header=0 then it is a router
                    dirlog.info("Node identified as Router.");
                    id=0;
                    router(incoming);
                    break;
                case "1":
                    dirlog.info("Node identified as Client.");                   //if header=1 then it is a client
                    id=1;
                    client(incoming);
                    break;
                default:
                    dirlog.warning("Node can't be identified. Closing connection...");
                    id=2;
            }
            incoming.close();
        }
        catch(IOException ex)
        {
            dirlog.severe("Couldn't receive data from Node.");
        }
    }
    
    private void router(Socket incoming)
    {
        dirlog.info("Router operations initiated.");
        IP[count] = ""+incoming.getInetAddress();                                       //get ip address of the router node
        RSA[count][0] = token[1];                                                       // get base of RSA key of the router node
        RSA[count][1] = token[2];                                                       //get exponent of RSA key of the router node
        status[count++] = true;                                                         //mark the router online
        dirlog.info("\n=====>> IP="+IP[count-1]+"\tN = "+token[1]+"\tE = "+token[2]+"\n");
        dirlog.info("Number of routers online : "+count);
    }
    private void client(Socket incoming)
    {
        dirlog.info("Client operations initiated.");
        Random rand = new Random();
        int[] router = new int[3];
        while(!status[router[0] = rand.nextInt(count)])
        {   //generate random routers untill status of the generated router is online
        } 
        while((router[1]=rand.nextInt(count))==router[0] && !status[router[1]])
        {
        }
        while(((router[2]=rand.nextInt(count))==router[1] ) && ((router[2]=rand.nextInt(count))==router[1]) && !status[router[2]])
        {
        }
        String metadata = "";
        for(int node :router)
        {
            metadata += IP[node] + "/" + RSA[node][1] + "/" + RSA[node][0];
        }
        try
        {
            DataOutputStream response = new DataOutputStream(incoming.getOutputStream());
            response.writeUTF(metadata);
            dirlog.info("Data sent to Client " + incoming.getInetAddress());
        }
        catch (IOException ex)
        {
            dirlog.warning("Data couldn't be sent to Client: "+ incoming.getInetAddress());
        }
    }
}
