package org.doz.user.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProjection {
    private String name;
    private String avatar;
    private String email;
}
