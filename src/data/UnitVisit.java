package data;

/**
 * Stores the last time student visited some unit.
 * @author Dovydas Rupsys, Nashwan Nouri
 */
public class UnitVisit {
	private String _module;									//the name of module this data is for
	private String _email;									//the email of the student
	private String _time;									//the time since last visit
	private String _town;									//the town of the student during the last visit
	private String _country;								//the country of the student during the last visit
	
	/**
	 * Constructs the object.
	 * @param module The name of module visited.
	 * @param name The name of student.
	 * @param time The time since last visit.
	 * @param country The country of the last visit
	 * @param town  The town of the last visit
	 */
	public UnitVisit(String module, String name, String time, String town, String country) {
		_module = module;
		_email = name;
		_time = time;
		_town =town;
		_country = country;
	}
	
	/**
	 * Gets module name.
	 * @return Module name.
	 */
	public String getModule(){
		return _module;
	}
	
	/**
	 * Gets student name.
	 * @return Student name.
	 */
	public String getEmail(){
		return _email;
	}
	
	/**
	 * Gets time since last visit.
	 * @return time since last visit.
	 */
	public String getTime(){
		return _time;
	}
	
	/**
	 * get students town at the last visit
	 * @return students town at the last visit
	 */
	public String getTown(){
		return _town;
	}
	
	/**
	 * get students country at the last visit
	 * @return students country at the last visit
	 */
	public String getCountry(){
		return _country;
	}
	
	/**
	 * Checks if this object is equal to some other object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnitVisit){
			UnitVisit v = (UnitVisit)obj;
			return _module == v.getModule() && _email == v.getEmail() && _time == v.getTime();
		}
		
		return false;
	}
	
	/**
	 * Converts this object to a string representation.
	 */
	@Override
	public String toString() {
		String result = "";
		
		if (_time.equals("now"))
			result = _module + " last visited: " + _time;
		else
			result = _module + " last visited: " + _time + " ago";
		
		return result;
	}
}
