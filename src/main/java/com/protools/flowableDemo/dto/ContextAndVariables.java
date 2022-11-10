package com.protools.flowableDemo.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.protools.flowableDemo.enums.Context;
import lombok.Data;

import java.util.Collection;

@Data
public class ContextAndVariables {
    private Context context;

    private Collection<Variable> variables;

    public ContextAndVariables() {
    }

    public ContextAndVariables(Context context, Collection<Variable> variables) {
        this.context = context;
        this.variables = variables;
    }

    @JsonGetter(value = "inseeContext")
    public Context getContext() {
        return context;
    }
}
