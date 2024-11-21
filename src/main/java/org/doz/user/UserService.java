package org.doz.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.doz.config.security.JwtService;
import org.doz.post.PostCommentRepository;
import org.doz.post.PostLikeRepository;
import org.doz.post.PostRepository;
import org.doz.post.PostService;
import org.doz.profile.*;
import org.doz.user.projections.UserProjection;
import org.doz.user.models.User;
import org.doz.user.utils.LoginRequestBody;
import org.doz.user.utils.RegisterRequestBody;
import org.doz.user.utils.UserException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@AllArgsConstructor
public class UserService {
    private final ProfileSocialMediaRepository profileSocialMediaRepository;
    private PostRepository postRepository;
    private PostCommentRepository postCommentRepository;
    private PostLikeRepository postLikeRepository;
    private UserRepository userRepository;
    private ProfileRepository profileRepository;


    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtService jwtService;

    public String register(RegisterRequestBody user) throws NoSuchAlgorithmException {
        userRepository.findByEmail(user.getEmail(), User.class)
                .ifPresent(u -> {
                    throw new UserException.UserAlreadyExistsException();
                });

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] emailHashBytes = digest.digest(user.getEmail()
                                                      .toLowerCase()
                                                      .trim()
                                                      .getBytes(StandardCharsets.UTF_8));
        String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        StringBuilder emailHash = new StringBuilder(2 * emailHashBytes.length);
        for (byte b : emailHashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                emailHash.append('0');
            }
            emailHash.append(hex);
        }

        User newUser = User.builder()
                .avatar("https://0.gravatar.com/avatar/" + emailHash)
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordHash)
                .build();
        userRepository.saveAndFlush(newUser);
        return jwtService.generateToken(newUser.getEmail());
    }

    public String login(LoginRequestBody user) {
        // check if the user exists
        userDetailsService.loadUserByUsername(user.getEmail());
        // userDetails is registered by the provider
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        return jwtService.generateToken(user.getEmail());
    }

    public UserProjection getUserByEmailWithProjection() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(((User) userDetails).getEmail(), UserProjection.class)
                .orElseThrow(UserException.UserNotFoundException::new);
    }

    public User getUserByEmail() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         return userRepository.findByEmail(((User) userDetails).getEmail(), User.class)
                .orElseThrow(UserException.UserNotFoundException::new);
    }

    @Transactional
    public void deleteUser() {
        Integer userId = getUserByEmail().getId();
        postLikeRepository.deleteAllByUserId(userId);
        postCommentRepository.deleteAllByUserId(userId);
        postRepository.deleteAllByUserId(userId);
        profileRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
