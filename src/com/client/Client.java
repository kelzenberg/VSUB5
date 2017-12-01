package com.client;

import com.BulletinBoardIntf;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class Client {

    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "BulletinBoard";
            Registry registry = LocateRegistry.getRegistry();
            //Compute comp = (Compute) registry.lookup(name);
            BulletinBoardIntf bb = (BulletinBoardIntf) registry.lookup(name);
            //Pi task = new Pi(Integer.parseInt(args[1]));
            //BigDecimal pi = comp.executeTask(task);
            int test = bb.getMessageCount();
            System.out.println("All Messages: " + test);
            bb.putMessage("Eine tolle Nachricht!");
            test = bb.getMessageCount();
            System.out.println("All Messages: " + test);
            String[] mes = bb.getMessages();
            System.out.println(Arrays.toString(mes));
        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }
}
