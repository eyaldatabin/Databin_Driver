/**
 * Created by Eyal on 16/03/2017.
 */
package databin.here.android.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastUpdatedFillLevelSample {

    @SerializedName("fill_level")
    @Expose
    private Integer fillLevel;
    @SerializedName("fill_level_in_precent")
    @Expose
    private Double fillLevelInPrecent;
    @SerializedName("fill_level_measurement_time")
    @Expose
    private String fillLevelMeasurementTime;

    public Integer getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(Integer fillLevel) {
        this.fillLevel = fillLevel;
    }

    public Double getFillLevelInPrecent() {
        return fillLevelInPrecent;
    }

    public void setFillLevelInPrecent(Double fillLevelInPrecent) {
        this.fillLevelInPrecent = fillLevelInPrecent;
    }

    public String getFillLevelMeasurementTime() {
        return fillLevelMeasurementTime;
    }

    public void setFillLevelMeasurementTime(String fillLevelMeasurementTime) {
        this.fillLevelMeasurementTime = fillLevelMeasurementTime;
    }

}
