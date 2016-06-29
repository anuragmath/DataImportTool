package org.openmf.mifos.dataimport.dto;

import java.util.List;
import java.util.Locale;

public class PaymentInventory {

	private final transient Integer rowIndex;
	
	private final transient String status;
	
	private final Integer pdcTypeId;
	
	private final boolean isSeriesCheques;
	
	private final Locale locale;
	
	private final String dateFormat;
	
	private final List<PdcPaymentInventory> pdcData;
	
	public PaymentInventory(Integer pdcTypeId, Integer rowIndex, String status,
			List<PdcPaymentInventory> pdcData ){
		this.isSeriesCheques = false;
		this.pdcTypeId = pdcTypeId;
		this.locale = Locale.ENGLISH;
		this.rowIndex = rowIndex;
		this.status = status;
		this.pdcData = pdcData;
		this.dateFormat = "dd MMMM yyyy";
	}
	
	public boolean getIsSeriesCheques(){
		return isSeriesCheques;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public Integer getRowIndex() {
        return rowIndex;
    }
	
	public String getDateFormat() {
    	return dateFormat;
    }
	
	public String getStatus() {
        return status;
    }
	
	public List<PdcPaymentInventory> getPdcData(){
		return pdcData;
	}
	
	public Integer getPdcTypeId(){
		return pdcTypeId;
	}
}
