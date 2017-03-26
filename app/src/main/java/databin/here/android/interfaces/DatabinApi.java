package databin.here.android.interfaces;

import databin.here.android.models.WorkPlan;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Eyal on 16/03/2017.
 */

public interface DatabinApi {
    @Headers("Authorization: Basic eWFscm9uQGdtYWlsLmNvbTpRM3cxZTByOHQ4eTE=")
    @GET("driverNavigationInfo/{SimId}")
    Call<WorkPlan> getWorkPlan(@Path("SimId") String SimId);
}
