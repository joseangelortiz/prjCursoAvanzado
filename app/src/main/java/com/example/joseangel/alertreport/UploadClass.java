package com.example.joseangel.alertreport;

public class UploadClass {
    private String stTitle;
    private String stDescription;
    private String stLocation;
    private String stImageUrl;

    public UploadClass() {
        //Empty constructor needed
    }

    public UploadClass(String title, String description, String location, String imageUrl) {

        if (title.trim().equals("")) {
            title = "Sin Título";
        }

        if (description.trim().equals("")) {
            description = "Sin Descripción";
        }

        if (location.trim().equals("")) {
            location = "Sin Ubicación";
        }

        stTitle = title;
        stDescription = description;
        stLocation = location;
        stImageUrl = imageUrl;

    }


    public String getTitle() {
        return stTitle;
    }

    public void setTitle(String title) {
        stTitle = title;
    }

    public String getDescription() {
        return stDescription;
    }

    public void setDescription(String description) {
        stDescription = description;
    }

    public String getLocation() {
        return stLocation;
    }

    public void setLocation(String location) {
        stLocation = location;
    }

    public String getImageUrl() {
        return stImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        stImageUrl = imageUrl;
    }

}
