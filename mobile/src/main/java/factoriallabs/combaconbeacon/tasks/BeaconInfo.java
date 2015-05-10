package factoriallabs.com.baconbeacon.tasks;

/**
 * Created by yuchen.hou on 15-05-09.
 */
public class BeaconInfo {

    public String description;
    public String name;
    public String imgUrl;
    public String extra = "Not in range";
    public String Id;

    public BeaconInfo(String a, String b, String c, String d){
        description = a;
        name = b;
        imgUrl = c;
        Id = d;
    }

}
