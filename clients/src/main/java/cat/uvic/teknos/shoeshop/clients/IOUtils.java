package cat.uvic.teknos.shoeshop.clients;


import cat.uvic.teknos.shoeshop.clients.exceptions.BackOfficeException;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    public static String readLine(BufferedReader in){
        String command;
        try{
            command = in.readLine();
        }catch (IOException e){
            throw new BackOfficeException("Error while reading the menu option", e);
        }
        return command;
    }
}
