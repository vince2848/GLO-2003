package ca.ulaval.glo2003.beds.bookings.transactions.domain.helpers;

import static ca.ulaval.glo2003.beds.bookings.transactions.domain.helpers.TransactionObjectMother.*;

import ca.ulaval.glo2003.beds.bookings.transactions.domain.Transaction;
import ca.ulaval.glo2003.beds.bookings.transactions.domain.TransactionReasons;
import ca.ulaval.glo2003.beds.domain.Price;
import java.time.LocalDate;

public class TransactionBuilder {

  private TransactionBuilder() {}

  private LocalDate DEFAULT_TIMESTAMP = createTimestamp();
  private LocalDate timestamp = DEFAULT_TIMESTAMP;

  private String DEFAULT_FROM = createFrom();
  private String from = DEFAULT_FROM;

  private String DEFAULT_TO = createTo();
  private String to = DEFAULT_TO;

  private Price DEFAULT_TOTAL = createTotal();
  private Price total = DEFAULT_TOTAL;

  private TransactionReasons DEFAULT_REASON = createReason();
  private TransactionReasons reason = DEFAULT_REASON;

  public static TransactionBuilder aTransaction() {
    return new TransactionBuilder();
  }

  public Transaction build() {
    return new Transaction(timestamp, from, to, total, reason);
  }

  public TransactionBuilder withTransactionReason(TransactionReasons reason) {
    this.reason = reason;
    return this;
  }

  public TransactionBuilder withFrom(String from) {
    this.from = from;
    return this;
  }

  public TransactionBuilder withTo(String to) {
    this.to = to;
    return this;
  }

  public TransactionBuilder withTotal(Price total) {
    this.total = total;
    return this;
  }

  public TransactionBuilder withTimestamp(LocalDate timestamp) {
    this.timestamp = timestamp;
    return this;
  }
}
