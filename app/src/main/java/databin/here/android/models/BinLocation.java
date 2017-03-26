/**
 * Created by Eyal on 16/03/2017.
 */

package databin.here.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BinLocation {

    @SerializedName("bin_latitude")
    @Expose
    private Double binLatitude;
    @SerializedName("bin_longitude")
    @Expose
    private Double binLongitude;
    @SerializedName("bin_address")
    @Expose
    private String binAddress;

    public Double getBinLatitude() {
        return binLatitude;
    }

    public void setBinLatitude(Double binLatitude) {
        this.binLatitude = binLatitude;
    }

    public Double getBinLongitude() {
        return binLongitude;
    }

    public void setBinLongitude(Double binLongitude) {
        this.binLongitude = binLongitude;
    }

    public String getBinAddress() {
        return binAddress;
    }

    public void setBinAddress(String binAddress) {
        this.binAddress = binAddress;
    }

}

