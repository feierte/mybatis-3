package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jie Zhao
 * @date 2025/11/4 21:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role {
  private Long id;
  private String roleCode;
  private String roleName;
  private String description;
  private LocalDateTime createdAt;
  private List<Permission> permissions; // 角色拥有的权限
}
