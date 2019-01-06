package me.dolia.pmm.form;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import lombok.Data;

/**
 * Data Transfer Object (DTO) to deal with {@code Transaction} entity data.
 *
 * @author Maksym Dolia
 */
@Data
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
}
