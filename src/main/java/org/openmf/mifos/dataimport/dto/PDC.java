package org.openmf.mifos.dataimport.dto;

import java.util.List;

public class PDC {

private final transient Integer rowIndex;
	
	private final transient String status;
	
	private final Integer accountId;
	
	private final List<PaymentInventory> paymentInventory;
	
	public PDC(Integer accoudId, Integer rowIndex, String status, List<PaymentInventory> paymentInventory) {
		this.accountId = accoudId;
		this.rowIndex = rowIndex;
		this.status = status;
		this.paymentInventory = paymentInventory;
	}

	public Integer getRowIndex() {
		return rowIndex;
	}

	public String getStatus() {
		return status;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public List<PaymentInventory> getPaymentInventory() {
		return paymentInventory;
	}
	
	
	
}
