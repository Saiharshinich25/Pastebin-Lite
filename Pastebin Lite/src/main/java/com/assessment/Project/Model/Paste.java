package com.assessment.Project.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paste {
    private String id;
    private String content;
    private Long expiresAt;     // epoch millis, null = no TTL
    private Integer maxViews;   // null = unlimited
    private Integer views;
}
