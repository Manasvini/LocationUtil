package com.epl.annotationmanager;

public class AnnotationObject {
    private LatLong latLong;
    private String annotationName;
    private int annotationValueType;
    private String annotationValue;
    private String annotationType;

    public LatLong getLatLong() {
        return latLong;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public int getAnnotationValueType() {
        return annotationValueType;
    }

    public String getAnnotationValue() {
        return annotationValue;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public void setAnnotationValue(String annotationValue) {
        this.annotationValue = annotationValue;
    }

    public void setAnnotationValueType(int annotationValueType) {
        this.annotationValueType = annotationValueType;
    }

    public void setLatLong(LatLong latLong) {
        this.latLong = latLong;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }
}
