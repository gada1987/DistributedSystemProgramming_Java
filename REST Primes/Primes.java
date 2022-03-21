package webtest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("Primes")
public class Primes{
	
	static ArrayList<String> primes = new ArrayList<String>();
	
	
	@GET
	@Path("/primeList/{clientInput}")
	@Produces(MediaType.TEXT_XML)
	public String getPrimes(@PathParam("clientInput") String clientInput){
		
		for(int i = 0; i < primes.size(); i++){
			
			if(clientInput.equals(primes.get(i))) return "true";
			
		}
		
		return "false";
		
	}
	
	
	@GET
	@Path("/prime/{clientInput}")
	@Produces(MediaType.TEXT_XML)
	public String prime(@PathParam("clientInput") String clientInput){
		
		String xmlCatalogue = "<Primes>" + "\n";
		boolean numberAlreadyExists = false;
		
		for(int i = 0; i < primes.size(); i++){
			
			xmlCatalogue = xmlCatalogue + "<Number>";
			
			xmlCatalogue = xmlCatalogue + primes.get(i) + "\n";
			
			if(clientInput.equals(primes.get(i))) numberAlreadyExists = true;
			
			xmlCatalogue = xmlCatalogue + "</Number>" + "\n";
			
		}
		
		if(!numberAlreadyExists){
			
			//cont.insert(clientInput);
			primes.add(clientInput);
			
			xmlCatalogue = xmlCatalogue + "<Number>";
					
			xmlCatalogue = xmlCatalogue + clientInput + "\n";
			
			xmlCatalogue = xmlCatalogue + "</Number>" + "\n";
			
		}
		
		xmlCatalogue = xmlCatalogue + "</Primes>";
		
		return xmlCatalogue;	
		
	}
	
}
