/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hotel;

import java.util.*;
/**
 *
 * @author Wayne O'Mahony
 */
public class HotelTest 
{    
    public static void main (String [] args) throws InterruptedException 
    {
        int[] room_Numbers = {10,20,30,40,50};
        int[] days1 = {1,2};
        int[] days2 = {3,4};
        int[] days3 = {5,6};
        int[] days4 = {7,8};
        
        Hotel Hayes = new Hotel(room_Numbers);
        
        Thread t0 = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                Hayes.bookRoom("A", days1, 10);
            }
        });
        
        Thread t1 = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                Hayes.bookRoom("B", days2, 20);
            }
        });
        
        Thread t2 = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                Hayes.bookRoom("C", days3, 30);
            }
        });
        
        Thread t3 = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                Hayes.bookRoom("D", days4, 40);
            }
        });
        
        Thread t4 = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {        
                //update and cancel non-existing bookings to check exceptions
                try 
                {
                    Hayes.updateBooking("C", days3, 10);
                } 
                catch(NoSuchBookingException e) 
                {
                    System.out.println(e.getMessage());
                }
        
                try 
                {
                    Hayes.cancelBooking("D");
                } 
                catch(NoSuchBookingException e) 
                {
                    System.out.println(e.getMessage());
                }
                
                try 
                {
                    Hayes.updateBooking("A", days3, 10);
                } 
                catch(NoSuchBookingException e) 
                {
                    System.out.println(e.getMessage());
                }

                try 
                {
                    Hayes.cancelBooking("B");
                } 
                catch(NoSuchBookingException e) 
                {
                    System.out.println(e.getMessage());
                }
            }
        });
        
        Thread t5 = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                Hayes.bookRoom("A", days2, 50);
                Hayes.bookRoom("B", days4, 50);
                Hayes.bookRoom("C", days1, 20);
                Hayes.bookRoom("D", days1, 10);
            }
        });
        
        t0.start(); //A booking
        t1.start(); //B booking
        t2.start(); //C booking
        t3.start(); //D booking
        t4.start(); //updating and cancelling the 4 bookings
        t5.start(); //this thread to mess with everything! 
        
    }//close main method
}//close HotelTest class