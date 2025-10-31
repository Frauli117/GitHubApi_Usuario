/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package githubclient.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Kenneth
 */
public class User {
    
    private String login;
    private String name;
    @JsonProperty("avatar_url") private String avatarUrl;
    private String bio;
    private String location;
    private String blog;
    private int followers;
    private int following;
    @JsonProperty("created_at") private String createdAt;

    public String getLogin() { return login; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getBio() { return bio; }
    public String getLocation() { return location; }
    public String getBlog() { return blog; }
    public int getFollowers() { return followers; }
    public int getFollowing() { return following; }
    public String getCreatedAt() { return createdAt; }  
}
