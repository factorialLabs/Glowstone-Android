package factoriallabs.com.baconbeacon.menu;

        import android.graphics.Bitmap;
        import android.os.Parcel;
        import android.os.Parcelable;

        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Map;

/**
 * Created by YuChen on 2015-02-26.
 */
public class Menu implements Parcelable {
    public String title;
    public HashMap<String,String> emails;
    public HashMap<String,String> social;
    public HashMap<String,String> phones;
    public String birthday;
    public String website;
    public String first_name;
    public String last_name;
    public String photo_url;
    public Bitmap cached_picture; //caches profile pic here if it is downloaded

    public Menu(){

    }

    // Parcelling part: so the object can be "serialized" and sent to the detail activity
    protected Menu(Parcel in) {
        title = in.readString();
        emails = (HashMap<String, String>) in.readSerializable();
        social = (HashMap<String, String>) in.readSerializable();
        phones = (HashMap<String, String>) in.readSerializable();
        birthday = in.readString();
        website = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        photo_url = in.readString();
        cached_picture = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeSerializable(emails);
        dest.writeSerializable(social);
        dest.writeSerializable(phones);
        dest.writeString(birthday);
        dest.writeString(website);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(photo_url);
        dest.writeValue(cached_picture);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };
}