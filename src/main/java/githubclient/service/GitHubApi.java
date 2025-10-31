/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package githubclient.service;

import githubclient.domain.Repo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import githubclient.util.ApiException;
import githubclient.domain.User;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
/**
 *
 * @author Kenneth
 */
public class GitHubApi {
    private static final String API = "https://api.github.com";

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    private HttpRequest.Builder baseRequest(URI uri) { 
        return HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20)).header("Accept", "application/vnd.github+json").header("X-GitHub-Api-Version", "2022-11-28");
    }


    public User getUser(String username) {
        URI uri = URI.create(API + "/users/" + username);
        HttpRequest req = baseRequest(uri).GET().build();
        HttpResponse<String> resp = send(req);
        if (resp.statusCode() == 200) {
            try { return mapper.readValue(resp.body(), User.class); }
            catch (IOException e) { 
                throw new ApiException(500, "No se pudo parsear el usuario"); }
            }
        handleStatus(resp);
        throw new ApiException(resp.statusCode(), "Error inesperado usuario");
    }


    public List<Repo> getRepos(String username) {
        URI uri = URI.create(API + "/users/" + username + "/repos?per_page=100&sort=updated");
        HttpRequest req = baseRequest(uri).GET().build();
        HttpResponse<String> resp = send(req);
        if (resp.statusCode() == 200) {
        try {
            
            List<Repo> repos = mapper.readValue(resp.body(), new TypeReference<>(){});
            repos.sort(Comparator.comparing(Repo::getUpdatedAt).reversed());
            System.out.println("DEBUG repos status=" + resp.statusCode());
            System.out.println("DEBUG parsed repos=" + repos.size());

            return repos;
            } catch (IOException e) {
                throw new ApiException(500, "No se pudo parsear la lista de repos");
            }
        }
        handleStatus(resp);
        throw new ApiException(resp.statusCode(), "Error inesperado repos");
    }


    public Map<String, Integer> getRepoLanguages(String owner, String repo) {
        URI uri = URI.create(API + "/repos/" + owner + "/" + repo + "/languages");
        HttpRequest req = baseRequest(uri).GET().build();
        HttpResponse<String> resp = send(req);
        if (resp.statusCode() == 200) {
            try { return mapper.readValue(resp.body(), new TypeReference<>(){}); }
            catch (IOException e) { 
                throw new ApiException(500, "No se pudo parsear lenguajes del repo"); 
            }
        }
        handleStatus(resp);
        throw new ApiException(resp.statusCode(), "Error inesperado lenguajes");
        }


    private HttpResponse<String> send(HttpRequest req) {
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("DEBUG " + req.method() + " " + req.uri() + " -> status=" + resp.statusCode());
            return resp;
        } catch (IOException | InterruptedException e) {
            throw new ApiException(503, "Falla de red/timeout: " + e.getMessage());
        }
    }

    private void handleStatus(HttpResponse<?> resp) {
        int code = resp.statusCode();
        if (code == 404) throw new ApiException(404, "No encontrado (verifica el nombre)");
        if (code == 403) {
            
            String remaining = optHeader(resp, "X-RateLimit-Remaining");
            String reset = optHeader(resp, "X-RateLimit-Reset");
            String extra = (remaining != null || reset != null) ? String.format(" (remaining=%s, resetEpoch=%s)", remaining == null ? "?" : remaining,reset == null ? "?" : reset): "";
            
            throw new ApiException(403, "Límite de peticiones alcanzado" + extra);
        }
        if (code >= 400 && code < 500) throw new ApiException(code, "Error del cliente: " + code);
        if (code >= 500) throw new ApiException(code, "Error del servidor GitHub: " + code);
    }
    

    private String optHeader(HttpResponse<?> resp, String name) {
        Optional<String> v = resp.headers().firstValue(name);
        return v.orElse(null);
    }
}
