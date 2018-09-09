package data;

import java.util.ArrayList;

/**
 * Class to create student object.
 * @author Nashwan Nouri, Dovydas Rupsys
 */
public class Student {
	
		private String _name;											//Stores the name of the student
		private String _id;												//Stores the id of the student
		private String _email;											//Stores the email of the student
		private	String _tutor;											//Stores name of the student's tutor
		private ArrayList<String> _anonymous_code;						//Will contain student's anonymous marking codes 
		private ArrayList<StudentResult> _results;						//Stores the student's results of all as assessments
		private ArrayList<UnitVisit> _lastVisits;						//Stores all known unit visits
		
		/**
		 * Constructs student's object.
		 * @param name Name of the student.
		 * @param number Student's id number.
		 * @param email Student's email address.
		 * @param tutor The name of student's tutor.
		 */
		public Student(String name, String number, String email, String tutor) {
			_name = name;
			_id = number;
			_email = email;
			_tutor = tutor;
			_anonymous_code = new ArrayList<String>();
			_results = new ArrayList<StudentResult>();
			_lastVisits = new ArrayList<UnitVisit>();
		}
		
		/**
		 * Adds participation data to the list of visits or updates old data if it exists.
		 * @param v Data to be added.
		 */
		public void addLastVisit(UnitVisit v){
			int vIndex = _lastVisits.indexOf(v);
			
			if (vIndex != -1){
				_lastVisits.remove(vIndex);
				_lastVisits.add(vIndex, v);
			}
			else
				_lastVisits.add(v);
		}
		
		/**
		 * Gets the number of known last unit visit.
		 * @return Number of visits.
		 */
		public int getUnitVisitedCount(){
			return _lastVisits.size();
		}
		
		/**
		 * Gets the specified unit visit.
		 * @param index Visit to be retrieved.
		 * @return Visit object.
		 */
		public UnitVisit getUnitVisited(int index){
			return _lastVisits.get(index);
		}
		
		/**
		 * Adds result to the list of results if it doesn't already exist.
		 * @param result Student result object to be added.
		 */
		public void addResult(StudentResult result){
			if (!_results.contains(result))
				_results.add(result);
		}
		
		/**
		 * Gets the size of the student results list.
		 * @return Integer representing the number of results in the list.
		 */
		public int getStudentResultsSize(){
			return _results.size();
		}
		
		/**
		 * Gets the student result specified.
		 * @param index Position on the list.
		 * @return Student result object in the specified position.
		 */
		public StudentResult getStudentResult(int index){
			return _results.get(index);
		}
		
		/**
		 * Adds an anonymous marking code to the list of codes.
		 * @param anonymousCode code to be added.
		 */
		public void addAnonymousCode(String anonymousCode){
			_anonymous_code.add(anonymousCode);
		}
		
		/**
		 * Checks if provided anonymous code is assigned to this student.
		 * @param anonymousCode Anonymous code to be searched.
		 * @return Real students id if anonymous marking code was found, otherwise empty string.
		 */
		public String deAnonymiseCode(String anonymousCode){
			//If anonymous code is the id of the student then return it
			if (anonymousCode.equals(_id))
				return _id;
			
			//If anonymous code was found in the list of codes then return students id
			for (int i = 0; i < _anonymous_code.size(); i++) {
				if (_anonymous_code.get(i).equals(anonymousCode))
					return _id;
			}
			
			return "";
		}
		
		/**
		 * Gets the name of the student.
		 * @return String representing student's name.
		 */
		public String getName() {
			return _name;
		}
		
		/**
		 * Gets the id number of the student.
		 * @return Integer value that represent student's id;
		 */
		public String getNumber() {
			return _id;
		}
		
		/**
		 * Gets the email of the students.
		 * @return String that represents the email of the student.
		 */
		public String getEmail() {
			return _email;
		}
		
		/**
		 * Gets the name of student's tutor.
		 * @return String that represents the name of the tutor.
		 */
		public String getTutor() {
			return _tutor;
		}
		
		/**
		 * Creates a string that represents the student.  
		 */
		public String toString() {
			return _name + " (" + _id + ")";
		}
	}	