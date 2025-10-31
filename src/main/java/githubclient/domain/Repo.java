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
public class Repo {
    private String name;
    private String description;
    private String language;
    @JsonProperty("stargazers_count") private int stargazersCount;
    @JsonProperty("forks_count") private int forksCount;
    @JsonProperty("updated_at") private String updatedAt;
    private Owner owner;

    public static class Owner { public String login; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLanguage() { return language; }
    public int getStargazersCount() { return stargazersCount; }
    public int getForksCount() { return forksCount; }
    public String getUpdatedAt() { return updatedAt; }
    public String getOwnerLogin() { return owner != null ? owner.login : ""; }
    
}
