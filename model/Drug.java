package model;

public class Drug {
    private int drugId;
    private String name;
    private String form;
    private String dosageInfo;

    public Drug(int drugId, String name, String form, String dosageInfo) {
        this.drugId = drugId;
        this.name = name;
        this.form = form;
        this.dosageInfo = dosageInfo;
    }

    public String getName() {
        return name;
    }

    public String getDosageInfo() {
        return dosageInfo;
    }

    public int getDrugId() {
        return drugId;
    }

    public String getForm() {
        return form;
    }

    @Override
    public String toString() {
        return name + " (" + form + ") - " + dosageInfo;
    }
}
