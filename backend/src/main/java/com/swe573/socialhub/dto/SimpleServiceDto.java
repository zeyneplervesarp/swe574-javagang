package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.LocationType;
import com.swe573.socialhub.enums.ServiceStatus;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SimpleServiceDto implements Serializable {
    private Long id;
    private String header;
    private LocationType locationType;
    private String location;
    private Double latitude;
    private Double longitude;

    public SimpleServiceDto() {
    }

    public SimpleServiceDto(Long id, String header, LocationType locationType, String location, Double latitude, Double longitude) {
        this.id = id;
        this.header = header;
        this.locationType = locationType;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public Long getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

}
