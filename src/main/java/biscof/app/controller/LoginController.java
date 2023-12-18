package biscof.app.controller;

import biscof.app.security.login.LoginDto;
import biscof.app.security.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${base-url}")
@RequiredArgsConstructor
public class LoginController {

    @Autowired
    private LoginService loginService;

//    @Operation(summary = "Sign in")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "User successfully logged in", content = @Content),
//        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
//        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
//    })
    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody LoginDto request
    ) {
        return ResponseEntity.ok(loginService.login(request));
    }
}
