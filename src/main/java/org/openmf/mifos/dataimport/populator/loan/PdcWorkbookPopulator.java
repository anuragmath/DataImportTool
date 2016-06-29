package org.openmf.mifos.dataimport.populator.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.openmf.mifos.dataimport.dto.loan.CompactLoan;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.openmf.mifos.dataimport.populator.ExtrasSheetPopulator;

public class PdcWorkbookPopulator extends AbstractWorkbookPopulator{

	private ExtrasSheetPopulator extrasSheetPopulator;
	
	private static final int SERIAL_NO_COL = 0;
	private static final int LOAN_ACCOUNT_NO_COL = 1;
	private static final int CUSTOMER_NAME_COL = 2;
	private static final int PAYMENT_MODE_COL = 3;
	private static final int IS_SECURITY_PDC_COL = 4;
	private static final int BANK_NAME_COL = 5;
	private static final int BRANCH_NAME_COL = 6;
	private static final int IFSC_CODE_COL = 7;
	private static final int MICR_CODE_COL = 8;
	private static final int CHEQUE_NO_COL = 9;
	private static final int CHEQUE_DATE_COL = 10;
	private static final int CHEQUE_VALUE_COL = 11;
	private static final int CHEQUE_STATUS_COL = 12;
	
	public PdcWorkbookPopulator(RestClient restClient, ExtrasSheetPopulator extrasSheetPopulator) {
		this.extrasSheetPopulator = extrasSheetPopulator;
	}
	
	@Override
	public Result downloadAndParse() {
		Result result =  extrasSheetPopulator.downloadAndParse();
    	return result;
	}

	@Override
	public Result populate(Workbook workbook) {
		Sheet bulkPdcUploadSheet = workbook.createSheet("AddPaymentInventory");
    	setLayout(bulkPdcUploadSheet);
    	Result result = extrasSheetPopulator.populate(workbook);
        if(result.isSuccess()) 
            result = setRules(bulkPdcUploadSheet);
        return result;
	}
	
	private void setLayout(Sheet worksheet) {
		Row rowHeader = worksheet.createRow(0);
		rowHeader.setHeight((short)500);
		worksheet.setColumnWidth(SERIAL_NO_COL, 1500);
		worksheet.setColumnWidth(LOAN_ACCOUNT_NO_COL, 5000);
		worksheet.setColumnWidth(CUSTOMER_NAME_COL, 7000);
		worksheet.setColumnWidth(PAYMENT_MODE_COL, 3000);
		worksheet.setColumnWidth(IS_SECURITY_PDC_COL, 3000);
		worksheet.setColumnWidth(BANK_NAME_COL, 7000);
		worksheet.setColumnWidth(BRANCH_NAME_COL, 4000);
		worksheet.setColumnWidth(IFSC_CODE_COL, 4000);
		worksheet.setColumnWidth(MICR_CODE_COL, 4000);
		worksheet.setColumnWidth(CHEQUE_NO_COL, 3000);
		worksheet.setColumnWidth(CHEQUE_DATE_COL, 3000);
		worksheet.setColumnWidth(CHEQUE_VALUE_COL, 3000);
		worksheet.setColumnWidth(CHEQUE_STATUS_COL, 5000);
		writeString(SERIAL_NO_COL, rowHeader, "Sl No");
		writeString(LOAN_ACCOUNT_NO_COL, rowHeader, "Loan No*");
		writeString(CUSTOMER_NAME_COL, rowHeader, "Customer name*");
		writeString(PAYMENT_MODE_COL, rowHeader, "Mode of Payment*");
		writeString(IS_SECURITY_PDC_COL, rowHeader, "Security PDCs*");
		writeString(BANK_NAME_COL, rowHeader, "Bank Details*");
		writeString(BRANCH_NAME_COL, rowHeader, "BRANCH NAME");
		writeString(IFSC_CODE_COL, rowHeader, "IFSC CODE*");
		writeString(MICR_CODE_COL, rowHeader, "MICR CODE*");
		writeString(CHEQUE_NO_COL, rowHeader, "Chq Nos*");
		writeString(CHEQUE_DATE_COL, rowHeader, "Chq Date*");
		writeString(CHEQUE_VALUE_COL, rowHeader, "Chq Value*");
		writeString(CHEQUE_STATUS_COL, rowHeader, "Status");
	}

	 private Result setRules(Sheet worksheet) {
	    	Result result = new Result();
	    	try {
	    		
	        	CellRangeAddressList paymentTypeRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), PAYMENT_MODE_COL, PAYMENT_MODE_COL );
	        	
	        	
	        	DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet)worksheet);
	        
	        	setNames(worksheet);
	        	
	        	DataValidationConstraint paymentTypeConstraint = validationHelper.createFormulaListConstraint("PaymentTypes");
	        	
	        	DataValidation paymentTypeValidation = validationHelper.createValidation(paymentTypeConstraint, paymentTypeRange);
	        	
	            worksheet.addValidationData(paymentTypeValidation);
	        	
	    	} catch (RuntimeException re) {
	    		re.printStackTrace();
	    		result.addError(re.getMessage());
	    	}
	       return result;
	    }
	    
	    
	    
	    private void setNames(Sheet worksheet) {
	    	Workbook pdcWorkbook = worksheet.getWorkbook();
	    	
	    	//Payment Type Name
	    	Name paymentTypeGroup = pdcWorkbook.createName();
	    	paymentTypeGroup.setNameName("PaymentTypes");
	    	paymentTypeGroup.setRefersToFormula("Extras!$D$2:$D$" + (extrasSheetPopulator.getPaymentTypesSize() + 1));
	    }

}
