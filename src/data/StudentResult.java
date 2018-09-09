package data;

/**
 * This class is designed to store mark information of a particular assessment.
 * @author Dovydas Rupsys
 */
public class StudentResult {
	private String _assessment;						//unit name and the assessment number
	private String _mark;							//mark achieved on the assessment
	private String _grade;							//grade achieved on the assessment
	
	/**
	 * Constructs student's result object.
	 * @param assessment Unit name and the assessment number.
	 * @param mark Mark achieved on the assessment.
	 */
	public StudentResult(String assessment, String mark, String grade){
		_assessment = assessment;
		_mark = mark;
		_grade = grade;
	}
	
	/**
	 * Return a string with the name of the unit and assessment number.
	 * @return Assessment name.
	 */
	public String getAssessmentName(){
		return _assessment;
	}
	
	/**
	 * Gets the mark achieved.
	 * @return Mark in a form of integer.
	 */
	public Float getMark() {
		try {
			return Float.parseFloat(_mark);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Get the grade achieved
	 * @return Grade achieved
	 */
	public String getGrade() {
			return _grade;
	}
		
	/**
	 * Returns string representation of the class.
	 */
	@Override
	public String toString() {
		return _assessment + " (mark " + _mark + ")";
	}
	
	/**
	 * Checks if this object contains the same data as some other student results object. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StudentResult){
			StudentResult sr = (StudentResult)obj;
			return toString().equals(sr.toString());
		}
		
		return false;
	}
	
}