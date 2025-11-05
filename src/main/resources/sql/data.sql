

INSERT INTO user (username, email, full_name, status) VALUES
                                                          ('alice_admin', 'alice@company.com', 'Alice Johnson', 1),
                                                          ('bob_editor', 'bob@company.com', 'Bob Smith', 1),
                                                          ('charlie_viewer', 'charlie@company.com', 'Charlie Brown', 1),
                                                          ('diana_hr', 'diana@company.com', 'Diana Prince', 1),
                                                          ('eric_dev', 'eric@company.com', 'Eric Chen', 1),
                                                          ('fiona_qa', 'fiona@company.com', 'Fiona Li', 1),
                                                          ('george_pm', 'george@company.com', 'George Wang', 1),
                                                          ('helen_support', 'helen@company.com', 'Helen Zhao', 0), -- 已禁用
                                                          ('ivan_audit', 'ivan@company.com', 'Ivan Liu', 1),
                                                          ('judy_backup', 'judy@company.com', 'Judy Sun', 1);


INSERT INTO role (role_code, role_name, description) VALUES
                                                         ('ROLE_ADMIN', '系统管理员', '拥有全部权限'),
                                                         ('ROLE_EDITOR', '内容编辑', '可编辑文章、上传资源'),
                                                         ('ROLE_VIEWER', '只读用户', '仅查看数据'),
                                                         ('ROLE_HR', '人力资源', '管理员工信息'),
                                                         ('ROLE_DEV', '开发人员', '访问开发接口和日志');

INSERT INTO permission (perm_code, perm_name, resource_type, url_pattern, description) VALUES
                                                                                           ('USER_VIEW', '查看用户', 'MENU', '/users', '用户列表页面'),
                                                                                           ('USER_EDIT', '编辑用户', 'BUTTON', '/api/users/*', '修改用户信息'),
                                                                                           ('USER_DELETE', '删除用户', 'BUTTON', '/api/users/delete', '删除用户'),
                                                                                           ('ARTICLE_VIEW', '查看文章', 'MENU', '/articles', '文章列表'),
                                                                                           ('ARTICLE_CREATE', '创建文章', 'BUTTON', '/api/articles', '发布新文章'),
                                                                                           ('ARTICLE_PUBLISH', '发布文章', 'BUTTON', '/api/articles/*/publish', '审核并发布'),
                                                                                           ('REPORT_VIEW', '查看报表', 'MENU', '/reports', '数据报表'),
                                                                                           ('HR_EMPLOYEE_MANAGE', '管理员工', 'MENU', '/hr/employees', 'HR员工管理'),
                                                                                           ('HR_SALARY_VIEW', '查看薪资', 'BUTTON', '/api/hr/salary', '仅HR可见'),
                                                                                           ('DEV_LOG_VIEW', '查看系统日志', 'MENU', '/logs', '开发调试用'),
                                                                                           ('DEV_API_DEBUG', '调用调试接口', 'API', '/api/debug/**', '开发专用'),
                                                                                           ('AUDIT_TRAIL_VIEW', '审计日志', 'MENU', '/audit', '安全审计'),
                                                                                           ('BACKUP_TRIGGER', '触发备份', 'BUTTON', '/api/system/backup', '系统备份'),
                                                                                           ('SETTING_READ', '查看系统设置', 'MENU', '/settings', '只读设置'),
                                                                                           ('SETTING_WRITE', '修改系统设置', 'BUTTON', '/api/settings', '修改全局配置');

-- 用户-角色分配（模拟真实场景）
-- 管理员：Alice
INSERT INTO user_role (user_id, role_id) VALUES
    (1, 1); -- alice → admin

-- 编辑：Bob
INSERT INTO user_role (user_id, role_id) VALUES
    (2, 2); -- bob → editor

-- 查看者：Charlie
INSERT INTO user_role (user_id, role_id) VALUES
    (3, 3); -- charlie → viewer

-- HR：Diana
INSERT INTO user_role (user_id, role_id) VALUES
    (4, 4); -- diana → hr

-- 开发：Eric
INSERT INTO user_role (user_id, role_id) VALUES
    (5, 5); -- eric → dev

-- Fiona（QA）拥有 viewer + editor
INSERT INTO user_role (user_id, role_id) VALUES
                                             (6, 2), (6, 3);

-- George（PM）拥有 editor + viewer + hr（临时）
INSERT INTO user_role (user_id, role_id) VALUES
                                             (7, 2), (7, 3), (7, 4);

