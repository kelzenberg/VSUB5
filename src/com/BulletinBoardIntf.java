package com;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardIntf extends Remote {

   /**
    * Gets the number of Messages on the BulletinBoard
    *
    * @return int number of pinned messages
    * @throws RemoteException
    */
   int getMessageCount() throws RemoteException;

   /**
    * Gets all Messages on the BulletinBoard as an Array
    *
    * @return String-Array with one Message object per slot
    * @throws RemoteException
    */
   String[] getMessages() throws RemoteException;

   /**
    * Gets a specific Message at the provided index of the Array
    *
    * @param index
    * @return String of the Message object at index
    * @throws RemoteException
    */
   String getMessage(int index) throws RemoteException;

   /**
    * Creates a new Message object with input message
    * and puts it onto the BulletinBoard
    *
    * @param msg
    * @throws RemoteException
    */
   void putMessage(String msg) throws RemoteException;
}
