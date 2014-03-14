/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.PhilippRosmann.AbstractJS;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public abstract class AbstractJS 
{
    private static final Logger LOG = Logger.getLogger(AbstractJS.class.getName());
    private ClientHubThread clientHubThread = null;
    
    private ExecutorService requestThreadPool = Executors.newFixedThreadPool(4);

    public static synchronized void setLogLevel(Level newLevel) throws SecurityException
    {
        LOG.setLevel(newLevel);
        RequestHandler.setLogLevel(newLevel);
    }
    
    public void start(int port) throws IOException
    {  
        if(clientHubThread!=null)
            throw new IOException("Illegal Server State");
        
        clientHubThread = new ClientHubThread(port);
        clientHubThread.start();
    }
    
    public void stop() throws InterruptedException, IOException
    {
        if(clientHubThread==null)
            throw new IOException("Illegal Server State");
        clientHubThread.interrupt();
        clientHubThread.join(1000);
        if(clientHubThread.isAlive())
        {
            LOG.severe("ClientHubThread didn´t terminate");
            clientHubThread.stop();
        }
        clientHubThread = null;
    }
    
    private class ClientHubThread extends Thread
    {
        private ServerSocket serverSocket = null;

        public ClientHubThread(int port) throws IOException
        {
            super(ClientHubThread.class.getName());
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
        }
        
        @Override
        public void run()
        {
            LOG.log(Level.INFO, "Server startet on Port {0}",
                String.format("%d", serverSocket.getLocalPort()));
            
            try
            {
                while(!isInterrupted())
                {
                    try
                    {
                        Socket client = serverSocket.accept();
                        requestThreadPool.execute(new RequestHandler(client, AbstractJS.this));
                    }
                    catch (SocketTimeoutException ignored){ }
                }
                
                LOG.info("Server exited cleanly");
            }
            catch (IOException ex)
            {
                LOG.log(Level.SEVERE, "Server crashed !", ex);
            }
        }

    }
    
    protected abstract byte[] handleRequestGetResponse(byte[] request);
    
}
