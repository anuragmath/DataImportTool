package org.openmf.mifos.dataimport.dto;

import java.util.Locale;

public class Clear {

	private final transient Integer rowIndex;
	
	private final transient Integer accountId;

	 private final String transactionAmount;

	 private final String transactionDate;

	 private final String dateFormat;

	 private final Locale locale;
	 
	 private final transient Integer transactionId;

	    public Clear(String transactionAmount, String transactionDate, Integer accountId, Integer transactionId, Integer rowIndex) {
		    this.transactionAmount = transactionAmount;
	        this.transactionDate = transactionDate;
	        this.accountId = accountId;
	        this.transactionId = transactionId;
	        this.rowIndex = rowIndex;
	        this.dateFormat = "dd MMMM yyyy";
	        this.locale = Locale.ENGLISH;
	    }
	    
		public String getTransactionAmount() {
	    	return transactionAmount;
	    }

	    public String getTransactionDate() {
		    return transactionDate;
	    }

	    public Locale getLocale() {
	    	return locale;
	    }

	    public String getDateFormat() {
	    	return dateFormat;
	    }

	    public Integer getRowIndex() {
	        return rowIndex;
	    }

	    public Integer getAccountId() {
	    	return accountId;
	    }

	    public Integer getTransactionId(){
	    	return transactionId;
	    }

}
