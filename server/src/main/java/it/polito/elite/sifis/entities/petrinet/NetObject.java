package it.polito.elite.sifis.entities.petrinet;


public abstract class NetObject {

    /** Latest id assigned to a net object*/
    public static long LATEST_ID = 0;
    /** Id of this net object*/
    protected String id;
    /** Label of this net object*/
    protected String label = "";

    /** Constructor that assigns the LATEST_ID to this object and increments it by 1*/
    public NetObject() {
        this.id = "" + LATEST_ID;
        LATEST_ID++;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        if (label.equals("")) {
            label = id;
        }
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
