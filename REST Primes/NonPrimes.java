package webtest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("Primes")
public class NonPrimes {
	
	static ArrayList<String> nonprimes = new ArrayList<String>();
	
	
	@GET
	@Path("/nonprimeList/{clientInput}")
	@Produces(MediaType.TEXT_XML)
	public String getNonPrimes(@PathParam("clientInput") String clientInput){
		
		for(int i = 0; i < nonprimes.size(); i++){
			
			if(clientInput.equals(nonprimes.get(i))) return "true";
			
		}
		
		return "false";
		
	}
	
	
	@GET
	@Path("/nonprime/{clientInput}")
	@Produces(MediaType.TEXT_XML)
	public String nonPrimes(@PathParam("clientInput") String clientInput){
		
		String xmlCatalogue = "<NonPrimes>" + "\n";
		boolean numberAlreadyExists = false;
		
		for(int i = 0; i < nonprimes.size(); i++){
			
			xmlCatalogue = xmlCatalogue + "<Number>";
			
			xmlCatalogue = xmlCatalogue + nonprimes.get(i) + "\n";
			
			if(clientInput.equals(nonprimes.get(i))) numberAlreadyExists = true;
			
			xmlCatalogue = xmlCatalogue + "</Number>" + "\n";
			
		}
		
		if(!numberAlreadyExists){
			
			nonprimes.add(clientInput);
			
			xmlCatalogue = xmlCatalogue + "<Number>";
					
			xmlCatalogue = xmlCatalogue + clientInput + "\n";
			
			xmlCatalogue = xmlCatalogue + "</Number>" + "\n";
			
		}
		
		xmlCatalogue = xmlCatalogue + "</NonPrimes>";
		
		return xmlCatalogue;	
		
	}
	
}
