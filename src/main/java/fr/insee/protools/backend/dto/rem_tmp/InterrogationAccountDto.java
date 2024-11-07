package fr.insee.protools.backend.dto.rem_tmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

import java.util.Objects;
import java.util.UUID;

/**
 * InterrogationAccountDto
 */

@JsonTypeName("InterrogationAccount")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-08-30T10:25:29.966974800+02:00[Europe/Paris]", comments = "Generator version: 7.8.0")
public class InterrogationAccountDto {

    private UUID interrogationId;

    private String account;

    /**
     * Create a builder with no initialized field (except for the default values).
     */
    public static Builder builder() {
        return new Builder();
    }

    public InterrogationAccountDto interrogationId(UUID interrogationId) {
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

    public InterrogationAccountDto account(String account) {
        this.account = account;
        return this;
    }

    /**
     * Get account
     *
     * @return account
     */

    @JsonProperty("account")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InterrogationAccountDto interrogationAccount = (InterrogationAccountDto) o;
        return Objects.equals(this.interrogationId, interrogationAccount.interrogationId) &&
                Objects.equals(this.account, interrogationAccount.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interrogationId, account);
    }

    @Override
    public String toString() {
        String sb = "class InterrogationAccountDto {\n" +
                "    interrogationId: " + toIndentedString(interrogationId) + "\n" +
                "    account: " + toIndentedString(account) + "\n" +
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

        private InterrogationAccountDto instance;

        public Builder() {
            this(new InterrogationAccountDto());
        }

        protected Builder(InterrogationAccountDto instance) {
            this.instance = instance;
        }

        protected Builder copyOf(InterrogationAccountDto value) {
            this.instance.setInterrogationId(value.interrogationId);
            this.instance.setAccount(value.account);
            return this;
        }

        public Builder interrogationId(UUID interrogationId) {
            this.instance.interrogationId(interrogationId);
            return this;
        }

        public Builder account(String account) {
            this.instance.account(account);
            return this;
        }

        /**
         * returns a built InterrogationAccountDto instance.
         * <p>
         * The builder is not reusable (NullPointerException)
         */
        public InterrogationAccountDto build() {
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

