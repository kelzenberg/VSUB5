package com;

import java.rmi.RemoteException;

public class InvalidMessageException extends RemoteException {

   private String error;

   /**
    * Declares an invalid message (e.g. message too long)
    *
    * @param error
    */
   public InvalidMessageException(String error) {
      this.error = error;
   }

   /**
    * Returns the error message
    *
    * @return String Error message
    */
   @Override
   public String toString() {
      return "InvalidMessageException{ " + error + " }";
   }
}
