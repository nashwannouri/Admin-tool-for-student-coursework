package data;
import java.util.ArrayList;

/**
 * Contains information about a particular assessment.
 * @author Darren Middleton, Dovydas Rupsys
 */
public class Assessment {
	private ArrayList<Result> _results;				//Declares a new object list "_results"	
	private String _module;							//Stores the module code.
	private String _assessment;						//Stores the assessment number.
	
	/**
	 * Constructs the assessment object.
	 * @param module Code of the specific module.
	 * @param assessment Code of the specific assessment.
	 */
	public Assessment(String module, String assessment){
		_module = module;
		_assessment = assessment;
		_results = new ArrayList<Result>();
	}

	public boolean containsResult(Result result){
		return _results.contains(result);
	}
	
	/**
	 * Adds a result to a list of results.
	 * @param result Result to be added.
	 */
	public void addResult(Result result){
		_results.add(result);
	}
	
	public String getAssessmentName(){
		return _assessment;
	}
	
	/**
	 * Gets the number of results in the list.
	 * @return The length of the results list.
	 */
	public int getResultsSize(){
		return _results.size();
	}
	
	/**
	 * Retrieves specified result. 
	 * @param Index The number in the results list to be selected.
	 * @return The selected result.
	 */
	public Result getResult(int index){
		return _results.get(index);
	}
	
	/**
	 * Gets the column names in a form of array.
	 * @return Array of string with column names.
	 */
	public String[] columnNamesArray(){
		return new String[] { "Student Name", "Student Number", "Mark", "Grade" };
	}
	
	/**
	 * Converts results into an array of rows and columns.
	 * @return an array of rows and columns with results data.
	 */
	public String[][] dataArray(){
		String[][] data = new String[_results.size()][];
		
		for (int i = 0; i < _results.size(); i++) {
			Result r = _results.get(i);
			data[i] = new String[] { r.getCandidateName(), r.getCandidateKey(), r.getMark(), r.getGrade() };
		}
		
		return data;
	}
	
	/**
	 * Converts this assessment into a string representation.
	 */
	@Override
	public String toString(){
		return _module + " " + _assessment;
	}
	
	/**
	 * Checks if this assessment is equal to some other object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Assessment){
			Assessment a = (Assessment)obj;
			
			return toString().equals(a.toString());
		}
		
		return false;
	}
}
