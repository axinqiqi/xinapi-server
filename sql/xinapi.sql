/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3306
 Source Schema         : xinapi

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 12/09/2023 17:57:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for interface_charging
-- ----------------------------
DROP TABLE IF EXISTS `interface_charging`;
CREATE TABLE `interface_charging`  (
                                       `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `interfaceId` bigint(0) NOT NULL COMMENT '接口id',
                                       `charging` float(255, 2) NOT NULL COMMENT '计费规则（元/条）',
  `availablePieces` bigint(0) NOT NULL COMMENT '接口剩余可调用次数',
  `userId` bigint(0) NOT NULL COMMENT '创建人',
  `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `isDelete` int(0) NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '接口计费表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interface_charging
-- ----------------------------
INSERT INTO `interface_charging` VALUES (1, 2, 0.52, 997, 1, '2023-08-25 16:18:28', '2023-09-12 16:25:40', 0);

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info`  (
                                   `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
                                   `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                                   `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口地址',
                                   `requestParams` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
                                   `requestHeader` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求头',
                                   `responseHeader` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应头',
                                   `status` int(0) NOT NULL DEFAULT 0 COMMENT '接口状态（0-关闭，1-开启）',
                                   `method` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求类型',
                                   `userId` bigint(0) NOT NULL COMMENT '创建人',
                                   `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                   `updateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                   `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
                                   `sdk` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口对应的SDK类路径',
                                   `parameterExample` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数示例',
                                   `isFree` tinyint(0) NULL DEFAULT 0 COMMENT '是否免费(0-收费, 1-免费)',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '接口信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interface_info
-- ----------------------------
INSERT INTO `interface_info` VALUES (1, 'getNameByPost', 'post方法获取名字', 'localhost:8111/api/interface/name', '{\n\"username\": \"\"\n}', '{\n\"Content-Type\":  \"application/json\"\n}', '{\n\"Content-Type\":  \"application/json\"\n}', 1, 'POST', 1, '2023-08-03 21:52:37', '2023-09-08 11:49:20', 0, 'com.xinapi.xinapiclientsdk.client.NameApiClient', '{\"username\":\"axin\"}', 1);
INSERT INTO `interface_info` VALUES (2, 'getDayWallpaperUrl', '每日壁纸URL', 'localhost:8111/api/interface/day/wallpaper', NULL, '{\"Content-Type\":\"application/json\"}', '{\"Content-Type\":\"application/json\"}', 1, 'GET', 1, '2023-08-25 09:45:58', '2023-09-09 09:17:05', 0, 'com.xinapi.xinapiclientsdk.client.DayApiClient', NULL, 0);
INSERT INTO `interface_info` VALUES (3, 'getMovieRankUrl', '获取豆瓣电影排行', 'localhost:8111/api/interface/douban/movieRank', NULL, '{\"Content-Type\":\"application/json\"}', '{\"Content-Type\":\"application/json\"}', 1, 'GET', 1, '2023-08-25 10:20:14', '2023-09-08 15:13:06', 0, 'com.xinapi.xinapiclientsdk.client.DouBanApiClient', NULL, 1);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
                            `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                            `userId` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
                            `interfaceId` bigint(0) NULL DEFAULT NULL COMMENT '接口id',
                            `count` int(0) NULL DEFAULT NULL COMMENT '购买数量',
                            `totalAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单应付价格',
                            `status` int(0) NULL DEFAULT 0 COMMENT '订单状态 0-未支付 1 -已支付 2-超时支付',
                            `isDelete` int(0) NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
                            `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
                            `updateTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
                            `orderSn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单号',
                            `charging` float(255, 2) NOT NULL COMMENT '单价',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (29, 1, 1, 1, 0.52, 2, 0, '2023-08-29 15:39:50', '2023-08-29 15:40:50', '202308291539502271696626551', 0.52);
INSERT INTO `t_order` VALUES (30, 1, 1, 1, 0.52, 2, 0, '2023-08-30 17:41:40', '2023-08-30 17:42:40', '202308301741402541696173791', 0.52);
INSERT INTO `t_order` VALUES (31, 1, 1, 1, 0.52, 2, 0, '2023-09-08 10:29:04', '2023-09-08 10:30:04', '202309081029041461699148411', 0.52);
INSERT INTO `t_order` VALUES (32, 1, 1, 2, 1.04, 2, 0, '2023-09-08 10:45:47', '2023-09-08 10:46:47', '202309081045478321699133331', 0.52);
INSERT INTO `t_order` VALUES (33, 1, 2, 1, 0.52, 2, 0, '2023-09-08 14:51:52', '2023-09-08 14:52:52', '202309081451527611700714321', 0.52);
INSERT INTO `t_order` VALUES (34, 1701511946016804866, 2, 1, 0.52, 2, 0, '2023-09-12 16:24:40', '2023-09-12 16:25:40', '202309121624401741701086811701511946016804866', 0.52);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
                         `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
                         `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
                         `gender` tinyint(0) NULL DEFAULT NULL COMMENT '性别',
                         `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
                         `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                         `accessKey` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'accessKey',
                         `secretKey` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'secretKey',
                         `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                         `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                         `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
                         `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
                         `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'QQ邮箱',
                         PRIMARY KEY (`id`) USING BTREE,
                         UNIQUE INDEX `uni_userAccount`(`userAccount`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1701496374457671683 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '管理员', 'admin', NULL, NULL, 'admin', '261f6a196a4513a0f8e6b2bd84735d34', '052f0af7893b0c9da74a44d98c8f4f54', '60fbc5cdafdb6ffcc7ebb685285d9ecd', '2023-09-09 11:01:08', '2023-09-09 11:02:14', 0, NULL, NULL);
INSERT INTO `user` VALUES (1701511946016804866, NULL, 'axin', NULL, NULL, 'user', 'f3fb166ea6cd5dea00c693a95399edac', '78a4c6b845f590b98643ee4020f93db9', '9b08a1812a032f0a7fb9a617427771fe', '2023-09-12 16:23:55', '2023-09-12 17:37:19', 0, NULL, NULL);
INSERT INTO `user` VALUES (1701525875573002241, 'you19970724@qq.com', NULL, NULL, NULL, 'user', NULL, '485b30d6e871bb007e82ec8097205548', '567d9f89b3f8ba80b309e828eb0c802a', '2023-09-12 17:19:16', '2023-09-12 17:19:16', 0, NULL, 'you19970724@qq.com');

-- ----------------------------
-- Table structure for user_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `user_interface_info`;
CREATE TABLE `user_interface_info`  (
                                        `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `userId` bigint(0) NOT NULL COMMENT '调用用户 id',
                                        `interfaceInfoId` bigint(0) NOT NULL COMMENT '接口 id',
                                        `totalNum` int(0) NOT NULL DEFAULT 0 COMMENT '总调用次数',
                                        `leftNum` int(0) NOT NULL DEFAULT 0 COMMENT '剩余调用次数',
                                        `status` int(0) NOT NULL DEFAULT 0 COMMENT '0-正常，1-禁用',
                                        `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                        `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                        `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
                                        `version` bigint(0) NULL DEFAULT 1 COMMENT '版本号',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户调用接口关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_interface_info
-- ----------------------------
INSERT INTO `user_interface_info` VALUES (1, 1, 2, 44, 4, 0, '2023-08-06 21:49:29', '2023-09-08 14:51:33', 0, 24);
INSERT INTO `user_interface_info` VALUES (4, 1, 3, 41, -21, 0, '2023-09-08 15:05:19', '2023-09-08 15:36:49', 0, 42);
INSERT INTO `user_interface_info` VALUES (5, 1, 1, 58, 13, 0, '2023-09-08 15:05:19', '2023-09-09 09:17:58', 0, 59);
INSERT INTO `user_interface_info` VALUES (6, 1701511946016804866, 1, 2, 8, 0, '2023-09-12 16:24:18', '2023-09-12 16:24:18', 0, 3);

SET FOREIGN_KEY_CHECKS = 1;
