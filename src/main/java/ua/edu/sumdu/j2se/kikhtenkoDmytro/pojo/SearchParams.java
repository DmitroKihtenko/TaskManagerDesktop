package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

public class SearchParams extends URIParams {
    public SearchParams() {
        setSearchRegex("");
        setFrom(0);
        setAmount(10);
    }

    public String getSearchRegex() {
        Object value = get("searchRegex");
        if(!(value instanceof String)) {
            set("searchRegex", "");
        }
        return get("searchRegex").toString();
    }

    public void setSearchRegex(String regex) {
        set("searchRegex", regex);
    }

    public int getAmount() {
        Object value = get("amount");
        if(!(value instanceof String)) {
            setAmount(1);
        }
        return Integer.parseInt(get("amount").toString());
    }

    public void setAmount(int amount) {
        set("amount", amount);
    }

    public int getFrom() {
        Object value = get("from");
        if(!(value instanceof String)) {
            setAmount(1);
        }
        return Integer.parseInt(get("from").toString());
    }

    public void setFrom(int from) {
        set("from", from);
    }

    public void toNext() {
        setFrom(getFrom() + getAmount());
    }

    public void toPrevious() {
        int from = getFrom() - getAmount();
        if(from < 0) {
            from = 0;
        }
        setFrom(from);
    }
}
