package com.client;

import com.BulletinBoardIntf;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class Client {

    public static void main(String args[]) {
        try {
            String name = "BulletinBoard";
            Registry registry = LocateRegistry.getRegistry();
            //Compute comp = (Compute) registry.lookup(name);
            BulletinBoardIntf bb = (BulletinBoardIntf) registry.lookup(name);
            //Pi task = new Pi(Integer.parseInt(args[1]));
            //BigDecimal pi = comp.executeTask(task);
            int test = bb.getMessageCount();
            System.out.println("All Messages: " + test);
            try {
                bb.putMessage("Eine tolle Nachricht!");
            } catch(RemoteException error) {
                System.out.println(error);
            }
            test = bb.getMessageCount();
            System.out.println("All Messages: " + test);
            printAllMessages(bb);
        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }
    
    private static void printAllMessages(BulletinBoardIntf bb) {
        String[] messages;
        try {
            messages = bb.getMessages();
        } catch(RemoteException error ) {
            return;
        }
        System.out.println("Messages begin ===");
        for(String message : messages) {
            System.out.println(message.toString());
        }
        System.out.println("Messages end   ===");
    }
}
