/**
 * Created by Eyal on 15/03/2017.
 */

package databin.here.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkPlan {

    @SerializedName("tabletId")
    @Expose
    private Integer tabletId;
    @SerializedName("driverId")
    @Expose
    private Integer driverId;
    @SerializedName("numOfBinsToCollect")
    @Expose
    private Integer numOfBinsToCollect;
    @SerializedName("binsInfo")
    @Expose
    private List<BinsInfo> binsInfo = null;

    public Integer getTabletId() {
        return tabletId;
    }

    public void setTabletId(Integer tabletId) {
        this.tabletId = tabletId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getNumOfBinsToCollect() {
        return numOfBinsToCollect;
    }

    public void setNumOfBinsToCollect(Integer numOfBinsToCollect) {
        this.numOfBinsToCollect = numOfBinsToCollect;
    }

    public List<BinsInfo> getBinsInfo() {
        return binsInfo;
    }

    public void setBinsInfo(List<BinsInfo> binsInfo) {
        this.binsInfo = binsInfo;
    }
}
