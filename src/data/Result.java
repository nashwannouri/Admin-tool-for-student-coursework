package data;

/**
 * Stores result information.
 * @author Darren Middleton, Dovydas Rupsys
 */
public class Result {
	private String _candidateName;				//Stores the name of the candidate
	private String _candidateKey; 				//Stores the candidate key (anonymous code)
	private String _mark;						//Stores the mark
	private String _grade;						//Stores any marking exceptions
	
	/**
	 * Constructs results object.
	 * @param grade Marking note of the result.
	 * @param mark Mark achieved on the assessment.
	 * @param candidateKey Anonymous marking code. 
	 */
	public Result(String grade, String mark, String candidateKey){
		_candidateName = "N/A";
		_candidateKey = candidateKey;
		_grade = grade;
		_mark = mark;
	}
	
	/**
	 * Sets the candidate name to whom these results belong.
	 * @param name Name of the student.
	 */
	public void setCandidateName(String name){
		_candidateName = name;
	}
	
	/**
	 * Gets the name of the student to whom these results belong.
	 * @return Name of the student.
	 */
	public String getCandidateName(){
		return _candidateName;
	}
	
	/**
	 * Retrieves the candidate key.
	 * @return The candidate key.
	 */
	public String getCandidateKey(){
		return _candidateKey;
	}
	
	/**
	 * Gets the mark for that object
	 * @return The mark.
	 */
	public String getMark(){
		return _mark;
	}
	
	/**
	 * Gets any marking exceptions.
	 * @return Anyone marking exceptions for that object.
	 */
	public String getGrade(){
		return _grade;
	}

	/**
	 * Represents result object in a form of a string.
	 */
	@Override
	public String toString() {
		return _candidateName + " " + _candidateKey;
	}
	
	/**
	 * Checks if this result has the same candidate name and key as some other result.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Result){
			Result r = (Result)obj;
			
			return toString().equals(r.toString());
		}
		
		return false;
	}
}
