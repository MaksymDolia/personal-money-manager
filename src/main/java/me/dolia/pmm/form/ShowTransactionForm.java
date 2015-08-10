package me.dolia.pmm.form;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ShowTransactionForm {

	private Date fromDate;
	private Date toDate;
	private SortTransaction sortBy = SortTransaction.DATE;
	private String comment;

	public ShowTransactionForm() {
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.set(Calendar.DAY_OF_MONTH, 1);
		fromDate = startCalendar.getTime();
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		toDate = endCalendar.getTime();
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public SortTransaction getSortBy() {
		return sortBy;
	}

	public void setSortBy(SortTransaction sortBy) {
		this.sortBy = sortBy;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
