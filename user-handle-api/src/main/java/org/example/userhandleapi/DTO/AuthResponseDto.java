package org.example.userhandleapi.DTO;

public record AuthResponseDto(String token, AuthStatus authStatus,String role){
}
