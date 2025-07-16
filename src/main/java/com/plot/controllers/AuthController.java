package com.plot.controllers;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.plot.config.JwtProvider;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.models.User;
import com.plot.repositories.UserRepository;
import com.plot.response.AuthResponse;
import com.plot.services.WalletService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsService customUserDetails;

    @Autowired
    private WalletService walletService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {
        String email = user.getEmail();
        String password = user.getPassword();
        String fullName = user.getFullName();
        String location = user.getLocation(); // Might be coordinates

        // Check if email exists
        if (userRepository.findByEmail(email) != null) {
            throw new UserException("Email is already used with another account");
        }

        // Convert coordinates to readable location (if needed)
        if (location != null && location.matches("^[-+]?\\d+(\\.\\d+)?\\s*,\\s*[-+]?\\d+(\\.\\d+)?$")) {
            try {
                location = getReadableLocationFromCoordinates(location);
            } catch (Exception e) {
                System.err.println("Could not convert coordinates to location: " + e.getMessage());
            }
        }

        // Create and save user
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setBackgroundImage("/upload_cover_photo.png");
        createdUser.setLocation(location);
        User savedUser = userRepository.save(createdUser);

        // Create wallet
        try {
            walletService.createWalletForUser(savedUser);
        } catch (UserException | WalletException e) {
            e.printStackTrace();
        }

        // Authenticate and generate token
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponse(token, true), HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody User user) throws UserException {
        try {
            String username = user.getEmail();
            String password = user.getPassword();
            String location = user.getLocation();
            
            Authentication authentication = authenticate(username, password,location);
            String token = jwtProvider.generateToken(authentication);

            AuthResponse res = new AuthResponse(token, true);
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);

        } catch (BadCredentialsException ex) {
            HashMap<String, String> error = new HashMap<>();
            error.put("message", "Invalid email or password");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    private Authentication authenticate(String username, String password,String location) throws UserException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        
        // Fetch the actual User entity (not just UserDetails)
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UserException("User not found with email: " + username);
        }
        
        // Convert coordinates to readable location (if needed)
        if (location != null && location.matches("^[-+]?\\d+(\\.\\d+)?\\s*,\\s*[-+]?\\d+(\\.\\d+)?$")) {
            try {
                location = getReadableLocationFromCoordinates(location);
            } catch (Exception e) {
                System.err.println("Could not convert coordinates to location: " + e.getMessage());
            }
        }

        // Update location if needed
        if (location != null && !location.isBlank()) {
            user.setLocation(location);
            userRepository.save(user); // Save the updated location
        }
        
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String getReadableLocationFromCoordinates(String coordinates) throws Exception {
        String[] parts = coordinates.split(",");
        double lat = Double.parseDouble(parts[0].trim());
        double lon = Double.parseDouble(parts[1].trim());

        String url = String.format(
            "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=%s&lon=%s", lat, lon);

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JSONObject json = new JSONObject(response.toString());
            JSONObject address = json.getJSONObject("address");

            String city = address.optString("city",
                          address.optString("town",
                          address.optString("village", "")));

            String country = address.optString("country", "");

            return (city + ", " + country).trim();
        }
    }
}
