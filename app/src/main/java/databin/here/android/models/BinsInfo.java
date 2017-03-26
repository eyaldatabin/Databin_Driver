/**
 * Created by Eyal on 16/03/2017.
 */

package databin.here.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BinsInfo {

    @SerializedName("bin_id")
    @Expose
    private Integer binId;
    @SerializedName("bin_name")
    @Expose
    private String binName;
    @SerializedName("Bin_location")
    @Expose
    private BinLocation binLocation;
    @SerializedName("Last_updated_fill_level_sample")
    @Expose
    private LastUpdatedFillLevelSample lastUpdatedFillLevelSample;
    @SerializedName("predicted_bin_fill_level_at_start_time")
    @Expose
    private Integer predictedBinFillLevelAtStartTime;
    @SerializedName("bin_collection_cause_id")
    @Expose
    private Integer binCollectionCauseId;
    @SerializedName("bin_collection_cause_name")
    @Expose
    private String binCollectionCauseName;

    public Integer getBinId() {
        return binId;
    }

    public void setBinId(Integer binId) {
        this.binId = binId;
    }

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }

    public BinLocation getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(BinLocation binLocation) {
        this.binLocation = binLocation;
    }

    public LastUpdatedFillLevelSample getLastUpdatedFillLevelSample() {
        return lastUpdatedFillLevelSample;
    }

    public void setLastUpdatedFillLevelSample(LastUpdatedFillLevelSample lastUpdatedFillLevelSample) {
        this.lastUpdatedFillLevelSample = lastUpdatedFillLevelSample;
    }

    public Integer getPredictedBinFillLevelAtStartTime() {
        return predictedBinFillLevelAtStartTime;
    }

    public void setPredictedBinFillLevelAtStartTime(Integer predictedBinFillLevelAtStartTime) {
        this.predictedBinFillLevelAtStartTime = predictedBinFillLevelAtStartTime;
    }

    public Integer getBinCollectionCauseId() {
        return binCollectionCauseId;
    }

    public void setBinCollectionCauseId(Integer binCollectionCauseId) {
        this.binCollectionCauseId = binCollectionCauseId;
    }

    public String getBinCollectionCauseName() {
        return binCollectionCauseName;
    }

    public void setBinCollectionCauseName(String binCollectionCauseName) {
        this.binCollectionCauseName = binCollectionCauseName;
    }

}
