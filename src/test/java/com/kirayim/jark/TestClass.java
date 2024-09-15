/*
 * Copyright notice
 * This code is not covered by any copyright
 *
 * In no event shall the author(s) be liable for any special, direct, indirect, consequential,
 *  or incidental damages or any damages whatsoever, whether in an action of contract,
 *  negligence or other tort, arising out of or in connection with the use of the code or the
 *  contents of the code
 *
 *  All information in the code is provided "as is" with no guarantee of completeness, accuracy,
 *   timeliness or of the results obtained from the use of this code, and without warranty of any
 *   kind, express or implied, including, but not limited to warranties of performance,
 *   merchantability and fitness for a particular purpose.
 *
 *  The author(s) will not be liable to You or anyone else for any decision made or action
 *  taken in reliance on the information given by the code or for any consequential, special
 *  or similar damages, even if advised of the possibility of such damages.
 *
 *
 */

package com.kirayim.jark;


import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;


public class TestClass {
	private boolean valid = false;
	private String name = "Default name";
	private int theAswer = 42;
	private double notTheAnswer = 55.6;
	private Duration howLong = Duration.ofSeconds(3745);
	private Instant when = Instant.now();
	private Date joesBirthday = new Date();
	private TestSubClass sub = new TestSubClass();
	private String fileName = "/usr/local/lib/jvm/fred.txt";
//	private ArrayList<TestSubClass> listItems; // = Collections.(new TestSubClass(),
////			TestSubClass.builder().withSubItemDouble(44.45745).withSubItemEmptyDouble(12409.124).build());

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the theAswer
	 */
	public int getTheAswer() {
		return theAswer;
	}
	/**
	 * @param theAswer the theAswer to set
	 */
	public void setTheAswer(int theAswer) {
		this.theAswer = theAswer;
	}
	/**
	 * @return the notTheAnswer
	 */
	public double getNotTheAnswer() {
		return notTheAnswer;
	}
	/**
	 * @param notTheAnswer the notTheAnswer to set
	 */
	public void setNotTheAnswer(double notTheAnswer) {
		this.notTheAnswer = notTheAnswer;
	}


	public Date getJoesBirthday() {
		return joesBirthday;
	}

	public void setJoesBirthday(Date joesBirthday) {
		this.joesBirthday = joesBirthday;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TestClass testClass = (TestClass) o;
		return valid == testClass.valid && theAswer == testClass.theAswer && Double.compare(notTheAnswer, testClass.notTheAnswer) == 0 && Objects.equals(name, testClass.name) && Objects.equals(howLong, testClass.howLong) && Objects.equals(when, testClass.when) && Objects.equals(joesBirthday, testClass.joesBirthday) && Objects.equals(sub, testClass.sub);
	}

	@Override
	public int hashCode() {
		return Objects.hash(valid, name, theAswer, notTheAnswer, howLong, when, joesBirthday, sub);
	}

	/**
	 * @return the howLong
	 */
	public Duration getHowLong() {
		return howLong;
	}
	/**
	 * @param howLong the howLong to set
	 */
	public void setHowLong(Duration howLong) {
		this.howLong = howLong;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "TestClass{" +
				"valid=" + valid +
				", name='" + name + '\'' +
				", theAswer=" + theAswer +
				", notTheAnswer=" + notTheAnswer +
				", howLong=" + howLong +
				", when=" + when +
				", sub=" + sub +
//				", listItems=" + listItems +
				'}';
	}

	/**
	 * @return the when
	 */
	public Instant getWhen() {
		return when;
	}
	/**
	 * @param when the when to set
	 */
	public void setWhen(Instant when) {
		this.when = when;
	}
	/**
	 * @return the sub
	 */
	public TestSubClass getSub() {
		return sub;
	}
	/**
	 * @param sub the sub to set
	 */
	public void setSub(TestSubClass sub) {
		this.sub = sub;
	}
	/**
	 * @return the listItems
	 */



//	public ArrayList<TestSubClass> getListItems() {
//		return listItems;
//	}
//	/**
//	 * @param listItems the listItems to set
//	 */
//	public void setListItems(ArrayList<TestSubClass> listItems) {
//		this.listItems = listItems;
//	}
//

}
