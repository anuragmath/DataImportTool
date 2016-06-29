package org.openmf.mifos.dataimport.dto;

import java.time.LocalDate;

public class PdcPaymentInventory {
	
	private final String nameOfBank;
	private final String ifscCode;
	private final String chequeDate;
	private final Double amount;
	private final boolean makePresentation;
	private final int presentationStatus;
	private final String date;
	private final Integer period;
	private final String chequeNo;
	
	public PdcPaymentInventory(String nameOfBank, String ifscCode, String chequeDate, Double amount, String chequeNo) {
		super();
		this.nameOfBank = nameOfBank;
		this.ifscCode = ifscCode;
		this.chequeDate = chequeDate;
		this.amount = amount;
		this.chequeNo = chequeNo;
		this.makePresentation = false;
		this.presentationStatus = 1;
		this.date = chequeDate;
		this.period = 1;
	}

	public String getNameOfBank() {
		return nameOfBank;
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
