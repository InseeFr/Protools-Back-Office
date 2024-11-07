package fr.insee.protools.backend.dto.rem_tmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

import java.util.Objects;
import java.util.UUID;

/**
 * InterrogationIdentifiersDto
 */

@JsonTypeName("InterrogationIdentifiers")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-08-30T10:25:29.966974800+02:00[Europe/Paris]", comments = "Generator version: 7.8.0")
public class InterrogationIdentifiersDto {

    private UUID interrogationId;

    private UUID surveyUnitId;

    private String originId;

    /**
     * Create a builder with no initialized field (except for the default values).
     */
    public static Builder builder() {
        return new Builder();
    }

    public InterrogationIdentifiersDto interrogationId(UUID interrogationId) {
        this.interrogationId = interrogationId;
        return this;
    }

    /**
     * Get interrogationId
     *
     * @return interrogationId
     */
    @Valid
    @JsonProperty("interrogationId")
    public UUID getInterrogationId() {
        return interrogationId;
    }

    public void setInterrogationId(UUID interrogationId) {
        this.interrogationId = interrogationId;
    }

    public InterrogationIdentifiersDto surveyUnitId(UUID surveyUnitId) {
        this.surveyUnitId = surveyUnitId;
        return this;
    }

    /**
     * Get surveyUnitId
     *
     * @return surveyUnitId
     */
    @Valid
    @JsonProperty("surveyUnitId")
    public UUID getSurveyUnitId() {
        return surveyUnitId;
    }

    public void setSurveyUnitId(UUID surveyUnitId) {
        this.surveyUnitId = surveyUnitId;
    }

    public InterrogationIdentifiersDto originId(String originId) {
        this.originId = originId;
        return this;
    }

    /**
     * Get originId
     *
     * @return originId
     */

    @JsonProperty("originId")
    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InterrogationIdentifiersDto interrogationIdentifiers = (InterrogationIdentifiersDto) o;
        return Objects.equals(this.interrogationId, interrogationIdentifiers.interrogationId) &&
                Objects.equals(this.surveyUnitId, interrogationIdentifiers.surveyUnitId) &&
                Objects.equals(this.originId, interrogationIdentifiers.originId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interrogationId, surveyUnitId, originId);
    }

    @Override
    public String toString() {
        String sb = "class InterrogationIdentifiersDto {\n" +
                "    interrogationId: " + toIndentedString(interrogationId) + "\n" +
                "    surveyUnitId: " + toIndentedString(surveyUnitId) + "\n" +
                "    originId: " + toIndentedString(originId) + "\n" +
                "}";
        return sb;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * Create a builder with a shallow copy of this instance.
     */
    public Builder toBuilder() {
        Builder builder = new Builder();
        return builder.copyOf(this);
    }

    public static class Builder {

        private InterrogationIdentifiersDto instance;

        public Builder() {
            this(new InterrogationIdentifiersDto());
        }

        protected Builder(InterrogationIdentifiersDto instance) {
            this.instance = instance;
        }

        protected Builder copyOf(InterrogationIdentifiersDto value) {
            this.instance.setInterrogationId(value.interrogationId);
            this.instance.setSurveyUnitId(value.surveyUnitId);
            this.instance.setOriginId(value.originId);
            return this;
        }

        public Builder interrogationId(UUID interrogationId) {
            this.instance.interrogationId(interrogationId);
            return this;
        }

        public Builder surveyUnitId(UUID surveyUnitId) {
            this.instance.surveyUnitId(surveyUnitId);
            return this;
        }

        public Builder originId(String originId) {
            this.instance.originId(originId);
            return this;
        }

        /**
         * returns a built InterrogationIdentifiersDto instance.
         * <p>
         * The builder is not reusable (NullPointerException)
         */
        public InterrogationIdentifiersDto build() {
            try {
                return this.instance;
            } finally {
                // ensure that this.instance is not reused
                this.instance = null;
            }
        }

        @Override
        public String toString() {
            return getClass() + "=(" + instance + ")";
        }
    }

}

