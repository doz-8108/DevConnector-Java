package org.doz.profile.projections;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileSocialMediaProjection {
    private String youtube;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;
}
