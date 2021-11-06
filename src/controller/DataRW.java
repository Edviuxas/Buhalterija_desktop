package controller;

import ds.FinanceManagementIS;
import ds.IndividualPersonUser;

import java.io.*;

public class DataRW {
//    public static FinanceManagementIS loadFinanceManagementSystemFromFile() {
//        FinanceManagementIS fmis = new FinanceManagementIS();
//        fmis.getAllUsers().add(new IndividualPersonUser("admin", "admin", -1, "admin", "admin", "admin"));
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("fmis.file"));
//            fmis = (FinanceManagementIS) ois.readObject();
//            ois.close();
//        } catch (ClassNotFoundException e) {
//            System.out.println("Failed with class recognition.");
//        } catch (IOException e) {
//            System.out.println("Failed to open file.");
//        }
//        return fmis;
//    }
//
//    public static void writeFinanceManagementSystemToFile(FinanceManagementIS fmis) throws IOException {
//        System.out.println("Using default name for saving...");
//        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("fmis.file"));
//        out.writeObject(fmis);
//        out.close();
//    }
}
