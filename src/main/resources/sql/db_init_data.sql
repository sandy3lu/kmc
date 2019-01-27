-- 系统常量信息
INSERT INTO `dict_constant` VALUES (1, '证书类别', -1);
INSERT INTO `dict_constant` VALUES (2, '证书状态', -1);
INSERT INTO `dict_constant` VALUES (3, '印章类型', -1);
INSERT INTO `dict_constant` VALUES (4, '印章申请状态', -1);
INSERT INTO `dict_constant` VALUES (5, '日志操作类型', -1);
INSERT INTO `dict_constant` VALUES (6, '服务类型', -1);
INSERT INTO `dict_constant` VALUES (7, '服务状态', -1);
INSERT INTO `dict_constant` VALUES (8, '账号（角色）类型', -1);
INSERT INTO `dict_constant` VALUES (9, '印章使用状态', -1);

INSERT INTO `dict_constant` VALUES (101, '制章人证书', 1);
INSERT INTO `dict_constant` VALUES (102, '签章人证书', 1);
INSERT INTO `dict_constant` VALUES (103, '签章服务器证书', 1);
INSERT INTO `dict_constant` VALUES (104, 'CA根证书', 1);

INSERT INTO `dict_constant` VALUES (201, '正常', 2);
INSERT INTO `dict_constant` VALUES (202, '过期', 2);
INSERT INTO `dict_constant` VALUES (203, '作废', 2);

INSERT INTO `dict_constant` VALUES (301, '单位专用章', 3);
INSERT INTO `dict_constant` VALUES (302, '财务专用章', 3);
INSERT INTO `dict_constant` VALUES (303, '税务专用章', 3);
INSERT INTO `dict_constant` VALUES (304, '合同专用章', 3);
INSERT INTO `dict_constant` VALUES (305, '法人代表名章', 3);
INSERT INTO `dict_constant` VALUES (306, '个人名章', 3);
INSERT INTO `dict_constant` VALUES (307, '其他类型印章', 3);

INSERT INTO `dict_constant` VALUES (401, '待申请', 4);
INSERT INTO `dict_constant` VALUES (402, '待审核', 4);
INSERT INTO `dict_constant` VALUES (403, '审核通过', 4);
INSERT INTO `dict_constant` VALUES (404, '审核未通过', 4);

INSERT INTO `dict_constant` VALUES (501, '创建用户', 5);
INSERT INTO `dict_constant` VALUES (502, '删除用户', 5);
INSERT INTO `dict_constant` VALUES (503, '重置密码', 5);
INSERT INTO `dict_constant` VALUES (504, '证书申请', 5);
INSERT INTO `dict_constant` VALUES (505, '证书导入', 5);
INSERT INTO `dict_constant` VALUES (506, '证书更新', 5);
INSERT INTO `dict_constant` VALUES (507, '证书验证', 5);
INSERT INTO `dict_constant` VALUES (508, '印章申请', 5);
INSERT INTO `dict_constant` VALUES (509, '印章审核', 5);
INSERT INTO `dict_constant` VALUES (510, '印章制作', 5);
INSERT INTO `dict_constant` VALUES (511, '印章更新', 5);
INSERT INTO `dict_constant` VALUES (512, '印章验证', 5);
INSERT INTO `dict_constant` VALUES (513, '配置CA', 5);
INSERT INTO `dict_constant` VALUES (514, '配置CRL', 5);
INSERT INTO `dict_constant` VALUES (515, '配置OCSP', 5);
INSERT INTO `dict_constant` VALUES (516, '配置签章服务器', 5);

INSERT INTO `dict_constant` VALUES (601, 'CA', 6);
INSERT INTO `dict_constant` VALUES (602, 'CRL', 6);
INSERT INTO `dict_constant` VALUES (603, 'OCSP', 6);
INSERT INTO `dict_constant` VALUES (604, '签章服务器', 6);
INSERT INTO `dict_constant` VALUES (605, '电子印章服务', 6);

INSERT INTO `dict_constant` VALUES (701, '启用', 7);
INSERT INTO `dict_constant` VALUES (702, '停用', 7);

INSERT INTO `dict_constant` VALUES (801, '超级管理员', 8);
INSERT INTO `dict_constant` VALUES (802, '管理员', 8);
INSERT INTO `dict_constant` VALUES (803, '审计员', 8);

INSERT INTO `dict_constant` VALUES (901, '正常', 9);
INSERT INTO `dict_constant` VALUES (902, '注销', 9);