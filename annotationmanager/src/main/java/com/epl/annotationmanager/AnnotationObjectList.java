package com.epl.annotationmanager;

import java.util.List;

public class AnnotationObjectList {
    private List<AnnotationObject> annotationObjectList;
    private LatLong latLong;

    public List<AnnotationObject> getAnnotationObjectList() {
        return annotationObjectList;
    }

    public void setAnnotationObjectList(List<AnnotationObject> annotationObjectList) {
        this.annotationObjectList = annotationObjectList;
    }

    public void setLatLong(LatLong latLong) {
        this.latLong = latLong;
    }

    public LatLong getLatLong() {
        return latLong;
    }
}
