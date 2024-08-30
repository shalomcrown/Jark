package com.kirayim.jark;

import javax.annotation.processing.Generated;

public class TestSubClass {

	enum TestTypes {NONE, ALL, RANDOM, SMILEY};

	private int subItemNumber = 34;
	private Double subItemEmptyDouble = null;
	private Double subItemDouble = 345.33;
	TestTypes testType = TestTypes.ALL;

	public TestSubClass() {
	}


	@Generated("SparkTools")
	private TestSubClass(Builder builder) {
		this.subItemNumber = builder.subItemNumber;
		this.subItemEmptyDouble = builder.subItemEmptyDouble;
		this.subItemDouble = builder.subItemDouble;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subItemDouble == null) ? 0 : subItemDouble.hashCode());
		result = prime * result + ((subItemEmptyDouble == null) ? 0 : subItemEmptyDouble.hashCode());
		result = prime * result + subItemNumber;
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
		TestSubClass other = (TestSubClass) obj;
		if (subItemDouble == null) {
			if (other.subItemDouble != null)
				return false;
		} else if (!subItemDouble.equals(other.subItemDouble))
			return false;
		if (subItemEmptyDouble == null) {
			if (other.subItemEmptyDouble != null)
				return false;
		} else if (!subItemEmptyDouble.equals(other.subItemEmptyDouble))
			return false;
		if (subItemNumber != other.subItemNumber)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TestSubClass [subItemNumber=" + subItemNumber + ", subItemEmptyDouble=" + subItemEmptyDouble
				+ ", subItemDouble=" + subItemDouble + "]";
	}
	/**
	 * @return the subItemNumber
	 */
	public int getSubItemNumber() {
		return subItemNumber;
	}
	/**
	 * @param subItemNumber the subItemNumber to set
	 */
	public void setSubItemNumber(int subItemNumber) {
		this.subItemNumber = subItemNumber;
	}
	/**
	 * @return the subItemEmptyDouble
	 */
	public Double getSubItemEmptyDouble() {
		return subItemEmptyDouble;
	}
	/**
	 * @param subItemEmptyDouble the subItemEmptyDouble to set
	 */
	public void setSubItemEmptyDouble(Double subItemEmptyDouble) {
		this.subItemEmptyDouble = subItemEmptyDouble;
	}
	/**
	 * @return the subItemDouble
	 */
	public Double getSubItemDouble() {
		return subItemDouble;
	}
	/**
	 * @param subItemDouble the subItemDouble to set
	 */
	public void setSubItemDouble(Double subItemDouble) {
		this.subItemDouble = subItemDouble;
	}

	public TestTypes getTestType() {
		return testType;
	}

	public void setTestType(TestTypes testType) {
		this.testType = testType;
	}

	/**
	 * Creates builder to build {@link TestSubClass}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Builder to build {@link TestSubClass}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private int subItemNumber;
		private Double subItemEmptyDouble;
		private Double subItemDouble;

		private Builder() {
		}

		public Builder withSubItemNumber(int subItemNumber) {
			this.subItemNumber = subItemNumber;
			return this;
		}

		public Builder withSubItemEmptyDouble(Double subItemEmptyDouble) {
			this.subItemEmptyDouble = subItemEmptyDouble;
			return this;
		}

		public Builder withSubItemDouble(Double subItemDouble) {
			this.subItemDouble = subItemDouble;
			return this;
		}

		public TestSubClass build() {
			return new TestSubClass(this);
		}
	}



}
