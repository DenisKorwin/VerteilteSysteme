package org.Weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Diese Annotation sorgt dafür, dass unbekannte JSON-Felder ignoriert werden
// → wichtig, um z.B. mit zukünftigen Erweiterungen des JSON-Formats kompatibel zu bleiben
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    private String city;
    private double tempCurrent;
    private double tempMax;
    private double tempMin;
    private String comment;
    private String timeStamp;
    private int cityId;

    public WeatherData() {}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTempCurrent() {
        return tempCurrent;
    }

    public void setTempCurrent(double tempCurrent) {
        this.tempCurrent = tempCurrent;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "City: " + city +
                ", TempCurrent: " + tempCurrent + "°C" +
                ", TempMax: " + tempMax + "°C" +
                ", TempMin: " + tempMin + "°C" +
                ", Comment: " + comment +
                ", TimeStamp: " + timeStamp +
                ", CityId: " + cityId;
    }
}
