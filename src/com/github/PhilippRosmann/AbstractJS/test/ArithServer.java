/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.PhilippRosmann.AbstractJS.test;

import com.github.PhilippRosmann.AbstractJS.AbstractJS;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public class ArithServer extends AbstractJS
{

    @Override
    protected byte[] handleRequestGetResponse(byte[] request)
    {
        String revceived = new String(request);
        String res = "ERROR";

        try
        {
            final String op;
            final double[] numbers;

            {
                final String[] token = revceived.split(" ");

                op = token[0].toUpperCase();

                numbers = new double[token.length-1];
                if(numbers.length<1)
                    throw new Exception("Min one numbers");

                for(int i=0; i< numbers.length; i++)
                {
                    Scanner sc = new Scanner(token[i+1]);
                    sc.useLocale(Locale.US);
                    try{numbers[i] = sc.nextDouble();}catch(InputMismatchException ex)
                    {
                        throw new Exception("Wrong number format");
                    }
                }


            }

            {
                double erg = 0;
                switch(op)
                {
                    case "ADD":
                        for(int i=0;i<numbers.length;i++)
                            erg += numbers[i];
                        break;

                    case "SUB":
                        erg = numbers[0];
                        for(int i=1;i<numbers.length;i++)
                            erg -= numbers[i];
                        break;

                    case "MUL":
                        erg = numbers[0];
                        for(int i=1;i<numbers.length;i++)
                            erg *= numbers[i];
                        break;

                    case "DIV":
                        erg = numbers[0];
                        for(int i=1;i<numbers.length;i++)
                            erg /= numbers[i];
                        break;
                    case "SQRT":
                        if(numbers.length>1)
                            throw new Exception("too many arguments for operation");
                        erg = Math.sqrt(numbers[0]);
                        break;

                    default:
                        throw new Exception("Unknown Command -"+op);
                }

                res = String.format(Locale.US,"RES: %f", erg);
            }
        }
        catch(Exception ex)
        {
            res = "Error: "+ex.getMessage();
        }

        return res.getBytes();
    }
    
    
    public static void main(String[] args)
    {
        AbstractJS.setLogLevel(Level.WARNING);
        try
        {
            new ArithServer().start(55555);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ArithServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
