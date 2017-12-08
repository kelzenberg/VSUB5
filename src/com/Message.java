package com;

import java.time.Instant;

public class Message {

   private final String message;
   private final Instant created;

   /**
    * Constructor to create a new Message object with a message
    *
    * @param msg
    */
   public Message(String message) {
      this.message = message;
      this.created = Instant.now();
   }

   /**
    * Get this objects message
    *
    * @return String message
    */
   public String getMessage() {
      return message;
   }

   /**
    * Gets this objects timestamp on which it was created
    *
    * @return Instant time created
    */
   public Instant getCreated() {
      return created;
   }

   /**
    * Gets all available information about this Message object
    *
    * @return String Message information
    */
   @Override
   public String toString() {
      return "Message{" +
              "message='" + message + '\'' +
              ", created=" + created +
              '}';
   }
}
