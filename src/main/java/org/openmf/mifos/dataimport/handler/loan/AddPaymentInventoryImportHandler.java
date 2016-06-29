package org.openmf.mifos.dataimport.handler.loan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.PDC;
import org.openmf.mifos.dataimport.dto.PaymentInventory;
import org.openmf.mifos.dataimport.dto.PdcPaymentInventory;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;

import com.google.gson.Gson;

public class AddPaymentInventoryImportHandler extends AbstractDataImportHandler {

	private final RestClient restClient;
	private final Workbook workbook;
	
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
	private static final int STATUS_COL = 13;
	
	private List<PaymentInventory> paymentInventory = new ArrayList<PaymentInventory>();
	private List<PdcPaymentInventory> pdcPaymentInventory = new ArrayList<PdcPaymentInventory>();
	private List<PDC> pdc = new ArrayList<PDC>();
	
    public AddPaymentInventoryImportHandler(Workbook workbook, RestClient client) {
		this.restClient = client;
		this.workbook = workbook;
	}
    
	@Override
	public Result parse() {
		
		Result result = new Result();
		Sheet PdcPaymentInventorySheet = workbook.getSheet("AddPaymentInventory");
		Integer noOfEntries = getNumberOfRows(PdcPaymentInventorySheet, SERIAL_NO_COL);
		 for (int rowIndex = 1; rowIndex < noOfEntries; rowIndex++) {
	            Row row1,row2;
	            try {
	                row1 = PdcPaymentInventorySheet.getRow(rowIndex);
	                int check = rowIndex +1;
	                if(check!= noOfEntries){
	                	row2 = PdcPaymentInventorySheet.getRow(rowIndex+1);
	                	if(readAsString(LOAN_ACCOUNT_NO_COL, row1).equals(readAsString(LOAN_ACCOUNT_NO_COL, row2)))
	                		pdcPaymentInventory.add(parseAsPdcPaymentInventory(row1));
	                	 else {
	                		pdcPaymentInventory.add(parseAsPdcPaymentInventory(row1));
	                		paymentInventory.add(parseAsPaymentInventory(row1, pdcPaymentInventory));
	                		pdc.add(parseAsPDC(paymentInventory, row1));
	                		pdcPaymentInventory = new ArrayList<PdcPaymentInventory>();
	                		paymentInventory = new ArrayList<PaymentInventory>();
	                	 }	
	                } else {
	                	pdcPaymentInventory.add(parseAsPdcPaymentInventory(row1));
                		paymentInventory.add(parseAsPaymentInventory(row1, pdcPaymentInventory));
                		pdc.add(parseAsPDC(paymentInventory, row1));
                		pdcPaymentInventory = new ArrayList<PdcPaymentInventory>();
                		paymentInventory = new ArrayList<PaymentInventory>();
	                }      
	            } catch (Exception e) {
	                result.addError("Row = " + rowIndex + " , " + e.getMessage());
	            }
	        }
	        return result;
	}
	

	private PDC parseAsPDC(List<PaymentInventory> paymentInventory, Row row) {
		String status = readAsString(STATUS_COL, row);
		return new PDC(Integer.parseInt(readAsString(LOAN_ACCOUNT_NO_COL, row).replaceAll("[^0-9]", "")),
				row.getRowNum(), status, paymentInventory );
	}

	private PdcPaymentInventory parseAsPdcPaymentInventory(Row row) {
		Double chequeAmount;
		String bankName = readAsString(BANK_NAME_COL, row);
		String chequeNo = readAsString(CHEQUE_NO_COL, row);
		String chequeDate = readAsDate(CHEQUE_DATE_COL, row);
		/*if(chequeDate.equals(""))
			chequeDate = ;*/
		String amount = readAsString(CHEQUE_VALUE_COL, row);
		String ifscCode = readAsString(IFSC_CODE_COL, row);
		
		if(amount.equals("NA"))
			chequeAmount = Double.parseDouble("0");
		else
			chequeAmount = Double.parseDouble(amount);
		
		return new PdcPaymentInventory(bankName, ifscCode, chequeDate, chequeAmount, chequeNo);
	}

	private PaymentInventory parseAsPaymentInventory(Row row, List<PdcPaymentInventory> pdcArray ) {
		
		String status = readAsString(STATUS_COL, row);
		String isSecurityPDC = readAsString(IS_SECURITY_PDC_COL, row);
		Integer pdcTypeId;

		if(isSecurityPDC.equals("YES"))
			pdcTypeId = 2;
		else
			pdcTypeId = 1;
		return new PaymentInventory(pdcTypeId, row.getRowNum(), status, pdcArray);
	}
	
	
	@Override
	public Result upload() {
		Result result = new Result();
        Sheet PdcPaymentInventorySheet = workbook.getSheet("AddPaymentInventory");
        restClient.createAuthToken();
        for(PDC array: pdc) {
        	try {
        		Gson gson = new Gson();
        		String payload = gson.toJson(array.getPaymentInventory());
        		String newPay = payload.substring(1, payload.length()-1);
        		restClient.post("loans/" + array.getAccountId() + "/paymentInventory", newPay);
        		Cell statusCell = PdcPaymentInventorySheet.getRow(array.getRowIndex()).createCell(STATUS_COL);
                statusCell.setCellValue("Imported");
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            } catch (Exception e) {
            	Cell loanAccountIdCell = PdcPaymentInventorySheet.getRow(array.getRowIndex()).createCell(LOAN_ACCOUNT_NO_COL);
                loanAccountIdCell.setCellValue(array.getAccountId());
            	String message = parseStatus(e.getMessage());
            	Cell statusCell = PdcPaymentInventorySheet.getRow(array.getRowIndex()).createCell(STATUS_COL);
            	statusCell.setCellValue(message);
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.RED));
                result.addError("Row = " + array.getRowIndex() + " ," + message);
            }
        }
        PdcPaymentInventorySheet.setColumnWidth(STATUS_COL, 15000);
    	writeString(STATUS_COL, PdcPaymentInventorySheet.getRow(0), "Status");
        return result;
	}
	
	public List<PaymentInventory> getPaymentInventory() {
		return paymentInventory;
	}

}
