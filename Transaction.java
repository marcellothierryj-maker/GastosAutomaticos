package com.thierry.gastosautomaticos;

public class Transaction {
    public long id;
    public long amountCents;
    public String merchant;
    public String category;
    public long timestamp;
    public String source;
    public boolean automatic;

    public Transaction(long id, long amountCents, String merchant, String category, long timestamp, String source, boolean automatic) {
        this.id = id; this.amountCents = amountCents; this.merchant = merchant; this.category = category;
        this.timestamp = timestamp; this.source = source; this.automatic = automatic;
    }
}
