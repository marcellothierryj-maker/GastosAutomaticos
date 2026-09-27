package com.thierry.gastosautomaticos;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.*;

public class TransactionParser {
    private static final Pattern MONEY = Pattern.compile("R\\$\\s*([0-9]{1,3}(?:\\.[0-9]{3})*|[0-9]+),([0-9]{2})", Pattern.CASE_INSENSITIVE);
    private static final String[] KEYS = {"compra","pagamento","pix","cartão","cartao","débito","debito","transferência","transferencia","saída","saida","cobrança","cobranca"};

    public static Parsed parse(String title, String text) {
        String all = ((title == null ? "" : title) + " " + (text == null ? "" : text)).trim();
        String low = all.toLowerCase(new Locale("pt","BR"));
        boolean looks = false; for (String k : KEYS) if (low.contains(k)) { looks=true; break; }
        if (!looks) return null;
        Matcher m = MONEY.matcher(all); if (!m.find()) return null;
        String major = m.group(1).replace(".","");
        long cents = Long.parseLong(major) * 100L + Long.parseLong(m.group(2));
        String merchant = guessMerchant(title, text);
        String category = guessCategory(low);
        return new Parsed(cents, merchant, category);
    }

    private static String guessMerchant(String title, String text) {
        if (title != null && !title.trim().isEmpty() && !title.equalsIgnoreCase("Notificações")) return clean(title);
        if (text != null) {
            String s = text.replaceAll("(?i)R\\$\\s*[0-9.]+,[0-9]{2}", "").replaceAll("\\s+", " ").trim();
            if (!s.isEmpty()) return clean(s.length() > 60 ? s.substring(0,60) : s);
        }
        return "Movimentação";
    }
    private static String clean(String s) { return s.replace("\n"," ").trim(); }
    private static String guessCategory(String s) {
        if (s.matches(".*(supermercado|mercado|atacad|carrefour|assai|pao de acucar|pão de açúcar|extra).*")) return "Mercado";
        if (s.matches(".*(uber|99|combust|posto|shell|ipiranga|estacionamento).*")) return "Transporte";
        if (s.matches(".*(ifood|restaurante|lanch|burger|pizza|delivery).*")) return "Alimentação";
        if (s.matches(".*(farmacia|farmácia|droga|drogasil).*")) return "Saúde";
        if (s.matches(".*(netflix|spotify|cinema|steam|playstation|prime video).*")) return "Lazer";
        if (s.matches(".*(pix|transferencia|transferência).*")) return "Transferência/Pix";
        return "Outros";
    }
    public static class Parsed { public long cents; public String merchant, category; public Parsed(long c,String m,String cat){cents=c;merchant=m;category=cat;} }
}
