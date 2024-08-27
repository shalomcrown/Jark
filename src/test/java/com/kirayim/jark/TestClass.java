package com.kirayim.jark;


import java.time.Duration;
import java.time.Instant;


public class TestClass {
	private boolean valid = false;
	private String name = "Default name";
	private int theAswer = 42;
	private double notTheAnswer = 55.6;
	private Duration howLong = Duration.ofSeconds(3745);
	private Instant when = Instant.now();
	private TestSubClass sub = new TestSubClass();
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((howLong == null) ? 0 : howLong.hashCode());
//		result = prime * result + ((listItems == null) ? 0 : listItems.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(notTheAnswer);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((sub == null) ? 0 : sub.hashCode());
		result = prime * result + theAswer;
		result = prime * result + (valid ? 1231 : 1237);
		result = prime * result + ((when == null) ? 0 : when.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestClass other = (TestClass) obj;
		if (howLong == null) {
			if (other.howLong != null)
				return false;
		} else if (!howLong.equals(other.howLong))
			return false;
//		if (listItems == null) {
//			if (other.listItems != null)
//				return false;
//		} else if (! Collections.isEqualCollection(listItems, other.listItems))
//			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(notTheAnswer) != Double.doubleToLongBits(other.notTheAnswer))
			return false;
		if (sub == null) {
			if (other.sub != null)
				return false;
		} else if (!sub.equals(other.sub))
			return false;
		if (theAswer != other.theAswer)
			return false;
		if (valid != other.valid)
			return false;
		if (when == null) {
			if (other.when != null)
				return false;
		} else if (!when.equals(other.when))
			return false;
		return true;
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
