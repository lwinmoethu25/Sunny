
package com.vicky7230.sunny.retrofitPojo.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clouds {

    @SerializedName("all")
    @Expose
    private Long all;

    public Long getAll() {
        return all;
    }

    public void setAll(Long all) {
        this.all = all;
    }

}
