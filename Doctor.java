package drPatients;

import java.util.ArrayList;
import java.util.List;

public class Doctor {

	private int doctorId;
	private String doctorName;
	
	List<Patient> patientsList = new ArrayList<Patient>();

	public int getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public List<Patient> getPatientsList() {
		return patientsList;
	}

	public void setPatientsList(List<Patient> patientsList) {
		this.patientsList = patientsList;
	}
}