-- Ivan（审计）拥有 viewer + audit 权限（通过自定义角色，这里我们给 admin 角色加 audit 权限）
-- 先给 admin 角色添加 audit 权限（见下文）

-- Judy（备份员）拥有 viewer + backup 权限（通过 admin 角色或单独权限）
INSERT INTO user_role (user_id, role_id) VALUES
    (10, 1); -- 直接给 admin（简化），实际可建 ROLE_BACKUP

-- 角色-权限分配
-- Admin 拥有所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission;

-- Editor：文章相关 + 用户查看
INSERT INTO role_permission (role_id, permission_id) VALUES
                                                         (2, (SELECT id FROM permission WHERE perm_code = 'ARTICLE_VIEW')),
                                                         (2, (SELECT id FROM permission WHERE perm_code = 'ARTICLE_CREATE')),
                                                         (2, (SELECT id FROM permission WHERE perm_code = 'ARTICLE_PUBLISH')),
                                                         (2, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'));

-- Viewer：只读
INSERT INTO role_permission (role_id, permission_id) VALUES
                                                         (3, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW')),
                                                         (3, (SELECT id FROM permission WHERE perm_code = 'ARTICLE_VIEW')),
                                                         (3, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW')),
                                                         (3, (SELECT id FROM permission WHERE perm_code = 'SETTING_READ'));

-- HR
INSERT INTO role_permission (role_id, permission_id) VALUES
                                                         (4, (SELECT id FROM permission WHERE perm_code = 'HR_EMPLOYEE_MANAGE')),
                                                         (4, (SELECT id FROM permission WHERE perm_code = 'HR_SALARY_VIEW')),
                                                         (4, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'));

-- Dev
INSERT INTO role_permission (role_id, permission_id) VALUES
                                                         (5, (SELECT id FROM permission WHERE perm_code = 'DEV_LOG_VIEW')),
                                                         (5, (SELECT id FROM permission WHERE perm_code = 'DEV_API_DEBUG')),
                                                         (5, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'));

-- 插入100个用户
INSERT INTO user (username, email, full_name, department, position, status) VALUES
                                                                                ('user001', 'user001@company.com', 'Alice Johnson', 'IT', 'Developer', 1),
                                                                                ('user002', 'user002@company.com', 'Bob Smith', 'HR', 'Manager', 1),
                                                                                ('user003', 'user003@company.com', 'Charlie Brown', 'Finance', 'Analyst', 1),
                                                                                ('user004', 'user004@company.com', 'Diana Prince', 'IT', 'DevOps Engineer', 1),
                                                                                ('user005', 'user005@company.com', 'Eric Chen', 'IT', 'Developer', 1),
                                                                                ('user006', 'user006@company.com', 'Fiona Li', 'QA', 'Tester', 1),
                                                                                ('user007', 'user007@company.com', 'George Wang', 'Project', 'Project Manager', 1),
                                                                                ('user008', 'user008@company.com', 'Helen Zhao', 'HR', 'HR Specialist', 0),
                                                                                ('user009', 'user009@company.com', 'Ivan Liu', 'Security', 'Security Analyst', 1),
                                                                                ('user010', 'user010@company.com', 'Judy Sun', 'IT', 'System Admin', 1),
                                                                                ('user011', 'user011@company.com', 'Kevin Zhang', 'Finance', 'Accountant', 1),
                                                                                ('user012', 'user012@company.com', 'Linda Wu', 'Marketing', 'Marketing Manager', 1),
                                                                                ('user013', 'user013@company.com', 'Mike Chen', 'IT', 'Senior Developer', 1),
                                                                                ('user014', 'user014@company.com', 'Nancy Wang', 'HR', 'HR Manager', 1),
                                                                                ('user015', 'user015@company.com', 'Oscar Liu', 'IT', 'Architect', 1),
                                                                                ('user016', 'user016@company.com', 'Paul Zhang', 'Finance', 'Finance Manager', 1),
                                                                                ('user017', 'user017@company.com', 'Queenie Wu', 'Marketing', 'Marketing Specialist', 1),
                                                                                ('user018', 'user018@company.com', 'Robert Chen', 'IT', 'Database Admin', 1),
                                                                                ('user019', 'user019@company.com', 'Sandy Wang', 'QA', 'QA Lead', 1),
                                                                                ('user020', 'user020@company.com', 'Tom Liu', 'Project', 'Scrum Master', 1),
                                                                                ('user021', 'user021@company.com', 'Uma Zhang', 'IT', 'Frontend Developer', 1),
                                                                                ('user022', 'user022@company.com', 'Victor Wu', 'IT', 'Backend Developer', 1),
                                                                                ('user023', 'user023@company.com', 'Wendy Chen', 'HR', 'Recruiter', 1),
                                                                                ('user024', 'user024@company.com', 'Xavier Wang', 'IT', 'DevOps Engineer', 1),
                                                                                ('user025', 'user025@company.com', 'Yvonne Liu', 'Finance', 'Auditor', 1),
                                                                                ('user026', 'user026@company.com', 'Zack Zhang', 'IT', 'Security Engineer', 1),
                                                                                ('user027', 'user027@company.com', 'Amy Wu', 'Marketing', 'Content Writer', 1),
                                                                                ('user028', 'user028@company.com', 'Ben Chen', 'Project', 'Business Analyst', 1),
                                                                                ('user029', 'user029@company.com', 'Cathy Wang', 'IT', 'UI/UX Designer', 1),
                                                                                ('user030', 'user030@company.com', 'David Liu', 'Finance', 'Financial Analyst', 1),
                                                                                ('user031', 'user031@company.com', 'Eva Zhang', 'HR', 'HR Generalist', 1),
                                                                                ('user032', 'user032@company.com', 'Frank Wu', 'IT', 'Network Engineer', 1),
                                                                                ('user033', 'user033@company.com', 'Grace Chen', 'QA', 'Automation Engineer', 1),
                                                                                ('user034', 'user034@company.com', 'Henry Wang', 'Project', 'Product Owner', 1),
                                                                                ('user035', 'user035@company.com', 'Iris Liu', 'IT', 'Full Stack Developer', 1),
                                                                                ('user036', 'user036@company.com', 'Jack Zhang', 'Marketing', 'Social Media Manager', 1),
                                                                                ('user037', 'user037@company.com', 'Kate Wu', 'Finance', 'Tax Specialist', 1),
                                                                                ('user038', 'user038@company.com', 'Leo Chen', 'IT', 'Mobile Developer', 1),
                                                                                ('user039', 'user039@company.com', 'Mia Wang', 'HR', 'Training Specialist', 1),
                                                                                ('user040', 'user040@company.com', 'Nick Liu', 'Project', 'Project Coordinator', 1),
                                                                                ('user041', 'user041@company.com', 'Olivia Zhang', 'IT', 'Data Scientist', 1),
                                                                                ('user042', 'user042@company.com', 'Peter Wu', 'Finance', 'Investment Analyst', 1),
                                                                                ('user043', 'user043@company.com', 'Quincy Chen', 'IT', 'Cloud Engineer', 1),
                                                                                ('user044', 'user044@company.com', 'Rita Wang', 'Marketing', 'SEO Specialist', 1),
                                                                                ('user045', 'user045@company.com', 'Steve Liu', 'IT', 'System Architect', 1),
                                                                                ('user046', 'user046@company.com', 'Tina Zhang', 'HR', 'Compensation Analyst', 1),
                                                                                ('user047', 'user047@company.com', 'Ulysses Wu', 'IT', 'API Developer', 1),
                                                                                ('user048', 'user048@company.com', 'Vera Chen', 'Finance', 'Budget Analyst', 1),
                                                                                ('user049', 'user049@company.com', 'William Wang', 'Project', 'Technical Lead', 1),
                                                                                ('user050', 'user050@company.com', 'Xena Liu', 'Marketing', 'Brand Manager', 1),
                                                                                ('user051', 'user051@company.com', 'Yuki Zhang', 'IT', 'AI Engineer', 1),
                                                                                ('user052', 'user052@company.com', 'Zoe Wu', 'QA', 'Performance Tester', 1),
                                                                                ('user053', 'user053@company.com', 'Alan Chen', 'Finance', 'Risk Analyst', 1),
                                                                                ('user054', 'user054@company.com', 'Bella Wang', 'HR', 'Benefits Specialist', 1),
                                                                                ('user055', 'user055@company.com', 'Chris Liu', 'IT', 'Blockchain Developer', 1),
                                                                                ('user056', 'user056@company.com', 'Daisy Zhang', 'Marketing', 'Digital Marketing Manager', 1),
                                                                                ('user057', 'user057@company.com', 'Evan Wu', 'Project', 'Agile Coach', 1),
                                                                                ('user058', 'user058@company.com', 'Felix Chen', 'IT', 'Embedded Systems Engineer', 1),
                                                                                ('user059', 'user059@company.com', 'Grace Wang', 'Finance', 'Credit Analyst', 1),
                                                                                ('user060', 'user060@company.com', 'Hugo Liu', 'HR', 'Organizational Development', 1),
                                                                                ('user061', 'user061@company.com', 'Ivy Zhang', 'IT', 'DevSecOps Engineer', 1),
                                                                                ('user062', 'user062@company.com', 'Jacky Wu', 'Marketing', 'Content Marketing Manager', 1),
                                                                                ('user063', 'user063@company.com', 'Kathy Chen', 'Project', 'Change Manager', 1),
                                                                                ('user064', 'user064@company.com', 'Leo Wang', 'IT', 'IoT Engineer', 1),
                                                                                ('user065', 'user065@company.com', 'Maggie Liu', 'Finance', 'Compliance Officer', 1),
                                                                                ('user066', 'user066@company.com', 'Nelson Zhang', 'HR', 'Talent Acquisition', 1),
                                                                                ('user067', 'user067@company.com', 'Olive Wu', 'IT', 'Game Developer', 1),
                                                                                ('user068', 'user068@company.com', 'Perry Chen', 'Marketing', 'Marketing Analyst', 1),
                                                                                ('user069', 'user069@company.com', 'Queenie Wang', 'Project', 'Program Manager', 1),
                                                                                ('user070', 'user070@company.com', 'Rex Liu', 'IT', 'Robotics Engineer', 1),
                                                                                ('user071', 'user071@company.com', 'Sue Zhang', 'Finance', 'Forensic Accountant', 1),
                                                                                ('user072', 'user072@company.com', 'Tommy Wu', 'HR', 'HRIS Specialist', 1),
                                                                                ('user073', 'user073@company.com', 'Uma Chen', 'IT', 'AR/VR Developer', 1),
                                                                                ('user074', 'user074@company.com', 'Vicky Wang', 'Marketing', 'Public Relations Manager', 1),
                                                                                ('user075', 'user075@company.com', 'Winston Liu', 'Project', 'Portfolio Manager', 1),
                                                                                ('user076', 'user076@company.com', 'Xander Zhang', 'IT', 'Cybersecurity Specialist', 1),
                                                                                ('user077', 'user077@company.com', 'Yara Wu', 'Finance', 'Treasury Analyst', 1),
                                                                                ('user078', 'user078@company.com', 'Zack Chen', 'HR', 'Workforce Planning', 1),
                                                                                ('user079', 'user079@company.com', 'Amanda Wang', 'IT', 'Solutions Architect', 1),
                                                                                ('user080', 'user080@company.com', 'Brian Liu', 'Marketing', 'Event Marketing Manager', 1),
                                                                                ('user081', 'user081@company.com', 'Cindy Zhang', 'Project', 'Release Manager', 1),
                                                                                ('user082', 'user082@company.com', 'Derek Wu', 'IT', 'Machine Learning Engineer', 1),
                                                                                ('user083', 'user083@company.com', 'Elena Chen', 'Finance', 'Internal Auditor', 1),
                                                                                ('user084', 'user084@company.com', 'Frank Wang', 'HR', 'Employee Relations', 1),
                                                                                ('user085', 'user085@company.com', 'Grace Liu', 'IT', 'Big Data Engineer', 1),
                                                                                ('user086', 'user086@company.com', 'Henry Zhang', 'Marketing', 'Marketing Communications', 1),
                                                                                ('user087', 'user087@company.com', 'Iris Wu', 'Project', 'Process Improvement', 1),
                                                                                ('user088', 'user088@company.com', 'Jack Chen', 'IT', 'Microservices Developer', 1),
                                                                                ('user089', 'user089@company.com', 'Kathy Wang', 'Finance', 'Tax Analyst', 1),
                                                                                ('user090', 'user090@company.com', 'Liam Liu', 'HR', 'Diversity & Inclusion', 1),
                                                                                ('user091', 'user091@company.com', 'Mia Zhang', 'IT', 'API Gateway Specialist', 1),
                                                                                ('user092', 'user092@company.com', 'Nora Wu', 'Marketing', 'Customer Marketing Manager', 1),
                                                                                ('user093', 'user093@company.com', 'Oscar Chen', 'Project', 'Agile Project Manager', 1),
                                                                                ('user094', 'user094@company.com', 'Paula Wang', 'IT', 'Test Automation Engineer', 1),
                                                                                ('user095', 'user095@company.com', 'Quincy Liu', 'Finance', 'Financial Planning', 1),
                                                                                ('user096', 'user096@company.com', 'Rita Zhang', 'HR', 'HR Analytics', 1),
                                                                                ('user097', 'user097@company.com', 'Steve Wu', 'IT', 'Database Developer', 1),
                                                                                ('user098', 'user098@company.com', 'Tina Chen', 'Marketing', 'Growth Marketing Manager', 1),
                                                                                ('user099', 'user099@company.com', 'Uma Wang', 'Project', 'Stakeholder Manager', 1),
                                                                                ('user100', 'user100@company.com', 'Victor Liu', 'IT', 'Enterprise Architect', 1);
INSERT INTO role (role_code, role_name, description) VALUES
                                                         ('ROLE_ADMIN', '超级管理员', '拥有系统所有权限'),
                                                         ('ROLE_IT_MANAGER', 'IT经理', 'IT部门管理权限'),
                                                         ('ROLE_DEVELOPER', '开发工程师', '代码开发和部署权限'),
                                                         ('ROLE_QA_MANAGER', 'QA经理', '测试团队管理权限'),
                                                         ('ROLE_TESTER', '测试工程师', '功能测试和自动化测试权限'),
                                                         ('ROLE_PROJECT_MANAGER', '项目经理', '项目管理权限'),
                                                         ('ROLE_HR_MANAGER', 'HR经理', '人力资源管理权限'),
                                                         ('ROLE_HR_SPECIALIST', 'HR专员', '员工信息管理权限'),
                                                         ('ROLE_FINANCE_MANAGER', '财务经理', '财务管理权限'),
                                                         ('ROLE_ACCOUNTANT', '会计', '账务处理权限'),
                                                         ('ROLE_MARKETING_MANAGER', '市场经理', '市场活动管理权限'),
                                                         ('ROLE_MARKETING_SPECIALIST', '市场专员', '市场推广权限'),
                                                         ('ROLE_DEVOPS_ENGINEER', 'DevOps工程师', '系统运维和部署权限'),
                                                         ('ROLE_SECURITY_ANALYST', '安全分析师', '安全监控和审计权限'),
                                                         ('ROLE_SYS_ADMIN', '系统管理员', '系统配置和维护权限'),
                                                         ('ROLE_DATA_SCIENTIST', '数据科学家', '数据分析和挖掘权限'),
                                                         ('ROLE_BUSINESS_ANALYST', '业务分析师', '业务流程分析权限'),
                                                         ('ROLE_UI_UX_DESIGNER', 'UI/UX设计师', '界面设计权限'),
                                                         ('ROLE_PRODUCT_OWNER', '产品负责人', '产品管理权限'),
                                                         ('ROLE_SCRUM_MASTER', 'Scrum Master', '敏捷项目管理权限');

INSERT INTO permission (perm_code, perm_name, resource_type, url_pattern, description) VALUES
                                                                                           ('USER_VIEW', '查看用户', 'MENU', '/users', '用户列表页面'),
                                                                                           ('USER_EDIT', '编辑用户', 'BUTTON', '/api/users/*', '修改用户信息'),
                                                                                           ('USER_DELETE', '删除用户', 'BUTTON', '/api/users/delete', '删除用户'),
                                                                                           ('ARTICLE_VIEW', '查看文章', 'MENU', '/articles', '文章列表'),
                                                                                           ('ARTICLE_CREATE', '创建文章', 'BUTTON', '/api/articles', '发布新文章'),
                                                                                           ('ARTICLE_PUBLISH', '发布文章', 'BUTTON', '/api/articles/*/publish', '审核并发布'),
                                                                                           ('REPORT_VIEW', '查看报表', 'MENU', '/reports', '数据报表'),
                                                                                           ('HR_EMPLOYEE_MANAGE', '管理员工', 'MENU', '/hr/employees', 'HR员工管理'),
                                                                                           ('HR_SALARY_VIEW', '查看薪资', 'BUTTON', '/api/hr/salary', '仅HR可见'),
                                                                                           ('DEV_LOG_VIEW', '查看系统日志', 'MENU', '/logs', '开发调试用'),
                                                                                           ('DEV_API_DEBUG', '调用调试接口', 'API', '/api/debug/**', '开发专用'),
                                                                                           ('AUDIT_TRAIL_VIEW', '审计日志', 'MENU', '/audit', '安全审计'),
                                                                                           ('BACKUP_TRIGGER', '触发备份', 'BUTTON', '/api/system/backup', '系统备份'),
                                                                                           ('SETTING_READ', '查看系统设置', 'MENU', '/settings', '只读设置'),
                                                                                           ('SETTING_WRITE', '修改系统设置', 'BUTTON', '/api/settings', '修改全局配置'),
                                                                                           ('DEPARTMENT_MANAGE', '管理部门', 'MENU', '/departments', '组织架构管理'),
                                                                                           ('PROJECT_VIEW', '查看项目', 'MENU', '/projects', '项目列表'),
                                                                                           ('PROJECT_CREATE', '创建项目', 'BUTTON', '/api/projects', '创建新项目'),
                                                                                           ('PROJECT_EDIT', '编辑项目', 'BUTTON', '/api/projects/*', '编辑项目信息'),
                                                                                           ('PROJECT_DELETE', '删除项目', 'BUTTON', '/api/projects/delete', '删除项目'),
                                                                                           ('TASK_VIEW', '查看任务', 'MENU', '/tasks', '任务列表'),
                                                                                           ('TASK_CREATE', '创建任务', 'BUTTON', '/api/tasks', '创建新任务'),
                                                                                           ('TASK_ASSIGN', '分配任务', 'BUTTON', '/api/tasks/assign', '分配任务给用户'),
                                                                                           ('TASK_EDIT', '编辑任务', 'BUTTON', '/api/tasks/*', '编辑任务信息'),
                                                                                           ('TASK_DELETE', '删除任务', 'BUTTON', '/api/tasks/delete', '删除任务'),
                                                                                           ('FINANCE_VIEW', '查看财务', 'MENU', '/finance', '财务报表'),
                                                                                           ('FINANCE_CREATE', '创建财务记录', 'BUTTON', '/api/finance', '录入财务数据'),
                                                                                           ('FINANCE_EDIT', '编辑财务记录', 'BUTTON', '/api/finance/*', '编辑财务信息'),
                                                                                           ('FINANCE_DELETE', '删除财务记录', 'BUTTON', '/api/finance/delete', '删除财务记录'),
                                                                                           ('MARKETING_VIEW', '查看市场活动', 'MENU', '/marketing', '市场活动列表'),
                                                                                           ('MARKETING_CREATE', '创建市场活动', 'BUTTON', '/api/marketing', '创建新活动'),
                                                                                           ('MARKETING_EDIT', '编辑市场活动', 'BUTTON', '/api/marketing/*', '编辑活动信息'),
                                                                                           ('MARKETING_DELETE', '删除市场活动', 'BUTTON', '/api/marketing/delete', '删除活动'),
                                                                                           ('MARKETING_ANALYTICS', '市场分析', 'MENU', '/marketing/analytics', '市场数据统计'),
                                                                                           ('QA_TEST_MANAGE', '管理测试', 'MENU', '/qa/tests', '测试用例管理'),
                                                                                           ('QA_EXECUTE_TESTS', '执行测试', 'BUTTON', '/api/qa/tests/execute', '执行测试用例'),
                                                                                           ('QA_REPORT_GENERATE', '生成测试报告', 'BUTTON', '/api/qa/reports', '生成测试报告'),
                                                                                           ('QA_BUG_MANAGE', '管理缺陷', 'MENU', '/qa/bugs', '缺陷跟踪'),
                                                                                           ('QA_BUG_CREATE', '创建缺陷', 'BUTTON', '/api/qa/bugs', '提交缺陷'),
                                                                                           ('QA_BUG_EDIT', '编辑缺陷', 'BUTTON', '/api/qa/bugs/*', '编辑缺陷信息'),
                                                                                           ('QA_BUG_CLOSE', '关闭缺陷', 'BUTTON', '/api/qa/bugs/close', '关闭缺陷'),
                                                                                           ('DEV_DEPLOY', '部署应用', 'BUTTON', '/api/deploy', '应用部署权限'),
                                                                                           ('DEV_CODE_REVIEW', '代码审查', 'MENU', '/dev/code-review', '代码审查'),
                                                                                           ('DEV_BRANCH_MANAGE', '分支管理', 'BUTTON', '/api/dev/branches', '分支操作'),
                                                                                           ('DEV_RELEASE_MANAGE', '版本发布', 'MENU', '/dev/releases', '版本管理'),
                                                                                           ('DEV_CI_CD', 'CI/CD管理', 'MENU', '/dev/ci-cd', '持续集成部署'),
                                                                                           ('SECURITY_MONITOR', '安全监控', 'MENU', '/security/monitor', '安全事件监控'),
                                                                                           ('SECURITY_POLICY', '安全策略', 'MENU', '/security/policy', '安全策略管理'),
                                                                                           ('DATA_ANALYTICS', '数据分析', 'MENU', '/data/analytics', '数据统计分析'),
                                                                                           ('DATA_EXPORT', '数据导出', 'BUTTON', '/api/data/export', '导出数据权限');

-- 为用户分配角色（模拟真实的企业角色分配）
-- 生成大量用户-角色关联数据
INSERT INTO user_role (user_id, role_id, assigned_by) VALUES
                                                          (1, 1, 'admin'), (2, 7, 'admin'), (3, 10, 'admin'), (4, 13, 'admin'), (5, 3, 'admin'),
                                                          (6, 5, 'admin'), (7, 6, 'admin'), (8, 8, 'admin'), (9, 14, 'admin'), (10, 15, 'admin'),
                                                          (11, 10, 'admin'), (12, 11, 'admin'), (13, 3, 'admin'), (14, 7, 'admin'), (15, 3, 'admin'),
                                                          (16, 9, 'admin'), (17, 12, 'admin'), (18, 15, 'admin'), (19, 5, 'admin'), (20, 20, 'admin'),
                                                          (21, 3, 'admin'), (22, 3, 'admin'), (23, 8, 'admin'), (24, 13, 'admin'), (25, 10, 'admin'),
                                                          (26, 14, 'admin'), (27, 12, 'admin'), (28, 17, 'admin'), (29, 18, 'admin'), (30, 10, 'admin'),
                                                          (31, 8, 'admin'), (32, 15, 'admin'), (33, 5, 'admin'), (34, 6, 'admin'), (35, 3, 'admin'),
                                                          (36, 12, 'admin'), (37, 10, 'admin'), (38, 3, 'admin'), (39, 8, 'admin'), (40, 6, 'admin'),
                                                          (41, 16, 'admin'), (42, 10, 'admin'), (43, 13, 'admin'), (44, 12, 'admin'), (45, 3, 'admin'),
                                                          (46, 8, 'admin'), (47, 3, 'admin'), (48, 10, 'admin'), (49, 6, 'admin'), (50, 12, 'admin'),
                                                          (51, 3, 'admin'), (52, 5, 'admin'), (53, 10, 'admin'), (54, 8, 'admin'), (55, 3, 'admin'),
                                                          (56, 12, 'admin'), (57, 20, 'admin'), (58, 3, 'admin'), (59, 10, 'admin'), (60, 8, 'admin'),
                                                          (61, 14, 'admin'), (62, 12, 'admin'), (63, 6, 'admin'), (64, 3, 'admin'), (65, 10, 'admin'),
                                                          (66, 8, 'admin'), (67, 3, 'admin'), (68, 12, 'admin'), (69, 6, 'admin'), (70, 3, 'admin'),
                                                          (71, 10, 'admin'), (72, 8, 'admin'), (73, 3, 'admin'), (74, 12, 'admin'), (75, 6, 'admin'),
                                                          (76, 14, 'admin'), (77, 10, 'admin'), (78, 8, 'admin'), (79, 3, 'admin'), (80, 12, 'admin'),
                                                          (81, 6, 'admin'), (82, 16, 'admin'), (83, 10, 'admin'), (84, 8, 'admin'), (85, 3, 'admin'),
                                                          (86, 12, 'admin'), (87, 6, 'admin'), (88, 3, 'admin'), (89, 10, 'admin'), (90, 8, 'admin'),
                                                          (91, 3, 'admin'), (92, 12, 'admin'), (93, 20, 'admin'), (94, 5, 'admin'), (95, 10, 'admin'),
                                                          (96, 8, 'admin'), (97, 3, 'admin'), (98, 12, 'admin'), (99, 6, 'admin'), (100, 3, 'admin');

-- 为每个角色分配权限（模拟真实的企业权限分配）
-- Admin 拥有所有权限
INSERT INTO role_permission (role_id, permission_id, granted_by)
SELECT 1, id, 'system' FROM permission;

-- Developer 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'DEV_CODE_REVIEW'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'DEV_BRANCH_MANAGE'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'DEV_RELEASE_MANAGE'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'DEV_CI_CD'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'DEV_LOG_VIEW'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'ARTICLE_VIEW'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'SETTING_READ'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'DATA_ANALYTICS'), 'system'),
                                                                     (3, (SELECT id FROM permission WHERE perm_code = 'DATA_EXPORT'), 'system');

-- QA Tester 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'QA_TEST_MANAGE'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'QA_EXECUTE_TESTS'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'QA_REPORT_GENERATE'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'QA_BUG_MANAGE'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'QA_BUG_CREATE'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'QA_BUG_EDIT'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'QA_BUG_CLOSE'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'ARTICLE_VIEW'), 'system'),
                                                                     (5, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system');

-- Project Manager 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'PROJECT_VIEW'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'PROJECT_CREATE'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'PROJECT_EDIT'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'PROJECT_DELETE'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'TASK_VIEW'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'TASK_CREATE'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'TASK_ASSIGN'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'TASK_EDIT'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'TASK_DELETE'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (6, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system');

-- HR Specialist 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (8, (SELECT id FROM permission WHERE perm_code = 'HR_EMPLOYEE_MANAGE'), 'system'),
                                                                     (8, (SELECT id FROM permission WHERE perm_code = 'HR_SALARY_VIEW'), 'system'),
                                                                     (8, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (8, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system'),
                                                                     (8, (SELECT id FROM permission WHERE perm_code = 'DEPARTMENT_MANAGE'), 'system');

-- Accountant 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (10, (SELECT id FROM permission WHERE perm_code = 'FINANCE_VIEW'), 'system'),
                                                                     (10, (SELECT id FROM permission WHERE perm_code = 'FINANCE_CREATE'), 'system'),
                                                                     (10, (SELECT id FROM permission WHERE perm_code = 'FINANCE_EDIT'), 'system'),
                                                                     (10, (SELECT id FROM permission WHERE perm_code = 'FINANCE_DELETE'), 'system'),
                                                                     (10, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (10, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system');

-- Marketing Specialist 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (12, (SELECT id FROM permission WHERE perm_code = 'MARKETING_VIEW'), 'system'),
                                                                     (12, (SELECT id FROM permission WHERE perm_code = 'MARKETING_CREATE'), 'system'),
                                                                     (12, (SELECT id FROM permission WHERE perm_code = 'MARKETING_EDIT'), 'system'),
                                                                     (12, (SELECT id FROM permission WHERE perm_code = 'MARKETING_DELETE'), 'system'),
                                                                     (12, (SELECT id FROM permission WHERE perm_code = 'MARKETING_ANALYTICS'), 'system'),
                                                                     (12, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (12, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system');

-- DevOps Engineer 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (13, (SELECT id FROM permission WHERE perm_code = 'DEV_DEPLOY'), 'system'),
                                                                     (13, (SELECT id FROM permission WHERE perm_code = 'DEV_CI_CD'), 'system'),
                                                                     (13, (SELECT id FROM permission WHERE perm_code = 'DEV_LOG_VIEW'), 'system'),
                                                                     (13, (SELECT id FROM permission WHERE perm_code = 'SYS_ADMIN'), 'system'),
                                                                     (13, (SELECT id FROM permission WHERE perm_code = 'BACKUP_TRIGGER'), 'system'),
                                                                     (13, (SELECT id FROM permission WHERE perm_code = 'SETTING_READ'), 'system'),
                                                                     (13, (SELECT id FROM permission WHERE perm_code = 'SETTING_WRITE'), 'system');

-- Security Analyst 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (14, (SELECT id FROM permission WHERE perm_code = 'SECURITY_MONITOR'), 'system'),
                                                                     (14, (SELECT id FROM permission WHERE perm_code = 'SECURITY_POLICY'), 'system'),
                                                                     (14, (SELECT id FROM permission WHERE perm_code = 'AUDIT_TRAIL_VIEW'), 'system'),
                                                                     (14, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (14, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system');

-- System Admin 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (15, (SELECT id FROM permission WHERE perm_code = 'SETTING_READ'), 'system'),
                                                                     (15, (SELECT id FROM permission WHERE perm_code = 'SETTING_WRITE'), 'system'),
                                                                     (15, (SELECT id FROM permission WHERE perm_code = 'BACKUP_TRIGGER'), 'system'),
                                                                     (15, (SELECT id FROM permission WHERE perm_code = 'DEV_LOG_VIEW'), 'system'),
                                                                     (15, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (15, (SELECT id FROM permission WHERE perm_code = 'USER_EDIT'), 'system'),
                                                                     (15, (SELECT id FROM permission WHERE perm_code = 'USER_DELETE'), 'system');

-- Data Scientist 权限
INSERT INTO role_permission (role_id, permission_id, granted_by) VALUES
                                                                     (16, (SELECT id FROM permission WHERE perm_code = 'DATA_ANALYTICS'), 'system'),
                                                                     (16, (SELECT id FROM permission WHERE perm_code = 'DATA_EXPORT'), 'system'),
                                                                     (16, (SELECT id FROM permission WHERE perm_code = 'REPORT_VIEW'), 'system'),
                                                                     (16, (SELECT id FROM permission WHERE perm_code = 'USER_VIEW'), 'system'),
                                                                     (16, (SELECT id FROM permission WHERE perm_code = 'ARTICLE_VIEW'), 'system');

