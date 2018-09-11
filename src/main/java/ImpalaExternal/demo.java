package ImpalaExternal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class demo {

	
	public static void main (String args[]) throws IOException, InterruptedException{
	 String cmd = "/home/exa1/.embulk/bin guess /home/exa1/mongo.yml -o /home/exa1/cconfig.yml";
	   Process pr = Runtime.getRuntime().exec(cmd);
	   //System.out.println("working");
	   pr.waitFor();
     

     // Grab output and print to display
     BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));

     String line = "";
     while ((line = reader.readLine()) != null) {
         System.out.println(line);
     }
	}
}
