create table WF_NODE 
(
   ID                   VARCHAR2(50)         not null,
   TEMPLATEID           VARCHAR2(50),
   NAME                 VARCHAR2(50),
   TYPE                 VARCHAR2(50),
   PARENTID             VARCHAR2(50),
   NEXTID               VARCHAR2(50),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_NODE primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_NODE is
'流程节点表';

comment on column WF_NODE.TEMPLATEID is
'流程模板ID';

comment on column WF_NODE.NAME is
'节点名字';

comment on column WF_NODE.TYPE is
'节点类型';

comment on column WF_NODE.PARENTID is
'父ID';

comment on column WF_NODE.NEXTID is
'下一个ID';

comment on column WF_NODE.MEMO is
'备注';

/*==============================================================*/
/* Table: WF_NODEUSER                                           */
/*==============================================================*/
create table WF_NODEUSER 
(
   ID                   VARCHAR2(50)         not null,
   NODEID               VARCHAR2(50),
   TYPE                 VARCHAR2(50),
   VALUE                VARCHAR2(4000),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_NODEUSER primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_NODEUSER is
'流程节点执行人表';

comment on column WF_NODEUSER.NODEID is
'流程节点ID';

comment on column WF_NODEUSER.TYPE is
'类型';

comment on column WF_NODEUSER.VALUE is
'值';

comment on column WF_NODEUSER.MEMO is
'备注';

/*==============================================================*/
/* Table: WF_PARAMETER                                          */
/*==============================================================*/
create table WF_PARAMETER 
(
   ID                   VARCHAR2(50)         not null,
   REFID                VARCHAR2(50),
   NAME                 VARCHAR2(4000),
   VALUE                VARCHAR2(4000),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_PARAMETER primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_PARAMETER is
'流程参数表';

comment on column WF_PARAMETER.REFID is
'引用ID';

comment on column WF_PARAMETER.NAME is
'参数名';

comment on column WF_PARAMETER.VALUE is
'参数值';

comment on column WF_PARAMETER.MEMO is
'备注';

/*==============================================================*/
/* Table: WF_PROCESS                                            */
/*==============================================================*/
create table WF_PROCESS 
(
   ID                   VARCHAR2(50)         not null,
   TEMPLATEID           VARCHAR2(50),
   CURNODEID            VARCHAR2(50),
   STATE                VARCHAR2(50),
   MEMO                 VARCHAR2(100),
   constraint PK_WF_PROCESS primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_PROCESS is
'流程实例表';

comment on column WF_PROCESS.TEMPLATEID is
'流程模板ID';

comment on column WF_PROCESS.CURNODEID is
'当前节点ID';

comment on column WF_PROCESS.STATE is
'流程状态';

comment on column WF_PROCESS.MEMO is
'备注';

/*==============================================================*/
/* Table: WF_TASKNODE                                           */
/*==============================================================*/
create table WF_TASKNODE 
(
   ID                   VARCHAR2(50)         not null,
   TEMPLATEID           VARCHAR2(50),
   CURRENTNODE          VARCHAR2(50),
   TAG1                 VARCHAR2(50),
   TAG2                 NUMBER(18,2),
   TIME                 VARCHAR2(50),
   USERID               VARCHAR2(50),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TASKNODE primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TASKNODE is
'任务表';

comment on column WF_TASKNODE.TEMPLATEID is
'流程实例ID';

comment on column WF_TASKNODE.CURRENTNODE is
'当前节点ID';

comment on column WF_TASKNODE.TAG1 is
'标识1';

comment on column WF_TASKNODE.TAG2 is
'标识2';

comment on column WF_TASKNODE.TIME is
'执行时间';

comment on column WF_TASKNODE.USERID is
'执行人';

comment on column WF_TASKNODE.MEMO is
'备注';

/*==============================================================*/
/* Table: WF_TASKNODEUSER                                       */
/*==============================================================*/
create table WF_TASKNODEUSER 
(
   ID                   VARCHAR2(50)         not null,
   TASKNODEID           VARCHAR2(50),
   TYPE                 VARCHAR2(50),
   VALUE                VARCHAR2(4000),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TASKNODEUSER primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TASKNODEUSER is
'任务执行人表';

comment on column WF_TASKNODEUSER.TASKNODEID is
'任务节点ID';

comment on column WF_TASKNODEUSER.TYPE is
'类型';

comment on column WF_TASKNODEUSER.VALUE is
'值';

comment on column WF_TASKNODEUSER.MEMO is
'备注';

/*==============================================================*/
/* Table: WF_TEMPLATE                                           */
/*==============================================================*/
create table WF_TEMPLATE 
(
   ID                   VARCHAR2(50)         not null,
   INSTANCECOUNT        VARCHAR2(50),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TEMPLATE primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TEMPLATE is
'流程模板表';

comment on column WF_TEMPLATE.INSTANCECOUNT is
'实例数';

comment on column WF_TEMPLATE.MEMO is
'备注';

/*==============================================================*/
/* Table: WF_TIMEOUT                                            */
/*==============================================================*/
create table WF_TIMEOUT 
(
   ID                   VARCHAR2(50)         not null,
   PROCESSID            VARCHAR2(50),
   TYPE                 INTEGER,
   BEGINTIME            DATE,
   CLAZZ                VARCHAR2(200),
   STATE                INTEGER,
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TIMEOUT primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TIMEOUT is
'定时器表';

comment on column WF_TIMEOUT.PROCESSID is
'流程实例ID';

comment on column WF_TIMEOUT.TYPE is
'类型';

comment on column WF_TIMEOUT.BEGINTIME is
'开始时间';

comment on column WF_TIMEOUT.CLAZZ is
'处理类';

comment on column WF_TIMEOUT.STATE is
'状态';

comment on column WF_TIMEOUT.MEMO is
'备注';

