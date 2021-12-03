package Model;

public class TransactionModel {

    String transaction_date, transaction_time, transaction_id, transaction_method;
    Double transaction_order,  amount_settled;

    public TransactionModel() {

    }

    public TransactionModel(String transaction_date, String transaction_time, String transaction_id, String transaction_method, Double transaction_order, Double amount_settled) {
        this.transaction_date = transaction_date;
        this.transaction_time = transaction_time;
        this.transaction_id = transaction_id;
        this.transaction_method = transaction_method;
        this.transaction_order = transaction_order;
        this.amount_settled = amount_settled;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(String transaction_time) {
        this.transaction_time = transaction_time;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_method() {
        return transaction_method;
    }

    public void setTransaction_method(String transaction_method) {
        this.transaction_method = transaction_method;
    }

    public Double getTransaction_order() {
        return transaction_order;
    }

    public void setTransaction_order(Double transaction_order) {
        this.transaction_order = transaction_order;
    }

    public Double getAmount_settled() {
        return amount_settled;
    }

    public void setAmount_settled(Double amount_settled) {
        this.amount_settled = amount_settled;
    }
}
