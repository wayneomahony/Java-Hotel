package Hotel;


import java.util.*;

/**
 *
 * @author Wayne O'Mahony HERE IS MY CHANGE
 */
public class Hotel 
{
    //consider making global variables volatile to stop threads from caching their values

    //Key and value pair to hold bookingRef and roomNum
    HashMap<String, Integer> br_rn = new HashMap<String, Integer>();
    //Key and value pair to hold bookingRef and days
    HashMap<String, int[]> br_d = new HashMap<String, int[]>();
    //Key and value pair to hold roomNum and days
    HashMap<Integer, int[]> rn_d = new HashMap<Integer, int[]>();
    
    //create a lock object and decide what to lock down
    Object lock = new Object();
    
    //constructs a hotel with room numbers as specified in roomNums. The rooms are initially unbooked
    public Hotel(int[] roomNums)
    {
        /*constructs a hotel with room numbers as specified 
        in roomNums.  The rooms are initially unbooked*/        

        synchronized(lock)
        {
            //now need to put these into HashMap with roomNum and days
            for(int i = 0; i<roomNums.length; i++)
            {
                //populated with roomNums and null because there are no days provided yet
                rn_d.put(roomNums[i], null);
            }
        }    
    }
    
    boolean roomBooked(int[] days, int roomNum)
    {
        synchronized(lock)
        {
            boolean booked = false;
            /*now need to check if there is a key value pair 
            in the rn_d hashmap which matches the data*/

            //if the HashMap is empty, room has to be available
            //this can only happen if there is no constructor called
            if(rn_d.isEmpty())
            {
                booked = false;
                return booked;
            }

            if(rn_d.get(roomNum) == null)
            {
                booked = false;
                return booked;
            }

            //ArrayList because I don't know how many days are linked to a roomNum. Could be several bookings
            ArrayList<Integer> roomDays = new ArrayList<Integer>();

            /*Now to check if HashMap contains roomNum.
            If it does then next things is to check whether or not
            roomNum is booked on corresponding days*/

            //the if statement checks that the key is in the HashMap
            if(rn_d.containsKey(roomNum) == true)
            {
                //the loop runs for the amount of days
                for(int i = 0; i < rn_d.get(roomNum).length; i++) //gets value associated with room num key
                {
                    //adds all the roomNum indexes to the array list
                    roomDays.add(rn_d.get(roomNum)[i]);//adds the days attempting to book to arraylist
                }

                //loop to run for the number of days 
                for(int n = 0; n < days.length; n++)
                {
                    //if statement checks if arraylist contains a day passed to the method
                    if(roomDays.contains(days[n]))  //boolean
                    {
                        //set booked to true if arraylist contains a day passed to the method
                        booked = true;
                    }
                }
            }
        
            //boolean reflects if any of the days are booked. May only have one day booked out of N days
            //possibly put in a boolean array to reflect each day's status
            return booked;
        }
    }
    
    boolean bookRoom(String bookingRef, int[] days, int roomNum)
    {
        synchronized(lock)
        {
            if(!rn_d.containsKey(roomNum))
            {
                System.out.println(Thread.currentThread().getName() + " :There is no room " + roomNum + " in the hotel. Booking not possible");
                return false;
            }
            
            if(roomBooked(days, roomNum) == true)
            {
                return false;
            }

            else
            {
                //need to lock down these!!
                //populate the HashMaps with data provided
                br_rn.put(bookingRef, roomNum);
                rn_d.put(roomNum, days);
                br_d.put(bookingRef, days);
                
                printHashmap(bookingRef);
                System.out.println("***\n***");
                
                return true;
            }
        }
    }
    
    boolean updateBooking(String bookingRef, int[] days, int roomNum) throws NoSuchBookingException
    {
        //variable for status of booking update
        Boolean updated = false;
        
        synchronized(lock)
        {
            if(br_rn.containsKey(bookingRef) == false)
            {
                throw new NoSuchBookingException(bookingRef);
            }

            //a check if the booking update clashes with any other existing bookings
            if(roomBooked(days, roomNum) == true)
            {
                //if there is another booking that clashes, then not possible to update
                System.out.println("***");
                System.out.println(Thread.currentThread().getName() + " :Clashes with existing booking. Update not possible");
                System.out.println(Thread.currentThread().getName() + " :" + roomNum + " already booked on days " + Arrays.toString(days));
                System.out.println("***");
                updated = false;
            }
        
            else
            {
                //check if br_rn hashmap contains a mathcing booking reference
                if(br_rn.get(bookingRef) != null)
                {
                    //read data into temporary variables
                    String new_BookingRef = bookingRef;
                    int[] new_Days = days;
                    int new_Room = roomNum;

                    //cancel the booking
                    cancelBooking(bookingRef);

                    /*book the room with the new data and assign the
                    status of bookRoom method to the updated boolean*/

                    System.out.println(Thread.currentThread().getName() + " :Booking Reference " + bookingRef + " successfully updated \n***");
                    updated = bookRoom(new_BookingRef, new_Days, new_Room);
                }
            }
        }
        
        return updated;
    }
    
