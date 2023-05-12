package Model;

import androidx.recyclerview.widget.RecyclerView;

public class Devices {
    private String ID;
    private String deviceName;
    private String Description;

//    public Devices() {
//    }

    public Devices(String id, String name, String description ){
        this.ID = id;
        this.deviceName = name;
        this.Description = description;

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID()
    {
        return ID;
    }


    public void setName(String name) {
        deviceName = name;
    }
    public String getName() {
        return deviceName;
    }


    public void setDescription(String description) {
        Description = description;
    }
    public String getDescription() {
        return Description;
    }


}

