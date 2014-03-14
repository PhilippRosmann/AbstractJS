/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.PhilippRosmann.AbstractJS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public class RequestHandler implements Runnable
{
    private static final Logger LOG = Logger.getLogger(RequestHandler.class.getName());
    private final Socket socket;
    private final AbstractJS parent;

    public RequestHandler(Socket socket, AbstractJS parent) throws SocketException
    {
        this.socket = socket;
        this.parent = parent;
        this.socket.setSoTimeout(1000);
    }
    
   
    
    public static synchronized void setLogLevel(Level newLevel) throws SecurityException
    {
        LOG.setLevel(newLevel);
    }
    
    @Override
    public void run()
    {
        LOG.log(Level.INFO, "Incoming Request from {0} ...", 
            socket.getInetAddress().getHostName());
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(1000);
            
            {
                InputStream is = socket.getInputStream();
                byte data;

                do
                {
                    data = (byte)is.read();
                    if(data!=-1)
                    {
                        buffer.put(data);
                    }
                }
                while(data!=-1);
            }

            LOG.log(Level.INFO, "Received {0} Bytes from {1}!", 
                new Object[]{buffer.position() , socket.getInetAddress().getHostName()});
            
            
            
            {
                OutputStream os = socket.getOutputStream();
                byte[] request = new byte[buffer.position()];
                System.arraycopy(buffer.array(), 0, request, 0, buffer.position());

                byte[] response = parent.handleRequestGetResponse(request);
      
                os.write(response);
                os.flush();
                socket.shutdownOutput();
            }
            
            

            LOG.log(Level.INFO,"The request from {0} was successfully handled!",socket.getInetAddress().getHostName());
        }
        catch(IOException ex)
        {
            LOG.log(Level.INFO, "Couldn´t handle request", ex);
        }
        finally
        {
            try
            {
                socket.close();
            } catch (IOException ignored){}
        }
    }    
}
