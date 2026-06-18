package com.abhishek.urlshortener.analytics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@Tag(
        name = "Analytics",
        description = "Click analytics for shortened URLs"
)
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService
    ) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{shortCode}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get URL analytics",
            description =
                    "Returns the total number of clicks and recent click information."
    )
    public AnalyticsResponse getAnalytics(
            @PathVariable String shortCode
    ) {
        return analyticsService.getAnalytics(shortCode);
    }
}