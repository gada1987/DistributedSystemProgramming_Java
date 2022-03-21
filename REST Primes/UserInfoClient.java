package webtest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class UserInfoClient {

    public static void main(String[] args) {
    	
    	//NonPrimes np;
    	//Primes p;
    	
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target(getBaseURI());
    	
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter number/s that you want to check for primeness, end with 0");
		
		while(true){
			
			int primeOrNot = input.nextInt();
			
			if(primeOrNot == 0) break;
			
			boolean unknown = true;
			
			String np =
	                target.path("nonprimeList").path(String.valueOf(primeOrNot)).request().accept(MediaType.TEXT_XML).get(String.class);
			
			if(np.equals("true")){
				
				System.out.println("not a prime\n");
				continue;
				
			}
			
			String p =
	                target.path("primeList").path(String.valueOf(primeOrNot)).request().accept(MediaType.TEXT_XML).get(String.class);
			
			if(p.equals("true")){
				
				System.out.println("is a prime\n");
				continue;
				
			}
			
			System.out.println("unknown, calculating if prime or not\n");
			
			
			
			boolean isPrime = true;
			
			if(primeOrNot < 2) isPrime = false;
			
			if(primeOrNot == 0) break;
			
			//Determine if number is prime or not
			int i, j;
			for(i = 2; i <= primeOrNot/2; i++){
				
				if((primeOrNot%i) == 0){
					
					isPrime = false;
					break;
					
				}
				
			}
			
			if(isPrime){
				
				System.out.println("Number is prime\n");
				
				//Number is a prime, check if it exists in xml catalogue
				//If it doesn't exist, insert it in to the catalogue
				target.path("prime").path(String.valueOf(primeOrNot)).request().accept(MediaType.TEXT_XML).get(String.class);
				
			}
			else{
				
				System.out.println("Number is not prime\n");
				
				//Number is not a prime, check if it exists in xml catalogue
				//If it doesn't exist, insert it in to the catalogue
				target.path("nonprime").path(String.valueOf(primeOrNot)).request().accept(MediaType.TEXT_XML).get(String.class);
				
			}
			
		}
		
    }
    
    private static URI getBaseURI(){
    	
        return UriBuilder.fromUri("http://localhost:8080/webtest/rest/Primes").build();
        
    }
    
    
}