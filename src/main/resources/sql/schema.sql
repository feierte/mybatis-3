-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100),
    full_name VARCHAR(100),
    department VARCHAR(50),
    position VARCHAR(50),
    status TINYINT DEFAULT 1 COMMENT '1: active, 0: disabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    perm_code VARCHAR(100) NOT NULL UNIQUE,
    perm_name VARCHAR(100) NOT NULL,
    resource_type VARCHAR(30) COMMENT 'API, MENU, BUTTON',
    url_pattern VARCHAR(200) COMMENT '如 /api/users/**',
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT,
    role_id BIGINT,
    assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(50),
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT,
    permission_id BIGINT,
    granted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    granted_by VARCHAR(50),
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
);