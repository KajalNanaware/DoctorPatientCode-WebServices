package drPatients;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "DoctorPatientList")

public class DoctorPatientList {
	
private List<Doctor> docs;
	
	public DoctorPatientList() { 
		docs = new CopyOnWriteArrayList<Doctor>(); 
	    }
	
	@XmlElement 
    @XmlElementWrapper(name = "doctors") 
    public List<Doctor> getDocs() {
		return docs;
	}

	public void setDocs(List<Doctor> docs) {
		this.docs = docs;
	}	

    @Override
    public String toString() {
		String s = "";
		for(Doctor p : docs) {
			List<Patient> patientList1 = p.getPatientsList();
			
			s +=p.getDoctorId()+":"+p.getDoctorName().toString()+"\n";
			
			s +="Patients for Doctor "+ p.getDoctorName() + " ";
			
			s +="\n";
			
			for(int i=0;i<patientList1.size();i++) {
				s +=""+patientList1.get(i).getPatientName()+":"+patientList1.get(i).getPatientInsuranceNum()+"\n";
			}			
			s +="\n";
		}
		return s;
    }

    public Doctor find(int id) {
	Doctor doc = null;
	// Search the list -- for now, the list is short enough that
	// a linear search is ok but binary search would be better if the
	// list got to be an order-of-magnitude larger in size.
	for (Doctor d : docs) {
	    if (d.getDoctorId() == id) {
		doc = d;
		break;
	    }
	}	
	return doc;
    }
    
    
    public int add(int id,String name,List<Patient> docPatientList) {	
	Doctor d = new Doctor();	
	d.setDoctorId(id);
	d.setDoctorName(name);
	d.setPatientsList(docPatientList);
	docs.add(d);	
	return 0;
	}

}
