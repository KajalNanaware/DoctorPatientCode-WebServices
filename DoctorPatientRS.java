package drPatients;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/")
public class DoctorPatientRS {
	@Context 
    private ServletContext sctx;          // dependency injection
    private static DoctorPatientList dlist; // set in populate()

    public DoctorPatientRS() { }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
	checkContext();
	return Response.ok(dlist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/xml");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {
	checkContext();
	return Response.ok(toJson(dlist), "application/json").build();
    }

    @GET    
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id: \\d+}")
    public Response getJson(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/json");
    }

    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain() {
	checkContext();
	return dlist.toString();
    }
    
    @GET
    @Path("/plain/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain(@PathParam("id") int id) {
	checkContext();
	return plainRequest(id);
	}
    
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("dname") String doctorName, @FormParam("dID") String doctorId, 
			   @FormParam("what") String what) {
    	
	checkContext();
	String msg = null;
	List<Patient> patientList1 = new ArrayList<Patient>();
	Patient newPatients = new Patient();
	
	// Require both properties to create.
	if (doctorName == null || doctorId == null) {
	    msg = "Property 'dID' or 'dname' is missing.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}	 
	else if(what.contains(";")) {
		
		String[] patientsArray = what.split(";");
		
		for(int i=0; i<patientsArray.length; i=i+3){
			String[] temp = patientsArray[i].split("!");
			newPatients.setPatientName(temp[0]);
			newPatients.setPatientInsuranceNum(temp[0]);
			patientList1.add(newPatients);
		}
			
	}	
	
	else if( what != null)
	{
		String[] temp = what.split("!");
		newPatients.setPatientName(temp[0]);
		newPatients.setPatientInsuranceNum(temp[0]);
		patientList1.add(newPatients);
	}
	// Otherwise, create the Prediction and add it to the collection.
	int id = addDoctorPatient(Integer.parseInt(doctorId), doctorName,patientList1);
	msg = "Doctor " + id + " created: (doctor = " + doctorName + " what = " + what + ").\n";
	return Response.ok(msg, "text/plain").build();
    }

    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("dname") String doctorName, @FormParam("dID") int doctorId) {
    	
	checkContext();

	// Check that sufficient data are present to do an edit.
	String msg = null;
	if (doctorName == null ) 
	    msg = "Doctor name is not given: nothing to edit.\n";

	//Prediction p = dlist.find(id);
	Doctor d = dlist.find(doctorId);
	if (d == null)
	    msg = "There is no Doctor with ID " + doctorId + "\n";

	if (msg != null)
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	// Update.
	if (doctorName != null) 
		d.setDoctorName(doctorName);	
	
	msg = "Doctor " + doctorId + " has been updated.\n";
	return Response.ok(msg, "text/plain").build();
    }

    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int doctorId) {
	checkContext();
	String msg = null;
	Doctor d = dlist.find(doctorId);
	if (d == null) {
	    msg = "There is no prediction with ID " + doctorId + ". Cannot delete.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}
	dlist.getDocs().remove(d);
	msg = "Doctor " + doctorId + " deleted.\n";

	return Response.ok(msg, "text/plain").build();
    }

    //** utilities
    private void checkContext() {
    	if (dlist == null) {
    		dlist = new DoctorPatientList();
    		populate();
    	}
    }

    private void populate() {
    	 List<Patient> patientTempList1= new ArrayList<Patient>();
    	  List<Patient> patientTempList2= new ArrayList<Patient>();
    	  
    	  
    	  String patientsDB = "/WEB-INF/data/patients.db";
    	  InputStream inputStream1 = sctx.getResourceAsStream(patientsDB);
    	  // Read the data into the array of Doctors. 
    	    if (inputStream1 != null) {
    	        try {
    	       BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1));
    	       int i = 0;
    	       String record = null;
    	       while ((record = reader.readLine()) != null) {
    	           String[] parts = record.split("!");     
    	           Patient p = new Patient();
    	           p.setPatientName(parts[0]);
    	           p.setPatientInsuranceNum(parts[1]);
    	         
    	          if(i<3) {
    	             patientTempList1.add(p);
    	            }
    				          else {
    	           patientTempList2.add(p);
    	          }
    	          i++;    
    	         }
    	        }
    	        catch (Exception e) { 
    	     throw new RuntimeException("I/O failed!"); 
    	        }
    	    }
    	  
    	  String doctorsDB = "/WEB-INF/data/drs.db";
    	  InputStream inputStream2 = sctx.getResourceAsStream(doctorsDB);
    	  
    	  // Read the data into the array of Doctor. 
    	    if (inputStream2 != null) {
    	        try {
    	     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream2));
    	     int i = 0;
    	     String record = null;
    	     while ((record = reader.readLine()) != null) {
    	         String[] parts = record.split("!");
    	 
    	         if(i==0) {
    	        	 
    	          addDoctorPatient(Integer.parseInt(parts[1]),parts[0],patientTempList1);
    	         }
    	         else {
    	          addDoctorPatient(Integer.parseInt(parts[1]),parts[0],patientTempList2);
    	         }
    	         
    	         i++;
    	     }
    	        }
    	        catch (Exception e) { 
    	     throw new RuntimeException("I/O failed!"+e); 
    	        }
    	    }
    }
   

    // Add a new prediction to the list.
    private int addDoctorPatient(int dId, String dname,List<Patient> docPatientList) {
	int id = dlist.add(dId, dname,docPatientList);
	return id;
    }

    // Prediction --> JSON document
    private String toJson(Doctor pred) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(pred);
	}
	catch(Exception e) { }
	return json;
    }

    // PredictionsList --> JSON document
    private String toJson(DoctorPatientList plist) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(plist);
	}
	catch(Exception e) { }
	return json;
    }
    
    private String plainRequest(int id){
    	Doctor doc = dlist.find(id);
    	if (doc == null) {
    	    String msg = id + " is a bad ID.\n";
    	    return msg;
    	}
    	else
    	{
    		DoctorPatientList docTempList = new DoctorPatientList();
    	    docTempList.add(doc.getDoctorId(), doc.getDoctorName(), doc.getPatientsList());
    		return docTempList.toString();
    	}
    }

    // Generate an HTTP error response or typed OK response.
    private Response toRequestedType(int id, String type) {
    	Doctor pred = dlist.find(id);
    	if (pred == null) {
    	    String msg = id + " is a bad ID.\n";
    	    return Response.status(Response.Status.BAD_REQUEST).
    		                                   entity(msg).
    		                                   type(MediaType.TEXT_PLAIN).
    		                                   build();
    	}
    	
    	else if (type.contains("json"))
    	    return Response.ok(toJson(pred), type).build();
    	else {
    		DoctorPatientList docTempList = new DoctorPatientList();

    	    docTempList.add(pred.getDoctorId(), pred.getDoctorName(), pred.getPatientsList());
    	    return Response.ok(docTempList, type).build(); // toXml is automatic
        }
    }
}
