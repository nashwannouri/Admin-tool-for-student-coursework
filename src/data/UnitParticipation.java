package data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import windows.MainWindow;

/**
 * This class extract students' names and their last participations from the
 * provided table.
 * @author Dovydas Rupsys, Nashwan Nouri
 */
public class UnitParticipation {
	private String _module; 						// the name of module this data is for
	private ArrayList<UnitVisit> _participants; 	//the list of data in this format { student's name, town, location, time of last visit }

	// Pattern used to match all html tags with student's email and his participation data
	private static Pattern _rowPattern = Pattern.compile("(<td\\s*class\\s*=\\s*\"cell\\s*)(c3|c4|c5|c6)\"[^>]*>((.|\\.)*?)</td>");
	// Pattern that matches all html tags
	private static Pattern _htmlTagPattern = Pattern.compile("<[^>]*>");
	
	/**
	 * Constructs participation object.
	 * @param htmlTable Table to be parsed.
	 */
	public UnitParticipation(String module, String htmlTable) {
		_participants = new ArrayList<UnitVisit>();
		
		// Ask the user to provide the module name for this object
		_module = module;

		// find all cells that contain student's name and his participation data
		Matcher rowMatcher = _rowPattern.matcher(htmlTable);
		while (rowMatcher.find()) {
			//remove all html tags from the match
			String email = _htmlTagPattern.matcher(rowMatcher.group()).replaceAll("");
			String time = "";
			String town = "";
			String country = "";
			
			// if there is participation data for this student then remove all html tags from it and save it
			if (rowMatcher.find())
				town = _htmlTagPattern.matcher(rowMatcher.group()).replaceAll("");

			//if town and country data is not available
			if (town.equals("now") || town.matches(".*[0-9].*")) {
				time = town;
				town = " ";
				country = " ";		
			} 
			
			else{
				if (rowMatcher.find())
					country = _htmlTagPattern.matcher(rowMatcher.group()).replaceAll("");
				
				// if town is not available
				if (country.equals("now") || country.matches(".*[0-9].*")) {
					time = country;
					country = town;
					town = " ";
				} 
				
				// if everything is available
				else {
					if (rowMatcher.find())
						time = _htmlTagPattern.matcher(rowMatcher.group()).replaceAll("");
				}
			}
	
			// if student's name and time  of last visit are not empty string
			// then add them to the list of participants
			if (email.length() > 0 && time.length() > 0)
				_participants.add(new UnitVisit(_module, email, time, town, country));
		}
	}

	/**
	 * Applies participation data on the list of students.
	 * @param students The list of students to be used.
	 */
	public void applyParticipantData(MainWindow window) {
		for (int i = 0; i < _participants.size(); i++) {
			UnitVisit v = _participants.get(i);
			Student student = window.findStudentByEmail(v.getEmail());
			if (student != null)
				student.addLastVisit(v);
		}
	}

	/**
	 * Creates a string representation of the object.
	 */
	@Override
	public String toString() {
		String result = _module + ":\n";
		
		for (int i = 0; i < _participants.size(); i++) {
			result += "\t" + _participants.get(i) + "\n";
		}
		
		return result;
	}
}
