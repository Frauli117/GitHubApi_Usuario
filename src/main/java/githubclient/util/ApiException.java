/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package githubclient.util;

/**
 *
 * @author Kenneth
 */
public class ApiException extends RuntimeException {
    private final int status;
    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }
    public int getStatus() { return status; }
}
