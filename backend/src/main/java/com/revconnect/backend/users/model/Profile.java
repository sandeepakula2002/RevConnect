package com.revconnect.backend.users.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String bio;
    private String location;
    private String website;

    // Creator / Business fields
    private String category;
    private String contactInfo;
    private String socialLinks;
    private String businessAddress;
    private String businessHours;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getWebsite() {
        return website;
    }

    public String getCategory() {
        return category;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getSocialLinks() {
        return socialLinks;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public User getUser() {
        return user;
    }

    // ===== SETTERS =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public void setUser(User user) {
        this.user = user;
    }
}