    void cancelBooking(String bookingRef) throws NoSuchBookingException
    {
        synchronized(lock)
        {
        
            if(br_rn.containsKey(bookingRef) == false)
            {
                throw new NoSuchBookingException(bookingRef);
            }
        
            if(br_rn.get(bookingRef) != null)//if related reference returns a value (other than null)
            {
                //3 temporary variables
                int roomNum = br_rn.get(bookingRef);//roomNum of bookingRef
                int[] days = br_d.get(bookingRef); //days of bookingRef
                int[] days2 = rn_d.get(roomNum);//all days associated with the room number. may include other bookings

                ArrayList<Integer> myArr = new ArrayList<Integer>();

                int j = 0;

                //this for loop captures all the days NOT associated with the booking ref into an arraylist
                for(int i = 0; i < days.length; i++)
                {
                    if(days[i] == days2[j])
                    {

                    }

                    else
                    {
                        myArr.add(days2[j]);
                    }

                    j++;
                }

                //don't know how many days Eugene will be testing with. 100 should suffice
                int[] newDays = new int[100];
                j = 0;

                //the arraylist then gets put back into the hashmap to not affect the rn_d hashmap
                for(int k = 0; k < myArr.size(); k++)
                {
                    int p = myArr.get(k);
                    newDays[j] = p;
                }

                System.out.println(Thread.currentThread().getName() + " :Booking Reference " + bookingRef + " successfully cancelled");
                
                //possibly just this stuff to lock down
                br_d.remove(bookingRef);
                br_rn.remove(bookingRef);
                rn_d.put(roomNum, newDays);//put replaces the old values
                //System.out.println("Booking Reference " + bookingRef + " successfully cancelled");
            }
        }
    }
    
    boolean roomsBooked(int[] days, int[] roomNums)
    {
        /*returns true if any of the rooms in roomNums is booked 
        on any of the days specified in days, otherwise returns false.
        All the work is done in the roomBooked method.
        The loop below just checks a room at a time*/ 
	for (int i = 0; i < roomNums.length; i++) 
        {
	    if (roomBooked(days, roomNums[i])) 
            {
		return true;
	    }
	}
        
	return false;
    
    }//close method
    
    boolean bookRooms(String bookingRef, int[] days, int[] roomNums)
    {
        /*create a booking with reference bookingRef for the rooms 
        in roomNums for each of the days specified in days. Returns 
        true if it is possible to book the rooms on the given days, 
        otherwise returns false*/
        
        if (roomsBooked(days, roomNums)) 
        {
	    return false;
	}
        else
        {
            for (int i = 0; i < roomNums.length; i++) 
            {
		//No more time to do this! Changing my Hashmap is proving difficult! 
	    }
        }
        return true;
    }
    
    boolean updateBooking(String bookingRef, int[] days, int[] roomNums) throws NoSuchBookingException
    {
        /*updates the booking with reference bookingRef so that it 
        now refers to the specified roomNums for each of the days 
        specified in days. Returns true if it is possible to update 
        the booking  (i.e., the new booking does not clash with an 
        existing booking), otherwise returns false and leaves the 
        original booking unchanged. If there is no booking with the 
        specified reference throws NoSuchBookingException*/
        
        return false;
    }
    
    void printHashmap(String bookingRef) throws NullPointerException
    {
        synchronized(lock)
        {
            //to print out: need to write hashmap values to array then print the array
            //need to print contents after bookRoom, update, and cancel
            //br_d, br_rn, rn_d
            String one = " :Days booked for BookingRef " + bookingRef + ": ";
            String two = " :Room numbers booked for BookingRef " + bookingRef + ": ";
            String three = " :Days booked with room number ";

            int[] brDays = br_d.get(bookingRef);
            Integer brRoomNums = br_rn.get(bookingRef);
            int[] rnDays = rn_d.get(brRoomNums);
        
            System.out.println(Thread.currentThread().getName() + one + Arrays.toString(brDays));
            System.out.println(Thread.currentThread().getName() + two + brRoomNums.toString());
            System.out.println(Thread.currentThread().getName() + three + brRoomNums.toString() + ": " + Arrays.toString(rnDays));
        }
    }    
}
