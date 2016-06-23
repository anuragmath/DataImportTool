package org.openmf.mifos.dataimport.handler.loan;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Transaction;
import org.openmf.mifos.dataimport.dto.Undo;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import com.google.gson.Gson;

public class LoanTransactionReversalDataImportHandler extends AbstractDataImportHandler {
	
	private final RestClient restClient;
	private final Workbook workbook;
	
	private List<Undo> loanTransactionReversals;
	
	private static final int LOAN_ACCOUNT_NO_COL = 0;
	private static final int TRANSACTION_ID_COL = 1;
	private static final int AMOUNT_COL = 3;
	private static final int BANK_NAME_COL = 4;
	private static final int CHEQUE_NO_COL = 5;
	private static final int PDC_STATUS_COL = 6;
	private static final int TRANSACTION_DATE_COL = 2; 
	private static final int STATUS_COL = 7;
	
	public LoanTransactionReversalDataImportHandler(Workbook workbook, RestClient client) {
		this.restClient = client;
		this.workbook = workbook;
		loanTransactionReversals = new ArrayList<Undo>();
	}
			

	@Override
	public Result parse() {
		
		Result result = new Result();
        Sheet loanTransactionReversalSheet = workbook.getSheet("LoanTransactionReversal");
        Integer noOfEntries = getNumberOfRows(loanTransactionReversalSheet, LOAN_ACCOUNT_NO_COL);
        for (int rowIndex = 1; rowIndex < noOfEntries; rowIndex++) {
            Row row;
            try {
                row = loanTransactionReversalSheet.getRow(rowIndex);
                if(isNotImported(row, STATUS_COL))
                    loanTransactionReversals.add(parseAsLoanTransactionReversal(row));
            } catch (Exception e) {
                result.addError("Row = " + rowIndex + " , " + e.getMessage());
            }
        }
        return result;
	}
	
	private Undo parseAsLoanTransactionReversal(Row row) {
		
		String loanAccountIdCheck = readAsString(LOAN_ACCOUNT_NO_COL, row);
		String newLoanAccountId =  loanAccountIdCheck.replaceAll("[^0-9]", "");
	    String repaymentAmount;
	    String status;
	    String pdcStatus = readAsInt(PDC_STATUS_COL, row).toString();
	    if(pdcStatus.equals("Bounced")){
	    	repaymentAmount= "0";
	    	status = "3";
	    }
	    else{
	    	repaymentAmount = readAsDouble(AMOUNT_COL, row).toString();
	    	status = "2";
	    }
        String transactionId = readAsLong(TRANSACTION_ID_COL, row).toString();
        String transactionDate = readAsDate(TRANSACTION_DATE_COL, row);
        return new Undo(repaymentAmount, transactionDate, Integer.parseInt(newLoanAccountId), Integer.parseInt(transactionId), Integer.parseInt(status), row.getRowNum());
        
	}
	
	@Override
	public Result upload() {
		Result result = new Result();
        Sheet loanTransactionReversalSheet = workbook.getSheet("LoanTransactionReversal");
        restClient.createAuthToken();
        for(Undo loanTransactionReversal : loanTransactionReversals) {
        	try {
        		Gson gson = new Gson();
        		String payload = gson.toJson(loanTransactionReversal);
        		if(loanTransactionReversal.getTransactionAmount().equals("0"))
        			restClient.post("loans/" + loanTransactionReversal.getAccountId() + "/transactions/" + loanTransactionReversal.getTransactionId() + "?command=undo", payload);
        		else
        			restClient.put("loans/" + loanTransactionReversal.getAccountId() + "/transactions/" + loanTransactionReversal.getTransactionId() , payload);
        		Cell statusCell = loanTransactionReversalSheet.getRow(loanTransactionReversal.getRowIndex()).createCell(STATUS_COL);
                statusCell.setCellValue("Imported");
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            } catch (Exception e) {
            	Cell loanAccountIdCell = loanTransactionReversalSheet.getRow(loanTransactionReversal.getRowIndex()).createCell(LOAN_ACCOUNT_NO_COL);
                loanAccountIdCell.setCellValue(loanTransactionReversal.getAccountId());
            	String message = parseStatus(e.getMessage());
            	Cell statusCell = loanTransactionReversalSheet.getRow(loanTransactionReversal.getRowIndex()).createCell(STATUS_COL);
            	statusCell.setCellValue(message);
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.RED));
                result.addError("Row = " + loanTransactionReversal.getRowIndex() + " ," + message);
            }
        }
        loanTransactionReversalSheet.setColumnWidth(STATUS_COL, 15000);
    	writeString(STATUS_COL, loanTransactionReversalSheet.getRow(0), "Status");
        return result;
	}
	
	public List<Undo> getLoanRepayments() {
    	return loanTransactionReversals;
    }
}
