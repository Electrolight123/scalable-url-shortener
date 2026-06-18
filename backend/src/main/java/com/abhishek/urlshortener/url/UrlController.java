package com.abhishek.urlshortener.url;

import com.abhishek.urlshortener.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(
        name = "URLs",
        description = "URL creation, retrieval, deletion, and redirection APIs"
)
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/urls")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create a shortened URL",
            description = "Creates a new shortened URL for the authenticated user."
    )
    public UrlResponse createUrl(
            @Valid @RequestBody CreateUrlRequest request,
            @AuthenticationPrincipal User user
    ) {
        return urlService.createUrl(request, user);
    }

    @GetMapping("/api/urls/my")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get the current user's URLs",
            description = "Returns all shortened URLs created by the authenticated user."
    )
    public List<UrlResponse> myUrls(
            @AuthenticationPrincipal User user
    ) {
        return urlService.myUrls(user);
    }

    @DeleteMapping("/api/urls/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Deactivate a shortened URL",
            description = "Deactivates a URL owned by the authenticated user."
    )
    public void deleteUrl(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        urlService.deleteUrl(id, user);
    }

    @GetMapping("/{shortCode}")
    @Operation(
            summary = "Redirect using a short code",
            description = "Redirects the client to the original URL."
    )
    public void redirect(
            @PathVariable String shortCode,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String originalUrl =
                urlService.resolveOriginalUrl(shortCode, request);

        response.sendRedirect(originalUrl);
    }

    @GetMapping("/api/health")
    @Operation(
            summary = "Health check",
            description = "Checks whether the URL shortener service is running."
    )
    public String health() {
        return "OK";
    }
}