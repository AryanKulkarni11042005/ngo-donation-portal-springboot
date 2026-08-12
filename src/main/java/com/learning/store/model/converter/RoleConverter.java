package com.learning.store.model.converter;
import com.learning.store.model.Role;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter extends AbstractEnumConverter<Role>{
    public RoleConverter() {
        super(Role.class);
    }
}
