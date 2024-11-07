package fr.insee.protools.backend.dto.sugoi;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habilitation implements Serializable {
    @Getter(AccessLevel.NONE)
    private String id;
    private String application;
    private String role;
    private String property;

    public Habilitation(String application, String role, String property) {
        this.application = application;
        this.role = role;
        this.property = property;
    }

    public String getId() {
        // raw id if specified, or build id from app role and property
        if (id != null) {
            return id;
        } else if (this.property != null) {
            return this.property + "_" + this.role + "_" + this.application;
        }
        //else
        return this.role + "_" + this.application;
    }
}