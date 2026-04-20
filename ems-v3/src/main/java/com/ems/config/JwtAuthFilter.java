package com.ems.config;

import com.ems.entity.RefreshToken;
import com.ems.service.RefreshTokenService;
import com.ems.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter — per-request authentication with transparent token refresh.
 *
 * Decision tree on every request:
 *  1. ems_jwt valid          -> authenticate, done
 *  2. ems_jwt expired/missing + ems_refresh valid in DB
 *                            -> rotate refresh token, issue new pair, authenticate transparently
 *  3. Both invalid/missing   -> clear cookies, SecurityConfig redirects to /login?expired
 */
@Component @RequiredArgsConstructor @Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil               jwt;
    private final UserDetailsServiceImpl uds;
    private final RefreshTokenService   refreshSvc;

    @Value("${jwt.cookie-name}")           private String accessCookieName;
    @Value("${jwt.refresh-cookie-name}")   private String refreshCookieName;
    @Value("${jwt.expiration-ms}")         private long   accessExpiryMs;
    @Value("${jwt.refresh-expiration-ms}") private long   refreshExpiryMs;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String accessToken  = cookieValue(req, accessCookieName);
        String refreshToken = cookieValue(req, refreshCookieName);

        // Step 1 — valid access token
        if (accessToken != null && jwt.isValid(accessToken)) {
            authenticate(accessToken, req);
            chain.doFilter(req, res);
            return;
        }

        // Step 2 — try refresh
        if (refreshToken != null) {
            tryRefresh(accessToken, refreshToken, req, res);
        } else if (accessToken != null) {
            clearCookie(res, accessCookieName); // stale access cookie, no refresh — wipe it
        }

        chain.doFilter(req, res);
    }

    // ── Refresh logic ─────────────────────────────────────────────────────────

    private void tryRefresh(String expiredAccess, String refreshValue,
                            HttpServletRequest req, HttpServletResponse res) {
        try {
            RefreshToken stored = refreshSvc.verifyToken(refreshValue);
            String username = stored.getUser().getUsername();
            String role     = stored.getUser().getRole().name();

            // Cross-check: if an expired access token exists, its username must match
            if (expiredAccess != null && jwt.isExpiredButOtherwiseValid(expiredAccess)) {
                Claims c = jwt.extractExpiredClaims(expiredAccess);
                if (!username.equals(c.getSubject())) {
                    log.warn("Token mismatch — possible substitution attack: jwt={} refresh={}", c.getSubject(), username);
                    clearBothCookies(res);
                    return;
                }
            }

            // Rotate: invalidate old UUID, create new one
            RefreshToken newRefresh = refreshSvc.rotateToken(stored);
            String newAccess = jwt.generateToken(username, role);

            // Write fresh cookies
            setAccessCookie(res, newAccess);
            setRefreshCookie(res, newRefresh.getToken());

            // Authenticate this request with the new token
            authenticate(newAccess, req);
            log.info("Tokens rotated transparently for user: {}", username);

        } catch (RefreshTokenService.TokenExpiredException |
                 RefreshTokenService.TokenNotFoundException e) {
            log.debug("Refresh failed ({}), clearing cookies", e.getMessage());
            clearBothCookies(res);
        }
    }

    private void authenticate(String token, HttpServletRequest req) {
        String username = jwt.extractUsername(token);
        UserDetails ud  = uds.loadUserByUsername(username);
        var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ── Cookie helpers (also called by AuthController) ────────────────────────

    public void setAccessCookie(HttpServletResponse res, String token) {
        Cookie c = new Cookie(accessCookieName, token);
        c.setHttpOnly(true); c.setPath("/");
        c.setMaxAge((int)(accessExpiryMs / 1000));
        res.addCookie(c);
    }

    public void setRefreshCookie(HttpServletResponse res, String token) {
        Cookie c = new Cookie(refreshCookieName, token);
        c.setHttpOnly(true); c.setPath("/");
        c.setMaxAge((int)(refreshExpiryMs / 1000));
        res.addCookie(c);
    }

    public void clearBothCookies(HttpServletResponse res) {
        clearCookie(res, accessCookieName);
        clearCookie(res, refreshCookieName);
    }

    private void clearCookie(HttpServletResponse res, String name) {
        Cookie c = new Cookie(name, "");
        c.setHttpOnly(true); c.setPath("/"); c.setMaxAge(0);
        res.addCookie(c);
    }

    private String cookieValue(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies())
            if (name.equals(c.getName())) return c.getValue();
        return null;
    }
}
