package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author Jie Zhao
 * @date 2025/11/4 21:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Permission {
  private Long id;
  private String permCode;
  private String permName;
  private String resourceType;
  private String urlPattern;
  private String description;
  private LocalDateTime createdAt;
}
