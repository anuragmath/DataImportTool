package org.openmf.mifos.dataimport.dto;

public class PdcPaymentInventory {
	
	private final String nameOfBank;
	private final String ifscCode;
	private final String chequeDate;
	private final Double amount;
	private final boolean makePresentation;
	private final int presentationStatus;
	private final String date;
	private final Integer period;
	private final String branchName;
	private final String micrCode;
	private final String chequeNo;
	
	public PdcPaymentInventory(String nameOfBank, String branchName, String ifscCode, String micrCode, String chequeDate, Double amount, String chequeNo, int chequeStatus) {
		super();
		this.nameOfBank = nameOfBank;
		this.branchName = branchName;
		this.ifscCode = ifscCode;
		this.micrCode = micrCode;
		this.chequeDate = chequeDate;
		this.amount = amount;
		this.chequeNo = chequeNo;
		this.makePresentation = false;
		this.presentationStatus = chequeStatus;
		this.date = chequeDate;
		this.period = 0;
	}

	public String getNameOfBank() {
		return nameOfBank;
	}

	public String getBranchName() {
		return branchName;
	}
	
	public String getMicrCode() {
		return micrCode;
	}
	
	public String getIfscCode() {
		return ifscCode;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public Double getAmount() {
		return amount;
	}

	public boolean isMakePresentation() {
		return makePresentation;
	}

	public int getPresentationStatus() {
		return presentationStatus;
	}

	public String getDate() {
		return date;
	}

	public String getChequeNo() {
		return chequeNo;
	}
}
