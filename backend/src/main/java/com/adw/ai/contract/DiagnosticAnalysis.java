package com.adw.ai.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Structured diagnostic output contract (spec section 7). The LLM must return
 * JSON matching this shape; the response is validated before it is persisted
 * or shown as structured data.
 */
public record DiagnosticAnalysis(
        @NotBlank String summary,
        @Valid VehicleContext vehicleContext,
        @NotNull List<@Valid KnownFact> knownFacts,
        @NotNull List<@Valid MissingInformation> missingInformation,
        @NotNull List<@Valid Dtc> dtcs,
        @NotNull List<@Valid Hypothesis> hypotheses,
        @NotNull List<@Valid TestPlanItem> testPlan,
        @NotNull List<String> safetyWarnings,
        @NotNull List<@Valid Source> sources,
        @NotNull ConclusionStatus conclusionStatus
) {

    public record VehicleContext(
            String vehicleId,
            @NotNull List<String> configurationWarnings
    ) {
    }

    public record KnownFact(
            @NotBlank String statement,
            @NotNull FactOrigin origin,
            String sourceId
    ) {
    }

    public record MissingInformation(
            @NotBlank String question,
            String reason
    ) {
    }

    public record Dtc(
            @NotBlank String code,
            @NotNull DtcStatus status,
            String description,
            @NotNull List<String> sourceIds
    ) {
    }

    public record Hypothesis(
            @NotBlank String id,
            @NotBlank String title,
            @NotNull Confidence confidence,
            @NotNull List<String> supportingEvidence,
            @NotNull List<String> contradictingEvidence,
            @NotNull List<String> unknowns,
            @NotNull HypothesisStatus status
    ) {
    }

    public record TestPlanItem(
            @NotBlank String id,
            @NotBlank String title,
            String purpose,
            @NotNull List<String> tools,
            @NotNull List<String> preconditions,
            @NotNull List<String> procedure,
            @NotNull List<String> expectedResults,
            @NotNull List<String> safetyNotes,
            @NotNull List<String> sourceIds,
            @NotNull TestStatus status
    ) {
    }

    public record Source(
            @NotBlank String id,
            String documentTitle,
            String publisher,
            String page,
            String url,
            String excerpt
    ) {
    }
}
