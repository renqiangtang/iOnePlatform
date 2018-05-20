/*
Navicat Oracle Data Transfer
Oracle Client Version : 11.2.0.1.0

Source Server         : 230
Source Server Version : 110200
Source Host           : :1521
Source Schema         : TRANSACTION

Target Server Type    : ORACLE
Target Server Version : 110200
File Encoding         : 65001

Date: 2012-05-02 10:54:46
*/


-- ----------------------------
-- Table structure for "TRANSACTION"."WF_NODE"
-- ----------------------------
DROP TABLE "TRANSACTION"."WF_NODE";
CREATE TABLE "TRANSACTION"."WF_NODE" (
"ID" VARCHAR2(50 BYTE) NOT NULL ,
"TEMPLATEID" VARCHAR2(50 BYTE) NULL ,
"NAME" VARCHAR2(50 BYTE) NULL ,
"TYPE" VARCHAR2(50 BYTE) NULL ,
"PARENTID" VARCHAR2(50 BYTE) NULL ,
"NEXTID" VARCHAR2(50 BYTE) NULL ,
"MEMO" VARCHAR2(4000 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "TRANSACTION"."WF_NODE" IS '流程节点表';
COMMENT ON COLUMN "TRANSACTION"."WF_NODE"."TEMPLATEID" IS '流程模板ID';
COMMENT ON COLUMN "TRANSACTION"."WF_NODE"."NAME" IS '节点名字';
COMMENT ON COLUMN "TRANSACTION"."WF_NODE"."TYPE" IS '节点类型';
COMMENT ON COLUMN "TRANSACTION"."WF_NODE"."PARENTID" IS '父ID';
COMMENT ON COLUMN "TRANSACTION"."WF_NODE"."NEXTID" IS '下一个ID';
COMMENT ON COLUMN "TRANSACTION"."WF_NODE"."MEMO" IS '备注';

-- ----------------------------
-- Records of WF_NODE
-- ----------------------------

-- ----------------------------
-- Table structure for "TRANSACTION"."WF_NODEUSER"
-- ----------------------------
DROP TABLE "TRANSACTION"."WF_NODEUSER";
CREATE TABLE "TRANSACTION"."WF_NODEUSER" (
"ID" VARCHAR2(50 BYTE) NOT NULL ,
"NODEID" VARCHAR2(50 BYTE) NULL ,
"TYPE" VARCHAR2(50 BYTE) NULL ,
"VALUE" VARCHAR2(4000 BYTE) NULL ,
"MEMO" VARCHAR2(4000 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "TRANSACTION"."WF_NODEUSER" IS '流程节点执行人表';
COMMENT ON COLUMN "TRANSACTION"."WF_NODEUSER"."NODEID" IS '流程节点ID';
COMMENT ON COLUMN "TRANSACTION"."WF_NODEUSER"."TYPE" IS '类型';
COMMENT ON COLUMN "TRANSACTION"."WF_NODEUSER"."VALUE" IS '值';
COMMENT ON COLUMN "TRANSACTION"."WF_NODEUSER"."MEMO" IS '备注';

-- ----------------------------
-- Records of WF_NODEUSER
-- ----------------------------

-- ----------------------------
-- Table structure for "TRANSACTION"."WF_PARAMETER"
-- ----------------------------
DROP TABLE "TRANSACTION"."WF_PARAMETER";
CREATE TABLE "TRANSACTION"."WF_PARAMETER" (
"ID" VARCHAR2(50 BYTE) NOT NULL ,
"REFID" VARCHAR2(50 BYTE) NULL ,
"NAME" VARCHAR2(4000 BYTE) NULL ,
"VALUE" VARCHAR2(4000 BYTE) NULL ,
"MEMO" VARCHAR2(4000 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "TRANSACTION"."WF_PARAMETER" IS '流程参数表';
COMMENT ON COLUMN "TRANSACTION"."WF_PARAMETER"."REFID" IS '引用ID';
COMMENT ON COLUMN "TRANSACTION"."WF_PARAMETER"."NAME" IS '参数名';
COMMENT ON COLUMN "TRANSACTION"."WF_PARAMETER"."VALUE" IS '参数值';
COMMENT ON COLUMN "TRANSACTION"."WF_PARAMETER"."MEMO" IS '备注';

-- ----------------------------
-- Records of WF_PARAMETER
-- ----------------------------

-- ----------------------------
-- Table structure for "TRANSACTION"."WF_PROCESS"
-- ----------------------------
DROP TABLE "TRANSACTION"."WF_PROCESS";
CREATE TABLE "TRANSACTION"."WF_PROCESS" (
"ID" VARCHAR2(50 BYTE) NOT NULL ,
"TEMPLATEID" VARCHAR2(50 BYTE) NULL ,
"TASKNODEID" VARCHAR2(50 BYTE) NULL ,
"STATE" VARCHAR2(50 BYTE) NULL ,
"MEMO" VARCHAR2(100 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "TRANSACTION"."WF_PROCESS" IS '流程实例表';
COMMENT ON COLUMN "TRANSACTION"."WF_PROCESS"."TEMPLATEID" IS '流程模板ID';
COMMENT ON COLUMN "TRANSACTION"."WF_PROCESS"."TASKNODEID" IS '当前任务节点ID';
COMMENT ON COLUMN "TRANSACTION"."WF_PROCESS"."STATE" IS '流程状态';
COMMENT ON COLUMN "TRANSACTION"."WF_PROCESS"."MEMO" IS '备注';

-- ----------------------------
-- Records of WF_PROCESS
-- ----------------------------

-- ----------------------------
-- Table structure for "TRANSACTION"."WF_TASKNODE"
-- ----------------------------
DROP TABLE "TRANSACTION"."WF_TASKNODE";
CREATE TABLE "TRANSACTION"."WF_TASKNODE" (
"ID" VARCHAR2(50 BYTE) NOT NULL ,
"PROCESSID" VARCHAR2(50 BYTE) NULL ,
"NODEID" VARCHAR2(50 BYTE) NULL ,
"TAG1" VARCHAR2(50 BYTE) NULL ,
"TAG2" NUMBER(18,2) NULL ,
"TIME" VARCHAR2(50 BYTE) NULL ,
"USERID" VARCHAR2(50 BYTE) NULL ,
"MEMO" VARCHAR2(4000 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "TRANSACTION"."WF_TASKNODE" IS '任务表';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODE"."PROCESSID" IS '流程实例ID';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODE"."NODEID" IS '当前节点ID';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODE"."TAG1" IS '标识1';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODE"."TAG2" IS '标识2';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODE"."TIME" IS '执行时间';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODE"."USERID" IS '执行人';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODE"."MEMO" IS '备注';

-- ----------------------------
-- Records of WF_TASKNODE
-- ----------------------------

-- ----------------------------
-- Table structure for "TRANSACTION"."WF_TASKNODEUSER"
-- ----------------------------
DROP TABLE "TRANSACTION"."WF_TASKNODEUSER";
CREATE TABLE "TRANSACTION"."WF_TASKNODEUSER" (
"ID" VARCHAR2(50 BYTE) NOT NULL ,
"TASKNODEID" VARCHAR2(50 BYTE) NULL ,
"TYPE" VARCHAR2(50 BYTE) NULL ,
"VALUE" VARCHAR2(4000 BYTE) NULL ,
"MEMO" VARCHAR2(4000 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "TRANSACTION"."WF_TASKNODEUSER" IS '任务执行人表';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODEUSER"."TASKNODEID" IS '任务节点ID';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODEUSER"."TYPE" IS '类型';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODEUSER"."VALUE" IS '值';
COMMENT ON COLUMN "TRANSACTION"."WF_TASKNODEUSER"."MEMO" IS '备注';

-- ----------------------------
-- Records of WF_TASKNODEUSER
-- ----------------------------

-- ----------------------------
-- Table structure for "TRANSACTION"."WF_TEMPLATE"
-- ----------------------------
DROP TABLE "TRANSACTION"."WF_TEMPLATE";
CREATE TABLE "TRANSACTION"."WF_TEMPLATE" (
"ID" VARCHAR2(50 BYTE) NOT NULL ,
"INSTANCECOUNT" VARCHAR2(50 BYTE) NULL ,
"MEMO" VARCHAR2(4000 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "TRANSACTION"."WF_TEMPLATE" IS '流程模板表';
COMMENT ON COLUMN "TRANSACTION"."WF_TEMPLATE"."INSTANCECOUNT" IS '实例数';
COMMENT ON COLUMN "TRANSACTION"."WF_TEMPLATE"."MEMO" IS '备注';

-- ----------------------------
-- Records of WF_TEMPLATE
-- ----------------------------

-- ----------------------------
-- Indexes structure for table WF_NODE
-- ----------------------------

-- ----------------------------
-- Checks structure for table "TRANSACTION"."WF_NODE"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_NODE" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "TRANSACTION"."WF_NODE"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_NODE" ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table WF_NODEUSER
-- ----------------------------

-- ----------------------------
-- Checks structure for table "TRANSACTION"."WF_NODEUSER"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_NODEUSER" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "TRANSACTION"."WF_NODEUSER"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_NODEUSER" ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table WF_PARAMETER
-- ----------------------------

-- ----------------------------
-- Checks structure for table "TRANSACTION"."WF_PARAMETER"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_PARAMETER" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "TRANSACTION"."WF_PARAMETER"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_PARAMETER" ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table WF_PROCESS
-- ----------------------------

-- ----------------------------
-- Checks structure for table "TRANSACTION"."WF_PROCESS"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_PROCESS" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "TRANSACTION"."WF_PROCESS"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_PROCESS" ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table WF_TASKNODE
-- ----------------------------

-- ----------------------------
-- Checks structure for table "TRANSACTION"."WF_TASKNODE"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_TASKNODE" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "TRANSACTION"."WF_TASKNODE"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_TASKNODE" ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table WF_TASKNODEUSER
-- ----------------------------

-- ----------------------------
-- Checks structure for table "TRANSACTION"."WF_TASKNODEUSER"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_TASKNODEUSER" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "TRANSACTION"."WF_TASKNODEUSER"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_TASKNODEUSER" ADD PRIMARY KEY ("ID");

-- ----------------------------
-- Indexes structure for table WF_TEMPLATE
-- ----------------------------

-- ----------------------------
-- Checks structure for table "TRANSACTION"."WF_TEMPLATE"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_TEMPLATE" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "TRANSACTION"."WF_TEMPLATE"
-- ----------------------------
ALTER TABLE "TRANSACTION"."WF_TEMPLATE" ADD PRIMARY KEY ("ID");
