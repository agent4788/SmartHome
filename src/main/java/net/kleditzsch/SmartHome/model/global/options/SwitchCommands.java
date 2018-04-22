package net.kleditzsch.SmartHome.model.global.options;

import com.google.gson.annotations.SerializedName;

public enum SwitchCommands {

    @SerializedName("on")
    on,
    @SerializedName("off")
    off,
    @SerializedName("toggle")
    toggle,
    @SerializedName("updateState")
    updateState,
    @SerializedName("updateSensor")
    updateSensor
}
