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
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
    private boolean purpose;
    private final String[] IP;
    private final int[][] RSA;
    private final boolean[] status;
    private final int N,count;
    
    TorDirectory() {
        
        dirlog.info("Tor Directory object created."); 
        input="";
        purpose=false;
        N=50;
        IP=new String[N];
        RSA=new int[N][2];
        status=new boolean[N];
        count=0;
    }
    
    
    public static void main(String args[]) throws IOException
    {
        dirlog.info("Tor Directory running.");
        TorDirectory tordir = new TorDirectory(); //create object
        tordir.loginit();                        //initialize log file
        tordir.connect();                        //connect to node
        if(tordir.purpose == true)
        {
            tordir.client();
        }
        else
        {
            tordir.router();
        }
    }
    
    
    private void loginit()
    {
        FileHandler log_file;
        try
        {
            log_file=new FileHandler("/home/tbking/Development/netbeans-8.0.2/Projects/TOR/src/directory/TorDir.log");
            dirlog.addHandler(log_file);
            SimpleFormatter formatter = new SimpleFormatter();
            log_file.setFormatter(formatter);
        }
        catch (IOException | SecurityException ex) {
            dirlog.severe("Exception raised in creating log file. Exiting program.");
        }
    }
    
    private void connect() throws IOException
    {
        dirlog.info("Waiting to connect");
        ServerSocket directory = new ServerSocket(9090);
        Socket incoming = directory.accept();
        dirlog.info("Node connected.");
        req = new DataInputStream(incoming.getInputStream());
        input = req.readLine();
        switch(input)
        {
            case "0":
                dirlog.info("Node identified as Router.");
                purpose=false;
                break;
            case "1":
                dirlog.info("Node identified as Client.");
                purpose=true;
                break;
                
        }
    }
    private void router() throws IOException
    {
        dirlog.info("Router operations initiated.");
        input = req.readLine();
        dirlog.info("Data Received"+input);
    }
    private void client()
    {
        dirlog.info("Client operations initiated.");
        dirlog.info("Data Received"+input);
    }
}
