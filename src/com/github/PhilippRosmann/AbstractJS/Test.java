/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.PhilippRosmann.AbstractJS;

import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author Philipp
 */
public class Test
{
    public static void main(String[] args)
    {
        AbstractJS.setLogLevel(Level.INFO);
        
        AbstractJS abstractJS = new AbstractJS()
        {
        };
        
        try
        {
            abstractJS.start(55555);
            
            Thread.sleep(5000);
            
            abstractJS.stop();
        }
        catch (IOException | InterruptedException e)
        {
            System.out.println("Fehler: "+e.getMessage());
        }
    }
}
